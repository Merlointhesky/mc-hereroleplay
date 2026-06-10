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
        
        // Run first report after 5 seconds, then repeat on interval
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::generateReport, 20L * 5L, intervalTicks).getTaskId();
        plugin.getLogger().info("Started YML Statistics Generator task.");
    }
    
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        // Force a final synchronous report generation on shutdown
        generateReport();
    }

    public void generateReport() {
        YamlConfiguration config = new YamlConfiguration();
        DatabaseManager db = plugin.getDatabaseManager();
        
        // Fetch all offline/database records
        java.util.List<PlayerProfile> profiles = db.getAllProfiles();
        Map<UUID, PlayerProfile> cache = db.getProfileCache();
        
        for (PlayerProfile profile : profiles) {
            UUID uuid = profile.getUuid();
            // Use active cached profile if player is currently online
            if (cache.containsKey(uuid)) {
                profile = cache.get(uuid);
            }
            
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = player.getName() != null ? player.getName() : uuid.toString();
            
            String path = "players." + name;
            int totalLevel = profile.getCombatLevel() + profile.getCollectLevel() + profile.getCraftLevel();
            
            config.set(path + ".uuid", uuid.toString());
            config.set(path + ".total-level", totalLevel);
            config.set(path + ".combat-level", profile.getCombatLevel());
            config.set(path + ".collect-level", profile.getCollectLevel());
            config.set(path + ".craft-level", profile.getCraftLevel());
            config.set(path + ".strength", profile.getStrengthPoints());
            config.set(path + ".agility", profile.getAgilityPoints());
            config.set(path + ".vitality", profile.getVitalityPoints());
            config.set(path + ".intelligence", profile.getIntelligencePoints());
            
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
