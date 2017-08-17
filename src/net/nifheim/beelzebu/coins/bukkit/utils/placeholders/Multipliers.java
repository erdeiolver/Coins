/*
 * This file is part of Coins.
 *
 * Copyright © 2017 Beelzebu
 * Coins is licensed under the GNU General Public License.
 *
 * Coins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Coins is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.nifheim.beelzebu.coins.bukkit.utils.placeholders;

import me.clip.placeholderapi.external.EZPlaceholderHook;

import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.Core;

import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class Multipliers extends EZPlaceholderHook {

    private final Main plugin;

    public Multipliers(Main plugin) {
        super(plugin, "coins-multipliers");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String coins) {
        if (p == null) {
            return "Player needed!";
        }
        String[] server = coins.split("_");
        if (coins.startsWith("enabler_")) {
            return CoinsAPI.getMultiplier(server[1]).getEnabler() != null ? CoinsAPI.getMultiplier(server[1]).getEnabler() : "";
        }
        if (coins.startsWith("amount_")) {
            return String.valueOf(CoinsAPI.getMultiplier(server[1]).getAmount());
        }
        if (coins.startsWith("time_")) {
            return CoinsAPI.getMultiplier(server[1]).getMultiplierTimeFormated();
        }
        return "";
    }
}
