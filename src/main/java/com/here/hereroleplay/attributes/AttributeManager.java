package com.here.hereroleplay.attributes;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AttributeManager {

    private final HereRolePlay plugin;
    // We use a specific UUID for our modifiers so we can find and remove them easily
    private static final UUID HRP_MODIFIER_UUID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    public AttributeManager(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    public void applyAttributes(Player player) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        // Reset base values to vanilla defaults in case they were modified by another plugin
        resetBaseAttribute(player, Attribute.GENERIC_MAX_HEALTH);
        resetBaseAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED);
        resetBaseAttribute(player, Attribute.GENERIC_ATTACK_DAMAGE);

        // Vitality -> Max Health (+1 HP per 2 points)
        double healthBonus = profile.getVitalityPoints() * 0.5;
        applyModifier(player, Attribute.GENERIC_MAX_HEALTH, healthBonus);

        // Agility -> Movement Speed (+0.002 speed per point)
        double speedBonus = profile.getAgilityPoints() * 0.002;
        applyModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, speedBonus);

        // Strength -> Attack Damage (+0.5 damage per point)
        double damageBonus = profile.getStrengthPoints() * 0.5;
        applyModifier(player, Attribute.GENERIC_ATTACK_DAMAGE, damageBonus);
        
        // Ensure their health doesn't exceed the new max
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null && player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }
    }

    private void resetBaseAttribute(Player player, Attribute attribute) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getDefaultValue());
        }
    }

    private void applyModifier(Player player, Attribute attribute, double amount) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        // Remove old modifier if it exists
        for (AttributeModifier modifier : instance.getModifiers()) {
            if (modifier.getUniqueId().equals(HRP_MODIFIER_UUID)) {
                instance.removeModifier(modifier);
            }
        }

        // Apply new modifier if amount > 0
        if (amount > 0) {
            AttributeModifier modifier = new AttributeModifier(HRP_MODIFIER_UUID, "HRP_Bonus", amount, AttributeModifier.Operation.ADD_NUMBER);
            instance.addModifier(modifier);
        }
    }
}
