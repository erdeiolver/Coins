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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.command.CoinsCommand;
import net.nifheim.broxxx.coins.databasehandler.FlatFile;
import net.nifheim.broxxx.coins.databasehandler.MySQL;
import net.nifheim.broxxx.coins.hooks.MVdWPlaceholderAPIHook;
import net.nifheim.broxxx.coins.hooks.PlaceholderAPI;
import net.nifheim.broxxx.coins.listener.CommandListener;
import net.nifheim.broxxx.coins.listener.PlayerJoinListener;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private final File messagesFile = new File(getDataFolder(), "messages.yml");
    private FileConfiguration messages;
    private final File configFile = new File(getDataFolder(), "config.yml");
    private final List<String> commandAliases = new ArrayList<>();

    public static String rep;
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private PlaceholderAPI placeholderAPI;

    private static Main instance;
    public static MySQL mysql;
    public static FlatFile ff;

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
        loadConfig(false);
        loadManagers();

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);

        if (getConfig().getBoolean("MySQL.Use")) {
            mysql = new MySQL();
            mysql.SQLConnection();
        } else {
            ff = new FlatFile(this);
        }

        updateFiles();

        motd();

        Bukkit.getOnlinePlayers().forEach((p) -> {
            CoinsAPI.createPlayer(p);
        });
    }

    @Override
    public void onDisable() {

        Bukkit.getScheduler().cancelTasks(this);

        motd();
    }

    private void copyFiles() {
        getDataFolder().mkdirs();

        if (!messagesFile.exists()) {
            copy(getResource("messages.yml"), messagesFile);
        }
        if (!configFile.exists()) {
            copy(getResource("config.yml"), configFile);
        }
    }

    private void loadManagers() {
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

    public void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Can't copy the file " + file.getName() + " to the plugin data folder.", e.getCause());
        }
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
    }

    private void updateFiles() {
        updateConfig();
        updateMessages();
    }

    private void updateConfig() {
        if (getConfig().getInt("version") == 1) {
            log("Config file is outdated, trying to update ...");

            List<String> aliases = new ArrayList<>();
            aliases.add("mycoins");
            aliases.add("coinsalias");
            getConfig().set("Command.Name", "coins");
            getConfig().set("Command.Description", "Base command of the Coins plugin");
            getConfig().set("Command.Usage", "/coins");
            getConfig().set("Command.Permission", "coins.use");
            getConfig().set("Command.Aliases", aliases);
            getConfig().set("version", 2);
            try {
                getConfig().save(configFile);
                log("Config file has been updated!");
            } catch (IOException ex) {
                log("&cAn internal error has ocurred when updating config file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }

    private void updateMessages() {
        if (getMessages().getInt("version") == 1) {
            log("Messages file is outdated, trying to update ...");

            getMessages().set("Errors.No Execute", "%prefix% &cCan't find a command to execute with this id.");
            getMessages().set("version", 2);
            try {
                getMessages().save(messagesFile);
                log("Messages file has been updated!");
            } catch (IOException ex) {
                log("&cAn internal error has ocurred when updating messages file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }

    public void reload() {
        getServer().getScheduler().cancelTasks(this);

        loadConfig(true);

        loadManagers();
    }

    private void loadConfig(boolean reloadConfig) {
        try {
            getConfig().getStringList("Command.Aliases").forEach((str) -> {
                commandAliases.add(str);
            });

            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            String commandName = getConfig().getString("Command.Name", "coins");
            String commandDescription = getConfig().getString("Command.Description", "Base command of the Coins plugin");
            String commandUsage = getConfig().getString("Command.Usage", "/coins");
            String commandPermission = getConfig().getString("Command.Permission", "coins.use");

            commandAliases.forEach((str) -> {
                unregisterCommand(str);
            });
            unregisterCommand(commandName);

            commandMap.register(commandName, new CoinsCommand(commandName, commandDescription, commandUsage, commandPermission, commandAliases));

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (reloadConfig) {
            try {
                reloadConfig();
                getMessages().load(messagesFile);
            } catch (IOException | InvalidConfigurationException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void unregisterCommand(final String command) {
        if (getServer() != null && getServer().getPluginManager() instanceof SimplePluginManager) {
            final SimplePluginManager manager = (SimplePluginManager) getServer().getPluginManager();
            try {
                final Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                CommandMap map = (CommandMap) field.get(manager);
                final Field field2 = SimpleCommandMap.class.getDeclaredField("knownCommands");
                field2.setAccessible(true);
                final Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) field2.get(map);
                knownCommands.entrySet().stream().filter((entry) -> (entry.getKey().equals(command))).forEachOrdered((entry) -> {
                    entry.getValue().unregister(map);
                });
                knownCommands.remove(command);
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
            }
        }
    }

    public void log(String str) {
        console.sendMessage(rep("&8[&cCoins&8]&7 " + str));
    }
}
