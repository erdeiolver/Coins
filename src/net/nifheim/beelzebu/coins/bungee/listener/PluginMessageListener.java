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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.nifheim.beelzebu.coins.common.CoinsCore;
import net.nifheim.beelzebu.coins.common.multiplier.Multiplier;
import net.nifheim.beelzebu.coins.common.utils.CacheManager;

/**
 * @author Beelzebu
 */
public class PluginMessageListener extends CoinsBungeeListener implements Listener {

    @EventHandler
    public void onMessageReceive(PluginMessageEvent e) {
        if (!e.getTag().equals(CoinsCore.MESSAGING_CHANNEL)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String channel = in.readUTF();
        switch (channel) {
            case "Coins": {
                ServerInfo server = ProxyServer.getInstance().getPlayer(e.getReceiver().toString()).getServer().getInfo();
                String input = in.readUTF();
                if (input.startsWith("getExecutors")) {
                    if (plugin.useRedis()) {
                        RedisBungee.getApi().sendChannelMessage("Coins", "getExecutors");
                    } else {
                        sendExecutors(server);
                    }
                } else if (input.startsWith("execute ")) {
                    String[] msg = input.split(" ");
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getPlayer(msg[1]), input.substring((msg[0] + msg[1]).length() + 2));
                }
                break;
            }
            case "Update": {
                String input = in.readUTF();
                if (input.startsWith("updateCache")) {
                    String[] updatemsg = input.split(" ");
                    if (updatemsg.length == 3) {
                        if (plugin.useRedis()) {
                            RedisBungee.getApi().sendChannelMessage("Update", updatemsg[1] + " " + updatemsg[2]);
                        } else {
                            ProxyServer.getInstance().getServers().keySet().forEach(server -> sendToBukkit("Update", Collections.singletonList(updatemsg[1] + " " + updatemsg[2]), ProxyServer.getInstance().getServerInfo(server), true));
                        }
                    }
                }
                break;
            }
            case "Multiplier":
                List<String> multiplierData = new ArrayList<>();
                String input = in.readUTF();
                if (input.startsWith("get ")) { // update the multiplier for the specified server
                    Multiplier multiplier = CacheManager.getMultiplier(input.split(" ")[1]);
                    if (multiplier != null) {
                        core.updateMultiplier(multiplier);
                    }
                } else if (input.equals("getAllMultipliers")) { // update all multipliers
                    CacheManager.getMultipliersData().keySet().stream().map(CacheManager::getMultiplier).forEach(core::updateMultiplier);
                } else if (input.startsWith("disable ")) {
                    Multiplier multiplier = CacheManager.getMultiplier(input.split(" ")[1]);
                    if (multiplier != null) {
                        if (plugin.useRedis()) {
                            RedisBungee.getApi().sendChannelMessage("Multiplier", input);
                        } else {
                            multiplier.setEnabled(false);
                        }
                    }
                } else { // store the data
                    multiplierData.add(input);
                    for (int i = 0; i < 4; i++) {
                        multiplierData.add(in.readUTF());
                    }
                    if (multiplierData.size() == 5) {
                        Multiplier multiplier = new Multiplier(multiplierData.get(0), multiplierData.get(2), Boolean.valueOf(multiplierData.get(1)), Integer.valueOf(multiplierData.get(3)), Long.valueOf(multiplierData.get(4)));
                        if (plugin.useRedis()) { // update in all servers if use redis
                            String multiplierString = "";
                            multiplierString = multiplierData.stream().map((str) -> str + "|||").reduce(multiplierString, String::concat);
                            RedisBungee.getApi().sendChannelMessage("Multiplier", multiplierString.substring(0, multiplierString.length() - 3));
                        } else { // just upadte this
                            CacheManager.addMultiplier(multiplierData.get(0), multiplier);
                            if (multiplier.isEnabled()) {
                                core.getMethods().callMultiplierEnableEvent(core.getUUID(multiplier.getEnabler()), multiplier.getData());
                            }
                        }
                        ProxyServer.getInstance().getServers().keySet().forEach(server -> sendToBukkit("Multiplier", multiplierData, ProxyServer.getInstance().getServerInfo(server), false));
                    }
                }
                break;
            default:
                break;
        }
    }
}
