import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        
        Process pr = rt.exec("cmd /c jadx -d output " + locApk);

        String line;
        BufferedReader input =
                new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

        int exit = pr.waitFor();

        System.out.println("Exited with code " + exit);
    }

}
