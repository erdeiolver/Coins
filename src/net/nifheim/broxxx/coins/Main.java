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
package net.nifheim.broxxx.coins;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.command.CommandManager;
import net.nifheim.broxxx.coins.databasehandler.FlatFile;
import net.nifheim.broxxx.coins.databasehandler.MySQL;
import net.nifheim.broxxx.coins.hooks.MVdWPlaceholderAPIHook;
import net.nifheim.broxxx.coins.hooks.PlaceholderAPI;
import net.nifheim.broxxx.coins.listener.CommandListener;
import net.nifheim.broxxx.coins.listener.PlayerJoinListener;
import net.nifheim.broxxx.coins.utils.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public final File messagesFile = new File(getDataFolder(), "messages.yml");
    private FileConfiguration messages;
    public final File configFile = new File(getDataFolder(), "config.yml");

    public static String rep;
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static Main instance;
    private CommandManager commandManager;
    private FileUtils fileUtils;

    public static MySQL mysql;
    public static FlatFile ff;

    private PlaceholderAPI placeholderAPI;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        copyFiles();
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    @Override
    public void onEnable() {

        instance = this;
        commandManager = new CommandManager(this);
        fileUtils = new FileUtils(this);
        loadConfig(false);
        updateFiles();
        motd();
        loadManagers();

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);

        if (getConfig().getBoolean("MySQL.Use")) {
            mysql = new MySQL(this);
        } else {
            ff = new FlatFile(this);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            CoinsAPI.createPlayer(p);
        }
    }

    @Override
    public void onDisable() {

        Bukkit.getScheduler().cancelTasks(this);

        motd();
    }

    private void copyFiles() {
        getDataFolder().mkdirs();

        if (!messagesFile.exists()) {
            fileUtils.copy(getResource("messages.yml"), messagesFile);
        }
        if (!configFile.exists()) {
            fileUtils.copy(getResource("config.yml"), configFile);
        }
    }

    private void loadConfig(boolean reloadConfig) {
        if (reloadConfig) {
            try {
                reloadConfig();
                getMessages().load(messagesFile);
            } catch (IOException | InvalidConfigurationException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    public FileConfiguration getMessages() {
        return messages;
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
    }

    public void log(String str) {
        console.sendMessage(rep("&8[&cCoins&8] &7" + str));
    }

    public void debug(String str) {
        if (getConfig().getBoolean("Debug")) {
            console.sendMessage(rep("&8[&cCoins&8] &cDebug: &7"));
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

    private void motd() {
        if (getDescription().getVersion().contains("BETA")) {
            console.sendMessage(rep(""));
            console.sendMessage(rep("    §c+=======================+"));
            console.sendMessage(rep("    §c|   §4Coins §fBy: §7Broxxx§c    |"));
            console.sendMessage(rep("    §c|-----------------------|"));
            console.sendMessage(rep("    §c|     §4v:§f" + getDescription().getVersion() + "      §c|"));
            console.sendMessage(rep("    §c+=======================+"));
            console.sendMessage(rep(""));
        } else {
            console.sendMessage(rep(""));
            console.sendMessage(rep("    §c+==================+"));
            console.sendMessage(rep("    §c| §4Coins §fBy: §7Broxxx§c |"));
            console.sendMessage(rep("    §c|------------------|"));
            console.sendMessage(rep("    §c|     §4v:§f" + getDescription().getVersion() + "      §c|"));
            console.sendMessage(rep("    §c+==================+"));
            console.sendMessage(rep(""));
        }
        if (getConfig().getBoolean("Debug", false)) {
            log("Debug mode is enabled.");
        }
    }
}
