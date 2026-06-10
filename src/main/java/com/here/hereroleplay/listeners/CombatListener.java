package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
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
}
