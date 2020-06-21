package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.AbstractFetcher;
import dev.markusk.bluelight.api.config.TargetConfiguration;
import dev.markusk.bluelight.api.data.AbstractDataManager;
import dev.markusk.bluelight.api.enums.ImportState;
import dev.markusk.bluelight.api.interfaces.Extractor;
import dev.markusk.bluelight.api.job.ImportJob;
import dev.markusk.bluelight.api.job.JobPriority;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.api.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class ImportJobImpl implements ImportJob {

  private static final Logger LOGGER = LogManager.getLogger();

  private final AbstractFetcher fetcher;
  private final String jobId;
  private final JobPriority priority;
  private final Article article;
  private final File workDir;
  private final String targetUid;

  public ImportJobImpl(final Article article, final AbstractFetcher fetcher, final File targetWorkDir,
      final String targetUid) {
    this(UUID.randomUUID().toString(), JobPriority.NORMAL, article, fetcher, targetWorkDir, targetUid);
  }

  public ImportJobImpl(final String jobId, final JobPriority priority, final Article article,
      final AbstractFetcher fetcher,
      final File targetWorkDir, final String targetUid) {
    this.jobId = jobId;
    this.priority = priority;
    this.article = article;
    this.fetcher = fetcher;
    this.workDir = targetWorkDir;
    this.targetUid = targetUid;
  }

  @Override
  public void run() {
    final Article article = this.getArticle();
    LOGGER.info(String.format("%s | Indexing article %s with identification: %s", this.getTargetUid(), article.getId(),
        article.getFileIdentification()));

    final TargetConfiguration configuration = this.getFetcher().getTargetConfiguration(this.getTargetUid());
    if (configuration.getImportStates() == null) {
      configuration.setImportStates(ImportState.getImportStates(configuration.getIndexType()));
    }
    final List<ImportState> importStates = configuration.getImportStates();

    final AbstractDataManager dataManager = this.getFetcher().getDataRegistry().getDataManager(this.getTargetUid());
    if (dataManager == null) return;
    if (!dataManager.hasArticle(article.getId()))
      dataManager.addArticle(article);

    if (importStates.isEmpty()) return;
    final Extractor extractor = this.getFetcher().getExtractorRegistry().getExtractor(this.getTargetUid());
    final File articleFile = new File(this.getWorkDir(),
        String.format("%s%s", article.getFileIdentification(), FileUtils.buildFileSuffix(configuration.getSuffix())));

    try {
      final Document parse = Jsoup.parse(articleFile, StandardCharsets.UTF_8.name());

      importStates.forEach(
          importState -> importState.getHandler().handel(LOGGER, this.getArticle(), dataManager, extractor, parse));
    } catch (IOException e) {
      LOGGER.error("Error while indexing file", e);
    }
  }

  @Override
  public AbstractFetcher getFetcher() {
    return this.fetcher;
  }

  @Override
  public Article getArticle() {
    return this.article;
  }

  @Override
  public File getWorkDir() {
    return this.workDir;
  }

  @Override
  public String getTargetUid() {
    return this.targetUid;
  }

  @Override
  public String getJobId() {
    return this.jobId;
  }

  @Override
  public JobPriority getPriority() {
    return this.priority;
  }
}
