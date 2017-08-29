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
package net.nifheim.beelzebu.coins.core.utils;

import java.util.List;

/**
 *
 * @author jaime
 */
public abstract class MessagesManager {

    private final String lang;

    public MessagesManager(String lang) {
        this.lang = lang;
    }

    public abstract Object get(String path);

    public abstract String getString(String path);

    public abstract List<String> getStringList(String path);

    public abstract Boolean getBoolean(String path);

    public abstract Integer getInt(String path);

    public abstract Double getDouble(String path);

    public abstract Object get(String path, Object def);

    public abstract String getString(String path, String def);

    public abstract List<String> getStringList(String path, List<String> def);

    public abstract Boolean getBoolean(String path, boolean def);

    public abstract Integer getInt(String path, int def);

    public abstract Double getDouble(String path, double def);

    public abstract void set(String path, Object value);

    public abstract Object getConfigurationSection(String path);

}
