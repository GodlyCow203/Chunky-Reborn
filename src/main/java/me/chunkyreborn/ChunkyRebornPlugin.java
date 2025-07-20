package me.chunkyreborn;

import me.chunkyreborn.commands.ChunkyRebornCommand;
import me.chunkyreborn.listeners.BiomeSelectorListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChunkyRebornPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if (getCommand("chunkyreborn") == null) {
            getLogger().severe("Command 'chunkyreborn' not found in plugin.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Chunky Reborn Enabled!");
        saveDefaultConfig();
        getCommand("chunkyreborn").setTabCompleter(new ChunkyTabCompleteer());
        getServer().getPluginManager().registerEvents(new BiomeSelectorListener(this), this);
        getCommand("chunkyreborn").setExecutor(new ChunkyRebornCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Chunky Reborn Disabled.");
    }
}
