package dev.markusk.bluelight.miner.config;

import dev.markusk.bluelight.api.Constants;
import dev.markusk.bluelight.api.interfaces.AbstractUrlData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DataStore implements AbstractUrlData {

  private static final Logger LOGGER = LogManager.getLogger();

  private final File file;

  private LastUrlData data;

  public DataStore(final File file) {
    this.file = file;
  }

  @Override
  public void setLastUrl(final String targetUid, final String url) {
    if (this.data == null) throw new NullPointerException("Json data not initialized");
    this.data.getLastUrlMap().put(targetUid, url);
  }

  @Override
  public String getLastUrl(final String targetUid) {
    if (this.data == null) throw new NullPointerException("Json data not initialized");
    return this.data.getLastUrlMap().get(targetUid);
  }

  @Override
  public synchronized void load() {
    if (!this.file.exists()) {
      this.data = new LastUrlData();
      return;
    }
    try (final FileReader fileReader = new FileReader(this.file, StandardCharsets.UTF_8)) {
      this.data = Constants.GSON.fromJson(fileReader, LastUrlData.class);
    } catch (IOException e) {
      LOGGER.error("Error while loading file", e);
    }
  }

  @Override
  public synchronized void save() {
    try (final FileWriter writer = new FileWriter(this.file, StandardCharsets.UTF_8)) {
      Constants.GSON.toJson(this.data, writer);
    } catch (IOException e) {
      LOGGER.error("Error while writing file", e);
    }
  }

}
