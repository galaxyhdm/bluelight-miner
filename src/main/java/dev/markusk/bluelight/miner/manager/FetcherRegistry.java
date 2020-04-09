package dev.markusk.bluelight.miner.manager;

import dev.markusk.bluelight.api.interfaces.AbstractInfoFetcher;

import java.util.HashMap;

public class FetcherRegistry {

  private final HashMap<String, AbstractInfoFetcher> fetcherMap;

  public FetcherRegistry() {
    this.fetcherMap = new HashMap<>();
  }

  public void addInfoFetcher(final AbstractInfoFetcher infoFetcher) {
    this.fetcherMap.put(infoFetcher.getTargetUid(), infoFetcher);
  }

  public void removeInfoFetcher(final String targetUid) {
    this.fetcherMap.remove(targetUid);
  }

  public HashMap<String, AbstractInfoFetcher> getFetcherMap() {
    return fetcherMap;
  }
}
