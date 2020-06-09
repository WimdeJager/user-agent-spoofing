package ua_spoofing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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

  /**
   * Constructor
   * @param location the location of the APK file
   * @param outputDir location the decompiled files should be placed (null if
   *                 not specified by user).
   */
  public APK(String location, String outputDir) {
    this.file = new File(location);
    if (!file.exists() || file.isDirectory()) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "The APK file does not exist!");
      System.exit(1);
    }

    this.name = FilenameUtils.getName(file.getPath());
     if (outputDir != null) {
       this.dir  = new File(outputDir);
     } else {
       this.dir = new File(this.file.getParent() + "\\"
           + FilenameUtils.removeExtension(name));
     }
  }

  /**
   * Decompiles the APK file, using JADX.
   * @throws IOException when output directory could not be cleaned, or when execution
   *                     of JADX process failed
   * @throws InterruptedException when JADX process got interrupted
   */
  public void decompileJADX() throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();

    OutputHandler.print(OutputHandler.Type.INF,
        "APK location: " + file.getPath());
    OutputHandler.print(OutputHandler.Type.INF,
        "Output location: " + dir.getPath());


    if (dir.exists()) {
      OutputHandler.print(OutputHandler.Type.INF,
          "Output directory already exists! Deleting...");
      FileUtils.deleteDirectory(dir);
    }

    OutputHandler.newline();

    OutputHandler.print(OutputHandler.Type.INF,
        "Decompiling, starting JADX...");
    OutputHandler.print(OutputHandler.Type.INF,
        "(this may take a while, depending on the size of the APK)");

    Process pr = rt.exec("cmd /c jadx -r -ds " + dir.getPath() + " "
        + file.getPath());

    String line;
    BufferedReader input =
        new BufferedReader(new InputStreamReader(pr.getInputStream()));

    while ((line = input.readLine()) != null) {
      OutputHandler.print(OutputHandler.Type.INF, line);
    }

    int exitVal = pr.waitFor();

    OutputHandler.print(OutputHandler.Type.INF,
        "Application is decompiled (exit code " + exitVal + ")");
    OutputHandler.newline();
  }

  /**
   * Decompiles the APK file, using Androguard
   * @throws IOException when output directory could not be cleaned, or when
   * execution of Androguard process failed
   * @throws InterruptedException when Androguard process got interrupted
   */
  public void decompileAG() throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();

    OutputHandler.print(OutputHandler.Type.INF,
        "APK location: " + file.getPath());
    OutputHandler.print(OutputHandler.Type.INF,
        "Output location: " + dir.getPath());

    if (dir.exists()) {
      OutputHandler.print(OutputHandler.Type.INF,
          "Output directory already exists! Cleaning...");
      FileUtils.cleanDirectory(dir);
    } else {
      if (!dir.mkdirs()) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "Could not create directory " + dir.getPath());
        System.exit(1);
      }
    }

    OutputHandler.newline();

    OutputHandler.print(OutputHandler.Type.INF,
        "Decompiling, starting Androguard...");
    OutputHandler.print(OutputHandler.Type.INF,
        "(this may take a while, depending on the size of the APK)");

    Process pr = rt.exec("cmd /c python  -c " +
        "\"from target.classes.decompiler import decompile_apk; " +
        "decompile_apk(\'"
        + FilenameUtils.separatorsToUnix(file.getPath()) + "\',\'"
        + FilenameUtils.separatorsToUnix(dir.getPath()) + "\')\"");

    OutputHandler.print(OutputHandler.Type.INF, "Processing...");

    int exitVal = pr.waitFor();

    OutputHandler.print(OutputHandler.Type.INF,
        "Application is decompiled (exit code " + exitVal + ")");
    OutputHandler.newline();
  }

  public void findUA() throws IOException {
    OutputHandler.print(OutputHandler.Type.INF,
        "Looking for user agent...");

    UAFinder uaFinder = new UAFinder(dir, name);
    uaFinder.find();
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
