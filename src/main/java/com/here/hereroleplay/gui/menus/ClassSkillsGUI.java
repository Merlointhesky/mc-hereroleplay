package com.here.hereroleplay.gui.menus;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.classes.HrpClass;
import com.here.hereroleplay.classes.ClassManager.ActiveSkill;
import com.here.hereroleplay.classes.ClassManager.PassiveSkill;
import com.here.hereroleplay.data.PlayerProfile;
import com.here.hereroleplay.gui.CustomGUI;
import com.here.hereroleplay.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassSkillsGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final HrpClass hrpClass;
    private final Inventory inventory;

    private final List<ActiveSkill> activeSkills;
    private final List<PassiveSkill> passiveSkills;

    private final Map<Integer, String> slotToSkillName = new HashMap<>();
    private final Map<String, Boolean> skillIsActive = new HashMap<>();

    public ClassSkillsGUI(HereRolePlay plugin, Player player, HrpClass hrpClass) {
        this.plugin = plugin;
        this.player = player;
        this.hrpClass = hrpClass;
        this.inventory = Bukkit.createInventory(this, 54, "§8" + hrpClass.getName() + " Skills");

        this.activeSkills = plugin.getClassManager().getActiveSkillsForClass(hrpClass.getName());
        this.passiveSkills = plugin.getClassManager().getPassiveSkillsForClass(hrpClass.getName());

        setupItems();
    }

    private void setupItems() {
        inventory.clear();
        slotToSkillName.clear();
        skillIsActive.clear();

        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Slot 4: Info Book
        inventory.setItem(4, new ItemBuilder(Material.BOOK)
                .setName("&b" + hrpClass.getName() + " Skills")
                .setLore(
                    "&7Unspent Points: &f" + profile.getUnspentSkillPoints(),
                    "",
                    "&7Allocate points to unlock and upgrade",
                    "&7abilities and passive buffs."
                ).build());

        // Header for Active Skills if there are any
        if (!activeSkills.isEmpty()) {
            inventory.setItem(13, new ItemBuilder(Material.COMPASS)
                    .setName("§b§lActive Abilities")
                    .setLore("§7These are triggered by actions in combat or gathering.")
                    .build());

            // Active slots: 1 -> 22; 2 -> 21, 23; 3 -> 20, 22, 24
            int[] activeSlots;
            if (activeSkills.size() == 1) {
                activeSlots = new int[]{22};
            } else if (activeSkills.size() == 2) {
                activeSlots = new int[]{21, 23};
            } else {
                activeSlots = new int[]{20, 22, 24};
            }

            for (int i = 0; i < activeSkills.size() && i < activeSlots.length; i++) {
                ActiveSkill skill = activeSkills.get(i);
                int level = profile.getSkillLevel(skill.getName());
                int slot = activeSlots[i];
                inventory.setItem(slot, new ItemBuilder(getSkillIcon(skill.getName()))
                        .setName("&b&l" + skill.getName())
                        .setGlowing(level >= 1)
                        .setLore(getSkillLore(skill.getName(), level, true))
                        .build());
                slotToSkillName.put(slot, skill.getName());
                skillIsActive.put(skill.getName(), true);
            }
        }

        // Header for Passive Skills if there are any
        if (!passiveSkills.isEmpty()) {
            inventory.setItem(31, new ItemBuilder(Material.BOOKSHELF)
                    .setName("§a§lPassive Buffs")
                    .setLore("§7These provide constant passive enhancements.")
                    .build());

            // Passive slots: 1 -> 40; 2 -> 39, 41; 3 -> 38, 40, 42; 4 -> 37, 39, 41, 43
            int[] passiveSlots;
            if (passiveSkills.size() == 1) {
                passiveSlots = new int[]{40};
            } else if (passiveSkills.size() == 2) {
                passiveSlots = new int[]{39, 41};
            } else if (passiveSkills.size() == 3) {
                passiveSlots = new int[]{38, 40, 42};
            } else {
                passiveSlots = new int[]{37, 39, 41, 43};
            }

            for (int i = 0; i < passiveSkills.size() && i < passiveSlots.length; i++) {
                PassiveSkill skill = passiveSkills.get(i);
                int level = profile.getSkillLevel(skill.getName());
                int slot = passiveSlots[i];
                inventory.setItem(slot, new ItemBuilder(getSkillIcon(skill.getName()))
                        .setName("&a&l" + skill.getName())
                        .setGlowing(level >= 1)
                        .setLore(getSkillLore(skill.getName(), level, false))
                        .build());
                slotToSkillName.put(slot, skill.getName());
                skillIsActive.put(skill.getName(), false);
            }
        }

        // Slot 49: Back button
        inventory.setItem(49, new ItemBuilder(Material.ARROW).setName("&cBack").build());

        // Fill remaining slots
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    private Material getSkillIcon(String skillName) {
        switch (skillName) {
            case "Cleave": return Material.IRON_SWORD;
            case "Heavy Strike": return Material.ANVIL;
            case "Swift Strike": return Material.FEATHER;
            case "Quick Shot": return Material.BOW;
            case "Precision": return Material.TARGET;
            case "Critical Damage": return Material.DIAMOND_SWORD;
            case "Arcane Missile": return Material.STICK;
            case "Fireball": return Material.MAGMA_CREAM;
            case "Spell Echo": return Material.LAPIS_LAZULI;
            case "Timber": return Material.IRON_AXE;
            case "Diggy Diggy Hole": return Material.IRON_SHOVEL;
            case "Tunnel Vision": return Material.IRON_PICKAXE;
            case "Dense Armor": return Material.IRON_CHESTPLATE;
            case "Rejuvenation": return Material.GOLDEN_HOE;
            case "Bountiful Harvest": return Material.WHEAT;
            case "Efficiency": return Material.SUGAR;
            case "Hacker": return Material.SHIELD;
            case "Repair": return Material.EXPERIENCE_BOTTLE;
            case "Power Surge": return Material.MINECART;
            case "Aegis": return Material.SHIELD;
            case "Holy Nova": return Material.GOLDEN_SWORD;
            case "Guardian": return Material.GOLDEN_APPLE;
            case "Transmutation": return Material.IRON_INGOT;
            case "Domain Lord": return Material.GRASS_BLOCK;
            case "Catalyst": return Material.GLISTERING_MELON_SLICE;
            case "Master of the Craft": return Material.BREWING_STAND;
            default: return Material.BOOK;
        }
    }

    private int getUpgradeCost(String skillName, int currentLvl, boolean isActive) {
        if (currentLvl == 0) {
            if (skillName.equals("Aegis") || skillName.equals("Holy Nova") || 
                skillName.equals("Transmutation") || skillName.equals("Master of the Craft")) {
                return 40;
            }
            return isActive ? 5 : 1;
        }
        return 1;
    }

    private List<String> getSkillLore(String skillName, int currentLvl, boolean isActive) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Type: " + (isActive ? "&bActive" : "&aPassive"));
        lore.add("&7Current Level: &e" + currentLvl);
        lore.add("");
        
        lore.add("&eDescription:");
        switch (skillName) {
            case "Cleave":
                lore.add("&7Sweep attack dealing AoE damage.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (10.0 + (currentLvl - 1) * 2.0));
                    lore.add("&7- Range: &f" + (3.0 + (currentLvl - 1) * 0.2) + " blocks");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (10.0 + currentLvl * 2.0));
                lore.add("&7- Range: &f" + (3.0 + currentLvl * 0.2) + " blocks");
                break;
            case "Heavy Strike":
                lore.add("&7Melee damage boost.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Melee Damage: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Melee Damage: &f+" + (currentLvl + 1) + "%");
                break;
            case "Swift Strike":
                lore.add("&7Melee speed boost. Decrease swing cooldown.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Melee Speed: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Melee Speed: &f+" + (currentLvl + 1) + "%");
                break;
            case "Quick Shot":
                lore.add("&7Fires a rapid sequence of arrows.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Arrows Fired: &f" + (3 + (currentLvl - 1)));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Arrows Fired: &f" + (3 + currentLvl));
                break;
            case "Precision":
                lore.add("&7Chance to land a critical strike.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Crit Chance: &f" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Crit Chance: &f" + (currentLvl + 1) + "%");
                break;
            case "Critical Damage":
                lore.add("&7Critical Damage boost on critical hits.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Critical Damage Boost: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Critical Damage Boost: &f+" + (currentLvl + 1) + "%");
                break;
            case "Arcane Missile":
                lore.add("&7Shoots a magical missile dealing damage on impact.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (8.0 + (currentLvl - 1) * 2.5));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (8.0 + currentLvl * 2.5));
                break;
            case "Fireball":
                lore.add("&7Shoots an explosive fireball that damages/ignites.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (12.0 + (currentLvl - 1) * 3.0));
                    lore.add("&7- Ignite Duration: &f" + (2.0 + (currentLvl - 1) * 0.5) + "s");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (12.0 + currentLvl * 3.0));
                lore.add("&7- Ignite Duration: &f" + (2.0 + currentLvl * 0.5) + "s");
                break;
            case "Spell Echo":
                lore.add("&7Mana regeneration boost.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Mana Regen: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Mana Regen: &f+" + (currentLvl + 1) + "%");
                break;
            case "Timber":
                lore.add("&7Instantly breaks columns of logs.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Max Blocks Broken: &f" + (10 + (currentLvl - 1) * 3));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Max Blocks Broken: &f" + (10 + currentLvl * 3));
                break;
            case "Diggy Diggy Hole":
                lore.add("&7Instantly breaks blocks in a circle/sphere.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Max Blocks Broken: &f" + (10 + (currentLvl - 1) * 3));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Max Blocks Broken: &f" + (10 + currentLvl * 3));
                break;
            case "Tunnel Vision":
                lore.add("&7Instantly breaks blocks in a 3x3 square column forward.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Max Blocks Broken: &f" + (10 + (currentLvl - 1) * 3));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Max Blocks Broken: &f" + (10 + currentLvl * 3));
                break;
            case "Dense Armor":
                lore.add("&7Direct damage reduction.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Damage Reduction: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage Reduction: &f+" + (currentLvl + 1) + "%");
                break;
            case "Rejuvenation":
                lore.add("&7AoE apply bonemeal if near crops, otherwise heals.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Healing: &f" + (6.0 + (currentLvl - 1) * 2.0));
                    lore.add("&7- Radius: &f" + (4.0 + (currentLvl - 1) * 0.5) + " blocks");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Healing: &f" + (6.0 + currentLvl * 2.0));
                lore.add("&7- Radius: &f" + (4.0 + currentLvl * 0.5) + " blocks");
                break;
            case "Bountiful Harvest":
                lore.add("&7Chance for double crops on harvest.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Double Crop Chance: &f" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Double Crop Chance: &f" + (currentLvl + 1) + "%");
                break;
            case "Efficiency":
                lore.add("&7Applies additional Efficiency effect on tools.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Efficiency Bonus: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Efficiency Bonus: &f+" + (currentLvl + 1) + "%");
                break;
            case "Hacker":
                lore.add("&7Reduces durability loss on tools used.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Durability Saver: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Durability Saver: &f+" + (currentLvl + 1) + "%");
                break;
            case "Repair":
                lore.add("&7Increases repair amounts and Mending effects.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Repair Modifier: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Repair Modifier: &f+" + (currentLvl + 1) + "%");
                break;
            case "Power Surge":
                lore.add("&7Increases speed on movement with vehicles/mounts.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Vehicle Speed Bonus: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Vehicle Speed Bonus: &f+" + (currentLvl + 1) + "%");
                break;
            case "Aegis":
                lore.add("&7Invulnerability (Resistance X) for a duration.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Duration: &f" + (10.0 + (currentLvl - 1) * 2.5) + "s");
                } else {
                    lore.add("&cLocked (Requires 40 Skill Points)");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Duration: &f" + (10.0 + currentLvl * 2.5) + "s");
                break;
            case "Holy Nova":
                lore.add("&7AoE healing for allies and damage to monsters.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Heal/Damage: &f" + (8.0 + (currentLvl - 1) * 2.0));
                    lore.add("&7- Radius: &f" + (4.0 + (currentLvl - 1) * 0.5) + " blocks");
                } else {
                    lore.add("&cLocked (Requires 40 Skill Points)");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Heal/Damage: &f" + (8.0 + currentLvl * 2.0));
                lore.add("&7- Radius: &f" + (4.0 + currentLvl * 0.5) + " blocks");
                break;
            case "Guardian":
                lore.add("&7Max health boost (+1 heart/2.0 HP per level).");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Max Health: &f+" + currentLvl + " Hearts (" + (currentLvl * 2.0) + " HP)");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Max Health: &f+" + (currentLvl + 1) + " Hearts (" + ((currentLvl + 1) * 2.0) + " HP)");
                break;
            case "Transmutation":
                lore.add("&7Converts target blocks into the held block.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Sphere Radius: &f" + (1 + currentLvl) + " blocks");
                } else {
                    lore.add("&cLocked (Requires 40 Skill Points)");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Sphere Radius: &f" + (1 + (currentLvl + 1)) + " blocks");
                break;
            case "Domain Lord":
                lore.add("&7Reduces fall damage.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Fall Damage Reduction: &f" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Fall Damage Reduction: &f" + (currentLvl + 1) + "%");
                break;
            case "Catalyst":
                lore.add("&7Increases applied potion durations.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Potion Duration: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Potion Duration: &f+" + (currentLvl + 1) + "%");
                break;
            case "Master of the Craft":
                lore.add("&7Doubles enchants and potions created.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Effect: &fActive (Doubles output)");
                } else {
                    lore.add("&cLocked (Requires 40 Skill Points)");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Effect: &fActive (Doubles output)");
                break;
        }
        
        lore.add("");
        int cost = getUpgradeCost(skillName, currentLvl, isActive);
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        int available = profile != null ? profile.getUnspentSkillPoints() : 0;
        boolean canAfford = available >= cost;
        lore.add("&eCost to " + (currentLvl == 0 ? "Unlock" : "Upgrade") + ": " + (canAfford ? "&a" : "&c") + cost + " Skill Point" + (cost > 1 ? "s" : ""));
        lore.add("&eAvailable Points: " + (canAfford ? "&a" : "&7") + available);
        if (canAfford) {
            lore.add("&aClick to purchase!");
        } else {
            lore.add("&cNot enough skill points!");
        }
        
        return lore;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 49) {
            player.openInventory(new ClassDirectoryGUI(plugin, player).getInventory());
            return;
        }

        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        String skillToUpgrade = slotToSkillName.get(slot);
        if (skillToUpgrade != null) {
            boolean isActive = skillIsActive.get(skillToUpgrade);
            int currentLevel = profile.getSkillLevel(skillToUpgrade);
            int cost = getUpgradeCost(skillToUpgrade, currentLevel, isActive);

            if (profile.getUnspentSkillPoints() >= cost) {
                profile.setUnspentSkillPoints(profile.getUnspentSkillPoints() - cost);
                profile.setSkillLevel(skillToUpgrade, currentLevel + 1);

                player.sendMessage("§a★ Successfully leveled up §6" + skillToUpgrade + "§a to Level §e" + (currentLevel + 1) + "§a!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);

                // Save database profile
                plugin.getDatabaseManager().saveProfile(profile);

                // Apply attributes in case passive changes player attributes
                plugin.getAttributeManager().applyAttributes(player);

                // Redraw
                setupItems();
            } else {
                player.sendMessage("§cYou do not have enough unspent skill points! (Requires " + cost + ")");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
