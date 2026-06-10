package com.here.hereroleplay.skills;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class SkillManager implements Listener {

    private final HereRolePlay plugin;

    public SkillManager(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleSkillTrigger(player, false, event);
        }
    }

    @EventHandler
    public void onFKeySwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        // Prevent the actual item swap
        event.setCancelled(true);
        
        handleSkillTrigger(player, true, null);
    }

    private void handleSkillTrigger(Player player, boolean isFKey, PlayerInteractEvent event) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        Material handItem = player.getInventory().getItemInMainHand().getType();
        String handItemName = handItem.name();

        if (isFKey) {
            if (handItemName.contains("SWORD") && handItem != Material.GOLDEN_SWORD) {
                // Cleave (Sword, F-Key)
                executeCleave(player, profile);
            } else if (handItem == Material.GOLDEN_SWORD) {
                // Holy Nova (Gold Sword, F-Key)
                executeHolyNova(player, profile);
            } else if (handItem == Material.STICK) {
                // Arcane Missile (Stick, F-Key)
                executeArcaneMissile(player, profile);
            } else if (handItem == Material.BLAZE_ROD) {
                // Fireball (Blaze Rod, F-Key)
                executeFireball(player, profile);
            } else if (handItem == Material.BOW) {
                // Quick Shot (Bow, F-Key)
                executeQuickShot(player, profile);
            } else if (handItem == Material.REDSTONE) {
                // Overload (Redstone, F-Key)
                executeOverload(player, profile);
            }
        } else {
            // Right Click / Shift-Right Click
            if (player.isSneaking()) {
                if (handItemName.contains("AXE")) {
                    executeTimber(player, profile);
                } else if (handItemName.contains("HOE")) {
                    executeRejuvenation(player, profile);
                } else if (handItem == Material.SHIELD) {
                    executeAegis(player, profile);
                }
            } else {
                // Non-sneaking right click
                if (handItem == Material.BOW) {
                    executeQuickShot(player, profile);
                } else if (handItem == Material.BREWING_STAND) {
                    executeBrewBurst(player, profile);
                } else if (handItem == Material.REDSTONE) {
                    executeOverload(player, profile);
                } else if (handItem == Material.IRON_INGOT && event != null && event.getClickedBlock() != null) {
                    executeTransmutation(player, profile, event.getClickedBlock());
                }
            }
        }
    }

    private void executeTimber(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Timber");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Miner class and Timber skill first!");
            return;
        }

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Timber!");
            return;
        }
        
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock != null && targetBlock.getType().name().contains("LOG")) {
            profile.setCurrentMana(profile.getCurrentMana() - cost);
            player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Timber" + ChatColor.GREEN + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
            
            int broken = 0;
            int maxBroken = 10 + (level - 1) * 3;
            Location loc = targetBlock.getLocation();
            while (loc.getBlock().getType().name().contains("LOG") && broken < maxBroken) {
                loc.getBlock().breakNaturally(player.getInventory().getItemInMainHand());
                loc.add(0, 1, 0);
                broken++;
            }
        }
    }

    private void executeCleave(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Cleave");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Warrior class and Cleave skill first!");
            return;
        }

        double cost = 30.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Cleave!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.RED + "You used " + ChatColor.DARK_RED + "Cleave" + ChatColor.RED + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.5f);
        player.spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1, 0), 5, 1, 0.2, 1, 0);

        double damage = 10.0 + (level - 1) * 2.0;
        double range = 3.0 + (level - 1) * 0.2;

        for (Entity entity : player.getNearbyEntities(range, 2, range)) {
            if (entity instanceof LivingEntity target && target != player) {
                target.damage(damage, player);
            }
        }
    }

    private void executeArcaneMissile(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Arcane Missile");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Wizard class and Arcane Missile skill first!");
            return;
        }

        double cost = 15.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Arcane Missile!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You used " + ChatColor.DARK_PURPLE + "Arcane Missile" + ChatColor.LIGHT_PURPLE + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 2f);
        
        org.bukkit.entity.Snowball projectile = player.launchProjectile(org.bukkit.entity.Snowball.class);
        projectile.setCustomName("Arcane Missile");
        projectile.setGlowing(true);
        projectile.setMetadata("arcane_missile_lvl", new FixedMetadataValue(plugin, level));
        projectile.setVelocity(player.getLocation().getDirection().multiply(2.0));
    }

    private void executeFireball(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Fireball");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Wizard class and Fireball skill first!");
            return;
        }

        double cost = 25.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Fireball!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GOLD + "You used " + ChatColor.RED + "Fireball" + ChatColor.GOLD + "!");
        
        org.bukkit.entity.SmallFireball fireball = player.launchProjectile(org.bukkit.entity.SmallFireball.class);
        fireball.setCustomName("Fireball");
        fireball.setMetadata("fireball_lvl", new FixedMetadataValue(plugin, level));
        fireball.setVelocity(player.getLocation().getDirection().multiply(1.5));
        player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
    }

    private void executeQuickShot(final Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Quick Shot");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Ranger class and Quick Shot skill first!");
            return;
        }

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Quick Shot!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Quick Shot" + ChatColor.GREEN + "!");
        
        final int arrowCount = 3 + (level - 1);
        new org.bukkit.scheduler.BukkitRunnable() {
            int fired = 0;
            @Override
            public void run() {
                if (!player.isOnline() || fired >= arrowCount) {
                    cancel();
                    return;
                }
                org.bukkit.entity.Arrow arrow = player.launchProjectile(org.bukkit.entity.Arrow.class);
                arrow.setVelocity(player.getLocation().getDirection().multiply(1.8));
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1f);
                fired++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private void executeRejuvenation(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Rejuvenation");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Farmer class and Rejuvenation skill first!");
            return;
        }

        double cost = 25.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Rejuvenation!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Rejuvenation" + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
        
        double healAmount = 6.0 + (level - 1) * 2.0;
        double radius = 4.0 + (level - 1) * 0.5;
        
        Location loc = player.getLocation();
        for (double d = 0; d < 360; d += 15) {
            double rad = Math.toRadians(d);
            double x = Math.cos(rad) * radius;
            double z = Math.sin(rad) * radius;
            loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(x, 0.1, z), 2, 0, 0, 0, 0);
        }
        
        player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + healAmount));
        for (Entity entity : player.getNearbyEntities(radius, 2, radius)) {
            if (entity instanceof LivingEntity target && target != player) {
                if (target instanceof Player || target instanceof org.bukkit.entity.Tameable || target instanceof org.bukkit.entity.Animals) {
                    target.setHealth(Math.min(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), target.getHealth() + healAmount));
                    target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }
    }

    private void executeOverload(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Overload");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Engineer class and Overload skill first!");
            return;
        }

        double cost = 15.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Overload!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.RED + "System Overload activated!");
        player.playSound(player.getLocation(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1f, 1.5f);
        
        int durationTicks = (int) ((5.0 + (level - 1) * 1.5) * 20);
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, durationTicks, 1));
        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 20, 0.3, 0.5, 0.3, new Particle.DustOptions(org.bukkit.Color.RED, 1.5f));
    }

    private void executeAegis(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Aegis");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Paladin class and Aegis skill first!");
            return;
        }

        double cost = 40.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Aegis!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GOLD + "Shield Aegis activated!");
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 0.8f);
        
        int durationTicks = (int) ((10.0 + (level - 1) * 2.5) * 20);
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, durationTicks, 4));
        player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation(), 15, 0.5, 0.5, 0.5, 0.1);
    }

    private void executeHolyNova(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Holy Nova");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Paladin class and Holy Nova skill first!");
            return;
        }

        double cost = 35.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Holy Nova!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.YELLOW + "Holy Nova casted!");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 2f);
        
        double amount = 8.0 + (level - 1) * 2.0;
        double radius = 4.0 + (level - 1) * 0.5;
        
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 40, radius / 2.0, 0.5, radius / 2.0, 0.1);
        
        for (Entity entity : player.getNearbyEntities(radius, 2, radius)) {
            if (entity instanceof LivingEntity target && target != player) {
                if (target instanceof Player || target instanceof org.bukkit.entity.Tameable || target instanceof org.bukkit.entity.Animals) {
                    target.setHealth(Math.min(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), target.getHealth() + amount));
                    target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.1);
                } else {
                    target.damage(amount, player);
                }
            }
        }
    }

    private void executeTransmutation(Player player, PlayerProfile profile, Block block) {
        if (block == null) return;
        Material blockType = block.getType();
        if (blockType != Material.STONE && blockType != Material.COBBLESTONE) {
            return;
        }
        
        int level = profile.getSkillLevel("Transmutation");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Landlord class and Transmutation skill first!");
            return;
        }

        double cost = 30.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Transmutation!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.YELLOW + "Transmuting block...");
        
        double r = Math.random();
        Material result;
        if (level >= 3 && r < 0.05 + (level - 3) * 0.03) {
            result = Material.DIAMOND_ORE;
        } else if (r < 0.15 + (level - 1) * 0.05) {
            result = Material.GOLD_ORE;
        } else if (r < 0.45 + (level - 1) * 0.05) {
            result = Material.IRON_ORE;
        } else {
            result = Material.COAL_ORE;
        }
        
        block.setType(result);
        block.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation().add(0.5, 1, 0.5), 15, 0.2, 0.2, 0.2, 0.2);
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);
    }

    private void executeBrewBurst(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Brew Burst");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Alchemist class and Brew Burst skill first!");
            return;
        }

        double cost = 25.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Brew Burst!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GREEN + "Brew Burst splash thrown!");
        
        org.bukkit.entity.ThrownPotion potion = player.launchProjectile(org.bukkit.entity.ThrownPotion.class);
        ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
        if (meta != null) {
            org.bukkit.potion.PotionEffectType effectType = org.bukkit.potion.PotionEffectType.REGENERATION;
            int duration = (6 + (level - 1) * 2) * 20;
            int amplifier = level >= 4 ? 1 : 0;
            meta.addCustomEffect(new org.bukkit.potion.PotionEffect(effectType, duration, amplifier), true);
            potionItem.setItemMeta(meta);
        }
        potion.setItem(potionItem);
        player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_THROW, 1f, 1f);
    }

    @EventHandler
    public void onProjectileHit(org.bukkit.event.entity.ProjectileHitEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Snowball snowball) {
            if (snowball.hasMetadata("arcane_missile_lvl")) {
                int level = snowball.getMetadata("arcane_missile_lvl").get(0).asInt();
                double damage = 8.0 + (level - 1) * 2.5;
                snowball.getWorld().spawnParticle(Particle.SPELL_WITCH, snowball.getLocation(), 15, 0.2, 0.2, 0.2, 0.1);
                snowball.getWorld().playSound(snowball.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1.5f);
                if (event.getHitEntity() instanceof LivingEntity target) {
                    if (snowball.getShooter() instanceof Player shooter) {
                        target.damage(damage, shooter);
                    } else {
                        target.damage(damage);
                    }
                }
            }
        } else if (event.getEntity() instanceof org.bukkit.entity.SmallFireball fireball) {
            if (fireball.hasMetadata("fireball_lvl")) {
                int level = fireball.getMetadata("fireball_lvl").get(0).asInt();
                double damage = 12.0 + (level - 1) * 3.0;
                int fireTicks = (int) ((2.0 + (level - 1) * 0.5) * 20);
                Location loc = fireball.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.5, 0.5, 0.5, 0.2);
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.2f);
                
                double radius = 3.0;
                for (Entity entity : fireball.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity target && entity != fireball.getShooter()) {
                        if (fireball.getShooter() instanceof Player shooter) {
                            target.damage(damage, shooter);
                        } else {
                            target.damage(damage);
                        }
                        target.setFireTicks(fireTicks);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPotionEffect(org.bukkit.event.entity.EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getAction() == org.bukkit.event.entity.EntityPotionEffectEvent.Action.ADDED) {
                org.bukkit.potion.PotionEffect effect = event.getNewEffect();
                if (effect != null && event.getCause().name().equals("POTION")) {
                    PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
                    if (profile != null) {
                        int level = profile.getSkillLevel("Catalyst");
                        if (level > 0) {
                            double durationBonus = 1.30 + (level - 1) * 0.10;
                            int newDuration = (int) (effect.getDuration() * durationBonus);
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                if (player.isOnline()) {
                                    player.addPotionEffect(new org.bukkit.potion.PotionEffect(effect.getType(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByFall(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL) {
                PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
                if (profile != null) {
                    int level = profile.getSkillLevel("Domain Lord");
                    if (level > 0) {
                        double reduction = 0.50 + (level - 1) * 0.10;
                        if (reduction >= 1.0) {
                            event.setCancelled(true);
                        } else {
                            event.setDamage(event.getDamage() * (1.0 - reduction));
                        }
                    }
                }
            }
        }
    }
}
