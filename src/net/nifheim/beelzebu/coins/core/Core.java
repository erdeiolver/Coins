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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.databasehandler.Database;
import net.nifheim.beelzebu.coins.core.databasehandler.MySQL;

/**
 *
 * @author Beelzebu
 */
public class Core {

    private static Core instance;
    private MethodInterface mi;
    private Database db;

    public static Core getInstance() {
        return instance == null ? instance = new Core() : instance;
    }

    public void setup(MethodInterface methodinterface) {
        mi = methodinterface;
        db = new MySQL(this);
    }

    public MethodInterface getMethods() {
        return mi;
    }

    public void debug(Object msg) {
        if (mi.getBoolean(mi.getConfig(), "Debug")) {
            mi.sendMessage(mi.getConsole(), (rep("&8[&cCoins&8] &cDebug: &7" + msg)));
        }
        File log = new File(mi.getDataFolder() + "/Debug.log");
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
                .replaceAll("&", "§");
    }
    
    public String removeColor(String str) {
        return "";
    }
}
