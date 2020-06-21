package dev.markusk.bluelight.miner.config;

import dev.markusk.bluelight.api.config.Configuration;
import dev.markusk.bluelight.api.config.TargetConfiguration;

import java.util.List;
import java.util.Map;

public class ConfigurationImpl implements Configuration {

  private String moduleFolder;
  private Map<String, TargetConfiguration> targets;
  private List<String> userAgents;

  @Override
  public String getModuleFolder() {
    return moduleFolder;
  }

  @Override
  public void setModuleFolder(final String moduleFolder) {
    this.moduleFolder = moduleFolder;
  }

  @Override
  public Map<String, TargetConfiguration> getTargets() {
    return targets;
  }

  @Override
  public void setTargets(final Map<String, TargetConfiguration> targets) {
    this.targets = targets;
  }

  @Override
  public List<String> getUserAgents() {
    return userAgents;
  }

  @Override
  public void setUserAgents(final List<String> userAgents) {
    this.userAgents = userAgents;
  }

}
