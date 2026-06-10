package com.here.hereroleplay.classes;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClassManager {

    private final HereRolePlay plugin;
    private final List<HrpClass> classes = new ArrayList<>();
    private final List<ActiveSkill> activeSkills = new ArrayList<>();
    private final List<PassiveSkill> passiveSkills = new ArrayList<>();

    public static class ActiveSkill {
        private final String name;
        private final String className;
        private final String trigger;
        private final int manaCost;
        private final String effect;

        public ActiveSkill(String name, String className, String trigger, int manaCost, String effect) {
            this.name = name;
            this.className = className;
            this.trigger = trigger;
            this.manaCost = manaCost;
            this.effect = effect;
        }

        public String getName() { return name; }
        public String getClassName() { return className; }
        public String getTrigger() { return trigger; }
        public int getManaCost() { return manaCost; }
        public String getEffect() { return effect; }
    }

    public static class PassiveSkill {
        private final String name;
        private final String className;
        private final String effect;

        public PassiveSkill(String name, String className, String effect) {
            this.name = name;
            this.className = className;
            this.effect = effect;
        }

        public String getName() { return name; }
        public String getClassName() { return className; }
        public String getEffect() { return effect; }
    }

    public ClassManager(HereRolePlay plugin) {
        this.plugin = plugin;
        registerDefaultClasses();
        registerSkills();
    }

    private void registerDefaultClasses() {
        // Base Classes
        classes.add(new HrpClass("Warrior", "Melee power, Armor scaling.", 20, 0, 20, 0, 40, Material.IRON_SWORD, false));
        classes.add(new HrpClass("Ranger", "Ranged Crit chance, Move speed.", 0, 20, 20, 0, 40, Material.BOW, false));
        classes.add(new HrpClass("Wizard", "Magic spells via Stick, Mana efficient.", 0, 0, 20, 20, 40, Material.BLAZE_ROD, false));
        classes.add(new HrpClass("Miner", "Excavation bursts, Toughness.", 20, 20, 0, 0, 40, Material.IRON_PICKAXE, false));
        classes.add(new HrpClass("Farmer", "AoE Healing, Resource yield.", 0, 20, 0, 20, 40, Material.IRON_HOE, false));
        classes.add(new HrpClass("Engineer", "Industrial mass production, Enchanting.", 20, 0, 0, 20, 40, Material.REDSTONE, false));
        
        // Hero Classes
        classes.add(new HrpClass("Paladin", "Pure active skill tank/healer (Aegis, Holy Nova).", 60, 0, 60, 60, 200, Material.SHIELD, true));
        classes.add(new HrpClass("Landlord", "World shaping (Transmutation).", 60, 60, 60, 0, 200, Material.GRASS_BLOCK, true));
        classes.add(new HrpClass("Alchemist", "Output doubling (Potions, Enchanting).", 0, 60, 60, 60, 200, Material.BREWING_STAND, true));
        
        // Admin
        classes.add(new HrpClass("Admin Class", "Unlocked by mastering everything.", 100, 100, 100, 100, 400, Material.COMMAND_BLOCK, true));
    }

    private void registerSkills() {
        // Warrior
        activeSkills.add(new ActiveSkill("Cleave", "Warrior", "Sword in hand, press [F]", 30, "Sweep attack dealing AoE damage."));
        passiveSkills.add(new PassiveSkill("Heavy Strike", "Warrior", "+1% melee damage per level"));
        passiveSkills.add(new PassiveSkill("Swift Strike", "Warrior", "+1% melee speed per level"));

        // Ranger
        activeSkills.add(new ActiveSkill("Quick Shot", "Ranger", "Bow in hand, press [F]", 20, "Fires a rapid sequence of arrows."));
        passiveSkills.add(new PassiveSkill("Precision", "Ranger", "+1% critical strike chance per level"));
        passiveSkills.add(new PassiveSkill("Critical Damage", "Ranger", "+1% critical strike damage per level"));

        // Wizard
        activeSkills.add(new ActiveSkill("Arcane Missile", "Wizard", "Stick in hand, press [F]", 15, "Shoots a magical missile dealing damage."));
        activeSkills.add(new ActiveSkill("Fireball", "Wizard", "Stick in hand, right click", 25, "Shoots an explosive fireball."));
        passiveSkills.add(new PassiveSkill("Spell Echo", "Wizard", "+1% mana regeneration rate per level"));

        // Miner
        activeSkills.add(new ActiveSkill("Timber", "Miner", "Axe in hand, Sneak + Right Click log", 20, "Instantly breaks columns of logs."));
        activeSkills.add(new ActiveSkill("Diggy Diggy Hole", "Miner", "Shovel in hand, Sneak + Right Click Dirt/Gravel/Sand", 20, "Instantly breaks blocks in a circle around the broken block."));
        activeSkills.add(new ActiveSkill("Tunnel Vision", "Miner", "Pickaxe in hand, Sneak + Right Click mine-able block", 20, "Instantly breaks blocks in a 3x3 square forward."));
        passiveSkills.add(new PassiveSkill("Dense Armor", "Miner", "+1% damage reduction per level"));

        // Farmer
        activeSkills.add(new ActiveSkill("Rejuvenation", "Farmer", "Hoe in hand, Sneak + Right Click", 25, "AoE apply bonemeal if near crops, otherwise heals."));
        passiveSkills.add(new PassiveSkill("Bountiful Harvest", "Farmer", "+1% double crop chance per level"));

        // Engineer
        passiveSkills.add(new PassiveSkill("Efficiency", "Engineer", "Applies additional Efficiency effect on tools used (+1% per level)"));
        passiveSkills.add(new PassiveSkill("Hacker", "Engineer", "Applies additional Durability on tools used (+1% per level)"));
        passiveSkills.add(new PassiveSkill("Repair", "Engineer", "Increases mending and repair amounts (+1% per level)"));
        passiveSkills.add(new PassiveSkill("Power Surge", "Engineer", "Increases vehicle/mount speed (+1% per level)"));

        // Paladin
        activeSkills.add(new ActiveSkill("Aegis", "Paladin", "Shield in hand, Sneak + Right Click", 40, "Invulnerability for a duration."));
        activeSkills.add(new ActiveSkill("Holy Nova", "Paladin", "Gold Sword in hand, press [F]", 35, "AoE healing for allies and damage to monsters."));
        passiveSkills.add(new PassiveSkill("Guardian", "Paladin", "+1 heart max health per level"));

        // Landlord
        activeSkills.add(new ActiveSkill("Transmutation", "Landlord", "Sneak + Right Click any blocks", 30, "Converts target block and nearby blocks."));
        passiveSkills.add(new PassiveSkill("Domain Lord", "Landlord", "+1% fall damage reduction per level"));

        // Alchemist
        passiveSkills.add(new PassiveSkill("Catalyst", "Alchemist", "+1% potion duration per level"));
        passiveSkills.add(new PassiveSkill("Master of the Craft", "Alchemist", "Doubles enchants and potions created"));
    }

    public List<ActiveSkill> getUnlockedActiveSkills(PlayerProfile profile) {
        List<ActiveSkill> unlocked = new ArrayList<>();
        boolean hasAdmin = profile.getUnlockedClasses().contains("Admin Class");
        for (ActiveSkill skill : activeSkills) {
            if (hasAdmin || (profile.getUnlockedClasses().contains(skill.getClassName()) && profile.getSkillLevel(skill.getName()) >= 1)) {
                unlocked.add(skill);
            }
        }
        return unlocked;
    }

    public List<PassiveSkill> getUnlockedPassiveSkills(PlayerProfile profile) {
        List<PassiveSkill> unlocked = new ArrayList<>();
        boolean hasAdmin = profile.getUnlockedClasses().contains("Admin Class");
        for (PassiveSkill skill : passiveSkills) {
            if (hasAdmin || (profile.getUnlockedClasses().contains(skill.getClassName()) && profile.getSkillLevel(skill.getName()) >= 1)) {
                unlocked.add(skill);
            }
        }
        return unlocked;
    }

    public List<HrpClass> getClasses() {
        return classes;
    }

    public List<ActiveSkill> getActiveSkillsForClass(String className) {
        List<ActiveSkill> list = new ArrayList<>();
        for (ActiveSkill skill : activeSkills) {
            if (skill.getClassName().equalsIgnoreCase(className)) {
                list.add(skill);
            }
        }
        return list;
    }

    public List<PassiveSkill> getPassiveSkillsForClass(String className) {
        List<PassiveSkill> list = new ArrayList<>();
        for (PassiveSkill skill : passiveSkills) {
            if (skill.getClassName().equalsIgnoreCase(className)) {
                list.add(skill);
            }
        }
        return list;
    }

    public void checkUnlocks(Player player) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        for (HrpClass hrpClass : classes) {
            // Only check classes they don't already have
            if (!profile.getUnlockedClasses().contains(hrpClass.getName())) {
                if (hrpClass.meetsRequirements(profile)) {
                    profile.addUnlockedClass(hrpClass.getName());
                    player.sendMessage(ChatColor.GOLD + "★ " + ChatColor.YELLOW + "You have unlocked the " + ChatColor.GOLD + hrpClass.getName() + ChatColor.YELLOW + " class!");
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                }
            }
        }
    }
}
