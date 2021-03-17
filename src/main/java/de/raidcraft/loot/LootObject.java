package de.raidcraft.loot;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A loot object is the high level part of the loot system representing
 * an object that is included in the loot result.
 * <p>{@link LootTable}s are loot objects themselves to allow recursive reward configurations.
 * <p>Every loot object must also define its {@link #type()} which controls how the object is looted.
 */
public interface LootObject {

    /**
     * The loot object type defines the underlying implementation of the loot object.
     * <p>An example could be an item or command.
     *
     * @return the type of this loot object
     */
    Optional<LootType> type();

    /**
     * The name of the loot object is formatted with the rarity if present.
     *
     * @return the name to display to the consumer of the object.
     *         may be null if not name is set.
     */
    String name();

    /**
     * @return a list of lore lines of this loot object that can be displayed in a gui.
     *         each element represents a line to display in the lore.
     */
    List<String> lore();

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
     * Adds this loot object to the given player if a type is present.
     *
     * @param player the player to add this loot object to
     * @see LootType#addTo(Player)
     */
    default void addTo(Player player) {

        type().ifPresent(lootType -> lootType.addTo(player));
    }

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
    default void onPostResultEvaluation(Collection<LootObject> result) {}
}
