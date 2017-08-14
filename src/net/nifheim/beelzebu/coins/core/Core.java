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
package net.nifheim.beelzebu.coins.core;

import net.nifheim.beelzebu.coins.core.utils.IMethods;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.databasehandler.Database;
import net.nifheim.beelzebu.coins.core.databasehandler.MySQL;
import net.nifheim.beelzebu.coins.core.utils.IConfiguration;
import net.nifheim.beelzebu.coins.core.utils.MessagesManager;

/**
 *
 * @author Beelzebu
 */
public class Core {

    private static Core instance;
    private IMethods mi;
    private Database db;

    public static Core getInstance() {
        return instance == null ? instance = new Core() : instance;
    }

    public void setup(IMethods methodinterface) {
        mi = methodinterface;
        db = new MySQL(this);
    }

    public IMethods getMethods() {
        return mi;
    }

    public void debug(Object msg) {
        if (getConfig().getBoolean("Debug")) {
            mi.sendMessage(mi.getConsole(), (rep("&8[&cCoins&8] &cDebug: &7" + msg)));
        }
        File log = new File(getDataFolder(),"Debug.log");
        BufferedWriter writer = null;
        // TODO Java 9:
        // try (writer = new BufferedWriter(new FileWriter(log, true))) {
        try {
            writer = new BufferedWriter(new FileWriter(log, true));
            writer.write(removeColor(msg.toString()));
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
                .replaceAll("&", "§")
                .replaceAll("%prefix%", getConfig().getString("Prefix"));
    }
    
    public String removeColor(String str) {
        return "";
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
}
