package net.nifheim.broxxx.coins;

import net.nifheim.broxxx.coins.databasehandler.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Beelzebu
 */
public class CoinsAPI {

    private static final FileConfiguration config = Main.getInstance().getConfig();
    private static final MySQL mysql = new MySQL();

    private static boolean mysql() {
        return config.getBoolean("MySQL.Use");
    }

    /**
     * Get the coins of a Online Player.
     *
     * @param p Player to get the coins.
     * @return
     * @throws SQLException
     */
    public static Double getCoins(Player p) throws SQLException {
        if (mysql()) {
            return mysql.getCoins(p);
        } else {
            return 0.0;
        }
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
        if (mysql()) {
            return mysql.getOfflineCoins(p);
        } else {
            return 0.0;
        }
    }

    /**
     * Get the coins String of a Online Player.
     *
     * @param p Player to get the coins string.
     * @return
     * @throws SQLException
     */
    public static String getCoinsString(Player p) throws SQLException {
        if (mysql()) {
            return mysql.getCoinsString(p);
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
        if (mysql()) {
            return mysql.getCoinsStringOffline(p);
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
        if (mysql()) {
            mysql.addCoins(p, coins);
        } else {

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
        if (mysql()) {
            mysql.addCoinsOffline(p, coins);
        } else {

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
        if (mysql()) {
            mysql.takeCoins(p, coins);
        } else {

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
        if (mysql()) {
            mysql.takeCoinsOffline(p, coins);
        } else {

        }
    }

    /**
     * Reset the coins of a Online Player.
     *
     * @param p
     * @throws SQLException
     */
    public static void resetCoins(Player p) throws SQLException {
        if (mysql()) {
            mysql.resetCoins(p);
        } else {

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
        if (mysql()) {
            mysql.resetCoinsOffline(p);
        } else {

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
        if (mysql()) {
            mysql.setCoins(p, coins);
        } else {

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
        if (mysql()) {
            mysql.setCoinsOffline(p, coins);
        } else {

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
        if (mysql()) {
            return mysql.isindb(p);
        } else {
            return false;
        }
    }

    /**
     * Get the top players in coins data.
     *
     * @param top
     * @return
     * @throws SQLException
     */
    public static ResultSet getDataTop(int top) throws SQLException {
        if (mysql()) {
            return mysql.getDataTop(top);
        } else {
            return mysql.getDataTop(top);
        }
    }
}
