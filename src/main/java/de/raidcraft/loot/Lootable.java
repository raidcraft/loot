package de.raidcraft.loot;

import org.bukkit.entity.Player;

public interface Lootable {

    /**
     * Gets the last cached rewards for the given player.
     * <p>This result remains constant for the lifetime of this table object.
     * Use the {@link #loot()} method to clear the result set and create a new one.
     * <p>Will generate a new cached result if none exists yet.
     *
     * @return the last cached result of the loot table
     */
    LootResult rewards(Player player);

    /**
     * Will reset the last cached result and generate a new result set.
     *
     * @return fresh random result
     */
    default LootResult loot() {

        return loot(null);
    }

    /**
     * Will reset the cache and loot the object in a player context.
     * <p>This means that requirements get evaluated. Otherwise requirements will all be true.
     *
     * @param player that is looting
     * @return random loot with evaluated requirements
     */
    LootResult loot(Player player);
}
