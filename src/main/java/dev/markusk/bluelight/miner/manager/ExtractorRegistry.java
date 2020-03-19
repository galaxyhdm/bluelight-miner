package dev.markusk.bluelight.miner.manager;

import dev.markusk.bluelight.api.interfaces.Extractor;

import java.util.HashMap;

public class ExtractorRegistry {

  private final HashMap<String, Extractor> extractorMap;
  private final Extractor defaultExtractor;

  public ExtractorRegistry(Extractor defaultExtractor) {
    this.extractorMap = new HashMap<>();
    this.defaultExtractor = defaultExtractor;
    if (this.defaultExtractor == null)
      throw new NullPointerException("defaultExtractor is null!");
  }

  public void addExtractor(final String targetId, final Extractor extractor) {
    this.extractorMap.put(targetId, extractor);
  }

  public void removeExtractor(final String targetUid) {
    this.extractorMap.remove(targetUid);
  }

  public HashMap<String, Extractor> getExtractorMap() {
    return this.extractorMap;
  }

  /**
   * @param targetUid the targetUid for the extractor
   * @return the provided {@link Extractor} or when the result is null return the {@link dev.markusk.bluelight.miner.extractor.DefaultExtractor}
   */
  public Extractor getExtractor(final String targetUid) {
    final Extractor result = this.extractorMap.get(targetUid);
    return result == null ? this.defaultExtractor : result;
  }

}
