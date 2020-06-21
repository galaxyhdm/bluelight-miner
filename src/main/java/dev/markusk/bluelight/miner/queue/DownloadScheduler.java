package dev.markusk.bluelight.miner.queue;

import dev.markusk.bluelight.api.interfaces.AbstractScheduler;
import dev.markusk.bluelight.api.job.AbstractJob;
import dev.markusk.bluelight.api.job.DownloadJob;
import dev.markusk.bluelight.miner.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DownloadScheduler implements AbstractScheduler {

  private static final Logger LOGGER = LogManager.getLogger();
  private final ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
  private ExecutorService priorityJobPoolExecutor;
  private PriorityBlockingQueue<AbstractJob> priorityQueue;

  @Override
  public void initialize() {
    this.priorityJobPoolExecutor = Executors.newFixedThreadPool(Environment.POOL_SIZE);
    this.priorityQueue = new PriorityBlockingQueue<>(300, Comparator.comparing(AbstractJob::getPriority));

    this.priorityJobScheduler.execute(() -> {
      while (true) {
        try {
          priorityJobPoolExecutor.execute(priorityQueue.take());
        } catch (InterruptedException e) {
          LOGGER.error("Error on priorityJobScheduler", e);
        }
      }
    });
  }

  @Override
  public void scheduleJob(final AbstractJob job) {
    if (!(job instanceof DownloadJob)) throw new IllegalArgumentException("Job is not a DownloadJob");
    this.priorityQueue.add(job);
  }

  @Override
  public int getQueuedCount() {
    return this.priorityQueue.size();
  }

  protected void close(final ExecutorService scheduler) {
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
    }
  }

  @Override
  public void closeScheduler() {
    close(priorityJobPoolExecutor);
    close(priorityJobScheduler);
  }
}
