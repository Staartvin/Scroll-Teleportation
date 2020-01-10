package me.staartvin.scrollteleportation.storage;

import com.sun.istack.internal.NotNull;
import me.staartvin.scrollteleportation.exceptions.DestinationInvalidException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Random;

public class ScrollDestination {

    @NotNull
    private DestinationType destinationType = null;
    // Depending on the destination type, this variable is set.
    private Location location = null;
    // If the destination type is RANDOM_IN_RANGE, this variable is used to determine the radius of the range.
    private int rangeRadius = 5;
    // The original string used to determine the location.
    private String locationString = null;

    public static ScrollDestination createFromLocationString(String locationString) throws DestinationInvalidException {

        ScrollDestination destination = new ScrollDestination();

        locationString = locationString.toLowerCase();

        if (!locationString.contains(",")) {
            // Can either be a fixed name or random location

            // We know it's truly random
            if (locationString.contains("random")) {
                destination.setDestinationType(DestinationType.RANDOM);
            } else { // It must be a name
                destination.setDestinationType(DestinationType.FIXED_NAME);
                destination.setupFixedNameLocation(locationString);
            }
        } else {
            // It can either random radius or a fixed location

            if (locationString.contains("random_radius")) {
                destination.setDestinationType(DestinationType.RANDOM_IN_RANGE);
                destination.setupRandomRadiusLocation(locationString);
            } else {
                destination.setDestinationType(DestinationType.FIXED_LOCATION);
                destination.setupFixedLocation(locationString);
            }
        }

        // Store location string so we can use it later (if we need to).
        destination.locationString = locationString.trim();

        return destination;
    }

    private static String locationToString(Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " on world " + location.getWorld().getName();
    }

    public Location getLocation() throws DestinationInvalidException {

        if (destinationType == DestinationType.FIXED_LOCATION) {
            return location;
        } else if (destinationType == DestinationType.RANDOM) {
            return this.getRandomLocation();
        } else if (destinationType == DestinationType.RANDOM_IN_RANGE) {
            return this.getRandomLocationWithRadius();
        } else if (destinationType == DestinationType.FIXED_NAME) {
            return this.getFixedNameLocation();
        }

        return null;
    }

    public String getLocationDescription() {
        if (destinationType == DestinationType.FIXED_LOCATION) {
            return this.getFixedLocationString();
        } else if (destinationType == DestinationType.RANDOM) {
            return this.getRandomLocationString();
        } else if (destinationType == DestinationType.RANDOM_IN_RANGE) {
            return this.getRandomRadiusLocationString();
        } else if (destinationType == DestinationType.FIXED_NAME) {
            return this.getFixedNameLocationString();
        }

        return "unknown";
    }

    public DestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    private void setupRandomRadiusLocation(String locationString) throws DestinationInvalidException {

        String point, radiusString;

        if (!locationString.contains("point")) {
            throw new DestinationInvalidException("No point given for '"
                    + location + "'!");
        }

        if (!locationString.contains("radius")) {
            throw new DestinationInvalidException("No radius given for '"
                    + location + "'!");
        }

        // Calculate start point and radius
        // point=world,x,y,z radius=1000
        String info = locationString.replaceAll(".*\\(|\\).*", "");

        String[] args = info.split(" ");

        if (args.length != 2)
            throw new DestinationInvalidException(
                    "Not all arguments were given for '" + locationString + "'!");

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
        this.rangeRadius = Integer.parseInt(radiusString);

        if (pointInfo.length != 4) {
            throw new DestinationInvalidException(
                    "Not all arguments were given in '" + point + "' for '"
                            + locationString + "'!");
        }

        if (Bukkit.getServer().getWorld(pointInfo[0]) == null) {
            throw new DestinationInvalidException("World '" + pointInfo[0]
                    + "' in '" + locationString + "' does not exist!");
        }

        World world = Bukkit.getServer().getWorld(pointInfo[0]);

        this.location = new Location(world, Integer.parseInt(pointInfo[1]),
                Integer.parseInt(pointInfo[2]), Integer.parseInt(pointInfo[3]));

    }

    private void setupFixedLocation(String locationString) throws DestinationInvalidException {
        String[] args = locationString.split(",");

        if (args.length != 4) {
            throw new DestinationInvalidException(
                    "More or less than 4 arguments defined for '" + locationString
                            + "'!");
        }

        String x, y, z, worldName;

        worldName = args[0].trim();
        x = args[1].trim();
        y = args[2].trim();
        z = args[3].trim();

        World realWorld = Bukkit.getServer().getWorld(worldName);

        if (realWorld == null) {
            throw new DestinationInvalidException(
                    "Unknown world '" + worldName + "' defined in '" + locationString
                            + "'!");
        }

        this.location = new Location(realWorld, Integer.parseInt(x),
                Integer.parseInt(y), Integer.parseInt(z));
    }

