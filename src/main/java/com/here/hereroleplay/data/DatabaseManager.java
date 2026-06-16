package com.here.hereroleplay.data;

import com.here.hereroleplay.HereRolePlay;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {

    private final HereRolePlay plugin;
    private Connection connection;
    private final Map<UUID, PlayerProfile> profileCache = new ConcurrentHashMap<>();

    public DatabaseManager(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        String fileName = plugin.getConfig().getString("database.file", "database.db");
        File databaseFile = new File(dataFolder, fileName);

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            plugin.getLogger().info("Connected to SQLite database.");
            createTables();
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("Failed to connect to the database!");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                // Save all cached profiles before closing
                for (PlayerProfile profile : profileCache.values()) {
                    saveProfileSync(profile);
                }
                connection.close();
                plugin.getLogger().info("Disconnected from SQLite database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createProfilesTable = "CREATE TABLE IF NOT EXISTS hrp_profiles (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "combatLevel INT DEFAULT 1, " +
                "collectLevel INT DEFAULT 1, " +
                "craftLevel INT DEFAULT 1, " +
                "combatXp DOUBLE DEFAULT 0, " +
                "collectXp DOUBLE DEFAULT 0, " +
                "craftXp DOUBLE DEFAULT 0, " +
                "strengthPoints INT DEFAULT 0, " +
                "agilityPoints INT DEFAULT 0, " +
                "vitalityPoints INT DEFAULT 0, " +
                "intelligencePoints INT DEFAULT 0, " +
                "unspentSkillPoints INT DEFAULT 0, " +
                "unlockedClasses TEXT DEFAULT '', " +
                "skillLevels TEXT DEFAULT ''" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createProfilesTable);
            try {
                statement.execute("ALTER TABLE hrp_profiles ADD COLUMN skillLevels TEXT DEFAULT '';");
            } catch (SQLException e) {
                // Column might already exist, which is fine
            }
        }
    }

    public void loadProfile(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerProfile profile = loadProfileSync(uuid);
            profileCache.put(uuid, profile);
        });
    }

    public PlayerProfile loadProfileSync(UUID uuid) {
        PlayerProfile profile = new PlayerProfile(uuid);
        String query = "SELECT * FROM hrp_profiles WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile.setCombatLevel(rs.getInt("combatLevel"));
                    profile.setCollectLevel(rs.getInt("collectLevel"));
                    profile.setCraftLevel(rs.getInt("craftLevel"));
                    
                    profile.setCombatXp(rs.getDouble("combatXp"));
                    profile.setCollectXp(rs.getDouble("collectXp"));
                    profile.setCraftXp(rs.getDouble("craftXp"));
                    
                    profile.setStrengthPoints(rs.getInt("strengthPoints"));
                    profile.setAgilityPoints(rs.getInt("agilityPoints"));
                    profile.setVitalityPoints(rs.getInt("vitalityPoints"));
                    profile.setIntelligencePoints(rs.getInt("intelligencePoints"));
                    profile.setUnspentSkillPoints(rs.getInt("unspentSkillPoints"));
                    
                    String classesStr = rs.getString("unlockedClasses");
                    if (classesStr != null && !classesStr.isEmpty()) {
                        profile.getUnlockedClasses().addAll(Arrays.asList(classesStr.split(",")));
                    }
                    
                    String skillLevelsStr = rs.getString("skillLevels");
                    deserializeSkillLevels(skillLevelsStr, profile);
                } else {
                    // Create new record
                    saveProfileSync(profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public void saveProfile(PlayerProfile profile) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveProfileSync(profile));
    }

    private void saveProfileSync(PlayerProfile profile) {
        String query = "INSERT OR REPLACE INTO hrp_profiles " +
                "(uuid, combatLevel, collectLevel, craftLevel, combatXp, collectXp, craftXp, " +
                "strengthPoints, agilityPoints, vitalityPoints, intelligencePoints, unspentSkillPoints, unlockedClasses, skillLevels) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, profile.getUuid().toString());
            stmt.setInt(2, profile.getCombatLevel());
            stmt.setInt(3, profile.getCollectLevel());
            stmt.setInt(4, profile.getCraftLevel());
            stmt.setDouble(5, profile.getCombatXp());
            stmt.setDouble(6, profile.getCollectXp());
            stmt.setDouble(7, profile.getCraftXp());
            stmt.setInt(8, profile.getStrengthPoints());
            stmt.setInt(9, profile.getAgilityPoints());
            stmt.setInt(10, profile.getVitalityPoints());
            stmt.setInt(11, profile.getIntelligencePoints());
            stmt.setInt(12, profile.getUnspentSkillPoints());
            stmt.setString(13, String.join(",", profile.getUnlockedClasses()));
            stmt.setString(14, serializeSkillLevels(profile.getSkillLevels()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerProfile getProfile(UUID uuid) {
        return profileCache.get(uuid);
    }

    public java.util.List<PlayerProfile> getAllProfiles() {
        java.util.List<PlayerProfile> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM hrp_profiles";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                PlayerProfile profile = new PlayerProfile(uuid);
                profile.setCombatLevel(rs.getInt("combatLevel"));
                profile.setCollectLevel(rs.getInt("collectLevel"));
                profile.setCraftLevel(rs.getInt("craftLevel"));
                profile.setCombatXp(rs.getDouble("combatXp"));
                profile.setCollectXp(rs.getDouble("collectXp"));
                profile.setCraftXp(rs.getDouble("craftXp"));
                profile.setStrengthPoints(rs.getInt("strengthPoints"));
                profile.setAgilityPoints(rs.getInt("agilityPoints"));
                profile.setVitalityPoints(rs.getInt("vitalityPoints"));
                profile.setIntelligencePoints(rs.getInt("intelligencePoints"));
                profile.setUnspentSkillPoints(rs.getInt("unspentSkillPoints"));
                
                String classesStr = rs.getString("unlockedClasses");
                if (classesStr != null && !classesStr.isEmpty()) {
                    profile.getUnlockedClasses().addAll(java.util.Arrays.asList(classesStr.split(",")));
                }
                
                String skillLevelsStr = rs.getString("skillLevels");
                deserializeSkillLevels(skillLevelsStr, profile);
                
                list.add(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String serializeSkillLevels(java.util.Map<String, Integer> map) {
        if (map == null || map.isEmpty()) return "";
        java.util.List<String> list = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(entry.getKey() + ":" + entry.getValue());
        }
        return String.join(",", list);
    }

    private void deserializeSkillLevels(String str, PlayerProfile profile) {
        if (str == null || str.isEmpty()) return;
        String[] parts = str.split(",");
        for (String part : parts) {
            String[] kv = part.split(":");
            if (kv.length == 2) {
                try {
                    profile.setSkillLevel(kv[0], Integer.parseInt(kv[1]));
                } catch (NumberFormatException e) {
                    // Ignore malformed entries
                }
            }
        }
    }
    
    public void unloadProfile(UUID uuid) {
        PlayerProfile profile = profileCache.remove(uuid);
        if (profile != null) {
            saveProfile(profile);
        }
    }
    
    public Map<UUID, PlayerProfile> getProfileCache() {
        return profileCache;
    }
}
