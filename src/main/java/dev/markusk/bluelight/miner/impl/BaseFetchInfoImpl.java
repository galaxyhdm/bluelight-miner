package dev.markusk.bluelight.miner.impl;

import dev.markusk.bluelight.api.interfaces.BaseFetchInfo;

import java.util.Date;

public class BaseFetchInfoImpl implements BaseFetchInfo {

  /**
   * This variable must be set
   * <p>
   * This string is used for identify the target.
   * <p>
   * Used for the content extractor and all other processes depends on the target.
   */
  private String targetUid;
  /**
   * This variable must be set
   * <p>
   * This string links to the complete article.
   **/
  private String url;
  /**
   * This string represents the title of the article.
   * <p>
   * Can be null
   */
  private String title;

  /**
   * This date represents the release time of the article
   * <p>
   * Can be null.
   */
  private Date releaseTime;

  /**
   * This date represents the fetch time of the article
   */
  private Date fetchTime;

  public BaseFetchInfoImpl(final String targetUid, final String url, final String title, final Date releaseTime,
      final Date fetchTime) {
    this.targetUid = targetUid;
    this.url = url;
    this.title = title;
    this.releaseTime = releaseTime;
    this.fetchTime = fetchTime;
  }


  public String getTargetUid() {
    return targetUid;
  }

  public void setTargetUid(final String targetUid) {
    this.targetUid = targetUid;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public Date getReleaseTime() {
    return releaseTime;
  }

  public void setReleaseTime(final Date releaseTime) {
    this.releaseTime = releaseTime;
  }

  public Date getFetchTime() {
    return fetchTime;
  }

  public void setFetchTime(final Date fetchTime) {
    this.fetchTime = fetchTime;
  }

  @Override
  public String toString() {
    return "BaseFetchInfo{" +
        "targetUid='" + targetUid + '\'' +
        ", url='" + url + '\'' +
        ", title='" + title + '\'' +
        ", releaseTime=" + releaseTime +
        ", fetchTime=" + fetchTime +
        '}';
  }

}
