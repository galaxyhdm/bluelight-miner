package dev.markusk.bluelight.miner.objects;

import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.api.objects.Location;
import dev.markusk.bluelight.api.objects.Topic;

import java.util.Date;
import java.util.List;

public class BluelightArticle implements Article {

  private String id;
  private String title;
  private String url;
  private Date releaseTime;
  private Date fetchTime;
  private String fileIdentification;
  private List<Location> locationTags;
  private List<Topic> topicTags;
  private String content;

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public String getTitle() {
    return this.title;
  }

  @Override
  public void setTitle(final String title) {
    this.title = title;
  }

  @Override
  public String getUrl() {
    return this.url;
  }

  @Override
  public void setUrl(final String url) {
    this.url = url;
  }

  @Override
  public Date getReleaseTime() {
    return this.releaseTime;
  }

  @Override
  public void setReleaseTime(final Date releaseTime) {
    this.releaseTime = releaseTime;
  }

  @Override
  public Date getFetchTime() {
    return this.fetchTime;
  }

  @Override
  public void setFetchTime(final Date fetchTime) {
    this.fetchTime = fetchTime;
  }

  @Override
  public String getFileIdentification() {
    return this.fileIdentification;
  }

  @Override
  public void setFileIdentification(final String fileIdentification) {
    this.fileIdentification = fileIdentification;
  }

  @Override
  public List<Location> getLocationTags() {
    return this.locationTags;
  }

  @Override
  public void setLocationTags(final List<Location> locationTags) {
    this.locationTags = locationTags;
  }

  @Override
  public List<Topic> getTopicTags() {
    return this.topicTags;
  }

  @Override
  public void setTopicTags(final List<Topic> topicTags) {
    this.topicTags = topicTags;
  }

  @Override
  public String getContent() {
    return this.content;
  }

  @Override
  public void setContent(final String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "BluelightArticle{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", url='" + url + '\'' +
        ", releaseTime=" + releaseTime +
        ", fetchTime=" + fetchTime +
        ", fileIdentification='" + fileIdentification + '\'' +
        '}';
  }
}
