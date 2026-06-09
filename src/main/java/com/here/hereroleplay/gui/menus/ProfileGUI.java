package com.here.hereroleplay.gui.menus;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import com.here.hereroleplay.gui.CustomGUI;
import com.here.hereroleplay.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ProfileGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final Inventory inventory;

    public ProfileGUI(HereRolePlay plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, "§8" + player.getName() + "'s Profile");
        
        setupItems();
    }

    private void setupItems() {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Player Head with Levels
        inventory.setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&e&l" + player.getName())
                .setLore(
                    "&7Combat Level: &c" + profile.getCombatLevel(),
                    "&7Collect Level: &a" + profile.getCollectLevel(),
                    "&7Craft Level: &e" + profile.getCraftLevel(),
                    "",
                    "&7Unspent Points: &b" + profile.getUnspentSkillPoints(),
                    "&7Mana: &b✦ " + Math.round(profile.getCurrentMana()) + " / " + Math.round(plugin.getManaManager().getMaxMana(profile))
                )
                .build());

        // Back button
        inventory.setItem(26, new ItemBuilder(Material.ARROW).setName("&cBack").build());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getSlot() == 26) {
            player.openInventory(new MainHubGUI(plugin, player).getInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
