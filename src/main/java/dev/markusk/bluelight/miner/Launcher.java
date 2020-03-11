package dev.markusk.bluelight.miner;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;

public class Launcher {

  public Launcher(final OptionSet optionSet) {
    System.out.println("\n" +
        "______ _             _ _      _     _         ___  ____                 \n" +
        "| ___ \\ |           | (_)    | |   | |        |  \\/  (_)                \n" +
        "| |_/ / | __ _ _   _| |_  ___| |__ | |_ ______| .  . |_ _ __   ___ _ __ \n" +
        "| ___ \\ |/ _` | | | | | |/ __| '_ \\| __|______| |\\/| | | '_ \\ / _ \\ '__|       _*_ ....iiooiioo\n" +
        "| |_/ / | (_| | |_| | | | (__| | | | |_       | |  | | | | | |  __/ |       __/_|_\\__ \n" +
        "\\____/|_|\\__,_|\\__,_|_|_|\\___|_| |_|\\__|      \\_|  |_/_|_| |_|\\___|_|      [(o)_R_(o)] \n" +
        "                                                              by Markus.\n");
    final WebsiteMiner websiteMiner = new WebsiteMiner(optionSet);
    websiteMiner.initialize();
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
    optionParser.accepts("dir", "Selects work dir").withRequiredArg().ofType(File.class).defaultsTo(new File("work"));
    optionParser.accepts("index-dir", "Selects index dir").withRequiredArg().ofType(File.class)
        .defaultsTo(new File("article_index"));
    optionParser.accepts("help", "See help");
    return optionParser;
  }

}
