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
package net.nifheim.beelzebu.coins.core.executor;

import java.util.List;

/**
 *
 * @author Beelzebu
 */
public class Executor {

    private final String ID;
    private final String displayName;
    private final double cost;
    private final List<String> commands;

    public Executor(String i, String d, double c, List<String> cmds) {
        ID = i;
        displayName = d;
        cost = c;
        commands = cmds;
    }

    public String getID() {
        return ID;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public Double getCost() {
        return cost;
    }

    public List<String> getCommands() {
        return commands;
    }
}
