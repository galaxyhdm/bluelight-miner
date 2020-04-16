package dev.markusk.bluelight.miner.manager;

import dev.markusk.bluelight.api.interfaces.AbstractFetcherRegistry;
import dev.markusk.bluelight.api.interfaces.AbstractInfoFetcher;

import java.util.HashMap;

public class FetcherRegistry implements AbstractFetcherRegistry {

  private final HashMap<String, AbstractInfoFetcher> fetcherMap;

  public FetcherRegistry() {
    this.fetcherMap = new HashMap<>();
  }

  @Override
  public void addInfoFetcher(final AbstractInfoFetcher infoFetcher) {
    this.fetcherMap.put(infoFetcher.getTargetUid(), infoFetcher);
  }

  @Override
  public void removeInfoFetcher(final String targetUid) {
    this.fetcherMap.remove(targetUid);
  }

  @Override
  public HashMap<String, AbstractInfoFetcher> getFetcherMap() {
    return fetcherMap;
  }
}
