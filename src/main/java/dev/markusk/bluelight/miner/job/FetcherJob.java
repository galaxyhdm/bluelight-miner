package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.AbstractInfoFetcher;
import dev.markusk.bluelight.api.handler.JobHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

public class FetcherJob extends TimerTask {

  private static final Logger LOGGER = LogManager.getLogger();

  private final AbstractInfoFetcher infoFetcher;

  private JobHandler jobHandler;

  public FetcherJob(final AbstractInfoFetcher infoFetcher) {
    this.infoFetcher = infoFetcher;
  }

  @Override
  public void run() {
    if (this.jobHandler != null) this.jobHandler.onStart();

    if (this.jobHandler != null) this.jobHandler.onEnd();
  }

  public void setJobHandler(final JobHandler jobHandler) {
    this.jobHandler = jobHandler;
  }

  public AbstractInfoFetcher getInfoFetcher() {
    return infoFetcher;
  }
}
