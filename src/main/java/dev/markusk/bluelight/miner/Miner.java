package dev.markusk.bluelight.miner;

import dev.markusk.bluelight.api.AbstractFetcher;
import dev.markusk.bluelight.api.config.Configuration;
import dev.markusk.bluelight.api.config.TargetConfiguration;
import dev.markusk.bluelight.api.console.BetterSystemOut;
import dev.markusk.bluelight.api.console.ConsoleController;
import dev.markusk.bluelight.api.extractor.DefaultExtractor;
import dev.markusk.bluelight.api.interfaces.*;
import dev.markusk.bluelight.api.modules.Module;
import dev.markusk.bluelight.api.modules.ModuleLoader;
import dev.markusk.bluelight.api.modules.ModuleManager;
import dev.markusk.bluelight.api.util.TorValidator;
import dev.markusk.bluelight.api.util.Utils;
import dev.markusk.bluelight.miner.config.DataStore;
import dev.markusk.bluelight.miner.manager.DataRegistry;
import dev.markusk.bluelight.miner.manager.ExtractorRegistry;
import dev.markusk.bluelight.miner.manager.FetcherExecutor;
import dev.markusk.bluelight.miner.manager.FetcherRegistry;
import dev.markusk.bluelight.miner.queue.DownloadScheduler;
import dev.markusk.bluelight.miner.queue.ImportScheduler;
import io.prometheus.client.exporter.HTTPServer;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
  private AbstractScheduler downloadScheduler;
  private AbstractScheduler importScheduler;

  //Manager
  private AbstractFetcherRegistry fetcherRegistry;
  private AbstractFetcherExecutor fetcherExecutor;
  private AbstractExtractorRegistry extractorRegistry;
  private AbstractDataRegistry dataRegistry;

  //Data
  private AbstractUrlData urlData;

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
    LOGGER.debug("NO_FETCH=" + NO_FETCH + " DEBUG=" + DEBUG + " POOL_SIZE=" + POOL_SIZE);
    LOGGER.info(String
        .format("Starting miner... (Version Nr. %s built on %s at %s)", VersionInfo.VERSION, VersionInfo.BUILD_DATE,
            VersionInfo.BUILD_TIME));

    final String version = dev.markusk.bluelight.api.VersionInfo.VERSION;
    final String buildDate = dev.markusk.bluelight.api.VersionInfo.BUILD_DATE;
    final String buildTime = dev.markusk.bluelight.api.VersionInfo.BUILD_TIME;
    LOGGER.info(String.format("Api-Version Nr. %s built on %s at %s)", version, buildDate, buildTime));

    //Create work dir
    this.workDir = (File) optionSet.valueOf("dir");
    if (!workDir.exists())
      LOGGER.info(workDir.mkdirs());

    this.configuration = this.loadConfig();
    if (this.configuration == null) {
      LOGGER.error("Configuration is null! Creating basic configuration...");
      this.saveDefaultConfig();
      System.exit(20);
    }

    this.dataRegistry = new DataRegistry(this);

    this.moduleLoader = new ModuleLoader(this);
    this.moduleManager = new ModuleManager(this, this.moduleLoader);
    this.loadModules();

    this.urlData = new DataStore(new File(this.workDir, "lastUrls.json"));
    this.urlData.load();

    this.checkTor();

    this.downloadScheduler = new DownloadScheduler();
    this.downloadScheduler.initialize();

    this.importScheduler = new ImportScheduler();
    this.importScheduler.initialize();

    this.extractorRegistry = new ExtractorRegistry(new DefaultExtractor());
    this.fetcherRegistry = new FetcherRegistry();

    this.configuration.getTargets().forEach(this::loadFetcher);

    this.fetcherExecutor = new FetcherExecutor(this, this.fetcherRegistry);

    if (SYNC_START)
      this.syncStart();

    this.fetcherExecutor.initializeJobs();
  }

  private void loadFetcher(final String targetUid, final TargetConfiguration targetConfiguration) {
    if (targetConfiguration.getDatabase() != null)
      this.getDataRegistry().getDataManager(targetUid, targetConfiguration.getDatabase());

    this.createFile(targetUid, targetConfiguration);
    this.registerExtractor(targetUid, targetConfiguration);
    this.createFetcher(targetUid, targetConfiguration);
    LOGGER.info("Loaded Fetcher: " + targetUid);
  }

  private void registerExtractor(final String targetUid, final TargetConfiguration targetConfiguration) {
    final Extractor extractor = Utils.getClass(targetConfiguration.getExtractorPath(), Extractor.class);
    if (extractor == null) {
      throw new NullPointerException(String.format("Extractor for %s is null!", targetUid));
    }
    this.extractorRegistry.addExtractor(targetUid, extractor);
  }

  private void createFetcher(final String targetUid, final TargetConfiguration targetConfiguration) {
    final AbstractInfoFetcher infoFetcher =
        Utils.getClass(targetConfiguration.getFetcherPath(), AbstractInfoFetcher.class);
    if (infoFetcher == null) {
      throw new NullPointerException(String.format("InfoFetcher for %s is null!", targetUid));
    }
    infoFetcher.initialize(targetUid, targetConfiguration.getFetchUrl(), targetConfiguration.getUpdateTime());
    this.fetcherRegistry.addInfoFetcher(infoFetcher);
  }

  private void createFile(final String targetUid, final TargetConfiguration targetConfiguration) {
    final File file = new File(this.workDir, targetConfiguration.getWorkDir());
    if (!file.exists() && !file.mkdir()) {
      throw new RuntimeException(
          String.format("Error creating %s directory for fetcher %s", file.getPath(), targetUid));
    }
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
      throw new RuntimeException(e);
    }
    return null;
  }

  private void saveDefaultConfig() {
    try (final InputStream resourceAsStream = Miner.class.getClassLoader().getResourceAsStream("config.yml")) {
      assert resourceAsStream != null;
      Files.copy(resourceAsStream, Path.of(new File("config.yml").toURI()));
    } catch (IOException e) {
      LOGGER.error("Error while saveDefaultConfig", e);
    }
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
      LOGGER.info(String.format("Loading %s with version: %s", module.getDescription().getName(),
          module.getDescription().getVersion()));
      this.moduleManager.enableModule(module);
    }
  }

  private void setupConsole() {
    this.consoleController =
        new ConsoleController(VersionInfo.DEBUG || this.optionSet.has("debug") || Environment.DEBUG, false);
    this.consoleController.setupConsole();
    new BetterSystemOut(LOGGER).overwrite();
  }

  @Override
  public AbstractFetcherRegistry getFetcherRegistry() {
    return this.fetcherRegistry;
  }

  @Override
  public AbstractFetcherExecutor getFetcherExecutor() {
    return this.fetcherExecutor;
  }

  @Override
  public AbstractExtractorRegistry getExtractorRegistry() {
    return this.extractorRegistry;
  }

  @Override
  public AbstractScheduler getDownloadScheduler() {
    return this.downloadScheduler;
  }

  @Override
  public AbstractScheduler getImportScheduler() {
    return this.importScheduler;
  }

  @Override
  public Configuration getConfiguration() {
    return this.configuration;
  }

  @Override
  public AbstractUrlData getUrlData() {
    return this.urlData;
  }

  @Override
  public File getWorkDir() {
    return this.workDir;
  }

  @Override
  public TargetConfiguration getTargetConfiguration(final String targetUid) {
    return this.configuration.getTargets().get(targetUid);
  }

  @Override
  public AbstractDataRegistry getDataRegistry() {
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

  private void syncStart() {
    try {
      final long waitMillis = this.getWaitMillis();
      LOGGER.debug(String.format("Waiting %ss to start jobs", TimeUnit.MILLISECONDS.toSeconds(waitMillis)));
      Thread.sleep(waitMillis - 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private long getWaitMillis() {
    final Date date = new Date();
    final Calendar instance = Calendar.getInstance();

    instance.set(Calendar.MINUTE, instance.get(Calendar.MINUTE) + 1);
    instance.set(Calendar.SECOND, 0);
    instance.set(Calendar.MILLISECOND, 0);

    return instance.getTimeInMillis() - date.getTime();
  }

}
