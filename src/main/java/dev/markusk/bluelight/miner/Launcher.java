package dev.markusk.bluelight.miner;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;

public class Launcher {

  public Launcher(final OptionSet optionSet) {
    System.out.println("\n" +
        "                        _           __        __    __           ___       __    __ \n" +
        "    ____  _________    (_)__  _____/ /_      / /_  / /_  _____  / (_)___ _/ /_  / /_\n" +
        "   / __ \\/ ___/ __ \\  / / _ \\/ ___/ __/_____/ __ \\/ / / / / _ \\/ / / __ `/ __ \\/ __/\n" +
        "  / /_/ / /  / /_/ / / /  __/ /__/ /_/_____/ /_/ / / /_/ /  __/ / / /_/ / / / / /_  \n" +
        " / .___/_/   \\____/_/ /\\___/\\___/\\__/     /_.___/_/\\__,_/\\___/_/_/\\__, /_/ /_/\\__/  \n" +
        "/_/              /___/                                           /____/   by markus.\n");

    final Miner miner = new Miner(optionSet);
    miner.initialize();
  }

  public static void main(String[] args) {
    final OptionParser optionParser = createOptionParser();
    final OptionSet optionSet = optionParser.parse(args);
    if (optionSet.has("help")) {
      try {
        optionParser.printHelpOn(System.out);
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.exit(-1);
    }
    new Launcher(optionSet);
  }

  private static OptionParser createOptionParser() {
    final OptionParser optionParser = new OptionParser();
    optionParser.accepts("debug", "Enables the debug mode");
    optionParser.accepts("help", "See help");
    optionParser.accepts("dir", "Selects work dir").withRequiredArg().ofType(File.class).defaultsTo(new File("work"));
    return optionParser;
  }

}
