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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class SkillManager implements Listener {

    private final HereRolePlay plugin;
    private final java.util.Map<java.util.UUID, Long> lastDamageTime = new java.util.concurrent.ConcurrentHashMap<>();

    public SkillManager(HereRolePlay plugin) {
        this.plugin = plugin;
        startPassiveTasks();
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

        // Prioritize off-hand shield skills if the player is blocking
        if (offHandItem == Material.SHIELD && player.isBlocking()) {
            if (player.isSneaking()) {
                executed = executeAegis(player, profile);
            } else {
                executed = executeHolyNova(player, profile);
            }
        }

        if (!executed) {
            if (player.isSneaking()) {
                // Shift + F abilities
                if (handItem == Material.STICK) {
                    executeQuicksand(player, profile);
                    executed = true;
                } else if (handItem == Material.BLAZE_ROD) {
                    executeFireRain(player, profile);
                    executed = true;
                } else if (handItem == Material.TROPICAL_FISH) {
                    executeWaterWave(player, profile);
                    executed = true;
                } else if (handItem == Material.BREEZE_ROD) {
                    executeGaleForce(player, profile);
                    executed = true;
                } else if (handItem == Material.BONE) {
                    executeRaiseUndead(player, profile);
                    executed = true;
                } else if (handItemName.contains("SWORD")) {
                    executeAssassination(player, profile);
                    executed = true;
                } else if (handItem == Material.SHIELD) {
                    executed = executeAegis(player, profile);
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
            } else {
                // F abilities
                if (handItemName.contains("SWORD")) {
                    executeCleave(player, profile);
                    executed = true;
                } else if (handItem == Material.SHIELD) {
                    executed = executeHolyNova(player, profile);
                } else if (handItem == Material.STICK) {
                    executeRockBlast(player, profile);
                    executed = true;
                } else if (handItem == Material.BLAZE_ROD) {
                    executeFireball(player, profile);
                    executed = true;
                } else if (handItem == Material.TROPICAL_FISH) {
                    executeWaterCannon(player, profile);
                    executed = true;
                } else if (handItem == Material.BREEZE_ROD) {
                    executeWindBlast(player, profile);
                    executed = true;
                } else if (handItem == Material.BONE) {
                    executeSoulDrain(player, profile);
                    executed = true;
                } else if (handItem == Material.CROSSBOW) {
                    executePiercingBolt(player, profile);
                    executed = true;
                } else if (handItemName.endsWith("_SPEAR")) {
                    if (player.isInsideVehicle() && player.getVehicle() instanceof org.bukkit.entity.AbstractHorse) {
                        executeSpearKnight(player, profile);
                        executed = true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Spear Knight requires you to be mounted on a horse!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    }
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
            }
        }
    }

    private void executeTimber(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Timber");
        if (level == 0) {
            return;
        }

        double cost = 20.0;
        if (!checkMana(player, profile, cost, "Timber")) return;
        
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
        if (!checkMana(player, profile, cost, "Diggy Diggy Hole")) return;

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
        if (!checkMana(player, profile, cost, "Tunnel Vision")) return;

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
        if (!checkMana(player, profile, cost, "Cleave")) return;

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

    private void executeRockBlast(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Rock Blast");
        if (level == 0) {
            return;
        }

        double cost = 15.0;
        if (!checkMana(player, profile, cost, "Rock Blast")) {
            return;
        }

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GRAY + "You used " + ChatColor.DARK_GRAY + "Rock Blast" + ChatColor.GRAY + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1.5f);
        
        org.bukkit.entity.Snowball projectile = player.launchProjectile(org.bukkit.entity.Snowball.class);
        projectile.setCustomName("Rock Blast");
        projectile.setGlowing(true);
        projectile.setMetadata("rock_blast_lvl", new FixedMetadataValue(plugin, level));
        projectile.setVelocity(player.getLocation().getDirection().multiply(2.0));
    }

    private void executeFireball(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Fireball");
        if (level == 0) {
            return;
        }

        double cost = 25.0;
        if (!checkMana(player, profile, cost, "Fireball")) return;

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
        if (!checkMana(player, profile, cost, "Quick Shot")) return;

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
        if (!checkMana(player, profile, cost, "Rejuvenation")) return;

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

    private boolean executeAegis(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Aegis");
        if (level == 0) {
            return false;
        }

        double cost = 40.0;
        if (!checkMana(player, profile, cost, "Aegis")) return false;

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GOLD + "Shield Aegis activated!");
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 0.8f);
        
        int durationTicks = (int) ((10.0 + (level - 1) * 2.5) * 20);
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, durationTicks, 9)); // Resistance X = 100% reduction
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 15, 0.5, 0.5, 0.5, 0.1);
        return true;
    }

    private boolean executeHolyNova(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Holy Nova");
        if (level == 0) {
            return false;
        }

        double cost = 35.0;
        if (!checkMana(player, profile, cost, "Holy Nova")) return false;

        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.YELLOW + "Holy Nova casted!");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 2f);
        
        double amount = 8.0 + (level - 1) * 2.0;
        double radius = 4.0 + (level - 1) * 0.5;
        
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 40, radius / 2.0, 0.5, radius / 2.0, 0.1);
        
        for (Entity entity : player.getNearbyEntities(radius, 2, radius)) {
            if (entity instanceof LivingEntity target && target != player) {
                if (isEnemy(target)) {
                    target.damage(amount, player);
                } else if (isFriend(target)) {
                    target.setHealth(Math.min(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), target.getHealth() + amount));
                    target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }
        return true;
    }

    private boolean isEnemy(LivingEntity target) {
        if (target instanceof org.bukkit.entity.Enemy ||
            target instanceof org.bukkit.entity.Slime ||
            target instanceof org.bukkit.entity.Ghast ||
            target instanceof org.bukkit.entity.Phantom ||
            target instanceof org.bukkit.entity.Shulker ||
            target instanceof org.bukkit.entity.EnderDragon) {
            return true;
        }

        try {
            org.bukkit.NamespacedKey customBossKey = new org.bukkit.NamespacedKey("heremobby", "custom_boss");
            org.bukkit.NamespacedKey customMobKey = new org.bukkit.NamespacedKey("heremobby", "custom_mob");
            if (target.getPersistentDataContainer().has(customBossKey, org.bukkit.persistence.PersistentDataType.STRING) ||
                target.getPersistentDataContainer().has(customMobKey, org.bukkit.persistence.PersistentDataType.STRING)) {
                return true;
            }
        } catch (Exception ignored) {}

        return false;
    }

    private boolean isFriend(LivingEntity target) {
        return target instanceof Player ||
               target instanceof org.bukkit.entity.Tameable ||
               target instanceof org.bukkit.entity.Animals ||
               target instanceof org.bukkit.entity.NPC ||
               target instanceof org.bukkit.entity.Golem ||
               target instanceof org.bukkit.entity.WaterMob ||
               target instanceof org.bukkit.entity.Ambient ||
               target instanceof org.bukkit.entity.Allay;
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
        if (!checkMana(player, profile, cost, "Transmutation")) return;

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
            if (snowball.hasMetadata("rock_blast_lvl")) {
                int level = snowball.getMetadata("rock_blast_lvl").get(0).asInt();
                double damage = 8.0 + (level - 1) * 2.5;
                snowball.getWorld().spawnParticle(Particle.BLOCK, snowball.getLocation(), 15, 0.2, 0.2, 0.2, 0.1, Material.COBBLESTONE.createBlockData());
                snowball.getWorld().playSound(snowball.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);
                if (event.getHitEntity() instanceof LivingEntity target) {
                    if (snowball.getShooter() instanceof Player shooter) {
                        target.damage(damage, shooter);
                    } else {
                        target.damage(damage);
                    }
                }

                Block hitBlock = event.getHitBlock();
                if (hitBlock == null && event.getHitEntity() != null) {
                    hitBlock = event.getHitEntity().getLocation().getBlock();
                }
                if (hitBlock != null) {
                    double radius = 1.0 + (level * 0.01);
                    double radiusSq = radius * radius;
                    Location center = hitBlock.getLocation().add(0.5, 0.5, 0.5);
                    int r = (int) Math.ceil(radius);
                    
                    List<Block> toFall = new ArrayList<>();
                    for (int x = -r; x <= r; x++) {
                        for (int y = -r; y <= r; y++) {
                            for (int z = -r; z <= r; z++) {
                                Block b = hitBlock.getRelative(x, y, z);
                                if (b.getType().isSolid() && !isBlacklisted(b.getType())) {
                                    if (b.getLocation().add(0.5, 0.5, 0.5).distanceSquared(center) <= radiusSq) {
                                        toFall.add(b);
                                    }
                                }
                            }
                        }
                    }
                    
                    for (Block b : toFall) {
                        org.bukkit.block.data.BlockData data = b.getBlockData();
                        b.setType(Material.AIR);
                        b.getWorld().spawnFallingBlock(b.getLocation().add(0.5, 0.0, 0.5), data);
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
        } else if (event.getEntity() instanceof org.bukkit.entity.WindCharge windCharge) {
            if (windCharge.hasMetadata("wind_blast_lvl")) {
                int level = windCharge.getMetadata("wind_blast_lvl").get(0).asInt();
                double damage = 8.0 + (level - 1) * 2.0;
                Location loc = windCharge.getLocation();
                loc.getWorld().spawnParticle(Particle.GUST_EMITTER_LARGE, loc, 1);
                loc.getWorld().playSound(loc, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1f, 1f);
                double radius = 4.0;
                for (Entity entity : windCharge.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity target && entity != windCharge.getShooter()) {
                        if (windCharge.getShooter() instanceof Player shooter) {
                            target.damage(damage, shooter);
                        } else {
                            target.damage(damage);
                        }
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
                        int level = Math.min(100, profile.getSkillLevel("Catalyst"));
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
                    int level = Math.min(100, profile.getSkillLevel("Domain Lord"));
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

    private void executeWaterWave(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Water Wave");
        if (level == 0) return;

        double cost = 30.0;
        if (!checkMana(player, profile, cost, "Water Wave")) return;

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
                java.util.Set<Block> toClear = new java.util.HashSet<>();
                for (Block b : changedBlocks) {
                    if (!b.getWorld().isChunkLoaded(b.getX() >> 4, b.getZ() >> 4)) continue;
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -1; dy <= 2; dy++) {
                            for (int dz = -2; dz <= 2; dz++) {
                                Block rel = b.getRelative(dx, dy, dz);
                                if (rel.getType() == Material.WATER) {
                                    toClear.add(rel);
                                }
                            }
                        }
                    }
                }
                for (Block b : toClear) {
                    b.setType(Material.AIR);
                }
            }, 200L);
        }
    }

    private boolean checkMana(Player player, PlayerProfile profile, double cost, String skillName) {
        if (profile.getCurrentMana() < cost) {
            player.sendMessage(ChatColor.RED + "Not enough mana for " + skillName + "!");
            player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.5f);
            return false;
        }
        return true;
    }

    private void executeWindBlast(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Wind Blast");
        if (level == 0) return;
        double cost = 20.0;
        if (!checkMana(player, profile, cost, "Wind Blast")) return;
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.DARK_AQUA + "You used " + ChatColor.AQUA + "Wind Blast" + ChatColor.DARK_AQUA + "!");
        org.bukkit.entity.WindCharge windCharge = player.launchProjectile(org.bukkit.entity.WindCharge.class);
        windCharge.setCustomName("Wind Blast");
        windCharge.setMetadata("wind_blast_lvl", new FixedMetadataValue(plugin, level));
        windCharge.setVelocity(player.getLocation().getDirection().multiply(1.5));
        player.playSound(player.getLocation(), Sound.ENTITY_WIND_CHARGE_THROW, 1f, 1.2f);
    }

    private void executeGaleForce(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Gale Force");
        if (level == 0) return;
        double cost = 25.0;
        if (!checkMana(player, profile, cost, "Gale Force")) return;
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.DARK_AQUA + "You used " + ChatColor.AQUA + "Gale Force" + ChatColor.DARK_AQUA + "!");
        
        double launchHeight = 1.0 + level * 0.05;
        org.bukkit.util.Vector launchVec = player.getVelocity();
        launchVec.setY(launchHeight);
        player.setVelocity(launchVec);
        
        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1f, 1f);
        player.getWorld().spawnParticle(Particle.GUST_EMITTER_SMALL, player.getLocation(), 10, 0.5, 0.2, 0.5, 0.1);
        
        double radius = 5.0;
        for (Entity entity : player.getNearbyEntities(radius, 3, radius)) {
            if (entity instanceof LivingEntity target && target != player && isEnemy(target)) {
                org.bukkit.util.Vector push = target.getLocation().toVector().subtract(player.getLocation().toVector());
                push.setY(0);
                if (push.lengthSquared() > 0) {
                    push.normalize();
                } else {
                    push = new org.bukkit.util.Vector(0, 0, 0);
                }
                push.multiply(1.5).setY(0.4);
                target.setVelocity(push);
                target.getWorld().spawnParticle(Particle.GUST, target.getLocation(), 3, 0.2, 0.2, 0.2, 0.1);
            }
        }
    }

    private void executeFireRain(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Fire Rain");
        if (level == 0) return;
        double cost = 30.0;
        if (!checkMana(player, profile, cost, "Fire Rain")) return;
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GOLD + "Channelling " + ChatColor.RED + "Fire Rain" + ChatColor.GOLD + "!");
        
        double damage = 4.0 + level * 0.5;
        
        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || player.getInventory().getItemInMainHand().getType() != Material.BLAZE_ROD || ticks >= 60) {
                    cancel();
                    return;
                }
                ticks += 2;
                
                double angle = Math.random() * Math.PI * 2;
                double r = Math.random() * 6.0;
                Location loc = player.getLocation().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
                
                Block highest = player.getWorld().getHighestBlockAt(loc);
                Location targetLoc = highest.getLocation().add(0.5, 1.0, 0.5);
                
                Location topLoc = targetLoc.clone().add(0, 5, 0);
                double steps = 10;
                for (int i = 0; i <= steps; i++) {
                    double pct = i / steps;
                    Location partLoc = topLoc.clone().add(targetLoc.clone().subtract(topLoc).multiply(pct));
                    partLoc.getWorld().spawnParticle(Particle.FLAME, partLoc, 1, 0, 0, 0, 0.02);
                }
                
                targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 0.5f, 1.5f);
                targetLoc.getWorld().spawnParticle(Particle.LAVA, targetLoc, 5, 0.2, 0.2, 0.2, 0.1);
                
                for (Entity entity : targetLoc.getWorld().getNearbyEntities(targetLoc, 2.0, 2.0, 2.0)) {
                    if (entity instanceof LivingEntity enemy && enemy != player && isEnemy(enemy)) {
                        enemy.damage(damage, player);
                        enemy.setFireTicks(40);
                    }
                }
                
                Block targetBlock = targetLoc.getBlock();
                if (targetBlock.getType() == Material.AIR || targetBlock.getType() == Material.CAVE_AIR) {
                    if (targetBlock.getRelative(org.bukkit.block.BlockFace.DOWN).getType().isSolid()) {
                        targetBlock.setType(Material.FIRE);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (targetBlock.getType() == Material.FIRE) {
                                targetBlock.setType(Material.AIR);
                            }
                        }, 100L);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void executeWaterCannon(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Water Cannon");
        if (level == 0) return;
        double cost = 25.0;
        if (!checkMana(player, profile, cost, "Water Cannon")) return;
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.BLUE + "You used " + ChatColor.AQUA + "Water Cannon" + ChatColor.BLUE + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1f, 1f);
        
        Location eyeLoc = player.getEyeLocation();
        org.bukkit.util.Vector dir = eyeLoc.getDirection().normalize();
        double range = 12.0;
        double pushForce = 1.0 + level * 0.1;
        
        for (double d = 0; d < range; d += 0.5) {
            Location checkLoc = eyeLoc.clone().add(dir.clone().multiply(d));
            checkLoc.getWorld().spawnParticle(Particle.SPLASH, checkLoc, 3, 0.1, 0.1, 0.1, 0.05);
            
            for (Entity entity : checkLoc.getWorld().getNearbyEntities(checkLoc, 1.2, 1.2, 1.2)) {
                if (entity instanceof LivingEntity target && target != player) {
                    org.bukkit.util.Vector push = target.getLocation().toVector().subtract(player.getLocation().toVector());
                    push.setY(0);
                    if (push.lengthSquared() > 0) {
                        push.normalize();
                    } else {
                        push = dir.clone().setY(0).normalize();
                    }
                    push.multiply(pushForce).setY(0.3);
                    target.setVelocity(push);
                    
                    target.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.JUMP_BOOST, 100, 200));
                    target.getWorld().spawnParticle(Particle.FALLING_WATER, target.getLocation().add(0, 1.5, 0), 10, 0.2, 0.2, 0.2, 0.05);
                }
            }
        }
    }

    private void executeQuicksand(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Quicksand");
        if (level == 0) return;
        double cost = 25.0;
        if (!checkMana(player, profile, cost, "Quicksand")) return;
        
        Block hitBlock = null;
        Location eyeLoc = player.getEyeLocation();
        org.bukkit.util.Vector dir = eyeLoc.getDirection().normalize();
        for (double d = 0; d < 25.0; d += 0.5) {
            Location checkLoc = eyeLoc.clone().add(dir.clone().multiply(d));
            checkLoc.getWorld().spawnParticle(Particle.FALLING_DUST, checkLoc, 1, 0, 0, 0, 0, Material.SAND.createBlockData());
            Block b = checkLoc.getBlock();
            if (b.getType().isSolid() && !isBlacklisted(b.getType())) {
                hitBlock = b;
                break;
            }
        }
        
        if (hitBlock == null) {
            player.sendMessage(ChatColor.RED + "Quicksand did not hit any solid blocks!");
            return;
        }
        
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.YELLOW + "You cast " + ChatColor.GOLD + "Quicksand" + ChatColor.YELLOW + "!");
        hitBlock.getWorld().playSound(hitBlock.getLocation(), Sound.BLOCK_SAND_BREAK, 1.5f, 0.8f);
        
        int radius = 1 + level / 5;
        double radiusSq = radius * radius;
        Location centerLoc = hitBlock.getLocation();
        
        List<Block> changedBlocks = new ArrayList<>();
        java.util.Map<Block, Material> originalTypes = new java.util.HashMap<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + z*z <= radiusSq) {
                        Block b = hitBlock.getRelative(x, y, z);
                        if (b.getType().isSolid() && !isBlacklisted(b.getType()) && b.getType() != Material.SAND) {
                            originalTypes.put(b, b.getType());
                            b.setType(Material.SAND);
                            changedBlocks.add(b);
                            b.getWorld().spawnParticle(Particle.BLOCK, b.getLocation().add(0.5, 1.0, 0.5), 3, 0.2, 0.2, 0.2, 0.05, Material.SAND.createBlockData());
                        }
                    }
                }
            }
        }
        
        for (Entity entity : hitBlock.getWorld().getNearbyEntities(centerLoc, radius + 1, 3, radius + 1)) {
            if (entity instanceof LivingEntity target && target != player && isEnemy(target)) {
                target.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, 100, 2));
                target.getWorld().spawnParticle(Particle.FALLING_DUST, target.getLocation().add(0, 1, 0), 8, 0.2, 0.2, 0.2, 0, Material.SAND.createBlockData());
            }
        }
        
        if (!changedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Block b : changedBlocks) {
                    if (b.getWorld().isChunkLoaded(b.getX() >> 4, b.getZ() >> 4)) {
                        Material orig = originalTypes.get(b);
                        if (orig != null && b.getType() == Material.SAND) {
                            b.setType(orig);
                        }
                    }
                }
            }, 200L);
        }
    }

    private void executeSpearKnight(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Spear Knight");
        if (level == 0) return;
        double cost = 30.0;
        if (!checkMana(player, profile, cost, "Spear Knight")) return;
        
        org.bukkit.entity.Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof org.bukkit.entity.AbstractHorse horse)) {
            player.sendMessage(ChatColor.RED + "You must be riding a horse to use Spear Knight!");
            return;
        }
        
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Spear Knight" + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1f, 1f);
        player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1f, 1.2f);
        
        org.bukkit.util.Vector direction = player.getLocation().getDirection().setY(0.15).normalize().multiply(1.8);
        horse.setVelocity(direction);
        
        horse.setMetadata("spear_knight_protect", new FixedMetadataValue(plugin, true));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (horse.isValid()) {
                horse.removeMetadata("spear_knight_protect", plugin);
            }
        }, 15L);
        
        double damage = 10.0 + level * 2.0;
        
        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;
            final java.util.Set<java.util.UUID> trampled = new java.util.HashSet<>();
            @Override
            public void run() {
                if (!player.isOnline() || !horse.isValid() || ticks >= 15) {
                    cancel();
                    return;
                }
                ticks++;
                
                horse.getWorld().spawnParticle(Particle.CRIT, horse.getLocation().add(0, 0.5, 0), 5, 0.5, 0.2, 0.5, 0.05);
                
                for (Entity entity : horse.getNearbyEntities(1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity target && target != player && target != horse && !trampled.contains(target.getUniqueId()) && isEnemy(target)) {
                        trampled.add(target.getUniqueId());
                        target.damage(damage, player);
                        
                        org.bukkit.util.Vector push = target.getLocation().toVector().subtract(horse.getLocation().toVector());
                        push.setY(0);
                        if (push.lengthSquared() > 0) {
                            push.normalize();
                        } else {
                            push = horse.getLocation().getDirection().setY(0).normalize();
                        }
                        push.multiply(1.2).setY(0.35);
                        target.setVelocity(push);
                        
                        target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation().add(0, 1, 0), 5);
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 0.8f);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void executeAssassination(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Assassination");
        if (level == 0) return;
        double cost = 30.0;
        if (!checkMana(player, profile, cost, "Assassination")) return;
        
        double range = 8.0 + (level - 1) * 0.2;
        LivingEntity target = null;
        double closestDist = Double.MAX_VALUE;
        
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e instanceof LivingEntity candidate && candidate != player && isEnemy(candidate)) {
                double dist = player.getLocation().distance(candidate.getLocation());
                if (dist < closestDist && dist <= range) {
                    closestDist = dist;
                    target = candidate;
                }
            }
        }
        
        if (target == null) {
            player.sendMessage(ChatColor.GRAY + "No target found in range for Assassination.");
            return;
        }
        
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.DARK_RED + "★ Assassination: Teleporting behind target!");
        
        org.bukkit.util.Vector targetDir = target.getLocation().getDirection().setY(0).normalize();
        Location teleportLoc = target.getLocation().subtract(targetDir.multiply(1.2));
        teleportLoc.setDirection(targetDir); 
        
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 15, 0.5, 1.0, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.2f);
        
        player.teleport(teleportLoc);
        
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 15, 0.5, 1.0, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.5f);
        
        double baseDamage = 5.0;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        double weaponDamage = getWeaponBaseDamage(weapon.getType());
        double totalBase = baseDamage + weaponDamage;
        
        int critDamageLvl = Math.min(100, profile.getSkillLevel("Critical Damage"));
        double critMultiplier = 1.5 + critDamageLvl * 0.01;
        double finalDamage = totalBase * critMultiplier;
        
        target.damage(finalDamage, player);
        target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.1);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.2f, 0.9f);
    }
    
    private double getWeaponBaseDamage(Material material) {
        String name = material.name();
        if (name.endsWith("_SPEAR")) {
            if (name.contains("WOODEN") || name.contains("GOLDEN")) return 5.0;
            if (name.contains("STONE")) return 6.0;
            if (name.contains("IRON")) return 7.0;
            if (name.contains("DIAMOND")) return 8.0;
            if (name.contains("NETHERITE")) return 9.0;
            return 6.0;
        }
        switch (material) {
            case WOODEN_SWORD: case GOLDEN_SWORD: return 4.0;
            case STONE_SWORD: return 5.0;
            case IRON_SWORD: return 6.0;
            case DIAMOND_SWORD: return 7.0;
            case NETHERITE_SWORD: return 8.0;
            case WOODEN_AXE: case GOLDEN_AXE: return 7.0;
            case STONE_AXE: return 9.0;
            case IRON_AXE: return 9.0;
            case DIAMOND_AXE: return 9.0;
            case NETHERITE_AXE: return 10.0;
            default: return 0.0;
        }
    }

    private void executePiercingBolt(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Piercing Bolt");
        if (level == 0) return;
        double cost = 25.0;
        if (!checkMana(player, profile, cost, "Piercing Bolt")) return;
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_GREEN + "Piercing Bolt" + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 0.5f);
        
        org.bukkit.entity.Arrow arrow = player.launchProjectile(org.bukkit.entity.Arrow.class);
        arrow.setCustomName("Piercing Bolt");
        arrow.setMetadata("piercing_bolt_lvl", new FixedMetadataValue(plugin, level));
        arrow.setCritical(true);
        arrow.setPierceLevel(5);
        arrow.setVelocity(player.getLocation().getDirection().multiply(2.2));
    }

    private void executeRaiseUndead(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Raise Undead");
        if (level == 0) return;
        double cost = 35.0;
        if (!checkMana(player, profile, cost, "Raise Undead")) return;
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.DARK_PURPLE + "You used " + ChatColor.GOLD + "Raise Undead" + ChatColor.DARK_PURPLE + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SKELETON_AMBIENT, 1f, 1.5f);
        
        int count = 1 + level / 10;
        Location spawnLoc = player.getLocation();
        
        String[] randomNames = {
            "Robert", "Alice", "Charles", "Arthur", "James", "Henry", "Thomas", "Mary", "John", "David",
            "Sarah", "William", "George", "Edward", "Elizabeth", "Richard", "Joseph", "Frank", "Albert",
            "Walter", "Harold", "Paul", "Ruth", "Helen", "Dorothy", "Margaret", "Donald", "Peter", "Steve",
            "Alex", "Bob", "Gary", "Brian", "Larry", "Kevin", "Mark", "Fred", "Harry", "Jack", "Garry",
            "Barry", "Larry", "Bruce", "Diana", "Clark", "Tony", "Natasha", "Clint"
        };
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 / count) * i;
            Location loc = spawnLoc.clone().add(Math.cos(angle) * 1.5, 0, Math.sin(angle) * 1.5);
            loc.setY(player.getWorld().getHighestBlockYAt(loc) + 1);
            
            org.bukkit.entity.Skeleton skeleton = player.getWorld().spawn(loc, org.bukkit.entity.Skeleton.class);
            String randomName = randomNames[random.nextInt(randomNames.length)];
            skeleton.setCustomName(ChatColor.GOLD + "Ex-" + randomName);
            skeleton.setCustomNameVisible(true);
            skeleton.setMetadata("summoner_uuid", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
            
            if (skeleton.getEquipment() != null) {
                skeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                
                Material[] weapons = {
                    Material.STONE_SWORD,
                    Material.IRON_SWORD,
                    Material.BOW,
                    Material.CROSSBOW,
                    Material.STONE_AXE,
                    Material.IRON_AXE,
                    Material.MACE
                };
                Material weapon = weapons[random.nextInt(weapons.length)];
                skeleton.getEquipment().setItemInMainHand(new ItemStack(weapon));
                
                if (weapon != Material.BOW && weapon != Material.CROSSBOW && random.nextBoolean()) {
                    skeleton.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
                }
            }
            
            skeleton.getWorld().spawnParticle(Particle.SOUL, skeleton.getLocation().add(0, 1, 0), 10, 0.2, 0.5, 0.2, 0.1);
            
            // Task to follow the player and target enemies
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (!skeleton.isValid() || !player.isOnline() || player.isDead()) {
                        cancel();
                        return;
                    }
                    
                    double distToPlayerSq = skeleton.getLocation().distanceSquared(player.getLocation());
                    
                    // 1. Teleport if extremely far
                    if (distToPlayerSq > 20.0 * 20.0) {
                        skeleton.teleport(player.getLocation());
                        skeleton.getWorld().spawnParticle(Particle.PORTAL, skeleton.getLocation(), 10, 0.2, 0.5, 0.2, 0.1);
                        skeleton.setTarget(null);
                        return;
                    }
                    
                    // 2. Clear target if too far from player
                    LivingEntity currentTarget = skeleton.getTarget();
                    if (currentTarget != null && (!currentTarget.isValid() || currentTarget.isDead() || distToPlayerSq > 16.0 * 16.0)) {
                        skeleton.setTarget(null);
                        currentTarget = null;
                    }
                    
                    // 3. Scan for target if we don't have one
                    if (currentTarget == null) {
                        Material mainHand = skeleton.getEquipment() != null ? skeleton.getEquipment().getItemInMainHand().getType() : Material.AIR;
                        boolean isRanged = mainHand == Material.BOW || mainHand == Material.CROSSBOW;
                        double scanRange = isRanged ? 25.0 : 12.0;
                        LivingEntity bestTarget = null;
                        double bestDistSq = scanRange * scanRange;
                        
                        for (Entity entity : skeleton.getNearbyEntities(scanRange, scanRange, scanRange)) {
                            if (entity instanceof org.bukkit.entity.Monster monster && monster.isValid() && !monster.isDead()) {
                                if (monster.hasMetadata("summoner_uuid")) {
                                    continue;
                                }
                                double distSq = skeleton.getLocation().distanceSquared(monster.getLocation());
                                if (distSq < bestDistSq) {
                                    bestDistSq = distSq;
                                    bestTarget = monster;
                                }
                            }
                        }
                        
                        if (bestTarget != null) {
                            skeleton.setTarget(bestTarget);
                            currentTarget = bestTarget;
                        }
                    }
                    
                    // 4. If we have no target, follow player
                    if (currentTarget == null) {
                        if (distToPlayerSq > 4.0 * 4.0) {
                            skeleton.getPathfinder().moveTo(player, 1.25);
                        }
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (skeleton.isValid()) {
                    skeleton.getWorld().spawnParticle(Particle.SMOKE, skeleton.getLocation().add(0, 1, 0), 10, 0.2, 0.5, 0.2, 0.1);
                    skeleton.remove();
                }
            }, 600L);
        }
    }

    private boolean isUndead(LivingEntity living) {
        if (living == null) return false;
        if (living.hasMetadata("summoner_uuid")) {
            return true;
        }
        try {
            if (living.getCategory() == org.bukkit.entity.EntityCategory.UNDEAD) {
                return true;
            }
        } catch (Throwable ignored) {}
        
        org.bukkit.entity.EntityType type = living.getType();
        if (type == org.bukkit.entity.EntityType.SKELETON ||
            type == org.bukkit.entity.EntityType.WITHER_SKELETON ||
            type == org.bukkit.entity.EntityType.STRAY ||
            type == org.bukkit.entity.EntityType.ZOMBIE ||
            type == org.bukkit.entity.EntityType.ZOMBIE_VILLAGER ||
            type == org.bukkit.entity.EntityType.DROWNED ||
            type == org.bukkit.entity.EntityType.HUSK ||
            type == org.bukkit.entity.EntityType.ZOMBIFIED_PIGLIN ||
            type == org.bukkit.entity.EntityType.ZOGLIN ||
            type == org.bukkit.entity.EntityType.PHANTOM ||
            type == org.bukkit.entity.EntityType.WITHER ||
            type == org.bukkit.entity.EntityType.SKELETON_HORSE ||
            type == org.bukkit.entity.EntityType.ZOMBIE_HORSE ||
            type == org.bukkit.entity.EntityType.GIANT ||
            type.name().equals("BOGGED")) {
            return true;
        }
        
        return living instanceof org.bukkit.entity.AbstractSkeleton || 
               living instanceof org.bukkit.entity.Zombie || 
               living instanceof org.bukkit.entity.Phantom || 
               living instanceof org.bukkit.entity.Wither;
    }

    private List<LivingEntity> findSoulDrainTargets(Player player) {
        List<LivingEntity> enemies = new ArrayList<>();
        List<LivingEntity> summons = new ArrayList<>();
        
        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
            if (entity instanceof LivingEntity living && living != player) {
                if (isUndead(living)) {
                    if (living.hasMetadata("summoner_uuid")) {
                        summons.add(living);
                    } else {
                        enemies.add(living);
                    }
                }
            }
        }
        
        return !enemies.isEmpty() ? enemies : summons;
    }

    private void executeSoulDrain(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Soul Drain");
        if (level == 0) return;

        List<LivingEntity> targets = findSoulDrainTargets(player);

        if (targets.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no undead mobs nearby to drain!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            
            // Debug console log to see nearby mobs and status
            StringBuilder sb = new StringBuilder();
            for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
                if (entity instanceof LivingEntity living) {
                    sb.append(living.getType().name())
                      .append(" (isUndead=").append(isUndead(living))
                      .append(", summoner=").append(living.hasMetadata("summoner_uuid"))
                      .append("), ");
                }
            }
            plugin.getLogger().info("Soul Drain failed for " + player.getName() + ". Nearby: " + sb.toString());
            return;
        }

        double cost = 20.0;
        if (!checkMana(player, profile, cost, "Soul Drain")) return;
        profile.setCurrentMana(profile.getCurrentMana() - cost);
        player.sendMessage(ChatColor.DARK_PURPLE + "You used " + ChatColor.LIGHT_PURPLE + "Soul Drain" + ChatColor.DARK_PURPLE + "!");
        
        double tickValue = (2.0 + level * 0.5) / 2.0;
        
        new org.bukkit.scheduler.BukkitRunnable() {
            int runs = 0;
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || runs >= 6) {
                    cancel();
                    return;
                }
                runs++;
                
                List<LivingEntity> currentTargets = findSoulDrainTargets(player);
                
                if (currentTargets.isEmpty()) {
                    return;
                }
                
                player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_AMBIENT, 0.8f, 1.5f);
                
                for (LivingEntity target : currentTargets) {
                    drawLaserLine(target.getLocation().add(0, 1, 0), player.getLocation().add(0, 1, 0));
                    
                    if (target.hasMetadata("summoner_uuid")) {
                        // Directly subtract health to bypass friendly fire and event cancellation plugins
                        double newHealth = Math.max(0.0, target.getHealth() - tickValue);
                        target.setHealth(newHealth);
                        target.playEffect(org.bukkit.EntityEffect.HURT);
                    } else {
                        target.damage(tickValue);
                    }
                    
                    player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + tickValue));
                    player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1.5, 0), 1, 0.1, 0.1, 0.1, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
    
    private void drawLaserLine(Location from, Location to) {
        double dist = from.distance(to);
        org.bukkit.util.Vector dir = to.toVector().subtract(from.toVector()).normalize();
        for (double d = 0; d < dist; d += 0.5) {
            Location p = from.clone().add(dir.clone().multiply(d));
            p.getWorld().spawnParticle(Particle.DUST, p, 1, 0, 0, 0, 0, new Particle.DustOptions(org.bukkit.Color.RED, 0.8f));
        }
    }

    private void startPassiveTasks() {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
                    if (profile == null) continue;
                    int level = profile.getSkillLevel("Deathly Rejuvenation");
                    if (level > 0) {
                        double maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        double regen = level * 0.02;
                        player.setHealth(Math.min(maxHp, player.getHealth() + regen));
                        if (player.getHealth() < maxHp) {
                            player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 0.1, 0), 1, 0.2, 0.2, 0.2, 0.01);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);

        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
                    if (profile == null) continue;
                    int level = profile.getSkillLevel("Fertilizer");
                    if (level > 0) {
                        double growthChance = level * 0.01;
                        if (Math.random() < growthChance) {
                            double radius = 5.0;
                            int r = 5;
                            Location center = player.getLocation();
                            List<Block> cropBlocks = new ArrayList<>();
                            for (int x = -r; x <= r; x++) {
                                for (int y = -2; y <= 2; y++) {
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
                            if (!cropBlocks.isEmpty()) {
                                Block chosen = cropBlocks.get((int) (Math.random() * cropBlocks.size()));
                                org.bukkit.block.data.BlockData data = chosen.getBlockData();
                                if (data instanceof Ageable ageable) {
                                    if (ageable.getAge() < ageable.getMaximumAge()) {
                                        ageable.setAge(ageable.getAge() + 1);
                                        chosen.setBlockData(ageable);
                                        chosen.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, chosen.getLocation().add(0.5, 0.5, 0.5), 5, 0.2, 0.2, 0.2, 0.05);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 80L, 80L);

        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateMonkState(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    @EventHandler
    public void onHorseDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("spear_knight_protect")) {
            event.setCancelled(true);
        }
    }

    private void executeBoomerangThrow(Player player, PlayerProfile profile) {
        int level = profile.getSkillLevel("Boomerang Throw");
        if (level == 0) return;

        double cost = 20.0;
        if (!checkMana(player, profile, cost, "Boomerang Throw")) return;

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
        if (!checkMana(player, profile, cost, "Laser DOT")) return;

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
        if (!checkMana(player, profile, cost, "Thunder Wave")) return;

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

    private boolean isSelectingEmptySlot(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        return mainHand == null || mainHand.getType() == Material.AIR;
    }

    private void updateMonkState(Player player) {
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;

        boolean isMonk = profile.getUnlockedClasses().contains("Monk") || profile.getUnlockedClasses().contains("Admin Class");
        if (!isMonk) return;

        boolean selectingEmpty = isSelectingEmptySlot(player);
        
        // Double Jump allow flight check
        int doubleJumpLvl = profile.getSkillLevel("Double Jump");
        if (doubleJumpLvl >= 1 && selectingEmpty) {
            if (player.getGameMode() == org.bukkit.GameMode.SURVIVAL || player.getGameMode() == org.bukkit.GameMode.ADVENTURE) {
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);
                }
            }
        } else {
            if (player.getGameMode() == org.bukkit.GameMode.SURVIVAL || player.getGameMode() == org.bukkit.GameMode.ADVENTURE) {
                if (player.getAllowFlight() && !player.hasMetadata("npc")) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }
            }
        }

        // Water Walking check
        int waterWalkingLvl = profile.getSkillLevel("Water Walking");
        boolean interrupted = System.currentTimeMillis() - lastDamageTime.getOrDefault(player.getUniqueId(), 0L) < 5000L;
        
        if (waterWalkingLvl >= 1 && selectingEmpty && !interrupted) {
            // Speed effect
            int amp = waterWalkingLvl / 15; // Speed I at 1-14, Speed II at 15-29, etc.
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 10, amp, true, false, true));
            
            // Water walking physics
            Location loc = player.getLocation();
            Block feet = loc.getBlock();
            Block below = feet.getRelative(org.bukkit.block.BlockFace.DOWN);
            
            if (below.getType() == Material.WATER && (feet.getType() == Material.AIR || feet.getType() == Material.WATER)) {
                double costPerSecond = 1.0; 
                double costPerTick = costPerSecond * 0.1; // 2 ticks = 0.1s
                if (profile.getCurrentMana() >= costPerTick) {
                    profile.setCurrentMana(profile.getCurrentMana() - costPerTick);
                    
                    if (player.getVelocity().getY() < 0) {
                        org.bukkit.util.Vector vel = player.getVelocity();
                        vel.setY(0);
                        player.setVelocity(vel);
                    }
                    if (player.getLocation().getY() < below.getY() + 1.0) {
                        Location target = player.getLocation();
                        target.setY(below.getY() + 1.0);
                        player.teleport(target);
                    }
                    
                    player.getWorld().spawnParticle(Particle.SPLASH, player.getLocation().add(0, 0.05, 0), 2, 0.1, 0, 0.1, 0.01);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE || player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            return;
        }
        
        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return;
        
        int doubleJumpLvl = profile.getSkillLevel("Double Jump");
        boolean isMonk = profile.getUnlockedClasses().contains("Monk") || profile.getUnlockedClasses().contains("Admin Class");
        
        if (isMonk && doubleJumpLvl >= 1 && isSelectingEmptySlot(player)) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
            
            double cost = 15.0;
            if (!checkMana(player, profile, cost, "Double Jump")) {
                return;
            }
            
            profile.setCurrentMana(profile.getCurrentMana() - cost);
            
            double launchPower = 0.8 + (doubleJumpLvl * 0.015);
            org.bukkit.util.Vector dir = player.getLocation().getDirection().setY(0).normalize();
            org.bukkit.util.Vector vel = dir.multiply(0.6).setY(launchPower);
            
            player.setVelocity(vel);
            player.sendMessage(ChatColor.AQUA + "Double Jump!");
            player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 1.2f);
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 15, 0.3, 0.1, 0.3, 0.05);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            lastDamageTime.put(player.getUniqueId(), System.currentTimeMillis());
            
            PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
            if (profile != null && profile.getSkillLevel("Water Walking") >= 1 && isSelectingEmptySlot(player)) {
                boolean isMonk = profile.getUnlockedClasses().contains("Monk") || profile.getUnlockedClasses().contains("Admin Class");
                if (isMonk) {
                    player.sendMessage(ChatColor.RED + "★ Your Water Walking stance was interrupted!");
                    player.playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 0.8f, 0.5f);
                }
            }
        }
    }
}
