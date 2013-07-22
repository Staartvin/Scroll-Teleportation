package me.staartvin.scrollteleportation.tasks;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeleportRunnable implements Runnable {

	private Location destination;
	private ItemStack item;
	private Player player;
	private ScrollTeleportation plugin;

	// This has to be run in sync because it teleports a player. Async will cause problems.
	public TeleportRunnable(ScrollTeleportation instance, Location location,
			ItemStack item, Player player) {
		plugin = instance;
		destination = location;
		this.item = item;
		this.player = player;
	}

	@Override
	public void run() {
		plugin.getTeleportHandler().teleport(player, destination, item);
	}

}
