package net.nifheim.broxxx.coins;

import net.nifheim.broxxx.coins.databasehandler.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.broxxx.coins.databasehandler.FlatFile;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Beelzebu
 */
public class CoinsAPI {

    private static final FileConfiguration config = Main.getInstance().getConfig();
    private static final MySQL mysql = Main.mysql;
    private static final FlatFile ff = Main.ff;

    private static boolean mysql() {
        return config.getBoolean("MySQL.Use");
    }

    /**
     * Get the coins of a Online Player.
     *
     * @param p Player to get the coins.
     * @return
     */
    public static Double getCoins(Player p) {
        if (mysql()) {
            try {
                return mysql.getCoins(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to get the coins of a user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            return ff.getCoins(p);
        }
        return 0.0;
    }

    /**
     * Get the coins of a Offline Player.
     *
     * @param p Player to get the coins.
     * @return
     * @deprecated
     */
    @Deprecated
    public static Double getOfflineCoins(OfflinePlayer p) {
        if (mysql()) {
            try {
                return mysql.getOfflineCoins(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to get the coins of a user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            return ff.getOfflineCoins(p);
        }
        return 0.0;
    }

    /**
     * Get the coins String of a Online Player.
     *
     * @param p Player to get the coins string.
     * @return
     */
    public static String getCoinsString(Player p) {
        if (mysql()) {
            try {
                return mysql.getCoinsString(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to get the string of coins of a user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            return ff.getCoinsString(p);
        }
        return "Player can't be null";
    }

    /**
     * Get the coins String of a Offline Player.
     *
     * @param p Player to get the coins string.
     * @return
     * @deprecated
     */
    @Deprecated
    public static String getCoinsStringOffline(OfflinePlayer p) {
        if (mysql()) {
            try {
                return mysql.getCoinsStringOffline(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to get the string of coins of a user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            return ff.getCoinsStringOffline(p);
        }
        return "Player can't be null";
    }

    /**
     * Add coins to a Online Player.
     *
     * @param p The player to add the coins.
     * @param coins The coins to add.
     */
    public static void addCoins(Player p, double coins) {
        if (mysql()) {
            try {
                mysql.addCoins(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to add the coins to a user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Add coins to a Offline Player.
     *
     * @param p
     * @param coins
     * @deprecated
     */
    @Deprecated
    public static void addCoinsOffline(OfflinePlayer p, double coins) {
        if (mysql()) {
            try {
                mysql.addCoinsOffline(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to add coins to an offline user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Take coins of a Online Player.
     *
     * @param p
     * @param coins
     */
    public static void takeCoins(Player p, double coins) {
        if (mysql()) {
            try {
                mysql.takeCoins(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Take coins of a Offline Player.
     *
     * @param p
     * @param coins
     * @deprecated
     */
    @Deprecated
    public static void takeCoinsOffline(OfflinePlayer p, double coins) {
        if (mysql()) {
            try {
                mysql.takeCoinsOffline(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Reset the coins of a Online Player.
     *
     * @param p
     */
    public static void resetCoins(Player p) {
        if (mysql()) {
            try {
                mysql.resetCoins(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Reset the coins of a Offline Player.
     *
     * @param p
     * @deprecated
     */
    @Deprecated
    public static void resetCoinsOffline(OfflinePlayer p) {
        if (mysql()) {
            try {
                mysql.resetCoinsOffline(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Set the coins of a Online Player.
     *
     * @param p
     * @param coins
     */
    public static void setCoins(Player p, double coins) {
        if (mysql()) {
            try {
                mysql.setCoins(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Set the coins of a Offline Player
     *
     * @param p
     * @param coins
     * @deprecated
     */
    @Deprecated
    public static void setCoinsOffline(OfflinePlayer p, double coins) {
        if (mysql()) {
            try {
                mysql.setCoinsOffline(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {

        }
    }

    /**
     * Get if the Offline Player is in the database.
     *
     * @param p
     * @return
     */
    public static boolean isindb(OfflinePlayer p) {
        if (mysql()) {
            try {
                return mysql.isindb(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            return ff.isindb(p);
        }
        return false;
    }

    /**
     * Get the top players in coins data.
     *
     * @param top
     * @return
     */
    public static ResultSet getDataTop(int top) throws SQLException {
        return mysql.getDataTop(top);
    }
}
