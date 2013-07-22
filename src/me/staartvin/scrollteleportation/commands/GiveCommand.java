package me.staartvin.scrollteleportation.commands;

import java.util.ArrayList;
import java.util.List;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

			String scroll = plugin.getMainConfig().matchScroll(args[1]);

			if (scroll == null) {
				sender.sendMessage(ChatColor.RED + "No scroll found.");
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

			giveScroll(scroll, target);

			target.sendMessage(ChatColor.GREEN + "You have been given a "
					+ ChatColor.GOLD
					+ plugin.getMainConfig().getScrollName(scroll));

			if (args.length == 3) {
				if (!target.getName().equalsIgnoreCase(sender.getName())) {
					sender.sendMessage(ChatColor.GREEN + "You have given "
							+ ChatColor.GOLD + target.getName()
							+ ChatColor.GREEN + " a " + ChatColor.GOLD
							+ plugin.getMainConfig().getScrollName(scroll));
				}
			}

			return true;
		}

		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW
				+ "Type '/scroll help' for a list of commands.");
		return true;
	}

	public void giveScroll(String scroll, Player player) {
		ItemStack item = new ItemStack(plugin.getMainConfig().getScrollItemId(), 1);

		ItemMeta im = item.getItemMeta();

		// Set name
		im.setDisplayName(ChatColor.GOLD
				+ plugin.getMainConfig().getScrollName(scroll));

		String destination = null;

		if (plugin.getMainConfig().isDestinationHidden(scroll)) {
			destination = "Destination: Unknown";
		} else {
			destination = "Destination: "
					+ plugin.getMainConfig().getStringDestination(scroll);
		}

		// Set lore
		List<String> lores = new ArrayList<String>();

		for (String lore : plugin.getMainConfig().getLores(scroll)) {
			lores.add(plugin.getMainConfig().getLoreLine(scroll, lore));
		}

		// Add destination
		lores.add(ChatColor.GREEN + destination);
		
		// Add uses
		lores.add(ChatColor.GREEN + "Uses: " + plugin.getMainConfig().getTotalUses(scroll));

		// Set real lore
		im.setLore(lores);

		// Set ItemMeta
		item.setItemMeta(im);

		// Give player item
		player.getInventory().addItem(item);
	}

}
