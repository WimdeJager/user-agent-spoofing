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
        this.apkFile = new File(locationAPK);
        this.apkDir = null;
    }

    /**
     * Decompiles the apk file, using JADX.
     * @throws IOException TODO
     * @throws InterruptedException TODO
     */
    public void decompile() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();

        apkDir = new File(FilenameUtils.getFullPath(apkFile.getPath())
                + "output");

        String cmd = "jadx -d " + apkDir.getPath() + " " + apkFile.getPath();

        System.out.println("[INFO] Apk location: " + apkFile.getPath());
        System.out.println("[INFO] Output location: " + apkDir.getPath());

        if (apkDir.exists()) {
            System.out.println("[INFO] output directory already exists! " +
                    "Deleting...");
            FileUtils.deleteDirectory(apkDir);
        }

        System.out.println("[INFO] Decompiling, starting JADX");
        
        Process pr = rt.exec("cmd /c " + cmd);

        String line;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = input.readLine()) != null) {
            System.out.println("[JADX] " + line);
        }

        int exit = pr.waitFor();

        System.out.println("[INFO] Exited with code " + exit);
    }

}
