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
import java.util.List;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.nifheim.beelzebu.coins.core.multiplier.Multiplier;
import net.nifheim.beelzebu.coins.core.utils.CacheManager;

/**
 *
 * @author Beelzebu
 */
public class PubSubMessageListener extends CoinsBungeeListener implements Listener {

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent e) {
        switch (e.getChannel()) {
            case "Coins":
                if (e.getMessage().equals("getExecutors")) {
                    core.debug("Sending executors");
                    ProxyServer.getInstance().getServers().values().forEach((server) -> {
                        sendExecutors(server);
                        core.debug("Sending to " + server.getName());
                    });
                }
                break;
            case "Update":
                ProxyServer.getInstance().getServers().keySet().forEach(server -> {
                    sendToBukkit("Update", Arrays.asList(e.getMessage()), ProxyServer.getInstance().getServerInfo(server), true);
                });
                break;
            case "Multiplier":
                if (e.getMessage().startsWith("disable ")) {
                    CacheManager.getMultiplier(e.getMessage().split(" ")[1]).setEnabled(false);
                } else {
                    List<String> multiplierData = Arrays.asList(e.getMessage().split("\\|\\|\\|"));
                    Multiplier multiplier = new Multiplier(multiplierData.get(0), multiplierData.get(2), Boolean.valueOf(multiplierData.get(1)), Integer.valueOf(multiplierData.get(3)), System.currentTimeMillis() + Long.valueOf(multiplierData.get(4)));
                    CacheManager.addMultiplier(multiplierData.get(0), multiplier);
                }
                break;
            default:
                break;
        }
    }
}
