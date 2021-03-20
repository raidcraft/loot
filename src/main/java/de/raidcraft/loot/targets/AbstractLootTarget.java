package de.raidcraft.loot.targets;

import de.raidcraft.loot.LootObject;
import de.raidcraft.loot.LootTarget;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Accessors(fluent = true)
public abstract class AbstractLootTarget<TType> implements LootTarget<TType> {

    private final Collection<LootObject> lootObjects = new ArrayList<>();

    @Override
    public void add(Collection<LootObject> lootObjects) {

        this.lootObjects.addAll(lootObjects);
    }

    public Collection<LootObject> lootObjects() {
        return this.lootObjects;
    }
}
