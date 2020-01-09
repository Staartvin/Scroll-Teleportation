package me.staartvin.scrollteleportation.commands;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommand implements CommandExecutor, TabCompleter {

	private ScrollTeleportation plugin;

	public CreateCommand(ScrollTeleportation instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (args[0].equalsIgnoreCase("create")) {
			if (!sender.hasPermission("scrollteleportation.create")) {
				sender.sendMessage(ChatColor.RED
						+ "You are not allowed to create scrolls");
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "Only players can perform this command!");
				return true;
			}

			if (args.length != 5) {
				sender.sendMessage(ChatColor.RED + "Invalid command usage!");
				sender.sendMessage(ChatColor.YELLOW
						+ "Usage: /scroll create <name> <displayName> <delay> <uses>");
				return true;
			}

			Player player = (Player) sender;
			String scroll = args[1];
			String scrollName = args[2];
			int delay = 1;
			int uses = 1;

			try {
				delay = Integer.parseInt(args[3]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invalid delay time!");
				return true;
			}

			try {
				uses = Integer.parseInt(args[4]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invalid uses!");
				return true;
			}

			if (plugin.getMainConfig().createNewScroll(scroll, scrollName,
					player.getLocation(), delay, uses)) {
				sender.sendMessage(ChatColor.GREEN
						+ "Successfully created new scroll with "
						+ ChatColor.GOLD + delay + ChatColor.GREEN
						+ " seconds delay and " + ChatColor.GOLD + uses
						+ ChatColor.GREEN + " uses and with a "
						+ "destination at " + ChatColor.GOLD + "your location"
						+ ChatColor.GREEN + "!");
			} else {
				sender.sendMessage(ChatColor.RED + "Scroll already exists!");
			}
			return true;
		}

		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW
				+ "Type '/scroll help' for a list of commands.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		return null;
	}
}
