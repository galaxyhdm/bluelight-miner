package dev.markusk.bluelight.miner.config;

import java.util.List;
import java.util.Map;

public final class Configuration {

  private Map<String, TargetConfiguration> targets;
  private List<String> userAgents;

  public Map<String, TargetConfiguration> getTargets() {
    return targets;
  }

  public void setTargets(final Map<String, TargetConfiguration> targets) {
    this.targets = targets;
  }

  public List<String> getUserAgents() {
    return userAgents;
  }

  public void setUserAgents(final List<String> userAgents) {
    this.userAgents = userAgents;
  }
}
