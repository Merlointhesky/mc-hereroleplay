package com.here.hereroleplay.gui.menus;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import com.here.hereroleplay.gui.CustomGUI;
import com.here.hereroleplay.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final Inventory inventory;

    private static List<PlayerProfile> cachedProfiles = null;
    private static long lastCacheTime = 0;

    public LeaderboardGUI(HereRolePlay plugin, Player player, List<PlayerProfile> profiles) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, "§8HereRolePlay Leaderboards");
        
        setupItems(profiles);
    }

    public static void openLeaderboard(HereRolePlay plugin, Player player) {
        long now = System.currentTimeMillis();
        if (cachedProfiles != null && (now - lastCacheTime) < 10000L) { // 10 seconds cache
            player.openInventory(new LeaderboardGUI(plugin, player, cachedProfiles).getInventory());
            return;
        }

        player.sendMessage("§aLoading leaderboards... Please wait.");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<PlayerProfile> profiles = plugin.getDatabaseManager().getAllProfiles();
            // Merge active cached profiles for online players
            java.util.Map<java.util.UUID, PlayerProfile> onlineCache = plugin.getDatabaseManager().getProfileCache();
            for (int i = 0; i < profiles.size(); i++) {
                PlayerProfile dbProfile = profiles.get(i);
                if (onlineCache.containsKey(dbProfile.getUuid())) {
                    profiles.set(i, onlineCache.get(dbProfile.getUuid()));
                }
            }

            cachedProfiles = profiles;
            lastCacheTime = System.currentTimeMillis();

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (player.isOnline()) {
                    player.openInventory(new LeaderboardGUI(plugin, player, cachedProfiles).getInventory());
                }
            });
        });
    }

    private void setupItems(List<PlayerProfile> profiles) {
        // Total Level
        List<PlayerProfile> totalSorted = new ArrayList<>(profiles);
        totalSorted.sort((p1, p2) -> {
            int tot1 = p1.getCombatLevel() + p1.getCollectLevel() + p1.getCraftLevel();
            int tot2 = p2.getCombatLevel() + p2.getCollectLevel() + p2.getCraftLevel();
            return Integer.compare(tot2, tot1);
        });

        // Combat
        List<PlayerProfile> combatSorted = new ArrayList<>(profiles);
        combatSorted.sort((p1, p2) -> {
            if (p1.getCombatLevel() != p2.getCombatLevel()) {
                return Integer.compare(p2.getCombatLevel(), p1.getCombatLevel());
            }
            return Double.compare(p2.getCombatXp(), p1.getCombatXp());
        });

        // Collect
        List<PlayerProfile> collectSorted = new ArrayList<>(profiles);
        collectSorted.sort((p1, p2) -> {
            if (p1.getCollectLevel() != p2.getCollectLevel()) {
                return Integer.compare(p2.getCollectLevel(), p1.getCollectLevel());
            }
            return Double.compare(p2.getCollectXp(), p1.getCollectXp());
        });

        // Craft
        List<PlayerProfile> craftSorted = new ArrayList<>(profiles);
        craftSorted.sort((p1, p2) -> {
            if (p1.getCraftLevel() != p2.getCraftLevel()) {
                return Integer.compare(p2.getCraftLevel(), p1.getCraftLevel());
            }
            return Double.compare(p2.getCraftXp(), p1.getCraftXp());
        });

        inventory.setItem(10, new ItemBuilder(Material.NETHER_STAR)
                .setName("&b&lTotal Level Leaderboard")
                .setLore(getLeaderboardLore(totalSorted, "total"))
                .build());

        inventory.setItem(12, new ItemBuilder(Material.IRON_SWORD)
                .setName("&c&lCombat Level Leaderboard")
                .setLore(getLeaderboardLore(combatSorted, "combat"))
                .build());

        inventory.setItem(14, new ItemBuilder(Material.DIAMOND_PICKAXE)
                .setName("&a&lCollect Level Leaderboard")
                .setLore(getLeaderboardLore(collectSorted, "collect"))
                .build());

        inventory.setItem(16, new ItemBuilder(Material.FURNACE)
                .setName("&e&lCraft Level Leaderboard")
                .setLore(getLeaderboardLore(craftSorted, "craft"))
                .build());

        // Back button
        inventory.setItem(22, new ItemBuilder(Material.ARROW).setName("&cBack").build());

        // Filler
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    private List<String> getLeaderboardLore(List<PlayerProfile> sorted, String type) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Top 10 players:");
        lore.add("");
        
        int count = Math.min(10, sorted.size());
        if (count == 0) {
            lore.add("&cNo data available");
            return lore;
        }

        for (int i = 0; i < count; i++) {
            PlayerProfile profile = sorted.get(i);
            OfflinePlayer op = Bukkit.getOfflinePlayer(profile.getUuid());
            String name = op.getName() != null ? op.getName() : "Unknown";
            
            String valStr = "";
            switch (type) {
                case "total":
                    valStr = String.valueOf(profile.getCombatLevel() + profile.getCollectLevel() + profile.getCraftLevel());
                    break;
                case "combat":
                    valStr = "Lvl " + profile.getCombatLevel();
                    break;
                case "collect":
                    valStr = "Lvl " + profile.getCollectLevel();
                    break;
                case "craft":
                    valStr = "Lvl " + profile.getCraftLevel();
                    break;
            }
            
            String color = "&7";
            if (i == 0) color = "&6&l1. ";
            else if (i == 1) color = "&f&l2. ";
            else if (i == 2) color = "&e&l3. ";
            else color = "&7" + (i + 1) + ". ";
            
            lore.add(color + name + " &8- &b" + valStr);
        }
        return lore;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getSlot() == 22) {
            player.openInventory(new MainHubGUI(plugin, player).getInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
