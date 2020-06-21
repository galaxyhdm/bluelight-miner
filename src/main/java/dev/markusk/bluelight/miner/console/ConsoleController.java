package dev.markusk.bluelight.miner.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConsoleController {

  private final boolean debug;
  private final String prompt;
  private final boolean startPrompt;

  private LineReader reader;
  private List<ConsoleHandler> handlers;

  public ConsoleController(final boolean debug, final boolean startPrompt) {
    this(debug, ">", startPrompt);
  }

  public ConsoleController(final boolean debug, final String prompt, boolean startPrompt) {
    this.debug = debug;
    this.prompt = prompt;
    this.startPrompt = startPrompt;
    this.handlers = new ArrayList<>();
  }

  public void setupConsole() {

    try {
      //Build JLine-Terminal, important variable system=true
      Terminal terminal = TerminalBuilder.builder().system(true).streams(System.in, System.out).build();
      this.reader = LineReaderBuilder.builder().terminal(terminal).parser(new DefaultParser())
          .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ").build(); //Hook up tap-completer
    } catch (Throwable e) {
      e.printStackTrace();
    }

    //setup console-input-thread
    final Thread consoleInputThread;
    if (startPrompt) {
      consoleInputThread = new Thread(() -> {
        String input;
        while (true) {
          input = reader.readLine(prompt, null, (MaskingCallback) null, null);
          if (input != null && input.trim().length() > 0) {
            final String finalInput = input;
            handlers.forEach(consoleHandler -> consoleHandler.handle(reader, finalInput));
          }
        }
      });
    } else consoleInputThread = null;

    //Remove all other handler and add ForwardLogHandler
    java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
    global.setUseParentHandlers(false);
    for (java.util.logging.Handler handler : global.getHandlers()) {
      global.removeHandler(handler);
    }
    global.addHandler(new ForwardLogHandler());

    //Remove all other appender
    final org.apache.logging.log4j.core.Logger logger =
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger());
    for (org.apache.logging.log4j.core.Appender appender : logger.getAppenders().values()) {
      if (appender instanceof org.apache.logging.log4j.core.appender.ConsoleAppender) {
        logger.removeAppender(appender);
      }
    }

    //Set logger to debug
    if (debug)
      Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);

    //Start TerminalConsoleWriterThread to output data to console
    final Thread consoleWriterThread = new Thread(new TerminalConsoleWriterThread(this.reader));
    consoleWriterThread.setName("console-writer-thread");
    consoleWriterThread.setDaemon(true);
    consoleWriterThread.start();

    //Final thread setup and start
    if (startPrompt && consoleInputThread != null) {
      consoleInputThread.setName("console-handler");
      consoleInputThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(logger));
      consoleInputThread.setDaemon(true);
      consoleInputThread.start();
    }
  }

  public void addHandler(final ConsoleHandler consoleHandler) {
    this.handlers.add(consoleHandler);
  }

  public void clearHandlers() {
    this.handlers.clear();
  }

  public List<ConsoleHandler> getHandlers() {
    return handlers;
  }

  public void setHandlers(final List<ConsoleHandler> handlers) {
    this.handlers = handlers;
  }

}
