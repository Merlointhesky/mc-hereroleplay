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

        String name = type.name();
        if (name.contains("LOG") || name.contains("WOOD")) {
            xpToGive = 2.0;
        } else if (name.contains("COAL_ORE") || name.contains("IRON_ORE") || name.contains("COPPER_ORE") || name.contains("GOLD_ORE") || name.contains("REDSTONE_ORE") || name.contains("LAPIS_ORE")) {
            xpToGive = 5.0;
        } else if (name.contains("DIAMOND_ORE") || name.contains("EMERALD_ORE") || type == Material.ANCIENT_DEBRIS || type == Material.NETHER_QUARTZ_ORE || type == Material.NETHER_GOLD_ORE) {
            xpToGive = 25.0;
        } else if (org.bukkit.Tag.MINEABLE_PICKAXE.isTagged(type) || org.bukkit.Tag.MINEABLE_SHOVEL.isTagged(type)) {
            xpToGive = 2.0;
        }

        if (xpToGive > 0) {
            plugin.getXpManager().addCollectXp(player, xpToGive);
        }
    }
}
