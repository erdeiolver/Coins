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

import lombok.Getter;

/**
 *
 * @author Beelzebu
 */
@Getter
public abstract class MultiplierData {

    private final String server;
    private final String enabler;
    private final boolean enabled;
    private final int amount;
    private final int minutes;
    private final int ID;
    private final boolean queue;

    MultiplierData(String server, String enabler, boolean enabled, int amount, int minutes, int id, boolean queue) {
        this.server = server;
        this.enabler = enabler;
        this.enabled = enabled;
        this.amount = amount;
        this.minutes = minutes;
        this.ID = id;
        this.queue = queue;
    }
}
