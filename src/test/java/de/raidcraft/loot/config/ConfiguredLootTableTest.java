package de.raidcraft.loot.config;

import de.raidcraft.loot.Reward;
import de.raidcraft.loot.RewardType;
import de.raidcraft.loot.TestBase;
import de.raidcraft.loot.types.EmptyReward;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ConfiguredLootTableTest extends TestBase {

    @Nested
    @DisplayName("load()")
    class Load {

        @Test
        @DisplayName("should load table with inline loot objects")
        void shouldLoadInlineLootObjects() {

            MemoryConfiguration configuration = new MemoryConfiguration();
            MemoryConfiguration reward1 = new MemoryConfiguration();
            reward1.set("type", "command");
            reward1.set("chance", 20);
            reward1.set("command", "foobar");
            MemoryConfiguration reward2 = new MemoryConfiguration();
            reward2.set("type", "none");
            configuration.set("rewards", Arrays.asList(reward1, reward2));

            ConfiguredLootTable lootTable = new ConfiguredLootTable(lootManager(), configuration);

            assertThatCode(lootTable::load)
                    .doesNotThrowAnyException();

            assertThat(lootTable.contents())
                    .hasSize(2)
                    .anyMatch(lootObject -> lootObject.chance() == 20d);
        }

        @Test
        @DisplayName("should load table with predefined loot objects")
        void shouldLoadTableWithPredefinedLootObjects() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("type", "none");
            lootManager().register("foo", lootManager().createLootObject(cfg));

            MemoryConfiguration tableCfg = new MemoryConfiguration();
            MemoryConfiguration reward1 = new MemoryConfiguration();
            reward1.set("reward", "foo");
            tableCfg.set("rewards", Collections.singletonList(reward1));

            ConfiguredLootTable lootTable = new ConfiguredLootTable(lootManager(), tableCfg);

            assertThatCode(lootTable::load)
                    .doesNotThrowAnyException();

            Optional<RewardType> type = ((Reward) lootTable.contents().get(0)).type();
            assertThat(type)
                    .isNotEmpty()
                    .get()
                    .isInstanceOf(EmptyReward.class);
        }
    }
}