package dev.markusk.bluelight.miner;

public class Environment {

  public static final int WAIT_MINUTES = 0;//Integer.parseInt(System.getenv("WAIT_MINUTES"));

  public static final boolean NO_FETCH =
      System.getenv("NO_FETCH") != null && Boolean.parseBoolean(System.getenv("NO_FETCH"));

  public static final boolean DEBUG = System.getenv("DEBUG") != null && Boolean.parseBoolean(System.getenv("DEBUG"));

  public static final boolean TOR = true;//Boolean.parseBoolean(System.getenv("TOR"));

  public static final int POOL_SIZE =
      System.getenv("POOL_SIZE") != null ? Integer.parseInt(System.getenv("POOL_SIZE")) : 2;

  public static final boolean SYNC_START =
      System.getenv("SYNC_START") != null && Boolean.parseBoolean(System.getenv("SYNC_START"));

}
