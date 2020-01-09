package me.staartvin.scrollteleportation.commands;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class HelpCommand implements CommandExecutor, TabCompleter {

	private ScrollTeleportation plugin;

	public HelpCommand(ScrollTeleportation instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (args.length == 1) {
			showHelpPage(1, sender);
		} else {
			Integer id = -1;

			try {
				id = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED
						+ (args[1] + " is not a valid page number!"));
				return true;
			}

			showHelpPage(id, sender);
		}

		return true;
	}

	private void showHelpPage(int page, CommandSender sender) {
		int maximumPages = 1;
		if (page == 1) {
			sender.sendMessage(ChatColor.BLUE + "--------------["
					+ ChatColor.GOLD + "Scroll Teleportation" + ChatColor.BLUE
					+ "]------------------");
			sender.sendMessage(ChatColor.GOLD + "/scroll"
					+ ChatColor.BLUE + " --- Shows basic information");
			sender.sendMessage(ChatColor.GOLD + "/scroll help"
					+ ChatColor.BLUE + " --- Shows a list of commands");
			sender.sendMessage(ChatColor.GOLD + "/scroll reload"
					+ ChatColor.BLUE + " --- Reload Scroll Teleportation");
			sender.sendMessage(ChatColor.GOLD + "/scroll give <scroll> (player)"
					+ ChatColor.BLUE + " --- Give a scroll to a player");
			sender.sendMessage(ChatColor.GOLD + "/scroll create <scroll> <displayName> <delay> <uses>"
					+ ChatColor.BLUE + " --- Create a new scroll with a <delay>, <uses> and a destination at your location");
			sender.sendMessage(ChatColor.GOLD + "/scroll set <var> <scroll> <result>"
					+ ChatColor.BLUE + " --- Set a scroll variable");
			sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.BLUE + "1 "
					+ ChatColor.GOLD + "of " + ChatColor.BLUE + maximumPages);
		} else {
			sender.sendMessage(ChatColor.BLUE + "--------------["
					+ ChatColor.GOLD + "Scroll Teleportation" + ChatColor.BLUE
					+ "]------------------");
			sender.sendMessage(ChatColor.GOLD + "/scroll"
					+ ChatColor.BLUE + " --- Shows basic information");
			sender.sendMessage(ChatColor.GOLD + "/scroll help"
					+ ChatColor.BLUE + " --- Shows a list of commands");
			sender.sendMessage(ChatColor.GOLD + "/scroll reload"
					+ ChatColor.BLUE + " --- Reload Scroll Teleportation");
			sender.sendMessage(ChatColor.GOLD + "/scroll give <scroll> (player)"
					+ ChatColor.BLUE + " --- Give a scroll to a player");
			sender.sendMessage(ChatColor.GOLD + "/scroll create <scroll> <displayName> <delay> <uses>"
					+ ChatColor.BLUE + " --- Create a new scroll with a <delay>, <uses> and a destination at your location");
			sender.sendMessage(ChatColor.GOLD + "/scroll set <var> <scroll> <result>"
					+ ChatColor.BLUE + " --- Set a scroll variable");
			sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.BLUE + "1 "
					+ ChatColor.GOLD + "of " + ChatColor.BLUE + maximumPages);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		return null;
	}
}
