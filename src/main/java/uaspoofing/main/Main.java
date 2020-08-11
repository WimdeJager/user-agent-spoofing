package uaspoofing.main;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import uaspoofing.output.OutputHandler;
import uaspoofing.apk.APK;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

// java -cp target/user-agent-spoofing-1.0.jar uaspoofing.main.Main D:\wimde\cs\bscproject\dataset\Benign-sample-187\a.a.hikidashi.apk
// java -cp target/user-agent-spoofing-1.0.jar uaspoofing.main.Main D:\wimde\cs\bscproject\dataset\samples\BeanBot\4edab972cc232a2525d6994760f0f71088707164.apk -j
// java -cp target/user-agent-spoofing-1.0.jar uaspoofing.main.Main -j D:\wimde\cs\bscproject\dataset\samples

/**
 * Main class.
 */
public class Main {
  public static void main(String[] args) throws IOException {
    APK apk;
    Options options = new Options();
    addOptions(options);

    CommandLineParser parser = new DefaultParser();

    try {
      CommandLine cmd = parser.parse(options, args);

      if (cmd.getArgs().length == 1) {
        String location = cmd.getArgs()[0];
        String outputDir, method;

        if (cmd.hasOption("j")) {
          method = "JADX";
        } else if (cmd.hasOption("a")) {
          method = "AG";
        } else {
          OutputHandler.print(OutputHandler.Type.INF,
              "No decompilation method specified, using Androguard.");
          method = "AG";
        }

        if (new File(location).isDirectory()) {
          OutputHandler.dirMode();

          processDirectory(location, method);
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

          apk = new APK(new File(location), outputDir);
          apk.decompile(method);
          apk.findUAs();
          apk.classifyUAs();
        }
      }

      else {
        HelpFormatter f = new HelpFormatter();
        f.printHelp("... APK_FILE [options]", options);
      }

    } catch (ParseException e) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "Parsing failed!" + e.getMessage());
    }
  }

  private static void addOptions(Options options) {
    options.addOption("o", true, "Output directory")
        .addOption("a", false, "Use Androguard for decompilation")
        .addOption("j", false, "Use JADX for decompilation");
  }

  private static void processDirectory(String loc, String method)
      throws IOException {
    OutputHandler.print(OutputHandler.Type.INF,
        "Directory: " + loc);

    File log = new File(loc + "\\isProcessed.txt");
    if (!log.exists() && !log.createNewFile()) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "File isProcessed.txt could not be created!");
    }

    _processDir(new File(loc), method, log);
  }

  private static void _processDir(File d, String m, File log) {

    for (File f : d.listFiles()) {
      if (f.isDirectory() && !f.getName().contains("uaspoof")) {
        OutputHandler.print(OutputHandler.Type.INF,
            "Directory: " + f.getPath());
        _processDir(f, m, log);
      }
      else {
        if (!isProcessed(f, log) &&
            FilenameUtils.getExtension(f.getName()).equals("apk")) {
          APK apk = new APK(f, null);

          apk.decompile(m);
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
