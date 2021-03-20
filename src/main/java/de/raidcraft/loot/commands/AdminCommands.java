package de.raidcraft.loot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import de.raidcraft.loot.Constants;
import de.raidcraft.loot.LootObject;
import de.raidcraft.loot.LootTable;
import de.raidcraft.loot.RCLoot;
import de.raidcraft.loot.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@CommandAlias("lootadmin|rcloot:admin|rcla")
@CommandPermission(Constants.Permissions.ADMIN)
public class AdminCommands extends BaseCommand {

    private final RCLoot plugin;

    public AdminCommands(RCLoot plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission(Constants.Permissions.ADMIN + ".reload")
    public void reload() {

        final CommandIssuer issuer = getCurrentCommandIssuer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            plugin.reload();
            issuer.sendMessage(ChatColor.GREEN + "Das RCLoot Plugin und alle Konfigurationen wurden erfolgreich neu geladen.");
        });
    }

    @Subcommand("loot")
    @CommandCompletion("@tables @players")
    @CommandPermission(Constants.Permissions.ADMIN + ".loot")
    public void loot(LootTable table, Player player) {

        Collection<LootObject> result = table.loot(player);
        Inventory inventory = Bukkit.createInventory(player, InventoryType.CHEST, table.name());

        ItemStack[] items = result.stream()
                .map(ItemUtil::toDisplayItem)
                .toArray(ItemStack[]::new);
        inventory.setContents(items);

        player.openInventory(inventory);
    }
}
