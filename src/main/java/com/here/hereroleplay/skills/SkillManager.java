package com.here.hereroleplay.skills;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashSet;
import java.util.Set;

public class SkillManager implements Listener {

    private final HereRolePlay plugin;

    public SkillManager(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShiftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleSkillTrigger(player, false);
        }
    }

    @EventHandler
    public void onFKeySwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        // Prevent the actual item swap
        event.setCancelled(true);
        
        handleSkillTrigger(player, true);
    }

    private void handleSkillTrigger(Player player, boolean isFKey) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        Material handItem = player.getInventory().getItemInMainHand().getType();
        
        if (handItem.name().contains("AXE") && !isFKey) {
            // Timber (Axe, Shift+Click)
            // Requires Miner or Warrior class, maybe? For now let's just make it cost mana
            executeTimber(player, profile);
        } else if (handItem.name().contains("SWORD") && isFKey) {
            // Cleave (Sword, F-Key)
            executeCleave(player, profile);
        } else if (handItem == Material.STICK && isFKey) {
            // Arcane Missile (Stick, F-Key)
            executeArcaneMissile(player, profile);
        }
    }

    private void executeTimber(Player player, PlayerProfile profile) {
        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Timber!");
            return;
        }
        
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock != null && targetBlock.getType().name().contains("LOG")) {
            profile.setCurrentMana(profile.getCurrentMana() - cost);
            player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Timber" + ChatColor.GREEN + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
            
            // Simple logic: Break a column of logs
            int broken = 0;
            Location loc = targetBlock.getLocation();
            while (loc.getBlock().getType().name().contains("LOG") && broken < 10) {
                loc.getBlock().breakNaturally(player.getInventory().getItemInMainHand());
                loc.add(0, 1, 0);
                broken++;
            }
        }
    }

    private void executeCleave(Player player, PlayerProfile profile) {
        double cost = 30.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Cleave!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.RED + "You used " + ChatColor.DARK_RED + "Cleave" + ChatColor.RED + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.5f);
        player.spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1, 0), 5, 1, 0.2, 1, 0);

        // Damage all entities within 3 blocks
        for (Entity entity : player.getNearbyEntities(3, 2, 3)) {
            if (entity instanceof LivingEntity target && target != player) {
                target.damage(10.0, player);
            }
        }
    }

    private void executeArcaneMissile(Player player, PlayerProfile profile) {
        double cost = 15.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Arcane Missile!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You used " + ChatColor.DARK_PURPLE + "Arcane Missile" + ChatColor.LIGHT_PURPLE + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 2f);
        
        // Very simple logic: Shoot a generic snowball that represents magic
        // In a real plugin, this would be a custom moving particle entity
        org.bukkit.entity.Snowball projectile = player.launchProjectile(org.bukkit.entity.Snowball.class);
        projectile.setCustomName("Arcane Missile");
        projectile.setGlowing(true);
        // Add velocity
        projectile.setVelocity(player.getLocation().getDirection().multiply(2.0));
    }
}
