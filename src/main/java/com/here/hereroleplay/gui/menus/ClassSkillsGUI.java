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
import java.util.List;

public class ClassSkillsGUI implements CustomGUI {

    private final HereRolePlay plugin;
    private final Player player;
    private final HrpClass hrpClass;
    private final Inventory inventory;

    private final List<ActiveSkill> activeSkills;
    private final List<PassiveSkill> passiveSkills;

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
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Slot 4: Info Book
        inventory.setItem(4, new ItemBuilder(Material.BOOK)
                .setName("&b" + hrpClass.getName() + " Skills")
                .setLore(
                    "&7Unspent Points: &f" + profile.getUnspentSkillPoints(),
                    "",
                    "&7Upgrade your active abilities (Cleave,",
                    "&7Arcane Missile, etc.) and passive buffs."
                ).build());

        // Slots for Active Skills (Slot 20, or 20 & 22)
        if (activeSkills.size() == 1) {
            ActiveSkill skill = activeSkills.get(0);
            int level = profile.getSkillLevel(skill.getName());
            inventory.setItem(20, new ItemBuilder(getSkillIcon(skill.getName()))
                    .setName("&b&l" + skill.getName())
                    .setGlowing(level >= 1)
                    .setLore(getSkillLore(skill.getName(), level, true))
                    .build());
        } else if (activeSkills.size() >= 2) {
            ActiveSkill skill1 = activeSkills.get(0);
            int level1 = profile.getSkillLevel(skill1.getName());
            inventory.setItem(20, new ItemBuilder(getSkillIcon(skill1.getName()))
                    .setName("&b&l" + skill1.getName())
                    .setGlowing(level1 >= 1)
                    .setLore(getSkillLore(skill1.getName(), level1, true))
                    .build());

            ActiveSkill skill2 = activeSkills.get(1);
            int level2 = profile.getSkillLevel(skill2.getName());
            inventory.setItem(22, new ItemBuilder(getSkillIcon(skill2.getName()))
                    .setName("&b&l" + skill2.getName())
                    .setGlowing(level2 >= 1)
                    .setLore(getSkillLore(skill2.getName(), level2, true))
                    .build());
        }

