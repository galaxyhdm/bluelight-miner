package dev.markusk.bluelight.miner.objects;

import dev.markusk.bluelight.api.objects.Location;

public class BluelightLocation implements Location {

  private String id;
  private String locationName;
  private Double latitude;
  private Double longitude;
  private boolean indexed;

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public String getLocationName() {
    return this.locationName;
  }

  @Override
  public void setLocationName(final String locationName) {
    this.locationName = locationName;
  }

  @Override
  public Double getLatitude() {
    return this.latitude;
  }

  @Override
  public void setLatitude(final Double latitude) {
    this.latitude = latitude;
  }

  @Override
  public Double getLongitude() {
    return this.longitude;
  }

  @Override
  public void setLongitude(final Double longitude) {
    this.longitude = longitude;
  }

  @Override
  public boolean isIndexed() {
    return this.indexed;
  }

  @Override
  public void setIndexed(final boolean indexed) {
    this.indexed = indexed;
  }
}
