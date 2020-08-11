package uaspoofing.apk;

import com.blueconic.browscap.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import uaspoofing.output.OutputHandler;
import uaspoofing.ua.UAFinder;
import uaspoofing.ua.UserAgent;

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

  /**
   * True if the APK has been decompiled, false otherwise. If the APK was
   * decompiled, the contents can be found in the File dir.
   */
  private boolean decompiled;

  /**
   * The list of User Agents found in this APK. Might be empty if findUAs()
   * has not been called yet, or if no UAs could be found in this APK.
   */
  private UAList uas;

  /**
   * Constructor
   *
   * @param file      the location of the APK file
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
          + "uaspoof-" + FilenameUtils.removeExtension(name));
    }

    this.decompiled = false;

    this.uas = new UAList();
  }


  /**
   * Decompiles the APK file
   */
  public void decompile() {
    Runtime rt = Runtime.getRuntime();

    if (dir.exists()) {
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

    OutputHandler.print(OutputHandler.Type.INF,
        "Scanning " + file.getName() + " ...");

    Process pr;
    try {
      pr = rt.exec("cmd /c target\\classes\\jadx-1.1.0\\bin\\jadx -r -ds " + dir.getPath
          () + " "
          + file.getPath());
      pr.waitFor();
      decompiled = true;
    } catch (IOException e) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "There was an error executing JADX. Do you have JADX installed " +
              "and did you add it to your PATH variable?");
      e.printStackTrace();
    } catch (InterruptedException e) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "The decompilation process got interrupted!");
      e.printStackTrace();
    }
  }

  public void findUAs() {
    if (decompiled) {
      UAFinder uaFinder = new UAFinder(dir);
      uas = uaFinder.find();

      OutputHandler.print(OutputHandler.Type.INF,
          "There were " + uas.size() + " User-Agents found.");
    } else {
      OutputHandler.print(OutputHandler.Type.ERR,
          "Application was not decompiled yet!");
    }
  }

  public void classifyUAs() {
    if (decompiled) {
      for (UserAgent ua : uas.getList()) {
        ua.classify();
      }
    }
  }

}
