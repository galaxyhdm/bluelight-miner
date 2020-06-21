package dev.markusk.bluelight.miner.console;

import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class BetterSystemOut {

  private final Logger logger;
  private final PrintStream oldPrintStream;

  public BetterSystemOut(final Logger logger) {
    this.logger = logger;
    this.oldPrintStream = System.out;
  }

  public void overwrite() {
    System.setOut(new BetterPrintStream(System.out, this.logger));
  }

  public void removeOverwrite() {
    System.setOut(this.oldPrintStream);
  }

  private static class BetterPrintStream extends PrintStream {

    private final Logger logger;

    public BetterPrintStream(final OutputStream out, final Logger logger) {
      super(out);
      this.logger = logger;
    }

    @Override
    public void println() {
      super.println();
    }

    @Override
    public void println(final String str) {
      this.logger.info(str);
    }

    @Override
    public void println(final int x) {
      this.logger.info(x);
    }

    @Override
    public void println(final boolean x) {
      this.logger.info(x);
    }

    @Override
    public void println(final char x) {
      this.logger.info(x);
    }

    @Override
    public void println(final long x) {
      this.logger.info(x);
    }

    @Override
    public void println(final float x) {
      this.logger.info(x);
    }

    @Override
    public void println(final double x) {
      this.logger.info(x);
    }

    @Override
    public void println(final char[] x) {
      this.logger.info(x);
    }

    @Override
    public void println(final Object x) {
      this.logger.info(x);
    }
  }

}
