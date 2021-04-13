package de.raidcraft.loot.util;

import de.raidcraft.loot.Constants;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

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

    /**
     * Guesses the reward type of the given config section and returns the type identifier of the reward.
     * <p>This method looks for keys inside the config and then infers the type.
     * e.g.: a {@code item:} or {@code command:} key lets us assume the relevant type.
     * <p>Only works for built-in types in the de.raidcraft.loot.types package.
     *
     * @param config the config to use for the type lookup. may be null.
     * @return the type of it could be guessed or an empty optional otherwise
     */
    public static Optional<String> guessType(ConfigurationSection config) {

        if (config == null) return Optional.empty();

        if (config.isSet("item")) {
            return Optional.of(Constants.Types.ITEM);
        } else if (config.isSet("command")) {
            return Optional.of(Constants.Types.COMMAND);
        }

        return Optional.empty();
    }
}
