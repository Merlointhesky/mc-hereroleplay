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

        double regenBonusPerInt = plugin.getConfig().getDouble("mana.intelligence-regen-bonus", 0.1);
        double maxManaBonusPerInt = plugin.getConfig().getDouble("mana.intelligence-max-bonus", 10.0);

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
                    "&7- Strength: &f" + profile.getStrengthPoints() + " &a(+" + String.format("%.1f", profile.getStrengthPoints() * 0.5) + " Attack Damage)",
                    "&7- Agility: &f" + profile.getAgilityPoints() + " &a(+" + String.format("%.4f", profile.getAgilityPoints() * 0.0004) + " Move Speed)",
                    "&7- Vitality: &f" + profile.getVitalityPoints() + " &a(+" + String.format("%.1f", profile.getVitalityPoints() * 0.5) + " Max HP)",
                    "&7- Intelligence: &f" + profile.getIntelligencePoints() + " &a(+" + String.format("%.0f", profile.getIntelligencePoints() * maxManaBonusPerInt) + " Max Mana, +" + String.format("%.1f", profile.getIntelligencePoints() * regenBonusPerInt) + " Mana/s)",
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
                int lvl = Math.max(1, profile.getSkillLevel(skill.getName()));
                String keybind = getActiveKeybind(skill.getName());
                String effectDesc = getActiveEffectDescription(skill.getName(), lvl);
                activeLore.add("&6" + skill.getName() + " Lvl " + lvl + " &7[" + keybind + "]: &f" + effectDesc);
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
                int lvl = Math.max(1, profile.getSkillLevel(skill.getName()));
                String effectDesc = getPassiveEffectDescription(skill.getName(), lvl);
                passiveLore.add("&6" + skill.getName() + " Lvl " + lvl + ": &f" + effectDesc);
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

    private String getActiveKeybind(String name) {
        switch (name) {
            case "Cleave":
            case "Quick Shot":
            case "Arcane Missile":
            case "Holy Nova":
            case "Boomerang Throw":
            case "Laser DOT":
            case "Thunder Wave":
            case "Chain Lightning":
                return "F";
            case "Fireball":
            case "Aegis":
            case "Rejuvenation":
            case "Timber":
            case "Diggy Diggy Hole":
            case "Tunnel Vision":
            case "Transmutation":
            case "Water Wave":
                return "Shift+F";
            default:
                return "Unknown";
        }
    }

    private String getActiveEffectDescription(String name, int level) {
        switch (name) {
            case "Cleave":
                return String.format("Sweep attack dealing %.1f damage in %.1f blocks range.", 10.0 + (level - 1) * 2.0, 3.0 + (level - 1) * 0.2);
            case "Quick Shot":
                return String.format("Fires a rapid sequence of %d arrows.", 3 + (level - 1));
            case "Arcane Missile":
                return String.format("Shoots a magic missile dealing %.1f damage.", 8.0 + (level - 1) * 2.5);
            case "Fireball":
                return String.format("Explosive fireball dealing %.1f damage and igniting for %.1fs.", 12.0 + (level - 1) * 3.0, 2.0 + (level - 1) * 0.5);
            case "Timber":
                return String.format("Instantly breaks up to %d logs vertically.", 10 + (level - 1) * 3);
            case "Diggy Diggy Hole":
                return String.format("Instantly breaks up to %d dirt/gravel/sand blocks.", 10 + (level - 1) * 3);
            case "Tunnel Vision":
                return String.format("Instantly mines up to %d blocks forward in a 3x3 grid.", 10 + (level - 1) * 3);
            case "Rejuvenation":
                return String.format("AoE heals %.1f HP in %.1fm radius, or grows crops.", 6.0 + (level - 1) * 2.0, 4.0 + (level - 1) * 0.5);
            case "Aegis":
                return String.format("Grants invulnerability for %.1fs.", 10.0 + (level - 1) * 2.5);
            case "Holy Nova":
                return String.format("AoE heals allies and damages enemies for %.1f in %.1fm radius.", 8.0 + (level - 1) * 2.0, 4.0 + (level - 1) * 0.5);
            case "Transmutation":
                return String.format("Converts blocks within a %d block radius.", 1 + level);
            case "Boomerang Throw":
                return String.format("Throws your axe, dealing %.1f damage on path.", 8.0 + level * 2.0);
            case "Thunder Wave":
                return String.format("Strikes lightning on yourself, dealing %.1f damage and pushing enemies back.", 10.0 + level * 2.0);
            case "Laser DOT":
                return String.format("Channels a laser beam dealing %.1f damage per tick over 3s.", 2.0 + level * 0.5);
            case "Chain Lightning":
                return String.format("Hits facing target for %.1f damage and jumps up to %d times.", 10.0 + (level - 1) * 2.0, 1 + level / 10);
            case "Water Wave":
                return String.format("Deals %.1f damage, pushes back and creates temporary water.", 5.0 + level * 1.5);
            default:
                return "No effect description.";
        }
    }

    private String getPassiveEffectDescription(String name, int level) {
        switch (name) {
            case "Heavy Strike":
                return String.format("+%d%% Melee Damage", level);
            case "Swift Strike":
                return String.format("+%d%% Melee Speed", level);
            case "Precision":
                return String.format("+%d%% Critical Strike Chance", level);
            case "Critical Damage":
                return String.format("+%d%% Critical Strike Damage", level);
            case "Spell Echo":
                return String.format("+%d%% Mana Regeneration", level);
            case "Dense Armor":
                return String.format("+%d%% Damage Reduction", level);
            case "Bountiful Harvest":
                return String.format("+%d%% Double Crop Chance", level);
            case "Efficiency":
                return String.format("+%d%% Efficiency on tools", level);
            case "Hacker":
                return String.format("+%d%% Durability on tools", level);
            case "Repair":
                return String.format("+%d%% Mending & Repair amount", level);
            case "Power Surge":
                return String.format("+%d%% Vehicle/Mount Speed", level);
            case "Guardian":
                return String.format("+%d Max Hearts", level);
            case "Domain Lord":
                return String.format("+%d%% Fall Damage Reduction", level);
            case "Catalyst":
                return String.format("+%d%% Potion Duration", level);
            case "Master of the Craft":
                return "Doubles enchants and potions created";
            default:
                return "No effect description.";
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
