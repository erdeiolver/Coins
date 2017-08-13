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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Beelzebu
 */
public class FileUtils {

    private final Core core;
    private final File messagesFile;
    private FileConfiguration messages;
    private final File configFile;

    public FileUtils(Core c) {
        core = c;
        messagesFile = new File(core.getDataFolder(), "messages.yml");
        configFile = new File(core.getDataFolder(), "config.yml");
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
        // TODO: do this with a writer for add comments.   
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
        File messagesFile_ = new File(core.getDataFolder(), "messages" + (locale.length() == 2 ? "_" + locale : locale) + ".yml");
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
        core.getDataFolder().mkdirs();

        if (!messagesFile.exists()) {
            copy(core.getResource("messages.yml"), messagesFile);
        }
        if (!configFile.exists()) {
            copy(core.getResource("config.yml"), configFile);
        }
        File es = new File(core.getDataFolder(), "messages_es.yml");
        if (!es.exists()) {
            copy(core.getResource("messages_es.yml"), es);
        }
    }
}
