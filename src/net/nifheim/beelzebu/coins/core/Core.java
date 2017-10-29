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
package net.nifheim.beelzebu.coins.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import net.nifheim.beelzebu.coins.bukkit.utils.bungee.PluginMessage;
import net.nifheim.beelzebu.coins.bungee.BungeeMethods;
import net.nifheim.beelzebu.coins.core.database.Database;
import net.nifheim.beelzebu.coins.core.database.MySQL;
import net.nifheim.beelzebu.coins.core.database.SQLite;
import net.nifheim.beelzebu.coins.core.executor.ExecutorManager;
import net.nifheim.beelzebu.coins.core.utils.FileManager;
import net.nifheim.beelzebu.coins.core.utils.IConfiguration;
import net.nifheim.beelzebu.coins.core.utils.IMethods;
import net.nifheim.beelzebu.coins.core.utils.MessagesManager;

/**
 *
 * @author Beelzebu
 */
public class Core {

    private static Core instance;
    private IMethods mi;
    private Database db;
    private ExecutorManager executorManager;
    private static boolean mysql = false;
    private HashMap<String, MessagesManager> messagesMap;

    public static Core getInstance() {
        return instance == null ? instance = new Core() : instance;
    }

    public void setup(IMethods methodinterface) {
        mi = methodinterface;
        messagesMap = new HashMap<>();
        FileManager fileUpdater = new FileManager(this);
        fileUpdater.copyFiles();
        getDatabase();
        executorManager = new ExecutorManager();
        motd(true);
    }

    public void shutdown() {
        getDatabase().shutdown();
        motd(false);
    }

    private void motd(boolean enable) {
        mi.sendMessage(mi.getConsole(), rep(""));
        mi.sendMessage(mi.getConsole(), rep("&6-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"));
        mi.sendMessage(mi.getConsole(), rep("           &4Coins &fBy:  &7Beelzebu"));
        mi.sendMessage(mi.getConsole(), rep(""));
	String version = "";
	int spaces = (42 - ("v: " + mi.getVersion()).length()) / 2;
	for (int i = 0; i < spaces; i++) {
	    version += " ";
	}
	version += rep("&4v: &f" + mi.getVersion());
        mi.sendMessage(mi.getConsole(), version);
        mi.sendMessage(mi.getConsole(), rep("&6-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"));
        mi.sendMessage(mi.getConsole(), rep(""));
        // Only send this in the onEnable
        if (enable) {
            if (getConfig().getBoolean("Debug", false)) {
                log("Debug mode is enabled.");
            }
            if (isMySQL()) {
                log("Enabled to use MySQL.");
            } else {
                log("Enabled to use SQLite.");
            }
        }
    }

    public IMethods getMethods() {
        return mi;
    }

    public void debug(Object msg) {
        if (getConfig().getBoolean("Debug")) {
            mi.sendMessage(mi.getConsole(), (rep("&8[&cCoins&8] &cDebug: &7" + msg)));
        }
        logToFile(msg);
    }

    public void log(Object msg) {
        mi.log(msg);
        logToFile(msg);
    }

    private void logToFile(Object msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        File log = new File(getDataFolder(), "/logs/latest.log");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(log, true))) {
            try {
                writer.write("[" + sdf.format(System.currentTimeMillis()) + "] " + removeColor(msg.toString()));
                writer.newLine();
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.WARNING, "Can''t save the debug to the file", ex);
        }
    }

    public Boolean isOnline(UUID uuid) {
        return mi.isOnline(uuid);
    }

    public String getNick(UUID uuid) {
        return mi.getName(uuid) != null ? mi.getName(uuid) : getDatabase().getNick(uuid);
    }

    public UUID getUUID(String player) {
        return mi.getUUID(player) != null ? mi.getUUID(player) : getDatabase().getUUID(player);
    }

    public Database getDatabase() {
        if (getConfig().getBoolean("MySQL.Use")) {
            mysql = true;
            return db == null ? db = new MySQL(this) : db;
        } else {
            return db == null ? db = new SQLite(this) : db;
        }
    }

    public boolean isMySQL() {
        return mysql;
    }

    public String rep(String msg) {
        return msg.replaceAll("%prefix%", getConfig().getString("Prefix")).replaceAll("&", "§");
    }

    public String removeColor(String str) {
        return ChatColor.stripColor(rep(str)).replaceAll("Debug: ", "");
    }

    public IConfiguration getConfig() {
        return mi.getConfig();
    }

    public File getDataFolder() {
        return mi.getDataFolder();
    }

    public InputStream getResource(String filename) {
        return mi.getResource(filename);
    }

    public MessagesManager getMessages(String lang) {
        if (lang == null || lang.equals("")) {
            lang = "default";
        }
        lang = lang.split("_")[0];
        if (!messagesMap.containsKey(lang)) {
            messagesMap.put(lang, mi.getMessages(lang));
            return mi.getMessages(lang);
        }
        return messagesMap.get(lang);
    }

    public String getString(String path, String lang) {
        try {
            return rep(getMessages(lang).getString(path));
        } catch (NullPointerException ex) {
            mi.log("The string " + path + " does not exists in the messages_" + lang.split("_")[0] + ".yml file, please add this manually.");
            mi.log("If you belive that this is an error please contact to the developer.");
            debug(ex);
            return rep(getMessages("").getString(path));
        }
    }

    public ExecutorManager getExecutorManager() {
        return executorManager;
    }

    public boolean isBungee() {
        return mi instanceof BungeeMethods;
    }

    public void updateCache(UUID player, Double coins) {
        if (!isBungee()) {
            PluginMessage pm = new PluginMessage();
            pm.sendToBungeeCord("Update", "updateCache " + player + " " + coins);
        }
    }
    
    public void reloadMessages() {
        messagesMap.keySet().forEach((lang) -> {
            messagesMap.get(lang).reload();
        });
    }
}
