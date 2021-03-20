package de.raidcraft.loot.types;

import de.raidcraft.loot.Constants;
import de.raidcraft.loot.LootObject;
import de.raidcraft.loot.LootType;
import de.raidcraft.loot.annotations.LootInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@LootInfo(Constants.DEFAULT_TYPE)
public class EmptyLoot implements LootType {

    @Override
    public EmptyLoot load(ConfigurationSection config) {

        return this;
    }

    @Override
    public void addTo(Player target) {

    }
}