    private void setupFixedNameLocation(String locationString) throws DestinationInvalidException {
        if (locationString == null) {
            throw new DestinationInvalidException("There is no name specified for the destination!");
        }

        if (locationString.contains("spawn")) {

            String[] params = locationString.split(" ");

            if (params.length < 2) {
                throw new DestinationInvalidException("The destination name '" + locationString + "' did not specify " +
                        "a world!");
            }

            String worldName = params[1];

            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                throw new DestinationInvalidException("World '" + worldName + "' does not exist!");
            }

            this.location = world.getSpawnLocation();

            return;
        }

        throw new DestinationInvalidException("Fixed name '" + locationString + "' unknown!");

    }

    private Location getFixedNameLocation()
            throws DestinationInvalidException {
        return location;
    }

    private Location getRandomLocation()
            throws DestinationInvalidException {
        double random = Math.random();
        boolean above = false;
        int x, y, z;
        World world;

        // Will it be a positive or negative number
        above = random > 0.45D;

        x = getRandomNumberRange(1, 10000);
        y = getRandomNumberRange(1, 250);
        z = getRandomNumberRange(1, 10000);

        if (!above) {
            // Make it a negative amount.
            x = x - (x * 2);
            z = z - (z * 2);
        }

        boolean worldSpecified = false;

        String[] params = this.locationString.split(" ");

        if (params.length > 1) {
            worldSpecified = true;
        }

        if (worldSpecified) {
            String worldName = params[1];

            world = Bukkit.getWorld(worldName);

            if (world == null) {
                throw new DestinationInvalidException("World '" + worldName + "' does not exist!");
            }
        } else {
            // Choose a random world since none was specified.
            List<World> worlds = Bukkit.getServer().getWorlds();

            int randomWorld = getRandomNumberRange(0, (worlds.size() - 1));
            world = worlds.get(randomWorld);
        }

        return new Location(world, x, y, z);
    }

    private int getRandomNumberRange(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    private Location getRandomLocationWithRadius() {
        int xOffSet, yOffSet, zOffSet;

        // All points are calculated.
        int xLowest = this.location.getBlockX() - this.rangeRadius;
        int xBiggest = this.location.getBlockX() + this.rangeRadius;

        int xDifference = xBiggest - xLowest;

        int zLowest = this.location.getBlockZ() - this.rangeRadius;
        int zBiggest = this.location.getBlockZ() + this.rangeRadius;

        int zDifference = zBiggest - zLowest;

        xOffSet = getRandomNumberRange(0, (xDifference - 1));
        yOffSet = getRandomNumberRange(1, 250);
        zOffSet = getRandomNumberRange(0, (zDifference - 1));

        int x = xLowest + xOffSet, y = yOffSet, z = zLowest + zOffSet;

        return new Location(this.location.getWorld(), x, y, z);
    }

    private Location secureLocation(Location oldLocation) {

        World world = oldLocation.getWorld();
        int x = oldLocation.getBlockX(), y = oldLocation.getBlockY(), z = oldLocation
                .getBlockZ();

        // Block for feet
        Block blockOnY = world.getBlockAt(x, y, z);
        // Block for head
        Block blockOnY2 = world.getBlockAt(x, (y + 1), z);
        // Block below feet
        Block blockOnY3 = world.getBlockAt(x, (y - 1), z);

        Location safeLocation = null;

        // TODO : Find fix for loading chunk before teleporting
        // Load chunk before teleporting
//        if (plugin.getMainConfig().doLoadChunk()) {
//            if (!oldLocation.getChunk().isLoaded()) {
//                // If chunk is not loaded -> load chunk
//
//                oldLocation.getChunk().load();
//            }
//        }

        // Player would suffocate or fall down
        if (!blockOnY.getType().equals(Material.AIR)
                || !blockOnY2.getType().equals(Material.AIR)
                || blockOnY3.getType().equals(Material.AIR)) {
            int safeY = oldLocation.getWorld().getHighestBlockYAt(x, z);
            safeLocation = new Location(world, x, safeY, z);
        } else {
            safeLocation = oldLocation;
        }
        return safeLocation;
    }

    private String getFixedNameLocationString() {
        return locationToString(location);
    }

    private String getFixedLocationString() {

        if (locationString.contains("spawn")) {
            return "spawn";
        }

        return "a named location";
    }

    private String getRandomLocationString() {
        return "a random location";
    }

    private String getRandomRadiusLocationString() {
        return "within " + rangeRadius + " meters around " + locationToString(location);
    }

    public enum DestinationType {FIXED_LOCATION, RANDOM, RANDOM_IN_RANGE, FIXED_NAME}
}
