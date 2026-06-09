package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class CollectListener implements Listener {

    private final HereRolePlay plugin;
    private static final String PLACED_META = "hrp_placed";

    public CollectListener(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Tag block as player-placed to prevent exploit
        event.getBlock().setMetadata(PLACED_META, new FixedMetadataValue(plugin, true));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Check for player-placed metadata to avoid exploits
        if (block.hasMetadata(PLACED_META)) {
            block.removeMetadata(PLACED_META, plugin);
            return;
        }

        Material type = block.getType();
        double xpToGive = 0;

        // Simplified list of blocks that give XP
        if (type.name().contains("LOG")) {
            xpToGive = 1.0;
        } else if (type == Material.COAL_ORE || type == Material.IRON_ORE || type == Material.COPPER_ORE) {
            xpToGive = 5.0;
        } else if (type == Material.DIAMOND_ORE || type == Material.EMERALD_ORE || type == Material.ANCIENT_DEBRIS) {
            xpToGive = 25.0;
        }

        if (xpToGive > 0) {
            plugin.getXpManager().addCollectXp(player, xpToGive);
        }
    }
}
