package me.staartvin.scrollteleportation.listeners;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

	private ScrollTeleportation plugin;

	public PlayerMoveListener(ScrollTeleportation instance) {
		plugin = instance;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		// Player only moved to look around but didn't walk
		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockY() == event.getTo().getBlockY()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		if (plugin.getTeleportHandler().isReady(player.getName())) {

			if (player.hasPermission("scrollteleportation.walkbypass"))
				return;

			String scroll = plugin.getMainConfig().getScroll(
					plugin.fixName(player.getItemInHand().getItemMeta()
							.getDisplayName()));
			
			if (!plugin.getMainConfig().doCancelOnMove(scroll))
				return;

			// Player has moved so teleportation is cancelled
			plugin.getTeleportHandler().setReady(player.getName(), false);

			if (plugin.getTeleportHandler().taskID.get(player.getName()) != null) {
				// Cancel teleport task
				plugin.getServer().getScheduler().cancelTask(plugin.getTeleportHandler().taskID.get(player.getName()));
				
				// Set taskID null
				plugin.getTeleportHandler().taskID.put(player.getName(), null);
			}
			
			// Inform player
			player.sendMessage(ChatColor.RED
					+ "Teleportation is cancelled because you moved.");
		}
	}
}
