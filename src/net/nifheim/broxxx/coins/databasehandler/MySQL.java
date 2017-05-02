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
        if (online()) {
            player = p.getUniqueId().toString();
        } else {
            player = p.getName();
        }
        return player;
    }

    // Query methods
    private static boolean online() {
        return config.getBoolean("Online Mode");
    }

    public Double getCoins(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    public Double getOfflineCoins(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    public String getCoinsString(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
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
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
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
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player ='" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + (oldCoins + coins) + " WHERE player = '" + localplayer + "';");
        }
    }

    public void addCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player ='" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + (oldCoins + coins) + " WHERE player = '" + localplayer + "';");
        }
    }

    public void takeCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();
        double beforeCoins = res.getDouble("balance");
        if (res.getString("player") != null) {

            if (beforeCoins - coins < 0) {
                if (!config.getBoolean("Allow Negative")) {
                    Statement update = c.createStatement();
                    update.executeUpdate("UPDATE " + prefix + "Coins SET balance = 0 WHERE player = '" + localplayer + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Coins SET balance = 0 WHERE player = '" + localplayer + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + (beforeCoins - coins) + " WHERE player = '" + localplayer + "';");
            }
        }
    }

    public void takeCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            double beforeCoins = res.getDouble("balance");

            if (beforeCoins - coins < 0) {
                if (!config.getBoolean("Allow Negative")) {
                    return;
                }
                if (config.getBoolean("Allow Negative")) {
                    Statement bypassUpdate = c.createStatement();
                    bypassUpdate.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + (beforeCoins - coins) + " WHERE player = '" + localplayer + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Coins SET balance = 0 WHERE player = '" + localplayer + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + (beforeCoins - coins) + " WHERE player = '" + localplayer + "';");
            }
        }
    }

    public void resetCoins(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + 0 + " WHERE player = '" + localplayer + "';");
        }
    }

    public void resetCoinsOffline(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Coins SET balance = 0 WHERE player = '" + localplayer + "';");
        }
    }

    public void setCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + coins + " WHERE player = '" + localplayer + "';");
        }
    }

    public void setCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Coins SET balance = " + coins + " WHERE player = '" + localplayer + "';");
        }
    }

    public boolean isindb(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();

        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Coins WHERE player = '" + localplayer + "';");
        res.next();

        return res.getString("player") != null;
    }

    public ResultSet getDataTop(int top) throws SQLException {
        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins ORDER BY balance DESC LIMIT " + top + ";");
        return res;
    }
}
