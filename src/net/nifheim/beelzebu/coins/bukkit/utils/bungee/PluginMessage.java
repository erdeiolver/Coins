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
package net.nifheim.beelzebu.coins.bukkit.utils.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.executor.Executor;
import net.nifheim.beelzebu.coins.core.utils.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Beelzebu
 */
public class PluginMessage implements PluginMessageListener {

    private final Core core = Core.getInstance();

    @Override
    public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("Coins")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("Coins")) {
            String id = in.readUTF();
            String displayname = in.readUTF();
            double cost = Double.valueOf(in.readUTF());
            int cmds = Integer.valueOf(in.readUTF());
            List<String> commands = new ArrayList<>();
            if (cmds > 0) {
                for (int i = 0; i < cmds; i++) {
                    commands.add(in.readUTF());
                }
            }
            Executor ex = new Executor(id, displayname, cost, commands);
            if (core.getExecutorManager().getExecutor(id) == null) {
                core.getExecutorManager().addExecutor(ex);
                core.log("The executor " + ex.getID() + " was received from BungeeCord.");
                core.debug("ID: " + ex.getID());
                core.debug("Displayname: " + ex.getDisplayName());
                core.debug("Cost: " + ex.getCost());
                core.debug("Commands: ");
                ex.getCommands().forEach((command) -> {
                    core.debug(command);
                });
            } else {
                core.debug("An executor with the id: " + ex.getID() + " was received from BungeeCord but a local Executor with that id already exists.");
            }
        } else if (subchannel.equals("Update")) {
            String data = in.readUTF();
            if (data.split(" ").length == 2) {
                UUID puuid = UUID.fromString(data.split(" ")[0]);
                CacheManager.updateCoins(puuid, Double.valueOf(data.split(" ")[1]));
            }
        }
    }

    public void sendToBungeeCord(String channel, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        out.writeUTF(message);
        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (p != null) {
            p.sendPluginMessage(Main.getInstance(), "Coins", out.toByteArray());
        }
    }
}
