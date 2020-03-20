package dev.markusk.bluelight.miner.config;

import dev.markusk.bluelight.miner.Constants;
import dev.markusk.bluelight.miner.data.LastUrlData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DataStore {

  private static final Logger LOGGER = LogManager.getLogger();

  private final File file;

  private LastUrlData data;

  public DataStore(final File file) {
    this.file = file;
  }

  public void setLastUrl(final String targetUid, final String url) {
    if (this.data == null) throw new NullPointerException("Json data not initialized");
    this.data.getLastUrlMap().put(targetUid, url);
  }

  public String getLastUrl(final String targetUid) {
    if (this.data == null) throw new NullPointerException("Json data not initialized");
    return this.data.getLastUrlMap().get(targetUid);
  }

  public synchronized void loadMap() {
    if (!this.file.exists()) this.data = new LastUrlData();
    try (final FileReader fileReader = new FileReader(this.file, StandardCharsets.UTF_8)) {
      this.data = Constants.GSON.fromJson(fileReader, LastUrlData.class);
    } catch (IOException e) {
      LOGGER.error("Error while loading file", e);
    }
  }

  public synchronized void saveMap() {
    try (final FileWriter writer = new FileWriter(this.file, StandardCharsets.UTF_8)) {
      Constants.GSON.toJson(this.data, writer);
    } catch (IOException e) {
      LOGGER.error("Error while writing file", e);
    }
  }

}
