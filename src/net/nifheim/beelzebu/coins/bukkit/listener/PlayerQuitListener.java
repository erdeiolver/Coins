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
package net.nifheim.beelzebu.coins.bukkit.listener;

import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.utils.CacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Beelzebu
 */
public class PlayerQuitListener implements Listener {

    private final Main plugin;

    public PlayerQuitListener(Main main) {
        plugin = main;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (CacheManager.getData().containsKey(e.getPlayer().getUniqueId())) {
            CacheManager.getData().remove(e.getPlayer().getUniqueId());
        }
    }
}
