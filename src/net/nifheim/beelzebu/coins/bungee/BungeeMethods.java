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
package net.nifheim.beelzebu.coins.bungee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.MethodInterface;

/**
 *
 * @author Beelzebu
 */
public class BungeeMethods implements MethodInterface {

    private final File configFile = new File(getDataFolder(), "config.yml");
    private final File messageFile = new File(getDataFolder(), "messages.yml");
    private Configuration config;
    private Configuration messages;
    private final CommandSender console = ProxyServer.getInstance().getConsole();

    public void createFiles() {
        try {
            if (!getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                getDataFolder().mkdirs();
            }
            if (!configFile.exists()) {
                Files.copy(((Plugin) getPlugin()).getResourceAsStream("config.yml"), configFile.toPath());
            }
            if (!messageFile.exists()) {
                Files.copy(((Plugin) getPlugin()).getResourceAsStream("messages.yml"), messageFile.toPath());
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messageFile);
        } catch (IOException ex) {
            Logger.getLogger(BungeeMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object getPlugin() {
        return Main.getInstance();
    }

    @Override
    public Object getConfig() {
        return config;
    }

    @Override
    public Object getMessages() {
        return messages;
    }

    @Override
    public String getString(Object player, String path) {
        try {
            path = Core.getInstance().rep(((Configuration) getMessages()).getString(path));
        } catch (NullPointerException ex) {
            log("The string " + path + " does not exists in the messages file, please add this manually.");
            log("If you belive that this is an error please contact to the developer.");
            Core.getInstance().debug(ex.getCause().getMessage());
            path = "";
        }
        return path;
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

    }

    @Override
    public File getDataFolder() {
        return ((Plugin) getPlugin()).getDataFolder();
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
    public String getString(Object file, Object player, String path) {
        return ((Configuration) file).getString(path);
    }

    @Override
    public Integer getInt(Object file, String path) {
        return ((Configuration) file).getInt(path);
    }

    @Override
    public Boolean getBoolean(Object file, String path) {
        return ((Configuration) file).getBoolean(path);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMessage(Object CommandSender, String msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
