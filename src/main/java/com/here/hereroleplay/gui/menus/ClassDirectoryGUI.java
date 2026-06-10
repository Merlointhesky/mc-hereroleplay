package com.here.hereroleplay.gui.menus;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.classes.HrpClass;
import com.here.hereroleplay.data.PlayerProfile;
import com.here.hereroleplay.gui.CustomGUI;
import com.here.hereroleplay.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ClassDirectoryGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final Inventory inventory;

    public ClassDirectoryGUI(HereRolePlay plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, "§8Class Directory");
        
        setupItems();
    }

    private void setupItems() {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        List<HrpClass> classes = plugin.getClassManager().getClasses();
        
        int slot = 10;
        for (HrpClass hrpClass : classes) {
            if (hrpClass.getName().equalsIgnoreCase("Admin Class")) {
                continue;
            }
            // Keep formatting somewhat centered
            if (slot == 17 || slot == 26 || slot == 35) slot += 2;
            
            boolean unlocked = profile.getUnlockedClasses().contains(hrpClass.getName());
            Material mat = unlocked ? Material.ENCHANTED_BOOK : Material.BOOK;
            String status = unlocked ? "&aUnlocked" : "&cLocked";
            
            inventory.setItem(slot, new ItemBuilder(mat)
                    .setName("&6" + hrpClass.getName())
                    .setLore(
                        "&7" + hrpClass.getDescription(),
                        "",
                        "&eRequirements:",
                        "&7Strength: " + hrpClass.getReqStrength(),
                        "&7Agility: " + hrpClass.getReqAgility(),
                        "&7Vitality: " + hrpClass.getReqVitality(),
                        "&7Intelligence: " + hrpClass.getReqIntelligence(),
                        "&7Total Points: " + hrpClass.getReqTotalPoints(),
                        "",
                        "&fStatus: " + status
                    ).build());
            slot++;
        }

        // Back button
        inventory.setItem(49, new ItemBuilder(Material.ARROW).setName("&cBack").build());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getSlot() == 49) {
            player.openInventory(new MainHubGUI(plugin, player).getInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
