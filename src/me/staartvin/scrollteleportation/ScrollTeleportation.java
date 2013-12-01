package me.staartvin.scrollteleportation;

import me.staartvin.scrollteleportation.commands.CommandHandler;
import me.staartvin.scrollteleportation.files.MainConfig;
import me.staartvin.scrollteleportation.listeners.PlayerInteractListener;
import me.staartvin.scrollteleportation.listeners.PlayerInvOpenListener;
import me.staartvin.scrollteleportation.listeners.PlayerMoveListener;
import me.staartvin.scrollteleportation.teleporthandler.DestinationHandler;
import me.staartvin.scrollteleportation.teleporthandler.TeleportHandler;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the official plugin 'Scroll Teleportation'. This code is written by Staartvin himself
 * and this code is 'all rights reserved'. You may not copy any code from this plugin without his
 * permission. 
 * 
 * @author Staartvin
 * 
 */
public class ScrollTeleportation extends JavaPlugin {

	private MainConfig config = new MainConfig(this);
	private TeleportHandler teleHandler = new TeleportHandler(this);
	private DestinationHandler desHandler = new DestinationHandler(this);
	
	// Test push
	
	public void onEnable() {
		// Load configuration file
		config.loadConfiguration();
		
		// Register listeners
		registerListeners();
		
		// Register command
		getCommand("scroll").setExecutor(new CommandHandler(this));
		
		getLogger().info("Scroll Teleportation v" + getDescription().getVersion() + " has been enabled.");
	}
	
	public void onDisable() {
		
		// Properly stop tasks
		getServer().getScheduler().cancelTasks(this);
		
		getLogger().info("Scroll Teleportation v" + getDescription().getVersion() + " has been disabled.");
	}
	
	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerInvOpenListener(this), this);
	}
	
	public MainConfig getMainConfig() {
		return config;
	}
	
	public TeleportHandler getTeleportHandler() {
		return teleHandler;
	}
	
	/**
	 * The displayname of an item has colours in it.
	 * Those need to be removed before comparing it.
	 * @param oldDisplayName Displayname to fix.
	 * @return A string without colours.
	 */
	public String fixName(String oldDisplayName) {
		return oldDisplayName.replace("§0", "")
				.replace("§1", "").replace("§2", "").replace("§3", "")
				.replace("§4", "").replace("§5", "").replace("§6", "")
				.replace("§7", "").replace("§8", "").replace("§9", "")
				.replace("§a", "").replace("§b", "").replace("§c", "")
				.replace("§d", "").replace("§e", "").replace("§f", "");
	}
	
	public DestinationHandler getDestinationHandler() {
		return desHandler;
	}
}
