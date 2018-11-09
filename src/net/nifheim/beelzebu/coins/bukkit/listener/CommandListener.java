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
package net.nifheim.beelzebu.coins.bukkit.listener;

import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.common.CoinsCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @author Beelzebu
 */
public class CommandListener implements Listener {

    private final Main plugin = Main.getInstance();
    private final FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent e) {
        // TODO: may be log the commands of the plugin and the command costs.
        String msg = e.getMessage().toLowerCase();
        CoinsCore.getInstance().getMethods().runAsync(() -> {
            if (config.getDouble("Command Cost." + msg) != 0) {
                if (CoinsAPI.getCoins(e.getPlayer().getUniqueId()) < config.getDouble("Command Cost." + msg)) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(CoinsCore.getInstance().rep(CoinsCore.getInstance().getMessages(e.getPlayer().spigot().getLocale()).getString("Errors.No Coins")));
                } else {
                    CoinsAPI.takeCoins(e.getPlayer().getName(), config.getDouble("Command Cost." + msg));
                }
            }
        });
    }
}
