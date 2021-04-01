package de.raidcraft.loot.util;

import de.raidcraft.loot.Constants;
import de.raidcraft.loot.Reward;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtil {

    public static ItemStack toDisplayItem(@NonNull Reward reward) {

        ItemStack item = reward.icon().orElse(new ItemStack(Constants.DEFAULT_ICON));
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(reward.name());
        meta.setLore(reward.lore());
        item.setItemMeta(meta);

        return item;
    }

    private ItemUtil() {

    }
}
