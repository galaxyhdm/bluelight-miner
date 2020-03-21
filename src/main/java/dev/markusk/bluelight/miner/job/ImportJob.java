package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.interfaces.Extractor;
import dev.markusk.bluelight.api.job.AbstractJob;
import dev.markusk.bluelight.api.job.JobPriority;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.database.PostgresDataManager;
import dev.markusk.bluelight.miner.Miner;
import dev.markusk.bluelight.miner.config.TargetConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
  private final String targetUid;

  public ImportJob(final Article article, final Miner miner, final File targetWorkDir,
      final String targetUid) {
    this(UUID.randomUUID().toString(), JobPriority.NORMAL, article, miner, targetWorkDir, targetUid);
  }

  public ImportJob(final String jobId, final JobPriority priority, final Article article, final Miner miner,
      final File targetWorkDir, final String targetUid) {
    this.jobId = jobId;
    this.priority = priority;
    this.article = article;
    this.miner = miner;
    this.workDir = targetWorkDir;
    this.targetUid = targetUid;
  }


  @Override
  public void run() {
    LOGGER.info("Index article: " + this.article.getId());
    final TargetConfiguration configuration = this.miner.getConfiguration(this.targetUid);
    final List<ImportState> importStates = this.getImportStates(configuration.getIndexType());

    final PostgresDataManager dataManager = this.miner.getDataManager();
    dataManager.addArticle(this.article);

    if (importStates.isEmpty()) return;
    final Extractor extractor = this.miner.getExtractorRegistry().getExtractor(this.targetUid);
    final File articleFile = new File(this.workDir, this.article.getFileIdentification() + ".html");

    try {
      final Document parse = Jsoup.parse(articleFile, StandardCharsets.UTF_8.name());

      if (importStates.contains(ImportState.CONTENT)) {
        LOGGER.info("Index content");
        final String content = extractor.getContent(parse);
        this.article.setContent(content);
        dataManager.updateArticleContent(this.article);
      }

    } catch (IOException e) {
      LOGGER.error("Error while indexing file", e);
    }

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
