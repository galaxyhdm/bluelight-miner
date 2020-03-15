package dev.markusk.bluelight.miner;

import dev.markusk.bluelight.api.AbstractFetcher;
import dev.markusk.bluelight.api.impl.RssFetcher;
import dev.markusk.bluelight.miner.manager.ExtractorRegistry;
import dev.markusk.bluelight.miner.manager.FetcherExecutor;
import dev.markusk.bluelight.miner.manager.FetcherRegistry;
import dev.markusk.bluelight.miner.queue.DownloadScheduler;
import dev.markusk.bluelight.util.console.ConsoleController;
import io.prometheus.client.exporter.HTTPServer;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Miner implements AbstractFetcher {

  private static final Logger LOGGER = LogManager.getLogger();

  private final OptionSet optionSet;

  private ConsoleController consoleController;

  //Scheduler
  private DownloadScheduler downloadScheduler;

  //Manager
  private FetcherRegistry fetcherRegistry;
  private FetcherExecutor fetcherExecutor;
  private ExtractorRegistry extractorRegistry;

  private boolean running;

  public Miner(final OptionSet optionSet) {
    this.optionSet = optionSet;

    final HTTPServer httpServer;
    try {
      httpServer = new HTTPServer(8080);
      Runtime.getRuntime().addShutdownHook(new Thread(httpServer::stop));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initialize() {
    if (this.running) return;
    this.running = true;
    this.setupConsole();

    this.downloadScheduler = new DownloadScheduler();
    this.downloadScheduler.initialize();

    this.extractorRegistry = new ExtractorRegistry();

    this.fetcherRegistry = new FetcherRegistry();
    final RssFetcher rssFetcher = new RssFetcher();

    this.fetcherRegistry.addInfoFetcher(rssFetcher);

    this.fetcherExecutor = new FetcherExecutor(this, this.fetcherRegistry);
    this.fetcherExecutor.initializeJobs();

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
    return downloadScheduler;
  }
}
