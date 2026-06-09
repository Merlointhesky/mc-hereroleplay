package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
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
            ItemStack weapon = player.getInventory().getItemInMainHand();

            // Very simple check for a weapon
            if (weapon == null || !weapon.getType().name().contains("SWORD") 
                && !weapon.getType().name().contains("AXE")
                && !weapon.getType().name().contains("BOW")
                && !weapon.getType().name().contains("MACE")
                && !weapon.getType().name().contains("TRIDENT")) {
                return;
            }

            double xpToGive = 1.0; // Tier 1 Default

            EntityType type = event.getEntityType();
            switch (type) {
                case ZOMBIE:
                case SKELETON:
                case SPIDER:
                    xpToGive = 5.0; // Tier 2
                    break;
                case ENDERMAN:
                case RAVAGER:
                    xpToGive = 25.0; // Tier 3
                    break;
                case ENDER_DRAGON:
                case WITHER:
                case WARDEN:
                    xpToGive = 250.0; // Tier 4
                    break;
                default:
                    break;
            }

            plugin.getXpManager().addCombatXp(player, xpToGive);
        }
    }
}
