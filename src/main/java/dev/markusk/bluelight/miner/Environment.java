package dev.markusk.bluelight.miner;

public class Environment {

  public static final int WAIT_MINUTES = Integer.parseInt(System.getenv("WAIT_MINUTES"));
  public static final boolean NO_FETCH = Boolean.parseBoolean(System.getenv("NO_FETCH"));
  public static final boolean DEBUG = Boolean.parseBoolean(System.getenv("DEBUG"));
  public static final boolean TOR = Boolean.parseBoolean(System.getenv("TOR"));
  public static final int POOL_SIZE = Integer.parseInt(System.getenv("POOL_SIZE"));

}
