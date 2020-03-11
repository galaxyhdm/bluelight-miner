package dev.markusk.bluelight.miner.objects;

import dev.markusk.bluelight.api.objects.Topic;

public class BluelightTopic implements Topic {

  private String id;
  private String topicName;

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public String getTopicName() {
    return this.topicName;
  }

  @Override
  public void setTopicName(final String topicName) {
    this.topicName = topicName;
  }
}
