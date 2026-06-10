package com.here.hereroleplay.skills;

import com.here.hereroleplay.HereRolePlay;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {

    private final HereRolePlay plugin;
    private final Map<UUID, PlayerBossBar> activeBars = new HashMap<>();

    public BossBarManager(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    public void showXpGain(Player player, String skillName, double xpGained, double currentXp, int currentLevel) {
        UUID uuid = player.getUniqueId();
        PlayerBossBar playerBar = activeBars.get(uuid);

        if (playerBar == null) {
            BossBar bar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
            bar.addPlayer(player);
            playerBar = new PlayerBossBar(bar);
            activeBars.put(uuid, playerBar);
        } else {
            playerBar.cancelTimer();
        }

        // Calculate progress
        double xpForCurrent = plugin.getXpManager().getXpRequiredForLevel(currentLevel);
        double xpForNext = plugin.getXpManager().getXpRequiredForLevel(currentLevel + 1);
        double levelProgressXp = Math.max(0, currentXp - xpForCurrent);
        double levelRequiredXp = xpForNext - xpForCurrent;

        double progress = 0.0;
        if (levelRequiredXp > 0) {
            progress = Math.min(1.0, Math.max(0.0, levelProgressXp / levelRequiredXp));
        }

        // Configure look based on skillName
        BarColor color;
        String prefix;
        switch (skillName.toLowerCase()) {
            case "combat":
                color = BarColor.RED;
                prefix = "§c⚔ Combat";
                break;
            case "collect":
                color = BarColor.GREEN;
                prefix = "§a⛏ Collect";
                break;
            case "craft":
                color = BarColor.YELLOW;
                prefix = "§e⚒ Craft";
                break;
            default:
                color = BarColor.WHITE;
                prefix = "§f✦ " + skillName;
                break;
        }

        playerBar.bar.setColor(color);
        playerBar.bar.setProgress(progress);

        // Format Title: ✦ SkillName +XP (Current/Required) Lvl X
        String title = String.format("%s §f+%.1f XP §7(§e%.1f§7/§e%.1f§7) §aLvl %d", 
                prefix, xpGained, levelProgressXp, levelRequiredXp, currentLevel);
        playerBar.bar.setTitle(title);
        playerBar.bar.setVisible(true);

        // Schedule to fade out in 3 seconds (60 ticks)
        final PlayerBossBar finalPlayerBar = playerBar;
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                finalPlayerBar.bar.setVisible(false);
            }
        }.runTaskLater(plugin, 60L);

        playerBar.setTimer(task);
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerBossBar playerBar = activeBars.remove(uuid);
        if (playerBar != null) {
            playerBar.cancelTimer();
            playerBar.bar.removeAll();
        }
    }

    public void clearAll() {
        for (PlayerBossBar playerBar : activeBars.values()) {
            playerBar.cancelTimer();
            playerBar.bar.removeAll();
        }
        activeBars.clear();
    }

    private static class PlayerBossBar {
        final BossBar bar;
        BukkitTask hideTask;

        PlayerBossBar(BossBar bar) {
            this.bar = bar;
        }

        void cancelTimer() {
            if (hideTask != null) {
                hideTask.cancel();
                hideTask = null;
            }
        }

        void setTimer(BukkitTask task) {
            this.hideTask = task;
        }
    }
}
