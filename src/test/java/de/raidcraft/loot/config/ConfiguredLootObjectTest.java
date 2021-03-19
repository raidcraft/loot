package de.raidcraft.loot.config;

import de.raidcraft.loot.TestBase;
import de.raidcraft.loot.types.ItemReward;
import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

class ConfiguredLootObjectTest extends TestBase {

    @Nested
    @DisplayName("type()")
    class Type {

        @Test
        @DisplayName("should set type config when loading type")
        void shouldSetTypeConfigWhenLoadingType() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("name", "Test");
            cfg.set("type", "item");
            cfg.set("with.item", "wooden_sword");

            ConfiguredLootObject reward = new ConfiguredLootObject(lootManager(), cfg);

            assertThat(reward.reward())
                    .isPresent()
                    .get()
                    .isInstanceOf(ItemReward.class)
                    .asInstanceOf(type(ItemReward.class))
                    .extracting(ItemReward::item, ItemReward::amount)
                    .contains(Material.WOODEN_SWORD, 1);
        }

        @Test
        @DisplayName("should return an empty optional if configuration is invalid")
        void shouldReturnEmptyOptionalIfConfigurationIsInvalid() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("type", "item");
            cfg.set("with.item", "foobar");

            ConfiguredLootObject reward = new ConfiguredLootObject(lootManager(), cfg);

            assertThat(reward.reward())
                    .isEmpty();
        }
    }
}