package ua_spoofing;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

// java -cp target/user-agent-spoofing-1.0.jar ua_spoofing.Main D:\wimde\cs\bscproject\dataset\Benign-sample-187\a.a.hikidashi.apk
// java -cp target/user-agent-spoofing-1.0.jar ua_spoofing.Main D:\wimde\cs\bscproject\dataset\samples\BeanBot\4edab972cc232a2525d6994760f0f71088707164.apk -j

/**
 * Main class.
 */
public class Main {
  public static void main(String[] args)
      throws IOException, InterruptedException, ParseException {
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
          method = "JADX";
        } else {
          OutputHandler.print(OutputHandler.Type.INF,
              "No decompilation method specified, using Androguard.");
          method = "ANDROGUARD";
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
          apk.findUA();
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
      throws IOException, InterruptedException {
    File d = new File(loc);
    _processDir(d, method);
  }

  private static void _processDir(File d, String method)
      throws IOException, InterruptedException {
    for (File f : d.listFiles()) {
      if (f.isDirectory()) {
        _processDir(f, method);
      }
      else {
        if (FilenameUtils.getExtension(f.getName()).equals("apk")) {
          APK apk = new APK(f, null);
          apk.decompile(method);
          apk.findUA();
          OutputHandler.newline();
        }
      }
    }
  }

}
