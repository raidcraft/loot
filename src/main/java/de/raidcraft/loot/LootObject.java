package de.raidcraft.loot;

import de.raidcraft.loot.config.Rarity;

import java.util.Collection;

/**
 * A loot object is the high level part of the loot system representing
 * an object that is included in the loot result.
 * <p>{@link LootTable}s are loot objects themselves to allow recursive reward configurations.
 */
public interface LootObject {

    /**
     * The chance of the loot object is a relative weight compared to all other
     * chances and not a direct chance, like in a dice roll.
     * <p>The chance from the rarity may be used if not explicit loot object chance is defined.
     * <p>For example a loot object with a chance of 2.0 compared to a loot object with a chance
     * of 1.0 can occur twice as often as the loot object with a chance of 1.0.
     *
     * @return the relative chance of the loot object
     */
    double chance();

    /**
     * @return true if the loot object is enabled and can be included in the result
     */
    boolean enabled();

    /**
     * Loot objects that are always included in the result ignore the general count
     * of the result set.
     * <p>If the result set has a count of 3 and there are 5 always loot objects,
     * the result will have a size of 5 and not other loot objects are included.
     *
     * @return true if the loot object should always be included in the result
     *         no matter the chance or count of the result set.
     */
    boolean always();

    /**
     * Unique loot objects are only included once in the complete result set.
     *
     * @return true if the loot object is unique and only included once
     */
    boolean unique();

    /**
     * Loot objects can be excluded from the random chance and only obtained via always drops.
     * <p>Objects that are excluded from the random drop will not increase the required chance for other objects.
     *
     * @return true if this loot object can only be obtained from the always drop
     */
    boolean excludeFromRandom();

    /**
     * Occurs before all the probabilities of all loot objects of the current result calculation are summed up together.
     * This is the moment to modify any settings immediately before a result is calculated.
     */
    default void onPreResultEvaluation() {}

    /**
     * Occurs when this LootObject has been hit by the result procedure.
     * (This means, this object will be part of the result set).
     */
    default void onHit() {}

    /**
     * Occurs after the result has been calculated and the result set is complete, but before
     * the result method exits.
     *
     * @param result the final result of the looting procedure
     */
    default void onPostResultEvaluation(Collection<Reward> result) {}

    /**
     * Merges this loot objects config with the config of the given loot object.
     * <p>
     * Any values set in this loot object will take precedence over the given loot object.
     *
     * @param lootObject the loot object to merge into this loot object
     * @return a new merged instance of both loot objects
     */
    LootObject merge(LootObject lootObject);

    /**
     * The rarity of the loot object contains additional display information
     * and the fallback chance of the object.
     *
     * @return the rarity of this loot object
     */
    Rarity rarity();
}
