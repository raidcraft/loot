package de.raidcraft.loot;

import java.util.Arrays;
import java.util.Collection;

/**
 * The loot target
 */
public interface LootTarget<TType> {

    Collection<Reward> rewards();

    default void add(Reward... lootObjects) {
        add(Arrays.asList(lootObjects));
    }

    void add(Collection<Reward> lootObjects);

    void addTo(TType target);
}
