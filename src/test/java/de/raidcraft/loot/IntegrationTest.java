package de.raidcraft.loot;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class IntegrationTest extends TestBase {

    @Nested
    @DisplayName("AdminCommands")
    class Commands {

        private PlayerMock player;

        @BeforeEach
        void setUp() {
            player = server().addPlayer();
            player.setOp(true);

            MemoryConfiguration reward1 = new MemoryConfiguration();
            reward1.set("type", "item");
            reward1.set("chance", 100);
            reward1.set("with.item", "dirt");
            reward1.set("with.amount", 10);

            MemoryConfiguration tableCfg = new MemoryConfiguration();
            tableCfg.set("type", "table");
            tableCfg.set("count", 4);
            tableCfg.set("rewards", Collections.singletonList(reward1));

            lootManager().register("foobar", lootManager().loadLootTable(tableCfg));
        }

        @Nested
        @DisplayName("/lootadmin")
        class AdminCommands {

            @Nested
            @DisplayName("loot")
            class add {

                @Test
                @DisplayName("should diplay loot table result as inventory to player")
                void shouldWork() {

                    server().dispatchCommand(player,"lootadmin loot foobar");
                    player.assertInventoryView(InventoryType.CHEST);
                }
            }
        }
    }
}
