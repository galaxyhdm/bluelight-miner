package dev.markusk.bluelight.miner.factory;

import dev.markusk.bluelight.api.builder.ArticleBuilder;
import dev.markusk.bluelight.api.factory.ObjectFactory;
import dev.markusk.bluelight.api.objects.Article;
import dev.markusk.bluelight.api.objects.Location;
import dev.markusk.bluelight.api.objects.Topic;
import dev.markusk.bluelight.miner.builder.ArticleBuilderImpl;
import dev.markusk.bluelight.miner.objects.ArticleImpl;
import dev.markusk.bluelight.miner.objects.LocationImpl;
import dev.markusk.bluelight.miner.objects.TopicImpl;

public class ObjectFactoryImpl implements ObjectFactory {

  @Override
  public Article createArticle() {
    return new ArticleImpl();
  }

  @Override
  public Location createLocation() {
    return new LocationImpl();
  }

  @Override
  public Topic createTopic() {
    return new TopicImpl();
  }

  @Override
  public ArticleBuilder createArticleBuilder() {
    return new ArticleBuilderImpl();
  }
}
