package de.raidcraft.loot.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.io.File;
import java.nio.file.Path;

public class ConfigUtil {

    /**
     * Extracts a file identifier from the two gives paths based on the relative path.
     * The identifier will be lowercased and every subdirectly is split by a dot.
     *
     * @param base the base path
     * @param file the file
     * @return the file identifier
     */
    public static String getFileIdentifier(Path base, File file) {

        Path relativePath = base.relativize(file.toPath());
        return relativePath.toString()
                .replace("\\", "/")
                .toLowerCase()
                .replace(".yml", "")
                .replace(".yaml", "");
    }

    /**
     * Merges the two configurations overwriting any value from the parent with
     * the defined value in the child.
     * <p>A new configuration section is returned with the merged values from both configs.
     *
     * @param parent the config that provides the default values
     * @param child the config that overwrites values from the parent
     * @return a new configuration with the merged values from both configs
     */
    public static ConfigurationSection merge(ConfigurationSection parent, ConfigurationSection child) {

        MemoryConfiguration config = new MemoryConfiguration();

        for (String key : parent.getKeys(true)) {
            config.set(key, parent.get(key));
        }

        for (String key : child.getKeys(true)) {
            config.set(key, child.get(key));
        }

        return config;
    }
}
