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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.broxxx.coins.Main;
import org.bukkit.Bukkit;

/**
 *
 * @author Beelzebu
 */
public class FileUtils {

    private final Main plugin;

    public FileUtils(Main main) {
        plugin = main;
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
        if (plugin.getConfig().getInt("version") == 1) {
            plugin.log("Config file is outdated, trying to update ...");

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
                plugin.getConfig().save(plugin.configFile);
                plugin.log("Config file has been updated from version 1 to 2!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating config file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        if (plugin.getConfig().getInt("version") == 2) {
            plugin.getConfig().set("Debug", false);
            plugin.getConfig().set("version", 3);
            try {
                plugin.getConfig().save(plugin.configFile);
                plugin.log("Config file has been updated from version 2 to 3!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating config file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void updateMessages() {
        if (plugin.getMessages().getInt("version") == 1) {
            plugin.log("Messages file is outdated, trying to update ...");

            plugin.getMessages().set("Errors.No Execute", "%prefix% &cCan't find a command to execute with this id.");
            plugin.getMessages().set("version", 2);
            try {
                plugin.getMessages().save(plugin.messagesFile);
                plugin.log("Messages file has been updated from version 1 to 2!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating messages file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        if (plugin.getMessages().getInt("version") == 2) {
            plugin.getMessages().set("Multipliers.Created", "%prefix% You sucessfull created a multiplier for %player%.");
            plugin.getMessages().set("Multipliers.Set", "%prefix% You sucessfull setted a multiplier for this server.");
            plugin.getMessages().set("Multipliers.No Multipliers", "%prefix% You don''t have any multiplier.");
            plugin.getMessages().set("version", 3);
            try {
                plugin.getMessages().save(plugin.messagesFile);
                plugin.log("Messages file has been updated from version 2 to 3!");
            } catch (IOException ex) {
                plugin.log("&cAn internal error has ocurred when updating messages file, disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

}
