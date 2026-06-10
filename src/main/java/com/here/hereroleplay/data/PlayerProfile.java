package com.here.hereroleplay.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerProfile {

    private final UUID uuid;
    
    // Core Levels
    private int combatLevel = 1;
    private int collectLevel = 1;
    private int craftLevel = 1;

    // XP
    private double combatXp = 0;
    private double collectXp = 0;
    private double craftXp = 0;

    // Attributes (Allocated Points)
    private int strengthPoints = 0;
    private int agilityPoints = 0;
    private int vitalityPoints = 0;
    private int intelligencePoints = 0;

    // Unlocked Classes
    private Set<String> unlockedClasses = new HashSet<>();

    // Unspent Skill Points
    private int unspentSkillPoints = 0;

    // Current Mana
    private double currentMana = 20.0;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    // Getters and Setters for Levels
    public int getCombatLevel() { return combatLevel; }
    public void setCombatLevel(int combatLevel) { this.combatLevel = combatLevel; }
    
    public int getCollectLevel() { return collectLevel; }
    public void setCollectLevel(int collectLevel) { this.collectLevel = collectLevel; }
    
    public int getCraftLevel() { return craftLevel; }
    public void setCraftLevel(int craftLevel) { this.craftLevel = craftLevel; }

    // Getters and Setters for XP
    public double getCombatXp() { return combatXp; }
    public void setCombatXp(double combatXp) { this.combatXp = combatXp; }
    
    public double getCollectXp() { return collectXp; }
    public void setCollectXp(double collectXp) { this.collectXp = collectXp; }
    
    public double getCraftXp() { return craftXp; }
    public void setCraftXp(double craftXp) { this.craftXp = craftXp; }

    // Getters and Setters for Attributes
    public int getStrengthPoints() { return strengthPoints; }
    public void setStrengthPoints(int strengthPoints) { this.strengthPoints = strengthPoints; }
    
    public int getAgilityPoints() { return agilityPoints; }
    public void setAgilityPoints(int agilityPoints) { this.agilityPoints = agilityPoints; }
    
    public int getVitalityPoints() { return vitalityPoints; }
    public void setVitalityPoints(int vitalityPoints) { this.vitalityPoints = vitalityPoints; }
    
    public int getIntelligencePoints() { return intelligencePoints; }
    public void setIntelligencePoints(int intelligencePoints) { this.intelligencePoints = intelligencePoints; }

    // Unspent Skill Points
    public int getUnspentSkillPoints() { return unspentSkillPoints; }
    public void setUnspentSkillPoints(int unspentSkillPoints) { this.unspentSkillPoints = unspentSkillPoints; }

    // Classes
    public Set<String> getUnlockedClasses() { return unlockedClasses; }
    public void addUnlockedClass(String className) { this.unlockedClasses.add(className); }
    public void removeUnlockedClass(String className) { this.unlockedClasses.remove(className); }

    // Mana
    public double getCurrentMana() { return currentMana; }
    public void setCurrentMana(double currentMana) { this.currentMana = currentMana; }

    // Skill Levels
    private final Map<String, Integer> skillLevels = new HashMap<>();

    public int getSkillLevel(String skillName) {
        return skillLevels.getOrDefault(skillName, 0);
    }

    public void setSkillLevel(String skillName, int level) {
        skillLevels.put(skillName, level);
    }

    public Map<String, Integer> getSkillLevels() {
        return skillLevels;
    }
}
