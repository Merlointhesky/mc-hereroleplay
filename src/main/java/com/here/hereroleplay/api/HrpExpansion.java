package com.here.hereroleplay.api;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.data.PlayerProfile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class HrpExpansion extends PlaceholderExpansion {

    private final HereRolePlay plugin;

    public HrpExpansion(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hrp";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        PlayerProfile profile = plugin.getDatabaseManager().getProfile(player.getUniqueId());
        if (profile == null) return "";

        switch (params.toLowerCase()) {
            case "combat_level": return String.valueOf(profile.getCombatLevel());
            case "collect_level": return String.valueOf(profile.getCollectLevel());
            case "craft_level": return String.valueOf(profile.getCraftLevel());
            case "mana": return String.valueOf(Math.round(profile.getCurrentMana()));
            case "max_mana": return String.valueOf(Math.round(plugin.getManaManager().getMaxMana(profile)));
            case "unspent_points": return String.valueOf(profile.getUnspentSkillPoints());
        }

        return null;
    }
}
