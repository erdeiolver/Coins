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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import net.nifheim.broxxx.coins.databasehandler.FlatFile;
import net.nifheim.broxxx.coins.databasehandler.MySQL;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class CoinsAPI {

    private static final FileConfiguration CONFIG = Main.getInstance().getConfig();
    private static final MySQL MYSQL = Main.mysql;
    private static final FlatFile FLATFILE = Main.ff;
    private static final DecimalFormat DF = new DecimalFormat("#.#");

    private static boolean mysql() {
        return CONFIG.getBoolean("MySQL.Use");
    }

    /**
     * Get the coins of a Online Player.
     *
     * @param p Player to get the coins.
     * @return
     */
    public static Double getCoins(Player p) {
        return getCoinsOffline(p);
    }

    /**
     * Get the coins of a Offline Player.
     *
     * @param p Player to get the coins.
     * @return
     */
    public static Double getCoinsOffline(OfflinePlayer p) {
        if (mysql()) {
            return MYSQL.getCoinsOffline(p);
        } else {
            return FLATFILE.getCoinsOffline(p);
        }
    }

    /**
     * Get the coins String of a Online Player.
     *
     * @param p Player to get the coins string.
     * @return
     */
    public static String getCoinsString(Player p) {
        return getCoinsStringOffline(p);
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
                return (DF.format(coins));
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
     * @deprecated This shouldn't used.
     * @see #addCoins(org.bukkit.entity.Player, java.lang.Double,
     * java.lang.Boolean)
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
            MYSQL.addCoins(p, coins, multiply);
        } else {
            FLATFILE.addCoins(p, coins);
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
            MYSQL.addCoinsOffline(p, coins);
        } else {
            FLATFILE.addCoinsOffline(p, coins);
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
            MYSQL.takeCoins(p, coins);
        } else {
            FLATFILE.takeCoins(p, coins);
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
            MYSQL.takeCoinsOffline(p, coins);
        } else {
            FLATFILE.takeCoinsOffline(p, coins);
        }
    }

    /**
     * Reset the coins of a Online Player.
     *
     * @param p
     */
    public static void resetCoins(Player p) {
        if (mysql()) {
            MYSQL.resetCoins(p);
        } else {
            FLATFILE.resetCoins(p);
        }
    }

    /**
     * Reset the coins of a Offline Player.
     *
     * @param p
     */
    public static void resetCoinsOffline(OfflinePlayer p) {
        if (mysql()) {
            MYSQL.resetCoinsOffline(p);
        } else {
            FLATFILE.resetCoinsOffline(p);
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
            MYSQL.setCoins(p, coins);
        } else {
            FLATFILE.setCoins(p, coins);
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
            MYSQL.setCoinsOffline(p, coins);
        } else {
            FLATFILE.setCoinsOffline(p, coins);
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
            return MYSQL.isindb(p);

        } else {
            return FLATFILE.isindb(p);
        }
    }

    /**
     * Get the top players in coins data.
     *
     * @param top The lenght of the top list, for example 5 will get a max of 5
     * users for the top.
     * @return The ordered top list.
     */
    public static List<String> getTop(int top) {
        if (mysql()) {
            return MYSQL.getTop(top);
        } else {
            return FLATFILE.getTop(top);
        }
    }

    /**
     * Get the active multiplier countdown time formated in HHH:mm:ss
     *
     * @param server The server to get the multiplier time.
     * @return The multiplier time formated.
     */
    public static String getMultiplierTimeFormated(String server) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHH:mm:ss");
        return sdf.format(MYSQL.getMultiplierTime(server));
    }

    /**
     * Register a player in the database.
     *
     * @param p The player to register.
     */
    public static void createPlayer(Player p) {
        if (!isindb(p)) {
            if (mysql()) {
                MYSQL.createPlayer(p);
            } else {
                FLATFILE.createPlayer(p);
            }
        }
    }

    /**
     * Create a multiplier for a player with the specified time.
     *
     * @param p The player to create the multiplier.
     * @param multiplier The multiplier.
     * @param minutes The time for the multiplier.
     */
    public static void createMultiplier(Player p, Integer multiplier, Integer minutes) {
        MYSQL.createMultiplier(p, multiplier, minutes);
    }

    /**
     * Get the enabled multiplier for a server.
     *
     * @param server The server to get the enabled multiplier.
     */
    public static void getMultiplierFor(String server) {

    }
}
