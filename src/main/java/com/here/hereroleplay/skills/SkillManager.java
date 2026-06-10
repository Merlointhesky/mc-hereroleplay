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
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

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
            } else if (handItem == Material.BOW) {
                // Quick Shot (Bow, F-Key)
                executeQuickShot(player, profile);
            }
        } else {
            // Right Click / Shift-Right Click
            if (player.isSneaking()) {
                if (handItem == Material.SHIELD) {
                    executeAegis(player, profile);
                } else if (handItemName.contains("HOE")) {
                    executeRejuvenation(player, profile);
                } else if (event != null && event.getClickedBlock() != null) {
                    Block clickedBlock = event.getClickedBlock();
                    if (handItemName.contains("AXE") && clickedBlock.getType().name().contains("LOG")) {
                        executeTimber(player, profile);
                    } else if (handItemName.contains("SHOVEL") && isShovellable(clickedBlock.getType())) {
                        executeDiggyDiggyHole(player, profile, clickedBlock);
                    } else if (handItemName.contains("PICKAXE") && isMineable(clickedBlock.getType())) {
                        executeTunnelVision(player, profile, clickedBlock);
                    } else {
                        executeTransmutation(player, profile, clickedBlock);
                    }
                }
            } else {
                // Non-sneaking right click
                if (handItem == Material.STICK) {
                    executeFireball(player, profile);
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

    private void executeDiggyDiggyHole(Player player, PlayerProfile profile, Block startBlock) {
        int level = profile.getSkillLevel("Diggy Diggy Hole");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Miner class and Diggy Diggy Hole skill first!");
            return;
        }

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Diggy Diggy Hole!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Diggy Diggy Hole" + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1f, 1f);

        int maxBroken = 10 + (level - 1) * 3;
        List<Block> queue = new ArrayList<>();
        List<Block> toBreak = new ArrayList<>();
        queue.add(startBlock);
        
        int index = 0;
        while (index < queue.size() && toBreak.size() < maxBroken) {
            Block current = queue.get(index++);
            if (isShovellable(current.getType()) && !toBreak.contains(current)) {
                toBreak.add(current);
                
                // Add neighbors (6 directions)
                for (org.bukkit.block.BlockFace face : org.bukkit.block.BlockFace.values()) {
                    if (face.isCartesian()) { // Up, Down, North, South, East, West
                        Block neighbor = current.getRelative(face);
                        if (!queue.contains(neighbor) && isShovellable(neighbor.getType())) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        for (Block b : toBreak) {
            b.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    private void executeTunnelVision(Player player, PlayerProfile profile, Block startBlock) {
        int level = profile.getSkillLevel("Tunnel Vision");
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "You must unlock the Miner class and Tunnel Vision skill first!");
            return;
        }

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Tunnel Vision!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Tunnel Vision" + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);

        int maxBroken = 10 + (level - 1) * 3;
        org.bukkit.block.BlockFace face = player.getFacing();
        
        // Determine orthogonal vectors for 3x3 grid
        int dx1 = 0, dy1 = 0, dz1 = 0; 
        int dx2 = 0, dy2 = 0, dz2 = 0; 
        
        if (face == org.bukkit.block.BlockFace.NORTH || face == org.bukkit.block.BlockFace.SOUTH) {
            dx1 = 1; dy1 = 0; dz1 = 0; 
            dx2 = 0; dy2 = 1; dz2 = 0; 
        } else if (face == org.bukkit.block.BlockFace.EAST || face == org.bukkit.block.BlockFace.WEST) {
            dx1 = 0; dy1 = 0; dz1 = 1; 
            dx2 = 0; dy2 = 1; dz2 = 0; 
        } else { 
            dx1 = 1; dy1 = 0; dz1 = 0; 
            dx2 = 0; dy2 = 0; dz2 = 1; 
        }
        
        int broken = 0;
        Block currentCenter = startBlock;
        
        for (int depth = 0; depth < 100 && broken < maxBroken; depth++) {
            boolean brokenAnyAtThisDepth = false;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (broken >= maxBroken) break;
                    Block b = currentCenter.getRelative(i * dx1 + j * dx2, i * dy1 + j * dy2, i * dz1 + j * dz2);
                    if (isMineable(b.getType())) {
                        b.breakNaturally(player.getInventory().getItemInMainHand());
                        broken++;
                        brokenAnyAtThisDepth = true;
                    }
                }
            }
            if (!brokenAnyAtThisDepth && depth > 0) {
                break;
            }
            currentCenter = currentCenter.getRelative(face);
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

        double radius = 4.0 + (level - 1) * 0.5;

        // Scan for crops in radius
        List<Block> cropBlocks = new ArrayList<>();
        Location center = player.getLocation();
        int r = (int) Math.ceil(radius);
        for (int x = -r; x <= r; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x*x + z*z <= radius*radius) {
                        Block b = center.getBlock().getRelative(x, y, z);
                        if (isCropBlock(b.getType())) {
                            cropBlocks.add(b);
                        }
                    }
                }
            }
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);

        if (cropBlocks.size() >= 3) {
            player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Rejuvenation (Crop Growth)" + ChatColor.GREEN + "!");
            player.playSound(player.getLocation(), Sound.ITEM_BONE_MEAL_USE, 1f, 1f);
            
            // Apply growth effect (+3 growth stages)
            for (Block b : cropBlocks) {
                org.bukkit.block.data.BlockData data = b.getBlockData();
                if (data instanceof Ageable ageable) {
                    int newAge = Math.min(ageable.getMaximumAge(), ageable.getAge() + 3);
                    ageable.setAge(newAge);
                    b.setBlockData(ageable);
                    b.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, b.getLocation().add(0.5, 0.5, 0.5), 5, 0.2, 0.2, 0.2, 0.05);
                }
            }
        } else {
            player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Rejuvenation (AoE Heal)" + ChatColor.GREEN + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
            
            double healAmount = 6.0 + (level - 1) * 2.0;
            
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
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, durationTicks, 9)); // Resistance X = 100% reduction
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

    private void executeTransmutation(Player player, PlayerProfile profile, Block center) {
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

        Material heldType = player.getInventory().getItemInMainHand().getType();
        if (!heldType.isBlock() || heldType == Material.AIR || heldType.isEdible() || heldType.name().contains("SHIELD")) {
            player.sendMessage(ChatColor.RED + "You must hold a valid block to transmute!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.YELLOW + "Transmuting blocks around target...");
        center.getWorld().playSound(center.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

        int radius = 1 + level;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + y*y + z*z <= radius*radius) {
                        Block b = center.getRelative(x, y, z);
                        Material t = b.getType();
                        if (t != Material.AIR && t != Material.BEDROCK && t != Material.BARRIER) {
                            b.setType(heldType);
                            b.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, b.getLocation().add(0.5, 0.5, 0.5), 1, 0.1, 0.1, 0.1, 0.05);
                        }
                    }
                }
            }
        }
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
                            double durationBonus = 1.0 + level * 0.01;
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
                        double reduction = level * 0.01;
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

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile != null && profile.getSkillLevel("Master of the Craft") > 0) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() != Material.AIR) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline()) {
                        ItemStack duplicate = item.clone();
                        if (!player.getInventory().addItem(duplicate).isEmpty()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), duplicate);
                        }
                        player.sendMessage("§a★ Master of the Craft: Enchantment Duplicated!");
                        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onBrewingStandClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.BREWING) return;
        
        int slot = event.getSlot();
        if (slot == 0 || slot == 1 || slot == 2) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType() != Material.AIR) {
                PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
                if (profile != null && profile.getSkillLevel("Master of the Craft") > 0) {
                    InventoryAction action = event.getAction();
                    if (action == InventoryAction.PICKUP_ALL || 
                        action == InventoryAction.PICKUP_HALF || 
                        action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        
                        ItemStack duplicate = clicked.clone();
                        duplicate.setAmount(1); 
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (player.isOnline()) {
                                if (!player.getInventory().addItem(duplicate).isEmpty()) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), duplicate);
                                }
                                player.sendMessage("§a★ Master of the Craft: Potion Duplicated!");
                                player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1f, 1f);
                            }
                        });
                    }
                }
            }
        }
    }

    private boolean isShovellable(Material mat) {
        String name = mat.name();
        return name.contains("DIRT") || name.contains("GRASS") || name.contains("GRAVEL") || 
               name.contains("SAND") || name.contains("CLAY") || name.contains("SOIL") || 
               name.contains("MUD") || name.contains("FARMLAND") || name.contains("PATH");
    }

    private boolean isMineable(Material mat) {
        if (mat == Material.AIR || mat == Material.BEDROCK || mat == Material.BARRIER) return false;
        String name = mat.name();
        return name.contains("ORE") || name.contains("STONE") || name.contains("DEEPSLATE") || 
               name.contains("ANDESITE") || name.contains("DIORITE") || name.contains("GRANITE") || 
               name.contains("TUFF") || name.contains("NETHERRACK") || name.contains("BASALT") || 
               name.contains("BLACKSTONE") || name.contains("OBSIDIAN") || mat == Material.COBBLESTONE;
    }

    private boolean isCropBlock(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || mat == Material.POTATOES || 
               mat == Material.BEETROOTS || mat == Material.COCOA || mat == Material.NETHER_WART || 
               mat == Material.SWEET_BERRY_BUSH || mat == Material.MELON_STEM || mat == Material.PUMPKIN_STEM;
    }
}
