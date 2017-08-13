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
package net.nifheim.beelzebu.coins.bukkit.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.Core;
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
        if (plugin.getConfig().getInt("version") == 1) {
            Core.getInstance().getMethods().log("Config file is outdated, trying to update ...");

            List<String> aliases = new ArrayList<>();
            aliases.add("mycoins");
            aliases.add("coinsalias");
            plugin.getConfig().set("Command.Name", "coins");
            plugin.getConfig().set("Command.Description", "Base command of the Coins plugin");
            plugin.getConfig().set("Command.Usage", "/coins");
            plugin.getConfig().set("Command.Permission", "coins.use");
            plugin.getConfig().set("Command.Aliases", aliases);
            plugin.getConfig().set("version", 2);
            try {
                plugin.getConfig().save(configFile);
                Core.getInstance().getMethods().log("Config file has been updated from version 1 to 2!");
            } catch (IOException ex) {
                Core.getInstance().getMethods().log("&cAn internal error has ocurred when updating config file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        if (plugin.getConfig().getInt("version") == 2) {
            plugin.getConfig().set("Debug", false);
            plugin.getConfig().set("version", 3);
            try {
                plugin.getConfig().save(configFile);
                Core.getInstance().getMethods().log("Config file has been updated from version 2 to 3!");
            } catch (IOException ex) {
                Core.getInstance().getMethods().log("&cAn internal error has ocurred when updating config file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void updateMessages() {
        // TODO: do this with a writer for add comments.
    }

    public FileConfiguration getMessages(String lang) {
        String locale = lang;
        if (lang == null) {
            locale = "";
        } else if (lang.length() > 2) {
            locale = lang.split("_")[0];;
        }
        File messagesFile_ = new File(plugin.getDataFolder(), "/messages" + (locale.length() == 2 ? "_" + locale : locale) + ".yml");
        FileConfiguration messages_ = YamlConfiguration.loadConfiguration(messagesFile_);
        if (messagesFile_.exists()) {
            return messages_;
        }
        if (messages == null) {
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        }
        return messages;

    }

    public void copyFiles() {
        plugin.getDataFolder().mkdirs();

        if (!messagesFile.exists()) {
            copy(plugin.getResource("messages.yml"), messagesFile);
        }
        if (!configFile.exists()) {
            copy(plugin.getResource("config.yml"), configFile);
        }
        File es = new File(plugin.getDataFolder(), "messages_es.yml");
        if (!es.exists()) {
            copy(plugin.getResource("messages_es.yml"), es);
        }
    }
}
