package qcdownloader.data;

import com.google.gson.reflect.TypeToken;
import qcdownloader.Config;
import qcdownloader.Main;
import qcdownloader.net.Network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class UpdatesIndex {
    public static TreeMap<Integer,TreeMap<Integer, List<Scene>>> updates = new TreeMap<>();

    public static int SceneCount () {
        int count = 0;
        for (Map<Integer,List<Scene>> y : updates.values()) {
            for (List<Scene> m : y.values()) {
                count += m.size();
            }
        }
        return count;
    }
    public static List<Scene> GetAllScenes () {
        ArrayList<Scene> s = new ArrayList<>();
        for (Map<Integer,List<Scene>> y : updates.values()) {
            for (List<Scene> m : y.values()) {
                s.addAll(m);
            }
        }
        return s;
    }
    public static LinkedHashSet<String> GetAllModels () {
        LinkedHashSet<String> m = new LinkedHashSet<>();
        for (Scene s : GetAllScenes()) {
            for (String model : s.Models.split(";")) {
                m.add(model);
            }
        }
        return m;
    }

    public static void LoadCachedUpdates () throws InterruptedException {
        try {
            String cache = new String(Files.readAllBytes(Paths.get("cache.json")));
            updates = Main.GSON.fromJson(cache,new TypeToken<TreeMap<Integer,TreeMap<Integer, List<Scene>>>>(){}.getType());
            System.out.println("=== Using cached scenes.");
            return;
        } catch (IOException ex) {} //just ignore any failures and get the new updates
        GetUpdates();
        SaveCachedUpdates();
    }
    public static void SaveCachedUpdates () {
        try {
            Files.write(Paths.get("cache.json"), Main.GSON.toJson(updates, updates.getClass()).getBytes());
        } catch (IOException ex) {
            System.err.println("Unable to write out to cache file. Program will continue but note that it will have to get the entire update history every startup.");
        }
    }
    public static void GetUpdates () throws InterruptedException {
        System.out.println("=== Updating known scenes...");
        Random r = new Random();

        LocalDate start = LocalDate.of(2004,1,1);
        LocalDate stop = LocalDate.now();
        long years = ChronoUnit.YEARS.between(start,stop);
        for (int y = 2004; y <= 2004 + years; y++) {
            if (Config.DEBUG) System.out.println("Getting months-with-updates for "+y);
            updates.put(y,new TreeMap<>());
            int[] months = Main.GSON.fromJson(Network.GetURL("GetMonthsWithUpdates",Integer.toString(y)),int[].class);
            for (int m : months) {
                if (Config.DEBUG) System.out.println("Getting scenes for "+y+"-"+m);
                Thread.sleep((long)r.nextFloat()*2000);
                Scene[] scenes = Main.GSON.fromJson(Network.GetURL("GetUpdatesByDate",Integer.toString(y),Integer.toString(m)),Scene[].class);
                updates.get(y).put(m, Arrays.asList(scenes));
            }
        }

        System.out.println("=== Finished updating known scenes.");
    }
}
