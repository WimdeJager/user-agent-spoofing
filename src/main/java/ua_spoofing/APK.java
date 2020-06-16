package ua_spoofing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import ua_spoofing.ua.UAFinder;

import java.io.File;
import java.io.IOException;

/**
 * Class that holds information about an APK file
 */
public class APK {
  /**
   * Location of (path to) the APK file
   */
  private File file;

  /**
   * Location of (path to) the decompiled source code (might be empty or not
   * existing if this.decompile() has not been called yet).
   */
  private File dir;

  /**
   * Name of the file (without path)
   */
  private String name;

  private boolean decompiled;

  /**
   * Constructor
   *
   * @param file       the location of the APK file
   * @param outputDir location the decompiled files should be placed (null if
   *                  not specified by user).
   */
  public APK(File file, String outputDir) {
    this.file = file;
    if (!file.exists() || file.isDirectory()) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "The APK file does not exist!");
      System.exit(1);
    }

    this.name = FilenameUtils.getName(file.getPath());
    if (outputDir != null) {
      this.dir = new File(outputDir);
    } else {
      this.dir = new File(this.file.getParent() + "\\"
          + FilenameUtils.removeExtension(name));
    }

    this.decompiled = false;
  }


  /**
   * Decompiles the APK file
   * @param method decompilation method to be used:
   *               - "JADX" to use JADX
   *               - "AG" or null to use Androguard
   * @throws IOException
   * @throws InterruptedException
   */
  public void decompile(String method) throws InterruptedException {
    Runtime rt = Runtime.getRuntime();

//    OutputHandler.print(OutputHandler.Type.INF,
//        "APK location: " + file.getPath());
//    OutputHandler.print(OutputHandler.Type.INF,
//        "Output location: " + dir.getPath());

    if (dir.exists()) {
//      OutputHandler.print(OutputHandler.Type.INF,
//          "Output directory already exists! Cleaning...");
      try {
        FileUtils.cleanDirectory(dir);
      } catch (IOException e) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "There was an error cleaning the output directory "
                + dir.getPath() + ", there might be another program using the" +
                " directory.");
        return;
      }
    } else {
      if (!dir.mkdirs()) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "Could not create directory " + dir.getPath());
        return;
      }
    }

//    OutputHandler.separator();

    OutputHandler.print(OutputHandler.Type.INF,
        "Decompiling " + file.getPath() + " ...");
//    OutputHandler.print(OutputHandler.Type.INF,
//        "(this may take a while, depending on the size of the APK)");

    Process pr;
    if (method.equals("JADX")) {
      try {
        pr = rt.exec("cmd /c jadx -r -ds " + dir.getPath() + " "
            + file.getPath());
        pr.waitFor();
        decompiled = true;
      } catch (IOException e) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "There was an error executing JADX. Do you have JADX installed " +
                "and did you add it to your PATH variable?");
        OutputHandler.print(OutputHandler.Type.ERR,
            "Message: " + e.getMessage());
      }

//      String line;
//      BufferedReader input =
//          new BufferedReader(new InputStreamReader(pr.getInputStream()));
//
//      while ((line = input.readLine()) != null) {
//        OutputHandler.print(OutputHandler.Type.INF, line);
//      }

    } else {
      try {
        pr = rt.exec("cmd /c python  -c " +
            "\"from target.classes.decompiler import decompile_apk; " +
            "decompile_apk(\'"
            + FilenameUtils.separatorsToUnix(file.getPath()) + "\',\'"
            + FilenameUtils.separatorsToUnix(dir.getPath()) + "\')\"");
        pr.waitFor();
        decompiled = true;
      } catch (IOException e) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "There was an error executing Androguard. Do you have Python and" +
                " Androguard installed?");
        OutputHandler.print(OutputHandler.Type.ERR,
            "Message: " + e.getMessage());
      }

//      OutputHandler.print(OutputHandler.Type.INF, "Processing...");
    }


//    OutputHandler.print(OutputHandler.Type.INF,
//        "Application is decompiled (exit code " + exitVal + ")");
//    OutputHandler.separator();
  }

  public void findUA() throws IOException {
    if (decompiled) {
      OutputHandler.print(OutputHandler.Type.INF,
          "Looking for user agent...");

      UAFinder uaFinder = new UAFinder(dir, name);
      uaFinder.find();
    } else {
      OutputHandler.print(OutputHandler.Type.ERR,
          "Application was not decompiled yet!");
    }
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public File getDir() {
    return dir;
  }

  public void setDir(File dir) {
    this.dir = dir;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
