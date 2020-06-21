package dev.markusk.bluelight.miner.extractor;

import dev.markusk.bluelight.api.interfaces.Extractor;
import dev.markusk.bluelight.api.objects.Location;
import dev.markusk.bluelight.api.objects.Topic;
import dev.markusk.bluelight.miner.objects.LocationImpl;
import dev.markusk.bluelight.miner.objects.TopicImpl;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class BaseExtractor implements Extractor {

  @Override
  public String getIdFromUrl(final String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }

  @Override
  public String getUniqueId() {
    return null;
  }

  @Override
  public Set<Location> getLocations(final Document document) {
    final Set<Location> locations = new HashSet<>();
    final Elements elements = document.getElementsByClass("tags");
    if (elements.isEmpty()) return null;
    try {
      elements.get(0).children().forEach(element -> {
        final Location location = new LocationImpl();
        location.setLocationName(element.text().trim());
        locations.add(location);
      });
    } catch (Exception e) {
      return null;
    }
    return locations;
  }

  @Override
  public Set<Topic> getTopics(final Document document) {
    final Set<Topic> topics = new HashSet<>();
    final Elements elements = document.getElementsByClass("tags");
    if (elements.isEmpty()) return null;
    try {
      elements.get(1).children().forEach(element -> {
        final Topic topic = new TopicImpl();
        topic.setTopicName(element.text().trim());
        topics.add(topic);
      });
    } catch (Exception e) {
      return null;
    }
    return topics;
  }

  @Override
  public String getContent(final Document document) {
    return document.select("div.card > p:not([class])").text();
  }
}
