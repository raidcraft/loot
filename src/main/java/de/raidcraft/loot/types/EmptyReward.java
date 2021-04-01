package de.raidcraft.loot.types;

import de.raidcraft.loot.Constants;
import de.raidcraft.loot.RewardInfo;
import de.raidcraft.loot.RewardType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@RewardInfo(Constants.DEFAULT_TYPE)
public class EmptyReward implements RewardType {

    @Override
    public EmptyReward load(ConfigurationSection config) {

        return this;
    }

    @Override
    public void addTo(Player target) {

    }
}
