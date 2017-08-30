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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.nifheim.beelzebu.coins.core.database.Database;
import net.nifheim.beelzebu.coins.core.database.MySQL;
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
    private FileManager fileUpdater;
    private ExecutorManager executorManager;

    public static Core getInstance() {
        return instance == null ? instance = new Core() : instance;
    }

    public void setup(IMethods methodinterface) {
        mi = methodinterface;
        fileUpdater = new FileManager(this);
        fileUpdater.copyFiles();
        fileUpdater.updateConfig();
        db = new MySQL(this);
        executorManager = new ExecutorManager();
    }

    public IMethods getMethods() {
        return mi;
    }

    public void debug(Object msg) {
        if (getConfig().getBoolean("Debug")) {
            mi.sendMessage(mi.getConsole(), (rep("&8[&cCoins&8] &cDebug: &7" + msg)));
        }
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

    public void log(Object msg) {
        mi.log(msg);
    }

    public String getNick(Object player) {
        return mi.getNick(player);
    }

    public String getNick(UUID uuid) {
        return mi.getNick(uuid);
    }

    public UUID getUUID(Object player) {
        return mi.getUUID(player);
    }

    public UUID getUUID(String player) {
        return mi.getUUID(player);
    }

    public Database getDatabase() {
        return db == null ? db = new MySQL(this) : db;
    }

    public String rep(String msg) {
        return msg
                .replaceAll("%prefix%", getConfig().getString("Prefix"))
                .replaceAll("&", "§");
    }

    public String removeColor(String str) {
        return ChatColor.stripColor(str).replaceAll("Debug: ", "");
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
        return mi.getMessages(lang);
    }

    public String getString(String path, String lang) {
        try {
            path = rep(getMessages(lang).getString(path));
        } catch (NullPointerException ex) {
            mi.log("The string " + path + " does not exists in the messages" + lang + ".yml file, please add this manually.");
            mi.log("If you belive that this is an error please contact to the developer.");
            path = rep(getMessages("").getString(path));
            Core.getInstance().debug(ex);
        }
        return path;
    }

    public ExecutorManager getExecutorManager() {
        return executorManager;
    }
}
