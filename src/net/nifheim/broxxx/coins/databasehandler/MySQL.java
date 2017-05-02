package net.nifheim.broxxx.coins.databasehandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
    private String player;

    public static Connection getConnection() {
        return c;
    }

    public void SQLConnection() {
        try {
            Connect();

            if (!MySQL.getConnection().isClosed()) {
                console.sendMessage(("&8[&cCoins&8] &7Plugin conected sucesful to the MySQL.").replaceAll("&", "§"));
            }
        } catch (SQLException e) {
            Logger.getLogger(MySQL.class.getName()).log(Level.WARNING, "Something was wrong with the connection, the error code is: " + e.getErrorCode(), e);
            Bukkit.getScheduler().cancelTasks(Main.getInstance());
            console.sendMessage(("&8[&cCoins&8] &7Can't connect to the database, disabling plugin...").replaceAll("&", "§"));
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            console.sendMessage(("&8[&cCoins&8] &7Checking the database connection ...").replaceAll("&", "§"));
            if (MySQL.getConnection() == null) {
                console.sendMessage(("&8[&cCoins&8] &7The database connection is null, reconnecting ...").replaceAll("&", "§"));
                Reconnect();
            } else {
                console.sendMessage(("&8[&cCoins&8] &7The connection to the database is still active.").replaceAll("&", "§"));
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
                + "`lastlogin` LONG NOT NULL,"
                + "PRIMARY KEY (`uuid`));";
        String createMultiplier
                = "CREATE TABLE IF NOT EXISTS `" + prefix + "Multipliers`"
                + "(`id` INT NOT NULL AUTO_INCREMENT,"
                + "`uuid` VARCHAR(50) NOT NULL,"
                + "`multiplier` INT,"
                + "`queue` INT,"
                + "`starttime` LONG,"
                + "`endtime` LONG,"
                + "`server` VARCHAR(50),"
                + "`enabled` BOOLEAN,"
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

    private String player(OfflinePlayer p) {
        player = p.getUniqueId().toString();
        return player;
    }

    // Query methods

    public Double getCoins(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    public Double getOfflineCoins(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    public String getCoinsString(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double coins = res.getDouble("balance");
            if (coins == 0) {
                return "0";
            } else {
                return String.valueOf(coins);
            }
        } else {
            return "Player can't be null";
        }
    }

    public String getCoinsStringOffline(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double coins = res.getDouble("balance");
            if (coins == 0) {
                return "0";
            } else {
                return String.valueOf(coins);
            }
        } else {
            return "Player can't be null";
        }
    }

    public void addCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE player ='" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (oldCoins + coins) + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void addCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE player ='" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (oldCoins + coins) + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void takeCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();
        double beforeCoins = res.getDouble("balance");
        if (res.getString("uuid") != null) {

            if (beforeCoins - coins < 0) {
                if (!config.getBoolean("Allow Negative")) {
                    Statement update = c.createStatement();
                    update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + localplayer + "';");
            }
        }
    }

    public void takeCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double beforeCoins = res.getDouble("balance");

            if (beforeCoins - coins < 0) {
                if (!config.getBoolean("Allow Negative")) {
                    return;
                }
                if (config.getBoolean("Allow Negative")) {
                    Statement bypassUpdate = c.createStatement();
                    bypassUpdate.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + localplayer + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + localplayer + "';");
            }
        }
    }

    public void resetCoins(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + 0 + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void resetCoinsOffline(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
        }
    }

    public void setCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + coins + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void setCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + coins + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public boolean isindb(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();

        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        return res.getString("player") != null;
    }

    public ResultSet getDataTop(int top) throws SQLException {
        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data ORDER BY balance DESC LIMIT " + top + ";");
        return res;
    }

    public void createPlayer(Player p) {

    }
}
