package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class CombatListener implements Listener {

    private final HereRolePlay plugin;

    public CombatListener(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            double xpToGive = 1.0; // Tier 1 Default (Passive mobs / Animals)

            EntityType type = event.getEntityType();
            String typeName = type.name();

            // Broad check for hostile mobs / monsters
            if (typeName.contains("ZOMBIE") || typeName.contains("SKELETON") || typeName.contains("SPIDER") ||
                type == EntityType.CREEPER || type == EntityType.PIGLIN || type == EntityType.BLAZE ||
                type == EntityType.GHAST || type == EntityType.SLIME || type == EntityType.MAGMA_CUBE ||
                type == EntityType.WITCH || type == EntityType.PHANTOM || type == EntityType.DROWNED ||
                type == EntityType.HUSK || type == EntityType.STRAY || type == EntityType.PILLAGER ||
                type == EntityType.VINDICATOR || type == EntityType.EVOKER || type == EntityType.HOGLIN ||
                type == EntityType.PIGLIN_BRUTE || type == EntityType.GUARDIAN || type == EntityType.ELDER_GUARDIAN) {
                xpToGive = 5.0; // Tier 2
            } else if (type == EntityType.ENDERMAN || type == EntityType.RAVAGER || type == EntityType.WITHER_SKELETON || type == EntityType.SHULKER) {
                xpToGive = 25.0; // Tier 3
            } else if (type == EntityType.ENDER_DRAGON || type == EntityType.WITHER || type == EntityType.WARDEN) {
                xpToGive = 250.0; // Tier 4
            }

            plugin.getXpManager().addCombatXp(player, xpToGive);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double damage = event.getFinalDamage();
            if (damage > 0) {
                // 1 heart = 2 damage points = 1 XP
                double xpToGive = damage / 2.0;
                plugin.getXpManager().addCombatXp(player, xpToGive);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        Player player = null;
        boolean isMelee = false;
        boolean isRanged = false;

        if (event.getDamager() instanceof Player p) {
            player = p;
            isMelee = true;
        } else if (event.getDamager() instanceof org.bukkit.entity.Arrow arrow && arrow.getShooter() instanceof Player p) {
            player = p;
            isRanged = true;
        }

        if (player == null) return;
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        if (isMelee && event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            // Heavy Strike passive (Warrior)
            int heavyStrikeLvl = profile.getSkillLevel("Heavy Strike");
            if (heavyStrikeLvl > 0) {
                double multiplier = 1.20 + (heavyStrikeLvl - 1) * 0.05;
                event.setDamage(event.getDamage() * multiplier);
            }
        }

        if (isRanged) {
            // Precision passive (Ranger)
            int precisionLvl = profile.getSkillLevel("Precision");
            if (precisionLvl > 0) {
                double critChance = 0.15 + (precisionLvl - 1) * 0.03;
                if (Math.random() < critChance) {
                    event.setDamage(event.getDamage() * 1.5);
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1.2f);
                    event.getEntity().getWorld().spawnParticle(org.bukkit.Particle.CRIT, event.getEntity().getLocation().add(0, 1, 0), 10, 0.2, 0.5, 0.2, 0.1);
                    player.sendMessage("§a★ Precision Critical Strike!");
                }
            }
        }
    }
}
