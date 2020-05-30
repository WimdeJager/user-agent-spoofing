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

        if (outputDir != null) {
            this.dir  = new File(outputDir);
        } else {
            this.dir = new File(this.file.getParent() + "\\"
                    + FilenameUtils.removeExtension(name));
        }
    }

    /**
     * Decompiles the APK file, using JADX.
     * @throws IOException TODO
     * @throws InterruptedException TODO
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

        Process pr = rt.exec("cmd /c jadx -d " + dir.getPath() + " "
                + file.getPath());

        String line;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = input.readLine()) != null) {
            OutputHandler.print(OutputHandler.Type.EXT, line);
        }

        int exit = pr.waitFor();

        OutputHandler.print(OutputHandler.Type.INF,
                "Exited with code " + exit);
    }

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

        String line;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = input.readLine()) != null) {
            OutputHandler.print(OutputHandler.Type.EXT, line);
        }

        int exit = pr.waitFor();

    public void findUA() throws IOException {
        OutputHandler.print(OutputHandler.Type.INF,
                "Looking for user agent...");

        UAFinder uaFinder = new UAFinder(dir);
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
