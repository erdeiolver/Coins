package net.nifheim.broxxx.coins.listener;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.Main;
import net.nifheim.broxxx.coins.databasehandler.MySQL;
import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final FileConfiguration config = Main.getInstance().getConfig();
    private static final MySQL mysql = new MySQL();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            if (config.getBoolean("MySQL.Use")) {
                try {
                    mysql.createPlayer(e.getPlayer());
                } catch (SQLException ex) {
                    Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Unable to create an entry in the database for player: " + e.getPlayer().getName() + " the error code is: " + ex.getErrorCode(), ex);
                }
            } else {
                Main.ff.createPlayer(e.getPlayer());
            }
        }, 5L);
    }
}
