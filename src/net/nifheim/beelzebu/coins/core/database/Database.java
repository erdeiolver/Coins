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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.nifheim.beelzebu.coins.core.Core;

/**
 *
 * @author Beelzebu
 */
public interface Database {

    String prefix = Core.getInstance().isMySQL() ? Core.getInstance().getConfig().getString("MySQL.Prefix") : "";

    Double getCoins(String player);

    void addCoins(String player, Double coins);

    void takeCoins(String player, Double coins);

    void resetCoins(String player);

    void setCoins(String player, Double coins);

    boolean isindb(String player);

    Double getCoins(UUID player);

    void addCoins(UUID player, Double coins);

    void takeCoins(UUID player, Double coins);

    void resetCoins(UUID player);

    void setCoins(UUID player, Double coins);

    boolean isindb(UUID player);

    @Deprecated
    List<String> getTop(int top);

    Map<String, Double> getTopPlayers(int top);

    void createPlayer(String player, UUID uuid);

    Connection getConnection() throws SQLException;

    String getNick(UUID uuid);

    UUID getUUID(String nick);

    void shutdown();

    public enum SQLQuery {
        SEARCH_USER_ONLINE("SELECT uuid FROM `" + prefix + "Data` WHERE uuid = ?;"),
        SEARCH_USER_OFFLINE("SELECT nick FROM `" + prefix + "Data` WHERE nick = ?;"),
        CREATE_USER("INSERT INTO `" + prefix + "Data` (`uuid`, `name`, `balance`, `lastlogin`) VALUES (?, ?, ?, ?);"),
        UPDATE_USER_ONLINE("UPDATE `" + prefix + "Data` SET name = ?, lastlogin = ? WHERE uuid = ?;"),
        UPDATE_USER_OFFLINE("UPDATE `" + prefix + "Data` SET uuid = ?, lastlogin = ? WHERE name = ?;"),
        UPDATE_COINS_ONLINE("UPDATE `" + prefix + "Data` SET balance = ? WHERE uuid = ?;"),
        UPDATE_COINS_OFFLINE("UPDATE `" + prefix + "Data` SET balance = ? WHERE nick = ?;");

        private final String name;

        SQLQuery(String query) {
            name = query;
        }
    }
}
