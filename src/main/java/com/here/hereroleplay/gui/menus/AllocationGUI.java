package com.here.hereroleplay.gui.menus;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import com.here.hereroleplay.gui.CustomGUI;
import com.here.hereroleplay.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class AllocationGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final Inventory inventory;

    public AllocationGUI(HereRolePlay plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, "§8Attribute Allocation");
        
        setupItems();
    }

    private void setupItems() {
        inventory.clear();
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Info item
        inventory.setItem(4, new ItemBuilder(Material.BOOK)
                .setName("&bUnspent Points: &f" + profile.getUnspentSkillPoints())
                .setLore("&7Click to spend 1 point.", "&7Shift-Click to spend 5 points.")
                .build());

        // Strength
        inventory.setItem(20, new ItemBuilder(Material.IRON_SWORD)
                .setName("&cStrength")
                .setLore(
                    "&7Current: &f" + profile.getStrengthPoints(),
                    "&7Effect: &f+" + (profile.getStrengthPoints() * 0.5) + " Attack Damage"
                ).build());

        // Agility
        inventory.setItem(21, new ItemBuilder(Material.FEATHER)
                .setName("&aAgility")
                .setLore(
                    "&7Current: &f" + profile.getAgilityPoints(),
                    "&7Effect: &f+" + (profile.getAgilityPoints() * 0.002) + " Move Speed"
                ).build());

        // Vitality
        inventory.setItem(23, new ItemBuilder(Material.GOLDEN_APPLE)
                .setName("&6Vitality")
                .setLore(
                    "&7Current: &f" + profile.getVitalityPoints(),
                    "&7Effect: &f+" + (profile.getVitalityPoints() * 0.5) + " Max Health"
                ).build());

        // Intelligence
        inventory.setItem(24, new ItemBuilder(Material.LAPIS_LAZULI)
                .setName("&9Intelligence")
                .setLore(
                    "&7Current: &f" + profile.getIntelligencePoints(),
                    "&7Effect: &f+" + (profile.getIntelligencePoints() * 10) + " Max Mana"
                ).build());

        // Back button
        inventory.setItem(49, new ItemBuilder(Material.ARROW).setName("&cBack").build());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        int slot = event.getSlot();
        if (slot == 49) {
            player.openInventory(new MainHubGUI(plugin, player).getInventory());
            return;
        }

        int pointsToSpend = event.isShiftClick() ? 5 : 1;
        
        if (profile.getUnspentSkillPoints() < pointsToSpend && (slot == 20 || slot == 21 || slot == 23 || slot == 24)) {
            // Check if they have enough to spend even 1, if so, just spend what they have
            if (profile.getUnspentSkillPoints() > 0) {
                pointsToSpend = profile.getUnspentSkillPoints();
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }
        }

        boolean updated = false;

        switch (slot) {
            case 20: // Strength
                profile.setStrengthPoints(profile.getStrengthPoints() + pointsToSpend);
                updated = true;
                break;
            case 21: // Agility
                profile.setAgilityPoints(profile.getAgilityPoints() + pointsToSpend);
                updated = true;
                break;
            case 23: // Vitality
                profile.setVitalityPoints(profile.getVitalityPoints() + pointsToSpend);
                updated = true;
                break;
            case 24: // Intelligence
                profile.setIntelligencePoints(profile.getIntelligencePoints() + pointsToSpend);
                updated = true;
                break;
        }

        if (updated) {
            profile.setUnspentSkillPoints(profile.getUnspentSkillPoints() - pointsToSpend);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
            
            // Re-apply attributes so changes take effect immediately
            plugin.getAttributeManager().applyAttributes(player);
            
            // Check for new class unlocks
            plugin.getClassManager().checkUnlocks(player);
            
            // Redraw GUI
            setupItems();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
