package uaspoofing;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import uaspoofing.output.OutputHandler;
import uaspoofing.apk.APK;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

// java -cp target/user-agent-spoofing-1.0.jar uaspoofing.Main D:\wimde\cs\bscproject\dataset\Benign-sample-187\a.a.hikidashi.apk
// java -cp target/user-agent-spoofing-1.0.jar uaspoofing.Main D:\wimde\cs\bscproject\dataset\samples\BeanBot\4edab972cc232a2525d6994760f0f71088707164.apk -j
// java -cp target/user-agent-spoofing-1.0.jar uaspoofing.Main -j D:\wimde\cs\bscproject\dataset\samples

/**
 * Main class. Handles input and creation of classes.
 */
public class Main {
  public static void main(String[] args) throws IOException {
    Options options = new Options();
    addOptions(options);

    CommandLineParser parser = new DefaultParser();

    try {
      CommandLine cmd = parser.parse(options, args);

      if (cmd.getArgs().length == 1) {
        String location = cmd.getArgs()[0];
        String outputDir;

        if (new File(location).isDirectory()) {
          OutputHandler.dirMode();

          processDirectory(location);
        }
        else if (new File(location).isFile()) {
          if (cmd.hasOption("o")) {
            outputDir = cmd.getOptionValue("o");
          } else {
            OutputHandler.print(OutputHandler.Type.INF,
                "No output directory specified, using default.");
            outputDir = null;

            OutputHandler.fileMode();
          }

          APK apk = new APK(new File(location), outputDir);
          apk.decompile();
          apk.findUAs();
          apk.classifyUAs();
        }
      }

      else {
        HelpFormatter f = new HelpFormatter();
        f.printHelp(
            "uaspoofing.Main (APK File | APK directory) [options]",
            options);
      }

    } catch (ParseException e) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "Parsing failed!" + e.getMessage());
    }
  }

  private static void addOptions(Options options) {
    options.addOption("o", true, "Output directory");
  }

  private static void processDirectory(String loc)
      throws IOException {
    OutputHandler.print(OutputHandler.Type.INF,
        "Directory: " + loc);

    File log = new File(loc + "\\isProcessed.txt");
    if (!log.exists() && !log.createNewFile()) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "File isProcessed.txt could not be created!");
    }

    _processDir(new File(loc), log);
  }

  private static void _processDir(File d, File log) {

    for (File f : d.listFiles()) {
      if (f.isDirectory() && !f.getName().contains("uaspoof")) {
        OutputHandler.print(OutputHandler.Type.INF,
            "Directory: " + f.getPath());
        _processDir(f, log);
      }
      else {
        if (!isProcessed(f, log) &&
            FilenameUtils.getExtension(f.getName()).equals("apk")) {
          APK apk = new APK(f, null);

          apk.decompile();
          apk.findUAs();
          apk.classifyUAs();


          try {
            FileWriter w = new FileWriter(log, true);
            w.write(f.getPath() + "\n");
            w.close();
          } catch (IOException e) {
            OutputHandler.print(OutputHandler.Type.WRN,
                "Could not write to isProcessed.txt! This file might be " +
                    "scanned again in a next run.");
          }

          OutputHandler.separator();
        }
      }
    }
  }

  private static boolean isProcessed(File file, File log) {
    Scanner s = null;
    try {
      s = new Scanner(log);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    while (s.hasNextLine()) {
      String line = s.nextLine();
      if (line.equals(file.getPath())) {
        s.close();
        return true;
      }
    }

    s.close();
    return false;
  }

}
