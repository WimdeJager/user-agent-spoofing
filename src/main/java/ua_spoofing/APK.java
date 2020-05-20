package ua_spoofing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;

/**
 * Class that holds information about an apk file
 */
public class APK {

    private String locApk;
    private String locSrc;

    public APK(String locationAPK) {
        this.locApk = locationAPK;
        this.locSrc = null;
    }

    public void decompile() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();

        locSrc = FilenameUtils.getFullPath(locApk) + "\\output";
        System.out.println("Src location: " + locSrc);

        String cmd = "jadx -d " + locSrc + " " + locApk;
        System.out.println("Command: " + cmd);
        
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
