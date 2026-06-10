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
    private final double baseRegen;
    private final double intelligenceRegenBonus;
    private final double baseMaxMana;
    private final double intelligenceMaxBonus;

    public ManaManager(HereRolePlay plugin) {
        this.plugin = plugin;
        this.baseRegen = plugin.getConfig().getDouble("mana.base-regen", 1.0);
        this.intelligenceRegenBonus = plugin.getConfig().getDouble("mana.intelligence-regen-bonus", 0.1);
        this.baseMaxMana = plugin.getConfig().getDouble("mana.base-max", 20.0);
        this.intelligenceMaxBonus = plugin.getConfig().getDouble("mana.intelligence-max-bonus", 10.0);
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
        return baseMaxMana + (profile.getIntelligencePoints() * intelligenceMaxBonus);
    }

    private void regenerateMana() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile == null) continue;

            double maxMana = getMaxMana(profile);
            double currentMana = profile.getCurrentMana();
            if (currentMana > maxMana) {
                profile.setCurrentMana(maxMana);
            } else if (currentMana < maxMana) {
                double regenAmount = baseRegen + (profile.getIntelligencePoints() * intelligenceRegenBonus);
                int spellEchoLvl = profile.getSkillLevel("Spell Echo");
                if (spellEchoLvl > 0) {
                    regenAmount *= (1.0 + spellEchoLvl * 0.01);
                }
                double newMana = Math.min(maxMana, currentMana + regenAmount);
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
            if (currentMana > maxMana) {
                currentMana = maxMana;
                profile.setCurrentMana(currentMana);
            }
            
            String actionbar = String.format("§b✦ Mana: %.0f / %.0f", currentMana, maxMana);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
        }
    }
}
