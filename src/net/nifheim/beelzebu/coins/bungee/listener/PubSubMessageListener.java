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
package net.nifheim.beelzebu.coins.bungee.listener;

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import java.util.Arrays;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Beelzebu
 */
public class PubSubMessageListener extends CoinsBungeeListener implements Listener {

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent e) {
        if (e.getChannel().equalsIgnoreCase("Coins")) {
            if (e.getMessage().equalsIgnoreCase("getExecutors")) {
                core.debug("Sending executors");
                ProxyServer.getInstance().getServers().values().forEach((server) -> {
                    sendExecutors(server);
                    core.debug("Sending to " + server.getName());
                });
            }
        } else if (e.getChannel().equalsIgnoreCase("Update")) {
            ProxyServer.getInstance().getServers().keySet().forEach(server -> {
                sendToBukkit("Update", Arrays.asList(e.getMessage()), ProxyServer.getInstance().getServerInfo(server));
            });
        }
    }
}
