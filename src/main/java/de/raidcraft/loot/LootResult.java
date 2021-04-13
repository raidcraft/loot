package de.raidcraft.loot;

import de.raidcraft.loot.util.ItemUtil;
import lombok.*;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Value
@Accessors(fluent = true)
public class LootResult {

    @NonNull
    Lootable source;
    @Nullable
    Player player;
    @Singular
    @With
    @NonNull
    Collection<Reward> rewards;

    /**
     * Creates a new loot result for the given loot source.
     * <p>Use the builder to add additional rewards and a player to the result.
     *
     * @param source the source to create a loot result from
     * @return the builder to create the loot result
     */
    public static LootResult.LootResultBuilder builder(Lootable source) {

        return new LootResultBuilder().source(source).rewards(new ArrayList<>());
    }

    /**
     * Tries to create an item stack display item for every
     * reward in this loot result.
     *
     * @return a list of item stacks that represent the loot result
     */
    public ItemStack[] asDisplayItems() {

        return rewards().stream()
                .map(ItemUtil::toDisplayItem)
                .toArray(ItemStack[]::new);
    }

    /**
     * Adds all rewards in this loot result to the given player.
     * <p>This does not have to be the same player that created (looted)
     * the loot result, but all requirement checks during the loot processes
     * where done using the given player at that time.
     * This simply adds the rewards to the player and performs no further checks.
     *
     * @param player the player to add this result to
     */
    public void addTo(Player player) {

        rewards().forEach(reward -> reward.addTo(player));
    }

    /**
     * Adds the rewards in this result the player that created
     * the result and is attached to it.
     * <p>Silently fails and does nothing if the {@link #player()} is null.
     */
    public void addToPlayer() {

        if (player() != null) {
            addTo(player());
        }
    }
}
