package net.nifheim.broxxx.coins.listener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.Main;
import net.nifheim.broxxx.coins.databasehandler.MySQL;
import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    static FileConfiguration config = Main.getInstance().getConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            try {
                Player p = e.getPlayer();
                String name = null;
                if (config.getBoolean("Online Mode")) {
                    name = p.getUniqueId().toString();
                } else {
                    name = p.getName();
                }

                Connection c = MySQL.getConnection();
                Statement check = null;
                check = c.createStatement();
                ResultSet res = check.executeQuery("SELECT uuid FROM Coins WHERE uuid = '" + name + "';");
                if (!res.next()) {
                    Statement update = c.createStatement();
                    update.executeUpdate("INSERT INTO Coins VALUES ('" + p.getUniqueId() +"', '"+ p.getName() + "', 0, " + System.currentTimeMillis() + ");");
                }
            } catch (SQLException ex) {
                if (ex.getSQLState().equals("closed")) {
                    Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Seems that the database connection is closed.");
                } else {
                    Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Something was wrong executing this command, the error code is: " + ex.getErrorCode(), ex.getCause());
                }
            }
        }, 5L);
    }
}
