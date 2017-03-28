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

    /**
     * Get the coins of a Online Player.
     *
     * @param p Player to get the coins.
     * @return
     * @throws SQLException
     */
    public static Double getCoins(Player p) throws SQLException {
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
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    /**
     * Get the coins of a Offline Player.
     *
     * @param p Player to get the coins.
     * @return
     * @throws SQLException
     * @deprecated Avoid using this method.
     */
    @Deprecated
    public static Double getOfflineCoins(OfflinePlayer p) throws SQLException {
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
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    /**
     * Get the coins String of a Online Player.
     *
     * @param p Player to get the coins string.
     * @return
     * @throws SQLException
     */
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

    /**
     * Get the coins String of a Offline Player.
     *
     * @param p Player to get the coins string.
     * @return
     * @throws SQLException
     * @deprecated
     */
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

    /**
     * Add coins to a Online Player.
     *
     * @param p The player to add the coins.
     * @param coins The coins to add.
     * @throws SQLException
     */
    public static void addCoins(Player p, double coins) throws SQLException {
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
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance = " + (oldCoins + (coins * config.getInt("Multiplier"))) + " WHERE player = '" + name + "';");
        }
    }

    /**
     * Add coins to a Offline Player.
     *
     * @param p
     * @param coins
     * @throws SQLException
     * @deprecated
     */
    @Deprecated
    public static void addCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
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
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance = " + (oldCoins + coins) + " WHERE player = '" + name + "';");
        }
    }

    /**
     * Take coins of a Online Player.
     *
     * @param p
     * @param coins
     * @throws SQLException
     */
    public static void takeCoins(Player p, double coins) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();
        double beforeCoins = res.getDouble("balance");
        if (res.getString("player") != null) {

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

    /**
     * Take coins of a Offline Player.
     *
     * @param p
     * @param coins
     * @throws SQLException
     * @deprecated
     */
    @Deprecated
    public static void takeCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
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
            double beforeCoins = res.getDouble("balance");

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

    /**
     * Reset the coins of a Online Player.
     *
     * @param p
     * @throws SQLException
     */
    public static void resetCoins(Player p) throws SQLException {
        String name;
        if (online()) {
            name = p.getUniqueId().toString();
        } else {
            name = p.getName();
        }
        double newcoins = 0;

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins WHERE player = '" + name + "';");
        res.next();

        if (res.getString("player") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE Coins SET balance = " + newcoins + " WHERE player = '" + name + "';");
        }
    }

    /**
     * Reset the coins of a Offline Player.
     *
     * @param p
     * @throws SQLException
     * @deprecated
     */
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

    /**
     * Set the coins of a Online Player.
     *
     * @param p
     * @param coins
     * @throws SQLException
     */
    public static void setCoins(Player p, double coins) throws SQLException {
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
            update.executeUpdate("UPDATE Coins SET balance = " + coins + " WHERE player = '" + name + "';");
        }
    }

    /**
     * Set the coins of a Offline Player
     *
     * @param p
     * @param coins
     * @throws SQLException
     * @deprecated
     */
    @Deprecated
    public static void setCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
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
            update.executeUpdate("UPDATE Coins SET balance = " + coins + " WHERE player = '" + name + "';");
        }
    }

    /**
     * Get if the Offline Player is in the database.
     *
     * @param p
     * @return
     * @throws SQLException
     */
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

    /**
     * Get the top players in coins data.
     *
     * @param top
     * @return
     * @throws SQLException
     */
    public static ResultSet getDataTop(int top) throws SQLException {
        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM Coins ORDER BY balance DESC LIMIT " + top + ";");
        return res;
    }
}
