package de.raidcraft.loot.targets;

import org.bukkit.entity.Player;

public class PlayerTarget extends AbstractLootTarget<Player> {

    @Override
    public void addTo(Player target) {

        rewards().forEach(lootObject -> lootObject.addTo(target));
    }
}
