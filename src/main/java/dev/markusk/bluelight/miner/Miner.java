package dev.markusk.bluelight.miner;

import dev.markusk.bluelight.api.AbstractFetcher;
import dev.markusk.bluelight.api.data.DataRegistry;
import dev.markusk.bluelight.api.impl.RssFetcher;
import dev.markusk.bluelight.api.interfaces.Extractor;
import dev.markusk.bluelight.api.modules.Module;
import dev.markusk.bluelight.api.modules.ModuleLoader;
import dev.markusk.bluelight.api.modules.ModuleManager;
import dev.markusk.bluelight.api.util.Utils;
import dev.markusk.bluelight.miner.config.Configuration;
import dev.markusk.bluelight.miner.config.DataStore;
import dev.markusk.bluelight.miner.config.TargetConfiguration;
import dev.markusk.bluelight.miner.extractor.DefaultExtractor;
import dev.markusk.bluelight.miner.manager.ExtractorRegistry;
import dev.markusk.bluelight.miner.manager.FetcherExecutor;
import dev.markusk.bluelight.miner.manager.FetcherRegistry;
import dev.markusk.bluelight.miner.queue.DownloadScheduler;
import dev.markusk.bluelight.miner.queue.ImportScheduler;
import dev.markusk.bluelight.util.TorValidator;
import dev.markusk.bluelight.util.console.ConsoleController;
import io.prometheus.client.exporter.HTTPServer;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static dev.markusk.bluelight.miner.Environment.*;

public class Miner implements AbstractFetcher {

  private static final Logger LOGGER = LogManager.getLogger();

  private final OptionSet optionSet;
  private File workDir;

  private Configuration configuration;
  private ConsoleController consoleController;

  //Module
  private ModuleLoader moduleLoader;
  private ModuleManager moduleManager;

  //Scheduler
  private DownloadScheduler downloadScheduler;
  private ImportScheduler importScheduler;

  //Manager
  private FetcherRegistry fetcherRegistry;
  private FetcherExecutor fetcherExecutor;
  private ExtractorRegistry extractorRegistry;
  private DataRegistry dataRegistry;

  //Data
  private DataStore dataStore;

  private boolean running;

  public Miner(final OptionSet optionSet) {
    this.optionSet = optionSet;

    final HTTPServer httpServer;
    try {
      httpServer = new HTTPServer(8080);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        this.fetcherExecutor.stop();
        this.downloadScheduler.closeScheduler();
        httpServer.stop();
      }));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initialize() {
    if (this.running) return;
    this.running = true;
    this.setupConsole();
    LOGGER.info("NO_FETCH=" + NO_FETCH + " DEBUG=" + DEBUG + " POOL_SIZE=" + POOL_SIZE);
    LOGGER.info(String
        .format("Starting miner... (Version Nr. %s built on %s at %s)", VersionInfo.VERSION, VersionInfo.BUILD_DATE,
            VersionInfo.BUILD_TIME));

    //Create work dir
    this.workDir = (File) optionSet.valueOf("dir");
    if (!workDir.exists())
      LOGGER.info(workDir.mkdirs());

    this.configuration = this.loadConfig();

    this.dataRegistry = new DataRegistry(this);

    // TODO: 01.04.2020 implement module loader
    this.moduleLoader = new ModuleLoader(this);
    this.moduleManager = new ModuleManager(this, this.moduleLoader);
    this.loadModules();

    this.dataStore = new DataStore(new File(this.workDir, "lastUrls.json"));
    this.dataStore.loadMap();

    this.checkTor();

    this.downloadScheduler = new DownloadScheduler();
    this.downloadScheduler.initialize();

    this.importScheduler = new ImportScheduler();
    this.importScheduler.initialize();

    this.extractorRegistry = new ExtractorRegistry(new DefaultExtractor());
    this.fetcherRegistry = new FetcherRegistry();

    this.loadFetcher();

    this.fetcherExecutor = new FetcherExecutor(this, this.fetcherRegistry);
    this.fetcherExecutor.initializeJobs();
  }

  private void loadFetcher() {
    this.configuration.getTargets().forEach((s, targetConfiguration) -> {
      if (targetConfiguration.getDatabase() != null)
        this.getDataRegistry().getDataManager(s, targetConfiguration.getDatabase());
      final Extractor extractor = Utils.getExtractor(targetConfiguration.getExtractorPath());
      this.extractorRegistry.addExtractor(s, extractor);
      final RssFetcher rssFetcher = new RssFetcher();
      rssFetcher.initialize(s, targetConfiguration.getFetchUrl(), targetConfiguration.getUpdateTime());
      this.fetcherRegistry.addInfoFetcher(rssFetcher);
      LOGGER.info("Loaded Fetcher: " + s);
      //Create workDirs
      final File file = new File(this.workDir, targetConfiguration.getWorkDir());
      if (!file.exists()) {
        LOGGER.info(String.format("Created work dir for %s: %s", s, file.mkdirs()));
      }
    });
  }

  private void checkTor() {
    final long count = this.configuration.getTargets().values().stream().filter(TargetConfiguration::isTor).count();
    if (count > 0) {
      LOGGER.info("Checking tor...");
      new TorValidator(true).checkTor();
    }
  }

  private Configuration loadConfig() {
    Constructor constructor = new Constructor(Configuration.class);
    final Yaml yaml = new Yaml(constructor);
    try (FileInputStream inputStream = new FileInputStream(new File("config.yml"))) {
      final Configuration load = yaml.loadAs(inputStream, Configuration.class);
      if (load != null) return load;
    } catch (Exception e) {
      LOGGER.error("Error", e);
      return null;
    }
    return null;
  }

  private void loadModules() {
    String moduleFolderPath =
        this.configuration.getModuleFolder() == null || this.configuration.getModuleFolder().isEmpty() ? "modules" :
            this.configuration.getModuleFolder();
    final File moduleFolder = new File(".", moduleFolderPath);
    if (!moduleFolder.exists() && !moduleFolder.mkdir()) {
      throw new RuntimeException("Error creating " + moduleFolder.getPath() + " directory");
    }
    final Module[] modules = this.moduleManager.loadModules(moduleFolder);
    for (final Module module : modules) {
      LOGGER.info(String.format("Loading %s", module.getDescription().getName()));
      this.moduleManager.enableModule(module);
    }
  }

  private void setupConsole() {
    this.consoleController =
        new ConsoleController(VersionInfo.DEBUG || this.optionSet.has("debug") || Environment.DEBUG, false);
    this.consoleController.setupConsole();
  }

  public FetcherRegistry getFetcherRegistry() {
    return this.fetcherRegistry;
  }

  public FetcherExecutor getFetcherExecutor() {
    return this.fetcherExecutor;
  }

  public ExtractorRegistry getExtractorRegistry() {
    return this.extractorRegistry;
  }

  public DownloadScheduler getDownloadScheduler() {
    return this.downloadScheduler;
  }

  public ImportScheduler getImportScheduler() {
    return this.importScheduler;
  }

  public TargetConfiguration getConfiguration(final String targetUid) {
    return this.configuration.getTargets().get(targetUid);
  }

  public Configuration getConfiguration() {
    return this.configuration;
  }

  public DataStore getDataStore() {
    return this.dataStore;
  }

  public File getWorkDir() {
    return this.workDir;
  }

  @Override
  public DataRegistry getDataRegistry() {
    return this.dataRegistry;
  }

  @Override
  public ModuleManager getModuleManager() {
    return this.moduleManager;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }
}
