package com.github.beadieststar64.plugins.trainticket;

import org.bukkit.plugin.java.JavaPlugin;

public final class TrainTicket extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new PaySign(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
