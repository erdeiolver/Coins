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

    private final FileConfiguration config = Main.getInstance().getConfig();
    private final String prefix = config.getString("MySQL.Prefix");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
//            if (config.getBoolean("MySQL.Use")) {
                try {
                    Player p = e.getPlayer();
                    String name;
                    if (config.getBoolean("Online Mode")) {
                        name = p.getUniqueId().toString();
                    } else {
                        name = p.getName();
                    }

                    Connection c = MySQL.getConnection();
                    Statement check = c.createStatement();
                    if (config.getBoolean("Online Mode")) {
                        ResultSet res = check.executeQuery("SELECT uuid FROM " + prefix + "Data WHERE uuid = '" + name + "';");
                        if (!res.next()) {
                            Statement update = c.createStatement();
                            update.executeUpdate("INSERT INTO " + prefix + "Data VALUES ('" + name + "', '" + p.getName() + "', 0, " + System.currentTimeMillis() + ");");
                        } else {
                            Statement update = c.createStatement();
                            update.executeUpdate("UPDATE " + prefix + "Data SET nick = " + p.getName() + " WHERE uuid = '" + name + "';");
                        }
                    } else {
                        ResultSet res = check.executeQuery("SELECT nick FROM " + prefix + "Data WHERE nick = '" + name + "';");
                        if (!res.next()) {
                            Statement update = c.createStatement();
                            update.executeUpdate("INSERT INTO " + prefix + "Data VALUES ('" + p.getUniqueId() + "', '" + name + "', 0, " + System.currentTimeMillis() + ");");
                        } else {
                            Statement update = c.createStatement();
                            update.executeUpdate("UPDATE " + prefix + "Data SET uuid = " + p.getUniqueId() + " WHERE nick = '" + name + "';");
                        }
                    }

                } catch (SQLException ex) {
                    if (ex.getSQLState().equals("closed")) {
                        Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Seems that the database connection is closed.");
                    } else {
                        Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Something was wrong executing this query, the error code is: " + ex.getErrorCode(), ex.getCause());
                    }
                }
//            }
//            else {
//                Main.ff.createPlayer(e.getPlayer());
//            }
        }, 5L);
    }
}
