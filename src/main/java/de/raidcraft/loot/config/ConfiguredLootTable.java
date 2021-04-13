package de.raidcraft.loot.config;

import de.raidcraft.loot.*;
import de.raidcraft.loot.util.RandomUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Log(topic = "RCLoot")
@RewardInfo("table")
@Accessors(fluent = true)
public class ConfiguredLootTable extends ConfiguredLootObject implements LootTable {

    @Getter
    private final List<LootObject> contents = new ArrayList<>();

    private final Set<LootObject> uniqueDrops = new HashSet<>();
    private final Map<Rarity, Integer> rarityCounts = new HashMap<>();
    private LootResult cachedResult;

    public ConfiguredLootTable(LootManager lootManager, ConfigurationSection config) {

        super(lootManager, config);
    }

    @Override
    protected LootObject create(LootManager lootManager, ConfigurationSection config) {

        return new ConfiguredLootTable(lootManager, config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws ConfigurationException {

        List<ConfigurationSection> rewards = (List<ConfigurationSection>) config().getList("rewards", new ArrayList<ConfigurationSection>());
        if (rewards != null) {
            for (ConfigurationSection reward : rewards) {
                contents.add(lootManager().createLootObject(reward));
            }
        }
    }

    @Override
    public int count() {

        return config().getInt("count", 1);
    }

    @Override
    public LootResult loot(Player player) {

        this.cachedResult = null;

        return rewards(player);
    }

    @Override
    public LootResult rewards(Player player) {

        if (cachedResult != null) return cachedResult;

        // If a player is set we need to check the requirements of this table
        // otherwise the table will always be active
        // TODO: implement requirements
//        if (!getLootingPlayer().map(this::isMeetingAllRequirements).orElse(true)) {
//            return new ArrayList<>();
//        }

        // The return value, a list of hit objects
        final List<Reward> result = new ArrayList<>();
        rarityCounts.clear();
        uniqueDrops.clear();

        // Do the PreEvaluation on all objects contained in the current table
        // This is the moment where those objects might disable themselves.
        contents().forEach(LootObject::onPreResultEvaluation);

        // Add all the objects that are hit "Always" to the result
        // Those objects are really added always, no matter what "Count"
        // is set in the table! If there are 5 objects "always", those 5 will
        // drop, even if the count says only 3.
        contents().stream()
                .filter(LootObject::enabled)
                .filter(LootObject::always)
                // TODO: implement requirements
//                .filter(object -> getLootingPlayer().map(object::isMeetingAllRequirements).orElse(true))
                .forEach(entry -> addToResult(result, entry));

        long realDropCount = count();

        // Continue only, if there is a Count left to be processed
        if (realDropCount > 0)
        {
            for (int dropCount = 0; dropCount < realDropCount; dropCount++)
            {
                // Find the objects, that can be hit now
                // This is all objects, that are Enabled
                Collection<LootObject> droppable = contents().stream()
                        .filter(object -> !object.excludeFromRandom())
                        .filter(LootObject::enabled)
                        .filter(this::hasNotReachedRarityCount)
                        // TODO: implement requirements
//                        .filter(object -> getLootingPlayer().map(object::isMeetingAllRequirements).orElse(true))
                        .collect(Collectors.toList());

                // This is the magic random number that will decide, which object is hit now
                double hitValue = RandomUtil.getDoubleValue(droppable.stream().mapToDouble(LootObject::chance).sum());

                // Find out in a loop which object's probability hits the random value...
                double runningValue = 0;
                for (LootObject object : droppable)
                {
                    // Count up until we find the first item that exceeds the hit-value...
                    runningValue += object.chance();
                    if (hitValue < runningValue)
                    {
                        // ...and the oscar goes too...
                        addToResult(result, object);
                        break;
                    }
                }
            }
        }

        // Now give all objects in the result set the chance to interact with
        // the other objects in the result set.
        for (Reward object : result) {
            object.onPostResultEvaluation(result);
        }

        // cache and return the result
        this.cachedResult = LootResult.builder(this)
                .player(player)
                .rewards(result)
                .build();

        return cachedResult;
    }

    private void addToResult(Collection<Reward> result, LootObject object) {

        if (!object.unique() || !uniqueDrops.contains(object))
        {
            if (object.unique()) {
                uniqueDrops.add(object);
            }

            rarityCounts.merge(object.rarity(), 1, Integer::sum);

            if (object instanceof LootTable) {
                // recursively go through all loot tables and add their results
                for (Reward reward : ((LootTable) object).loot().rewards()) {
                    addToResult(result, reward);
                }
            } else if (object instanceof Reward) {
                result.add((Reward) object);
                object.onHit();
            }
        }
    }

    private boolean hasNotReachedRarityCount(LootObject lootObject) {

        Rarity rarity = lootObject.rarity();
        int count = rarityCounts.getOrDefault(rarity, 0);

        if (rarity.max() < 0) return true;

        return count < rarity.max();
    }
}
