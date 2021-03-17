package de.raidcraft.loot;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface LootTable extends LootObject {

    /**
     * The maximum number of entries expected in the Result. The final count of items in the result may be lower
     * if some of the entries may return an empty result (no drop).
     *
     * @return maximum number of entries expected in the result
     */
    int count();

    /**
     * Gets the content of the table that will be evaluated when querying for the result.
     * <p>The content can also be other table objects and they may contain more tables.
     * Can recurse indefinitely.
     *
     * @return contents of the table
     */
    Collection<LootObject> contents();

    /**
     * Gets the result. Calling this method will start the random pick process and generate the result.
     * <p>This result remains constant for the lifetime of this table object.
     * Use the {@link #loot()} method to clear the result and create a new one.
     *
     * @return calculated random result of this table
     */
    Collection<LootObject> result();

    /**
     * Will reset the last cached result and
     * generate a new result by calling {@link #result()}.
     *
     * @return fresh random result
     */
    Collection<LootObject> loot();

    /**
     * Will reset the cache and loot the object in a player context.
     * <p>This means that requirements get evaluated. Otherwise requirements will all be true.
     *
     * @param player that is looting
     * @return random loot with evaluated requirements
     */
    Collection<LootObject> loot(Player player);
}
