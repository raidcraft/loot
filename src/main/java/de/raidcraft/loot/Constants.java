package de.raidcraft.loot;

import org.bukkit.Material;

public final class Constants {

    public static final String DEFAULT_TYPE = "none";
    public static final Material DEFAULT_ICON = Material.LIME_STAINED_GLASS_PANE;

    private Constants() {}

    public static final class Placeholder {

        public static final String REWARD_NAME = "{REWARD_NAME}";
        public static final String PLAYER_NAME = "%player%";
    }

    public static final class Permissions {

        public static final String PREFIX = "rcloot.";
        public static final String ADMIN = PREFIX + "admin";
    }

    public static final class Types {

        public static final String COMMAND = "command";
        public static final String ITEM = "item";
        public static final String EMPTY = DEFAULT_TYPE;
    }
}
