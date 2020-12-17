package qcdownloader.data;

import qcdownloader.Config;
import qcdownloader.net.Network;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Downloader {
    private static final String ENDPOINT = "https://www.qualitycontrol.cc/updatefiles/";
    public static final Duration LOCKOUT = Duration.ofHours(12);
    public static String BuildURL (String folder, String file, boolean isPhoto) {
        return ENDPOINT + folder + (isPhoto? "/photos/" : "/videos/") + file;
    }

    public static boolean OverranQuota() {
        if (Config.LAST_QUOTA_OVERRUN == null) return false;
        return LocalDateTime.now().minus(LOCKOUT).isBefore(Config.LAST_QUOTA_OVERRUN);
    }

    public static void DownloadFile (Integer year, Integer month, Scene scene, String file, boolean isPhoto) throws IOException {
        //check if file is real file in scene
        if (!Arrays.asList(scene.GetPhotos()).contains(file) && !Arrays.asList(scene.GetVideos()).contains(file)) {
            if (Config.DEBUG) System.out.println(file+" was not a real file in scene, skipping");
            return;
        }
        if (file.equals("false")) {
            return;
        }
        //check if exceeded quota
        if (OverranQuota()) return;
        //create directories
        File dir = Paths.get(Config.OUTPUT_DIR,year.toString(),month.toString(),scene.SafeTitle()).toFile();
        dir.mkdirs();
        File put = Paths.get(dir.getPath(),file).toFile();

        System.out.print("Downloading /"+year+"/"+month+"/"+scene.SafeTitle()+"/"+file+"... ");
        //download file
        Network.DownloadFile(BuildURL(scene.Folder,file,isPhoto),put);
        if (put.length()/1024 == 1) {
            System.err.println("=== Exceeded the quota for today! Try again in about 12 hours.");
            Config.LAST_QUOTA_OVERRUN = LocalDateTime.now();
            put.delete();
        }
        System.out.println("Downloaded "+put.length()/1024+"kb file.");
    }

    public static void DownloadScene (Integer year, Integer month, Scene scene) {
        if (OverranQuota()) return;
        //make directories
        File dir = Paths.get(Config.OUTPUT_DIR,year.toString(),month.toString(),scene.SafeTitle()).toFile();
        dir.mkdirs();
        //download/move files
        try {
            for (String file : scene.GetPhotos()) { //photos
                if (Paths.get(dir.getPath(),file).toFile().exists()) continue; //already downloaded this file
                if (FileExistsInOldDir(file)) {
                    System.out.println("Found file in old folder, moving: "+file);
                    Files.move(Paths.get(Config.OLD_DOWNLOAD_DIR, file), Paths.get(dir.getPath(), file));
                    continue;
                }
                DownloadFile(year,month,scene,file,true);
            }
            for (String file : scene.GetVideos()) { //videos
                if (Paths.get(dir.getPath(),file).toFile().exists()) continue; //already downloaded this file
                if (FileExistsInOldDir(file)) {
                    System.out.println("Found file in old folder, moving: "+file);
                    Files.move(Paths.get(Config.OLD_DOWNLOAD_DIR, file), Paths.get(dir.getPath(), file));
                    continue;
                }
                DownloadFile(year,month,scene,file,false);
            }
        } catch (IOException ex) {
            System.err.println("Error writing file");
            ex.printStackTrace();
        }
    }
    public static void DownloadAllScenes () {
        for (Integer year : UpdatesIndex.updates.keySet()) {
            for (Integer month : UpdatesIndex.updates.get(year).keySet()) {
                for (Scene scene : UpdatesIndex.updates.get(year).get(month)) {
                    DownloadScene(year,month,scene);
                }
            }
        }
    }
    private static boolean FileExistsInOldDir (String name) {
        return Paths.get(Config.OLD_DOWNLOAD_DIR,name).toFile().exists();
    }
}
