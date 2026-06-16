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
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> loadAndApply(event.getPlayer()), 1L);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> loadAndApply(event.getPlayer()), 1L);
    }

    private void loadAndApply(org.bukkit.entity.Player player) {
        com.here.hereroleplay.data.PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) {
            plugin.getLogger().warning("Profile for " + player.getName() + " was not in cache on join/respawn. Loading synchronously...");
            profile = plugin.getDatabaseManager().loadProfileSync(player.getUniqueId());
            plugin.getDatabaseManager().getProfileCache().put(player.getUniqueId(), profile);
        }
        plugin.getAttributeManager().applyAttributes(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Unload and save profile when they leave
        plugin.getDatabaseManager().unloadProfile(event.getPlayer().getUniqueId());
        plugin.getBossBarManager().removePlayer(event.getPlayer());
    }
}
