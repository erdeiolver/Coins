package net.nifheim.broxxx.coins.databasehandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class MySQL {

    private static final FileConfiguration config = Main.getInstance().getConfig();
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private static Main plugin;

    private static final String host = config.getString("MySQL.Host");
    private static final int port = config.getInt("MySQL.Port");
    private static final String name = config.getString("MySQL.Database");
    private static final String user = config.getString("MySQL.User");
    private static final String passwd = config.getString("MySQL.Password");
    private static final String prefix = config.getString("MySQL.Prefix");
    private static final int checkdb = config.getInt("MySQL.Connection Interval") * 1200;
    private static Connection c;

    public static Connection getConnection() {
        return c;
    }

    public void SQLConnection() {
        try {
            Connect();

            if (!MySQL.getConnection().isClosed()) {
                console.sendMessage(plugin.rep("%prefix% Plugin conected sucesful to the MySQL."));
            }
        } catch (SQLException e) {
            Logger.getLogger(MySQL.class.getName()).log(Level.WARNING, "Something was wrong with the connection, the error code is: " + e.getErrorCode(), e);
            Bukkit.getScheduler().cancelTasks(Main.getInstance());
            console.sendMessage(plugin.rep("%prefix% Can't connect to the database, disabling plugin..."));
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            console.sendMessage(plugin.rep("%prefix% Checking the database connection ..."));
            if (MySQL.getConnection() == null) {
                console.sendMessage(plugin.rep("%prefix% The database connection is null, reconnecting ..."));
                Reconnect();
            } else {
                console.sendMessage(plugin.rep("%prefix% The connection to the database is still active."));
            }
        }, 0L, checkdb);
    }

    private void Connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }
        c = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?autoReconnect=true", user, passwd);
        String createData
                = "CREATE TABLE IF NOT EXISTS `" + prefix + "Data`"
                + "(`uuid` VARCHAR(50) NOT NULL,"
                + "`nick` VARCHAR(50) NOT NULL,"
                + "`balance` DOUBLE NOT NULL,"
                + "`lastlogin` INT NOT NULL,"
                + "PRIMARY KEY (`uuid`));";
        String createMultiplier
                = "CREATE TABLE IF NOT EXISTS `" + prefix + "Multipliers`"
                + "(`id` INT NOT NULL AUTO_INCREMENT,"
                + "`uuid` VARCHAR(50) NOT NULL,"
                + "`multiplier` INT,"
                + "`queue` INT,"
                + "`starttime` INT,"
                + "`endtime` INT,"
                + "PRIMARY KEY (`id`));";

        Statement update = c.createStatement();
        update.execute(createData);
        update.execute(createMultiplier);
    }

    public void Reconnect() {
        Disconnect();

        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            try {
                Connect();
            } catch (SQLException ex) {
            }
        }, 20L);
    }

    private void Disconnect() {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
        }
    }
}
