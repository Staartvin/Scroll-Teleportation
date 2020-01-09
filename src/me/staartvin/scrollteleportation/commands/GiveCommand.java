package me.staartvin.scrollteleportation.commands;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.storage.Scroll;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand {

    private ScrollTeleportation plugin;

    public GiveCommand(ScrollTeleportation instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("scrollteleportation.give")) {
                sender.sendMessage(ChatColor.RED
                        + "You are not allowed to give scrolls");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Incorrect command usage!");
                sender.sendMessage(ChatColor.YELLOW
                        + "Usage: /scroll give <scroll> (name)");
                return true;
            }

            Player target = null;

            Scroll scroll = plugin.getScrollStorage().getLoadedScroll(args[1]).orElse(null);

            if (scroll == null) {
                sender.sendMessage(ChatColor.RED + "No scroll found with that name.");
                return true;
            }

            // No target given
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You are not a player!");
                    return true;
                }

                target = (Player) sender;
            } else if (args.length == 3) {
                // Target given
                if (plugin.getServer().getPlayer(args[2]) == null) {
                    sender.sendMessage(ChatColor.RED
                            + "There is no player with that name online!");
                } else {
                    target = plugin.getServer().getPlayer(args[2]);
                }
            } else {
                // Incorrect command usage
                sender.sendMessage(ChatColor.RED + "Incorrect command usage!");
                sender.sendMessage(ChatColor.YELLOW
                        + "Usage: /scroll give <scroll> (name)");
                return true;
            }

			ItemStack itemStack = scroll.getItemStack();

            // Add item
            target.getInventory().addItem(itemStack);

            target.sendMessage(ChatColor.GREEN + "You have been given a "
                    + ChatColor.GOLD + scroll.getDisplayName());

            if (args.length == 3) {
                if (!target.getName().equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(ChatColor.GREEN + "You have given " + ChatColor.GOLD + target.getName()
                            + ChatColor.GREEN + " a " + ChatColor.GOLD + scroll.getDisplayName());
                }
            }

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Command not recognised!");
        sender.sendMessage(ChatColor.YELLOW
                + "Type '/scroll help' for a list of commands.");
        return true;
    }

}
