package de.raidcraft.loot.config;

import de.raidcraft.loot.Constants;
import de.raidcraft.loot.util.ConfigUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

@EqualsAndHashCode(of = "config")
@Accessors(fluent = true)
public class Rarity {

    @Getter()
    private final ConfigurationSection config;

    public Rarity(ConfigurationSection config) {
        this.config = config;
    }

    public double chance() {

        return config.getDouble("chance", 0);
    }

    public Rarity chance(double chance) {

        config.set("chance", chance);

        return this;
    }

    public String name() {

        return config.getString("name");
    }

    public Rarity name(String name) {

        config.set("name", name);

        return this;
    }

    public String format() {

        return config.getString("format", Constants.Placeholder.LOOT_OBJECT_NAME);
    }

    public Rarity format(String format) {

        config.set("format", format);

        return this;
    }

    public int max() {

        return config.getInt("max", 0);
    }

    public Rarity max(int max) {

        config.set("max", max);

        return this;
    }

    /**
     * Merges the given configuration section with the config of this rarity.
     * <p>All values defined in the provided config will overwrite the values in this config.
     *
     * @param config the config that should be merged/overwrite values in this config
     * @return a new rarity instance with the merged values
     */
    public Rarity merge(@NonNull ConfigurationSection config) {

        return new Rarity(ConfigUtil.merge(this.config, config));
    }

    /**
     * Uses the config of the provided rarity and merges its config with this rarity config.
     *
     * @param rarity the rarity to merge with this rarity
     * @return a new rarity instance with the merged values
     * @see #merge(ConfigurationSection)
     */
    public Rarity merge(@NonNull Rarity rarity) {

        return merge(rarity.config());
    }

    @Override
    public String toString() {
        return "Rarity{" +
                "chance=" + chance() +
                ", name='" + name() + '\'' +
                ", max=" + max() +
                '}';
    }
}
