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
package net.nifheim.beelzebu.coins.bukkit;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import net.nifheim.beelzebu.coins.bukkit.utils.Messages;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.utils.IConfiguration;
import net.nifheim.beelzebu.coins.core.utils.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.nifheim.beelzebu.coins.core.utils.IMethods;

/**
 *
 * @author Beelzebu
 */
public class BukkitMethods implements IMethods {

    private final Main plugin = Main.getInstance();
    private final CommandSender console = Bukkit.getConsoleSender();

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
        Bukkit.getScheduler().runTaskAsynchronously((Plugin) getPlugin(), rn);
    }

    @Override
    public void runAsync(Runnable rn, Long timer) {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin) getPlugin(), rn, 0, timer * 1200);
    }

    @Override
    public void runSync(Runnable rn) {
        Bukkit.getScheduler().runTask((Plugin) getPlugin(), rn);
    }

    @Override
    public void executeCommand(String cmd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void log(Object log) {
        console.sendMessage(Core.getInstance().rep("&8[&cCoins&8] &7" + log));
    }

    @Override
    public String getNick(Object player) {
        return Bukkit.getPlayer((UUID) player).getName();
    }

    @Override
    public String getNick(UUID uuid) {
        return Bukkit.getPlayer(uuid).getName();
    }

    @Override
    public UUID getUUID(Object player) {
        return ((Player) player).getUniqueId();
    }

    @Override
    public UUID getUUID(String player) {
        return Bukkit.getServer().getOfflinePlayer(player).getUniqueId();
    }

    @Override
    public Object getConsole() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMessage(Object CommandSender, String msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
    
    @Override
    public InputStream getResource(String file) {
        return plugin.getResource(file);
    }
}
