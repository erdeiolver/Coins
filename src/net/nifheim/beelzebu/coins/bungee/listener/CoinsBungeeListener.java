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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.config.ServerInfo;
import net.nifheim.beelzebu.coins.bungee.utils.Configuration;
import net.nifheim.beelzebu.coins.core.Core;

/**
 *
 * @author Beelzebu
 */
public abstract class CoinsBungeeListener {

    final Core core = Core.getInstance();
    final Configuration config = (Configuration) core.getConfig();
    final List<String> message = Collections.synchronizedList(new ArrayList<>());

    public void sendExecutors(ServerInfo server) {
        ((net.md_5.bungee.config.Configuration) config.getConfigurationSection("Command executor")).getKeys().forEach((id) -> {
            synchronized (message) {
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
        });
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
