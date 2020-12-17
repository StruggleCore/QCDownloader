package qcdownloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Config {
    public static String USERNAME;
    public static String PASSWORD;
    public static String USER_AGENT;
    public static String OUTPUT_DIR;
    public static String OLD_DOWNLOAD_DIR;
    public static LocalDateTime LAST_QUOTA_OVERRUN;

    public static boolean DEBUG;

    public static boolean IsConfigValid () {
        boolean valid = true;
        if (USERNAME == null || USERNAME.equals("")) {
            valid = false;
            System.err.println("Username not specified!");
        }
        if (PASSWORD == null || PASSWORD.equals("")) {
            valid = false;
            System.err.println("Password not specified!");
        }
        if (USER_AGENT == null || USER_AGENT.equals("")) {
            valid = false;
            System.err.println("User agent not specified. It's not recommended to continue without a user agent, you'll get banned!");
        }
        if (OUTPUT_DIR == null || OUTPUT_DIR.equals("")) {
            valid = false;
            System.err.println("Output directory not specified");
        }
        return valid;
    }

    public static void LoadConfig () {
        System.out.println("=== Loading config...");
        try {
            String config = new String(Files.readAllBytes(Paths.get("config.json")));
            Main.GSON_WITH_STATICS.fromJson(config,Config.class);
            return;
        } catch (IOException ex) {} //just ignore any failures and expect validation to fail
    }
    public static void SaveConfig () {
        System.out.println("=== Saving config");
        try {
            Files.write(Paths.get("config.json"), Main.GSON_WITH_STATICS.toJson(new Config()).getBytes());
        } catch (IOException ex) {
            System.err.println("Unable to write out to cache file. Program will continue but note that it will have to get the entire update history every startup.");
        }
    }
}
