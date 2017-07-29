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
package net.nifheim.beelzebu.coins;

import net.nifheim.beelzebu.coins.listener.PlayerJoinListener;
import net.nifheim.beelzebu.coins.listener.CommandListener;
import net.nifheim.beelzebu.coins.listener.GUIListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.beelzebu.coins.command.CommandManager;
import net.nifheim.beelzebu.coins.databasehandler.FlatFile;
import net.nifheim.beelzebu.coins.databasehandler.MySQL;
import net.nifheim.beelzebu.coins.utils.placeholders.MVdWPlaceholderAPIHook;
import net.nifheim.beelzebu.coins.utils.placeholders.PlaceholderAPI;
import net.nifheim.beelzebu.coins.utils.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static Main instance;
    private CommandManager commandManager;
    private FileUtils fileUtils;

    public static MySQL mysql;
    public static FlatFile flatfile;

    private PlaceholderAPI placeholderAPI;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        fileUtils = new FileUtils(this);
        fileUtils.copyFiles();
    }

    @Override
    public void onEnable() {

        instance = this;
        commandManager = new CommandManager(this);
        loadConfig(false);
        updateFiles();
        motd(true);
        loadManagers();

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        if (getConfig().getBoolean("MySQL.Use")) {
            mysql = new MySQL(this);
        } else {
            flatfile = new FlatFile(this);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            Bukkit.getOnlinePlayers().forEach((p) -> {
                CoinsAPI.createPlayer(p);
            });
        }, 30);
    }

    @Override
    public void onDisable() {

        Bukkit.getScheduler().cancelTasks(this);

        motd(false);
    }

    private void loadConfig(boolean reloadConfig) {
        if (reloadConfig) {
            fileUtils.reloadConfig();
            fileUtils.reloadMessages();
        }
    }

    private void updateFiles() {
        fileUtils.updateConfig();
        fileUtils.updateMessages();
    }

    private void loadManagers() {
        // Create the command
        commandManager.registerCommand();
        // Hook placeholders
        if (getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            log("MVdWPlaceholderAPI found, hooking in ");
            MVdWPlaceholderAPIHook.hook(this);
        }
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log("PlaceholderAPI found, hooking in ");
            placeholderAPI = new PlaceholderAPI(this);
            placeholderAPI.hook();
        }
    }

    public String rep(String str) {
        return str
                .replaceAll("%prefix%", getMessages().getString("Prefix"))
                .replaceAll("&", "§");
    }

    public void reload() {
        getServer().getScheduler().cancelTasks(this);

        loadConfig(true);

        loadManagers();

        debug("Plugin reloaded");
    }

    public void log(Object log) {
        console.sendMessage(rep("&8[&cCoins&8] &7" + log));
    }

    public void debug(String str) {
        if (getConfig().getBoolean("Debug")) {
            console.sendMessage(rep("&8[&cCoins&8] &cDebug: &7" + str));
            File log = new File(getDataFolder() + "/Debug.log");
            BufferedWriter writer = null;
            // TODO Java 9:
            // try (writer = new BufferedWriter(new FileWriter(log, true))) {
            try {
                writer = new BufferedWriter(new FileWriter(log, true));
                writer.write(removeColor(str));
                writer.newLine();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Can''t save the debug to the file", ex);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public String getString(String msg) {
        try {
            msg = rep(getMessages().getString(msg));
        } catch (NullPointerException ex) {
            log("The string " + msg + " does not exists in the messages file, please add this manually.");
            log("If you belive that this is an error please contact to the developer.");
            debug(ex.getCause().getMessage());
            msg = "";
        }
        return msg;
    }

    private void motd(Boolean enable) {
        if (getDescription().getVersion().contains("BETA")) {
            console.sendMessage(rep(""));
            console.sendMessage(rep("    &c+==========================+"));
            console.sendMessage(rep("    &c|    &4Coins &fBy: &7Beelzebu&c    |"));
            console.sendMessage(rep("    &c|--------------------------|"));
            console.sendMessage(rep("    &c|       &4v:&f" + getDescription().getVersion() + "       &c|"));
            console.sendMessage(rep("    &c+==========================+"));
            console.sendMessage(rep(""));
            console.sendMessage(rep("&cThis is a BETA, please report bugs!"));
        } else {
            console.sendMessage(rep(""));
            console.sendMessage(rep("    &c+======================+"));
            console.sendMessage(rep("    &c|   &4Coins &fBy: &7Beelzebu&c   |"));
            console.sendMessage(rep("    &c|----------------------|"));
            console.sendMessage(rep("    &c|       &4v:&f" + getDescription().getVersion() + "        &c|"));
            console.sendMessage(rep("    &c+====================+"));
            console.sendMessage(rep(""));
        }
        // Only send this in the onEnable
        if (enable) {
            if (getConfig().getBoolean("Debug", false)) {
                log("Debug mode is enabled.");
            }
        }
    }

    public FileConfiguration getMessages() {
        return fileUtils.getMessages();
    }

    private String removeColor(String str) {
        return str
                .replaceAll("&0", "")
                .replaceAll("&1", "")
                .replaceAll("&2", "")
                .replaceAll("&3", "")
                .replaceAll("&4", "")
                .replaceAll("&5", "")
                .replaceAll("&6", "")
                .replaceAll("&7", "")
                .replaceAll("&8", "")
                .replaceAll("&9", "")
                .replaceAll("&a", "")
                .replaceAll("&b", "")
                .replaceAll("&c", "")
                .replaceAll("&d", "")
                .replaceAll("&e", "")
                .replaceAll("&f", "")
                .replaceAll("&r", "")
                .replaceAll("&l", "")
                .replaceAll("&m", "")
                .replaceAll("&n", "")
                .replaceAll("&o", "");
    }
}
