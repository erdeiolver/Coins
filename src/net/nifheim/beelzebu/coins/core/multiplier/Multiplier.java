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
package net.nifheim.beelzebu.coins.core.multiplier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.beelzebu.coins.core.Core;

/**
 * Handle Coin's Multipliers
 *
 * @author Beelzebu
 */
public class Multiplier {

    private final Core core;
    private static Connection connection = null;
    private static final String PREFIX = Core.getInstance().getConfig().getString("MySQL.PREFIX");
    private static String SERVER = Core.getInstance().getConfig().getString("Multipliers.Server");
    private final String ENABLER;
    private Boolean ENABLED;
    private Integer AMOUNT;
    private Long TIMELEFT;

    public Multiplier(Core c) {
        core = c;
        ENABLER = getEnabler(SERVER);
        try {
            connection = Core.getInstance().getDatabase().getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Multiplier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Multiplier(Core c, String sv) {
        core = c;
        SERVER = sv;
        ENABLER = getEnabler(SERVER);
        try {
            connection = Core.getInstance().getDatabase().getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Multiplier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create a multiplier for a player with the specified time.
     *
     * @param uuid The player to create the multiplier.
     * @param multiplier The multiplier.
     * @param minutes The time for the multiplier.
     */
    public void createMultiplier(UUID uuid, Integer multiplier, Integer minutes) {
        try {
            connection.createStatement().executeUpdate("INSERT INTO " + PREFIX + "Multipliers VALUES(NULL, '" + uuid + "', " + multiplier + ", -1, " + minutes + ", 0, 0, " + "'" + Core.getInstance().getConfig().getString("Multipliers.Server") + "'" + ", false);");
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong when creating a multiplier for " + core.getNick(uuid));
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    /**
     * Get the active multiplier countdown time formated in HHH:mm:ss
     *
     * @return The multiplier time formated.
     */
    public String getMultiplierTimeFormated() {
        Long endtime = getMultiplierTime(SERVER);
        String format;
        Long time = -75600000L + getMultiplierTime(SERVER);
        if (endtime > 86400000) {
            format = "%1$td, %1$tH:%1$tM:%1$tS";
        } else if (endtime > 3600000) {
            format = "%1$tH:%1$tM:%1$tS";
        } else {
            format = "%1$tM:%1$tS";
        }
        if (time <= 0) {
            return String.format(format, time);
        }
        return "Ninguno :(";
    }

    /**
     * Get the enabled multiplier amount for the Server.
     *
     * @return
     */
    public Integer getMultiplierAmount() {
        return getMultiplierAmount(SERVER);
    }

    /**
     * Use the multiplier of a player in the server by the multiplier id.
     *
     * @param uuid Player that has a multiplier.
     * @param id The id of the multiplier.
     * @param type The type of the multiplier.
     */
    public void useMultiplier(UUID uuid, Integer id, MultiplierType type) {
        useMultiplier(uuid, id, SERVER, type);
    }

    /**
     * Get the multipliers of a player in this server.
     * <p>
     * If the server is set to null it shows all the multipliers for this
     * player</p>
     *
     * @param uuid The player to get the multipliers.
     * @return
     */
    public Set<Integer> getMultipliersFor(UUID uuid) {
        return getMultipliersFor(uuid, SERVER);
    }

    /**
     * Get the enabler of a multiplier for this server.
     *
     * @return
     * @throws NullPointerException if the server doesn't has a multiplier
     * active or the multiplier wasn't enabled by a player.
     */
    public String getEnabler() throws NullPointerException {
        return ENABLER;
    }

    // SQL QUERY //
    public Long getMultiplierTime(String server) {
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + PREFIX + "Multipliers WHERE server = '" + server + "' AND enabled = true;");
            if (res.next()) {
                Long start = System.currentTimeMillis();
                Long end = res.getLong("endtime");
                if ((end - start) > 0) {
                    return (end - start);
                } else {
                    connection.createStatement().executeUpdate("DELETE FROM " + PREFIX + "Multipliers WHERE server = '" + server + "' AND enabled = true;");
                    //plugin.getConfig().set("Multipliers.Amount", 1);
                    //plugin.saveConfig();
                }
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong when we're getting the multiplier time for " + server);
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return 0L;
    }

    public void useMultiplier(UUID uuid, Integer id, String server, final MultiplierType type) {
        try {
            ResultSet res0 = connection.createStatement().executeQuery("SELECT * FROM " + PREFIX + "Multipliers");
            res0.next();
            Boolean enabled = res0.getBoolean("enabled");
            if (!enabled) {
                ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + PREFIX + "Multipliers WHERE id = " + id + " AND uuid = '" + uuid + "';");
                if (res.next()) {
                    Long minutes = res.getLong("minutes");
                    Long endtime = System.currentTimeMillis() + (minutes * 60000);
                    switch (type) {
                        case GLOBAL:
                            server = type.toString();
                            break;
                        case PERSONAL:
                            server = type.toString();
                            break;
                        case SERVER:
                        default:
                            break;
                    }
                    connection.createStatement().executeUpdate("UPDATE " + PREFIX + "Multipliers SET endtime = " + endtime + ", enabled = true, server = '" + server + "' WHERE id = " + id + ";");
                    //plugin.getConfig().set("Multipliers.Amount", res.getInt("multiplier"));
                    //plugin.saveConfig();
                } else {
                    //p.sendMessage(core.getString("Multipliers.No Multipliers"));
                }
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong when creating a multiplier for " + core.getNick(uuid));
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    public Set<Integer> getMultipliersFor(UUID uuid, String server) {
        Set<Integer> multipliers = new HashSet<>();
        try {
            String query = "SELECT * FROM " + PREFIX + "Multipliers WHERE uuid = '" + uuid + "' AND enabled = false";
            if (server != null) {
                query += " AND server = '" + server + "'";
            }
            ResultSet res = connection.createStatement().executeQuery(query + ";");
            while (res.next()) {
                multipliers.add(res.getInt("id"));
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong when getting the multipliers for " + core.getNick(uuid));
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return multipliers;
    }

    private String getEnabler(String server) {
        String enabler = null;
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + PREFIX + "Multipliers WHERE enabled = true AND server = '" + server + "';");
            if (res.next()) {
                enabler = Core.getInstance().getNick(res.getString("uuid"));
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong where getting the enabler for " + server);
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return enabler;
    }

    public int getMultiplierAmount(String server) {
        int amount = 1;
        try {
            ResultSet res = connection.createStatement().executeQuery("SELECT * FROM " + PREFIX + "Multipliers WHERE enabled = true AND server = '" + server + "';");
            if (res.next()) {
                amount = res.getInt("multiplier");
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong where getting the multiplier amount for " + server);
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return amount;
    }
}
