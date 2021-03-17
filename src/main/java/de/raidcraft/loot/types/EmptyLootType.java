package de.raidcraft.loot.types;

import de.raidcraft.loot.Constants;
import de.raidcraft.loot.LootType;
import de.raidcraft.loot.annotations.LootTypeInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@LootTypeInfo(Constants.DEFAULT_TYPE)
public class EmptyLootType implements LootType {

    @Override
    public EmptyLootType load(ConfigurationSection config) {

        return this;
    }

    @Override
    public void addTo(Player target) {

    }
}
