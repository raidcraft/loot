package de.raidcraft.loot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface Reward extends LootObject {
    /**
     * The loot object type defines the underlying implementation of the loot object.
     * <p>An example could be an item or command.
     *
     * @return the type of this loot object
     */
    Optional<RewardType> type();

    /**
     * The name of the loot object is formatted with the rarity if present.
     *
     * @return the name to display to the consumer of the object.
     * may be null if not name is set.
     */
    String name();

    /**
     * Sets the name of the loot object.
     *
     * @param name the name to set
     * @return this loot object
     */
    LootObject name(String name);

    /**
     * @return a list of lore lines of this loot object that can be displayed in a gui.
     * each element represents a line to display in the lore.
     */
    List<String> lore();

    /**
     * Sets the lore of the loot object.
     *
     * @param lore the lore to set
     * @return this loot object
     */
    LootObject lore(String... lore);

    /**
     * @return an optional item stack to use as the icon for this loot object
     */
    Optional<ItemStack> icon();

    /**
     * Sets the icon of the loot object.
     *
     * @param icon the icon to set
     * @return this loot object
     */
    LootObject icon(ItemStack icon);

    /**
     * Adds this loot object to the given player if a type is present.
     *
     * @param player the player to add this loot object to
     * @see RewardType#addTo(Player)
     */
    default void addTo(Player player) {

        type().ifPresent(reward -> reward.addTo(player));
    }
}
