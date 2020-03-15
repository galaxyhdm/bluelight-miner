package dev.markusk.bluelight.miner.job;

import dev.markusk.bluelight.api.builder.ArticleBuilder;
import dev.markusk.bluelight.api.impl.BaseFetchInfo;
import dev.markusk.bluelight.api.interfaces.Extractor;
import dev.markusk.bluelight.api.job.AbstractJob;
import dev.markusk.bluelight.api.job.JobPriority;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.miner.Environment;
import dev.markusk.bluelight.miner.Miner;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.stream.Stream;

public class DownloadJob implements AbstractJob {

  private static final Logger LOGGER = LogManager.getLogger();

  private final Miner miner;
  private final String jobId;
  private final JobPriority priority;
  private final BaseFetchInfo baseInfo;

  public DownloadJob(final String jobId, final JobPriority priority, final BaseFetchInfo baseInfo, final Miner miner) {
    this.jobId = jobId;
    this.priority = priority;
    this.baseInfo = baseInfo;
    this.miner = miner;
  }

  @Override
  public void run() {
    final String fileIdentification = this.getFileIdentification();
    final Extractor extractor = this.miner.getExtractorRegistry().getExtractor(this.baseInfo.getTargetUid());

    String id = extractor.getIdFromUrl(this.baseInfo.getUrl());
    if (id == null) id = extractor.getUniqueId();

    final Article article = new ArticleBuilder().id(id).url(this.baseInfo.getUrl()).title(this.baseInfo.getTitle())
        .fetchTime(this.baseInfo.getFetchTime()).releaseTime(this.baseInfo.getReleaseTime())
        .fileIdentification(fileIdentification).createArticle();

    String[] commandArray =
        {"curl", this.baseInfo.getUrl(), "-o", article.getFileIdentification() + ".html"};

    if (Environment.TOR) { // TODO: 15.03.2020 update to get info from TargetConfiguration
      String[] torCommand = {"torsocks", "-i"};
      commandArray = Stream.of(torCommand, commandArray).flatMap(Stream::of).toArray(String[]::new);
    }

    LOGGER.debug(Arrays.toString(commandArray));
    LOGGER.info(article.getId());
  }

  private String getFileIdentification() {
    final String fileIdentificationRaw = String
        .format("%s%s%s%s", this.baseInfo.getTargetUid(), this.baseInfo.getFetchTime(), this.baseInfo.getTitle(),
            this.baseInfo.getUrl());
    return DigestUtils.sha256Hex(fileIdentificationRaw);
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
