package me.staartvin.scrollteleportation.commands;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

	private ScrollTeleportation plugin;
	
	public CommandHandler(ScrollTeleportation instance) {
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.BLUE
					+ "-----------------------------------------------------");
			sender.sendMessage(ChatColor.GOLD + "Developed by: "
					+ ChatColor.GRAY + "Staartvin");
			sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.GRAY
					+ plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.YELLOW
					+ "Type /scroll help for a list of commands.");
			return true;
		} else if (args[0].equalsIgnoreCase("give")) {
			return new GiveCommand(plugin).onCommand(sender, cmd, label, args);
		} else if (args[0].equalsIgnoreCase("help")) {
			if (args.length == 1) {
				showHelpPage(1, sender);
				return true;
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
				return true;
			}
		} else if (args[0].equalsIgnoreCase("reload")) {
			return new ReloadCommand(plugin).onCommand(sender, cmd, label, args);
		}
		
		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW + "Type '/scroll help' for a list of commands.");
		return true;
	}

	private void showHelpPage(int page, CommandSender sender) {
		int maximumPages = 1;
		if (page == 1) {
			sender.sendMessage(ChatColor.BLUE + "--------------["
					+ ChatColor.GOLD + "Scroll Teleportation" + ChatColor.BLUE
					+ "]------------------");
			sender.sendMessage(ChatColor.GOLD + "/scroll give <scroll> (player)"
					+ ChatColor.BLUE + " --- Give a scroll to a player");
			sender.sendMessage(ChatColor.GOLD + "/scroll"
					+ ChatColor.BLUE + " --- Shows basic information");
			sender.sendMessage(ChatColor.GOLD + "/scroll help"
					+ ChatColor.BLUE + " --- Shows a list of commands");
			sender.sendMessage(ChatColor.GOLD + "/scroll reload"
					+ ChatColor.BLUE + " --- Reload Scroll Teleportation");
			sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.BLUE + "1 "
					+ ChatColor.GOLD + "of " + ChatColor.BLUE + maximumPages);
		} else {
			sender.sendMessage(ChatColor.BLUE + "--------------["
					+ ChatColor.GOLD + "Scroll Teleportation" + ChatColor.BLUE
					+ "]------------------");
			sender.sendMessage(ChatColor.GOLD + "/scroll give <scroll> (player)"
					+ ChatColor.BLUE + " --- Give a scroll to a player");
			sender.sendMessage(ChatColor.GOLD + "/scroll"
					+ ChatColor.BLUE + " --- Shows basic information");
			sender.sendMessage(ChatColor.GOLD + "/scroll help"
					+ ChatColor.BLUE + " --- Shows a list of commands");
			sender.sendMessage(ChatColor.GOLD + "/scroll reload"
					+ ChatColor.BLUE + " --- Reload Scroll Teleportation");
			sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.BLUE + "1 "
					+ ChatColor.GOLD + "of " + ChatColor.BLUE + maximumPages);
		}
	}
}
