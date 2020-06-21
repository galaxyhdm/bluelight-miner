package dev.markusk.bluelight.miner.console;

import org.jline.reader.LineReader;

public interface ConsoleHandler {

  void handle(final LineReader lineReader, final String input);

}
