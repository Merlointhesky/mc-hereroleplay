package com.here.hereroleplay.data;

import com.here.hereroleplay.HereRolePlay;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class StatisticsGenerator {

    private final HereRolePlay plugin;
    private final File statsFile;
    private int taskId = -1;

    public StatisticsGenerator(HereRolePlay plugin) {
        this.plugin = plugin;
        this.statsFile = new File(plugin.getDataFolder(), "statistics.yml");
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("statistics.enabled", true)) {
            return;
        }
        
        long intervalTicks = plugin.getConfig().getInt("statistics.update-interval", 30) * 20L * 60L;
        
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::generateReport, intervalTicks, intervalTicks).getTaskId();
        plugin.getLogger().info("Started YML Statistics Generator task.");
    }
    
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public void generateReport() {
        YamlConfiguration config = new YamlConfiguration();
        
        // We only generate stats for players currently in the cache (online players)
        // For a full server report, we'd query the DB, but this fulfills the simple active player report
        DatabaseManager db = plugin.getDatabaseManager();
        
        Map<UUID, PlayerProfile> cache = db.getProfileCache();
        
        for (Map.Entry<UUID, PlayerProfile> entry : cache.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerProfile profile = entry.getValue();
            
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = player.getName() != null ? player.getName() : uuid.toString();
            
            String path = "players." + name;
            
            // Calculate a rough "Total Level" sum
            int totalLevel = profile.getCombatLevel() + profile.getCollectLevel() + profile.getCraftLevel();
            
            config.set(path + ".uuid", uuid.toString());
            config.set(path + ".total-level", totalLevel);
            config.set(path + ".combat-level", profile.getCombatLevel());
            config.set(path + ".collect-level", profile.getCollectLevel());
            config.set(path + ".craft-level", profile.getCraftLevel());
            
            String classes = String.join(", ", profile.getUnlockedClasses());
            config.set(path + ".classes", classes.isEmpty() ? "None" : classes);
        }

        try {
            config.save(statsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save statistics.yml!");
            e.printStackTrace();
        }
    }
}
