package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerConnectionListener implements Listener {

    private final HereRolePlay plugin;

    public PlayerConnectionListener(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        // Pre-load profile asynchronously before the player fully joins
        plugin.getDatabaseManager().loadProfile(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // We schedule this 1 tick later just to be safe that profile is fully loaded
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getAttributeManager().applyAttributes(event.getPlayer());
        }, 1L);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getAttributeManager().applyAttributes(event.getPlayer());
        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Unload and save profile when they leave
        plugin.getDatabaseManager().unloadProfile(event.getPlayer().getUniqueId());
    }
}
