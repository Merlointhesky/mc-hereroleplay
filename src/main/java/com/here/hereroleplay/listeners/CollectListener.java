package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
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
        boolean isCrop = false;

        // Farming crops check
        if (type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES || 
            type == Material.BEETROOTS || type == Material.COCOA || type == Material.NETHER_WART || 
            type == Material.SWEET_BERRY_BUSH) {
            isCrop = true;
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Ageable ageable) {
                // Ensure the crop is fully ripe
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    xpToGive = 3.0; // Farming base XP
                }
            }
        } else if (type == Material.MELON || type == Material.PUMPKIN) {
            isCrop = true;
            xpToGive = 3.0; // Pumpkin/melon block breaks
        } else if (type == Material.SUGAR_CANE || type == Material.CACTUS || type == Material.BAMBOO) {
            isCrop = true;
            xpToGive = 1.0; // Individual vertical growth blocks
        }

        if (!isCrop) {
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
        }

        if (xpToGive > 0) {
            plugin.getXpManager().addCollectXp(player, xpToGive);
            
            // Farmer - Bountiful Harvest passive
            if (isCrop) {
                PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
                if (profile != null) {
                    int harvestLvl = profile.getSkillLevel("Bountiful Harvest");
                    if (harvestLvl > 0) {
                        double doubleChance = harvestLvl * 0.01;
                        if (Math.random() < doubleChance) {
                            for (org.bukkit.inventory.ItemStack drop : block.getDrops(player.getInventory().getItemInMainHand())) {
                                block.getWorld().dropItemNaturally(block.getLocation(), drop);
                            }
                            player.sendMessage(org.bukkit.ChatColor.GREEN + "★ Bountiful Harvest: Double Crops!");
                            player.playSound(block.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 1f, 1.5f);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            plugin.getXpManager().addCollectXp(player, 10.0);
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile != null) {
            int hackerLvl = profile.getSkillLevel("Hacker");
            if (hackerLvl > 0) {
                double ignoreChance = hackerLvl * 0.01;
                if (Math.random() < ignoreChance) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onItemMend(PlayerItemMendEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile != null) {
            int repairLvl = profile.getSkillLevel("Repair");
            if (repairLvl > 0) {
                double multiplier = 1.0 + repairLvl * 0.01;
                int originalAmount = event.getRepairAmount();
                event.setRepairAmount((int) Math.round(originalAmount * multiplier));
            }
        }
    }
}
