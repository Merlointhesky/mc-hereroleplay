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
        activeSkills.add(new ActiveSkill("Cleave", "Warrior", "Press [F] with Sword", 30, "Sweep attack dealing AoE damage."));
        passiveSkills.add(new PassiveSkill("Heavy Strike", "Warrior", "+20% Melee Damage"));

        // Ranger
        activeSkills.add(new ActiveSkill("Quick Shot", "Ranger", "Swap Hand/Right Click with Bow", 20, "Rapid arrows firing."));
        passiveSkills.add(new PassiveSkill("Precision", "Ranger", "+15% Crit Chance"));

        // Wizard
        activeSkills.add(new ActiveSkill("Arcane Missile", "Wizard", "Press [F] with Stick", 15, "Shoot magical missile projectile."));
        activeSkills.add(new ActiveSkill("Fireball", "Wizard", "Press [F] with Blaze Rod", 25, "Shoot explosive fireball projectile."));
        passiveSkills.add(new PassiveSkill("Spell Echo", "Wizard", "+20% Mana Regeneration"));

        // Miner
        activeSkills.add(new ActiveSkill("Timber", "Miner", "Shift-Right Click Log with Axe", 20, "Instantly break column of logs."));
        passiveSkills.add(new PassiveSkill("Dense Armor", "Miner", "+5 Armor points"));

        // Farmer
        activeSkills.add(new ActiveSkill("Rejuvenation", "Farmer", "Shift-Right Click with Hoe", 25, "AoE heal self and nearby entities."));
        passiveSkills.add(new PassiveSkill("Bountiful Harvest", "Farmer", "+25% double crop yield"));

        // Engineer
        activeSkills.add(new ActiveSkill("Overload", "Engineer", "Right Click with Redstone", 15, "Gain speed burst."));
        passiveSkills.add(new PassiveSkill("Efficiency", "Engineer", "+10% Movement Speed"));

        // Paladin
        activeSkills.add(new ActiveSkill("Aegis", "Paladin", "Shift-Right Click with Shield", 40, "Absorbs next damage hit."));
        activeSkills.add(new ActiveSkill("Holy Nova", "Paladin", "Press [F] with Gold Sword", 35, "AoE heal and damage."));
        passiveSkills.add(new PassiveSkill("Guardian", "Paladin", "+20% Max Health"));

        // Landlord
        activeSkills.add(new ActiveSkill("Transmutation", "Landlord", "Right Click Stone/Cobble with Iron Ingot", 30, "Turns block to ore."));
        passiveSkills.add(new PassiveSkill("Domain Lord", "Landlord", "No fall damage"));

        // Alchemist
        activeSkills.add(new ActiveSkill("Brew Burst", "Alchemist", "Right Click with Brewing Stand", 25, "Throws splash healing/buff potion."));
        passiveSkills.add(new PassiveSkill("Catalyst", "Alchemist", "+30% Potion Duration"));
    }

    public List<ActiveSkill> getUnlockedActiveSkills(PlayerProfile profile) {
        List<ActiveSkill> unlocked = new ArrayList<>();
        boolean hasAdmin = profile.getUnlockedClasses().contains("Admin Class");
        for (ActiveSkill skill : activeSkills) {
            if (hasAdmin || profile.getUnlockedClasses().contains(skill.getClassName())) {
                unlocked.add(skill);
            }
        }
        return unlocked;
    }

    public List<PassiveSkill> getUnlockedPassiveSkills(PlayerProfile profile) {
        List<PassiveSkill> unlocked = new ArrayList<>();
        boolean hasAdmin = profile.getUnlockedClasses().contains("Admin Class");
        for (PassiveSkill skill : passiveSkills) {
            if (hasAdmin || profile.getUnlockedClasses().contains(skill.getClassName())) {
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
