package de.raidcraft.loot.config;

import de.raidcraft.loot.ConfigurationException;
import de.raidcraft.loot.LootManager;
import de.raidcraft.loot.LootObject;
import de.raidcraft.loot.util.ConfigUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.UUID;

@Log(topic = "RCLoot")
@Accessors(fluent = true)
@ToString(of = {"uuid", "config"})
@EqualsAndHashCode(of = {"uuid"})
public abstract class ConfiguredLootObject implements LootObject {

    private final UUID uuid = UUID.randomUUID();
    @Getter
    private final LootManager lootManager;
    @Getter
    private final ConfigurationSection config;

    public ConfiguredLootObject(LootManager lootManager, ConfigurationSection config) {
        this.lootManager = lootManager;
        this.config = config;
    }

    @Override
    public double chance() {

        if (config().isSet("chance")) {
            return config().getDouble("chance", 0);
        }

        return rarity().chance();
    }

    public ConfiguredLootObject chance(double chance) {

        config().set("chance", chance);

        return this;
    }

    @Override
    public boolean enabled() {

        return config().getBoolean("enabled", true);
    }

    public ConfiguredLootObject enabled(boolean enabled) {

        config().set("enabled", enabled);

        return this;
    }

    @Override
    public boolean always() {

        return config().getBoolean("always", false);
    }

    public ConfiguredLootObject always(boolean always) {

        config().set("always", always);

        return this;
    }

    @Override
    public boolean unique() {

        return config().getBoolean("unique", false);
    }

    public ConfiguredLootObject unique(boolean unique) {

        config().set("unique", unique);

        return this;
    }

    public boolean excludeFromRandom() {

        return config().getBoolean("exclude-from-random", false);
    }

    public ConfiguredLootObject excludeFromRandom(boolean exclude) {

        config().set("exclude-from-random", exclude);

        return this;
    }

    @Override
    public Rarity rarity() {

        try {
            if (config().isConfigurationSection("rarity")) {
                return new Rarity(config().getConfigurationSection("rarity"));
            } else if (config().isSet("rarity") && config.isString("rarity")) {
                return lootManager().rarity(config().getString("rarity"));
            }
        } catch (ConfigurationException e) {
            log.severe("rarity " + config().getString("rarity") + " configured in " + this + " not found: " + e.getMessage());
            e.printStackTrace();
        }

        log.warning("falling back to using default empty rarity in " + this);
        return new Rarity(new MemoryConfiguration());
    }

    public ConfiguredLootObject rarity(String rarity) {

        config().set("rarity", rarity);

        return this;
    }

    @Override
    public LootObject merge(LootObject lootObject) {

        if (lootObject instanceof ConfiguredLootObject) {
            return merge(((ConfiguredLootObject) lootObject).config());
        }

        return this;
    }

    /**
     * Merges this loot object with the given config.
     * <p>
     * Any values set in this loot object will take precedence over the given config.
     *
     * @param config the config to merge with this loot object
     * @return a new instance of this loot object with the merged config
     */
    public LootObject merge(@NonNull ConfigurationSection config) {

        return create(lootManager(), ConfigUtil.merge(config(), config));
    }

    protected abstract LootObject create(LootManager lootManager, ConfigurationSection config);
}
