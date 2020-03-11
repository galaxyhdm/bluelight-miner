package dev.markusk.bluelight.miner.factory;

import dev.markusk.bluelight.api.factory.FetcherFactory;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.api.objects.Location;
import dev.markusk.bluelight.api.objects.Topic;
import dev.markusk.bluelight.miner.objects.BluelightArticle;
import dev.markusk.bluelight.miner.objects.BluelightLocation;
import dev.markusk.bluelight.miner.objects.BluelightTopic;

public class BluelightFetcherFactory implements FetcherFactory {

  @Override
  public Article createArticle() {
    return new BluelightArticle();
  }

  @Override
  public Location createLocation() {
    return new BluelightLocation();
  }

  @Override
  public Topic createTopic() {
    return new BluelightTopic();
  }
}
