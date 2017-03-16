package net.nifheim.broxxx.coins.listener;

import java.io.IOException;
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
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
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
                try {
                    check = c.createStatement();
                } catch (SQLException ex) {
                    Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                ResultSet res = check.executeQuery("SELECT player FROM Coins WHERE player = '" + name + "';");
                if (!res.next()) {
                    Statement update = c.createStatement();
                    update.executeUpdate("INSERT INTO Coins VALUES ('" + name + "',0);");
                }
            } catch (SQLException ex) {
                Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, 5L);
    }
}
