package me.staartvin.scrollteleportation.teleporthandler;

import java.util.ArrayList;
import java.util.List;

import me.staartvin.scrollteleportation.ScrollTeleportation;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TeleportHandler {

	private ScrollTeleportation plugin;
	List<String> readyToBeTeleported = new ArrayList<String>();

	public TeleportHandler(ScrollTeleportation instance) {
		plugin = instance;
	}

	public boolean isReady(String playerName) {
		return readyToBeTeleported.contains(playerName);
	}

	public void setReady(String playerName, Boolean status) {
		if (status) {
			if (!readyToBeTeleported.contains(playerName)) {
				readyToBeTeleported.add(playerName);
			}
		} else {
			if (readyToBeTeleported.contains(playerName)) {
				readyToBeTeleported.remove(playerName);
			}
		}
	}

	public void teleport(Player player, Location destination, ItemStack item) {
		// Check if player hasn't moved
		if (plugin.getTeleportHandler().isReady(player.getName())) {

			// Teleport
			player.teleport(destination);

			// Remove from ready list
			plugin.getTeleportHandler().setReady(player.getName(), false);

			// Send message
			player.sendMessage(plugin.getMainConfig().getTeleportMessage());

			// Decrease use
			decreaseUse(item, player);
			
			// Play effects
			//player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
		}
	}

	public void decreaseUse(ItemStack item, Player player) {

		// Player can bypass uses so no 'use' is used.
		if (player.hasPermission("scrollteleportation.usesbypass")) {
			return;
		}
		
		ItemMeta im = item.getItemMeta();
		List<String> lore = im.getLore();
		// Get last lore line
		String uses = plugin.fixName(lore
				.get(lore.size() - 1));
		
		// Old scroll
		if (!uses.contains("Uses:")) {
			uses = "Uses: 1";
		}

		// Only get number
		uses = uses.replace("Uses:", "");

		// Trim string
		uses = uses.trim();

		// Get uses
		int realUses = Integer.parseInt(uses);

		if (realUses == 1) {
			// Remove item because it was last use
			player.getInventory().remove(item);
			
			return;
		} else {
			
			// Remove it first, then give it back
			player.getInventory().remove(item);
			
			// Remove a use
			realUses = realUses - 1;
			
			uses = ChatColor.GREEN + "Uses: " + realUses;
			
			// Change use (Set last lore to uses)
			lore.set(lore.size() - 1, uses);
			
			// Set lore in ItemMeta
			im.setLore(lore);
			
			// Save item
			item.setItemMeta(im);
			
			// Give new item back (1 less use)
			player.getInventory().addItem(item);
		}
	}
}
