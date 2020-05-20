package ua_spoofing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Class that holds information about an apk file
 */
public class APK {

    /**
     * Location of (path to) the apk file
     */
    private File apkFile;
    /**
     * Location of (path to) the decompiled source code. If null, the application
     * has not been decompiled
     */
    private File apkDir;

    /**
     * Constructor
     * @param locationAPK the location of the apk file
     */
    public APK(String locationAPK) {
        this.locApk = locationAPK;
        this.locSrc = null;
    }

    /**
     * Decompiles the apk file, using JADX.
     * @throws IOException TODO
     * @throws InterruptedException TODO
     */
    public void decompile() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();

        locSrc = FilenameUtils.getFullPath(locApk) + "\\output";
        System.out.println("Src location: " + locSrc);

        String cmd = "jadx -d " + locSrc + " " + locApk;
        System.out.println("Command: " + cmd);
        if (apkDir.exists()) {
            System.out.println("[INFO] output directory already exists! " +
                    "Deleting...");
            FileUtils.deleteDirectory(apkDir);
        }
        
        Process pr = rt.exec("cmd /c " + cmd);

        String line;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = input.readLine()) != null) {
            System.out.println("[JADX] " + line);
        }

        int exit = pr.waitFor();

        System.out.println("Exited with code " + exit);
    }

}
