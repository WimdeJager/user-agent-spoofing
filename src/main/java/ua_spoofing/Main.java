package ua_spoofing;

import java.io.IOException;

// java -cp target/user-agent-spoofing-1.0.jar ua_spoofing.Main D:\wimde\cs\bscproject\sample_database\com.spotify.music\com.spotify.music.apk

/**
 * Main class.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            System.err.println("ERROR --- Provide an APK file!");
        } else if (args.length > 1) {
            System.err.println("ERROR --- More than one argument given");
        }

        APK apk = new APK(args[0]);

        apk.decompile();
    }
}
