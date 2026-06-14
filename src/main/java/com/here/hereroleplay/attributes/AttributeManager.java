package com.here.hereroleplay.attributes;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.UUID;

public class AttributeManager implements Listener {

    private final HereRolePlay plugin;
    private static final UUID HRP_MODIFIER_UUID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID HRP_SPEED_MODIFIER_UUID = UUID.fromString("22222222-3333-4444-5555-666666666666");

    public AttributeManager(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    public void applyAttributes(Player player) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Reset speeds
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);

        // Reset attributes
        resetBaseAttribute(player, Attribute.GENERIC_MAX_HEALTH);
        resetBaseAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED);
        resetBaseAttribute(player, Attribute.GENERIC_ATTACK_DAMAGE);
        resetBaseAttribute(player, Attribute.GENERIC_ARMOR);
        resetBaseAttribute(player, Attribute.GENERIC_ATTACK_SPEED);

        // Clean stale modifiers
        cleanStaleModifiers(player, Attribute.GENERIC_MAX_HEALTH);
        cleanStaleModifiers(player, Attribute.GENERIC_MOVEMENT_SPEED);
        cleanStaleModifiers(player, Attribute.GENERIC_ATTACK_DAMAGE);
        cleanStaleModifiers(player, Attribute.GENERIC_ARMOR);
        cleanStaleModifiers(player, Attribute.GENERIC_ATTACK_SPEED);

        // Vitality -> Max Health (+1 HP per 2 points)
        double healthBonus = profile.getVitalityPoints() * 0.5;
        // Paladin -> Guardian passive (+1 heart / 2.0 HP per level)
        int guardianLvl = profile.getSkillLevel("Guardian");
        if (guardianLvl > 0) {
            healthBonus += guardianLvl * 2.0;
        }
        applyModifier(player, Attribute.GENERIC_MAX_HEALTH, healthBonus);

        // Agility -> Movement Speed (+0.0004 speed per point)
        double speedBonus = profile.getAgilityPoints() * 0.0004;
        applyModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, speedBonus);

        // Strength -> Attack Damage (+0.5 damage per point)
        double damageBonus = profile.getStrengthPoints() * 0.5;
        applyModifier(player, Attribute.GENERIC_ATTACK_DAMAGE, damageBonus);

        // Warrior -> Swift Strike (+1% attack speed per level, default base is 4.0)
        int swiftStrikeLvl = profile.getSkillLevel("Swift Strike");
        double attackSpeedBonus = 0.0;
        if (swiftStrikeLvl > 0) {
            attackSpeedBonus = swiftStrikeLvl * 0.01 * 4.0;
        }
        applyModifier(player, Attribute.GENERIC_ATTACK_SPEED, attackSpeedBonus);
        
        // Ensure health doesn't exceed the new max
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null && player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }
    }

    private void cleanStaleModifiers(Player player, Attribute attribute) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        for (AttributeModifier modifier : new java.util.ArrayList<>(instance.getModifiers())) {
            UUID uuid = modifier.getUniqueId();
            String name = modifier.getName().toLowerCase();
            
            if (uuid.equals(HRP_MODIFIER_UUID)) {
                continue;
            }
            
            if (name.contains("modifier") || name.startsWith("minecraft:") || name.equals("armor") || name.equals("toughness") || name.contains("attack")) {
                continue;
            }
            
            instance.removeModifier(modifier);
        }
    }

    private void resetBaseAttribute(Player player, Attribute attribute) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            double defaultValue = instance.getDefaultValue();
            if (attribute == Attribute.GENERIC_MOVEMENT_SPEED) {
                defaultValue = 0.1;
            } else if (attribute == Attribute.GENERIC_MAX_HEALTH) {
                defaultValue = 20.0;
            } else if (attribute == Attribute.GENERIC_ATTACK_DAMAGE) {
                defaultValue = 2.0;
            } else if (attribute == Attribute.GENERIC_ARMOR) {
                defaultValue = 0.0;
            } else if (attribute == Attribute.GENERIC_ATTACK_SPEED) {
                defaultValue = 4.0;
            }
            instance.setBaseValue(defaultValue);
        }
    }

    private void applyModifier(Player player, Attribute attribute, double amount) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        for (AttributeModifier modifier : instance.getModifiers()) {
            if (modifier.getUniqueId().equals(HRP_MODIFIER_UUID)) {
                instance.removeModifier(modifier);
            }
        }

        if (amount > 0) {
            AttributeModifier modifier = new AttributeModifier(HRP_MODIFIER_UUID, "HRP_Bonus", amount, AttributeModifier.Operation.ADD_NUMBER);
            instance.addModifier(modifier);
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile != null) {
                int level = profile.getSkillLevel("Power Surge");
                if (level > 0) {
                    Entity mount = event.getMount();
                    if (mount instanceof LivingEntity livingMount) {
                        AttributeInstance speedAttr = livingMount.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                        if (speedAttr != null) {
                            for (AttributeModifier modifier : new java.util.ArrayList<>(speedAttr.getModifiers())) {
                                if (modifier.getUniqueId().equals(HRP_SPEED_MODIFIER_UUID)) {
                                    speedAttr.removeModifier(modifier);
                                }
                            }
                            double bonus = speedAttr.getBaseValue() * (level * 0.01);
                            AttributeModifier modifier = new AttributeModifier(HRP_SPEED_MODIFIER_UUID, "PowerSurgeMountSpeed", bonus, AttributeModifier.Operation.ADD_NUMBER);
                            speedAttr.addModifier(modifier);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player) {
            Entity mount = event.getDismounted();
            if (mount instanceof LivingEntity livingMount) {
                AttributeInstance speedAttr = livingMount.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (speedAttr != null) {
                    for (AttributeModifier modifier : new java.util.ArrayList<>(speedAttr.getModifiers())) {
                        if (modifier.getUniqueId().equals(HRP_SPEED_MODIFIER_UUID)) {
                            speedAttr.removeModifier(modifier);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        org.bukkit.entity.Vehicle vehicle = event.getVehicle();
        if (vehicle.getPassengers().isEmpty()) return;
        Entity passenger = vehicle.getPassengers().get(0);
        if (passenger instanceof Player player) {
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile != null) {
                int level = profile.getSkillLevel("Power Surge");
                if (level > 0) {
                    double multiplier = 1.0 + level * 0.01;
                    if (vehicle instanceof org.bukkit.entity.Minecart minecart) {
                        double defaultMax = 0.4;
                        minecart.setMaxSpeed(defaultMax * multiplier);
                    } else if (vehicle instanceof org.bukkit.entity.Boat boat) {
                        org.bukkit.util.Vector vel = boat.getVelocity();
                        double horizontalSpeed = Math.sqrt(vel.getX() * vel.getX() + vel.getZ() * vel.getZ());
                        if (horizontalSpeed > 0.05 && horizontalSpeed < 2.0) {
                            double maxSpeed = 0.5 * multiplier; 
                            if (horizontalSpeed < maxSpeed) {
                                vel.setX(vel.getX() * multiplier);
                                vel.setZ(vel.getZ() * multiplier);
                                boat.setVelocity(vel);
                            }
                        }
                    }
                }
            }
        }
    }
}
