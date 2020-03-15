package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.AbstractInfoFetcher;
import dev.markusk.bluelight.api.handler.JobHandler;
import dev.markusk.bluelight.api.impl.BaseFetchInfo;
import dev.markusk.bluelight.api.job.JobPriority;
import dev.markusk.bluelight.miner.Miner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

public class FetcherJob extends TimerTask {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Miner miner;
  private final AbstractInfoFetcher infoFetcher;
  private JobHandler jobHandler;

  public FetcherJob(final Miner miner, final AbstractInfoFetcher infoFetcher) {
    this.miner = miner;
    this.infoFetcher = infoFetcher;
  }

  @Override
  public void run() {
    if (this.jobHandler != null) this.jobHandler.onStart();
    try {
      final List<BaseFetchInfo> fetchInfos = this.infoFetcher.getFetchInfos();
      // TODO: 15.03.2020 sort for not fetched urls or ids
      fetchInfos.forEach(baseFetchInfo -> {
        this.miner.getDownloadScheduler()
            .scheduleJob(new DownloadJob(UUID.randomUUID().toString(), JobPriority.NORMAL, baseFetchInfo, miner));
      });
    } catch (Exception e) {
      LOGGER.error(String.format("Error while fetching infos from: %s", this.infoFetcher.getTargetUid()), e);
    }
    if (this.jobHandler != null) this.jobHandler.onEnd();
  }

  public void setJobHandler(final JobHandler jobHandler) {
    this.jobHandler = jobHandler;
  }

  public AbstractInfoFetcher getInfoFetcher() {
    return infoFetcher;
  }
}
