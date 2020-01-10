package me.staartvin.scrollteleportation.listeners;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.exceptions.DestinationInvalidException;
import me.staartvin.scrollteleportation.files.LanguageString;
import me.staartvin.scrollteleportation.storage.Scroll;
import me.staartvin.scrollteleportation.tasks.TeleportRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

public class ActivateScrollListener implements Listener {

    private ScrollTeleportation plugin;
    private List<Material> ignoredBlocks;

    public ActivateScrollListener(ScrollTeleportation instance) {
        plugin = instance;

        // Create list of ignored blocks
        ignoredBlocks = Arrays.asList(Material.DISPENSER, Material.CHEST, Material.ENDER_CHEST,
                Material.TRAPPED_CHEST, Material.DROPPER, Material.FURNACE, Material.CRAFTING_TABLE, Material.MINECART,
                Material.CAULDRON, Material.DARK_OAK_DOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR,
                Material.JUNGLE_DOOR, Material.ACACIA_TRAPDOOR, Material.SPRUCE_DOOR, Material.DARK_OAK_DOOR);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Ignore off hand
        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

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

            // When clicking an ignored block, don't count it as a click.
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material clickedBlock = event.getClickedBlock().getType();

                // TODO: Find a way to ignore when clicking on a door with a scroll
//				if (ignoredBlocks.contains(clickedBlock))
//					return;
            }

            // Is there an item in the player's hand
            if (item == null)
                return;

            // Is the item in hand a paper
            if (!item.getType().equals(plugin.getMainConfig().getScrollMaterial()))
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

            Scroll scroll = plugin.getScrollStorage().getLoadedScroll(im.getDisplayName()).orElse(null);

            if (scroll == null) {
                return;
            }

            if (!player.hasPermission("scrollteleportation.teleport")) {
                player.sendMessage(plugin.getMainConfig().getTranslatableMessage(LanguageString.NOT_ALLOWED_TO_USE_SCROLL));
                return;
            }
            Location destination = null;
            try {
                destination = scroll.getDestination().getLocation();
            } catch (DestinationInvalidException e) {
                e.printStackTrace();
            }

            if (destination == null) {
                player.sendMessage(ChatColor.RED + "Destination could not be found!");
                return;
            }

            int delay = scroll.getTeleportDelay();

            if (!player.hasPermission("scrollteleportation.delaybypass")) {
                // Inform player that he is going to be teleported.
                player.sendMessage(plugin.getMainConfig().getTranslatableMessage(LanguageString.TELEPORTING_IN_TIME).replace("%time%", delay + ""));
            }

            // Set player ready to be teleported
            plugin.getTeleportHandler().setReady(player.getName(), true);

            if (scroll.isCancelledOnMove() && !player.hasPermission("scrollteleportation.walkbypass")) {
                // Send warning
                player.sendMessage(plugin.getMainConfig().getTranslatableMessage(LanguageString.MOVEMENT_WARNING));
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
