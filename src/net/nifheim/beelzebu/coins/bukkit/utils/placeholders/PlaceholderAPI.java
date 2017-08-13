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

import org.bukkit.entity.Player;

/**
 * 
 * @author Beelzebu
 */
public class PlaceholderAPI extends EZPlaceholderHook {

    private final Main plugin;

    public PlaceholderAPI(Main plugin) {
        super(plugin, "coins");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String coins) {
        if (p == null) {
            return "Player needed!";
        }
        if (coins.equalsIgnoreCase("amount")) {
            return CoinsAPI.getCoinsString(p.getName());
        }
        if (coins.startsWith("multiplier_")) {
            return CoinsAPI.getMultiplier(coins.substring(11)).getMultiplierTimeFormated();
        }
        return "0";
    }
}