        // Slot for Passive Skill (Slot 24)
        if (!passiveSkills.isEmpty()) {
            PassiveSkill skill = passiveSkills.get(0);
            int level = profile.getSkillLevel(skill.getName());
            inventory.setItem(24, new ItemBuilder(getSkillIcon(skill.getName()))
                    .setName("&a&l" + skill.getName())
                    .setGlowing(level >= 1)
                    .setLore(getSkillLore(skill.getName(), level, false))
                    .build());
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
            case "Quick Shot": return Material.BOW;
            case "Precision": return Material.TARGET;
            case "Arcane Missile": return Material.BLAZE_ROD;
            case "Fireball": return Material.MAGMA_CREAM;
            case "Spell Echo": return Material.LAPIS_LAZULI;
            case "Timber": return Material.IRON_AXE;
            case "Dense Armor": return Material.IRON_CHESTPLATE;
            case "Rejuvenation": return Material.GOLDEN_HOE;
            case "Bountiful Harvest": return Material.WHEAT;
            case "Overload": return Material.REDSTONE_TORCH;
            case "Efficiency": return Material.SUGAR;
            case "Aegis": return Material.SHIELD;
            case "Holy Nova": return Material.GOLDEN_SWORD;
            case "Guardian": return Material.GOLDEN_APPLE;
            case "Transmutation": return Material.IRON_INGOT;
            case "Domain Lord": return Material.GRASS_BLOCK;
            case "Brew Burst": return Material.BREWING_STAND;
            case "Catalyst": return Material.GLISTERING_MELON_SLICE;
            default: return Material.BOOK;
        }
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
                lore.add("&7Increases melee attack damage.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Melee Damage: &f+" + (20 + (currentLvl - 1) * 5) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Melee Damage: &f+" + (20 + currentLvl * 5) + "%");
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
                lore.add("&7Chance to deal 1.5x critical damage.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Crit Chance: &f" + (15 + (currentLvl - 1) * 3) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Crit Chance: &f" + (15 + currentLvl * 3) + "%");
                break;
            case "Arcane Missile":
                lore.add("&7Shoots a magical missile projectile.");
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
                lore.add("&7Explosive fireball projectile dealing AoE fire damage.");
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
                lore.add("&7Increases mana regeneration rate.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Mana Regen: &f+" + (20 + (currentLvl - 1) * 5) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Mana Regen: &f+" + (20 + currentLvl * 5) + "%");
                break;
            case "Timber":
                lore.add("&7Instantly break columns of logs.");
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
                lore.add("&7Flat defense boost.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Armor Points: &f+" + (5.0 + (currentLvl - 1) * 1.5));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Armor Points: &f+" + (5.0 + currentLvl * 1.5));
                break;
            case "Rejuvenation":
                lore.add("&7AoE heal self and nearby allies.");
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
                lore.add("&7Chance for double crops on break.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Double Yield Chance: &f" + (25 + (currentLvl - 1) * 5) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Double Yield Chance: &f" + (25 + currentLvl * 5) + "%");
                break;
            case "Overload":
                lore.add("&7Gains speed burst.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Duration: &f" + (5.0 + (currentLvl - 1) * 1.5) + "s");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Duration: &f" + (5.0 + currentLvl * 1.5) + "s");
                break;
            case "Efficiency":
                lore.add("&7Boosts movement speed.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Speed Boost: &f+" + (10 + (currentLvl - 1) * 2.5) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Speed Boost: &f+" + (10 + currentLvl * 2.5) + "%");
                break;
            case "Aegis":
                lore.add("&7Invulnerability block for a duration.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Aegis Duration: &f" + (10.0 + (currentLvl - 1) * 2.5) + "s");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Aegis Duration: &f" + (10.0 + currentLvl * 2.5) + "s");
                break;
            case "Holy Nova":
                lore.add("&7AoE healing for allies and damage to monsters.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Damage/Heal: &f" + (8.0 + (currentLvl - 1) * 2.0));
                    lore.add("&7- Radius: &f" + (4.0 + (currentLvl - 1) * 0.5) + " blocks");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Damage/Heal: &f" + (8.0 + currentLvl * 2.0));
                lore.add("&7- Radius: &f" + (4.0 + currentLvl * 0.5) + " blocks");
                break;
            case "Guardian":
                lore.add("&7Increases player maximum health.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Max Health: &f+" + (20 + (currentLvl - 1) * 5) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Max Health: &f+" + (20 + currentLvl * 5) + "%");
                break;
            case "Transmutation":
                lore.add("&7Turns stone/cobblestone into random ores.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Coal Chance: &f" + Math.max(0, 60 - (currentLvl - 1) * 5) + "%");
                    lore.add("&7- Iron Chance: &f" + (30 + (currentLvl - 1) * 3) + "%");
                    lore.add("&7- Gold Chance: &f" + (10 + (currentLvl - 1) * 1) + "%");
                    if (currentLvl >= 3) {
                        lore.add("&7- Diamond Chance: &f" + (5 + (currentLvl - 3) * 3) + "%");
                    }
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Coal Chance: &f" + Math.max(0, 60 - currentLvl * 5) + "%");
                lore.add("&7- Iron Chance: &f" + (30 + currentLvl * 3) + "%");
                lore.add("&7- Gold Chance: &f" + (10 + currentLvl * 1) + "%");
                if (currentLvl + 1 >= 3) {
                    lore.add("&7- Diamond Chance: &f" + (5 + ((currentLvl + 1) - 3) * 3) + "%");
                }
                break;
            case "Domain Lord":
                lore.add("&7Reduces fall damage.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Fall Damage Reduction: &f" + Math.min(100, 50 + (currentLvl - 1) * 10) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Fall Damage Reduction: &f" + Math.min(100, 50 + currentLvl * 10) + "%");
                break;
            case "Brew Burst":
                lore.add("&7Throws Regeneration splash potions.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Regen Duration: &f" + (6 + (currentLvl - 1) * 2) + "s");
                    lore.add("&7- Regen Tier: &f" + (currentLvl >= 4 ? "II" : "I"));
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Regen Duration: &f" + (6 + currentLvl * 2) + "s");
                lore.add("&7- Regen Tier: &f" + (currentLvl + 1 >= 4 ? "II" : "I"));
                break;
            case "Catalyst":
                lore.add("&7Increases applied potion durations.");
                lore.add("");
                lore.add("&bCurrent Effect:");
                if (currentLvl > 0) {
                    lore.add("&7- Potion Duration: &f+" + (30 + (currentLvl - 1) * 10) + "%");
                } else {
                    lore.add("&cLocked");
                }
                lore.add("");
                lore.add("&bNext Level Preview:");
                lore.add("&7- Potion Duration: &f+" + (30 + currentLvl * 10) + "%");
                break;
        }
        
        lore.add("");
        int cost = currentLvl == 0 && isActive ? 5 : 1;
        lore.add("&eCost to " + (currentLvl == 0 ? "Unlock" : "Upgrade") + ": &f" + cost + " Skill Point" + (cost > 1 ? "s" : ""));
        lore.add("&aClick to purchase!");
        
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

        String skillToUpgrade = null;
        boolean isActive = false;

        if (activeSkills.size() == 1 && slot == 20) {
            skillToUpgrade = activeSkills.get(0).getName();
            isActive = true;
        } else if (activeSkills.size() >= 2) {
            if (slot == 20) {
                skillToUpgrade = activeSkills.get(0).getName();
                isActive = true;
            } else if (slot == 22) {
                skillToUpgrade = activeSkills.get(1).getName();
                isActive = true;
            }
        }

        if (slot == 24 && !passiveSkills.isEmpty()) {
            skillToUpgrade = passiveSkills.get(0).getName();
            isActive = false;
        }

        if (skillToUpgrade != null) {
            int currentLevel = profile.getSkillLevel(skillToUpgrade);
            int cost = (currentLevel == 0 && isActive) ? 5 : 1;

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
