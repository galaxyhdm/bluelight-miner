package dev.markusk.bluelight.miner.builder;

import dev.markusk.bluelight.api.builder.ArticleBuilder;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.api.objects.Location;
import dev.markusk.bluelight.api.objects.Topic;
import dev.markusk.bluelight.miner.objects.ArticleImpl;

import java.util.Date;
import java.util.Set;

public class ArticleBuilderImpl implements ArticleBuilder {

  private String id;
  private String title;
  private String url;
  private Date releaseTime;
  private Date fetchTime;
  private String fileIdentification;
  private Set<Location> locationTags;
  private Set<Topic> topicTags;
  private String content;

  @Override
  public ArticleBuilder id(final String id) {
    this.id = id;
    return this;
  }

  @Override
  public ArticleBuilder title(final String title) {
    this.title = title;
    return this;
  }

  @Override
  public ArticleBuilder url(final String url) {
    this.url = url;
    return this;
  }

  @Override
  public ArticleBuilder releaseTime(final Date releaseTime) {
    this.releaseTime = releaseTime;
    return this;
  }

  @Override
  public ArticleBuilder fetchTime(final Date fetchTime) {
    this.fetchTime = fetchTime;
    return this;
  }

  @Override
  public ArticleBuilder fileIdentification(final String fileIdentification) {
    this.fileIdentification = fileIdentification;
    return this;
  }

  @Override
  public ArticleBuilder locationTags(final Set<Location> locationTags) {
    this.locationTags = locationTags;
    return this;
  }

  @Override
  public ArticleBuilder topicTags(final Set<Topic> topicTags) {
    this.topicTags = topicTags;
    return this;
  }

  @Override
  public ArticleBuilder content(final String content) {
    this.content = content;
    return this;
  }

  @Override
  public Article createArticle() {
    return new ArticleImpl(id, title, url, releaseTime, fetchTime, fileIdentification, locationTags, topicTags, content);
  }

}
