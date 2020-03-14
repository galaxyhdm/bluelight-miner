package dev.markusk.bluelight.miner.config;

public class TargetConfiguration {

  private String fetchUrl;
  private int updateTime;
  private boolean tor;

  public String getFetchUrl() {
    return fetchUrl;
  }

  public void setFetchUrl(final String fetchUrl) {
    this.fetchUrl = fetchUrl;
  }

  public int getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(final int updateTime) {
    this.updateTime = updateTime;
  }

  public boolean isTor() {
    return tor;
  }

  public void setTor(final boolean tor) {
    this.tor = tor;
  }
}
