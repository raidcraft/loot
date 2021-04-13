package de.raidcraft.loot.config;

import com.google.common.base.Strings;
import de.raidcraft.loot.*;
import de.raidcraft.loot.util.ConfigUtil;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

@Log(topic = "RCLoot")
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class ConfiguredReward extends ConfiguredLootObject implements Reward {

    private ItemStack icon;

    public ConfiguredReward(LootManager lootManager, ConfigurationSection config) {
        super(lootManager, config);
    }

    @Override
    protected LootObject create(LootManager lootManager, ConfigurationSection config) {

        return new ConfiguredReward(lootManager, config);
    }

    @Override
    public Optional<RewardType> type() {

        LootManager lootManager = lootManager();
        Optional<String> guessType = ConfigUtil.guessType(config());
        Optional<RewardType> reward = guessType
                .flatMap(lootManager::lootType)
                .map(rewardType -> {
                    try {
                        return rewardType.load(config());
                    } catch (ConfigurationException e) {
                        log.severe("failed to load reward type " + rewardType + " in reward " + this + ": " + e.getMessage());
                        return null;
                    }
                });

        if (reward.isEmpty()) {
            log.warning("empty or unknown reward type '" + guessType.orElse(null) + "' in: " + this);
        }

        return reward;
    }

    public ConfiguredLootObject type(String type) {

        config().set("type", type);

        return this;
    }

    @Override
    public String name() {

        String name = config().getString("name");
        if (Strings.isNullOrEmpty(name)) return null;

        return rarity().format().replace(Constants.Placeholder.REWARD_NAME, name);
    }

    public ConfiguredLootObject name(String name) {

        config().set("name", name);

        return this;
    }

    @Override
    public List<String> lore() {

        return config().getStringList("lore");
    }

    @Override
    public LootObject lore(String... lore) {

        config().set("lore", lore);

        return this;
    }

    @Override
    public Optional<ItemStack> icon() {

        if (icon != null) return Optional.of(icon);

        type().filter(lootType -> lootType instanceof IconProvider)
                .map(lootType -> (IconProvider) lootType)
                .map(IconProvider::icon)
                .ifPresentOrElse(itemStack -> icon = itemStack, () -> {
                    String icon = config().getString("icon");
                    if (Strings.isNullOrEmpty(icon)) return;

                    Material material = Material.matchMaterial(icon);
                    if (material == null) return;

                    this.icon = new ItemStack(material);
                });

        return Optional.ofNullable(icon);
    }

    @Override
    public LootObject icon(ItemStack icon) {

        this.icon = icon;

        return this;
    }

    public ConfiguredLootObject lore(List<String> lore) {

        config().set("lore", lore);

        return this;
    }
}
