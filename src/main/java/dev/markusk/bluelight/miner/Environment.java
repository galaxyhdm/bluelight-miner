package dev.markusk.bluelight.miner;

public class Environment {

  public static final int WAIT_MINUTES = 0;//Integer.parseInt(System.getenv("WAIT_MINUTES"));
  public static final boolean NO_FETCH = false;//Boolean.parseBoolean(System.getenv("NO_FETCH"));
  public static final boolean DEBUG = true;//Boolean.parseBoolean(System.getenv("DEBUG"));
  public static final boolean TOR = true;//Boolean.parseBoolean(System.getenv("TOR"));
  public static final int POOL_SIZE = 1;//Integer.parseInt(System.getenv("POOL_SIZE"));

}
