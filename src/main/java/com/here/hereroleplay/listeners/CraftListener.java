package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    private final HereRolePlay plugin;

    public CraftListener(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        ItemStack result = event.getRecipe().getResult();
        if (result != null && result.getType() != Material.AIR) {
            // Simplified XP logic: 2 XP per craft for now. 
            // In a real plugin, this would check the recipe complexity.
            plugin.getXpManager().addCraftXp(player, 2.0);
        }
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        
        // Multipliers based on furnace type
        double multiplier = 1.0;
        Material blockType = event.getBlock().getType();
        
        if (blockType == Material.BLAST_FURNACE) {
            multiplier = plugin.getConfig().getDouble("xp.multipliers.blast-furnace", 2.0);
        } else if (blockType == Material.SMOKER) {
            multiplier = plugin.getConfig().getDouble("xp.multipliers.smoker", 2.0);
        }
        
        int amount = event.getItemAmount();
        double xpToGive = amount * multiplier * 2.0; // Base 2.0 per item smelted
        
        plugin.getXpManager().addCraftXp(player, xpToGive);
    }
}
