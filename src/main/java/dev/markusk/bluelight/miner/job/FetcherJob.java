package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.handler.JobHandler;
import dev.markusk.bluelight.api.impl.BaseFetchInfo;
import dev.markusk.bluelight.api.interfaces.AbstractInfoFetcher;
import dev.markusk.bluelight.api.job.DownloadJob;
import dev.markusk.bluelight.api.job.JobPriority;
import dev.markusk.bluelight.miner.Constants;
import dev.markusk.bluelight.miner.Environment;
import dev.markusk.bluelight.miner.Miner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

public class FetcherJob extends TimerTask { // TODO: 16.04.2020 move to api

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
      final String lastUrl =
          this.miner.getDataStore().getLastUrl(this.infoFetcher.getTargetUid());
      final List<BaseFetchInfo> baseFetchInfos = this.getFilteredInfos(fetchInfos, lastUrl);

      if (!Environment.NO_FETCH)
        baseFetchInfos.forEach(baseFetchInfo -> {
          this.miner.getDownloadScheduler()
              .scheduleJob(new DownloadJob(UUID.randomUUID().toString(), JobPriority.NORMAL, baseFetchInfo, miner));
        });

      LOGGER.info(this.infoFetcher.getTargetUid() + " | New article count: " + baseFetchInfos.size());
      Constants.ARTICLE_COUNT.labels(this.infoFetcher.getTargetUid()).set(baseFetchInfos.size());
      if (baseFetchInfos.size() > 0) {
        final String lastFetchedUrl = baseFetchInfos.get(0).getUrl();
        LOGGER.info(this.infoFetcher.getTargetUid() + " | lastUrl=" + lastFetchedUrl);
        this.miner.getDataStore().setLastUrl(this.infoFetcher.getTargetUid(), lastFetchedUrl);
        this.miner.getDataStore().saveMap();
      }
    } catch (Exception e) {
      LOGGER.error(String.format("Error while fetching infos from: %s", this.infoFetcher.getTargetUid()), e);
    }
    if (this.jobHandler != null) this.jobHandler.onEnd();
  }

  private List<BaseFetchInfo> getFilteredInfos(final List<BaseFetchInfo> fetchInfos, final String lastUrl) {
    final BaseFetchInfo lastInfo = fetchInfos.stream().filter(
        baseFetchInfo -> baseFetchInfo.getUrl().equals(lastUrl))
        .findFirst().orElse(null);

    return lastInfo == null ? fetchInfos : fetchInfos.subList(0, fetchInfos.indexOf(lastInfo));
  }

  public void setJobHandler(final JobHandler jobHandler) {
    this.jobHandler = jobHandler;
  }

  public AbstractInfoFetcher getInfoFetcher() {
    return infoFetcher;
  }
}
