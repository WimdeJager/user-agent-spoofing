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

  private boolean decompiled;

  private UAList uas;

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
          + "uaspoof-" + FilenameUtils.removeExtension(name));
    }

    this.decompiled = false;

    this.uas = new UAList();
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
    }
  }

  public void findUAs() throws IOException {
    if (decompiled) {
      UAFinder uaFinder = new UAFinder(dir);
      uas = uaFinder.find();

//      OutputHandler.printList(uas);

    } else {
      OutputHandler.print(OutputHandler.Type.ERR,
          "Application was not decompiled yet!");
    }
  }

  public void classifyUAs() throws IOException, ParseException {
    if (decompiled) {
      for (UserAgent ua : uas.getList()) {
        ua.classify();
      }
    }
  }

}
