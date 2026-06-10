package com.here.hereroleplay.skills;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class XpManager {

    private final HereRolePlay plugin;
    private final double baseModifier;
    private final double scalingCoefficient;

    public XpManager(HereRolePlay plugin) {
        this.plugin = plugin;
        this.baseModifier = plugin.getConfig().getDouble("xp.base-modifier", 100.0);
        this.scalingCoefficient = plugin.getConfig().getDouble("xp.scaling-coefficient", 1.5);
    }

    /**
     * Calculates the total XP required to reach the given level.
     * Formula: X_req = B * (L - 1)^C
     */
    public double getXpRequiredForLevel(int level) {
        if (level <= 1) return 0;
        return baseModifier * Math.pow(level - 1, scalingCoefficient);
    }

    public void addCombatXp(Player player, double amount) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        profile.setCombatXp(profile.getCombatXp() + amount);
        plugin.getBossBarManager().showXpGain(player, "Combat", amount, profile.getCombatXp(), profile.getCombatLevel());
        checkLevelUp(player, profile, "Combat", profile.getCombatLevel(), profile.getCombatXp());
    }

    public void addCollectXp(Player player, double amount) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        profile.setCollectXp(profile.getCollectXp() + amount);
        plugin.getBossBarManager().showXpGain(player, "Collect", amount, profile.getCollectXp(), profile.getCollectLevel());
        checkLevelUp(player, profile, "Collect", profile.getCollectLevel(), profile.getCollectXp());
    }

    public void addCraftXp(Player player, double amount) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        profile.setCraftXp(profile.getCraftXp() + amount);
        plugin.getBossBarManager().showXpGain(player, "Craft", amount, profile.getCraftXp(), profile.getCraftLevel());
        checkLevelUp(player, profile, "Craft", profile.getCraftLevel(), profile.getCraftXp());
    }

    private void checkLevelUp(Player player, PlayerProfile profile, String pillar, int currentLevel, double currentXp) {
        int nextLevel = currentLevel + 1;
        double xpRequired = getXpRequiredForLevel(nextLevel);

        boolean leveledUp = false;

        while (currentXp >= xpRequired) {
            currentLevel++;
            leveledUp = true;
            profile.setUnspentSkillPoints(profile.getUnspentSkillPoints() + 1); // Grant 1 skill point per level
            nextLevel = currentLevel + 1;
            xpRequired = getXpRequiredForLevel(nextLevel);
        }

        if (leveledUp) {
            // Update profile
            switch (pillar.toLowerCase()) {
                case "combat": profile.setCombatLevel(currentLevel); break;
                case "collect": profile.setCollectLevel(currentLevel); break;
                case "craft": profile.setCraftLevel(currentLevel); break;
            }

            // Notify player
            String messagePath = "level-up." + pillar.toLowerCase();
            String msg = plugin.getConfig().getString("messages." + messagePath);
            if (msg == null) {
                // fallback if missing from messages.yml section
                msg = "&aYou have leveled up " + pillar + " to level &e%level%&a!";
            }
            msg = ChatColor.translateAlternateColorCodes('&', msg.replace("%level%", String.valueOf(currentLevel)));
            
            player.sendMessage(msg);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }
}
