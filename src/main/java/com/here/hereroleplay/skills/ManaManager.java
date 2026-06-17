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
                int spellEchoLvl = Math.min(100, profile.getSkillLevel("Spell Echo"));
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
            case "Rock Blast":
            case "Fireball":
            case "Quick Shot":
            case "Boomerang Throw":
            case "Laser DOT":
            case "Thunder Wave":
            case "Water Cannon":
            case "Wind Blast":
            case "Soul Drain":
            case "Piercing Bolt":
            case "Spear Knight":
                return "F";
            case "Aegis":
            case "Rejuvenation":
            case "Timber":
            case "Diggy Diggy Hole":
            case "Tunnel Vision":
            case "Transmutation":
            case "Water Wave":
            case "Fire Rain":
            case "Gale Force":
            case "Quicksand":
            case "Raise Undead":
            case "Assassination":
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
            
            String mainF = "";
            String mainShiftF = "";
            String offhandShieldPart = "";
            
            ItemStack handItem = player.getInventory().getItemInMainHand();
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            
            Material handMat = handItem != null ? handItem.getType() : Material.AIR;
            Material offHandMat = offHandItem != null ? offHandItem.getType() : Material.AIR;
            String handName = handMat.name();
            
            java.util.List<ActiveSkill> unlocked = plugin.getClassManager().getUnlockedActiveSkills(profile);
            
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
                        case "Rock Blast":
                        case "Quicksand":
                            matches = handMat == Material.STICK;
                            break;
                        case "Fireball":
                        case "Fire Rain":
                            matches = handMat == Material.BLAZE_ROD;
                            break;
                        case "Quick Shot":
                            matches = handMat == Material.BOW;
                            break;
                        case "Piercing Bolt":
                            matches = handMat == Material.CROSSBOW;
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
                        case "Water Cannon":
                        case "Water Wave":
                            matches = handMat == Material.TROPICAL_FISH;
                            break;
                        case "Wind Blast":
                        case "Gale Force":
                            matches = handMat == Material.BREEZE_ROD;
                            break;
                        case "Raise Undead":
                        case "Soul Drain":
                            matches = handMat == Material.BONE;
                            break;
                        case "Spear Knight":
                            matches = handName.endsWith("_SPEAR");
                            break;
                        case "Assassination":
                            matches = handName.contains("SWORD");
                            break;
                    }
                    
                    if (matches) {
                        String trigger = getSkillTriggerText(skill.getName());
                        if (trigger.equals("F")) {
                            if (!mainF.isEmpty()) mainF += " ";
                            mainF += String.format("§e[F] %s", skill.getName().toUpperCase());
                        } else if (trigger.equals("Shift+F")) {
                            if (!mainShiftF.isEmpty()) mainShiftF += " ";
                            mainShiftF += String.format("§a[Shift+F] %s", skill.getName().toUpperCase());
                        }
                    }
                }
            }
            
            // Offhand shield
            if (offHandMat == Material.SHIELD) {
                for (ActiveSkill skill : unlocked) {
                    if (skill.getName().equals("Holy Nova")) {
                        if (!offhandShieldPart.isEmpty()) offhandShieldPart += " ";
                        offhandShieldPart += String.format("§6[Block+F] %s", skill.getName().toUpperCase());
                    } else if (skill.getName().equals("Aegis")) {
                        if (!offhandShieldPart.isEmpty()) offhandShieldPart += " ";
                        offhandShieldPart += String.format("§a[Block+Shift+F] %s", skill.getName().toUpperCase());
                    }
                }
            }
            
            java.util.List<String> parts = new java.util.ArrayList<>();
            if (!mainF.isEmpty()) parts.add(mainF);
            if (!mainShiftF.isEmpty()) parts.add(mainShiftF);
            if (!offhandShieldPart.isEmpty()) parts.add(offhandShieldPart);
            
            String skillsText = String.join("      ", parts);
            
            // Calculate health layer and color
            double currentHP = player.getHealth();
            double maxHP = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH) != null ? player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue() : 20.0;
            
            double layerSize = 40.0;
            int currentLayer = (int) Math.ceil(currentHP / layerSize);
            if (currentLayer < 1) currentLayer = 1;
            
            String hpColor = "§c"; // Default red
            switch (currentLayer) {
                case 1: hpColor = "§c"; break; // Red
                case 2: hpColor = "§5"; break; // Dark Purple
                case 3: hpColor = "§9"; break; // Blue
                case 4: hpColor = "§2"; break; // Dark Green
                case 5: hpColor = "§6"; break; // Gold
                case 6: hpColor = "§d"; break; // Light Purple
                case 7: hpColor = "§b"; break; // Aqua
                case 8: hpColor = "§a"; break; // Light Green
                default: hpColor = "§f"; break; // White
            }
            
            String hpText = String.format("%s❤ HP(%s) %.0f/%.0f", hpColor, toRoman(currentLayer), currentHP, maxHP);
            
            String actionbar;
            if (!skillsText.isEmpty()) {
                actionbar = String.format("%s      §b✦ Mana: %.0f / %.0f      %s", hpText, currentMana, maxMana, skillsText);
            } else {
                actionbar = String.format("%s      §b✦ Mana: %.0f / %.0f", hpText, currentMana, maxMana);
            }
            
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
        }
    }

    private String toRoman(int num) {
        if (num < 1) return "I";
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                num -= values[i];
                sb.append(symbols[i]);
            }
        }
        return sb.toString();
    }
}
