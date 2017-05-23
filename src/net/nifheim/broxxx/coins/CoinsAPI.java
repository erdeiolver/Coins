/*
 * This file is part of Coins.
 *
 * Copyright © 2017 Beelzebu
 * Coins is licensed under the GNU General Public License.
 *
 * Coins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Coins is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.nifheim.broxxx.coins;

import net.nifheim.broxxx.coins.databasehandler.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.databasehandler.FlatFile;
import net.nifheim.broxxx.coins.listener.PlayerJoinListener;

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
    private static final DecimalFormat df = new DecimalFormat("#.#");

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
        return Double.parseDouble("0");
    }

    /**
     * Get the coins of a Offline Player.
     *
     * @param p Player to get the coins.
     * @return
     */
    public static Double getCoinsOffline(OfflinePlayer p) {
        if (mysql()) {
            try {
                return mysql.getCoinsOffline(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to get the coins of a user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            return ff.getCoinsOffline(p);
        }
        return Double.parseDouble("0");
    }

    /**
     * Get the coins String of a Online Player.
     *
     * @param p Player to get the coins string.
     * @return
     */
    public static String getCoinsString(Player p) {

        if (p != null) {
            double coins = getCoins(p);
            if (coins == 0.0 || coins == 0) {
                return "0";
            } else {
                return (df.format(coins));
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
     */
    public static String getCoinsStringOffline(OfflinePlayer p) {

        if (p != null) {
            double coins = getCoinsOffline(p);
            if (coins == 0.0 || coins == 0) {
                return "0";
            } else {
                return (df.format(coins));
            }
        } else {
            return "Player can't be null";
        }
    }

    /**
     *
     */
    /**
     * Add coins to a Online Player.
     *
     * @param p The player to add the coins.
     * @param coins The coins to add.
     * @deprecated Use addCoins(Player p, Double coins, Boolean multiply)
     */
    @Deprecated
    public static void addCoins(Player p, Double coins) {
        addCoins(p, coins, false);
    }

    /**
     * Add coins to a Online Player.
     *
     * @param p The player to add the coins.
     * @param coins The coins to add.
     * @param multiply Multiply coins if there are any active multipliers
     */
    public static void addCoins(Player p, Double coins, Boolean multiply) {
        if (mysql()) {
            try {
                mysql.addCoins(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to add the coins to a user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.addCoins(p, coins);
        }
    }

    /**
     * Add coins to a Offline Player.
     *
     * @param p
     * @param coins
     */
    public static void addCoinsOffline(OfflinePlayer p, Double coins) {
        if (mysql()) {
            try {
                mysql.addCoinsOffline(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to add coins to an offline user, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.addCoinsOffline(p, coins);
        }
    }

    /**
     * Take coins of a Online Player.
     *
     * @param p
     * @param coins
     */
    public static void takeCoins(Player p, Double coins) {
        if (mysql()) {
            try {
                mysql.takeCoins(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.takeCoins(p, coins);
        }
    }

    /**
     * Take coins of a Offline Player.
     *
     * @param p
     * @param coins
     */
    public static void takeCoinsOffline(OfflinePlayer p, Double coins) {
        if (mysql()) {
            try {
                mysql.takeCoinsOffline(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.takeCoinsOffline(p, coins);
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
            ff.resetCoins(p);
        }
    }

    /**
     * Reset the coins of a Offline Player.
     *
     * @param p
     */
    public static void resetCoinsOffline(OfflinePlayer p) {
        if (mysql()) {
            try {
                mysql.resetCoinsOffline(p);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.resetCoinsOffline(p);
        }
    }

    /**
     * Set the coins of a Online Player.
     *
     * @param p
     * @param coins
     */
    public static void setCoins(Player p, Double coins) {
        if (mysql()) {
            try {
                mysql.setCoins(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.setCoins(p, coins);
        }
    }

    /**
     * Set the coins of a Offline Player
     *
     * @param p
     * @param coins
     */
    public static void setCoinsOffline(OfflinePlayer p, Double coins) {
        if (mysql()) {
            try {
                mysql.setCoinsOffline(p, coins);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsAPI.class.getName()).log(Level.WARNING, "An error ocurred when atemping to execute a query in the database to <INSERTE ACCION>, the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.setCoinsOffline(p, coins);
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
     * @throws java.sql.SQLException
     */
    public static ResultSet getDataTop(int top) throws SQLException {
        return mysql.getDataTop(top);
    }

    public static String getMultiplierTimeFormated() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HHH:mm:ss");
            return sdf.format(mysql.getMultiplierTime());
        } catch (SQLException ex) {
            Logger.getLogger(CoinsAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "0";
    }

    public static void createPlayer(Player p) {
        if (config.getBoolean("MySQL.Use")) {
            try {
                mysql.createPlayer(p);
            } catch (SQLException ex) {
                Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Unable to create an entry in the database for player: " + p.getName() + " the error code is: " + ex.getErrorCode(), ex);
            }
        } else {
            ff.createPlayer(p);
        }
    }

    public static void createMultiplier(Player p, Integer multiplier, Integer minutes) {
        try {
            mysql.createMultiplier(p, multiplier, minutes);
        } catch (SQLException ex) {
            Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Unable to create a multiplier in the database for player: " + p.getName() + " the error code is: " + ex.getErrorCode(), ex);
        }
    }
}
