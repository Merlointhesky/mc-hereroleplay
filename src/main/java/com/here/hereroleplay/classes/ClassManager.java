package com.here.hereroleplay.classes;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClassManager {

    private final HereRolePlay plugin;
    private final List<HrpClass> classes = new ArrayList<>();

    public ClassManager(HereRolePlay plugin) {
        this.plugin = plugin;
        registerDefaultClasses();
    }

    private void registerDefaultClasses() {
        // Base Classes
        classes.add(new HrpClass("Warrior", "Melee power, Armor scaling.", 20, 0, 20, 0, 40));
        classes.add(new HrpClass("Ranger", "Ranged Crit chance, Move speed.", 0, 20, 20, 0, 40));
        classes.add(new HrpClass("Wizard", "Magic spells via Stick, Mana efficient.", 0, 0, 20, 20, 40));
        classes.add(new HrpClass("Miner", "Excavation bursts, Toughness.", 20, 20, 0, 0, 40));
        classes.add(new HrpClass("Farmer", "AoE Healing, Resource yield.", 0, 20, 0, 20, 40));
        classes.add(new HrpClass("Engineer", "Industrial mass production, Enchanting.", 20, 0, 0, 20, 40));
        
        // Hero Classes
        classes.add(new HrpClass("Paladin", "Pure active skill tank/healer (Aegis, Holy Nova).", 30, 0, 30, 30, 100));
        classes.add(new HrpClass("Landlord", "World shaping (Transmutation).", 30, 30, 30, 0, 100));
        classes.add(new HrpClass("Alchemist", "Output doubling (Potions, Enchanting).", 0, 30, 30, 30, 100));
        
        // Admin
        classes.add(new HrpClass("Admin Class", "Unlocked by mastering everything.", 100, 100, 100, 100, 400));
    }

    public List<HrpClass> getClasses() {
        return classes;
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
