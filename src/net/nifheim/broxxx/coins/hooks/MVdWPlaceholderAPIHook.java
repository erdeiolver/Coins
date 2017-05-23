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
package net.nifheim.broxxx.coins.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;

import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * 
 * @author Beelzebu
 */
public class MVdWPlaceholderAPIHook {

    public static void hook(Main plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceHolderAPI")) {
            PlaceholderAPI.registerPlaceholder(plugin, "coins", (PlaceholderReplaceEvent e) -> {
                final Player p = e.getPlayer();
                final OfflinePlayer offp = e.getPlayer();
                if (offp == null) {
                    return "Player is needed!";
                }
                String coins = CoinsAPI.getCoinsString(p);
                if (coins != null) {
                    return coins;
                } else {
                    return "0";
                }
            });
        }
    }
}
