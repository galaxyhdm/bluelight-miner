package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.AbstractFetcher;
import dev.markusk.bluelight.api.Constants;
import dev.markusk.bluelight.api.config.TargetConfiguration;
import dev.markusk.bluelight.api.interfaces.BaseFetchInfo;
import dev.markusk.bluelight.api.interfaces.Extractor;
import dev.markusk.bluelight.api.job.DownloadJob;
import dev.markusk.bluelight.api.job.JobPriority;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.api.util.FileUtils;
import dev.markusk.bluelight.miner.builder.ArticleBuilderImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class DownloadJobImpl implements DownloadJob {

  public static final int MAX_TRIES =
      System.getenv("MAX_TRIES") != null ? Integer.parseInt(System.getenv("MAX_TRIES")) : 3;

  private static final Logger LOGGER = LogManager.getLogger();

  private final AbstractFetcher fetcher;
  private final String jobId;
  private final JobPriority priority;
  private final BaseFetchInfo baseInfo;

  private int tries = 1;

  public DownloadJobImpl(final BaseFetchInfo baseInfo,
      final AbstractFetcher fetcher) {
    this(UUID.randomUUID().toString(), JobPriority.NORMAL, baseInfo, fetcher);
  }

  public DownloadJobImpl(final String jobId, final JobPriority priority, final BaseFetchInfo baseInfo,
      final AbstractFetcher fetcher) {
    this.jobId = jobId;
    this.priority = priority;
    this.baseInfo = baseInfo;
    this.fetcher = fetcher;
  }

  @Override
  public void run() {
    final String fileIdentification = this.getFileIdentification();
    final Extractor extractor = this.getExtractor();
    final TargetConfiguration configuration = this.getTargetConfiguration();

    String id = extractor.getIdFromUrl(this.getBaseInfo().getUrl());
    if (id == null) id = extractor.getUniqueId();

    final Article article = this.getArticle(fileIdentification, id);

    final File targetWorkDir = new File(this.getFetcher().getWorkDir(), configuration.getWorkDir());

    String[] commandArray =
        {"curl", "-A", String.format("'%s'", pickUserAgent()), this.getBaseInfo().getUrl(), "-L", "-o",
            String.format("%s%s", article.getFileIdentification(), FileUtils.buildFileSuffix(configuration.getSuffix()))
        };

    if (configuration.isTor()) {
      String[] torCommand = {"torsocks", "-i"};
      commandArray = Stream.of(torCommand, commandArray).flatMap(Stream::of).toArray(String[]::new);
    }

    LOGGER.info(String
        .format("%s | Downloading article %s with identification: %s", this.getBaseInfo().getTargetUid(),
            article.getId(),
            article.getFileIdentification()));
    LOGGER.debug(this.getBaseInfo().getTargetUid() + " | " + Arrays.toString(commandArray));

    do {
      try {
        final Process process =
            new ProcessBuilder(commandArray).redirectErrorStream(true).directory(targetWorkDir).start();
        final int exitCode = process.waitFor();
        LOGGER.debug(String.format("Try: %s | Exit-Code: %s", this.tries, exitCode));
        if (exitCode == 0) break;
        this.tries++;
      } catch (IOException | InterruptedException e) {
        LOGGER.error("Error while downloading", e);
      }
    } while (this.tries <= MAX_TRIES);


    if (configuration.isAutoIndex()) {
      this.getFetcher().getImportScheduler()
          .scheduleJob(new ImportJobImpl(article, this.getFetcher(), targetWorkDir, this.getBaseInfo().getTargetUid()));
    }
  }

  public Article getArticle(final String fileIdentification, final String id) { // TODO: 21.06.2020 !!!!
    return new ArticleBuilderImpl()
        .id(id).url(this.getBaseInfo().getUrl()).title(this.getBaseInfo().getTitle())
        .fetchTime(this.getBaseInfo().getFetchTime()).releaseTime(this.getBaseInfo().getReleaseTime())
        .fileIdentification(fileIdentification).createArticle();
  }

  private Extractor getExtractor() {
    return this.getFetcher().getExtractorRegistry().getExtractor(this.getBaseInfo().getTargetUid());
  }

  private TargetConfiguration getTargetConfiguration() {
    return this.getFetcher().getTargetConfiguration(this.getBaseInfo().getTargetUid());
  }

  public String getFileIdentification() {
    final String fileIdentificationRaw = String
        .format("%s%s%s%s", this.getBaseInfo().getTargetUid(), this.getBaseInfo().getFetchTime(),
            this.getBaseInfo().getTitle(),
            this.getBaseInfo().getUrl());
    return DigestUtils.sha256Hex(fileIdentificationRaw);
  }

  public String pickUserAgent() {
    final List<String> userAgents = this.getFetcher().getConfiguration().getUserAgents();
    return userAgents.get(Constants.RANDOM.nextInt(userAgents.size()));
  }

  @Override
  public AbstractFetcher getFetcher() {
    return this.fetcher;
  }

  @Override
  public BaseFetchInfo getBaseInfo() {
    return this.baseInfo;
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
