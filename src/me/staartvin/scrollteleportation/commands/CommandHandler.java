package me.staartvin.scrollteleportation.commands;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

	private ScrollTeleportation plugin;

	private Map<String, CommandExecutor> commands = new HashMap<>();
	
	public CommandHandler(ScrollTeleportation instance) {
		plugin = instance;

		commands.put("give", new GiveCommand(plugin));
		commands.put("reload", new ReloadCommand(plugin));
		commands.put("create", new CreateCommand(plugin));
		commands.put("set", new SetCommand(plugin));
		commands.put("help", new HelpCommand(plugin));
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
		} else {
			CommandExecutor executor = commands.get(args[0].toLowerCase().trim());

			if (executor != null) {
				executor.onCommand(sender, cmd, label, args);
			} else {
				sender.sendMessage(ChatColor.RED + "Command not recognised!");
				sender.sendMessage(ChatColor.YELLOW + "Type '/scroll help' for a list of commands.");
			}

			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

		if (strings.length == 1) {
			return commands.keySet().stream().sorted().collect(Collectors.toList());
		} else if (strings.length >= 2) {

			TabCompleter completer = (TabCompleter) commands.get(strings[0].toLowerCase().trim());

			if (completer != null) {
				return completer.onTabComplete(commandSender, command, s, strings);
			}
		}

		return null;
	}
}
