package dev.markusk.bluelight.miner;

public class VersionInfo {

  public static final String VERSION;
  public static final String BUILD_DATE;
  public static final String BUILD_TIME;
  public static final boolean DEBUG;

  private static final String VERSION_RAW = "@VERSION@";
  private static final String BUILD_DATE_RAW = "@DATE@";
  private static final String BUILD_TIME_RAW = "@TIME@";

  static {
    //noinspection ConstantConditions
    VERSION = VERSION_RAW.startsWith("@") ? "DEVELOPMENT" : VERSION_RAW;
    BUILD_DATE = BUILD_DATE_RAW.startsWith("@") ? "DEBUG_BUILD" : BUILD_DATE_RAW;
    BUILD_TIME = BUILD_TIME_RAW.startsWith("@") ? "DEBUG_BUILD" : BUILD_TIME_RAW;
    DEBUG = VERSION_RAW.startsWith("@");
  }


}
