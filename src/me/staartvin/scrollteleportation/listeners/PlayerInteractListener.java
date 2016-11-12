package me.staartvin.scrollteleportation.listeners;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.tasks.TeleportRunnable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class PlayerInteractListener implements Listener {

	private ScrollTeleportation plugin;

	public PlayerInteractListener(ScrollTeleportation instance) {
		plugin = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (plugin.getTeleportHandler().isReady(player.getName())) {

			if (player.hasPermission("scrollteleportation.walkbypass"))
				return;

			// When clicking a chest, don't count it as a click.
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Material clickedBlock = event.getClickedBlock().getType();
				if (clickedBlock == Material.CHEST || clickedBlock == Material.ENDER_CHEST
						|| clickedBlock == Material.TRAPPED_CHEST)
					return;
			}
			
			System.out.println("CLICKED: " + event.getAction());
			System.out.println("clicked block: " + event.getClickedBlock());

			// Player has moved so teleportation is cancelled
			plugin.getTeleportHandler().setReady(player.getName(), false);

			if (plugin.getTeleportHandler().taskID.get(player.getName()) != null) {
				// Cancel teleport task
				plugin.getServer().getScheduler().cancelTask(plugin.getTeleportHandler().taskID.get(player.getName()));

				// Set taskID null
				plugin.getTeleportHandler().taskID.put(player.getName(), null);
			}

			// Inform player
			player.sendMessage(ChatColor.RED + "Teleportation is cancelled because you interacted.");
			return;
		}

		// Did the player right click
		else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
				|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

			// When clicking a chest, don't count it as a click.
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Material clickedBlock = event.getClickedBlock().getType();
				if (clickedBlock == Material.CHEST || clickedBlock == Material.ENDER_CHEST
						|| clickedBlock == Material.TRAPPED_CHEST)
					return;
			}

			// Is there an item in the player's hand
			if (item == null)
				return;

			// Is the item in hand a paper
			if (!item.getType().equals(Material.getMaterial(plugin.getMainConfig().getScrollItemId())))
				return;

			// If item doesn't have item meta it can never be a scroll
			if (!item.hasItemMeta())
				return;

			ItemMeta im = item.getItemMeta();

			// Does the scroll have a name
			if (!im.hasDisplayName())
				return;

			// Change displayname
			im.setDisplayName(plugin.fixName(im.getDisplayName()));
			// Is there a scroll defined in the config with this name?
			if (plugin.getMainConfig().getScroll(im.getDisplayName()) == null)
				return;

			String scroll = plugin.getMainConfig().getScroll(im.getDisplayName());

			if (!player.hasPermission("scrollteleportation.teleport")) {
				player.sendMessage(ChatColor.RED + "You are not allowed to use scrolls!");
				return;
			}
			Location destination = null;

			destination = plugin.getDestinationHandler().createLocation(plugin.getMainConfig().getDestination(scroll),
					player);

			if (destination == null) {
				player.sendMessage(ChatColor.RED + "Destination could not be found!");
				return;
			}

			int delay = plugin.getMainConfig().getDelay(scroll);

			if (!player.hasPermission("scrollteleportation.delaybypass")) {
				// Inform player that he is going to be teleported.
				player.sendMessage(plugin.getMainConfig().getCastMessage().replace("%time%", delay + ""));
			}

			// Set player ready to be teleported
			plugin.getTeleportHandler().setReady(player.getName(), true);

			if (plugin.getMainConfig().doCancelOnMove(scroll)
					&& !player.hasPermission("scrollteleportationt.walkbypass")) {
				// Send warning
				player.sendMessage(plugin.getMainConfig().getMoveWarningMessage());
			}

			if (!player.hasPermission("scrollteleportation.delaybypass")) {
				// Teleport after delay

				BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin,
						new TeleportRunnable(plugin, destination, item, player), delay * 20);

				// Save taskID
				plugin.getTeleportHandler().taskID.put(player.getName(), task.getTaskId());

			} else {
				// Teleport instantly
				plugin.getTeleportHandler().teleport(player, destination, item);
			}
		}
	}
}
