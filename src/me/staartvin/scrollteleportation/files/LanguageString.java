package me.staartvin.scrollteleportation.files;

public enum LanguageString {

    TELEPORTING_IN_TIME("teleporting message", "&6Teleporting in %time% seconds.."),
    MOVEMENT_WARNING("movement warning", "&cDon''t move or teleportation is cancelled."),
    COMMENCING_TELEPORT("teleport message", "&6Commencing teleport!"),
    NOT_ALLOWED_TO_USE_SCROLL("not allowed to use scroll", "&cYou are not allowed to use scrolls."),
    CANCELLED_DUE_TO_MOVEMENT("cancelled due to movement", "&cTeleportation is cancelled because you moved."),
    CANCELLED_DUE_TO_INTERACTION("cancelled due to interaction", "&cTeleportation is cancelled because you interacted" +
            "."),
    POTION_EFFECTS_APPLIED("potions effects applied", "&6You feel strange effects as you've been teleported..");

    private String configPath, defaultString;

    LanguageString(String configPath, String defaultString) {
        this.configPath = configPath;
        this.defaultString = defaultString;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getDefaultString() {
        return defaultString;
    }
}
