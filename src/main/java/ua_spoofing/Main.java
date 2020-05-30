package ua_spoofing;

import java.io.IOException;

import org.apache.commons.cli.*;

// java -cp target/user-agent-spoofing-1.0.jar ua_spoofing.Main D:\wimde\cs\bscproject\sample_database\com.spotify.music\com.spotify.music.apk

/**
 * Main class.
 */
public class Main {
    public static void main(String[] args)
            throws IOException, InterruptedException, ParseException {
        APK apk;
        Options options = new Options();

        addOptions(options);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.getArgs().length == 1) {
                String location = cmd.getArgs()[0];
                String outputDir;

                if (cmd.hasOption("o")) {
                    outputDir = cmd.getOptionValue("o");
                } else {
                    OutputHandler.print(OutputHandler.Type.INF,
                            "No output directory specified, using default.");
                    outputDir = null;
                }

                apk = new APK(location, outputDir);

                if (cmd.hasOption("j")) {
                    OutputHandler.newline();
                    apk.decompileJADX();
                } else if (cmd.hasOption("a")) {
                    OutputHandler.newline();
                    apk.decompileAG();
                } else {
                    OutputHandler.print(OutputHandler.Type.INF,
                            "No decompilation method specified, using Androguard.");
                    OutputHandler.newline();
                    apk.decompileAG();
                }
            }

            else {
                HelpFormatter f = new HelpFormatter();
                f.printHelp("... APK_FILE [options]", options);
            }

        } catch (ParseException e) {
            OutputHandler.print(OutputHandler.Type.ERR,
                    "Parsing failed!" + e.getMessage());
        }
    }

    private static void addOptions(Options options) {
        options.addOption("o", true, "Output directory")
                .addOption("a", false, "Use Androguard for decompilation")
                .addOption("j", false, "Use JADX for decompilation");
    }
}
