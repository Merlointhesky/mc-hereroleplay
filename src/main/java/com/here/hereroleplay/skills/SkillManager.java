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
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryAction;
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
    public void onFKeySwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        // Prevent the actual item swap
        event.setCancelled(true);
        
        handleSkillTrigger(player);
    }

    private void handleSkillTrigger(Player player) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Material handItem = mainHand.getType();
        String handItemName = handItem.name();

        ItemStack offHand = player.getInventory().getItemInOffHand();
        Material offHandItem = offHand.getType();

        boolean executed = false;

        if (player.isSneaking()) {
            // Shift + F abilities
            if (handItem == Material.STICK) {
                executeFireball(player, profile);
                executed = true;
            } else if (handItem == Material.BLAZE_ROD) {
                executeWaterWave(player, profile);
                executed = true;
            } else if (handItem == Material.SHIELD) {
                executeAegis(player, profile);
                executed = true;
            } else if (handItemName.contains("HOE")) {
                executeRejuvenation(player, profile);
                executed = true;
            } else if (handItemName.contains("AXE") && !handItemName.contains("PICKAXE")) {
                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock != null && org.bukkit.Tag.LOGS.isTagged(targetBlock.getType())) {
                    executeTimber(player, profile);
                    executed = true;
                }
            } else if (handItemName.contains("SHOVEL")) {
                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock != null && isShovellable(targetBlock.getType())) {
                    executeDiggyDiggyHole(player, profile, targetBlock);
                    executed = true;
                }
            } else if (handItemName.contains("PICKAXE")) {
                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock != null && isMineable(targetBlock.getType())) {
                    executeTunnelVision(player, profile, targetBlock);
                    executed = true;
                }
            } else if (isValidTransmutationBlock(handItem)) {
                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock != null) {
                    executeTransmutation(player, profile, targetBlock);
                    executed = true;
                }
            }

            // Fallback to off-hand shield if nothing executed in main hand
            if (!executed && offHandItem == Material.SHIELD) {
                executeAegis(player, profile);
            }
        } else {
            // F abilities
            if (handItemName.contains("SWORD")) {
                executeCleave(player, profile);
                executed = true;
            } else if (handItem == Material.SHIELD) {
                executeHolyNova(player, profile);
                executed = true;
            } else if (handItem == Material.STICK) {
                executeArcaneMissile(player, profile);
                executed = true;
            } else if (handItem == Material.BLAZE_ROD) {
                executeChainLightning(player, profile);
                executed = true;
            } else if (handItem == Material.BOW) {
                executeQuickShot(player, profile);
                executed = true;
            } else if (handItemName.contains("AXE") && !handItemName.contains("PICKAXE")) {
                executeBoomerangThrow(player, profile);
                executed = true;
            } else if (handItem == Material.TRIDENT) {
                executeLaserDot(player, profile);
                executed = true;
            } else if (handItem == Material.MACE) {
                executeThunderWave(player, profile);
                executed = true;
            }

            // Fallback to off-hand shield if nothing executed in main hand
            if (!executed && offHandItem == Material.SHIELD) {
                executeHolyNova(player, profile);
            }
        }
    }

    private void executeTimber(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Timber");
        if (level == 0) {
            return;
        }

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Timber!");
            return;
        }
        
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock != null && org.bukkit.Tag.LOGS.isTagged(targetBlock.getType())) {
            profile.setCurrentMana(profile.getCurrentMana() - cost);
            player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Timber" + ChatColor.GREEN + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
            
            int broken = 0;
            int maxBroken = 10 + (level - 1) * 3;
            Location loc = targetBlock.getLocation();
            while (org.bukkit.Tag.LOGS.isTagged(loc.getBlock().getType()) && broken < maxBroken) {
                loc.getBlock().breakNaturally(player.getInventory().getItemInMainHand());
                loc.add(0, 1, 0);
                broken++;
            }
        }
    }

    private void executeDiggyDiggyHole(Player player, PlayerProfile profile, Block startBlock) {
        int level = profile.getSkillLevel("Diggy Diggy Hole");
        if (level == 0) {
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
                    b.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, b.getLocation().add(0.5, 0.5, 0.5), 5, 0.2, 0.2, 0.2, 0.05);
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
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(x, 0.1, z), 2, 0, 0, 0, 0);
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
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, durationTicks, 9)); // Resistance X = 100% reduction
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 15, 0.5, 0.5, 0.5, 0.1);
    }

    private void executeHolyNova(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Holy Nova");
        if (level == 0) {
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

    private void executeTransmutation(Player player, PlayerProfile profile, Block center) {
        int level = profile.getSkillLevel("Transmutation");
        if (level == 0) {
            return;
        }

        double cost = 30.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Transmutation!");
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Material heldType = heldItem.getType();
        if (!isValidTransmutationBlock(heldType)) {
            player.sendMessage(ChatColor.RED + "You cannot transmute into this block type!");
            return;
        }

        Material targetType = center.getType();
        if (!isValidTransmutationBlock(targetType)) {
            player.sendMessage(ChatColor.RED + "You cannot transmute this block type!");
            return;
        }

        // Consume 1 block from player's hand
        if (heldItem.getAmount() > 1) {
            heldItem.setAmount(heldItem.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.YELLOW + "Transmuting blocks around target...");
        center.getWorld().playSound(center.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

        int radius = 1 + level;
        double radiusSq = radius * radius;
        Location centerLoc = center.getLocation();

        java.util.Queue<Block> queue = new java.util.LinkedList<>();
        java.util.Set<Block> visited = new java.util.HashSet<>();
        List<Block> blocksToChange = new ArrayList<>();

        queue.add(center);
        visited.add(center);

        org.bukkit.block.BlockFace[] faces = {
            org.bukkit.block.BlockFace.UP,
            org.bukkit.block.BlockFace.DOWN,
            org.bukkit.block.BlockFace.NORTH,
            org.bukkit.block.BlockFace.SOUTH,
            org.bukkit.block.BlockFace.EAST,
            org.bukkit.block.BlockFace.WEST
        };

        while (!queue.isEmpty()) {
            Block current = queue.poll();
            blocksToChange.add(current);

            for (org.bukkit.block.BlockFace face : faces) {
                Block neighbor = current.getRelative(face);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    if (neighbor.getType() == targetType) {
                        if (neighbor.getLocation().distanceSquared(centerLoc) <= radiusSq) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        for (Block b : blocksToChange) {
            b.setType(heldType);
            b.getWorld().spawnParticle(Particle.ENCHANT, b.getLocation().add(0.5, 0.5, 0.5), 1, 0.1, 0.1, 0.05);
        }
    }

    @EventHandler
    public void onProjectileHit(org.bukkit.event.entity.ProjectileHitEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Snowball snowball) {
            if (snowball.hasMetadata("arcane_missile_lvl")) {
                int level = snowball.getMetadata("arcane_missile_lvl").get(0).asInt();
                double damage = 8.0 + (level - 1) * 2.5;
                snowball.getWorld().spawnParticle(Particle.WITCH, snowball.getLocation(), 15, 0.2, 0.2, 0.2, 0.1);
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
        return org.bukkit.Tag.MINEABLE_SHOVEL.isTagged(mat);
    }

    private boolean isMineable(Material mat) {
        if (mat == Material.AIR || mat == Material.BEDROCK || mat == Material.BARRIER) return false;
        return org.bukkit.Tag.MINEABLE_PICKAXE.isTagged(mat);
    }

    private boolean isCropBlock(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || mat == Material.POTATOES || 
               mat == Material.BEETROOTS || mat == Material.COCOA || mat == Material.NETHER_WART || 
               mat == Material.SWEET_BERRY_BUSH || mat == Material.MELON_STEM || mat == Material.PUMPKIN_STEM;
    }

    private void executeChainLightning(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Chain Lightning");
        if (level == 0) return;

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Chain Lightning!");
            return;
        }

        // Raycast up to 15 blocks
        Location eyeLoc = player.getEyeLocation();
        org.bukkit.util.Vector dir = eyeLoc.getDirection().normalize();
        LivingEntity firstTarget = null;
        for (double d = 0; d < 15; d += 0.5) {
            Location checkLoc = eyeLoc.clone().add(dir.clone().multiply(d));
            checkLoc.getWorld().spawnParticle(Particle.ENCHANTED_HIT, checkLoc, 1, 0, 0, 0, 0);
            for (Entity entity : checkLoc.getWorld().getNearbyEntities(checkLoc, 0.5, 0.5, 0.5)) {
                if (entity instanceof LivingEntity target && target != player) {
                    firstTarget = target;
                    break;
                }
            }
            if (firstTarget != null) break;
        }

        if (firstTarget == null) {
            player.sendMessage(ChatColor.GRAY + "No target found in direction.");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.YELLOW + "You used " + ChatColor.GOLD + "Chain Lightning" + ChatColor.YELLOW + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 2f);

        double damage = 10.0 + (level - 1) * 2.0;
        int maxJumps = 1 + (level / 10);
        java.util.Set<LivingEntity> hitSet = new java.util.HashSet<>();

        LivingEntity current = firstTarget;
        double currentDamage = damage;

        for (int jump = 0; jump <= maxJumps; jump++) {
            hitSet.add(current);
            current.getWorld().strikeLightningEffect(current.getLocation());
            current.damage(currentDamage, player);

            // Find next target
            LivingEntity next = null;
            double bestDistSq = Double.MAX_VALUE;
            for (Entity e : current.getNearbyEntities(8, 8, 8)) {
                if (e instanceof LivingEntity candidate && candidate != player && !hitSet.contains(candidate)) {
                    double distSq = candidate.getLocation().distanceSquared(current.getLocation());
                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        next = candidate;
                    }
                }
            }

            if (next == null) break;

            // Draw line between current and next
            drawParticleLine(current.getLocation().add(0, 1, 0), next.getLocation().add(0, 1, 0));
            currentDamage = Math.max(5.0, currentDamage * 0.95);
            current = next;
        }
    }

    private void drawParticleLine(Location loc1, Location loc2) {
        double dist = loc1.distance(loc2);
        org.bukkit.util.Vector dir = loc2.toVector().subtract(loc1.toVector()).normalize();
        for (double d = 0; d < dist; d += 0.5) {
            Location p = loc1.clone().add(dir.clone().multiply(d));
            p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, p, 1, 0, 0, 0, 0);
        }
    }

    private void executeWaterWave(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Water Wave");
        if (level == 0) return;

        double cost = 30.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Water Wave!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.BLUE + "You used " + ChatColor.AQUA + "Water Wave" + ChatColor.BLUE + "!");
        player.playSound(player.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1f, 1f);

        double radius = 4.0 + level * 0.5;
        double damage = 5.0 + level * 1.5;
        double force = 1.0 + level * 0.15;

        // Push and damage mobs
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity target && target != player) {
                target.damage(damage, player);
                org.bukkit.util.Vector push = target.getLocation().toVector().subtract(player.getLocation().toVector());
                push.setY(0);
                if (push.lengthSquared() > 0) {
                    push.normalize();
                } else {
                    push = player.getLocation().getDirection().setY(0).normalize();
                }
                push.multiply(force).setY(0.35);
                target.setVelocity(push);
            }
        }

        // Place temporary water blocks perimeter ring
        List<Block> changedBlocks = new ArrayList<>();
        Location center = player.getLocation();
        int r = 2;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (Math.abs(x) == r || Math.abs(z) == r) {
                    Block b = center.getBlock().getRelative(x, 0, z);
                    if (b.getType() == Material.AIR || b.getType() == Material.CAVE_AIR) {
                        b.setType(Material.WATER);
                        changedBlocks.add(b);
                    }
                }
            }
        }

        if (!changedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Block b : changedBlocks) {
                    if (b.getType() == Material.WATER) {
                        b.setType(Material.AIR);
                    }
                }
            }, 20L);
        }
    }

    private void executeBoomerangThrow(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Boomerang Throw");
        if (level == 0) return;

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Boomerang Throw!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GOLD + "You used " + ChatColor.YELLOW + "Boomerang Throw" + ChatColor.GOLD + "!");

        ItemStack axeStack = player.getInventory().getItemInMainHand().clone();
        axeStack.setAmount(1);
        org.bukkit.entity.Item item = player.getWorld().dropItem(player.getEyeLocation(), axeStack);
        item.setPickupDelay(32767);
        item.setGravity(false);

        org.bukkit.util.Vector direction = player.getLocation().getDirection().normalize();
        item.setVelocity(direction.multiply(1.2));

        double damage = 8.0 + level * 2.0;

        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;
            final int maxDistanceTicks = 12;
            final java.util.Set<java.util.UUID> hitEntities = new java.util.HashSet<>();

            @Override
            public void run() {
                if (!item.isValid() || !player.isOnline()) {
                    item.remove();
                    cancel();
                    return;
                }

                ticks++;

                if (ticks <= maxDistanceTicks) {
                    item.setVelocity(direction.multiply(1.2));
                } else {
                    org.bukkit.util.Vector toPlayer = player.getLocation().add(0, 1, 0).toVector().subtract(item.getLocation().toVector());
                    if (toPlayer.lengthSquared() < 1.5) {
                        item.remove();
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                        cancel();
                        return;
                    }
                    item.setVelocity(toPlayer.normalize().multiply(1.2));
                }

                item.getWorld().spawnParticle(Particle.CRIT, item.getLocation(), 3, 0.1, 0.1, 0.1, 0.05);
                if (ticks % 3 == 0) {
                    item.getWorld().playSound(item.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1.5f);
                }

                for (Entity entity : item.getNearbyEntities(1.2, 1.2, 1.2)) {
                    if (entity instanceof LivingEntity target && target != player && !hitEntities.contains(target.getUniqueId())) {
                        hitEntities.add(target.getUniqueId());
                        target.damage(damage, player);
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 1.2f);
                    }
                }

                if (ticks > 40) {
                    item.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void executeLaserDot(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Laser DOT");
        if (level == 0) return;

        double cost = 20.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Laser DOT!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.DARK_PURPLE + "You used " + ChatColor.LIGHT_PURPLE + "Laser DOT" + ChatColor.DARK_PURPLE + "!");

        double damagePerTick = 2.0 + level * 0.5;

        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.getInventory().getItemInMainHand().getType() != Material.TRIDENT || ticks >= 60) {
                    cancel();
                    return;
                }

                ticks += 5;

                Location eyeLoc = player.getEyeLocation();
                org.bukkit.util.Vector dir = eyeLoc.getDirection().normalize();
                LivingEntity hitTarget = null;

                for (double d = 0; d < 20.0; d += 0.5) {
                    Location checkLoc = eyeLoc.clone().add(dir.clone().multiply(d));
                    checkLoc.getWorld().spawnParticle(Particle.DUST, checkLoc, 1, 0, 0, 0, 0, new Particle.DustOptions(org.bukkit.Color.RED, 1.0f));
                    for (Entity e : checkLoc.getWorld().getNearbyEntities(checkLoc, 0.4, 0.4, 0.4)) {
                        if (e instanceof LivingEntity target && target != player) {
                            hitTarget = target;
                            break;
                        }
                    }
                    if (hitTarget != null) break;
                }

                if (hitTarget != null) {
                    hitTarget.damage(damagePerTick, player);
                    hitTarget.getWorld().spawnParticle(Particle.FLASH, hitTarget.getLocation().add(0, 1, 0), 1);
                    hitTarget.getWorld().playSound(hitTarget.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.5f, 2f);
                }

                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.3f, 2f);
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private void executeThunderWave(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Thunder Wave");
        if (level == 0) return;

        double cost = 25.0;
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for Thunder Wave!");
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.DARK_RED + "You used " + ChatColor.GOLD + "Thunder Wave" + ChatColor.DARK_RED + "!");

        // Strike visual lightning at player
        player.getWorld().strikeLightningEffect(player.getLocation());

        double radius = 5.0 + level * 0.5;
        double damage = 10.0 + level * 2.0;
        double pushForce = 1.5 + level * 0.15;

        // Damage and pushback nearby
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity target && target != player) {
                target.damage(damage, player);
                org.bukkit.util.Vector push = target.getLocation().toVector().subtract(player.getLocation().toVector());
                push.setY(0);
                if (push.lengthSquared() > 0) {
                    push.normalize();
                } else {
                    push = new org.bukkit.util.Vector(0, 0, 0);
                }
                push.multiply(pushForce).setY(0.4);
                target.setVelocity(push);
            }
        }

        player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.1);
    }
}
