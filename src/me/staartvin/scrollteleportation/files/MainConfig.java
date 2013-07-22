package me.staartvin.scrollteleportation.files;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.exceptions.DestinationInvalidException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfig {

	private ScrollTeleportation plugin;
	private FileConfiguration config;

	public MainConfig(ScrollTeleportation instance) {
		plugin = instance;
	}

	public void loadConfiguration() {
		config = plugin.getConfig();
		config.options()
				.header("Scroll Teleportation v"
						+ plugin.getDescription().getVersion()
						+ " Config"
						+ "\n\nThe name of scroll should be unique. ST uses this to identify the scroll."
						+ "\nYou can add as many lores as you like. To get a blank line, just type '' as lore line.."
						+ "\nDestination is the destination a player will be teleported to."
						+ "\nWhen 'destination hidden' is true, the scroll will show 'unknown' as destination"
						+ "\nDelay is time (in seconds) before a player is teleported."
						+ "\nWhen 'cancel on move' is true, the teleportation of a scroll will be cancelled on move."
						+ "\nUses is the amount of uses a scroll have before it becomes thin air.");

		// Messages
		config.addDefault("Messages.cast message",
				"§6Teleporting in %time% seconds..");
		config.addDefault("Messages.move warning",
				"§4Don't move or teleportation is cancelled.");
		config.addDefault("Messages.teleport message", "§6Commencing teleport!");

		// General information
		config.addDefault("Scroll.scrollItemID", 339);
		
		// Create an example scroll
		config.addDefault("Scrolls.ExampleScroll.name",
				"Scroll of Mysteriousness");
		config.addDefault("Scrolls.ExampleScroll.lores.lore1",
				"§3This mighty and rare scroll");
		config.addDefault("Scrolls.ExampleScroll.lores.lore2",
				"§3will teleport you to");
		config.addDefault("Scrolls.ExampleScroll.lores.lore3",
				"§3a place never visited by humans.");
		config.addDefault("Scrolls.ExampleScroll.lores.lore4", "");
		config.addDefault("Scrolls.ExampleScroll.lores.lore5",
				"§7Rare scroll, Unknown location");
		config.addDefault("Scrolls.ExampleScroll.lores.lore6", "");
		config.addDefault("Scrolls.ExampleScroll.destination",
				"world_nether, 100, 100, 100");
		config.addDefault("Scrolls.ExampleScroll.destination hidden", false);
		config.addDefault("Scrolls.ExampleScroll.delay", 5);
		config.addDefault("Scrolls.ExampleScroll.cancel on move", true);
		config.addDefault("Scrolls.ExampleScroll.uses", 1);

		config.options().copyDefaults(true);
		plugin.saveConfig();
	}

	public String getCastMessage() {
		return config.getString("Messages.cast message");
	}

	public String getTeleportMessage() {
		return config.getString("Messages.teleport message");
	}

	public Location getDestination(String scroll)
			throws DestinationInvalidException {
		String location = config
				.getString("Scrolls." + scroll + ".destination");
		String[] args = location.split(",");

		if (args.length != 4) {
			throw new DestinationInvalidException(
					"More or less than 4 arguments defined in config!");
		}

		String x, y, z, world;

		world = args[0].trim();
		x = args[1].trim();
		y = args[2].trim();
		z = args[3].trim();

		World realWorld = plugin.getServer().getWorld(world);

		if (realWorld == null) {
			throw new DestinationInvalidException("World '" + world
					+ "' does not exist!");
		}

		Location realLocation = new Location(realWorld, Integer.parseInt(x),
				Integer.parseInt(y), Integer.parseInt(z));

		return realLocation;
	}

	public String getScroll(String scrollName) {
		Set<String> scrolls = config.getConfigurationSection("Scrolls")
				.getKeys(false);

		for (String scroll : scrolls) {
			if (getScrollName(scroll).equalsIgnoreCase(scrollName)) {
				return scroll;
			}
		}
		return null;
	}

	public String getScrollName(String scroll) {
		return config.getString("Scrolls." + scroll + ".name");
	}

	public int getDelay(String scroll) {
		return config.getInt("Scrolls." + scroll + ".delay");
	}

	public String getMoveWarningMessage() {
		return config.getString("Messages.move warning");
	}

	public List<String> getLores(String scroll) {
		List<String> lores = new ArrayList<String>();
		Set<String> realLores = config.getConfigurationSection(
				"Scrolls." + scroll + ".lores").getKeys(false);

		for (String lore : realLores) {
			lores.add(lore);
		}

		return lores;
	}

	public boolean isDestinationHidden(String scroll) {
		return config.getBoolean("Scrolls." + scroll + ".destination hidden");
	}

	public boolean doCancelOnMove(String scroll) {
		return config.getBoolean("Scrolls." + scroll + ".cancel on move");
	}

	public String getStringDestination(String scroll) {
		String destination = config.getString("Scrolls." + scroll
				+ ".destination");
		String[] arguments = destination.split(",");

		String world, x, y, z;

		world = arguments[0].trim();
		x = arguments[1].trim();
		y = arguments[2].trim();
		z = arguments[3].trim();

		String stringDestination = x + ", " + y + ", " + z + " in " + world;
		return stringDestination;
	}

	public String matchScroll(String scroll) {
		Set<String> scrolls = config.getConfigurationSection("Scrolls")
				.getKeys(false);

		for (String scrollString : scrolls) {
			if (scrollString.equalsIgnoreCase(scroll)) {
				return scrollString;
			}
		}
		return null;
	}

	public String getLoreLine(String scroll, String lore) {
		return config.getString("Scrolls." + scroll + ".lores." + lore);
	}

	public int getTotalUses(String scroll) {
		return config.getInt("Scrolls." + scroll + ".uses", 1);
	}

	public void reload() {
		plugin.reloadConfig();
		plugin.saveConfig();
		loadConfiguration();
	}
	
	public int getScrollItemId() {
		return config.getInt("Scroll.scrollItemID", 399);
	}
}
