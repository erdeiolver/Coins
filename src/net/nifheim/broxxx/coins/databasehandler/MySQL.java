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
package net.nifheim.broxxx.coins.databasehandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class MySQL {

    private final Main plugin = Main.getInstance();

    private final String host = plugin.getConfig().getString("MySQL.Host");
    private final int port = plugin.getConfig().getInt("MySQL.Port");
    private final String name = plugin.getConfig().getString("MySQL.Database");
    private final String user = plugin.getConfig().getString("MySQL.User");
    private final String passwd = plugin.getConfig().getString("MySQL.Password");
    private final String prefix = plugin.getConfig().getString("MySQL.Prefix");
    private final int checkdb = plugin.getConfig().getInt("MySQL.Connection Interval") * 1200;
    private static Connection c;
    private String player;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public static Connection getConnection() {
        return c;
    }

    public void SQLConnection() {
        try {
            Connect();

            if (!MySQL.getConnection().isClosed()) {
                plugin.log("Plugin conected sucesful to the MySQL.");
            }
        } catch (SQLException e) {
            Logger.getLogger(MySQL.class.getName()).log(Level.WARNING, "Something was wrong with the connection, the error code is: " + e.getErrorCode(), e);
            Bukkit.getScheduler().cancelTasks(Main.getInstance());
            plugin.log("Can't connect to the database, disabling plugin...");
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            plugin.log("Checking the database connection ...");
            if (MySQL.getConnection() == null) {
                plugin.log("The database connection is null, reconnecting ...");
                Reconnect();
            } else {
                plugin.log("The connection to the database is still active.");
            }
        }, 0L, checkdb);
    }

    private void Connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }
        c = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?autoReconnect=true", user, passwd);
        String createData
                = "CREATE TABLE IF NOT EXISTS `" + prefix + "Data`"
                + "(`uuid` VARCHAR(50) NOT NULL,"
                + "`nick` VARCHAR(50) NOT NULL,"
                + "`balance` DOUBLE NOT NULL,"
                + "`lastlogin` LONG NOT NULL,"
                + "PRIMARY KEY (`uuid`));";
        String createMultiplier
                = "CREATE TABLE IF NOT EXISTS `" + prefix + "Multipliers`"
                + "(`id` INT NOT NULL AUTO_INCREMENT,"
                + "`uuid` VARCHAR(50) NOT NULL,"
                + "`multiplier` INT,"
                + "`queue` INT AUTO_INCREMENT,"
                + "`minutes` INT,"
                + "`starttime` LONG,"
                + "`endtime` LONG,"
                + "`server` VARCHAR(50),"
                + "`enabled` BOOLEAN,"
                + "PRIMARY KEY (`id`));";

        Statement update = c.createStatement();
        update.execute(createData);
        update.execute(createMultiplier);
    }

    public void Reconnect() {
        Disconnect();

        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            try {
                Connect();
            } catch (SQLException ex) {
            }
        }, 20L);
    }

    public void Disconnect() {
        try {
            if (!c.isClosed()) {
                c.close();
            }
        } catch (SQLException e) {
        }
    }

    private String player(OfflinePlayer p) {
        player = p.getUniqueId().toString();
        return player;
    }

    // Query methods
    public Double getCoins(Player p) throws SQLException {
        String localplayer = player(p);

        ResultSet res = res("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");

        if (res.getString("uuid") != null) {
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    public Double getCoinsOffline(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double coins = res.getDouble("balance");

            return coins;
        }
        return 0.0;
    }

    public String getCoinsStringOffline(OfflinePlayer p) throws SQLException {
        double coins = getCoinsOffline(p);
        if (coins == 0) {
            return "0";
        } else {
            return (df.format(coins));
        }
    }

    public void addCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid ='" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (oldCoins + coins) + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void addCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid ='" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double oldCoins = res.getDouble("balance");

            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (oldCoins + coins) + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void takeCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();
        double beforeCoins = res.getDouble("balance");
        if (res.getString("uuid") != null) {

            if (beforeCoins - coins < 0) {
                if (!plugin.getConfig().getBoolean("Allow Negative")) {
                    Statement update = c.createStatement();
                    update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + localplayer + "';");
            }
        }
    }

    public void takeCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            double beforeCoins = res.getDouble("balance");

            if (beforeCoins - coins < 0) {
                if (!plugin.getConfig().getBoolean("Allow Negative")) {
                    return;
                }
                if (plugin.getConfig().getBoolean("Allow Negative")) {
                    Statement bypassUpdate = c.createStatement();
                    bypassUpdate.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + localplayer + "';");
                }
            } else if (beforeCoins == coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
            } else if (beforeCoins > coins) {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + (beforeCoins - coins) + " WHERE uuid = '" + localplayer + "';");
            }
        }
    }

    public void resetCoins(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + 0 + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void resetCoinsOffline(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = 0 WHERE uuid = '" + localplayer + "';");
        }
    }

    public void setCoins(Player p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + coins + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public void setCoinsOffline(OfflinePlayer p, double coins) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        if (res.getString("uuid") != null) {
            Statement update = c.createStatement();
            update.executeUpdate("UPDATE " + prefix + "Data SET balance = " + coins + " WHERE uuid = '" + localplayer + "';");
        }
    }

    public boolean isindb(OfflinePlayer p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();

        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
        res.next();

        return res.getString("uuid") != null;
    }

    public List<String> getTop(int top) {
        List<String> toplist = new ArrayList<>();
        try {
            int i = 0;
            Statement check = c.createStatement();
            ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Data ORDER BY balance DESC LIMIT " + top + ";");
            while (res.next()) {
                i++;
                String playername = res.getString("nick");
                toplist.add(playername);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.WARNING, "Can''''t execute the query to select the data for the top list in the database, the error code is: {0}", ex.getErrorCode());
        }
        return toplist;
    }

    public void createPlayer(Player p) throws SQLException {
        String localplayer = player(p);

        Statement check = c.createStatement();
        if (plugin.getConfig().getBoolean("Online Mode")) {
            ResultSet res = check.executeQuery("SELECT uuid FROM " + prefix + "Data WHERE uuid = '" + localplayer + "';");
            if (!res.next()) {
                Statement update = c.createStatement();
                update.executeUpdate("INSERT INTO " + prefix + "Data VALUES ('" + localplayer + "', '" + p.getName() + "', 0.0, " + System.currentTimeMillis() + ");");
            } else {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET nick = '" + p.getName() + "', lastlogin = " + System.currentTimeMillis() + " WHERE uuid = '" + localplayer + "';");
            }
        } else {
            ResultSet res = check.executeQuery("SELECT nick FROM " + prefix + "Data WHERE nick = '" + p.getName() + "';");
            if (!res.next()) {
                Statement update = c.createStatement();
                update.executeUpdate("INSERT INTO " + prefix + "Data VALUES ('" + localplayer + "', '" + p.getName() + "', 0.0, " + System.currentTimeMillis() + ");");
            } else {
                Statement update = c.createStatement();
                update.executeUpdate("UPDATE " + prefix + "Data SET uuid = '" + localplayer + "', lastlogin = " + System.currentTimeMillis() + " WHERE nick = '" + p.getName() + "';");
            }
        }
    }

    public Long getMultiplierTime() throws SQLException {
        Statement check = c.createStatement();
        ResultSet res = check.executeQuery("SELECT * FROM " + prefix + "Multipliers WHERE server = '" + plugin.getConfig().getString("Multipliers.Server") + "' AND enabled = true;");
        res.next();

        return (res.getLong("starttime") - res.getLong("endtime"));
    }

    public void createMultiplier(Player p, Integer multiplier, Integer minutes) throws SQLException {
        String localplayer = player(p);
        c.createStatement().executeUpdate("INSERT INTO " + prefix + "Multipliers VALUES(NULL, '" + localplayer + "', " + multiplier + ", -1, " + minutes + ", 0, 0, " + "'" + plugin.getConfig().getString("Multipliers.Server") + "'" + ", false);");
    }

    public List<Integer> getMultipliers(Player p) throws SQLException {
        String localplayer = player(p);
        ResultSet res = c.createStatement().executeQuery("SELECT * FROM " + prefix + "Multipliers WHERE uuid = '" + localplayer + "';");
        List<Integer> list = new ArrayList();
        if (res.next()) {
            while (res.next()) {
                list.add(res.getInt("id"));
            }
        }
        return list;
    }

    public void useMultiplier(Player p, Integer id, Integer multiplier, Integer minutes) throws SQLException {
        String localplayer = player(p);
        Long endtime = System.currentTimeMillis() + (minutes * 60000);
        Boolean enabled = c.createStatement().executeQuery("").getBoolean("enabled");
        if (!enabled) {
            ResultSet res = c.createStatement().executeQuery("SELECT * FROM " + prefix + "Multipliers WHERE minutes = " + minutes + " AND multiplier = " + multiplier + " AND uuid = '" + localplayer + "';");
            c.createStatement().executeUpdate("UPDATE " + prefix + "Multipliers SET starttime = " + System.currentTimeMillis() + ", endtime = " + endtime + ", enabled = true WHERE id = " + id + ";");
        }
    }

    private ResultSet res(String query) {
        try {
            Statement check = c.createStatement();
            ResultSet res = check.executeQuery(query);
            res.next();

            return res;
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
