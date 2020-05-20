package ua_spoofing;

import java.io.IOException;

/**
 * Created by wimde on 18-5-2020.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        APK apk = new APK(args[0]);

        apk.decompile();
    }
}
