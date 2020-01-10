package me.staartvin.scrollteleportation.teleporthandler;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.files.LanguageString;
import me.staartvin.scrollteleportation.storage.Scroll;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeleportHandler {

    public HashMap<String, Integer> taskID = new HashMap<String, Integer>();
    List<String> readyToBeTeleported = new ArrayList<String>();
    private ScrollTeleportation plugin;

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
            readyToBeTeleported.remove(playerName);
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
            player.sendMessage(plugin.getMainConfig().getTranslatableMessage(LanguageString.COMMENCING_TELEPORT));

            // Decrease use
            decreaseUse(item, player);

            if (player.hasPermission("scrollteleportation.potioneffectbypass")) return;

            // Play effects (if present)
            plugin.getScrollStorage().getScrollByItemStack(item).ifPresent(scroll -> {
                scroll.applyEffects(player);

                if (scroll.getEffects().size() > 0) {
                    player.sendMessage(plugin.getMainConfig().getTranslatableMessage(LanguageString.POTION_EFFECTS_APPLIED));
                }

            });
        }
    }

    public void decreaseUse(ItemStack item, Player player) {

        // Player can bypass uses so no 'use' is used.
        if (player.hasPermission("scrollteleportation.usesbypass")) {
            return;
        }

        // The number of uses is not recorded in the item stack.
        if (!Scroll.hasPersistentData(item, Scroll.KEY_TOTAL_USES, PersistentDataType.INTEGER)) return;

        NamespacedKey key = new NamespacedKey(plugin, Scroll.KEY_TOTAL_USES);

        // Find the uses that are stored in the item stack.
        int currentUses = item.getItemMeta().getPersistentDataContainer().get(key,
                PersistentDataType.INTEGER);

        ItemMeta im = item.getItemMeta();

        // The uses are infinite, so we don't remove anything.
        if (currentUses == Scroll.SCROLL_USES_INFINITE) {
            return;
        }

        if (currentUses == 1 || currentUses == 0) {

            // If we have more than one scroll in this itemstack
            if (item.getAmount() != 1) {
                // Decrease amount by 1
                item.setAmount(item.getAmount() - 1);

                Scroll scroll = plugin.getScrollStorage().getScrollByItemStack(item).orElse(null);

                // Since we have used up one scroll, we reset the uses back to the total uses.
                if (scroll == null) {
                    currentUses = 1;
                } else {
                    currentUses = scroll.getUses() + 1;
                }


            } else { // This is the last item
                // Remove item because it was last use
                player.getInventory().remove(item);

                return;
            }
        }


        // Remove a use
        currentUses = currentUses - 1;

        // Store updated uses in tag.
        im.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, currentUses);

        String uses = ChatColor.GREEN + "Uses: " + currentUses;

        List<String> lore = im.getLore();

        // Change use (Set last lore to uses)
        lore.set(lore.size() - 1, uses);

        // Set lore in ItemMeta
        im.setLore(lore);

        // Save item
        item.setItemMeta(im);

    }
}
