package de.raidcraft.loot;

import com.google.common.base.Strings;
import de.raidcraft.loot.annotations.RewardInfo;
import de.raidcraft.loot.config.ConfiguredLootObject;
import de.raidcraft.loot.config.ConfiguredLootTable;
import de.raidcraft.loot.config.Rarity;
import de.raidcraft.loot.types.CommandReward;
import de.raidcraft.loot.types.EmptyReward;
import de.raidcraft.loot.types.ItemReward;
import de.raidcraft.loot.util.ConfigUtil;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log(topic = "RCLoot")
public final class LootManager {

    private final RCLoot plugin;
    private final Map<String, Rarity> rarities = new HashMap<>();
    private final Map<String, RewardType.Registration<?>> lootTypes = new HashMap<>();
    private final Map<String, LootObject> rewards = new HashMap<>();
    private final Map<String, LootTable> lootTables = new HashMap<>();

    LootManager(RCLoot plugin) {
        this.plugin = plugin;

        register(EmptyReward.class, EmptyReward::new);
        register(ItemReward.class, ItemReward::new);
        register(CommandReward.class, CommandReward::new);
    }

    void load() {

        loadRarities();
        loadRewards();
        loadLootTables();
    }

    /**
     * Registers a new loot type for the given class.
     * <p>Make sure that the loot type is annotated with {@link RewardInfo} and has a unique
     * identifier. A {@link TypeRegistrationException} will be thrown instead.
     *
     * @param typeClass the class of the loot type that is registered
     * @param supplier the supplier that knows how to create an instance of the loot type
     * @param <TType> the type of the loot type class
     */
    public <TType extends RewardType> void register(@NonNull Class<TType> typeClass, @NonNull Supplier<TType> supplier) {

        if (!typeClass.isAnnotationPresent(RewardInfo.class)) {
            throw new TypeRegistrationException("loot type " + typeClass.getCanonicalName()
                    + " is missing the required @LootTypeInfo annotation");
        }

        String identifier = typeClass.getAnnotation(RewardInfo.class).value().toLowerCase();
        if (lootTypes.containsKey(identifier)) {
            log.severe("unable to register loot type " + typeClass.getCanonicalName()
                    + "! A type with the same identifier " + identifier + " is already registered: "
                    + lootTypes.get(identifier).typeClass().getCanonicalName()
            );
        } else {
            RewardType.Registration<?> registration = new RewardType.Registration<>(identifier, typeClass, supplier);
            lootTypes.put(identifier, registration);
            log.info("registered loot type " + identifier + ": " + typeClass.getCanonicalName());
        }
    }

    /**
     * Tries to find a loot type for the given type identifier
     * and creates a new instance of the given type.
     *
     * @param type the identifier of the loot type, e.g. none or item.
     *             can be null or empty, but will return an empty optional.
     * @return a new loot type instance if the type was found
     */
    public Optional<RewardType> lootType(String type) {

        if (Strings.isNullOrEmpty(type)) return Optional.empty();

        return Optional.ofNullable(lootTypes.get(type.toLowerCase()))
                .map(RewardType.Registration::supplier)
                .map(Supplier::get);
    }

    /**
     * Tries to find a loot table with the given identifier.
     *
     * @param identifier the identifier of the loot table.
     *                   can be null or empty.
     * @return the loot table or an empty optional.
     */
    public Optional<LootTable> lootTable(String identifier) {

        if (Strings.isNullOrEmpty(identifier)) return Optional.empty();

        return Optional.ofNullable(lootTables.get(identifier.toLowerCase()));
    }

    /**
     * Tries to find a loot object with the given identifier.
     *
     * @param identifier the identifier of the loot object
     * @return the loot object or an empty optional
     */
    public Optional<LootObject> reward(String identifier) {

        if (Strings.isNullOrEmpty(identifier)) return Optional.empty();

        return Optional.ofNullable(rewards.get(identifier.toLowerCase()));
    }

    /**
     * Loads a loot object from the given config source.
     * <p>It will try to infer an object, type or table based on the properties that are
     * set in the configuration section.
     * <p>A default loot object with the provided config is returned if none match.
     *
     * @param config the config to load a loot object from
     * @return the loaded loot object
     * @throws ConfigurationException if the loot object type is invalid or if the referenced loot object or table does not exist
     */
    public LootObject loadReward(@NonNull ConfigurationSection config) throws ConfigurationException {

        if (config.isSet("type")) {
            if (lootType(config.getString("type")).isEmpty()) {
                throw new ConfigurationException("unknown loot type " + config.getString("type"));
            }
        } else if (config.isSet("object")) {
            Optional<LootObject> object = reward(config.getString("object"));
            if (object.isEmpty()) {
                throw new ConfigurationException("unknown loot object " + config.getString("object"));
            }

            LootObject lootObject = object.get();
            if (lootObject instanceof ConfiguredLootObject) {
                return ((ConfiguredLootObject) lootObject).merge(config);
            }

            return lootObject;
        } else if (config.isSet("table")) {
            Optional<LootTable> table = lootTable(config.getString("table"));
            if (table.isEmpty()) {
                throw new ConfigurationException("unknown loot table " + config.getString("table"));
            }

            LootTable lootTable = table.get();
            if (lootTable instanceof ConfiguredLootTable) {
                return ((ConfiguredLootTable) lootTable).merge(config);
            }

            return lootTable;
        }

        return new ConfiguredLootObject(this, config);
    }

