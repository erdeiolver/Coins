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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import java.util.Arrays;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.nifheim.beelzebu.coins.bungee.Main;

/**
 *
 * @author Beelzebu
 */
public class PluginMessageListener extends CoinsBungeeListener implements Listener {

    @EventHandler
    public void onMessageReceive(PluginMessageEvent e) {
        if (!e.getTag().equals("Coins")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String channel = in.readUTF();
        if (channel.equals("Coins")) {
            ServerInfo server = ProxyServer.getInstance().getPlayer(e.getReceiver().toString()).getServer().getInfo();
            String input = in.readUTF();
            if (input.startsWith("getExecutors")) {
                if (Main.useRedis()) {
                    RedisBungee.getApi().sendChannelMessage("Coins", "getExecutors");
                } else {
                    sendExecutors(server);
                }
            } else if (input.startsWith("execute ")) {
                String[] msg = input.split(" ");
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getPlayer(msg[1]), input.substring((msg[0] + msg[1]).length() + 2));
            }
        } else if (channel.equals("Update")) {
            String input = in.readUTF();
            if (input.startsWith("updateCache")) {
                String[] updatemsg = input.split(" ");
                if (updatemsg.length == 3) {
                    if (Main.useRedis()) {
                        RedisBungee.getApi().sendChannelMessage("Update", updatemsg[1] + " " + updatemsg[2]);
                    } else {
                        ProxyServer.getInstance().getServers().keySet().forEach(server -> {
                            sendToBukkit("Update", Arrays.asList(updatemsg[1] + " " + updatemsg[2]), ProxyServer.getInstance().getServerInfo(server));
                        });
                    }
                }
            }
        }
    }
}
