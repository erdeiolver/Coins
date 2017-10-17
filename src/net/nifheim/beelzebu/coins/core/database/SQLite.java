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
import java.util.List;
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
    public synchronized void createPlayer(String player, UUID uuid) {
        try {
            core.debug("A database connection was opened.");
            ResultSet res;
            core.debug("Trying to create or update data.");
            if (core.getConfig().getBoolean("Online Mode")) {
                core.debug("Preparing to create or update an entry for online mode.");
                res = getConnection().prepareStatement("SELECT uuid FROM Data WHERE uuid = '" + uuid + "';").executeQuery();
                if (!res.next()) {
                    getConnection().prepareStatement("INSERT INTO Data VALUES ('" + uuid.toString() + "', '" + player + "', 0.0, " + System.currentTimeMillis() + ");").execute();
                    core.debug("An entry in the database was created for: " + player);
                } else {
                    getConnection().prepareStatement("UPDATE Data SET nick = '" + player + "', lastlogin = " + System.currentTimeMillis() + " WHERE uuid = '" + uuid + "';").execute();
                    core.debug("The nickname of: " + player + " was updated in the database.");
                }
            } else {
                core.debug("Preparing to create or update an entry for offline mode.");
                res = getConnection().prepareStatement("SELECT nick FROM Data WHERE nick = '" + player + "';").executeQuery();
                if (!res.next()) {
                    getConnection().prepareStatement("INSERT INTO Data VALUES ('" + uuid.toString() + "', '" + player + "', 0.0, " + System.currentTimeMillis() + ");").execute();
                    core.debug("An entry in the database was created for: " + player);
                } else {
                    getConnection().prepareStatement("UPDATE Data SET uuid = '" + uuid.toString() + "', lastlogin = " + System.currentTimeMillis() + " WHERE nick = '" + player + "';").execute();
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
            ResultSet res = getConnection().prepareStatement("SELECT * FROM Data WHERE nick = '" + player + "';").executeQuery();
            if (res.next() && res.getString("uuid") != null) {
                double coins = res.getDouble("balance");
                CacheManager.updateCoins(core.getUUID(player), coins);
                return coins;
            } else {
                CacheManager.updateCoins(core.getUUID(player), 0D);
                createPlayer(player, core.getUUID(player));
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
                getConnection().prepareStatement("UPDATE Data SET balance = " + (oldCoins + coins) + " WHERE nick = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), oldCoins + coins);
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
                getConnection().prepareStatement("UPDATE Data SET balance = 0 WHERE nick = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), 0D);
            } else if (beforeCoins > coins) {
                getConnection().prepareStatement("UPDATE Data SET balance = " + (beforeCoins - coins) + " WHERE nick = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), (beforeCoins - coins));
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred taking coins to the player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void resetCoins(String player) {
        try {
            if (isindb(player) && getCoins(player) >= 0) {
                getConnection().prepareStatement("UPDATE Data SET balance = " + core.getConfig().getDouble("General.Starting Coins") + " WHERE nick = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), core.getConfig().getDouble("General.Starting Coins"));
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
            if (isindb(player) && getCoins(player) >= 0) {
                getConnection().prepareStatement("UPDATE Data SET balance = " + coins + " WHERE nick = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(core.getUUID(player), coins);
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
            ResultSet res = getConnection().prepareStatement("SELECT * FROM Data WHERE nick = '" + player + "';").executeQuery();
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
            ResultSet res = getConnection().prepareStatement("SELECT * FROM Data WHERE uuid = '" + player + "';").executeQuery();
            if (res.next() && res.getString("uuid") != null) {
                double coins = res.getDouble("balance");
                CacheManager.updateCoins(player, coins);
                return coins;
            } else {
                CacheManager.updateCoins(player, 0D);
                createPlayer(core.getNick(player), player);
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
                getConnection().prepareStatement("UPDATE Data SET balance = " + (oldCoins + coins) + " WHERE uuid = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(player, oldCoins + coins);
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
                getConnection().prepareStatement("UPDATE Data SET balance = 0 WHERE uuid = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(player, 0D);
            } else if (beforeCoins > coins) {
                getConnection().prepareStatement("UPDATE Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(player, beforeCoins - coins);
            }
        } catch (SQLException ex) {
            core.log("&cAn internal error has occurred taking coins to the player: " + core.getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void resetCoins(UUID player) {
        try {
            if (isindb(player) && getCoins(player) >= 0) {
                getConnection().prepareStatement("UPDATE Data SET balance = " + core.getConfig().getDouble("General.Starting Coins") + " WHERE uuid = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(player, core.getConfig().getDouble("General.Starting Coins"));
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
            if (isindb(player) && getCoins(player) >= 0) {
                getConnection().prepareStatement("UPDATE Data SET balance = " + coins + " WHERE uuid = '" + player + "';").executeUpdate();
                CacheManager.updateCoins(player, coins);
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
            ResultSet res = getConnection().prepareStatement("SELECT * FROM Data WHERE uuid = '" + player + "';").executeQuery();
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
            ResultSet res = getConnection().prepareStatement("SELECT * FROM Data ORDER BY balance DESC LIMIT " + top + ";").executeQuery();
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
    public String getNick(UUID uuid) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM Data WHERE uuid = '" + uuid + "';").executeQuery();
                if (res.next()) {
                    return res.getString("nick");
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
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
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM Data WHERE nick = '" + nick + "';").executeQuery();
                if (res.next()) {
                    return UUID.fromString(res.getString("uuid"));
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.log("Something was wrong getting the uuid for the nick '" + nick + "'");
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return null;
    }

    @Override
    public void shutdown() {
        // nothing (?)
    }
}
