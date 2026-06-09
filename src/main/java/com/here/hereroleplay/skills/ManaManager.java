package com.here.hereroleplay.skills;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ManaManager {

    private final HereRolePlay plugin;
    private int regenTaskId = -1;
    private int actionbarTaskId = -1;

    public ManaManager(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Regen Task (every 1 second)
        regenTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::regenerateMana, 20L, 20L).getTaskId();
        
        // Actionbar Task (every 5 ticks / 0.25s)
        actionbarTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateActionBars, 5L, 5L).getTaskId();
    }

    public void stop() {
        if (regenTaskId != -1) Bukkit.getScheduler().cancelTask(regenTaskId);
        if (actionbarTaskId != -1) Bukkit.getScheduler().cancelTask(actionbarTaskId);
    }

    public double getMaxMana(PlayerProfile profile) {
        // Base 100 + 10 per intelligence point
        return 100.0 + (profile.getIntelligencePoints() * 10.0);
    }

    private void regenerateMana() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile == null) continue;

            double maxMana = getMaxMana(profile);
            if (profile.getCurrentMana() < maxMana) {
                // Regen 5 mana per second + 0.5 per int point
                double regenAmount = 5.0 + (profile.getIntelligencePoints() * 0.5);
                double newMana = Math.min(maxMana, profile.getCurrentMana() + regenAmount);
                profile.setCurrentMana(newMana);
            }
        }
    }

    private void updateActionBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile == null) continue;

            double maxMana = getMaxMana(profile);
            double currentMana = profile.getCurrentMana();
            
            String actionbar = String.format("§b✦ Mana: %.0f / %.0f", currentMana, maxMana);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
        }
    }
}
