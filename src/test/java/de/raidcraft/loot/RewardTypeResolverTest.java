package de.raidcraft.loot;

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RewardTypeResolverTest {

    @Test
    @DisplayName("should return implicit type if set")
    void shouldReturnImplicitType() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("type", Constants.DEFAULT_TYPE);

        assertThat(RewardTypeResolver.resolveType(cfg))
                .isNotEmpty()
                .get()
                .isEqualTo("none");
    }

    @Test
    @DisplayName("should return an empty optional if no type is set")
    void shouldReturnEmptyOptionalIfNoTypeIsSet() {

        assertThat(RewardTypeResolver.resolveType(new MemoryConfiguration()))
                .isEmpty();
    }

    @Test
    @DisplayName("should return item type if item is set")
    void shouldReturnItemTypeIfItemIsSet() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("item", "foobar");

        assertThat(RewardTypeResolver.resolveType(cfg))
                .isNotEmpty()
                .get()
                .isEqualTo(Constants.Types.ITEM);
    }

    @Test
    @DisplayName("should return command type if command is set")
    void shouldReturnCommandTypeIfSet() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("command", "foobar");

        assertThat(RewardTypeResolver.resolveType(cfg))
                .isNotEmpty()
                .get()
                .isEqualTo(Constants.Types.COMMAND);
    }

    @Test
    @DisplayName("should allow overriding guessed type with explicit type")
    void explicitTypeShouldOverrideGuessedType() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("item", "foobar");
        cfg.set("type", "command");

        assertThat(RewardTypeResolver.resolveType(cfg))
                .isNotEmpty()
                .get()
                .isEqualTo(Constants.Types.COMMAND);
    }
}