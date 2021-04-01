package de.raidcraft.loot.targets;

import de.raidcraft.loot.LootTarget;
import de.raidcraft.loot.Reward;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Accessors(fluent = true)
public abstract class AbstractLootTarget<TType> implements LootTarget<TType> {

    private final Collection<Reward> rewards = new ArrayList<>();

    @Override
    public void add(Collection<Reward> lootObjects) {

        this.rewards.addAll(lootObjects);
    }

    public Collection<Reward> rewards() {
        return this.rewards;
    }
}
