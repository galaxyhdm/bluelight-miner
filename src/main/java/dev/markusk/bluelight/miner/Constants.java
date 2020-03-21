package dev.markusk.bluelight.miner;

import com.google.gson.Gson;
import io.prometheus.client.Gauge;

import java.util.Random;

public class Constants {

  public static final Random RANDOM = new Random();
  public static final Gson GSON = new Gson();

  public static final Gauge ARTICLE_COUNT;

  static {
    ARTICLE_COUNT = Gauge.build().name("article_count").help("The article count").labelNames("targetUid").register();
  }

}
