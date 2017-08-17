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
package net.nifheim.beelzebu.coins;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.multiplier.Multiplier;
import net.nifheim.beelzebu.coins.core.utils.CacheManager;

/**
 *
 * @author Beelzebu
 */
public class CoinsAPI {

    private static final Core core = Core.getInstance();
    private static final DecimalFormat DF = new DecimalFormat("#.#");

    /**
     * Get the coins of a Player by his name.
     *
     * @param player Player to get the coins.
     * @return
     */
    public static Double getCoins(String player) {
        return core.getDatabase().getCoins(player);
    }

    /**
     * Get the coins String of a Offline Player.
     *
     * @param p Player to get the coins string.
     * @return
     */
    public static String getCoinsString(String p) {
        if (p != null) {
            double coins = getCoins(p);
            return (DF.format(coins));
        } else {
            return "Player can't be null";
        }
    }

    /**
     * Add coins to a Online Player.
     *
     * @param p The player to add the coins.
     * @param coins The coins to add.
     * @deprecated This should not be used.
     * @see #addCoins(org.bukkit.entity.Player, java.lang.Double,
     * java.lang.Boolean)
     */
    @Deprecated
    public static void addCoins(String p, Double coins) {
        addCoins(p, coins, false);
    }

    /**
     * Add coins to a Online Player.
     *
     * @param p The player to add the coins.
     * @param coins The coins to add.
     * @param multiply Multiply coins if there are any active multipliers
     */
    public static void addCoins(String p, Double coins, Boolean multiply) {
        core.getDatabase().addCoins(p, coins, multiply);
    }

    /**
     * Take coins of a Online Player.
     *
     * @param p
     * @param coins
     */
    public static void takeCoins(String p, Double coins) {
        core.getDatabase().takeCoins(p, coins);
    }

    /**
     * Reset the coins of a Online Player.
     *
     * @param p
     */
    public static void resetCoins(String p) {
        core.getDatabase().resetCoins(p);
    }

    /**
     * Set the coins of a Online Player.
     *
     * @param p
     * @param coins
     */
    public static void setCoins(String p, Double coins) {
        core.getDatabase().setCoins(p, coins);
    }

    /**
     * Get if a player with the specified name exists in the database. Is not
     * recommended check a player by his name because it can change.
     *
     * @param player The name to look for in the database.
     * @return true if the player exists in the database or false if not.
     */
    public static boolean isindb(String player) {
        return core.getDatabase().isindb(player);
    }

    /**
     * Get if a player with the specified uuid exists in the database.
     *
     * @param uuid The uuid to look for in the database.
     * @return true if the player exists in the database or false if not.
     */
    public static boolean isindb(UUID uuid) {
        return core.getDatabase().isindb(uuid);
    }

    /**
     * Get the top players in coins data.
     *
     * @param top The lenght of the top list, for example 5 will get a max of 5
     * users for the top.
     * @return The ordered top list.
     */
    public static List<String> getTop(int top) {
        return core.getDatabase().getTop(top);
    }

    /**
     * Register a player in the database.
     *
     * @param p The player to register.
     * @param uuid The uuid of the player
     */
    public static void createPlayer(String p, UUID uuid) {
        core.getDatabase().createPlayer(p, uuid);
    }

    /**
     * Get and modify information about multipliers for a specified server.
     *
     * @param server The server to modify and get info about multiplier.
     * @return
     */
    public static Multiplier getMultiplier(String server) {
        return new Multiplier(server);
    }

    public static Multiplier getMultiplier() {
        return new Multiplier();
    }
}
