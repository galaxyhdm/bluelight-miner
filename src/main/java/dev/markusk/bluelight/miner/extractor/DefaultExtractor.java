package dev.markusk.bluelight.miner.extractor;

import dev.markusk.bluelight.api.interfaces.Extractor;
import dev.markusk.bluelight.api.objects.Location;
import dev.markusk.bluelight.api.objects.Topic;
import dev.markusk.bluelight.api.util.CodeGeneration;
import org.jsoup.nodes.Document;

import java.util.Set;

public class DefaultExtractor implements Extractor {

  @Override
  public String getIdFromUrl(final String url) {
    return null;
  }

  @Override
  public String getUniqueId() {
    return CodeGeneration.getCodeString(1, 9, '-', CodeGeneration.Mode.ALPHANUMRICUPPER);
  }

  @Override
  public Set<Location> getLocations(final Document document) {
    return null;
  }

  @Override
  public Set<Topic> getTopics(final Document document) {
    return null;
  }

  @Override
  public String getContent(final Document document) {
    return null;
  }
}
