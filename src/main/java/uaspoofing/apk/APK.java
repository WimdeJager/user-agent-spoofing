package uaspoofing.apk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import uaspoofing.output.OutputHandler;
import uaspoofing.ua.UAFinder;

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
   * The list of User Agents found in this APK. Might be empty if findUAs()
   * has not been called yet, or if no UAs could be found in this APK.
   */
  private UAList uas;

  /**
   * Constructor
   *
   * @param file      the location of the APK file
   * @param outputDir location the decompiled files should be placed (null if
   *                  not specified by user, or if in directory mode).
   */
  public APK(File file, File outputDir) {
    this.file = file;
    if (!file.exists() || file.isDirectory()) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "The APK file does not exist!");
      System.exit(1);
    }

    // initialize outputDir
    if (outputDir != null) {
      // if not null, outputDir was specified by user
      this.dir = outputDir;
    } else {
      // else, give default name: 'uaspoof-<APK name>'
      this.dir = new File(this.file.getParent() + "\\"
          + "uaspoof-"
          + FilenameUtils.removeExtension(FilenameUtils.getName(file.getPath())));
    }

    this.uas = new UAList();
  }


  /**
   * Decompiles the APK file using JADX.
   */
  public void decompile() {
    if (dir.exists()) {
      // if output directory exists, clean directory
      try {
        FileUtils.cleanDirectory(dir);
      } catch (IOException e) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "There was an error cleaning the output directory "
                + dir.getPath() + ", there might be another program using the" +
                " directory.");
        e.printStackTrace();
        return;
      }
    } else {
      // dir does not exist, create directory
      if (!dir.mkdirs()) {
        OutputHandler.print(OutputHandler.Type.ERR,
            "Could not create directory " + dir.getPath());
        return;
      }
    }

    OutputHandler.print(OutputHandler.Type.INF,
        "Scanning " + file.getName() + " ...");

    try {
      // execute JADX
      Runtime rt = Runtime.getRuntime();
      Process pr = rt.exec(
          "cmd /c target\\classes\\jadx-1.1.0\\bin\\jadx -r -ds "
              + dir.getPath() + " " + file.getPath());
      pr.waitFor(); // wait for JADX to return
    } catch (IOException e) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "There was an error during the decompilation process");
      e.printStackTrace();
    } catch (InterruptedException e) {
      OutputHandler.print(OutputHandler.Type.ERR,
          "There was an error during the decompilation process");
      e.printStackTrace();
    }
  }

  /**
   * Creates ```UAFinder``` for a directory, and stores the result in ```uas```
   */
  public void findUAs() {
    UAFinder uaFinder = new UAFinder(dir);
    uas = uaFinder.find();

    OutputHandler.print(OutputHandler.Type.INF,
        "There were " + uas.size() + " User-Agents found.");
  }

  /**
   * Classify each UA in ```uas```
   */
  public void classifyUAs() {
    uas.classifyAll();
  }

}
