package com.here.hereroleplay.api;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HereRolePlayAPI {

    private static HereRolePlay plugin;

    public static void init(HereRolePlay instance) {
        plugin = instance;
    }

    /**
     * Retrieves the cached profile for a player.
     * @param uuid The UUID of the player.
     * @return The PlayerProfile, or null if not loaded.
     */
    public static PlayerProfile getProfile(UUID uuid) {
        if (plugin == null) return null;
        return plugin.getDatabaseManager().getProfile(uuid);
    }
    
    /**
     * Forces a profile to be loaded from the database (Async).
     */
    public static void loadProfileAsync(UUID uuid) {
        if (plugin != null) {
            plugin.getDatabaseManager().loadProfile(uuid);
        }
    }
    
    /**
     * Forces a profile to be saved to the database (Async).
     */
    public static void saveProfileAsync(PlayerProfile profile) {
        if (plugin != null) {
            plugin.getDatabaseManager().saveProfile(profile);
        }
    }
    
    /**
     * Gets a player's total level across all 3 pillars.
     */
    public static int getTotalLevel(Player player) {
        PlayerProfile profile = getProfile(player.getUniqueId());
        if (profile == null) return 0;
        return profile.getCombatLevel() + profile.getCollectLevel() + profile.getCraftLevel();
    }

    /**
     * Gives Combat XP to a player and handles leveling up.
     */
    public static void giveCombatXp(Player player, double amount) {
        if (plugin != null) {
            plugin.getXpManager().addCombatXp(player, amount);
        }
    }

    /**
     * Gives Collect XP to a player and handles leveling up.
     */
    public static void giveCollectXp(Player player, double amount) {
        if (plugin != null) {
            plugin.getXpManager().addCollectXp(player, amount);
        }
    }

    /**
     * Gives Craft XP to a player and handles leveling up.
     */
    public static void giveCraftXp(Player player, double amount) {
        if (plugin != null) {
            plugin.getXpManager().addCraftXp(player, amount);
        }
    }
}
