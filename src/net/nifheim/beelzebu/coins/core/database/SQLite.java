/**
 * This file is part of Coins
 *
 * Copyright (C) 2017 Beelzebu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.nifheim.beelzebu.coins.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.utils.CacheManager;

/**
 * Manages the SQLite database.
 *
 * @version 1.0.0
 * @author Beelzebu
 * @since 1.8.1-BETA
 */
public class SQLite implements Database {

    private final Core core;
    private static Connection connection;

    public SQLite(Core c) {
        core = c;
        updateDatabase();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return (connection == null ? true : connection.isClosed()) ? connection = DriverManager.getConnection("jdbc:sqlite:" + core.getDataFolder() + "/database.db") : connection;
    }

    private void updateDatabase() {
        try {
            core.debug("A database connection was opened.");
            String Data
                    = "CREATE TABLE IF NOT EXISTS `Data`"
                    + "(`uuid` VARCHAR(50),"
                    + "`nick` VARCHAR(50),"
                    + "`balance` DOUBLE,"
                    + "`lastlogin` LONG);";
            String Multiplier = "CREATE TABLE IF NOT EXISTS `Multipliers`"
                    + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "`uuid` VARCHAR(50),"
                    + "`multiplier` INT,"
                    + "`queue` INT,"
                    + "`minutes` INT,"
                    + "`endtime` LONG,"
                    + "`server` VARCHAR(50),"
                    + "`enabled` BOOLEAN);";
            getConnection().createStatement().executeUpdate(Data);
            core.debug("The data table was updated.");
            getConnection().createStatement().executeUpdate(Multiplier);
            core.debug("The multipliers table was updated");
            if (core.getConfig().getBoolean("General.Purge.Enabled", true)) {
                getConnection().createStatement().executeUpdate("DELETE FROM Data WHERE lastlogin < " + (System.currentTimeMillis() - (core.getConfig().getInt("General.Purge.Days") * 86400000)) + ";");
                core.debug("Inactive users were removed from the database.");
            }
        } catch (SQLException ex) {
            core.log("Something was wrong creating the default databases. Please check the debug log.");
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void createPlayer(Connection c, String player, UUID uuid) {
        try {
            core.debug("A database connection was opened.");
            ResultSet res;
            core.debug("Trying to create or update data.");
            if (core.getConfig().getBoolean("Online Mode")) {
                core.debug("Preparing to create or update an entry for online mode.");
                res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_ONLINE, uuid).executeQuery();
                if (!res.next()) {
                    Utils.generatePreparedStatement(getConnection(), SQLQuery.CREATE_USER, uuid, player, 0.0, System.currentTimeMillis()).executeUpdate();
                    core.debug("An entry in the database was created for: " + player);
                } else {
                    Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_USER_ONLINE, player, System.currentTimeMillis(), uuid).executeUpdate();
                    core.debug("The nickname of: " + player + " was updated in the database.");
                }
            } else {
                core.debug("Preparing to create or update an entry for offline mode.");
                res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_OFFLINE, player).executeQuery();
                if (!res.next()) {
                    Utils.generatePreparedStatement(getConnection(), SQLQuery.CREATE_USER, uuid, player, 0.0, System.currentTimeMillis()).executeUpdate();
                    core.debug("An entry in the database was created for: " + player);
                } else {
                    Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_USER_OFFLINE, uuid, System.currentTimeMillis(), player).executeUpdate();
                    core.debug("The uuid of: " + core.getNick(uuid) + " was updated in the database.");
                }
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred creating the player: " + player + " in the database.");
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public Double getCoins(String player) {
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_OFFLINE, player).executeQuery();
            if (res.next() && res.getString("uuid") != null) {
                double coins = res.getDouble("balance");
                CacheManager.updateCoins(core.getUUID(player), coins);
                return coins;
            } else {
                CacheManager.updateCoins(core.getUUID(player), 0D);
                createPlayer(getConnection(), player, core.getUUID(player));
                return 0D;
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred creating the data for player: " + player);
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return 0D;
    }

    @Override
    public void addCoins(String player, Double coins) {
        try {
            if (isindb(player) && getCoins(player) >= 0) {
                double oldCoins = getCoins(player);
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_OFFLINE, oldCoins + coins, player).executeUpdate();
                getCoins(player);
                core.getMethods().callCoinsChangeEvent(core.getUUID(player), oldCoins, oldCoins + coins);
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred adding coins to the player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void takeCoins(String player, Double coins) {
        try {
            double beforeCoins = getCoins(player);
            if (beforeCoins - coins < 0 || beforeCoins == coins) {
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_OFFLINE, 0, player).executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), 0D);
            } else {
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_OFFLINE, beforeCoins - coins, player).executeUpdate();
                getCoins(player);
            }
            core.getMethods().callCoinsChangeEvent(core.getUUID(player), beforeCoins, beforeCoins - coins);
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred taking coins to the player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void resetCoins(String player) {
        try {
            if (isindb(player)) {
                double oldCoins = getCoins(player);
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_OFFLINE, core.getConfig().getDouble("General.Starting Coins", 0), player).executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), core.getConfig().getDouble("General.Starting Coins"));
                core.getMethods().callCoinsChangeEvent(core.getUUID(player), oldCoins, core.getConfig().getDouble("General.Starting Coins"));
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred reseting the coins of player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void setCoins(String player, Double coins) {
        try {
            if (isindb(player)) {
                double oldCoins = getCoins(player);
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_ONLINE, coins, player).executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), coins);
                core.getMethods().callCoinsChangeEvent(core.getUUID(player), oldCoins, coins);
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred setting the coins of player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public boolean isindb(String player) {
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_OFFLINE, player).executeQuery();
            if (res.next()) {
                return res.getString("nick") != null;
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred cheking if the player: " + player + " exists in the database.");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return false;
    }

    @Override
    public Double getCoins(UUID player) {
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_ONLINE, player).executeQuery();
            if (res.next() && res.getString("uuid") != null) {
                double coins = res.getDouble("balance");
                CacheManager.updateCoins(player, coins);
                return coins;
            } else {
                CacheManager.updateCoins(player, 0D);
                createPlayer(getConnection(), core.getNick(player), player);
                return 0D;
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred creating the data for player: " + core.getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return 0D;
    }

    @Override
    public void addCoins(UUID player, Double coins) {
        try {
            if (isindb(player) && getCoins(player) >= 0) {
                double oldCoins = getCoins(player);
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_ONLINE, oldCoins + coins, player).executeUpdate();
                getCoins(player);
                core.getMethods().callCoinsChangeEvent(player, oldCoins, oldCoins + coins);
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred adding coins to the player: " + core.getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void takeCoins(UUID player, Double coins) {
        try {
            double beforeCoins = getCoins(player);
            if (beforeCoins - coins < 0 || beforeCoins == coins) {
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_ONLINE, 0, player).executeUpdate();
                CacheManager.updateCoins(player, 0D);
            } else {
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_ONLINE, beforeCoins - coins, player).executeUpdate();
                getCoins(player);
            }
            core.getMethods().callCoinsChangeEvent(player, beforeCoins, beforeCoins - coins);
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred taking coins to the player: " + core.getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void resetCoins(UUID player) {
        try {
            if (isindb(player)) {
                double oldCoins = getCoins(player);
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_ONLINE, core.getConfig().getDouble("General.Starting Coins", 0), player).executeUpdate();
                CacheManager.updateCoins(player, core.getConfig().getDouble("General.Starting Coins"));
                core.getMethods().callCoinsChangeEvent(player, oldCoins, core.getConfig().getDouble("General.Starting Coins"));
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred reseting the coins of player: " + core.getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void setCoins(UUID player, Double coins) {
        try {
            if (isindb(player)) {
                double oldCoins = getCoins(player);
                Utils.generatePreparedStatement(getConnection(), SQLQuery.UPDATE_COINS_ONLINE, coins, player).executeUpdate();
                CacheManager.updateCoins(player, coins);
                core.getMethods().callCoinsChangeEvent(player, oldCoins, coins);
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred setting the coins of player: " + core.getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public boolean isindb(UUID player) {
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_ONLINE, player).executeQuery();
            if (res.next()) {
                return res.getString("nick") != null;
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred cheking if the player: " + player + " exists in the database.");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return false;
    }

    @Override
    public List<String> getTop(int top) {
        List<String> toplist = new ArrayList<>();
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SELECT_TOP, top).executeQuery();
            while (res.next()) {
                String playername = res.getString("nick");
                int coins = (int) res.getDouble("balance");
                toplist.add(playername + ", " + coins);
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred generating the toplist");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return toplist;
    }

    @Override
    public Map<String, Double> getTopPlayers(int top) {
        Map<String, Double> topplayers = new HashMap<>();
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SELECT_TOP, top).executeQuery();
            while (res.next()) {
                String playername = res.getString("nick");
                double coins = res.getDouble("balance");
                topplayers.put(playername, coins);
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred generating the toplist");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return Utils.sortByValue(topplayers);
    }

    @Override
    public String getNick(UUID uuid) {
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_ONLINE, uuid).executeQuery();
            if (res.next()) {
                return res.getString("nick");
            }
        } catch (SQLException ex) {
            core.log("Something was wrong getting the nick for the uuid '" + uuid + "'");
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return null;
    }

    @Override
    public UUID getUUID(String nick) {
        try {
            ResultSet res = Utils.generatePreparedStatement(getConnection(), SQLQuery.SEARCH_USER_OFFLINE, nick).executeQuery();
            if (res.next()) {
                return UUID.fromString(res.getString("uuid"));
            }
        } catch (SQLException ex) {
            core.log("Something was wrong getting the uuid for the nick '" + nick + "'");
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return null;
    }
}
