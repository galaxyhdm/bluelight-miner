package dev.markusk.bluelight.miner.manager;

import dev.markusk.bluelight.api.interfaces.Extractor;

import java.util.HashMap;

public class ExtractorRegistry {

  private final HashMap<String, Extractor> extractorMap;

  public ExtractorRegistry() {
    this.extractorMap = new HashMap<>();
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

  public Extractor getExtractor(final String targetUid){
    return this.extractorMap.get(targetUid);
  }

}
