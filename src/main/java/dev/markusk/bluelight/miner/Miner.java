package dev.markusk.bluelight.miner;

import dev.markusk.bluelight.api.AbstractFetcher;
import dev.markusk.bluelight.api.impl.RssFetcher;
import dev.markusk.bluelight.api.interfaces.Extractor;
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

  //Scheduler
  private DownloadScheduler downloadScheduler;
  private ImportScheduler importScheduler;

  //Manager
  private FetcherRegistry fetcherRegistry;
  private FetcherExecutor fetcherExecutor;
  private ExtractorRegistry extractorRegistry;

  //Config
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

    this.dataStore = new DataStore(new File(this.workDir, "lastUrls.json"));
    this.dataStore.loadMap();

    this.configuration = this.loadConfig();

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
    return importScheduler;
  }

  public TargetConfiguration getConfiguration(final String targetUid) {
    return this.configuration.getTargets().get(targetUid);
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public DataStore getDataStore() {
    return this.dataStore;
  }

  public File getWorkDir() {
    return workDir;
  }
}
