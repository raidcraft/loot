package de.raidcraft.loot;

import java.util.Collection;

public interface LootTable extends LootObject, Lootable {

    /**
     * Loads the loot table and all of its rewards.
     * <p>Make sure that all dependant tables and loot objects are loaded when loading the loot table.
     *
     * @throws ConfigurationException if the loot table references invalid loot objects or missing tables
     */
    void load() throws ConfigurationException;

    /**
     * The maximum number of entries expected in the Result.
     * <p>The final count of items in the result may be lower
     * if some of the entries may return an empty result (no drop).
     * <p>The result set may also be higher if there are more {@link #always()}
     * loot objects than the count.
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
}
