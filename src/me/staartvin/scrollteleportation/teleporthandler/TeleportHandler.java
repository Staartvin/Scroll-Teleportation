package me.staartvin.scrollteleportation.teleporthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.exceptions.PotionEffectInvalidException;

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
	public HashMap<String, Integer> taskID = new HashMap<String, Integer>();
	
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
			try {
				applyEffects(player, plugin.getMainConfig().getScroll(plugin.fixName(item.getItemMeta().getDisplayName())));
			} catch (PotionEffectInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		// Get uses - if infinite, set it to -1.
		int realUses = uses.equalsIgnoreCase("infinite") ? -1 : Integer.parseInt(uses);

		if (realUses == 1) {
			// Remove item because it was last use
			player.getInventory().remove(item);
			
			return;
		} else if (realUses < 0) {
			// Do nothing, as it is infinitely many times used.
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
	
	public void applyEffects(Player player, String scroll) throws PotionEffectInvalidException {
		
		// Player does not get potion effects
		if (player.hasPermission("scrollteleportation.potioneffectbypass")) return;
		
		List<String> effects = plugin.getMainConfig().getEffects(scroll);
		
		for (String effect: effects) {
			String[] args = effect.split(" ");
			
			if (args.length != 2) {
				throw new PotionEffectInvalidException("Missing duration argument for '" + effect + "'!");
			}
			
			String realEffect = args[0].trim();
			int duration = 1;
			
			try {
				duration = Integer.parseInt(args[1].trim());
			} catch (Exception e) {
				throw new PotionEffectInvalidException("Invalid duration for '" + effect + "'!");
			}
			
			PotionEffectType potionEffect = matchPotionEffect(realEffect);
			
			if (potionEffect == null) {
				throw new PotionEffectInvalidException("PotionEffect '" + realEffect + "' is not a valid effect!");
			}
			
			// Play effect 
			player.addPotionEffect(new PotionEffect(potionEffect, duration * 20, 1));
		}	
	}
	
	public PotionEffectType matchPotionEffect(String effectName) {
		if (effectName.equalsIgnoreCase("speed")) return PotionEffectType.SPEED;
		else if (effectName.equalsIgnoreCase("slow")) return PotionEffectType.SLOW;
		else if (effectName.equalsIgnoreCase("fast_digging")) return PotionEffectType.FAST_DIGGING;
		else if (effectName.equalsIgnoreCase("slow_digging")) return PotionEffectType.SLOW_DIGGING;
		else if (effectName.equalsIgnoreCase("increase_damage")) return PotionEffectType.INCREASE_DAMAGE;
		else if (effectName.equalsIgnoreCase("heal")) return PotionEffectType.HEAL;
		else if (effectName.equalsIgnoreCase("harm")) return PotionEffectType.HARM;
		else if (effectName.equalsIgnoreCase("jump")) return PotionEffectType.JUMP;
		else if (effectName.equalsIgnoreCase("confusion")) return PotionEffectType.CONFUSION;
		else if (effectName.equalsIgnoreCase("regeneration")) return PotionEffectType.REGENERATION;
		else if (effectName.equalsIgnoreCase("damage_resistance")) return PotionEffectType.DAMAGE_RESISTANCE;
		else if (effectName.equalsIgnoreCase("fire_resistance")) return PotionEffectType.FIRE_RESISTANCE;
		else if (effectName.equalsIgnoreCase("water_breathing")) return PotionEffectType.WATER_BREATHING;
		else if (effectName.equalsIgnoreCase("invisibility")) return PotionEffectType.INVISIBILITY;
		else if (effectName.equalsIgnoreCase("blindness")) return PotionEffectType.BLINDNESS;
		else if (effectName.equalsIgnoreCase("night_vision")) return PotionEffectType.NIGHT_VISION;
		else if (effectName.equalsIgnoreCase("hunger")) return PotionEffectType.HUNGER;
		else if (effectName.equalsIgnoreCase("weakness")) return PotionEffectType.WEAKNESS;
		else if (effectName.equalsIgnoreCase("poison")) return PotionEffectType.POISON;
		else if (effectName.equalsIgnoreCase("wither")) return PotionEffectType.WITHER;
		else return null;
	}
}
