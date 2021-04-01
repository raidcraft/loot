package de.raidcraft.loot.types;

import com.google.common.base.Strings;
import de.raidcraft.loot.ConfigurationException;
import de.raidcraft.loot.Constants;
import de.raidcraft.loot.RewardInfo;
import de.raidcraft.loot.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@RewardInfo("command")
public class CommandReward implements RewardType {

    private String command;
    private boolean op = false;

    @Override
    public CommandReward load(ConfigurationSection config) throws ConfigurationException {

        command = config.getString("command");
        op = config.getBoolean("op", op);

        if (Strings.isNullOrEmpty(command)) {
            throw new ConfigurationException("command must not be null or empty");
        }

        return this;
    }

    @Override
    public void addTo(Player target) {

        command = command.replace(Constants.Placeholder.PLAYER_NAME, target.getName());

        if (op) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            target.performCommand(command);
        }
    }
}
