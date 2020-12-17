package qcdownloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import qcdownloader.data.Downloader;
import qcdownloader.data.DownloadsIndex;
import qcdownloader.data.Scene;
import qcdownloader.data.UpdatesIndex;
import qcdownloader.support.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static Gson GSON = new GsonBuilder().create();
    public static Gson GSON_WITH_STATICS = new GsonBuilder().excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter()).create();
    public static void main (String[] args) throws InterruptedException {
        //config
        Config.LoadConfig();
        if (!Config.IsConfigValid()) {
            System.err.println("Configuration is not valid. Try again");
            System.exit(-1);
        }
        //update indexes
        UpdatesIndex.LoadCachedUpdates();

        //display menu
        while (true) {
            DownloadsIndex.ScanDownloadsDirectory();

            System.out.println("===== Main Menu =====");
            if (Downloader.OverranQuota()) {
                System.err.println("Currently within quota cooldown, no downloading is possible.");
                System.err.println("Next download opportunity: "+Config.LAST_QUOTA_OVERRUN.plus(Downloader.LOCKOUT));
            }
            System.out.println("Known Scenes: "+UpdatesIndex.SceneCount()+", Downloaded Scenes: "+ DownloadsIndex.FullyDownloadedSceneCount()+", Percent Downloaded: "+((float)DownloadsIndex.FullyDownloadedSceneCount()/UpdatesIndex.SceneCount())*100+"%");
            System.out.println("1) Refresh known scenes from server");
            System.out.println("2) Rescan download directory");
            System.out.println("3) Download scenes by date");
            System.out.println("4) Download scenes by model");
            System.out.println("5) Download any undownloaded scenes until you hit the daily download limit");
            System.out.println("0) Exit");
            System.out.print("Choose a selection by entering a number.\n> ");
            Scanner scn = new Scanner(System.in);
            switch (scn.nextInt()) {
                case 1:
                    UpdatesIndex.GetUpdates();
                    UpdatesIndex.SaveCachedUpdates();
                    break;
                case 2:
                    DownloadsIndex.ScanDownloadsDirectory();
                    break;
                case 3:
                    int year;
                    do {
                        System.out.println("Available years are: " + Arrays.toString(UpdatesIndex.updates.keySet().toArray()));
                        System.out.print("Please enter a year.\n> ");
                        year = scn.nextInt();
                    } while (!UpdatesIndex.updates.keySet().contains(year));
                    int month;
                    do {
                        System.out.println("Available months are: "+ Arrays.toString(UpdatesIndex.updates.get(year).keySet().toArray()));
                        System.out.print("Please enter a month.\n> ");
                        month = scn.nextInt();
                    } while (!UpdatesIndex.updates.get(year).keySet().contains(month));
                    Object[] scenes = UpdatesIndex.updates.get(year).get(month).toArray();
                    int sceneIndex;
                    do {
                        System.out.println("Available scenes are:");
                        for (int i = 0; i < scenes.length; i++) {
                            System.out.println(i+") "+((Scene)scenes[i]).Title);
                        }
                        System.out.print("Please enter a scene number.\n> ");
                        sceneIndex = scn.nextInt();
                    } while (sceneIndex < 0 || sceneIndex > scenes.length);
                    Downloader.DownloadScene(year,month,(Scene)scenes[sceneIndex]);
                    break;
                case 4:
                    System.out.println("Available models are:");
                    Object[] models = UpdatesIndex.GetAllModels().toArray();
                    for (int i = 0; i < models.length; i++) {
                        System.out.println(i+") "+models[i]);
                    }
                    System.out.println("Please select a model.\n> ");
                    break;
                case 5:
                    Downloader.DownloadAllScenes();
                    break;
                case 0:
                    Config.SaveConfig();
                    System.out.println("Goodbye.");
                    return;
            }
        }
    }
}
