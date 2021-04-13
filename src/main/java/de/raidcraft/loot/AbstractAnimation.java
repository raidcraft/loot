package de.raidcraft.loot;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

@Data
@Accessors(fluent = true)
public abstract class AbstractAnimation implements Animation {

    private final Player player;
    private final LootTable lootTable;
    private long duration;
    private long currentTick;

    protected AbstractAnimation(@NonNull Player player, @NonNull LootTable lootTable) {
        this.player = player;
        this.lootTable = lootTable;
    }

    @Override
    public final void start() {

        
    }

    protected abstract void onStart();
}
