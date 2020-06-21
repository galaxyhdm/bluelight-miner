package dev.markusk.bluelight.miner.data;

import dev.markusk.bluelight.api.data.DataSettings;

public class DataSettingsImpl implements DataSettings {

  private String adapterName;
  // "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
  private String connectionUrl;

  @Override
  public String getAdapterName() {
    return this.adapterName;
  }

  @Override
  public void setAdapterName(final String adapterName) {
    this.adapterName = adapterName;
  }

  @Override
  public String getConnectionUrl() {
    return this.connectionUrl;
  }

  @Override
  public void setConnectionUrl(final String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

}
