package me.staartvin.scrollteleportation.files;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.exceptions.DestinationInvalidException;
import me.staartvin.scrollteleportation.storage.Scroll;
import me.staartvin.scrollteleportation.storage.ScrollDestination;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

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
						+ "\nUses is the amount of uses a scroll have before it becomes thin air. To create scrolls with infinite uses, just set it to -1."
						+ "\nEffects are effects that are played when the scroll is used. (The number is the duration in seconds)"
						+ "\nThis is a list of effects you can use: http://jd.bukkit.org/rb/doxygen/d3/d70/classorg_1_1bukkit_1_1potion_1_1PotionEffectType.html"
						+ "\n\nDestinations can be defined in multiple ways:"
						+ "\n	1. Fixed point (eg: 'world,x,y,z')"
						+ "\n	2. Random point (eg: 'random(world)')"
						+ "\n		This takes a world to get random coordinates on. If you leave the world out, a random world will be selected"
						+ "\n 		Be careful with this one. This can cause much lag as it will try to teleport a player to unloaded chunks."
						+ "\n	3. Random point with radius (eg: 'random_radius(point=world,x,y,z radius=1000)')"
						+ "\n		This will put a player in a random location within a radius of the point"
						+ "\n	4. Fixed name (eg: 'spawn, world')"
						+ "\n		world is the world that you want the spawn of");
		

		// Messages
		config.addDefault("Messages.cast message",
				"&6Teleporting in %time% seconds..");
		config.addDefault("Messages.move warning",
				"&4Don't move or teleportation is cancelled.");
		config.addDefault("Messages.teleport message", "&6Commencing teleport!");

		// General information
		config.addDefault("Scroll.scrollMaterial", "PAPER");
		
		config.addDefault("Scroll.load-chunk-on-teleport", false);

		// Create an example scroll
		config.addDefault("Scrolls.ExampleScroll.name",
				"Scroll of Mysteriousness");
		config.addDefault("Scrolls.ExampleScroll.lores",
				Arrays.asList(
						"&3This mighty and rare scroll",
						"&3will teleport you to",
						"&3a place never visited by humans.",
						"",
						"&7Rare scroll, Unknown location",
						""
						));
		config.addDefault("Scrolls.ExampleScroll.destination",
				"world, 100, 100, 100");
		config.addDefault("Scrolls.ExampleScroll.destination hidden", false);
		config.addDefault("Scrolls.ExampleScroll.delay", 5);
		config.addDefault("Scrolls.ExampleScroll.cancel on move", true);
		config.addDefault("Scrolls.ExampleScroll.uses", 1);
		config.addDefault("Scrolls.ExampleScroll.effects",
				Arrays.asList("BLINDNESS 10", "POISON 2"));

		// Create another example scroll
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.name",
				"Scroll of Unforeseen Travel");
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.lores",
				Arrays.asList(
						"&3This scroll is a one of its kind",
						"&3and is very rare. It will allow",
						"&3you to travel to an unpredictable",
						"&3destination.",
						"",
						"&7Very rare scroll, Unpredictable destination"
				));
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.destination",
				"random_radius(point=world,1,1,1 radius=4000)");
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.destination hidden", true);
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.delay", 5);
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.cancel on move", true);
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.uses", 5);
		config.addDefault("Scrolls.Scroll_of_unforeseen_travel.effects",
				Arrays.asList("CONFUSION 10"));

		config.options().copyDefaults(true);
		plugin.saveConfig();
	}

	public String getCastMessage() {
		return addColourCode(config.getString("Messages.cast message"));
	}

	public String getTeleportMessage() {
		return addColourCode(config.getString("Messages.teleport message"));
	}

	public String getScroll(String scrollName) {
		Set<String> scrolls = config.getConfigurationSection("Scrolls")
				.getKeys(false);

		for (String scroll : scrolls) {
			if (getScrollDisplayName(scroll).equalsIgnoreCase(scrollName)) {
				return scroll;
			}
		}
		return null;
	}

	public String getScrollDisplayName(String scroll) {
		return addColourCode(config.getString("Scrolls." + scroll + ".name"));
	}

	public int getDelay(String scroll) {
		return config.getInt("Scrolls." + scroll + ".delay");
	}

	public String getMoveWarningMessage() {
		return addColourCode(config.getString("Messages.move warning"));
	}

	public List<String> getLoreStrings(String scroll) {
		List<String> lores = new ArrayList<String>();
		return config.getStringList(
				"Scrolls." + scroll + ".lores").stream().map(text -> ChatColor.translateAlternateColorCodes('&',
				text)).collect(Collectors.toList());
	}

	public boolean isDestinationHidden(String scroll) {
		return config.getBoolean("Scrolls." + scroll + ".destination hidden");
	}

	public boolean doCancelOnMove(String scroll) {
		return config.getBoolean("Scrolls." + scroll + ".cancel on move");
	}

	public ScrollDestination getScrollDestination(String scroll) {
		String destination = config.getString("Scrolls." + scroll
				+ ".destination", "random");
		try {
			return ScrollDestination.createFromLocationString(destination);
		} catch (DestinationInvalidException e) {
			e.printStackTrace();
		}

		return null;
	}

	public int getTotalUses(String scroll) {
		int uses = config.getInt("Scrolls." + scroll + ".uses", 1);
		
		// Infinite uses
		if (uses < 0) {
			return Scroll.SCROLL_USES_INFINITE;
		}
		
		return uses;
	}

	public void reload() {
		plugin.reloadConfig();
		loadConfiguration();
	}

	public Material getScrollMaterial() {
		return Material.getMaterial(Objects.requireNonNull(config.getString("Scroll.scrollMaterial", "PAPER")));
	}

	public List<PotionEffect> getEffects(String scroll) {
		List<PotionEffect> effects = new ArrayList<>();

		config.getStringList("Scrolls." + scroll + ".effects").forEach(effectString -> {
			String[] args = effectString.split(" ");

			if (args.length != 2) {
				plugin.getLogger().severe("Missing duration argument for '" + effectString + "'!");
				return;
			}

			String realEffect = args[0].trim();
			int duration = 1;

			try {
				duration = Integer.parseInt(args[1].trim());
			} catch (Exception e) {
				plugin.getLogger().severe("Invalid duration for '" + realEffect + "'!");
				return;
			}

			PotionEffectType potionEffect = PotionEffectType.getByName(realEffect.toUpperCase().replace(" " , "_"));

			if (potionEffect == null) {
				plugin.getLogger().severe("PotionEffect '" + realEffect + "' is not a valid effect!");
				return;
			}

			effects.add(new PotionEffect(potionEffect, duration * 20, 1));

		});

		return effects;
	}

	public boolean createNewScroll(String scroll, String scrollName,
			Location destination, int delay, int uses) {

		if (config.getString("Scrolls." + scroll + ".name") != null) {
			return false;
		}

		// Set scroll name
		setName(scroll, scrollName);

		// Set destination
		setDestination(scroll, destination);

		// Set delay
		setDelay(scroll, delay);

		// Set destination hidden
		setDestinationHidden(scroll, false);

		// Set cancel on move true
		setCancelOnMove(scroll, true);

		// Set uses
		setUses(scroll, uses);

		// Set effects
		config.set("Scrolls." + scroll + ".effects", Arrays.asList());

		// Set lore
		config.set("Scrolls." + scroll + ".lores.lore1",
				"&3This mighty and rare scroll");

		// Set lore
		config.set("Scrolls." + scroll + ".lores.lore2", "&3will teleport you");

		// Set lore
		config.set("Scrolls." + scroll + ".lores.lore3", "");

		// Set lore
		config.set("Scrolls." + scroll + ".lores.lore4", "");

		// Save
		plugin.saveConfig();

		return true;
	}

	public void setName(String scroll, String name) {
		config.set("Scrolls." + scroll + ".name", name);

		plugin.saveConfig();
	}

	public void setDelay(String scroll, int delay) {
		config.set("Scrolls." + scroll + ".delay", delay);

		plugin.saveConfig();
	}

	public void setUses(String scroll, int uses) {
		config.set("Scrolls." + scroll + ".uses", uses);

		plugin.saveConfig();
	}

	public void setDestinationHidden(String scroll, boolean status) {
		config.set("Scrolls." + scroll + ".destination hidden", status);

		plugin.saveConfig();
	}

	public void setCancelOnMove(String scroll, boolean status) {
		config.set("Scrolls." + scroll + ".cancel on move", status);

		plugin.saveConfig();
	}

	public void setDestination(String scroll, Location location) {
		config.set(
				"Scrolls." + scroll + ".destination",
				location.getWorld().getName() + ", " + location.getBlockX()
						+ ", " + location.getBlockY() + ", "
						+ location.getBlockZ());

		plugin.saveConfig();
	}
	
	public String addColourCode(String oldString) {
		return ChatColor.translateAlternateColorCodes('&', oldString);
	}

	public Set<String> getScrollsInConfig() {
		return config.getConfigurationSection("Scrolls").getKeys(false);
	}
	
	public boolean doLoadChunk() {
		return config.getBoolean("Scroll.load-chunk-on-teleport");
	}
}
