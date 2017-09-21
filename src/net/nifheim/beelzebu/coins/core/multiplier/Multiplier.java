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
package net.nifheim.beelzebu.coins.core.multiplier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.nifheim.beelzebu.coins.core.Core;

/**
 * Handle Coin's Multipliers
 *
 * @author Beelzebu
 */
public final class Multiplier {

    private final Core core = Core.getInstance();
    private final String prefix = core.isMySQL() ? core.getConfig().getString("MySQL.prefix") : "";
    private String server = core.getConfig().getString("Multipliers.Server");
    private final String enabler;
    private Boolean enabled = false;
    private Integer amount;
    private Connection getConnection() throws SQLException {
        return core.getDatabase().getConnection();
    }

    public Multiplier() {
        enabler = getEnabler(server);
        enabled = isEnabled(server);
        amount = getAmount(server);
        getMultiplierTime(server);
    }

    public Multiplier(String sv) {
        server = sv;
        enabler = getEnabler(server);
        enabled = isEnabled(server);
        amount = getAmount(server);
        getMultiplierTime(server);
    }

    /**
     * Get the nick of the player who enabled this multiplier.
     *
     * @return The nick of the player.
     */
    public String getEnabler() {
        return enabler;
    }

    /**
     * Return <i>true</i> if the server has a multiplier enabled and
     * <i>false</i> if not.
     *
     * @return
     */
    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * Get the the amount of the multiplier enabled in this server.
     *
     * @return
     */
    public Integer getAmount() {
        return amount;
    }

    /**
     * Create a multiplier for a player with the specified time.
     *
     * @param uuid The player to create the multiplier.
     * @param multiplier The multiplier.
     * @param minutes The time for the multiplier.
     */
    public void createMultiplier(UUID uuid, Integer multiplier, Integer minutes) {
        try (Connection c = getConnection()) {
            try {
                c.prepareStatement("INSERT INTO " + prefix + "Multipliers VALUES(NULL, '" + uuid + "', " + multiplier + ", -1, " + minutes + ", 0, " + "'" + server + "'" + ", false);").executeUpdate();
            } finally {
                c.close();
            }
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
        Long endtime = getMultiplierTime(server);
        String format;
        Long time = -75600000L + getMultiplierTime(server);
        if (endtime > 86400000) {
            format = "%1$td, %1$tH:%1$tM:%1$tS";
        } else if (endtime > 3600000) {
            format = "%1$tH:%1$tM:%1$tS";
        } else {
            format = "%1$tM:%1$tS";
        }
        if (time < 0) {
            return String.format(format, time);
        }
        return "Ninguno :(";
    }

    /**
     * Use the multiplier of a player in the server by the multiplier id.
     *
     * @param uuid Player that has a multiplier.
     * @param id The id of the multiplier.
     * @param type The type of the multiplier.
     */
    public void useMultiplier(UUID uuid, Integer id, MultiplierType type) {
        useMultiplier(uuid, id, server, type);
    }

    /**
     * Get the multipliers of a player in this server.
     * <p>
     * If the server is set to null it shows all the multipliers for this
     * player.
     * </p>
     *
     * @param uuid The player to get the multipliers.
     * @return
     */
    public Set<Integer> getMultipliersFor(UUID uuid) {
        return getMultipliersFor(uuid, server);
    }

    public Long getMultiplierTime(String server) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Multipliers WHERE server = '" + server + "' AND enabled = true;").executeQuery();
                if (res.next()) {
                    Long start = System.currentTimeMillis();
                    Long end = res.getLong("endtime");
                    if ((end - start) > 0) {
                        return (end - start);
                    } else {
                        c.prepareStatement("DELETE FROM " + prefix + "Multipliers WHERE server = '" + server + "' AND enabled = true;").executeUpdate();
                        amount = getAmount(server);
                        enabled = false;
                    }
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong when we're getting the multiplier time for " + server);
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return 0L;
    }

    public void useMultiplier(UUID uuid, Integer id, String server, final MultiplierType type) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                if (!isEnabled()) {
                    res = c.prepareStatement("SELECT * FROM " + prefix + "Multipliers WHERE id = " + id + " AND uuid = '" + uuid + "';").executeQuery();
                    if (res.next()) {
                        Long minutes = res.getLong("minutes");
                        Long endtime = System.currentTimeMillis() + (minutes * 60000);
                        switch (type) {
                            case GLOBAL:
                            case PERSONAL:
                                server = type.toString();
                                break;
                            case SERVER:
                            default:
                                break;
                        }
                        c.prepareStatement("UPDATE " + prefix + "Multipliers SET endtime = " + endtime + ", enabled = true, server = '" + server + "' WHERE id = " + id + ";").executeUpdate();
                        amount = getAmount(server);
                    }
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong when creating a multiplier for " + core.getNick(uuid));
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
    }

    private Set<Integer> getMultipliersFor(UUID uuid, String server) {
        Set<Integer> multipliers = new HashSet<>();
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                String query = "SELECT * FROM " + prefix + "Multipliers WHERE uuid = '" + uuid + "' AND enabled = false";
                if (server != null) {
                    query += " AND server = '" + server + "'";
                }
                res = c.prepareStatement(query + ";").executeQuery();
                while (res.next()) {
                    multipliers.add(res.getInt("id"));
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong when getting the multipliers for " + core.getNick(uuid));
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return multipliers;
    }

    private String getEnabler(String server) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Multipliers WHERE enabled = true AND server = '" + server + "';").executeQuery();
                if (res.next()) {
                    return core.getNick(UUID.fromString(res.getString("uuid")));
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong where getting the enabler for " + server);
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return "";
    }

    private Boolean isEnabled(String server) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Multipliers WHERE enabled = true AND server = '" + server + "';").executeQuery();
                if (res.next()) {
                    return true;
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong where getting if the server " + server + " has a multiplier enabled.");
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex);
        }
        return false;
    }

    private Integer getAmount(String server) {
        try (Connection c = getConnection()) {
            ResultSet res = null;
            try {
                res = c.prepareStatement("SELECT * FROM " + prefix + "Multipliers WHERE enabled = true AND server = '" + server + "';").executeQuery();
                if (res.next()) {
                    return res.getInt("multiplier");
                }
            } finally {
                if (res != null) {
                    res.close();
                }
                c.close();
            }
        } catch (SQLException ex) {
            core.getMethods().log("&cSomething was wrong where getting the multiplier amount for " + server);
            core.debug("The error code is: " + ex.getErrorCode());
            core.debug(ex.getMessage());
        }
        return 1;
    }
}
