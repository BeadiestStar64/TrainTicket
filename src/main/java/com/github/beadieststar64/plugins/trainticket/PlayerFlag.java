package com.github.beadieststar64.plugins.trainticket;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerFlag {

    private final Player player;
    private final Plugin plugin;

    public PlayerFlag(Player player, Plugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public void setMeta(boolean flag) {player.setMetadata("TrainTicket", new FixedMetadataValue(plugin, flag));}

    public boolean get() {
        try {
            return player.getMetadata("TrainTicket").get(0).asBoolean();
        }catch(Exception e) {
            return false;
        }
    }

    public void set() {
        setMeta(true);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                setMeta(false);
            }
        };
        runnable.runTaskLater(plugin, 3);
    }
}
