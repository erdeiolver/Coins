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
package net.nifheim.beelzebu.coins.bungee.events;

import java.util.UUID;
import net.md_5.bungee.api.plugin.Event;
import net.nifheim.beelzebu.coins.core.multiplier.MultiplierData;

/**
 *
 * @author Beelzebu
 */
public class MultiplierEnableEvent extends Event {

    private final UUID enabler;
    private final MultiplierData data;

    public MultiplierEnableEvent(UUID uuid, MultiplierData multiplierData) {
        enabler = uuid;
        data = multiplierData;
    }

    public String getEnabler() {
        return data.getEnabler();
    }

    public UUID getEnablerUUID() {
        return enabler;
    }

    public MultiplierData getData() {
        return data;
    }

    public int getID() {
        return data.getID();
    }

    public int getAmount() {
        return data.getAmount();
    }

    public int getMinutes() {
        return data.getMinutes();
    }
}
