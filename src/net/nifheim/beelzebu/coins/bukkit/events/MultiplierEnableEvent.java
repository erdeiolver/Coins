/**
 * This file is part of Coins
 *
 * Copyright (C) 2017 Beelzebu
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.nifheim.beelzebu.coins.bukkit.events;

import java.util.UUID;
import net.nifheim.beelzebu.coins.common.multiplier.MultiplierData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Beelzebu
 */
public class MultiplierEnableEvent extends Event {

    private final static HandlerList handlers = new HandlerList();
    private final UUID enabler;
    private final MultiplierData data;

    public MultiplierEnableEvent(UUID uuid, MultiplierData multiplierData) {
        enabler = uuid;
        data = multiplierData;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the enabler for this multiplier.
     *
     * @return the enabler.
     */
    public String getEnabler() {
        return data.getEnabler();
    }

    /**
     * Get the UUID of the enabler for this multiplier.
     *
     * @return the uuid.
     * @throws NullPointerException if the multiplier is fake, this can be null.
     */
    public UUID getEnablerUUID() throws NullPointerException {
        return enabler;
    }

    /**
     * Get all the data about this multiplier.
     *
     * @return all the multiplier data.
     */
    public MultiplierData getData() {
        return data;
    }

    /**
     * Get the id of this multiplier.
     *
     * @return the id, can be -1 if the multiplier is fake.
     */
    public int getID() {
        return data.getID();
    }

    /**
     * Get the amount of this multiplier.
     *
     * @return the amount.
     */
    public int getAmount() {
        return data.getAmount();
    }

    /**
     * Get the amount of minutes that this multiplier will be enabled.
     *
     * @return the minutes.
     */
    public int getMinutes() {
        return data.getMinutes();
    }
}
