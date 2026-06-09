package com.here.hereroleplay.gui.menus;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.gui.CustomGUI;
import com.here.hereroleplay.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MainHubGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final Inventory inventory;

    public MainHubGUI(HereRolePlay plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, "§8HereRolePlay Hub");
        
        setupItems();
    }

    private void setupItems() {
        // Profile Item
        inventory.setItem(11, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&eYour Profile")
                .setLore("&7View your current level,", "&7stats, and unlocked classes.")
                .build());

        // Allocation Menu Item
        inventory.setItem(13, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .setName("&bAllocate Points")
                .setLore("&7Spend your unused skill points", "&7to increase your attributes.")
                .build());

        // Class Directory Item
        inventory.setItem(15, new ItemBuilder(Material.WRITTEN_BOOK)
                .setName("&6Class Directory")
                .setLore("&7View available classes and", "&7their unlock requirements.")
                .build());
                
        // Filler
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        
        switch (slot) {
            case 11:
                player.openInventory(new ProfileGUI(plugin, player).getInventory());
                break;
            case 13:
                player.openInventory(new AllocationGUI(plugin, player).getInventory());
                break;
            case 15:
                player.openInventory(new ClassDirectoryGUI(plugin, player).getInventory());
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
