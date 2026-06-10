package com.here.hereroleplay.gui.menus;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.classes.ClassManager;
import com.here.hereroleplay.data.PlayerProfile;
import com.here.hereroleplay.gui.CustomGUI;
import com.here.hereroleplay.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ProfileGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final Inventory inventory;

    public ProfileGUI(HereRolePlay plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 45, "§8" + player.getName() + "'s Profile");
        
        setupItems();
    }

    private void setupItems() {
        inventory.clear();
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Player Head with Levels
        int totalLevel = profile.getCombatLevel() + profile.getCollectLevel() + profile.getCraftLevel();
        inventory.setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&e&l" + player.getName())
                .setLore(
                    "&7Combat Level: &c" + profile.getCombatLevel(),
                    "&7Collect Level: &a" + profile.getCollectLevel(),
                    "&7Craft Level: &e" + profile.getCraftLevel(),
                    "&7Total Level: &b&l" + totalLevel,
                    "",
                    "&dAllocated Attributes:",
                    "&7- Strength: &f" + profile.getStrengthPoints(),
                    "&7- Agility: &f" + profile.getAgilityPoints(),
                    "&7- Vitality: &f" + profile.getVitalityPoints(),
                    "&7- Intelligence: &f" + profile.getIntelligencePoints(),
                    "",
                    "&7Unspent Points: &b" + profile.getUnspentSkillPoints(),
                    "&7Mana: &b✦ " + Math.round(profile.getCurrentMana()) + " / " + Math.round(plugin.getManaManager().getMaxMana(profile))
                )
                .build());

        // Unlocked Active Skills
        List<ClassManager.ActiveSkill> activeSkills = plugin.getClassManager().getUnlockedActiveSkills(profile);
        List<String> activeLore = new ArrayList<>();
        activeLore.add("&7Your unlocked active class skills:");
        activeLore.add("");
        if (activeSkills.isEmpty()) {
            activeLore.add("&cNone");
            activeLore.add("&7Unlock classes to gain active skills!");
        } else {
            for (ClassManager.ActiveSkill skill : activeSkills) {
                activeLore.add("&6" + skill.getName() + " &7(" + skill.getClassName() + ")");
                activeLore.add("&7- Trigger: &f" + skill.getTrigger());
                activeLore.add("&7- Cost: &b" + skill.getManaCost() + " Mana");
                activeLore.add("&7- Effect: &f" + skill.getEffect());
                activeLore.add("");
            }
        }
        inventory.setItem(20, new ItemBuilder(Material.BLAZE_POWDER)
                .setName("&b&lActive Skills")
                .setLore(activeLore)
                .build());

        // Unlocked Passive Skills
        List<ClassManager.PassiveSkill> passiveSkills = plugin.getClassManager().getUnlockedPassiveSkills(profile);
        List<String> passiveLore = new ArrayList<>();
        passiveLore.add("&7Your unlocked passive class skills:");
        passiveLore.add("");
        if (passiveSkills.isEmpty()) {
            passiveLore.add("&cNone");
            passiveLore.add("&7Unlock classes to gain passive skills!");
        } else {
            for (ClassManager.PassiveSkill skill : passiveSkills) {
                passiveLore.add("&6" + skill.getName() + " &7(" + skill.getClassName() + ")");
                passiveLore.add("&7- Effect: &f" + skill.getEffect());
                passiveLore.add("");
            }
        }
        inventory.setItem(24, new ItemBuilder(Material.NETHER_STAR)
                .setName("&a&lPassive Skills")
                .setLore(passiveLore)
                .build());

        // Back button
        inventory.setItem(40, new ItemBuilder(Material.ARROW).setName("&cBack").build());

        // Filler
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getSlot() == 40) {
            player.openInventory(new MainHubGUI(plugin, player).getInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
