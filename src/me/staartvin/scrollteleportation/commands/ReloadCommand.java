package me.staartvin.scrollteleportation.commands;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

	private ScrollTeleportation plugin;

	public ReloadCommand(ScrollTeleportation instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("scrollteleportation.reload")) {
				sender.sendMessage(ChatColor.RED
						+ "You are not allowed to reload");
				return true;
			}

			plugin.getMainConfig().reload();
			
			sender.sendMessage(ChatColor.GREEN + "Scroll Teleportation reloaded!");
			return true;
		}
		
		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW + "Type '/scroll help' for a list of commands.");
		return true;
	}

}
