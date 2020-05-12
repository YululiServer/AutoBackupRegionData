package xyz.acrylicstyle.backup;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AutoBackupRegionData extends JavaPlugin implements Listener {
    public static ConfigProvider config = null;
    public static int period = 24; // hours
    public static int keepFiles = 14;

    @Override
    public void onEnable() {
        config = new ConfigProvider("./plugins/AutoBackupRegionData/config.yml");
        period = config.getInt("delayHour", 24); // 1 day
        keepFiles = config.getInt("keepFiles", 14); // 2 weeks
        Bukkit.getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void run() {
                Log.info("regionデータをバックアップしています...");
                File folder = new File("./backupregiondata");
                if (folder.listFiles() != null) {
                    CollectionList<File> files = ICollectionList.asList(folder.listFiles());
                    files.sort(Comparator.comparingLong(File::lastModified));
                    files.sort(Comparator.reverseOrder());
                    files.foreach((file, i) -> {
                        if (i > keepFiles) {
                            Log.info("Deleting " + file.getAbsolutePath());
                            try {
                                FileUtils.deleteDirectory(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                File src = new File("./world/region");
                File dest = new File("./backupregiondata/" + new Date().getTime() + "/");
                dest.mkdirs();
                try {
                    FileUtils.copyDirectoryToDirectory(src, dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.info("regionデータのバックアップが完了しました。");
            }
        }.runTaskTimerAsynchronously(this, period * 60 * 60 * 20, period * 60 * 60 * 20);
    }
}
