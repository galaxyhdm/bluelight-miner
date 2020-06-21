package dev.markusk.bluelight.miner.config;

import dev.markusk.bluelight.api.config.TargetConfiguration;
import dev.markusk.bluelight.api.data.DataSettings;
import dev.markusk.bluelight.api.enums.ImportState;

import java.util.List;

public class TargetConfigurationImpl implements TargetConfiguration {

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
  private String fetcherPath;
  private String workDir;
  private String suffix;
  private DataSettings database;

  private List<ImportState> importStates;

  @Override
  public String getFetchUrl() {
    return fetchUrl;
  }

  @Override
  public void setFetchUrl(final String fetchUrl) {
    this.fetchUrl = fetchUrl;
  }

  @Override
  public int getUpdateTime() {
    return updateTime;
  }

  @Override
  public void setUpdateTime(final int updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public boolean isTor() {
    return tor;
  }

  @Override
  public void setTor(final boolean tor) {
    this.tor = tor;
  }

  @Override
  public boolean isAutoIndex() {
    return autoIndex;
  }

  @Override
  public void setAutoIndex(final boolean autoIndex) {
    this.autoIndex = autoIndex;
  }

  @Override
  public byte getIndexType() {
    return indexType;
  }

  @Override
  public void setIndexType(final byte indexType) {
    this.indexType = indexType;
  }

  @Override
  public String getExtractorPath() {
    return extractorPath;
  }

  @Override
  public void setExtractorPath(final String extractorPath) {
    this.extractorPath = extractorPath;
  }

  @Override
  public String getFetcherPath() {
    return fetcherPath;
  }

  @Override
  public void setFetcherPath(final String fetcherPath) {
    this.fetcherPath = fetcherPath;
  }

  @Override
  public String getWorkDir() {
    return workDir;
  }

  @Override
  public void setWorkDir(final String workDir) {
    this.workDir = workDir;
  }

  @Override
  public String getSuffix() {
    return this.suffix;
  }

  @Override
  public void setSuffix(final String suffix) {
    this.suffix = suffix;
  }

  @Override
  public DataSettings getDatabase() {
    return this.database;
  }

  @Override
  public void setDatabase(final DataSettings database) {
    this.database = database;
  }

  @Override
  public List<ImportState> getImportStates() {
    return this.importStates;
  }

  @Override
  public void setImportStates(final List<ImportState> importStates) {
    this.importStates = importStates;
  }

}
