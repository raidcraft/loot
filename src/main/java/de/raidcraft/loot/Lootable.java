package de.raidcraft.loot;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface Lootable {
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
    default Collection<LootObject> loot() {

        return loot(null);
    }

    /**
     * Will reset the cache and loot the object in a player context.
     * <p>This means that requirements get evaluated. Otherwise requirements will all be true.
     *
     * @param player that is looting
     * @return random loot with evaluated requirements
     */
    Collection<LootObject> loot(Player player);
}
