package me.staartvin.scrollteleportation.teleporthandler;

import java.util.List;
import java.util.Random;

import me.staartvin.scrollteleportation.ScrollTeleportation;
import me.staartvin.scrollteleportation.exceptions.DestinationInvalidException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DestinationHandler {

	private ScrollTeleportation plugin;

	public DestinationHandler(ScrollTeleportation instance) {
		plugin = instance;
	}

	public static enum destinationPoint {
		FIXED_LOCATION, RANDOM, RANDOM_RADIUS, FIXED_NAME
	};

	public Location createLocation(String location, Player player) {

		Location destination = null;
		@SuppressWarnings("rawtypes")
		Enum destinationEnum;
		try {
			destinationEnum = getEnumLocation(location);
		} catch (DestinationInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if (destinationEnum.equals(destinationPoint.FIXED_LOCATION)) {
			try {
				destination = getFixedLocation(location);
			} catch (DestinationInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (destinationEnum.equals(destinationPoint.FIXED_NAME)) {
			try {
				destination = getFixedNameLocation(location);
			} catch (DestinationInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (destinationEnum.equals(destinationPoint.RANDOM)) {
			try {
				destination = getRandomLocation(location);
			} catch (DestinationInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (destinationEnum.equals(destinationPoint.RANDOM_RADIUS)) {
			try {
				destination = getRandomLocationWithRadius(location);
			} catch (DestinationInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				throw new DestinationInvalidException(
						"Could not get destination type for '" + location
								+ "'!");
			} catch (DestinationInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return secureLocation(destination, player);
	}

	/**
	 * Tries to find the location enum type.
	 * 
	 * @param location Location string
	 * @return A {@link destinationPoint} enum type
	 * @throws DestinationInvalidException when no location enum type could be
	 *             found.
	 */
	private destinationPoint getEnumLocation(String location)
			throws DestinationInvalidException {

		String[] words = location.split(",");
		if (location.contains(",") && !location.contains("random") && words.length == 2) {
			return destinationPoint.FIXED_NAME;
		} else if (!location.contains(",") && location.contains("random")) {
			return destinationPoint.RANDOM;
		} else if (location.contains(",") && location.contains("(")
				&& location.contains(")") && location.contains("random_radius")) {
			return destinationPoint.RANDOM_RADIUS;
		} else if (location.contains(",") && !location.contains("random") && words.length == 4) {
			return destinationPoint.FIXED_LOCATION;
		} else {
			throw new DestinationInvalidException(
					"Could not get destination type for '" + location + "'!");
		}
	}

	private Location getFixedLocation(String location)
			throws DestinationInvalidException {
		String[] args = location.split(",");

		if (args.length != 4) {
			throw new DestinationInvalidException(
					"More or less than 4 arguments defined for '" + location
							+ "'!");
		}

		String x, y, z, world;

		world = args[0].trim();
		x = args[1].trim();
		y = args[2].trim();
		z = args[3].trim();

		World realWorld = plugin.getServer().getWorld(world);

		if (realWorld == null) {
			throw new DestinationInvalidException("World '" + world
					+ "' does not exist!");
		}

		Location realLocation = new Location(realWorld, Integer.parseInt(x),
				Integer.parseInt(y), Integer.parseInt(z));
		return realLocation;
	}

	private Location getFixedNameLocation(String location)
			throws DestinationInvalidException {
		if (location.contains("spawn")) {

			if (!location.contains(","))
				throw new DestinationInvalidException(
						"No world given for spawn at '" + location + "'!");
			String[] args = location.split(",");

			if (args.length < 2)
				throw new DestinationInvalidException(
						"No world given for spawn at '" + location + "'!");

			String world = args[1].trim();
			if (plugin.getServer().getWorld(world) == null)
				throw new DestinationInvalidException("World '" + world
						+ "' for '" + location + "' does not exist!");

			return plugin.getServer().getWorld(world).getSpawnLocation();
		} else {
			throw new DestinationInvalidException("Fixed name '" + location
					+ "' unknown!");
		}
	}

	private Location getRandomLocation(String location)
			throws DestinationInvalidException {
		double random = Math.random();
		boolean above = false;
		int x, y, z;
		World world;

		// Will it be a positive or negative number
		if (random > 0.45D)
			above = true;
		else
			above = false;

		x = getRandomNumberRange(1, 10000);
		y = getRandomNumberRange(1, 250);
		z = getRandomNumberRange(1, 10000);

		if (!above) {
			// Make it a negative amount.
			x = x - (x * 2);
			z = z - (z * 2);
		}

		
		String worldName = location.replaceAll(".*\\(|\\).*", "");
		// No world given. Select random world
		if (worldName.trim().isEmpty() || !location.contains("(") || !location.contains(")")) {
			List<World> worlds = plugin.getServer().getWorlds();

			int randomWorld = getRandomNumberRange(0, (worlds.size() - 1));
			world = worlds.get(randomWorld);
		} else {
			// World is specified. Search for world;

			if (plugin.getServer().getWorld(worldName) == null) {
				throw new DestinationInvalidException("World '" + worldName
						+ "' for '" + location + "' does not exist!");
			}

			world = plugin.getServer().getWorld(worldName);
		}

		return new Location(world, x, y, z);
	}

	private int getRandomNumberRange(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}

	private Location getRandomLocationWithRadius(String location)
			throws DestinationInvalidException {
		int xOffSet, yOffSet, zOffSet;
		Location pointLocation;
		World world;


		String point, radiusString;

		if (!location.contains("point")) {
			throw new DestinationInvalidException("No point given for '"
					+ location + "'!");
		}

		if (!location.contains("radius")) {
			throw new DestinationInvalidException("No radius given for '"
					+ location + "'!");
		}

		// Calculate start point and radius
		// point=world,x,y,z radius=1000
		String info = location.replaceAll(".*\\(|\\).*", "");

		String[] args = info.split(" ");

		if (args.length != 2)
			throw new DestinationInvalidException(
					"Not all arguments were given for '" + location + "'!");

		if (args[0].contains("point")) {
			point = args[0].replace("point=", "");
		} else {
			point = args[1].replace("point=", "");
		}

		if (args[0].contains("radius")) {
			radiusString = args[0].replace("radius=", "");
		} else {
			radiusString = args[1].replace("radius=", "");
		}

		String[] pointInfo = point.split(",");
		int radius = Integer.parseInt(radiusString);

		if (pointInfo.length != 4)
			throw new DestinationInvalidException(
					"Not all arguments were given in '" + point + "' for '"
							+ location + "'!");
		if (plugin.getServer().getWorld(pointInfo[0]) == null) {
			throw new DestinationInvalidException("World '" + pointInfo[0]
					+ "' for '" + location + "' does not exist!");
		}
		
		world = plugin.getServer().getWorld(pointInfo[0]);

		pointLocation = new Location(world, Integer.parseInt(pointInfo[1]),
				Integer.parseInt(pointInfo[2]), Integer.parseInt(pointInfo[3]));

		// All points are calculated.
		int xLowest = pointLocation.getBlockX() - radius;
		int xBiggest = pointLocation.getBlockX() + radius;
		
		int xDifference = xBiggest - xLowest;
		
		int zLowest = pointLocation.getBlockZ() - radius;
		int zBiggest = pointLocation.getBlockZ() + radius;
		
		int zDifference = zBiggest - zLowest;
		
		
		xOffSet = getRandomNumberRange(0,
				(xDifference - 1));
		yOffSet = getRandomNumberRange(1, 250);
		zOffSet = getRandomNumberRange(0,
				(zDifference - 1));
		
		int x = xLowest + xOffSet, y = yOffSet, z = zLowest + zOffSet;
		
		return new Location(world, x, y, z);
	}
	
	private Location secureLocation(Location oldLocation, Player player) {
		World world = oldLocation.getWorld();
		int x = oldLocation.getBlockX(),y = oldLocation.getBlockY(), z = oldLocation.getBlockZ();
		
		// Block for feet
		Block blockOnY = world.getBlockAt(x, y, z);
		// Block for head
		Block blockOnY2 = world.getBlockAt(x, (y + 1), z);
		// Block below feet
		Block blockOnY3 = world.getBlockAt(x, (y - 1), z);
		
		
		Location safeLocation = null;
		// Player would suffocate or fall down
		if (!blockOnY.getType().equals(Material.AIR) || !blockOnY2.getType().equals(Material.AIR) || blockOnY3.getType().equals(Material.AIR)) {
			int safeY = oldLocation.getWorld().getHighestBlockYAt(x, z);
			safeLocation = new Location(world, x, safeY, z);
		} else {
			safeLocation = oldLocation;
		}
		return safeLocation;
	}

}
