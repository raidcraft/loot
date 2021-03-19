package de.raidcraft.loot;

import de.raidcraft.loot.annotations.RewardInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * The loot type is what actually gets looted by the player.
 * <p>It can be an item, money or even a command that is executed.
 * <p>Every loot type must be annotated with {@link RewardInfo}
 * and provide a unique identifier to the type.
 */
public interface RewardType {

    /**
     * The load method is called once after creating a new instance
     * of the type.
     * <p>The method may throw a {@link ConfigurationException} to signal
     * that the configuration of the type failed.
     *
     * @param config the config this type is loaded with
     * @return this loot type
     * @throws ConfigurationException if a configuration error occurred
     */
    RewardType load(ConfigurationSection config) throws ConfigurationException;

    /**
     * The addTo method is called when this loot type is rewarded to the player.
     * <p>This is the place were items are added, commands executed and so on.
     * <p>The method may be called within the same loot type instance for different
     * targets. Make sure to create copies of the result type if needed.
     *
     * @param target the target this type should be added to
     */
    void addTo(Player target);

    @Value
    @Accessors(fluent = true)
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    class Registration<TType extends RewardType> {

        String identifier;
        Class<TType> typeClass;
        Supplier<TType> supplier;
    }
}
