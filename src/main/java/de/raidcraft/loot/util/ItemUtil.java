package de.raidcraft.loot.util;

import de.raidcraft.loot.Constants;
import de.raidcraft.loot.LootObject;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtil {

    public static ItemStack toDisplayItem(@NonNull LootObject lootObject) {

        ItemStack item = lootObject.icon().orElse(new ItemStack(Constants.DEFAULT_ICON));
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(lootObject.name());
        meta.setLore(lootObject.lore());
        item.setItemMeta(meta);

        return item;
    }

    private ItemUtil() {

    }
}
