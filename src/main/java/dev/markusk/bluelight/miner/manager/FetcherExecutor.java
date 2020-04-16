package dev.markusk.bluelight.miner.manager;

import dev.markusk.bluelight.api.handler.JobHandler;
import dev.markusk.bluelight.api.impl.FetcherJob;
import dev.markusk.bluelight.api.interfaces.AbstractFetcherExecutor;
import dev.markusk.bluelight.api.interfaces.AbstractFetcherRegistry;
import dev.markusk.bluelight.api.interfaces.AbstractInfoFetcher;
import dev.markusk.bluelight.miner.Constants;
import dev.markusk.bluelight.miner.Environment;
import dev.markusk.bluelight.miner.Miner;
import io.prometheus.client.Histogram;

import java.util.HashMap;
import java.util.Timer;

public class FetcherExecutor implements AbstractFetcherExecutor {

  private final static Histogram REQUEST_LATENCY = Histogram.build()
      .name("requests_latency_seconds").help("Request latency in seconds.").labelNames("targetUid").register();

  private final Miner miner;
  private final AbstractFetcherRegistry fetcherRegistry;
  private final HashMap<String, FetcherJob> jobMap;
  private final Timer timer;

  public FetcherExecutor(final Miner miner, final AbstractFetcherRegistry fetcherRegistry) {
    this.miner = miner;
    this.fetcherRegistry = fetcherRegistry;
    this.timer = new Timer("Fetcher Executor");
    this.jobMap = new HashMap<>();
  }

  @Override
  public void initializeJobs() {
    this.jobMap.clear();
    this.fetcherRegistry.getFetcherMap().forEach((s, infoFetcher) -> addJob(infoFetcher));
  }

  @Override
  public void addJob(AbstractInfoFetcher infoFetcher) {
    final FetcherJob fetcherJob = new FetcherJob(this.miner, infoFetcher, !Environment.NO_FETCH);
    this.jobMap.put(infoFetcher.getTargetUid(), fetcherJob);
    this.registerHandler(fetcherJob);
    this.timer.schedule(fetcherJob, 100 * getRandomNumberInRange(1, 10), minutesToMillis(infoFetcher.getUpdateTime()));
  }

  private void registerHandler(FetcherJob job) {
    job.setJobHandler(new JobHandler() {
      private Histogram.Timer timer;

      @Override
      public void onStart() {
        this.timer = REQUEST_LATENCY.labels(job.getInfoFetcher().getTargetUid()).startTimer();
      }

      @Override
      public void updateGauge(final String targetUid, final double value) {
        Constants.ARTICLE_COUNT.labels(targetUid).set(value);
      }

      @Override
      public void onEnd() {
        if (timer == null) return;
        this.timer.observeDuration();
      }
    });
  }

  @Override
  public FetcherJob getFetcherJob(final String targetUid) {
    return this.jobMap.get(targetUid);
  }

  private int minutesToMillis(int minutes) {
    return 1000 * 60 * minutes;
  }

  public void stop() {
    this.timer.cancel();
  }

  private int getRandomNumberInRange(int min, int max) {

    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }
    return Constants.RANDOM.nextInt((max - min) + 1) + min;
  }

}
