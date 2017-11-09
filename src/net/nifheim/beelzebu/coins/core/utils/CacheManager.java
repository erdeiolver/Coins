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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.multiplier.Multiplier;

/**
 *
 * @author Beelzebu
 */
public class CacheManager {

    private static final Core core = Core.getInstance();
    private static final Map<UUID, Double> playersData = Collections.synchronizedMap(new HashMap<>());
    @Getter
    private static final Map<String, Multiplier> multipliersData = Collections.synchronizedMap(new HashMap<>());

    public static Double getCoins(UUID uuid) {
        synchronized (playersData) {
            if (playersData.containsKey(uuid)) {
                return playersData.get(uuid);
            }
        }
        return -1D;
    }

    public static void updateCoins(UUID uuid, Double coins) {
        synchronized (playersData) {
            if (playersData.containsKey(uuid)) {
                playersData.replace(uuid, coins);
            } else if (Core.getInstance().isOnline(uuid)) {
                playersData.put(uuid, coins);
            }
        }
    }

    public static void removePlayer(UUID uuid) {
        synchronized (playersData) {
            if (playersData.containsKey(uuid)) {
                playersData.remove(uuid);
            }
        }
    }

    public static void addMultiplier(String server, Multiplier multiplier) {
        synchronized (multipliersData) {
	    core.log("Multiplier: " + server + " added");
            multipliersData.put(server, multiplier);
        }
    }

    public static void removeMultiplier(String server) {
        synchronized (multipliersData) {
	    core.log("Multiplier: " + server + " removed");
            multipliersData.remove(server);
        }
    }

    public static Multiplier getMultiplier(String server) {
        synchronized (multipliersData) {
            if (multipliersData.containsKey(server)) {
                return multipliersData.get(server);
            }
        }
        return null;
    }
}
