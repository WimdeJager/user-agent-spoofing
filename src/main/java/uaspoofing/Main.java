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

    boolean correctInput = true;

    try {
      CommandLine cmd = parser.parse(options, args);

      if (cmd.getArgs().length == 1) {
        // if user specified a file or directory
        File location = new File(cmd.getArgs()[0]);
        if (!location.exists()) {
          // throw exception if input file does not exist
          throw new FileNotFoundException("APK file or directory does not " +
              "exist!");
        }

        if (location.isDirectory()) {
          // DIRECTORY MODE
          if (cmd.hasOption("o")) {
            // output directory cannot be specified in directory mode
            correctInput = false;
          } else {
            // start processing APKs
            OutputHandler.dirMode();
            processDirectory(location);
          }
        }
        else if (location.isFile()) {
          // FILE MODE
          File outputDir;

          if (cmd.hasOption("o")) {
            outputDir = new File(cmd.getOptionValue("o"));
          } else {
            OutputHandler.print(OutputHandler.Type.INF,
                "No output directory specified, using default.");

            // set outputDir to null to indicate default directory
            outputDir = null;

            OutputHandler.fileMode();
          }

          // process APK
          APK apk = new APK(location, outputDir);
          apk.decompile();
          apk.findUAs();
          apk.classifyUAs();
        }
      }

      else {
        // user did not specify an input file
        correctInput = false;
      }

      if (!correctInput) {
        // print 'usage: ...'
        HelpFormatter f = new HelpFormatter();
        f.printHelp(
            "uaspoofing.Main <input file> [options]\n" +
                "Note: output directory can only be specified in file mode.",
            options);
      }

    } catch (ParseException e) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "Parsing failed!" + e.getMessage());
      e.printStackTrace();
      System.exit(0);
    }
  }

  private static void addOptions(Options options) {
    options.addOption("o", true, "Output directory");
  }

  private static void processDirectory(File loc) {
    // process directory recursively
    OutputHandler.print(OutputHandler.Type.INF,
        "Directory: " + loc.getName());

    // create file isProcessed.txt to keep track of APKs that have been
    // processed
    File log = new File(loc + "\\isProcessed.txt");
    try {
      if (!log.exists() && !log.createNewFile()) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "File isProcessed.txt could not be created!");
      }
    } catch (IOException e) {
      // createNewFile() failed
      e.printStackTrace();
    }

    _processDir(loc, log);
  }

  private static void _processDir(File d, File log) {
    // helper function for processDir

    for (File f : d.listFiles()) {
      // loop over all files in directory
      if (f.isDirectory() && !f.getName().contains("uaspoof")) {
        // if file is directory, recurse
        // if file prefix is 'uaspoof-...', directory is an output directory
        // for a previously processed APK
        OutputHandler.print(OutputHandler.Type.INF,
            "Directory: " + f.getPath());
        _processDir(f, log);
      }

      else {
        if (!isProcessed(f, log) &&
            FilenameUtils.getExtension(f.getName()).equals("apk")) {
          // if file has not been previously processed and file is indeed an
          // APK file, process APK

          APK apk = new APK(f, null);
          apk.decompile();
          apk.findUAs();
          apk.classifyUAs();

          // write APK name to isProcessed.txt
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
    // check if APK is in isProcessed.txt
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
