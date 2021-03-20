package de.raidcraft.loot;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import de.raidcraft.loot.commands.AdminCommands;
import de.raidcraft.loot.commands.PlayerCommands;
import io.ebean.Database;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@PluginMain
@Accessors(fluent = true)
public class RCLoot extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static RCLoot instance;

    private Database database;
    private PaperCommandManager commandManager;

    @Getter
    private LootManager lootManager;

    @Getter
    private static boolean testing = false;

    public RCLoot() {
        instance = this;
    }

    public RCLoot(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
        testing = true;
    }

    @Override
    public void onEnable() {

        loadConfig();
        setupDatabase();
        setupLootManager();
        setupListener();
        setupCommands();
    }

    public void reload() {

        loadConfig();
        lootManager().reload();
    }

    private void loadConfig() {

        getDataFolder().mkdirs();
        saveDefaultConfig();
    }

    private void setupListener() {


    }

    private void setupLootManager() {

        lootManager = new LootManager(this);
        lootManager.load();
    }

    private void setupCommands() {

        this.commandManager = new PaperCommandManager(this);

        // contexts
        lootTableContext(commandManager);

        // completions
        lootTableCompletion(commandManager);

        commandManager.registerCommand(new AdminCommands(this));
        commandManager.registerCommand(new PlayerCommands(this));
    }

    private void lootTableContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerContext(LootTable.class, context -> {
            String name = context.popFirstArg();
            return lootManager()
                    .lootTable(name)
                    .orElseThrow(() -> new InvalidCommandArgument("Es gibt keine Loot Tabelle mit dem Namen: " + name));
        });
    }

    private void lootTableCompletion(PaperCommandManager commandManager) {

        commandManager.getCommandCompletions().registerAsyncCompletion("tables",
                context -> lootManager().lootTables().keySet());
    }

    private void setupDatabase() {

        this.database = new EbeanWrapper(Config.builder(this)
                .entities(
                        // TODO: add your database entities here
                )
                .build()).connect();
    }
}
