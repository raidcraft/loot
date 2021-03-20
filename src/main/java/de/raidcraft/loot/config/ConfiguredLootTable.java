package de.raidcraft.loot.config;

import de.raidcraft.loot.ConfigurationException;
import de.raidcraft.loot.LootManager;
import de.raidcraft.loot.LootObject;
import de.raidcraft.loot.LootTable;
import de.raidcraft.loot.annotations.LootInfo;
import de.raidcraft.loot.util.RandomUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Log(topic = "RCLoot")
@LootInfo("table")
@Accessors(fluent = true)
public class ConfiguredLootTable extends ConfiguredLootObject implements LootTable {

    @Getter
    private final Collection<LootObject> contents = new ArrayList<>();

    private Collection<LootObject> uniqueDrops = new HashSet<>();
    private Collection<LootObject> cachedResult = new ArrayList<>();

    public ConfiguredLootTable(LootManager lootManager, ConfigurationSection config) {

        super(lootManager, config);
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
    public Collection<LootObject> loot(Player player) {

        this.cachedResult = null;

        return result(player);
    }

    @Override
    public Collection<LootObject> result() {

        return result(null);
    }

    public Collection<LootObject> result(Player player) {

        if (cachedResult != null) return cachedResult;

        // If a player is set we need to check the requirements of this table
        // otherwise the table will always be active
        // TODO: implement requirements
//        if (!getLootingPlayer().map(this::isMeetingAllRequirements).orElse(true)) {
//            return new ArrayList<>();
//        }

        // The return value, a list of hit objects
        List<LootObject> result = new ArrayList<>();
        uniqueDrops = new HashSet<>();

        // Do the PreEvaluation on all objects contained in the current table
        // This is the moment where those objects might disable themselves.
        contents().forEach(LootObject::onPreResultEvaluation);

        // Add all the objects that are hit "Always" to the result
        // Those objects are really added always, no matter what "Count"
        // is set in the table! If there are 5 objects "always", those 5 will
        // drop, even if the count says only 3.
        contents().stream()
                .filter(entry -> entry.always() && entry.enabled())
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
                Collection<LootObject> dropables = contents().stream()
//                        .filter(object -> !object.isExcludeFromRandom())
                        .filter(LootObject::enabled)
                        // TODO: implement requirements
//                        .filter(object -> getLootingPlayer().map(object::isMeetingAllRequirements).orElse(true))
                        .collect(Collectors.toList());

                // This is the magic random number that will decide, which object is hit now
                double hitValue = RandomUtil.getDoubleValue(dropables.stream().mapToDouble(LootObject::chance).sum());

                // Find out in a loop which object's probability hits the random value...
                double runningValue = 0;
                for (LootObject object : dropables)
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
        for (LootObject object : result) {
            object.onPostResultEvaluation(result);
        }

        // Return the set now
        this.cachedResult = List.copyOf(result);

        return cachedResult;
    }

    private void addToResult(Collection<LootObject> result, LootObject object) {

        if (!object.unique() || !uniqueDrops.contains(object))
        {
            if (object.unique()) {
                uniqueDrops.add(object);
            }

            if (object instanceof LootTable) {
                // recursively go through all loot tables and add their results
                result.addAll(((LootTable) object).loot());
            } else {
                // INSTANCECHECK
                // Check if the object to add implements IRDSObjectCreator.
                // If it does, call the CreateInstance() method and add its return value
                // to the result set. If it does not, add the object o directly.
//                LootObject adder = object;
//                if (object instanceof RDSObjectCreator)
//                    adder = ((RDSObjectCreator)object).createInstance();

                result.add(object);
                object.onHit();
            }
        }
    }
}
