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

/**
 *
 * @author Beelzebu
 */
public class MultiplierDataBuilder {
    
    private final String server;
    private final String enabler;
    private int amount = 1;
    private boolean enabled = false;
    
    public MultiplierDataBuilder(String server, String enabler) {
        this(server, enabler, 1);
    }
    
    public MultiplierDataBuilder(String server, String enabler, int amount) {
        this(server, enabler, amount, false);
    }
    
    public MultiplierDataBuilder(String server, String enabler, int amount, boolean enabled) {
        this.server = server;
        this.enabler = enabler;
        this.amount = amount;
        this.enabled = enabled;
    }
    
    public MultiplierData create() {
        return new MultiplierData(server, enabler, enabled, amount);
    }
}
