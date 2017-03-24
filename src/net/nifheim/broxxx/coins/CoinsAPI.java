package net.nifheim.broxxx.coins;

import net.nifheim.broxxx.coins.databasehandler.MySQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CoinsAPI {

    private static final FileConfiguration config = Main.getInstance().getConfig();
    private static final Connection c = MySQL.getConnection();

    private static boolean online() {
        return config.getBoolean("Online Mode");
    }

    public static Integer getCoins(Player p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            int coins = res.getInt("balance");

            return coins;
        }
        return 0;
    }

    @Deprecated
    public static Integer getOfflineCoins(OfflinePlayer p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            int coins = res.getInt("balance");

            return coins;
        }
        return 0;
    }

    public static String getCoinsString(Player p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            int coins = res.getInt("balance");
            if (coins == 0) {
                return "0";
            } else {
                return String.valueOf(coins);
            }
        } else {
            return "Player can't be null";
        }
    }

    @Deprecated
    public static String getCoinsStringOffline(OfflinePlayer p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            int coins = res.getInt("balance");
            if (coins == 0) {
                return "0";
            } else {
                return String.valueOf(coins);
            }
        } else {
            return "Player can't be null";
        }
    }

    public static void addCoins(Player p, int coins) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player ='" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            int oldCoins = res.getInt("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance = " + (oldCoins + (coins * config.getInt("Multiplier"))) + " WHERE player = '" + name + "';");
        }
    }

    @Deprecated
    public static void addCoinsOffline(OfflinePlayer p, int coins) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player ='" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            int oldCoins = res.getInt("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance = " + (oldCoins + coins) + " WHERE player = '" + name + "';");
        }
    }

    public static void takeCoins(Player p, int coins) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();
        int beforeCoins = res.getInt("balance");
        if (res.getString("player") != null) {
            /*int beforeCoins = res.getInt("balance");*/

            if (beforeCoins - coins < 0) {
                if (!config.getBoolean("Allow Negative")) {
                    Statement update = c.createStatement();
                    update.executeUpdate("UPDATE Coins SET balance = 0 WHERE player = '" + name + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE Coins SET balance = 0 WHERE player = '" + name + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE Coins SET balance = " + (beforeCoins - coins) + " WHERE player = '" + name + "';");
            }
        }
    }

    @Deprecated
    public static void takeCoinsOffline(OfflinePlayer p, int coins) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            int beforeCoins = res.getInt("balance");

            if (beforeCoins - coins < 0) {
                if (!config.getBoolean("Allow Negative")) {
                    return;
                }
                if (config.getBoolean("Allow Negative")) {
                    Statement bypassUpdate = c.createStatement();
                    bypassUpdate.executeUpdate("UPDATE Coins SET balance = " + (beforeCoins - coins) + " WHERE player = '" + name + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE Coins SET balance = 0 WHERE player = '" + name + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE Coins SET balance = " + (beforeCoins - coins) + " WHERE player = '" + name + "';");
            }
        }
    }

    public static void resetCoins(Player p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }
        int newcoins = 0;

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance = " + newcoins + " WHERE player = '" + name + "';");
        }
    }

    @Deprecated
    public static void resetCoinsOffline(OfflinePlayer p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance = 0 WHERE player = '" + name + "';");
        }
    }

    public static void setCoins(Player p, int coins) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance =" + coins + "WHERE player = '" + name + "';");
        }
    }

    @Deprecated
    public static void setCoinsOffline(OfflinePlayer p, int coins) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance =" + coins + "WHERE player = '" + name + "';");
        }
    }

    public static boolean isindb(OfflinePlayer p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();

        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        return res.getString("player") != null;
    }

    public static ResultSet getDataTop(int top) throws SQLException {
        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins ORDER BY balance DESC LIMIT " + top + ";");
        return res;
    }
}
