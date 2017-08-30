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
import java.util.UUID;

/**
 *
 * @author Beelzebu
 */
public class SQLite implements Database {

    @Override
    public Double getCoins(String player) {
        throw new UnsupportedOperationException("getCoins is not finished yet.");
    }

    @Override
    public void addCoins(String player, Double coins, Boolean multiply) {
        throw new UnsupportedOperationException("addCoins is not finished yet.");
    }

    @Override
    public void takeCoins(String player, Double coins) {
        throw new UnsupportedOperationException("takeCoins is not finished yet.");
    }

    @Override
    public void resetCoins(String player) {
        throw new UnsupportedOperationException("resetCoins is not finished yet.");
    }

    @Override
    public void setCoins(String player, Double coins) {
        throw new UnsupportedOperationException("setCoins is not finished yet.");
    }

    @Override
    public boolean isindb(String player) {
        throw new UnsupportedOperationException("isindb is not finished yet.");
    }

    @Override
    public Double getCoins(UUID player) {
        throw new UnsupportedOperationException("getCoins is not finished yet.");
    }

    @Override
    public void addCoins(UUID player, Double coins, Boolean multiply) {
        throw new UnsupportedOperationException("addCoins is not finished yet.");
    }

    @Override
    public void takeCoins(UUID player, Double coins) {
        throw new UnsupportedOperationException("takeCoins is not finished yet.");
    }

    @Override
    public void resetCoins(UUID player) {
        throw new UnsupportedOperationException("resetCoins is not finished yet.");
    }

    @Override
    public void setCoins(UUID player, Double coins) {
        throw new UnsupportedOperationException("setCoins is not finished yet.");
    }

    @Override
    public boolean isindb(UUID player) {
        throw new UnsupportedOperationException("isindb is not finished yet.");
    }

    @Override
    public List<String> getTop(int top) {
        throw new UnsupportedOperationException("getTop is not finished yet.");
    }

    @Override
    public void createPlayer(String player, UUID uuid) {
        throw new UnsupportedOperationException("createPlayer is not finished yet.");
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException("getConnection is not finished yet.");
    }

}