    /**
     * Tries to get a pre-configured rarity by the defined name.
     *
     * @param rarity the name of the rarity.
     *               an empty or null string throws a {@link ConfigurationException}.
     * @return the configured rarity if it was found
     * @throws ConfigurationException if the rarity was not found
     */
    public Rarity rarity(String rarity) throws ConfigurationException {

        if (Strings.isNullOrEmpty(rarity)) {
            throw new ConfigurationException("defined rarity is null or empty");
        }

        if (!rarities.containsKey(rarity.toLowerCase())) {
            throw new ConfigurationException("rarity " + rarity + " not found");
        }

        return rarities.get(rarity.toLowerCase());
    }

    private void loadRarities() {

        ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("rarities");
        if (rarities != null) {
            for (String key : rarities.getKeys(false)) {
                Rarity rarity = new Rarity(rarities.getConfigurationSection(key));
                this.rarities.put(key.toLowerCase(), rarity);
                log.info("loaded rarity " + key + ": " + rarity.toString());
            }
        }
    }

    private void loadRewards() {

        final Map<String, ConfigurationSection> failedLoads = new HashMap<>();
        int count = 0;

        ConfigurationSection lootObjects = plugin.getConfig().getConfigurationSection("rewards");
        if (lootObjects != null) {
            for (String key : lootObjects.getKeys(false)) {
                ConfigurationSection section = lootObjects.getConfigurationSection(key);
                if (section != null) {
                    try {
                        count++;
                        this.rewards.put(key, loadReward(section));
                    } catch (ConfigurationException e) {
                        // queue the loading of the loot object for later
                        failedLoads.put(key, section);
                    }
                }
            }
        }

        try {
            Path path = new File(plugin.getDataFolder(),
                    Objects.requireNonNull(plugin.getConfig().getString("rewards-path", "rewards"))
            ).toPath();

            Files.createDirectories(path);

            List<File> files = Files.find(path, Integer.MAX_VALUE,
                    (file, fileAttr) -> fileAttr.isRegularFile())
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"))
                    .collect(Collectors.toList());

            for (File file : files) {
                String key = ConfigUtil.getFileIdentifier(path, file);
                YamlConfiguration config = new YamlConfiguration();
                try {
                    count++;
                    config.load(file);
                    this.rewards.put(key, loadReward(config));
                } catch (ConfigurationException e) {
                    // queue the loading of the loot object for later
                    failedLoads.put(key, config);
                } catch (InvalidConfigurationException e) {
                    log.severe("invalid reward config " + file.getAbsolutePath() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log.severe("unable to load rewards from configured path: " + e.getMessage());
            e.printStackTrace();
        }

        // try to load all failed attempts to check if their types have been loaded
        for (Map.Entry<String, ConfigurationSection> entry : failedLoads.entrySet()) {
            try {
                this.rewards.put(entry.getKey(), loadReward(entry.getValue()));
            } catch (ConfigurationException e) {
                log.severe("failed to load reward " + entry.getKey() + ": " + e.getMessage());
            }
        }

        log.info("loaded " + this.rewards.size() + "/" + count + " rewards");
    }

    private void loadLootTables() {

        try {
            Path path = new File(plugin.getDataFolder(),
                    Objects.requireNonNull(plugin.getConfig().getString("loot-tables-path", "loot-tables"))
            ).toPath();
            Files.createDirectories(path);

            List<File> files = Files.find(path, Integer.MAX_VALUE,
                    (file, fileAttr) -> fileAttr.isRegularFile())
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"))
                    .collect(Collectors.toList());

            for (File file : files) {
                String key = ConfigUtil.getFileIdentifier(path, file);
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(file);
                    lootTables.put(key, new ConfiguredLootTable(this, config));
                } catch (InvalidConfigurationException e) {
                    log.severe("invalid loot table config " + file.getAbsolutePath() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log.severe("unable to load loot tables from configured path: " + e.getMessage());
            e.printStackTrace();
        }

        int count = lootTables.size();
        // we need to initialize all tables first to allow tables to depend on tables
        Map.copyOf(lootTables).forEach((key, lootTable) -> {
            try {
                lootTable.load();
            } catch (ConfigurationException e) {
                log.severe("failed to load loot table " + key + ": " + e.getMessage());
                lootTables.remove(key);
            }
        });

        log.info("loaded " + lootTables.size() + "/" + count + " loot tables");
    }
}
