package de.raidcraft.loot;

import com.google.common.base.Strings;
import de.raidcraft.loot.annotations.LootTypeInfo;
import de.raidcraft.loot.config.ConfiguredLootObject;
import de.raidcraft.loot.config.Rarity;
import de.raidcraft.loot.types.EmptyLootType;
import de.raidcraft.loot.types.ItemLootType;
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
    private final Map<String, LootType.Registration<?>> lootTypes = new HashMap<>();
    private final Map<String, LootObject> lootObjects = new HashMap<>();

    LootManager(RCLoot plugin) {
        this.plugin = plugin;

        register(EmptyLootType.class, EmptyLootType::new);
        register(ItemLootType.class, ItemLootType::new);
    }

    void load() {

        loadRarities();
        loadLootObjects();
    }

    /**
     * Registers a new loot type for the given class.
     * <p>Make sure that the loot type is annotated with {@link LootTypeInfo} and has a unique
     * identifier. A {@link TypeRegistrationException} will be thrown instead.
     *
     * @param typeClass the class of the loot type that is registered
     * @param supplier the supplier that knows how to create an instance of the loot type
     * @param <TType> the type of the loot type class
     * @return this loot manager
     */
    public <TType extends LootType> LootManager register(@NonNull Class<TType> typeClass, @NonNull Supplier<TType> supplier) {

        if (!typeClass.isAnnotationPresent(LootTypeInfo.class)) {
            throw new TypeRegistrationException("loot type " + typeClass.getCanonicalName()
                    + " is missing the required @LootTypeInfo annotation");
        }

        String identifier = typeClass.getAnnotation(LootTypeInfo.class).value().toLowerCase();
        if (lootTypes.containsKey(identifier)) {
            log.severe("unable to register loot type " + typeClass.getCanonicalName()
                    + "! A type with the same identifier " + identifier + " is already registered: "
                    + lootTypes.get(identifier).typeClass().getCanonicalName()
            );
            return this;
        }

        LootType.Registration<?> registration = new LootType.Registration<>(identifier, typeClass, supplier);
        lootTypes.put(identifier, registration);
        log.info("registered loot type " + identifier + ": " + typeClass.getCanonicalName());

        return this;
    }

    /**
     * Tries to find a loot type for the given type identifier
     * and creates a new instance of the given type.
     *
     * @param type the identifier of the loot type, e.g. none or item.
     *             can be null or empty, but will return an empty optional.
     * @return a new loot type instance if the type was found
     */
    public Optional<LootType> lootType(String type) {

        if (Strings.isNullOrEmpty(type)) return Optional.empty();

        return Optional.ofNullable(lootTypes.get(type.toLowerCase()))
                .map(LootType.Registration::supplier)
                .map(Supplier::get);
    }

    /**
     * Tries to find a loot object with the given identifier.
     *
     * @param identifier the identifier of the loot object
     * @return the loot object or an empty optional
     */
    public Optional<LootObject> lootObject(String identifier) {

        if (Strings.isNullOrEmpty(identifier)) return Optional.empty();

        return Optional.ofNullable(lootObjects.get(identifier.toLowerCase()));
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

    private void loadLootObjects() {

        final Map<String, ConfigurationSection> failedLoads = new HashMap<>();
        int count = 0;

        ConfigurationSection lootObjects = plugin.getConfig().getConfigurationSection("loot-objects");
        if (lootObjects != null) {
            for (String key : lootObjects.getKeys(false)) {
                ConfigurationSection section = lootObjects.getConfigurationSection(key);
                if (section != null) {
                    try {
                        count++;
                        this.lootObjects.put(key, loadLootObject(section));
                    } catch (ConfigurationException e) {
                        // queue the loading of the loot object for later
                        failedLoads.put(key, section);
                    }
                }
            }
        }

        try {
            Path path = new File(plugin.getDataFolder(),
                    Objects.requireNonNull(plugin.getConfig().getString("loot-object-path", "loot-objects"))
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
                    this.lootObjects.put(key, loadLootObject(config));
                } catch (ConfigurationException e) {
                    // queue the loading of the loot object for later
                    failedLoads.put(key, config);
                } catch (InvalidConfigurationException e) {
                    log.severe("invalid loot object config " + file.getAbsolutePath() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log.severe("unable to load loot objects from configured path: " + e.getMessage());
            e.printStackTrace();
        }

        // try to load all failed attempts to check if their types have been loaded
        for (Map.Entry<String, ConfigurationSection> entry : failedLoads.entrySet()) {
            try {
                this.lootObjects.put(entry.getKey(), loadLootObject(entry.getValue()));
            } catch (ConfigurationException e) {
                log.severe("failed to load loot object " + entry.getKey() + ": " + e.getMessage());
            }
        }

        log.info("loaded " + this.lootObjects.size() + "/" + count + " loot objects");
    }

    private LootObject loadLootObject(@NonNull ConfigurationSection config) throws ConfigurationException {

        if (config.isSet("type")) {
            if (lootType(config.getString("type")).isEmpty()) {
                throw new ConfigurationException("unknown loot type " + config.getString("type"));
            }
        } else if (config.isSet("object")) {
            Optional<LootObject> object = lootObject(config.getString("object"));
            if (object.isEmpty()) {
                throw new ConfigurationException("unknown loot object " + config.getString("object"));
            }

            LootObject lootObject = object.get();
            if (lootObject instanceof ConfiguredLootObject) {
                return ((ConfiguredLootObject) lootObject).merge(config);
            }

            return lootObject;
        } else if (config.isSet("table")) {

        }

        return new ConfiguredLootObject(this, config);
    }
}
