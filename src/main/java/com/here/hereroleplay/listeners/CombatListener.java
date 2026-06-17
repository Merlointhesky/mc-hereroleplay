package com.here.hereroleplay.listeners;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class CombatListener implements Listener {

    private final HereRolePlay plugin;

    public CombatListener(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            double xpToGive = 1.0; // Tier 1 Default (Passive mobs / Animals)

            EntityType type = event.getEntityType();
            String typeName = type.name();

            NamespacedKey customBossKey = new NamespacedKey("heremobby", "custom_boss");
            NamespacedKey customMobKey = new NamespacedKey("heremobby", "custom_mob");

            if (event.getEntity().getPersistentDataContainer().has(customBossKey, PersistentDataType.STRING)) {
                String bossId = event.getEntity().getPersistentDataContainer().get(customBossKey, PersistentDataType.STRING);
                if ("void_necromancer".equalsIgnoreCase(bossId)) {
                    xpToGive = 300.0;
                } else if ("storm_archmage".equalsIgnoreCase(bossId)) {
                    xpToGive = 400.0;
                } else if ("overworld_wither".equalsIgnoreCase(bossId)) {
                    xpToGive = 800.0;
                } else if ("deep_dark_guardian".equalsIgnoreCase(bossId)) {
                    xpToGive = 1500.0;
                } else {
                    xpToGive = 500.0;
                }
            } else if (event.getEntity().getPersistentDataContainer().has(customMobKey, PersistentDataType.STRING)) {
                String mobId = event.getEntity().getPersistentDataContainer().get(customMobKey, PersistentDataType.STRING);
                if ("infernal_pyromancer".equalsIgnoreCase(mobId)) {
                    xpToGive = 35.0;
                } else {
                    xpToGive = 15.0;
                }
            } else {
                // Broad check for hostile mobs / monsters
                if (typeName.contains("ZOMBIE") || typeName.contains("SKELETON") || typeName.contains("SPIDER") ||
                    type == EntityType.CREEPER || type == EntityType.PIGLIN || type == EntityType.BLAZE ||
                    type == EntityType.GHAST || type == EntityType.SLIME || type == EntityType.MAGMA_CUBE ||
                    type == EntityType.WITCH || type == EntityType.PHANTOM || type == EntityType.DROWNED ||
                    type == EntityType.HUSK || type == EntityType.STRAY || type == EntityType.PILLAGER ||
                    type == EntityType.VINDICATOR || type == EntityType.EVOKER || type == EntityType.HOGLIN ||
                    type == EntityType.PIGLIN_BRUTE || type == EntityType.GUARDIAN || type == EntityType.ELDER_GUARDIAN) {
                    xpToGive = 5.0; // Tier 2
                } else if (type == EntityType.ENDERMAN || type == EntityType.RAVAGER || type == EntityType.WITHER_SKELETON || type == EntityType.SHULKER) {
                    xpToGive = 25.0; // Tier 3
                } else if (type == EntityType.ENDER_DRAGON || type == EntityType.WITHER || type == EntityType.WARDEN) {
                    xpToGive = 250.0; // Tier 4
                }
                
                xpToGive *= 1.1; // Increase standard combat XP by 10%
            }

            plugin.getXpManager().addCombatXp(player, xpToGive);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            // Miner - Dense Armor damage reduction passive
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile != null) {
                int denseArmorLvl = Math.min(100, profile.getSkillLevel("Dense Armor"));
                if (denseArmorLvl > 0) {
                    double reduction = denseArmorLvl * 0.01;
                    event.setDamage(event.getDamage() * (1.0 - reduction));
                }
            }
            
            double damage = event.getFinalDamage();
            if (damage > 0) {
                // 1 heart = 2 damage points = 1 XP, increased by 10%
                double xpToGive = (damage / 2.0) * 1.1;
                plugin.getXpManager().addCombatXp(player, xpToGive);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        Player player = null;
        boolean isMelee = false;
        boolean isRanged = false;

        if (event.getDamager() instanceof Player p) {
            player = p;
            isMelee = true;
        } else if (event.getDamager() instanceof org.bukkit.entity.Arrow arrow && arrow.getShooter() instanceof Player p) {
            player = p;
            isRanged = true;
        }

        // --- NECROMANCER SUMMON FRIENDLINESS CHECKS ---
        // 1. Summoner damages minion
        if (event.getEntity().hasMetadata("summoner_uuid")) {
            String summonerUuidStr = event.getEntity().getMetadata("summoner_uuid").get(0).asString();
            if (player != null && player.getUniqueId().toString().equals(summonerUuidStr)) {
                event.setCancelled(true);
                return;
            }
        }
        // 2. Minion damages summoner
        if (event.getEntity() instanceof Player targetPlayer) {
            org.bukkit.entity.Entity damager = event.getDamager();
            if (damager instanceof org.bukkit.entity.Projectile proj && proj.getShooter() instanceof org.bukkit.entity.Entity shooter) {
                damager = shooter;
            }
            if (damager.hasMetadata("summoner_uuid")) {
                String summonerUuidStr = damager.getMetadata("summoner_uuid").get(0).asString();
                if (targetPlayer.getUniqueId().toString().equals(summonerUuidStr)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        // ----------------------------------------------

        if (player == null) return;
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        if (isMelee && event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            // Heavy Strike passive (Warrior)
            int heavyStrikeLvl = Math.min(100, profile.getSkillLevel("Heavy Strike"));
            if (heavyStrikeLvl > 0) {
                double multiplier = 1.0 + heavyStrikeLvl * 0.01;
                event.setDamage(event.getDamage() * multiplier);
            }
        }

        if (isRanged) {
            // Precision passive (Ranger)
            int precisionLvl = Math.min(100, profile.getSkillLevel("Precision"));
            if (precisionLvl > 0) {
                double critChance = precisionLvl * 0.01;
                if (Math.random() < critChance) {
                    int critDamageLvl = Math.min(100, profile.getSkillLevel("Critical Damage"));
                    double critMultiplier = 1.5 + critDamageLvl * 0.01;
                    event.setDamage(event.getDamage() * critMultiplier);
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1.2f);
                    event.getEntity().getWorld().spawnParticle(org.bukkit.Particle.CRIT, event.getEntity().getLocation().add(0, 1, 0), 10, 0.2, 0.5, 0.2, 0.1);
                    player.sendMessage("§a★ Precision Critical Strike!");
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(org.bukkit.event.entity.EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player) {
            org.bukkit.entity.Entity entity = event.getEntity();
            if (entity.hasMetadata("summoner_uuid")) {
                String summonerUuid = entity.getMetadata("summoner_uuid").get(0).asString();
                if (player.getUniqueId().toString().equals(summonerUuid)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
