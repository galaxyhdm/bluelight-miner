package dev.markusk.bluelight.miner.config;

public class TargetConfiguration {

  private String fetchUrl;
  private Integer updateTime;
  private Boolean tor;
  private Boolean autoIndex;

  /**
   * 1 = locations
   * 2 = topics
   * 4 = content
   * <p>
   * mean:
   * - 1 = locations only
   * - 3 = locations + topics
   * - 5 = location + content
   * - 6 = topic + content
   * - 7 = location + topic + content
   */
  private Byte indexType;
  private String extractorPath;

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

  public boolean isAutoIndex() {
    return autoIndex;
  }

  public void setAutoIndex(final boolean autoIndex) {
    this.autoIndex = autoIndex;
  }

  public byte getIndexType() {
    return indexType;
  }

  public void setIndexType(final byte indexType) {
    this.indexType = indexType;
  }

  public String getExtractorPath() {
    return extractorPath;
  }

  public void setExtractorPath(final String extractorPath) {
    this.extractorPath = extractorPath;
  }
}
