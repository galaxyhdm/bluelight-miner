package dev.markusk.bluelight.miner.builder;

import dev.markusk.bluelight.api.builder.BaseFetchInfoBuilder;
import dev.markusk.bluelight.api.interfaces.BaseFetchInfo;
import dev.markusk.bluelight.miner.impl.BaseFetchInfoImpl;

import java.util.Date;

public class BaseFetchInfoBuilderImpl implements BaseFetchInfoBuilder {

  private String targetUid;
  private String url;
  private String title;
  private Date releaseTime;
  private Date fetchTime;

  @Override
  public BaseFetchInfoBuilder targetUid(final String targetUid) {
    this.targetUid = targetUid;
    return this;
  }

  @Override
  public BaseFetchInfoBuilder url(final String url) {
    this.url = url;
    return this;
  }

  @Override
  public BaseFetchInfoBuilder title(final String title) {
    this.title = title;
    return this;
  }

  @Override
  public BaseFetchInfoBuilder releaseTime(final Date releaseTime) {
    this.releaseTime = releaseTime;
    return this;
  }

  @Override
  public BaseFetchInfoBuilder fetchTime(final Date fetchTime) {
    this.fetchTime = fetchTime;
    return this;
  }

  @Override
  public BaseFetchInfo createBaseFetchInfo() {
    return new BaseFetchInfoImpl(targetUid, url, title, releaseTime, fetchTime);
  }

}
