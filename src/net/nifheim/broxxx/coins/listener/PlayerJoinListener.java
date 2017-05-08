package net.nifheim.broxxx.coins.listener;

import net.nifheim.broxxx.coins.CoinsAPI;

import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            CoinsAPI.createPlayer(e.getPlayer());
        }, 5L);
    }
}
