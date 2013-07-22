package me.staartvin.scrollteleportation.commands;

import java.util.ArrayList;
import java.util.List;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand {

	private ScrollTeleportation plugin;

	public SetCommand(ScrollTeleportation instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (args[0].equalsIgnoreCase("set")) {
			if (!sender.hasPermission("scrollteleportation.set")) {
				sender.sendMessage(ChatColor.RED
						+ "You are not allowed to set a variable");
				return true;
			}

			if (args.length < 4) {
				if (!(args.length == 3 && args[1].equalsIgnoreCase("destination"))) {
					sender.sendMessage(ChatColor.RED + "Incorrect command usage!");
					sender.sendMessage(ChatColor.YELLOW + "Usage: /scroll set <variable> <scroll> <result>");
					return true;
				}
			}
			
			String variable = args[1];
			String scroll = plugin.getMainConfig().matchScroll(args[2]);
			List<String> resultList = new ArrayList<String>();
			
			if (scroll == null) {
				sender.sendMessage(ChatColor.RED + "There is no scroll by that name!");
				return true;
			}
			
			// Fill result list
			for (int i=0;i<args.length;i++) {
				String argument = args[i];
				
				// Don't include 'set', 'variable' and 'scrollname'
				if (i == 0 || i == 1 || i == 2) continue;
				
				resultList.add(argument);
			}
			
			// Make it on string
			String result = convertToString(resultList);
			
			if (variable.equalsIgnoreCase("name")) {
				plugin.getMainConfig().setName(scroll, result);
				
				sender.sendMessage(ChatColor.GREEN + "Set name of " + scroll + " to " + ChatColor.GOLD + result);
				return true;
			} else if (variable.equalsIgnoreCase("delay")) {
				int delay = 1;
				
				try {
					delay = Integer.parseInt(result);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Delay number is not a valid number!");
					return true;
				}
				
				plugin.getMainConfig().setDelay(scroll, delay);
				
				sender.sendMessage(ChatColor.GREEN + "Set delay of " + scroll + " to " + ChatColor.GOLD + result + ChatColor.GREEN + " seconds");
				return true;
			} else if (variable.equalsIgnoreCase("uses")) {
				int uses = 1;
				
				try {
					uses = Integer.parseInt(result);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Use number is not a valid number!");
					return true;
				}
				
				plugin.getMainConfig().setUses(scroll, uses);
				
				sender.sendMessage(ChatColor.GREEN + "Set uses of " + scroll + " to " + ChatColor.GOLD + result);
				return true;
			} else if (variable.equalsIgnoreCase("destination_hidden")) {
				if (result.equalsIgnoreCase("true")) {
					plugin.getMainConfig().setDestinationHidden(scroll, true);
					
					sender.sendMessage(ChatColor.GREEN + "Set destination_hidden of " + scroll + " to " + ChatColor.GOLD + "true");
				} else {
					plugin.getMainConfig().setDestinationHidden(scroll, false);
					
					sender.sendMessage(ChatColor.GREEN + "Set destination_hidden of " + scroll + " to " + ChatColor.GOLD + "false");
				}
				return true;
			} else if (variable.equalsIgnoreCase("cancel_on_move")) {
				if (result.equalsIgnoreCase("true")) {
					plugin.getMainConfig().setCancelOnMove(scroll, true);
					
					sender.sendMessage(ChatColor.GREEN + "Set cancel_on_move of " + scroll + " to " + ChatColor.GOLD + "true");
				} else {
					plugin.getMainConfig().setCancelOnMove(scroll, false);
					
					sender.sendMessage(ChatColor.GREEN + "Set cancel_on_move of " + scroll + " to " + ChatColor.GOLD + "false");
				}
				return true;
			} else if (variable.equalsIgnoreCase("destination")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Cannot get your location!");
					return true;
				}
				Player player = (Player) sender;
				
				plugin.getMainConfig().setDestination(scroll, player.getLocation());
				
				sender.sendMessage(ChatColor.GREEN + "Set destination of " + scroll + " to " + ChatColor.GOLD + "your location");
				
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "I don't recognise '" + variable + "' as a variable!");
				sender.sendMessage(ChatColor.YELLOW + "You can only use: delay, destination, destination_hidden, cancel_on_move, uses or name");
				return true;
			}
		}
		
		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW + "Type '/scroll help' for a list of commands.");
		return true;
	}

	private String convertToString(List<String> list) {
		StringBuilder stringBuilder = new StringBuilder("");
		
		for (String entry: list) {
			stringBuilder.append(entry + " ");
		}
		String returnString = stringBuilder.toString().trim();
		
		
		return returnString;
	}
	
}
