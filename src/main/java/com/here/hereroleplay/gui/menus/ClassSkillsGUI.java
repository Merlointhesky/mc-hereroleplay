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
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1.3f);
    }

    private void setupItems() {
        inventory.clear();
        slotToSkillName.clear();
        skillIsActive.clear();

        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Active skills centered on Row 2 (slots 9-17)
        if (!activeSkills.isEmpty()) {
            List<Integer> activeSlots = getCenteredSlots(9, activeSkills.size());
            for (int i = 0; i < activeSkills.size() && i < activeSlots.size(); i++) {
                ActiveSkill skill = activeSkills.get(i);
                int level = profile.getSkillLevel(skill.getName());
                int slot = activeSlots.get(i);
                inventory.setItem(slot, new ItemBuilder(getSkillIcon(skill.getName()))
                        .setName("&b&l" + skill.getName())
                        .setGlowing(level >= 1)
                        .setLore(getSkillLore(skill.getName(), level, true))
                        .build());
                slotToSkillName.put(slot, skill.getName());
                skillIsActive.put(skill.getName(), true);
            }
        }

        // Passive skills centered on Row 4 (slots 27-35)
        if (!passiveSkills.isEmpty()) {
            List<Integer> passiveSlots = getCenteredSlots(27, passiveSkills.size());
            for (int i = 0; i < passiveSkills.size() && i < passiveSlots.size(); i++) {
                PassiveSkill skill = passiveSkills.get(i);
                int level = profile.getSkillLevel(skill.getName());
                int slot = passiveSlots.get(i);
                inventory.setItem(slot, new ItemBuilder(getSkillIcon(skill.getName()))
                        .setName("&a&l" + skill.getName())
                        .setGlowing(level >= 1)
                        .setLore(getSkillLore(skill.getName(), level, false))
                        .build());
                slotToSkillName.put(slot, skill.getName());
                skillIsActive.put(skill.getName(), false);
            }
        }

        // Slot 48: Back button
        inventory.setItem(48, new ItemBuilder(Material.ARROW).setName("&cBack to Class Directory").build());

        // Slot 50: Current Points display
        inventory.setItem(50, new ItemBuilder(Material.BOOK)
                .setName("&bUnspent Points: &f" + profile.getUnspentSkillPoints())
                .setLore("&7Earn levels in Combat, Collect, and Craft", "&7to receive more skill points.")
                .build());

        // Fill remaining slots
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    private List<Integer> getCenteredSlots(int rowStart, int count) {
        List<Integer> slots = new ArrayList<>();
        int center = rowStart + 4;
        if (count == 1) {
            slots.add(center);
        } else if (count == 2) {
            slots.add(center - 1);
            slots.add(center + 1);
        } else if (count == 3) {
            slots.add(center - 2);
            slots.add(center);
            slots.add(center + 2);
        } else if (count == 4) {
            slots.add(center - 3);
            slots.add(center - 1);
            slots.add(center + 1);
            slots.add(center + 3);
        } else if (count == 5) {
            slots.add(center - 4);
            slots.add(center - 2);
            slots.add(center);
            slots.add(center + 2);
            slots.add(center + 4);
        } else {
            int startOffset = (9 - count) / 2;
            for (int i = 0; i < count && i < 9; i++) {
                slots.add(rowStart + startOffset + i);
            }
        }
        return slots;
    }

    private Material getSkillIcon(String skillName) {
        switch (skillName) {
            case "Cleave": return Material.IRON_SWORD;
            case "Heavy Strike": return Material.ANVIL;
            case "Swift Strike": return Material.FEATHER;
            case "Quick Shot": return Material.BOW;
            case "Precision": return Material.TARGET;
            case "Critical Damage": return Material.DIAMOND_SWORD;
            case "Rock Blast": return Material.COBBLESTONE;
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
            case "Holy Nova": return Material.SHIELD;
            case "Guardian": return Material.GOLDEN_APPLE;
            case "Transmutation": return Material.IRON_INGOT;
            case "Domain Lord": return Material.GRASS_BLOCK;
            case "Catalyst": return Material.GLISTERING_MELON_SLICE;
            case "Master of the Craft": return Material.BREWING_STAND;
            case "Boomerang Throw": return Material.IRON_AXE;
            case "Thunder Wave": return Material.MACE;
            case "Laser DOT": return Material.TRIDENT;
            case "Water Wave": return Material.WATER_BUCKET;
            case "Spear Knight": return Material.TRIDENT;
            case "Assassination": return Material.ENDER_PEARL;
            case "Piercing Bolt": return Material.SPECTRAL_ARROW;
            case "Recycle Bolt": return Material.ARROW;
            case "Quicksand": return Material.SAND;
            case "Fire Rain": return Material.FIRE_CHARGE;
            case "Water Cannon": return Material.PRISMARINE_SHARD;
            case "Wind Blast": return Material.BREEZE_ROD;
            case "Gale Force": return Material.WIND_CHARGE;
            case "Fertilizer": return Material.BONE_MEAL;
            case "Iron Resolve": return Material.ANVIL;
            case "Raise Undead": return Material.BONE;
            case "Soul Drain": return Material.WITHER_SKELETON_SKULL;
            case "Deathly Rejuvenation": return Material.GHAST_TEAR;
            default: return Material.BOOK;
        }
    }

    private int getUpgradeCost(String skillName, int currentLvl, boolean isActive) {
        if (currentLvl == 0) {
            if (skillName.equals("Aegis") || skillName.equals("Holy Nova") || 
                skillName.equals("Transmutation")) {
                return 40;
            }
            if (skillName.equals("Master of the Craft")) {
                return 60;
            }
            return isActive ? 5 : 1;
        }
        return 1;
    }

    private int getMaxLevel(String skillName) {
        if (skillName.equals("Master of the Craft")) {
            return 1;
        }
        return 100;
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
            case "Spear Knight":
                lore.add("&7Charges forward on your horse, trampling mobs.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (10.0 + currentLvl * 2.0));
                    lore.add("&7- Action: &fTrample charge (mounted)");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (10.0 + (currentLvl + 1) * 2.0));
                break;
            case "Assassination":
                lore.add("&7Teleports behind closest target, critical strike.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Range: &f" + String.format("%.1f", 8.0 + (currentLvl - 1) * 0.2) + " blocks");
                    lore.add("&7- Damage: &fCrit based on weapon & Critical Damage passive");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Range: &f" + String.format("%.1f", 8.0 + currentLvl * 0.2) + " blocks");
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
            case "Piercing Bolt":
                lore.add("&7Fires a high-velocity piercing bolt in a line.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (12.0 + (currentLvl - 1) * 2.0));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (12.0 + currentLvl * 2.0));
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
            case "Recycle Bolt":
                lore.add("&7Chance to not consume crossbow arrows.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Save Chance: &f" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Save Chance: &f" + (currentLvl + 1) + "%");
                break;
            case "Rock Blast":
                lore.add("&7Shoots a high-impact rock projectile.");
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
            case "Quicksand":
                lore.add("&7Turns landing block and area into sand, slowing mobs.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Radius: &f" + (1 + currentLvl / 5) + " blocks");
                    lore.add("&7- CC Duration: &f5.0s (Slowness III)");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Radius: &f" + (1 + (currentLvl + 1) / 5) + " blocks");
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
            case "Fire Rain":
                lore.add("&7Channels falling fire around player, dealing splash damage.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage per fall: &f" + (4.0 + currentLvl * 0.5));
                    lore.add("&7- Duration: &f3.0s");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage per fall: &f" + (4.0 + (currentLvl + 1) * 0.5));
                break;
            case "Water Cannon":
                lore.add("&7Projects water forward, pushing targets, stops jumping.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Push force: &f" + String.format("%.2f", 1.0 + currentLvl * 0.1));
                    lore.add("&7- Debuff: &fNo-Jump for 5s");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Push force: &f" + String.format("%.2f", 1.0 + (currentLvl + 1) * 0.1));
                break;
            case "Water Wave":
                lore.add("&7Creates a water barrier, pushing back and damaging enemies.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (5.0 + currentLvl * 1.5));
                    lore.add("&7- Push Force: &f" + (1.0 + currentLvl * 0.15));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (5.0 + (currentLvl + 1) * 1.5));
                lore.add("&7- Push Force: &f" + (1.0 + (currentLvl + 1) * 0.15));
                break;
            case "Wind Blast":
                lore.add("&7Launches an explosive wind charge projectile.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (8.0 + (currentLvl - 1) * 2.0));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (8.0 + currentLvl * 2.0));
                break;
            case "Gale Force":
                lore.add("&7Launches the player upward, knocking back nearby mobs.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Launch height: &f" + String.format("%.2f", 1.0 + currentLvl * 0.05));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Launch height: &f" + String.format("%.2f", 1.0 + (currentLvl + 1) * 0.05));
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
            case "Fertilizer":
                lore.add("&7Crops in a 5-block radius grow faster.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Growth Chance: &f" + currentLvl + "% every 4s");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Growth Chance: &f" + (currentLvl + 1) + "% every 4s");
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
            case "Iron Resolve":
                lore.add("&7Grants raw knockback resistance.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Knockback Resist: &f+" + currentLvl + "%");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Knockback Resist: &f+" + (currentLvl + 1) + "%");
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
                    lore.add("");
                    lore.add("&aMAX LEVEL");
                } else {
                    lore.add("&cLocked (Requires 60 Skill Points)");
                    lore.add("");
                    lore.add("&bNext Level Preview:");
                    lore.add("&7- Effect: &fActive (Doubles output)");
                }
                break;
            case "Boomerang Throw":
                lore.add("&7Throws your axe like a boomerang, hitting enemies.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (8.0 + currentLvl * 2.0));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (8.0 + (currentLvl + 1) * 2.0));
                break;
            case "Thunder Wave":
                lore.add("&7Calls lightning on yourself, pushing back and damaging nearby enemies.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage: &f" + (10.0 + currentLvl * 2.0));
                    lore.add("&7- Radius: &f" + (5.0 + currentLvl * 0.5) + " blocks");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage: &f" + (10.0 + (currentLvl + 1) * 2.0));
                lore.add("&7- Radius: &f" + (5.0 + (currentLvl + 1) * 0.5) + " blocks");
                break;
            case "Laser DOT":
                lore.add("&7Channels a laser beam that deals damage over time.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage Per Tick: &f" + (2.0 + currentLvl * 0.5));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage Per Tick: &f" + (2.0 + (currentLvl + 1) * 0.5));
                break;
            case "Raise Undead":
                lore.add("&7Summons friendly skeleton cohorts to fight mobs.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Skeleton Count: &f" + (1 + currentLvl / 10));
                    lore.add("&7- Summon Duration: &f30s");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Skeleton Count: &f" + (1 + (currentLvl + 1) / 10));
                break;
            case "Soul Drain":
                lore.add("&7Channels health from your active skeletons to heal yourself.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Tick damage/heal: &f" + String.format("%.1f", (2.0 + currentLvl * 0.5) / 2.0) + " per 0.5s");
                    lore.add("&7- Channel Duration: &f3s");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Tick damage/heal: &f" + String.format("%.1f", (2.0 + (currentLvl + 1) * 0.5) / 2.0) + " per 0.5s");
                break;
            case "Deathly Rejuvenation":
                lore.add("&7Passive health regeneration.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                lore.add("&7- Regeneration: &f+" + String.format("%.2f", currentLvl * 0.01) + " HP/s");
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Regeneration: &f+" + String.format("%.2f", (currentLvl + 1) * 0.01) + " HP/s");
                break;
        }
        
        int maxLvl = getMaxLevel(skillName);
        if (currentLvl >= maxLvl) {
            int previewIndex = -1;
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).contains("Next Level Preview")) {
                    previewIndex = i;
                    break;
                }
            }
            if (previewIndex != -1) {
                if (previewIndex > 0 && lore.get(previewIndex - 1).trim().isEmpty()) {
                    previewIndex--;
                }
                while (lore.size() > previewIndex) {
                    lore.remove(previewIndex);
                }
            }
            lore.add("");
            lore.add("&aMaximum level reached!");
            return lore;
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
        if (slot == 48) {
            player.openInventory(new ClassDirectoryGUI(plugin, player).getInventory());
            return;
        }

        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        String skillToUpgrade = slotToSkillName.get(slot);
        if (skillToUpgrade != null) {
            boolean isActive = skillIsActive.get(skillToUpgrade);
            int currentLevel = profile.getSkillLevel(skillToUpgrade);
            int maxLevel = getMaxLevel(skillToUpgrade);

            if (currentLevel >= maxLevel) {
                player.sendMessage("§c" + skillToUpgrade + " is already at its maximum level (Level " + maxLevel + ")!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

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
