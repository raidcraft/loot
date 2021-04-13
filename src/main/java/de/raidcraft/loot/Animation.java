package de.raidcraft.loot;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

public interface Animation {

    /**
     * @return the loot result that triggered the animation
     */
    LootResult result();

    /**
     * @return the total duration of the animation in ticks
     */
    long duration();

    /**
     * @return the current tick of the animation starts at 0 and ends at the duration
     */
    long currentTick();

    /**
     * @return the remaining time of the animation in ticks
     */
    default long remainingDuration() {

        return duration() - currentTick();
    }

    void load(@NonNull ConfigurationSection config) throws ConfigurationException;

    void start();

    void tick();

    void end();

    void abort();
}
