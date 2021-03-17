package de.raidcraft.loot;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IntegrationTest extends TestBase {

    @Nested
    @DisplayName("Commands")
    class Commands {

        private Player player;

        @BeforeEach
        void setUp() {
            player = server().addPlayer();
        }

        @Nested
        @DisplayName("/template:admin")
        class AdminCommands {

            @Nested
            @DisplayName("foo bar")
            class add {

                @Test
                @DisplayName("should work")
                void shouldWork() {

                    server().dispatchCommand(server().getConsoleSender(),"rc:template add foo " + player.getName() + " bar");
                }
            }
        }
    }
}
