package de.raidcraft.loot;

import com.google.common.base.Strings;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Tries to guess the {@link RewardType} from the provided config options in the given config section.
 * <p>RewardTypeResolvers must be added to the {@link #REWARD_TYPE_RESOLVERS} list
 * and are evaluated in the order they are registered.
 * <p>All built-in registered resolvers are already added to the list.
 */
@FunctionalInterface
public interface RewardTypeResolver {

    List<RewardTypeResolver> REWARD_TYPE_RESOLVERS = Arrays.asList(
            config -> config.isSet("item") ? Constants.Types.ITEM : null,
            config -> config.isSet("command") ? Constants.Types.COMMAND : null
    );

    /**
     * Guesses the reward type of the given config section and returns the type identifier of the reward.
     * <p>This method looks for keys inside the config and then infers the type.
     * e.g.: a {@code item:} or {@code command:} key lets us assume the relevant type.
     * <p>Register your own {@link RewardTypeResolver} by adding them to the {@link #REWARD_TYPE_RESOLVERS} list.
     *
     * @param config the config to use for the type lookup. may be null.
     * @return the type of it could be guessed or an empty optional otherwise
     */
    static Optional<String> resolveType(ConfigurationSection config) {

        if (config == null) return Optional.empty();
        if (config.isSet("type")) {
            return Optional.ofNullable(config.getString("type"));
        }

        for (RewardTypeResolver resolver : REWARD_TYPE_RESOLVERS) {
            String result = resolver.resolve(config);
            if (!Strings.isNullOrEmpty(result)) {
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    /**
     * Resolves the reward type by analyzing the config options in the provided config.
     * <p>Return null if the provided config does not match your type.
     * <p><pre>{@code
     * // the following type resolver resolves all configs with the money option into the money type
     * ConfigUtil.REWARD_TYPE_RESOLVERS.add(config -> config.isSet("money") ? "money" : null);
     *
     * rewards:
     *   # this is an example config using the just registered resolver
     *   - money: 2000
     *   # without the resolver the reward must be defined like this
     *   - type: money
     *     money: 2000
     * }</pre>
     *
     * @param config the config to analyze. is never null.
     * @return the type identifier or null if the type does not match this resolver.
     */
    String resolve(@NonNull ConfigurationSection config);
}
