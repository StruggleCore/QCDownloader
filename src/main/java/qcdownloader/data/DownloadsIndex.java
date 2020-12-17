package qcdownloader.data;

import qcdownloader.Config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DownloadsIndex {
    private static class Download {
        public Scene scene;
        public ArrayList<String> downloadedPhotos = new ArrayList<>();
        public ArrayList<String> downloadedVideos = new ArrayList<>();
        public boolean FullyDownloaded () {
            return Arrays.equals(scene.GetPhotos(), downloadedPhotos.toArray()) && Arrays.equals(scene.GetVideos(),downloadedVideos.toArray());
        }
    }

    private static ArrayList<Download> downloads = new ArrayList<>();

    public static int FullyDownloadedSceneCount () {
        int count = 0;
        for (Download d : downloads) {
            if (d.FullyDownloaded()) count++;
            else {
                if (Config.DEBUG) System.out.println("not fully downloaded: "+d.scene.PublishDate+" "+d.scene.SafeTitle());
            }
        }
        return count;
    }

    public static void ScanDownloadsDirectory () {
        System.out.println("=== Updating downloaded files index");
        downloads = new ArrayList<>();
        for (Integer y : UpdatesIndex.updates.keySet()) {
            Map<Integer, List<Scene>> year = UpdatesIndex.updates.get(y);
            for (Integer m : year.keySet()) {
                if (Config.DEBUG) System.out.print("Scanning "+y+"-"+m+" scenes: ");
                List<Scene> scenes = year.get(m);
                for (Scene s : scenes) {
                    File folder = Paths.get(Config.OUTPUT_DIR,Integer.toString(y),Integer.toString(m),s.SafeTitle()).toFile();
                    if (folder.exists()) {
                        if (Config.DEBUG) System.out.print(s.SafeTitle()+", ");
                        List<String> files = Arrays.asList(folder.list());
                        Download d = new Download();
                        d.scene = s;
                        //photos
                        for (String f : s.GetPhotos()) {
                            if (files.contains(f)) d.downloadedPhotos.add(f);
                        }
                        //videos
                        for (String f : s.GetVideos()) {
                            if (files.contains(f)) d.downloadedVideos.add(f);
                        }
                        downloads.add(d);
                    }
                }
                if (Config.DEBUG) System.out.println();
            }
        }
        System.out.println("=== Finished updating downloaded files index");
    }
}
