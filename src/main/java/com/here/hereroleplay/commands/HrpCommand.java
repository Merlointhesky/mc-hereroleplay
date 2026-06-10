package com.here.hereroleplay.commands;

import com.here.hereroleplay.HereRolePlay;
import com.here.hereroleplay.gui.menus.MainHubGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HrpCommand implements CommandExecutor, TabCompleter {

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
                    String pillar = "combat";
                    if (args.length >= 5) {
                        pillar = args[4].toLowerCase();
                    }
                    
                    switch (pillar) {
                        case "combat":
                            plugin.getXpManager().addCombatXp(target, amount);
                            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Combat XP to " + target.getName());
                            break;
                        case "collect":
                            plugin.getXpManager().addCollectXp(target, amount);
                            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Collect XP to " + target.getName());
                            break;
                        case "craft":
                            plugin.getXpManager().addCraftXp(target, amount);
                            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Craft XP to " + target.getName());
                            break;
                        default:
                            sender.sendMessage(ChatColor.RED + "Invalid pillar: '" + args[4] + "'. Use combat, collect, or craft.");
                            break;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount.");
                }
                return true;
            }
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            subcommands.add("leaderboard");
            subcommands.add("top");
            if (sender.hasPermission("hrp.admin")) {
                subcommands.add("admin");
            }
            StringUtil.copyPartialMatches(args[0], subcommands, completions);
            Collections.sort(completions);
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            if (sender.hasPermission("hrp.admin")) {
                List<String> adminCommands = new ArrayList<>();
                adminCommands.add("givexp");
                StringUtil.copyPartialMatches(args[1], adminCommands, completions);
                Collections.sort(completions);
                return completions;
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("givexp")) {
            if (sender.hasPermission("hrp.admin")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList();
                StringUtil.copyPartialMatches(args[2], playerNames, completions);
                Collections.sort(completions);
                return completions;
            }
        }

        if (args.length == 5 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("givexp")) {
            if (sender.hasPermission("hrp.admin")) {
                List<String> pillars = new ArrayList<>();
                pillars.add("combat");
                pillars.add("collect");
                pillars.add("craft");
                StringUtil.copyPartialMatches(args[4], pillars, completions);
                Collections.sort(completions);
                return completions;
            }
        }

        return Collections.emptyList();
    }
}
