package de.raidcraft.loot.types;

import com.google.common.base.Strings;
import de.raidcraft.loot.ConfigurationException;
import de.raidcraft.loot.IconProvider;
import de.raidcraft.loot.LootObject;
import de.raidcraft.loot.LootType;
import de.raidcraft.loot.annotations.LootInfo;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Accessors(fluent = true)
@LootInfo("item")
public class ItemLoot implements LootType, IconProvider {

    private Material item;
    private int amount = 1;

    @Override
    public ItemLoot load(ConfigurationSection config) {

        String item = config.getString("item");
        if (Strings.isNullOrEmpty(item)) {
            throw new ConfigurationException("item must not be null or empty");
        }

        this.item = Material.matchMaterial(item);
        this.amount = config.getInt("amount", amount);

        if (this.item == null) {
            throw new ConfigurationException("unknown item: " + item);
        }

        return this;
    }

    @Override
    public void addTo(Player target) {

        target.getInventory().addItem(new ItemStack(item, amount));
    }

    @Override
    public ItemStack icon() {

        return new ItemStack(item, amount);
    }
}
