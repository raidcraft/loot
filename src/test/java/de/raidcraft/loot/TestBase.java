package de.raidcraft.loot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@Accessors(fluent = true)
@Getter
public class TestBase {

    private ServerMock server;
    private RCLoot plugin;

    @BeforeEach
    protected void setUp() {

        server = MockBukkit.mock();
        plugin = MockBukkit.load(RCLoot.class);
    }

    @AfterEach
    protected void tearDown() {

        MockBukkit.unmock();
    }

    public LootManager lootManager() {

        return plugin().lootManager();
    }
}
