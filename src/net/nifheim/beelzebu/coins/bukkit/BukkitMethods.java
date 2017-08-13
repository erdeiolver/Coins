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
import java.util.UUID;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.MethodInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Beelzebu
 */
public class BukkitMethods implements MethodInterface {

    private final CommandSender console = Bukkit.getConsoleSender();

    @Override
    public Object getPlugin() {
        return Main.getInstance();
    }

    @Override
    public Object getConfig() {
        return Main.getInstance().getConfig();
    }

    @Override
    public Object getMessages() {
        return Main.getInstance().getMessages(null);
    }

    @Override
    public String getString(Object player, String msg) {
        Player p;
        String locale;
        try {
            p = (Player) player;
            if (Bukkit.getServer().getPlayer(p.getName()) != null) {
                locale = p.spigot().getLocale();
            } else {
                locale = "";
            }
        } catch (Exception ex) {
            locale = "";
        }
        try {
            msg = Core.getInstance().rep(Main.getInstance().getMessages(locale).getString(msg));
        } catch (NullPointerException ex) {
            log("The string " + msg + " does not exists in the messages file, please add this manually.");
            log("If you belive that this is an error please contact to the developer.");
            Core.getInstance().debug(ex);
            msg = "";
        }
        return msg.replaceAll("%prefix%", Main.getInstance().getMessages(locale).getString("Prefix"));
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
    public File getDataFolder() {
        return ((JavaPlugin) getPlugin()).getDataFolder();
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
    public String getString(Object file, Object player, String path) {
        //Player p = (Player) player;
        return ((FileConfiguration) file).getString(path, "");
    }

    @Override
    public Integer getInt(Object file, String path) {
        return ((FileConfiguration) file).getInt(path, 0);
    }

    @Override
    public Boolean getBoolean(Object file, String path) {
        return ((FileConfiguration) file).getBoolean(path, false);
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
}
