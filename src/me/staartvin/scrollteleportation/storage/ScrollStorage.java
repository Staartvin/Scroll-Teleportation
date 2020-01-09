package me.staartvin.scrollteleportation.storage;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.files.MainConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScrollStorage {

    private ScrollTeleportation plugin;
    private List<Scroll> loadedScrolls = new ArrayList<>();

    public ScrollStorage(ScrollTeleportation instance) {
        this.plugin = instance;
    }

    public void addLoadedScroll(Scroll scroll) {
        if (loadedScrolls.contains(scroll)) {
            loadedScrolls.add(scroll);
        }
    }

    public void removeLoadedScroll(String scrollName) {
        loadedScrolls.removeIf(scroll -> scroll.getDisplayName().equalsIgnoreCase(scrollName) || scroll.getInternalName().equalsIgnoreCase(scrollName));
    }

    public Optional<Scroll> getLoadedScroll(String scrollName) {
        return loadedScrolls.stream().filter(scroll -> scroll.getInternalName().equalsIgnoreCase(scrollName) || scroll.getDisplayName().equalsIgnoreCase(scrollName)).findFirst();
    }

    public boolean loadScrollsFromConfig() {

        // First clear all scrolls.
        loadedScrolls.clear();

        MainConfig mainConfig = plugin.getMainConfig();

        // Go over entry in the config and load the scroll in memory
        for (String internalScrollName : mainConfig.getScrollsInConfig()) {

            Scroll scroll = new Scroll(internalScrollName);

            scroll.setDisplayName(mainConfig.getScrollDisplayName(internalScrollName));
            scroll.setDescriptionLore(mainConfig.getLoreStrings(internalScrollName));
            scroll.setCancelOnMove(mainConfig.doCancelOnMove(internalScrollName));
            scroll.setDestinationHidden(mainConfig.isDestinationHidden(internalScrollName));
            scroll.setEffects(mainConfig.getEffects(internalScrollName));
            scroll.setTeleportDelay(mainConfig.getDelay(internalScrollName));
            scroll.setUses(mainConfig.getTotalUses(internalScrollName));
            scroll.setDestination(mainConfig.getScrollDestination(internalScrollName));
            scroll.setMaterial(mainConfig.getScrollMaterial());

            loadedScrolls.add(scroll);
        }

        return true;
    }

    public void giveScrollToPlayer(Player player, String scrollName) {
        Scroll scroll = this.getLoadedScroll(scrollName).orElse(null);

        if (scroll == null) return;


    }

    public Optional<Scroll> getScrollByItemStack(ItemStack stack) {
        if (!Scroll.hasPersistentData(stack, Scroll.KEY_INTERNAL_NAME, PersistentDataType.STRING))
            return Optional.empty();

        NamespacedKey key = new NamespacedKey(plugin, Scroll.KEY_INTERNAL_NAME);

        String internalScrollName = stack.getItemMeta().getPersistentDataContainer().get(key,
                PersistentDataType.STRING);

        return this.getLoadedScroll(internalScrollName);
    }

}
