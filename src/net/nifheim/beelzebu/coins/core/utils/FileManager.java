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
package net.nifheim.beelzebu.coins.core.utils;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import net.nifheim.beelzebu.coins.core.Core;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Beelzebu
 */
public class FileManager {

    private final Core core;
    private final File messagesFile;
    private final File configFile;
    private final File logsFolder;

    public FileManager(Core c) {
        core = c;
        messagesFile = new File(core.getDataFolder(), "messages.yml");
        configFile = new File(core.getDataFolder(), "config.yml");
        logsFolder = new File(core.getDataFolder(), "logs");
        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }
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
            Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "Can''t copy the file {0} to the plugin data folder. {1}", new Object[]{file.getName(), e.getMessage()});
        }
    }

    public void updateConfig() {
        try {
            List<String> lines = FileUtils.readLines(configFile, Charsets.UTF_8);
            int index;
            if (core.getConfig().getInt("version") < 5) {
                index = lines.indexOf("version: " + core.getConfig().getInt("version"));
                if (index != -1) {
                    lines.set(index, "version: 5");
                    lines.addAll(index + 1, Arrays.asList(
                            "",
                            "# This is the prefix used in all the messages.",
                            "Prefix: '&c&lCoins &6&l>&7'"));
                }
                lines.remove("  Use: " + core.getConfig().getBoolean("MySQL.Use"));
                index = lines.indexOf("#  Connection Interval: 5");
                if (index != -1) {
                    lines.set(index, "  Connection Interval: 5");
                }
                index = lines.indexOf("  Connection Interval: " + core.getConfig().getInt("MySQL.Connection Interval"));
                if (index != -1) {
                    if (!lines.containsAll(Arrays.asList(
                            "# Plugin general configurations.",
                            "General:",
                            "  # Here you can define the starting coins of a player when is registred in the",
                            "  # database or his coins are reset with \"/coins reset\"",
                            "  Starting Coins: 0"))) {
                        lines.addAll(index + 1, Arrays.asList(
                                "",
                                "# Plugin general configurations.",
                                "General:",
                                "  # Here you can define the starting coins of a player when is registred in the",
                                "  # database or his coins are reset with \"/coins reset\"",
                                "  Starting Coins: 0",
                                "  # Here you can configure the base command of the plugin.",
                                "  Command:",
                                "    Name: '" + core.getConfig().getString("Command.Name") + "'",
                                "    Description: '" + core.getConfig().getString("Command.Description") + "'",
                                "    Usage: '" + core.getConfig().getString("Command.Usage") + "'",
                                "    Permission: '" + core.getConfig().getString("Command.Permission") + "'",
                                "    Aliases:",
                                "  # Here you can configure the autopurge of inactive accounts.",
                                "  Purge:",
                                "    Enabled: true # If this is true the old accouns would be purged at server startup.",
                                "    Days: 60 # The time in days before deleting an account.",
                                ""));
                        index = lines.indexOf("    Aliases:");
                        for (String alias : core.getConfig().getStringList("Command.Aliases")) {
                            lines.add(index++, "  - '" + alias + "'");
                        }
                    } else if (core.getConfig().getString("General.Command.Name") == null) {
                        index = lines.indexOf("  Starting Coins: 0");
                        lines.addAll(index + 1, Arrays.asList("  # Here you can configure the base command of the plugin.",
                                "  Command:",
                                "    Name: '" + core.getConfig().getString("Command.Name") + "'",
                                "    Description: '" + core.getConfig().getString("Command.Description") + "'",
                                "    Usage: '" + core.getConfig().getString("Command.Usage") + "'",
                                "    Permission: '" + core.getConfig().getString("Command.Permission") + "'",
                                "    Aliases:",
                                "  # Here you can configure the autopurge of inactive accounts.",
                                "  Purge:",
                                "    Enabled: true # If this is true the old accouns would be purged at server startup.",
                                "    Days: 60 # The time in days before deleting an account.",
                                ""));
                        index = lines.indexOf("    Aliases:");
                        for (String alias : core.getConfig().getStringList("Command.Aliases")) {
                            lines.add(index++, "  - '" + alias + "'");
                        }
                    }
                }
                if (core.getConfig().getConfigurationSection("Command") != null) {
                    lines.removeAll(Arrays.asList(
                            "# Here you can configure the base command of the plugin.",
                            "Command:",
                            "  Name: '" + core.getConfig().getString("Command.Name") + "'",
                            "  Description: '" + core.getConfig().getString("Command.Description") + "'",
                            "  Usage: '" + core.getConfig().getString("Command.Usage") + "'",
                            "  Permission: '" + core.getConfig().getString("Command.Permission") + "'",
                            "  Aliases:"));
                    core.getConfig().getStringList("Command.Aliases").forEach((alias) -> {
                        lines.removeAll(Arrays.asList(
                                "  - '" + alias + "'"));
                    });
                }
                if (core.getConfig().getInt("Multipliers.Amount") >= 0) {
                    index = lines.indexOf("  Amount: " + core.getConfig().getInt("Multipliers.Amount"));
                    if (index != -1) {
                        lines.remove("  # You should't touch this number, this can be set with /coins multiplier set");
                        lines.set(index, "");
                    }
                }
                if (core.getConfig().getConfigurationSection("Multipliers") != null) {
                    index = lines.indexOf("Multipliers:");
                    if (index != -1) {
                        lines.addAll(index + 1, Arrays.asList(
                                "  # Here you can configure some aspects of the GUI of multipliers",
                                "  GUI:",
                                "    Close:",
                                "      # If you're using 1.8 please check ,http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html",
                                "      # If you're using 1.9+ use https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html",
                                "      Sound: 'UI_BUTTON_CLICK'",
                                "      Pitch: '1'"));
                    }
                }
                FileUtils.writeLines(configFile, lines);
                core.getConfig().reload();
                core.getMethods().log("Configuration file updated to v5.");
            } else if (core.getConfig().getInt("version") == 5) {
                index = lines.indexOf("MySQL:") + 1;
                lines.add(index, "  Use: true");
                index = lines.indexOf("version: 5");
                lines.set(index, "version: 6");
                FileUtils.writeLines(configFile, lines);
                core.getConfig().reload();
                core.getMethods().log("Configuration file updated to v6.");
            } else {
                core.getMethods().log("The config file is up to date.");
            }
        } catch (IOException ex) {
            core.getMethods().log("An unexpected error occurred while updating the config file.");
            core.debug(ex.getMessage());
        }
    }

    public void updateMessages() {
        // TODO: do this with a writer for add comments.
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File latestLog = new File(logsFolder, "latest.log");
        if (latestLog.exists()) {
            try {
                int filen = 1;
                while (new File(logsFolder, sdf.format(latestLog.lastModified()) + "-" + filen + ".log.gz").exists()) {
                    filen++;
                }
                gzipFile(Files.newInputStream(latestLog.toPath()), logsFolder + "/" + sdf.format(latestLog.lastModified()) + "-" + filen + ".log.gz");
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.WARNING, "An unexpected error has ocurred while trying to compress the latest log file. {0}", ex.getMessage());
            }
        }
    }

    private void gzipFile(InputStream in, String to) throws IOException {
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(to));
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        in.close();
        out.close();
    }
}
