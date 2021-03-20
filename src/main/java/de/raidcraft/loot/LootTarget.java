package de.raidcraft.loot;

import java.util.Arrays;
import java.util.Collection;

/**
 * The loot target
 */
public interface LootTarget<TType> {

    Collection<LootObject> lootObjects();

    default void add(LootObject... lootObjects) {
        add(Arrays.asList(lootObjects));
    }

    void add(Collection<LootObject> lootObjects);

    void addTo(TType target);
}
