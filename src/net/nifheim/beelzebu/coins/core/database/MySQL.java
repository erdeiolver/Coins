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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.utils.CacheManager;

/**
 *
 * @author Beelzebu
 */
public class MySQL implements Database {

    private final Core core;
    private static HikariDataSource ds;

    private final String host;
    private final String port;
    private final String name;
    private final String user;
    private final String passwd;
    private final String prefix;

    public MySQL(Core c) {
        core = c;
        host = core.getConfig().getString("MySQL.Host");
        port = core.getConfig().getString("MySQL.Port");
        name = core.getConfig().getString("MySQL.Database");
        user = core.getConfig().getString("MySQL.User");
        passwd = core.getConfig().getString("MySQL.Password");
        prefix = core.getConfig().getString("MySQL.Prefix");
        SQLConnection();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private void SQLConnection() {
        Connect();
        updateDatabase();
        core.getMethods().runAsync(() -> {
            core.getMethods().log("Checking the database connection ...");
            try (Connection c = getConnection()) {
                try {
                    if (c == null || c.isClosed()) {
                        core.getMethods().log("The database connection is null, check your MySQL settings!");
                        if (ds != null) {
                            ds.close();
                        }
                        Connect();
                    } else {
                        core.getMethods().log("The connection to the database is still active.");
                    }
                } finally {
                    c.close();
                }
            } catch (SQLException ex) {
                core.getMethods().log("The database connection is null, check your MySQL settings!");
                core.debug("The error code is: " + ex.getErrorCode());
                core.debug(ex.getMessage());
            }
        }, (long) core.getConfig().getInt("MySQL.Connection Interval"));
    }

    private void Connect() {
        HikariConfig hc = new HikariConfig();
        hc.setPoolName("Coins MySQL");
        hc.setDriverClassName("com.mysql.jdbc.Driver");
        hc.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + name + "?autoReconnect=true");
        hc.addDataSourceProperty("cachePrepStmts", "true");
        hc.addDataSourceProperty("prepStmtCacheSize", "250");
        hc.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hc.addDataSourceProperty("characterEncoding", "utf8");
        hc.addDataSourceProperty("encoding", "UTF-8");
        hc.addDataSourceProperty("useUnicode", "true");
        hc.setUsername(user);
        hc.setPassword(passwd);
        hc.setMaxLifetime(180000L);
        hc.setMinimumIdle(2);
        hc.setIdleTimeout(30000);
        hc.setConnectionTimeout(30000);
        hc.setMaximumPoolSize(8);
        hc.validate();
        ds = new HikariDataSource(hc);

        try (Connection c = getConnection()) {
            try {
                if (!c.isClosed()) {
                    core.getMethods().log("Plugin conected sucesful to the MySQL.");
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.debug(String.format("Something was wrong with the connection, the error code is: %s", ex.getErrorCode()));
            core.getMethods().log("Can't connect to the database...");
            core.getMethods().log("Check your settings and restart the server.");
        }
    }

    public synchronized void updateDatabase() {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            core.debug("A database connection was opened.");
            try {
                DatabaseMetaData md = c.getMetaData();
                String Data
                        = "CREATE TABLE IF NOT EXISTS `" + prefix + "Data`"
                        + "(`uuid` VARCHAR(50) NOT NULL,"
                        + "`nick` VARCHAR(50) NOT NULL,"
                        + "`balance` DOUBLE NOT NULL,"
                        + "`lastlogin` LONG NOT NULL,"
                        + "PRIMARY KEY (`uuid`));";
                String Multiplier = "CREATE TABLE IF NOT EXISTS `" + prefix + "Multipliers`"
                        + "(`id` INT NOT NULL AUTO_INCREMENT,"
                        + "`uuid` VARCHAR(50) NOT NULL,"
                        + "`multiplier` INT,"
                        + "`queue` INT,"
                        + "`minutes` INT,"
                        + "`endtime` LONG,"
                        + "`server` VARCHAR(50),"
                        + "`enabled` BOOLEAN,"
                        + "PRIMARY KEY (`id`));";
                st.executeUpdate(Data);
                core.debug("The data table was updated.");
                st.executeUpdate(Multiplier);
                if (!isColumnMissing(md, "Multipliers", "starttime")) {
                    st.executeUpdate("ALTER TABLE `" + prefix + "Multipliers` DROP COLUMN starttime;");
                }
                core.debug("The multipliers table was updated");
                if (core.getConfig().getBoolean("General.Purge.Enabled", true)) {
                    st.executeUpdate("DELETE FROM " + prefix + "Data WHERE lastlogin < " + (System.currentTimeMillis() - (core.getConfig().getInt("General.Purge.Days") * 86400000)) + ";");
                    core.debug("Inactive users were removed from the database.");
                }
            } finally {
                st.close();
                c.close();
                core.debug("The connection was closed.");
            }
        } catch (SQLException ex) {
            core.getMethods().log("Something was wrong creating the default databases. Please check the debug log.");
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public synchronized void createPlayer(String player, UUID uuid) {
        try (Connection c = getConnection()) {
            core.debug("A database connection was opened.");
            ResultSet res = null;
            try {
                core.debug("Trying to create or update data.");
                if (core.getConfig().getBoolean("Online Mode")) {
                    core.debug("Preparing to create or update an entry for online mode.");
                    res = c.prepareStatement("SELECT uuid FROM " + prefix + "Data WHERE uuid = '" + uuid + "';").executeQuery();
                    if (!res.next()) {
                        c.prepareStatement("INSERT INTO " + prefix + "Data VALUES ('" + uuid.toString() + "', '" + player + "', 0.0, " + System.currentTimeMillis() + ");").execute();
                        core.debug("An entry in the database was created for: " + player);
                    } else {
                        c.prepareStatement("UPDATE " + prefix + "Data SET nick = '" + player + "', lastlogin = " + System.currentTimeMillis() + " WHERE uuid = '" + uuid + "';").execute();
                        core.debug("The nickname of: " + player + " was updated in the database.");
                    }
                } else {
                    core.debug("Preparing to create or update an entry for offline mode.");
                    res = c.prepareStatement("SELECT nick FROM " + prefix + "Data WHERE nick = '" + player + "';").executeQuery();
                    if (!res.next()) {
                        c.prepareStatement("INSERT INTO " + prefix + "Data VALUES ('" + uuid.toString() + "', '" + player + "', 0.0, " + System.currentTimeMillis() + ");").execute();
                        core.debug("An entry in the database was created for: " + player);
                    } else {
                        c.prepareStatement("UPDATE " + prefix + "Data SET uuid = '" + uuid.toString() + "', lastlogin = " + System.currentTimeMillis() + " WHERE nick = '" + player + "';").execute();
                        core.debug("The uuid of: " + core.getNick(uuid) + " was updated in the database.");
                    }
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
                core.debug("The connection was closed.");
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred creating the player: " + player + " in the database.");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public Double getCoins(String player) {
        if (CacheManager.getData().containsKey(core.getUUID(player))) {
            return CacheManager.getData().get(core.getUUID(player));
        }
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Data WHERE nick = '" + player + "';").executeQuery();
                if (res.next() && res.getString("uuid") != null) {
                    double coins = res.getDouble("balance");
                    if (!CacheManager.getData().containsKey(core.getUUID(player))) {
                        CacheManager.getData().put(core.getUUID(player), coins);
                    }
                    return coins;
                } else {
                    if (!CacheManager.getData().containsKey(core.getUUID(player))) {
                        CacheManager.getData().put(core.getUUID(player), 0D);
                    }
                    createPlayer(player, core.getUUID(player));
                    return 0D;
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred creating the data for player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return 0D;
    }

    @Override
    public void addCoins(String player, Double coins, Boolean multiply) {
        try (Connection c = getConnection()) {
            try {
                if (isindb(player) && getCoins(player) >= 0) {
                    if (multiply) {
                        coins = coins * CoinsAPI.getMultiplier().getAmount();
                    }
                    double oldCoins = getCoins(player);
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + (oldCoins + coins) + " WHERE nick = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(core.getUUID(player), oldCoins + coins);
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred adding coins to the player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
        }
    }

    @Override
    public void takeCoins(String player, Double coins) {
        try (Connection c = getConnection()) {
            try {
                double beforeCoins = getCoins(player);
                if (beforeCoins - coins < 0) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = 0 WHERE nick = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(core.getUUID(player), 0D);
                } else if (beforeCoins == coins) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = 0 WHERE nick = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(core.getUUID(player), 0D);
                } else if (beforeCoins > coins) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE nick = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(core.getUUID(player), (beforeCoins - coins));
                }

            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred taking coins to the player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
        }
    }

    @Override
    public void resetCoins(String player) {
        try (Connection c = getConnection()) {
            try {
                if (isindb(player) && getCoins(player) >= 0) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + core.getConfig().getDouble("General.Starting Coins") + " WHERE nick = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(core.getUUID(player), core.getConfig().getDouble("General.Starting Coins"));
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred reseting the coins of player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
        }
    }

    @Override
    public void setCoins(String player, Double coins) {
        try (Connection c = getConnection()) {
            try {
                if (isindb(player) && getCoins(player) >= 0) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + coins + " WHERE nick = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(core.getUUID(player), coins);
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred setting the coins of player: " + player);
            core.debug("&cThe error code is: " + ex.getErrorCode());
        }
    }

    @Override
    public boolean isindb(String player) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Data WHERE nick = '" + player + "';").executeQuery();
                if (res.next()) {
                    return res.getString("nick") != null;
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred cheking if the player: " + player + " exists in the database.");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return false;
    }

    @Override
    public Double getCoins(UUID player) {
        if (CacheManager.getData().containsKey(player)) {
            return CacheManager.getData().get(core.getUUID(player));
        }
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Data WHERE uuid = '" + player + "';").executeQuery();
                if (res.next() && res.getString("uuid") != null) {
                    double coins = res.getDouble("balance");
                    if (!CacheManager.getData().containsKey(player)) {
                        CacheManager.getData().put(player, coins);
                    }
                    return coins;
                } else {
                    if (!CacheManager.getData().containsKey(core.getUUID(player))) {
                        CacheManager.getData().put(core.getUUID(player), 0D);
                    }
                    createPlayer(core.getNick(player), player);
                    return 0D;
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred creating the data for player: " + core.getMethods().getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return 0D;
    }

    @Override
    public void addCoins(UUID player, Double coins, Boolean multiply) {
        try (Connection c = getConnection()) {
            try {
                if (isindb(player) && getCoins(player) >= 0) {
                    if (multiply) {
                        coins = coins * CoinsAPI.getMultiplier().getAmount();
                    }
                    double oldCoins = getCoins(player);
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + (oldCoins + coins) + " WHERE uuid = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(player, oldCoins + coins);
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred adding coins to the player: " + core.getMethods().getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    @Override
    public void takeCoins(UUID player, Double coins) {
        try (Connection c = getConnection()) {
            try {
                double beforeCoins = getCoins(player);
                if (beforeCoins - coins < 0) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(player, 0D);
                } else if (beforeCoins == coins) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(player, 0D);
                } else if (beforeCoins > coins) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(player, beforeCoins - coins);
                }

            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred taking coins to the player: " + core.getMethods().getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
        }
    }

    @Override
    public void resetCoins(UUID player) {
        try (Connection c = getConnection()) {
            try {
                if (isindb(player) && getCoins(player) >= 0) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + core.getConfig().getDouble("General.Starting Coins") + " WHERE uuid = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(player, core.getConfig().getDouble("General.Starting Coins"));
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred reseting the coins of player: " + core.getMethods().getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
        }
    }

    @Override
    public void setCoins(UUID player, Double coins) {
        try (Connection c = getConnection()) {
            try {
                if (isindb(player) && getCoins(player) >= 0) {
                    c.prepareStatement("UPDATE " + prefix + "Data SET balance = " + coins + " WHERE uuid = '" + player + "';").executeUpdate();
                    CacheManager.updateCoins(player, coins);
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred setting the coins of player: " + core.getMethods().getNick(player));
            core.debug("&cThe error code is: " + ex.getErrorCode());
        }
    }

    @Override
    public boolean isindb(UUID player) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Data WHERE uuid = '" + player + "';").executeQuery();
                if (res.next()) {
                    return res.getString("nick") != null;
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred cheking if the player: " + player + " exists in the database.");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return false;
    }

    @Override
    public List<String> getTop(int top) {
        List<String> toplist = new ArrayList<>();
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Data ORDER BY balance DESC LIMIT " + top + ";").executeQuery();
                while (res.next()) {
                    String playername = res.getString("nick");
                    int coins = (int) res.getDouble("balance");
                    toplist.add(playername + ", " + coins);
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cAn internal error has occurred generating the toplist");
            core.debug("&cThe error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return toplist;
    }

    private boolean isColumnMissing(DatabaseMetaData metaData, String table, String column) throws SQLException {
        try (ResultSet res = metaData.getColumns(null, null, prefix + table, column)) {
            return !res.next();
        }
    }
}
