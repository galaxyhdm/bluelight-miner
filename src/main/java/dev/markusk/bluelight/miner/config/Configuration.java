package dev.markusk.bluelight.miner.config;

import java.util.Map;

public final class Configuration {

  private Map<String, TargetConfiguration> targets;

  public Map<String, TargetConfiguration> getTargets() {
    return targets;
  }

  public void setTargets(final Map<String, TargetConfiguration> targets) {
    this.targets = targets;
  }
}
