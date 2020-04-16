package dev.markusk.bluelight.miner.queue;

import dev.markusk.bluelight.api.interfaces.AbstractScheduler;
import dev.markusk.bluelight.api.job.AbstractJob;
import dev.markusk.bluelight.api.job.ImportJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ImportScheduler implements AbstractScheduler {

  private static final Logger LOGGER = LogManager.getLogger();

  private ExecutorService priorityJobExecutor = Executors.newSingleThreadExecutor();
  private PriorityBlockingQueue<AbstractJob> priorityQueue;

  @Override
  public void initialize() {
    this.priorityQueue = new PriorityBlockingQueue<>(300, Comparator.comparing(AbstractJob::getPriority));

    this.priorityJobExecutor.execute(() -> {
      while (true) {
        try {
          final AbstractJob job = priorityQueue.take();
          job.run();
        } catch (InterruptedException e) {
          LOGGER.error("Error on priorityJobExecutor", e);
        }
      }
    });
  }

  @Override
  public void scheduleJob(final AbstractJob job) {
    if (!(job instanceof ImportJob)) throw new IllegalArgumentException("Job is not a ImportJob");
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
    close(priorityJobExecutor);
  }
}
