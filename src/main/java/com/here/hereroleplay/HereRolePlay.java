package com.here.hereroleplay;

import com.here.hereroleplay.api.HereRolePlayAPI;
import com.here.hereroleplay.api.HrpExpansion;
import com.here.hereroleplay.attributes.AttributeManager;
import com.here.hereroleplay.classes.ClassManager;
import com.here.hereroleplay.commands.HrpCommand;
import com.here.hereroleplay.data.DatabaseManager;
import com.here.hereroleplay.data.StatisticsGenerator;
import com.here.hereroleplay.gui.GUIListener;
import com.here.hereroleplay.listeners.CollectListener;
import com.here.hereroleplay.listeners.CombatListener;
import com.here.hereroleplay.listeners.CraftListener;
import com.here.hereroleplay.listeners.PlayerConnectionListener;
import com.here.hereroleplay.skills.BossBarManager;
import com.here.hereroleplay.skills.ManaManager;
import com.here.hereroleplay.skills.SkillManager;
import com.here.hereroleplay.skills.XpManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HereRolePlay extends JavaPlugin {

    private DatabaseManager databaseManager;
    private StatisticsGenerator statisticsGenerator;
    private XpManager xpManager;
    private BossBarManager bossBarManager;
    private AttributeManager attributeManager;
    private ManaManager manaManager;
    private ClassManager classManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
        // Initialize API
        HereRolePlayAPI.init(this);
        
        // Initialize Database
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();
        
        // Initialize Statistics Generator
        statisticsGenerator = new StatisticsGenerator(this);
        statisticsGenerator.start();
        
        // Initialize XP Manager
        xpManager = new XpManager(this);
        
        // Initialize Boss Bar Manager
        bossBarManager = new BossBarManager(this);
        
        // Initialize Attribute & Mana Managers
        attributeManager = new AttributeManager(this);
        manaManager = new ManaManager(this);
        manaManager.start();
        
        // Initialize Class Manager
        classManager = new ClassManager(this);
        
        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new CollectListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new SkillManager(this), this);
        getServer().getPluginManager().registerEvents(attributeManager, this);
        
        // Haste Task for Engineer Efficiency (every 5 seconds)
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                com.here.hereroleplay.data.PlayerProfile profile = databaseManager.getProfile(player.getUniqueId());
                if (profile != null) {
                    int efficiencyLvl = Math.min(100, profile.getSkillLevel("Efficiency"));
                    if (efficiencyLvl > 0) {
                        int amp = (efficiencyLvl - 1) / 10;
                        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.HASTE, 160, amp, true, false, true));
                    }
                }
            }
        }, 100L, 100L);

        // Register Commands
        HrpCommand hrpCommand = new HrpCommand(this);
        getCommand("hrp").setExecutor(hrpCommand);
        getCommand("hrp").setTabCompleter(hrpCommand);
        
        // Register PlaceholderAPI if present
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new HrpExpansion(this).register();
        }

        getLogger().info("HereRolePlay has been enabled!");
    }

    @Override
    public void onDisable() {
        if (bossBarManager != null) {
            bossBarManager.clearAll();
        }
        if (manaManager != null) {
            manaManager.stop();
        }
        if (statisticsGenerator != null) {
            statisticsGenerator.stop();
        }
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("HereRolePlay has been disabled!");
    }
    
    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public XpManager getXpManager() {
        return xpManager;
    }
    
    public AttributeManager getAttributeManager() {
        return attributeManager;
    }
    
    public ManaManager getManaManager() {
        return manaManager;
    }
    
    public ClassManager getClassManager() {
        return classManager;
    }
    
    public StatisticsGenerator getStatisticsGenerator() {
        return statisticsGenerator;
    }
}
