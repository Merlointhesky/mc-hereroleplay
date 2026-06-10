package com.here.hereroleplay.commands;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.gui.menus.MainHubGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HrpCommand implements CommandExecutor {

    private final HereRolePlay plugin;

    public HrpCommand(HereRolePlay plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfig().getString("messages.commands.player-only", "Only players can use this command."));
            return true;
        }

        if (args.length == 0) {
            // Open Main Hub
            MainHubGUI gui = new MainHubGUI(plugin, player);
            player.openInventory(gui.getInventory());
            return true;
        }

        if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("top")) {
            com.here.hereroleplay.gui.menus.LeaderboardGUI.openLeaderboard(plugin, player);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("admin")) {
            if (!sender.hasPermission("hrp.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }
            
            if (args.length >= 4 && args[1].equalsIgnoreCase("givexp")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                
                try {
                    double amount = Double.parseDouble(args[3]);
                    // Assuming args[4] is the pillar, or just give combat for test
                    plugin.getXpManager().addCombatXp(target, amount);
                    sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Combat XP to " + target.getName());
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount.");
                }
                return true;
            }
        }
        
        return true;
    }
}
