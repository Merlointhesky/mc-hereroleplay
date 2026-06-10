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
        inventory.clear();
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        List<HrpClass> classes = plugin.getClassManager().getClasses();
        
        int baseSlot = 10;
        int heroSlot = 30;
        
        for (HrpClass hrpClass : classes) {
            if (hrpClass.getName().equalsIgnoreCase("Admin Class")) {
                continue;
            }
            
            boolean unlocked = profile.getUnlockedClasses().contains(hrpClass.getName());
            Material mat = hrpClass.getIcon();
            String status = unlocked ? "&aUnlocked &7(Click to open skills)" : "&cLocked";
            
            int slot = hrpClass.isHero() ? heroSlot++ : baseSlot++;
            
            inventory.setItem(slot, new ItemBuilder(mat)
                    .setName("&6" + hrpClass.getName())
                    .setGlowing(unlocked)
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
        }

        // Back button
        inventory.setItem(49, new ItemBuilder(Material.ARROW).setName("&cBack").build());

        // Filler
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int clickedSlot = event.getSlot();
        if (clickedSlot == 49) {
            player.openInventory(new MainHubGUI(plugin, player).getInventory());
            return;
        }

        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        List<HrpClass> classes = plugin.getClassManager().getClasses();
        int baseSlot = 10;
        int heroSlot = 30;

        for (HrpClass hrpClass : classes) {
            if (hrpClass.getName().equalsIgnoreCase("Admin Class")) {
                continue;
            }
            
            int expectedSlot = hrpClass.isHero() ? heroSlot++ : baseSlot++;
            if (clickedSlot == expectedSlot) {
                if (profile.getUnlockedClasses().contains(hrpClass.getName())) {
                    player.openInventory(new ClassSkillsGUI(plugin, player, hrpClass).getInventory());
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
                } else {
                    player.sendMessage("§cYou must unlock the " + hrpClass.getName() + " class first!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                }
                break;
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
