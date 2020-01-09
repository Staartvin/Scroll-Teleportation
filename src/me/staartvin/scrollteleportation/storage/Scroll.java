package me.staartvin.scrollteleportation.storage;

import com.sun.istack.internal.NotNull;
import me.staartvin.scrollteleportation.ScrollTeleportation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scroll {

    public static String KEY_INTERNAL_NAME = "internalName";
    public static String KEY_TOTAL_USES = "totalUses";
    public static int SCROLL_USES_INFINITE = -1;

    @NotNull
    private String internalName, displayName;
    private List<String> descriptionLore = new ArrayList<>();
    private boolean destinationHidden = false, cancelOnMove = true;
    private int teleportDelay = 5; // In seconds
    private int uses = 1;
    private List<PotionEffect> effects = new ArrayList<>();
    private ScrollDestination destination;
    private Material material = Material.PAPER;

    public Scroll(String internalName) {
        this.setInternalName(internalName);
    }


    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getDescriptionLore() {
        return descriptionLore;
    }

    public void setDescriptionLore(List<String> lore) {
        this.descriptionLore = lore;
    }

    public boolean isDestinationHidden() {
        return destinationHidden;
    }

    public void setDestinationHidden(boolean destinationHidden) {
        this.destinationHidden = destinationHidden;
    }

    public boolean isCancelledOnMove() {
        return cancelOnMove;
    }

    public void setCancelOnMove(boolean cancelOnMove) {
        this.cancelOnMove = cancelOnMove;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<PotionEffect> effects) {
        this.effects = effects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scroll scroll = (Scroll) o;
        return displayName.equals(scroll.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName);
    }

    public ScrollDestination getDestination() {
        return destination;
    }

    public void setDestination(ScrollDestination destination) {
        this.destination = destination;
    }

    public ItemStack getItemStack() {

        ItemStack item = new ItemStack(this.getMaterial(), 1);

        ItemMeta im = item.getItemMeta();

        // Set name
        im.setDisplayName(ChatColor.GOLD + this.getDisplayName());

        String destination = null;

        if (this.isDestinationHidden()) {
            destination = "Destination: Unknown";
        } else {
            destination = "Destination: " + getDestination().getLocationDescription();
        }

        // Set lore

        List<String> lores = new ArrayList<>(this.descriptionLore);

        // Add destination
        lores.add(ChatColor.GREEN + destination);

        // Add uses
        lores.add(ChatColor.GREEN + "Uses: " + this.getUses());

        // Set real lore
        im.setLore(lores);


        NamespacedKey internalNameKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(
                "ScrollTeleportation")), KEY_INTERNAL_NAME);

        NamespacedKey totalUsesKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(
                "ScrollTeleportation")), KEY_TOTAL_USES);

        // Store the internal name in the itemstack so that we can retrieve it later.
        im.getPersistentDataContainer().set(internalNameKey, PersistentDataType.STRING, this.getInternalName());

        // Store the uses of the scroll so we can store it in the itemstack.
        im.getPersistentDataContainer().set(totalUsesKey, PersistentDataType.INTEGER, this.getUses());

        // Set ItemMeta
        item.setItemMeta(im);

        return item;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void applyEffects(Player player) {
        player.addPotionEffects(this.effects);
    }

    public static boolean hasPersistentData(ItemStack stack, String keyString, PersistentDataType dataType) {

        ScrollTeleportation plugin = (ScrollTeleportation) Bukkit.getPluginManager().getPlugin(
                "ScrollTeleportation");

        if (stack == null) return false;

        if (stack.getType() != plugin.getMainConfig().getScrollMaterial()) return false;

        if (!stack.hasItemMeta()) return false;

        if (stack.getItemMeta() == null) return false;

        NamespacedKey key = new NamespacedKey(plugin, keyString);

        return stack.getItemMeta().getPersistentDataContainer().has(key, dataType);
    }
}
