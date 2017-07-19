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
package net.nifheim.broxxx.coins.utils;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.broxxx.coins.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Beelzebu
 */
public class FileUtils {

    private final Main plugin;
    private final File messagesFile;
    private FileConfiguration messages;
    private final File configFile;
    private FileConfiguration config;

    public FileUtils(Main main) {
        plugin = main;
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        configFile = new File(plugin.getDataFolder(), "config.yml");
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

    public void updateConfig() {
        // TODO: create v4
        if (getConfig().getInt("version") == 1) {
            plugin.log("Config file is outdated, trying to update ...");

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
                saveConfig();
                plugin.log("Config file has been updated from version 1 to 2!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating config file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        if (getConfig().getInt("version") == 2) {
            getConfig().set("Debug", false);
            getConfig().set("version", 3);
            try {
                saveConfig();
                plugin.log("Config file has been updated from version 2 to 3!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating config file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void updateMessages() {
        if (getMessages().getInt("version") == 1) {
            plugin.log("Messages file is outdated, trying to update ...");

            getMessages().set("Errors.No Execute", "%prefix% &cCan't find a command to execute with this id.");
            getMessages().set("version", 2);
            try {
                saveMessages();
                plugin.log("Messages file has been updated from version 1 to 2!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating messages file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        if (getMessages().getInt("version") == 2) {
            getMessages().set("Multipliers.Created", "%prefix% You sucessfull created a multiplier for %player%.");
            getMessages().set("Multipliers.Set", "%prefix% You sucessfull setted a multiplier for this server.");
            getMessages().set("Multipliers.No Multipliers", "%prefix% You don''t have any multiplier.");
            getMessages().set("version", 3);
            try {
                saveMessages();
                plugin.log("Messages file has been updated from version 2 to 3!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating messages file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        if (getMessages().getInt("version") == 3) {
            getMessages().set("Coins.Give Target", "%prefix% You recived &f%coins%&7. %multiplier_format%");
            getMessages().set("Multipliers.Already active", "%prefix% There is an active multiplier already");
            getMessages().set("Multipliers.Format", "&8(&bx%multiplier%&8) &aMultiplier enabled by &c%enabler%&a!");
            getMessages().set("version", 4);
            try {
                saveMessages();
                plugin.log("Messages file has been updated from version 3 to 4!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating messages file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        final InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }
        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public void saveConfig() throws IOException {
        getConfig().save(configFile);
    }

    public FileConfiguration getMessages() {
        if (messages == null) {
            reloadMessages();
        }
        return messages;
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        final InputStream defMessagesStream = plugin.getResource("messages.yml");
        if (defMessagesStream == null) {
            return;
        }
        messages.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defMessagesStream, Charsets.UTF_8)));
    }

    public void saveMessages() throws IOException {
        getMessages().save(messagesFile);
    }

    public void copyFiles() {
        plugin.getDataFolder().mkdirs();

        if (!messagesFile.exists()) {
            copy(plugin.getResource("messages.yml"), messagesFile);
        }
        if (!configFile.exists()) {
            copy(plugin.getResource("config.yml"), configFile);
        }
    }
}
