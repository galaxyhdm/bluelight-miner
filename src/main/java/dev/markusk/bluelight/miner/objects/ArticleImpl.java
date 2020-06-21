package dev.markusk.bluelight.miner.objects;

import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.api.objects.Location;
import dev.markusk.bluelight.api.objects.Topic;

import java.util.Date;
import java.util.Set;

public class ArticleImpl implements Article {

  private String id;
  private String title;
  private String url;
  private Date releaseTime;
  private Date fetchTime;
  private String fileIdentification;
  private Set<Location> locationTags;
  private Set<Topic> topicTags;
  private String content;

  public ArticleImpl() {
  }

  public ArticleImpl(final String id, final String title, final String url, final Date releaseTime,
      final Date fetchTime,
      final String fileIdentification, final Set<Location> locationTags,
      final Set<Topic> topicTags, final String content) {
    this.id = id;
    this.title = title;
    this.url = url;
    this.releaseTime = releaseTime;
    this.fetchTime = fetchTime;
    this.fileIdentification = fileIdentification;
    this.locationTags = locationTags;
    this.topicTags = topicTags;
    this.content = content;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getTitle() {
    return this.title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getUrl() {
    return this.url;
  }

  @Override
  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public Date getReleaseTime() {
    return this.releaseTime;
  }

  @Override
  public void setReleaseTime(Date releaseTime) {
    this.releaseTime = releaseTime;
  }

  @Override
  public Date getFetchTime() {
    return this.fetchTime;
  }

  @Override
  public void setFetchTime(Date fetchTime) {
    this.fetchTime = fetchTime;
  }

  @Override
  public String getFileIdentification() {
    return this.fileIdentification;
  }

  @Override
  public void setFileIdentification(String fileIdentification) {
    this.fileIdentification = fileIdentification;
  }

  @Override
  public Set<Location> getLocationTags() {
    return this.locationTags;
  }

  @Override
  public void setLocationTags(Set<Location> locationTags) {
    this.locationTags = locationTags;
  }

  @Override
  public Set<Topic> getTopicTags() {
    return this.topicTags;
  }

  @Override
  public void setTopicTags(Set<Topic> topicTags) {
    this.topicTags = topicTags;
  }

  @Override
  public String getContent() {
    return this.content;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }

}
