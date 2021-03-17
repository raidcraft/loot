package de.raidcraft.loot;

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
public class RCLoot extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static RCLoot instance;

    private Database database;
    private PaperCommandManager commandManager;

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

        commandManager.registerCommand(new AdminCommands(this));
        commandManager.registerCommand(new PlayerCommands(this));
    }

    private void setupDatabase() {

        this.database = new EbeanWrapper(Config.builder(this)
                .entities(
                        // TODO: add your database entities here
                )
                .build()).connect();
    }
}
