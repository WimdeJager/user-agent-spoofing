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
     * @param location the location of the APK file
     */
    public APK(String location, String outputDir) {
        this.file = new File(location);
        this.dir  = new File(outputDir);
        this.name = FilenameUtils.getName(file.getPath());
        System.out.println(name);
    }

    /**
     * Decompiles the APK file, using JADX.
     * @throws IOException TODO
     * @throws InterruptedException TODO
     */
    public void decompileJADX() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();

        System.out.println("[INFO] APK location: " + file.getPath());
        System.out.println("[INFO] Output location: " + dir.getPath());

        if (dir.exists()) {
            System.out.println("[INFO] output directory already exists! " +
                    "Cleaning...");
            FileUtils.cleanDirectory(dir);
        }

        System.out.println("[INFO] Decompiling, starting JADX");
        System.out.println("[INFO] This may take a while, depending on the size" +
                " of the APK");

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

    public void decompileAG() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();

        System.out.println("[INFO] APK location: " + file.getPath());
        System.out.println("[INFO] Output location: " + dir.getPath());

        if (dir.exists()) {
            System.out.println("[INFO] output directory already exists! " +
                    "Cleaning...");
            FileUtils.deleteDirectory(dir);
        } else {
            if (!dir.mkdirs()) {
                System.out.println("[ERROR] Could not create directory " + dir.getPath());
                System.exit(1);
            }
        }

        System.out.println("[INFO] Decompiling, starting AndroGuard");
        System.out.println("[INFO] This may take a while, depending on the size" +
                " of the APK");

        String cmd = "cmd /c python  -c " +
                "\"from target.classes.decompiler import decompile_apk; " +
                "decompile_apk(\'"
                + FilenameUtils.separatorsToUnix(file.getPath()) + "\',\'"
                + FilenameUtils.separatorsToUnix(dir.getPath()) + "\')\"";

        System.out.println("COMMAND: " + cmd);

        Process pr = rt.exec(cmd);

        String line;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = input.readLine()) != null) {
            System.out.println("[ANDROG] " + line);
        }

        int exit = pr.waitFor();

        System.out.println("[INFO] Exited with code " + exit);

//        PythonInterpreter python = new PythonInterpreter();
//        python.exec("import sys\n" +
//                "sys.path.append('target/classes/')\n" +
//                "sys.path.append('C:/Users/" + System.getProperty("user.name") + "/AppData/Local/Programs/Python/Python38/Lib/site-packages')\n" +
//                "from decompiler import decompile_apk");
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.dir = null;
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
