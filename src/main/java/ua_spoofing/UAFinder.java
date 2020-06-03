package ua_spoofing;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Tries to find a User Agent in the .java files that are present in the given (underlying)
 * directory(ies).
 */
public class UAFinder {

    private File dir;
    private String apkName;

    public UAFinder(File dir, String apkName) {
        this.dir     = dir;
        this.apkName = apkName;
    }

    public void find() throws IOException {
        _find(dir);
    }

    private void _find(File d) throws IOException {
//        OutputHandler.print(OutputHandler.Type.INF,
//                "Looking in dir " + d.getPath());
        File[] files = d.listFiles(); // list files/directories in directory
        for (File f : files) {
            if (f.isDirectory()) {
                if (apkName.contains(f.getName()))
                    _find(f);
            } else {
//                OutputHandler.print(OutputHandler.Type.INF,
//                        "Processing file " + f.getName() + " ...");
                String fString = FileUtils.readFileToString(f, Charset.defaultCharset());
                if (fString.contains("setHeader")
                        || fString.contains("setParameter")
                        || fString.contains("setUserAgentString")
//                        || fString.contains("ua")
                        || fString.contains("User-Agent")
                        || fString.contains("useragent")) {
                    OutputHandler.print(OutputHandler.Type.INF,
                            "Possible UA found in file " + f.getPath());
                }
            }
        }
    }

}
