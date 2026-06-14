package com.here.hereroleplay.skills;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.classes.ClassManager;
import com.here.hereroleplay.classes.ClassManager.ActiveSkill;
import com.here.hereroleplay.data.PlayerProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ManaManager {

    private final HereRolePlay plugin;
    private int regenTaskId = -1;
    private int actionbarTaskId = -1;
    private final double baseRegen;
    private final double intelligenceRegenBonus;
    private final double baseMaxMana;
    private final double intelligenceMaxBonus;

    public ManaManager(HereRolePlay plugin) {
        this.plugin = plugin;
        this.baseRegen = plugin.getConfig().getDouble("mana.base-regen", 1.0);
        this.intelligenceRegenBonus = plugin.getConfig().getDouble("mana.intelligence-regen-bonus", 0.1);
        this.baseMaxMana = plugin.getConfig().getDouble("mana.base-max", 20.0);
        this.intelligenceMaxBonus = plugin.getConfig().getDouble("mana.intelligence-max-bonus", 10.0);
    }

    public void start() {
        // Regen Task (every 1 second)
        regenTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::regenerateMana, 20L, 20L).getTaskId();
        
        // Actionbar Task (every 5 ticks / 0.25s)
        actionbarTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateActionBars, 5L, 5L).getTaskId();
    }

    public void stop() {
        if (regenTaskId != -1) Bukkit.getScheduler().cancelTask(regenTaskId);
        if (actionbarTaskId != -1) Bukkit.getScheduler().cancelTask(actionbarTaskId);
    }

    public double getMaxMana(PlayerProfile profile) {
        return baseMaxMana + (profile.getIntelligencePoints() * intelligenceMaxBonus);
    }

    private void regenerateMana() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile == null) continue;

            double maxMana = getMaxMana(profile);
            double currentMana = profile.getCurrentMana();
            if (currentMana > maxMana) {
                profile.setCurrentMana(maxMana);
            } else if (currentMana < maxMana) {
                double regenAmount = baseRegen + (profile.getIntelligencePoints() * intelligenceRegenBonus);
                int spellEchoLvl = profile.getSkillLevel("Spell Echo");
                if (spellEchoLvl > 0) {
                    regenAmount *= (1.0 + spellEchoLvl * 0.01);
                }
                double newMana = Math.min(maxMana, currentMana + regenAmount);
                profile.setCurrentMana(newMana);
            }
        }
    }

    private boolean isBlacklisted(Material mat) {
        if (mat == null) return true;
        String name = mat.name().toUpperCase();
        return mat == Material.AIR || 
               mat == Material.BEDROCK || 
               mat == Material.BARRIER || 
               name.contains("VILLAGER") ||
               name.contains("HEAD") ||
               name.contains("SKULL") ||
               name.contains("CHEST") ||
               name.contains("BARREL") ||
               name.contains("SHULKER") ||
               name.contains("PORTAL") ||
               name.contains("SPAWNER") ||
               name.contains("COMMAND");
    }

    private boolean isValidTransmutationBlock(Material mat) {
        if (mat == null) return false;
        if (!mat.isBlock() || !mat.isSolid()) return false;
        if (mat.isEdible() || mat.name().contains("SHIELD")) return false;
        if (isBlacklisted(mat)) return false;
        if (mat.isInteractable()) return false;

        String name = mat.name();
        if (name.contains("PLATE") || 
            name.contains("SIGN") || 
            name.contains("BANNER") || 
            name.contains("BED") || 
            name.contains("CAULDRON") || 
            name.contains("CANDLE") || 
            name.contains("CORAL") || 
            name.contains("PISTON") || 
            name.contains("GLASS_PANE") || 
            name.contains("BARS") || 
            name.contains("WALL") || 
            name.contains("FENCE") || 
            name.contains("GATE") || 
            name.contains("STAIRS") || 
            name.contains("SLAB") || 
            name.contains("POT") || 
            name.contains("LANTERN") || 
            name.contains("TORCH") || 
            name.contains("CAMPFIRE") || 
            name.contains("EGG") || 
            name.contains("SENSOR") || 
            name.contains("VEIN") || 
            name.contains("GRATE") || 
            name.contains("CONDUIT") || 
            name.contains("TARGET") || 
            name.contains("LIGHTNING_ROD") || 
            name.contains("PATH") || 
            name.contains("INFESTED") || 
            name.contains("AMETHYST_BUD") || 
            name.contains("AMETHYST_CLUSTER") || 
            name.contains("POINTED_DRIPSTONE") || 
            name.contains("LEGACY_") || 
            name.equals("BAMBOO") || 
            name.equals("CHAIN") || 
            name.equals("FARMLAND") || 
            name.equals("FROSTED_ICE") || 
            name.equals("BUDDING_AMETHYST")
        ) {
            return false;
        }

        return true;
    }

    private String getSkillTriggerText(String skillName) {
        switch (skillName) {
            case "Cleave":
            case "Holy Nova":
            case "Arcane Missile":
            case "Quick Shot":
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
                return "F";
        }
    }

    private void updateActionBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile == null) continue;

            double maxMana = getMaxMana(profile);
            double currentMana = profile.getCurrentMana();
            if (currentMana > maxMana) {
                currentMana = maxMana;
                profile.setCurrentMana(currentMana);
            }
            
            String fPart = "";
            String shiftFPart = "";
            ItemStack handItem = player.getInventory().getItemInMainHand();
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            
            Material handMat = handItem != null ? handItem.getType() : Material.AIR;
            Material offHandMat = offHandItem != null ? offHandItem.getType() : Material.AIR;
            String handName = handMat.name();
            
            java.util.List<ActiveSkill> unlocked = plugin.getClassManager().getUnlockedActiveSkills(profile);
            
            boolean mainHandHasF = false;
            boolean mainHandHasShiftF = false;
            
            if (handMat != Material.AIR) {
                for (ActiveSkill skill : unlocked) {
                    boolean matches = false;
                    switch (skill.getName()) {
                        case "Cleave":
                            matches = handName.contains("SWORD");
                            break;
                        case "Holy Nova":
                            matches = handMat == Material.SHIELD;
                            break;
                        case "Arcane Missile":
                            matches = handMat == Material.STICK;
                            break;
                        case "Fireball":
                            matches = handMat == Material.STICK;
                            break;
                        case "Quick Shot":
                            matches = handMat == Material.BOW;
                            break;
                        case "Aegis":
                            matches = handMat == Material.SHIELD;
                            break;
                        case "Rejuvenation":
                            matches = handName.contains("HOE");
                            break;
                        case "Timber":
                            matches = handName.contains("AXE") && !handName.contains("PICKAXE");
                            break;
                        case "Diggy Diggy Hole":
                            matches = handName.contains("SHOVEL");
                            break;
                        case "Tunnel Vision":
                            matches = handName.contains("PICKAXE");
                            break;
                        case "Transmutation":
                            matches = isValidTransmutationBlock(handMat);
                            break;
                        case "Boomerang Throw":
                            matches = handName.contains("AXE") && !handName.contains("PICKAXE");
                            break;
                        case "Thunder Wave":
                            matches = handMat == Material.MACE;
                            break;
                        case "Laser DOT":
                            matches = handMat == Material.TRIDENT;
                            break;
                        case "Chain Lightning":
                            matches = handMat == Material.BLAZE_ROD;
                            break;
                        case "Water Wave":
                            matches = handMat == Material.BLAZE_ROD;
                            break;
                    }
                    
                    if (matches) {
                        String trigger = getSkillTriggerText(skill.getName());
                        if (trigger.equals("F")) {
                            if (!fPart.isEmpty()) fPart += " ";
                            fPart += String.format("§e[F] %s", skill.getName().toUpperCase());
                            mainHandHasF = true;
                        } else if (trigger.equals("Shift+F")) {
                            if (!shiftFPart.isEmpty()) shiftFPart += " ";
                            shiftFPart += String.format("§a[Shift+F] %s", skill.getName().toUpperCase());
                            mainHandHasShiftF = true;
                        }
                    }
                }
            }
            
            // Offhand shield fallback
            if (offHandMat == Material.SHIELD) {
                for (ActiveSkill skill : unlocked) {
                    if (skill.getName().equals("Holy Nova") && !mainHandHasF) {
                        if (!fPart.isEmpty()) fPart += " ";
                        fPart += String.format("§e[F] %s", skill.getName().toUpperCase());
                    } else if (skill.getName().equals("Aegis") && !mainHandHasShiftF) {
                        if (!shiftFPart.isEmpty()) shiftFPart += " ";
                        shiftFPart += String.format("§a[Shift+F] %s", skill.getName().toUpperCase());
                    }
                }
            }
            
            String actionbar;
            if (!fPart.isEmpty() && !shiftFPart.isEmpty()) {
                actionbar = String.format("§b✦ Mana: %.0f / %.0f      %s      %s", currentMana, maxMana, fPart, shiftFPart);
            } else if (!fPart.isEmpty()) {
                actionbar = String.format("§b✦ Mana: %.0f / %.0f      %s", currentMana, maxMana, fPart);
            } else if (!shiftFPart.isEmpty()) {
                actionbar = String.format("§b✦ Mana: %.0f / %.0f                            %s", currentMana, maxMana, shiftFPart);
            } else {
                actionbar = String.format("§b✦ Mana: %.0f / %.0f", currentMana, maxMana);
            }
            
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
        }
    }
}
