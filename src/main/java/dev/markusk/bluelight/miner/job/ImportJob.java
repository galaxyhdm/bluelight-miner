package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.job.AbstractJob;
import dev.markusk.bluelight.api.job.JobPriority;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.miner.Miner;
import dev.markusk.bluelight.miner.config.TargetConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImportJob implements AbstractJob {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Miner miner;
  private final String jobId;
  private final JobPriority priority;
  private final Article article;
  private final File workDir;
  private final TargetConfiguration configuration;

  public ImportJob(final Article article, final Miner miner, final File targetWorkDir,
      final TargetConfiguration configuration) {
    this(UUID.randomUUID().toString(), JobPriority.NORMAL, article, miner, targetWorkDir, configuration);
  }

  public ImportJob(final String jobId, final JobPriority priority, final Article article, final Miner miner,
      final File targetWorkDir, final TargetConfiguration configuration) {
    this.jobId = jobId;
    this.priority = priority;
    this.article = article;
    this.miner = miner;
    this.workDir = targetWorkDir;
    this.configuration = configuration;
  }


  @Override
  public void run() {
    final List<ImportState> importStates = this.getImportStates(this.configuration.getIndexType());

  }

  private List<ImportState> getImportStates(final byte value) {
    final List<ImportState> importStates = new ArrayList<>();
    byte tempValue = value;
    for (final ImportState importState : ImportState.reverse()) {
      final byte stateValue = importState.getValue();
      byte newValue = (byte) (tempValue - stateValue);
      if (newValue >= 0) {
        tempValue = newValue;
        importStates.add(importState);
      }
    }
    return importStates;
  }

  @Override
  public String getJobId() {
    return this.jobId;
  }

  @Override
  public JobPriority getPriority() {
    return this.priority;
  }

  private enum ImportState {
    LOCATIONS((byte) 1),
    TOPICS((byte) 2),
    CONTENT((byte) 4);

    private final byte value;

    ImportState(final byte value) {
      this.value = value;
    }

    static ImportState[] reverse() {
      ImportState[] importStates = new ImportState[ImportState.values().length];
      int j = ImportState.values().length;
      for (int i = 0; i < ImportState.values().length; i++) {
        importStates[j - 1] = ImportState.values()[i];
        j = j - 1;
      }
      return importStates;
    }

    public byte getValue() {
      return value;
    }

  }

}
