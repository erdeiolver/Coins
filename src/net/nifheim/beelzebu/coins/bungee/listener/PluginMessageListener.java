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
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.nifheim.beelzebu.coins.bungee.Main;
import net.nifheim.beelzebu.coins.bungee.utils.Configuration;
import net.nifheim.beelzebu.coins.core.Core;

/**
 *
 * @author Beelzebu
 */
public class PluginMessageListener implements Listener {

    private final Core core = Core.getInstance();
    private final Configuration config = (Configuration) core.getConfig();
    private final List<String> message = Collections.synchronizedList(new ArrayList<>());

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
            if (input.equals("getexecutors")) {
                for (String id : ((net.md_5.bungee.config.Configuration) config.getConfigurationSection("Command executor")).getKeys()) {
                    message.clear();
                    List<String> commands = config.getStringList("Command executor." + id + ".Command");
                    List<String> messages = Arrays.asList(
                            id,
                            String.valueOf(config.getDouble("Command executor." + id + ".Cost")),
                            String.valueOf(commands.size())
                    );
                    message.addAll(messages);
                    message.addAll(messages.size(), commands);
                    sendToBukkit("Coins", message, server);
                }
            } else if (input.startsWith("execute ")) {
                String[] msg = input.split(" ");
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getPlayer(msg[1]), input.substring((msg[0] + msg[1]).length() + 2));
            }
        }
    }

    public void sendToBukkit(String channel, List<String> messages, ServerInfo server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        messages.forEach((msg) -> {
            out.writeUTF(msg);
        });
        server.sendData("Coins", out.toByteArray(), true);
    }
}
