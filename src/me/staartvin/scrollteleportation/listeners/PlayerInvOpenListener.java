package me.staartvin.scrollteleportation.listeners;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class PlayerInvOpenListener implements Listener {

	private ScrollTeleportation plugin;

	public PlayerInvOpenListener(ScrollTeleportation instance) {
		plugin = instance;
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();

		if (event.getInventory().getType() == InventoryType.PLAYER)
			return;

		if (plugin.getTeleportHandler().isReady(player.getName())) {

			if (player.hasPermission("scrollteleportation.invbypass"))
				return;

			//if (!plugin.getMainConfig().doCancelOnMove(scroll))
			//return;

			// Player has moved so teleportation is cancelled
			plugin.getTeleportHandler().setReady(player.getName(), false);

			if (plugin.getTeleportHandler().taskID.get(player.getName()) != null) {
				// Cancel teleport task
				plugin.getServer()
						.getScheduler()
						.cancelTask(
								plugin.getTeleportHandler().taskID.get(player
										.getName()));

				// Set taskID null
				plugin.getTeleportHandler().taskID.put(player.getName(), null);
			}

			// Inform player
			player.sendMessage(ChatColor.RED
					+ "Teleportation is cancelled because you opened an inventory.");
		}
	}
}
