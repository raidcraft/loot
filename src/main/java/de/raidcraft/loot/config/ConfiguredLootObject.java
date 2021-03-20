package de.raidcraft.loot.config;

import com.google.common.base.Strings;
import de.raidcraft.loot.*;
import de.raidcraft.loot.util.ConfigUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static de.raidcraft.loot.Constants.DEFAULT_TYPE;

@Log(topic = "RCLoot")
@Accessors(fluent = true)
@ToString(of = {"uuid", "config"})
@EqualsAndHashCode(of = {"uuid"})
public class ConfiguredLootObject implements LootObject {

    private final UUID uuid = UUID.randomUUID();
    @Getter
    private final LootManager lootManager;
    @Getter
    private final ConfigurationSection config;
    private ItemStack icon;

    public ConfiguredLootObject(LootManager lootManager, ConfigurationSection config) {
        this.lootManager = lootManager;
        this.config = config;
    }

    @Override
    public Optional<LootType> type() {

        String type = config().getString("type", DEFAULT_TYPE);
        return lootManager().lootType(type)
                .map(lootType -> {
                    try {
                        return lootType.load(Objects.requireNonNullElse(
                                config().getConfigurationSection("with"),
                                config().createSection("with"))
                        );
                    } catch (ConfigurationException e) {
                        log.severe("failed to load reward type " + type + " in reward " + toString() + ": " + e.getMessage());
                        return null;
                    }
                });
    }

    public ConfiguredLootObject type(String type) {

        config().set("type", type);

        return this;
    }

    @Override
    public String name() {

        String name = config().getString("name");
        if (Strings.isNullOrEmpty(name)) return null;

        return rarity().format().replace(Constants.Placeholder.REWARD_NAME, name);
    }

    public ConfiguredLootObject name(String name) {

        config().set("name", name);

        return this;
    }

    @Override
    public List<String> lore() {

        return config().getStringList("lore");
    }

    @Override
    public LootObject lore(String... lore) {

        config().set("lore", lore);

        return this;
    }

    @Override
    public Optional<ItemStack> icon() {

        if (icon != null) return Optional.of(icon);

        type().filter(lootType -> lootType instanceof IconProvider)
                .map(lootType -> (IconProvider) lootType)
                .map(IconProvider::icon)
                .ifPresentOrElse(itemStack -> icon = itemStack, () -> {
                    String icon = config().getString("icon");
                    if (Strings.isNullOrEmpty(icon)) return;

                    Material material = Material.matchMaterial(icon);
                    if (material == null) return;

                    this.icon = new ItemStack(material);
                });

        return Optional.ofNullable(icon);
    }

    @Override
    public LootObject icon(ItemStack icon) {

        this.icon = icon;

        return this;
    }

    public ConfiguredLootObject lore(List<String> lore) {

        config().set("lore", lore);

        return this;
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

    public ConfiguredLootObject merge(@NonNull ConfigurationSection config) {

        return new ConfiguredLootObject(lootManager(), ConfigUtil.merge(config(), config));
    }

    public ConfiguredLootObject merge(@NonNull ConfiguredLootObject config) {

        return merge(config.config());
    }

    /**
     * Merges this loot object with the given loot object if possible.
     * <p>The provided loot object is returned if merging is not possible.
     *
     * @param lootObject the loot object that should be merged
     * @return the provided loot object or a new merged loot object
     */
    public LootObject merge(LootObject lootObject) {

        if (lootObject instanceof ConfiguredLootObject) {
            return merge((ConfiguredLootObject) lootObject);
        }

        return lootObject;
    }

    /**
     * The rarity of the loot object contains additional display information
     * and the fallback chance of the object.
     *
     * @return the rarity of this loot object
     */
    public Rarity rarity() {

        try {
            if (config().isConfigurationSection("rarity")) {
                return new Rarity(config().getConfigurationSection("rarity"));
            } else if (config().isSet("rarity") && config.isString("rarity")) {
                return lootManager().rarity(config().getString("rarity"));
            }
        } catch (ConfigurationException e) {
            log.severe("rarity " + config().getString("rarity") + " configured in " + toString() + " not found: " + e.getMessage());
            e.printStackTrace();
        }

        log.warning("falling back to using default empty rarity in " + toString());
        return new Rarity(new MemoryConfiguration());
    }
}
