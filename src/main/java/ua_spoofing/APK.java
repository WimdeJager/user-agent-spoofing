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
    private File file;
    /**
     * Location of (path to) the decompiled source code. If null, the application
     * has not been decompiled
     */
    private File dir;

    /**
     * Name of the file (without path)
     */
    private String name;

    /**
     * Constructor
     * @param locationAPK the location of the apk file
     */
    public APK(String locationAPK) {
        this.file = new File(locationAPK);
        this.dir  = null;
        this.name = FilenameUtils.getName(file.getPath());
        System.out.println(name);
    }

    /**
     * Decompiles the apk file, using JADX.
     * @throws IOException TODO
     * @throws InterruptedException TODO
     */
    public void decompile() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();

        dir = new File(FilenameUtils.getFullPath(file.getPath())
                + "output");

        System.out.println("[INFO] Apk location: " + file.getPath());
        System.out.println("[INFO] Output location: " + dir.getPath());

        if (dir.exists()) {
            System.out.println("[INFO] output directory already exists! " +
                    "Deleting...");
            FileUtils.deleteDirectory(dir);
        }

        System.out.println("[INFO] Decompiling, starting JADX");
        
        Process pr = rt.exec("cmd /c jadx -d " + dir.getPath() + " "
                + file.getPath());

        String line;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = input.readLine()) != null) {
            System.out.println("[JADX] " + line);
        }

        int exit = pr.waitFor();

        System.out.println("[INFO] Exited with code " + exit);
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

}
