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
package net.nifheim.beelzebu.coins.bungee;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import net.nifheim.beelzebu.coins.bungee.utils.Messages;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.utils.IConfiguration;
import net.nifheim.beelzebu.coins.core.utils.MessagesManager;
import net.nifheim.beelzebu.coins.core.utils.IMethods;

/**
 *
 * @author Beelzebu
 */
public class BungeeMethods implements IMethods {
    
    private final Main plugin = Main.getInstance();
    private final CommandSender console = ProxyServer.getInstance().getConsole();
    
    @Override
    public Object getPlugin() {
        return plugin;
    }
    
    @Override
    public IConfiguration getConfig() {
        return plugin.getConfiguration();
    }
    
    @Override
    public MessagesManager getMessages(String lang) {
        return new Messages(lang);
    }
    
    @Override
    public void runAsync(Runnable rn) {
        ProxyServer.getInstance().getScheduler().runAsync((Plugin) getPlugin(), rn);
    }
    
    @Override
    public void runAsync(Runnable rn, Long timer) {
        ProxyServer.getInstance().getScheduler().schedule((Plugin) getPlugin(), rn, timer, TimeUnit.MINUTES);
    }
    
    @Override
    public void runSync(Runnable rn) {
        rn.run();
    }
    
    @Override
    public void executeCommand(String cmd) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(console, cmd);
    }
    
    @Override
    public void log(Object log) {
        console.sendMessage(Core.getInstance().rep("&8[&cCoins&8] &7" + log));
    }
    
    @Override
    public String getNick(Object player) {
        return ((ProxiedPlayer) player).getName();
    }
    
    @Override
    public String getNick(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid).getName();
    }
    
    @Override
    public UUID getUUID(Object player) {
        return ((ProxiedPlayer) player).getUniqueId();
    }
    
    @Override
    public UUID getUUID(String player) {
        return ProxyServer.getInstance().getPlayer(player).getUniqueId();
    }
    
    @Override
    public Object getConsole() {
        return console;
    }
    
    @Override
    public void sendMessage(Object commandsender, String msg) {
        ((CommandSender) commandsender).sendMessage(TextComponent.fromLegacyText(msg));
    }
    
    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
    
    @Override
    public InputStream getResource(String file) {
        return plugin.getResourceAsStream(file);
    }
}
