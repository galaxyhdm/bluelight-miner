package dev.markusk.bluelight.miner.console;

import org.jline.reader.LineReader;

public class TerminalConsoleWriterThread implements Runnable {

  private final LineReader reader;

  public TerminalConsoleWriterThread(final LineReader reader) {
    this.reader = reader;
  }

  @Override
  public void run() {
    String message;
    while (true) {
      message = QueueLogAppender.getNextLogEvent("TerminalConsole");
      if (message == null) continue;

      try {
        if (reader.isReading()) {
          reader.callWidget(LineReader.CLEAR);
          reader.getTerminal().writer().println(message.trim());
          reader.callWidget(LineReader.REDRAW_LINE);
          reader.callWidget(LineReader.REDISPLAY);
        } else {
          reader.getTerminal().writer().println(message.trim());
        }
        reader.getTerminal().writer().flush();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }
}
