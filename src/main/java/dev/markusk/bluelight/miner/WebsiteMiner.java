package dev.markusk.bluelight.miner;

import dev.markusk.bluelight.api.AbstractFetcher;
import dev.markusk.bluelight.api.data.AbstractDataManager;
import dev.markusk.bluelight.api.factory.FetcherFactory;
import dev.markusk.bluelight.database.PostgresDataManager;
import dev.markusk.bluelight.miner.factory.BluelightFetcherFactory;
import dev.markusk.bluelight.util.TorValidator;
import dev.markusk.bluelight.util.console.ConsoleController;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static dev.markusk.bluelight.miner.Environment.*;

public class WebsiteMiner implements AbstractFetcher {

  private static final Logger LOGGER = LogManager.getLogger();

  private FetcherFactory fetcherFactory;
  private AbstractDataManager dataManager;
  private ConsoleController consoleController;

  private OptionSet optionSet;
  private File workDir;
  private File indexDir;

  private boolean running;

  public WebsiteMiner(final OptionSet optionSet) {
    this.optionSet = optionSet;
    this.workDir = (File) optionSet.valueOf("dir");
    this.indexDir = (File) optionSet.valueOf("index-dir");
    if (!workDir.exists())
      LOGGER.info(workDir.mkdirs());
    if (!indexDir.exists())
      LOGGER.info(indexDir.mkdirs());
  }

  @Override
  public void initialize() {
    if (this.running) return;
    this.running = true;
    this.fetcherFactory = new BluelightFetcherFactory();
    this.setupConsole();

    LOGGER.info("WORK_DIR=" + workDir.getAbsolutePath() + " WAIT_MINUTES=" + WAIT_MINUTES + " NO_FETCH=" + NO_FETCH +
        " DEBUG=" + DEBUG + " TOR=" + TOR + " POOL_SIZE=" + POOL_SIZE);
    LOGGER.info(String
        .format("Starting bluelight-miner... (Version Nr. %s built on %s at %s)", VersionInfo.VERSION,
            VersionInfo.BUILD_DATE,
            VersionInfo.BUILD_TIME));

    this.dataManager = new PostgresDataManager();
    final boolean initialize = this.dataManager.initialize(this);
    if (!initialize) {
      LOGGER.error("Error while creating pooledConnection to database");
      System.exit(100);
    }

    if (TOR) {
      LOGGER.info("Checking tor...");
      new TorValidator(true).checkTor();
    }


  }

  @Override
  public FetcherFactory getFetcherFactory() {
    return this.fetcherFactory;
  }

  private void setupConsole() {
    this.consoleController =
        new ConsoleController(VersionInfo.DEBUG || this.optionSet.has("debug") || DEBUG, false);
    this.consoleController.setupConsole();
  }

}
