package net.nifheim.broxxx.coins.databasehandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class MySQL {

    static FileConfiguration config = Main.getInstance().getConfig();
    final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private Main plugin;

    private static final String host = config.getString("MySQL.Host");
    private static final int port = config.getInt("MySQL.Port");
    private static final String name = config.getString("MySQL.Database");
    private static final String user = config.getString("MySQL.User");
    private static final String passwd = config.getString("MySQL.Password");
    private static final String prefix = config.getString("MySQL.Prefix");
    private static Connection c;

    public static Connection getConnection() {
        return c;
    }

    public static void Connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }
        c = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?autoReconnect=true", user, passwd);
        String createdb
                = "CREATE TABLE IF NOT EXISTS `" + prefix + "Data`"
                + "(`uuid` VARCHAR(50) NOT NULL,"
                + "`nick` VARCHAR (50) NOT NULL,"
                + "`balance` DOUBLE NOT NULL,"
                + "`lastlogin` INT NOT NULL,"
                + "PRIMARY KEY (`uuid`));" /*
                + "CREATE TABLE IF NOT EXISTS `" + prefix + "Multipliers`"
                + "(`uuid` VARCHAR(50) NOT NULL,"
                + "`nick` VARCHAR (50) NOT NULL,"
                + "`multipliers` VARCHAR(666),"
                + "`active` INT,"
                + "`starttime` INT,"
                + "`endtime` INT,"
                + "PRIMARY KEY (`uuid`));"
                 */;

        Statement update = c.createStatement();
        update.execute(createdb);
    }

    public static void Reconnect() {
        Disconnect();

        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
            try {
                Connect();
            } catch (SQLException ex) {
            }
        }, 20L);
    }

    public static void Disconnect() {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
        }
    }
}
