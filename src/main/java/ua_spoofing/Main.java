package ua_spoofing;

import java.io.IOException;

/**
 * Main class.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        APK apk = new APK(args[0]);

        apk.decompile();
    }
}
