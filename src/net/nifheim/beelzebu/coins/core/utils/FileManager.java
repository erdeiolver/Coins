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
            if (core.getConfig().getInt("version") == 10) {
                core.log("The config file is up to date.");
            } else {
                switch (core.getConfig().getInt("version")) {
                    case 7:
                        index = lines.indexOf("  Purge:") + 3;
                        lines.addAll(index, Arrays.asList(
                                "    Logs:",
                                "      Days: 10 # The days to keep plugin logs."
                        ));
                        index = lines.indexOf("version: 7");
                        lines.set(index, "version: 8");
                        core.log("Configuration file updated to v8");
                        break;
                    case 8:
                        index = lines.indexOf("  Purge:") + 5;
                        lines.addAll(index, Arrays.asList(
                                "  Executor Sign:",
                                "    1: '&c&lCoins'",
                                "    2: '%executor_displayname%'",
                                "    3: '%ececutor_cost%'",
                                "    4: ''"
                        ));
                        index = lines.indexOf("version: 8");
                        lines.set(index, "version: 9");
                        core.log("Configuration file updated to v9");
                        break;
                    case 9:
                        index = lines.indexOf("MySQL:") - 2;
                        lines.addAll(index, Arrays.asList(
                                "# Here you can enable Vault to make this plugin manage all the Vault transactions.",
                                "Vault:",
                                "  Use: false",
                                "  # Names used by vault for the currency.",
                                "  Name:",
                                "    Singular: 'Coin'",
                                "    Plural: 'Coins'",
                                ""
                        ));
                        index = lines.indexOf("version: 9");
                        lines.set(index, "version: 10");
                        core.log("Configuraton file updated to v10");
                        break;
                    default:
                        core.log("The config file is up to date.");
                        break;
                }
            }
            FileUtils.writeLines(configFile, lines);
            core.getConfig().reload();
        } catch (IOException ex) {
            core.log("An unexpected error occurred while updating the config file.");
            core.debug(ex.getMessage());
        }
    }

    public void updateMessages() {
        try {
            List<String> lines = FileUtils.readLines(messagesFile, Charsets.UTF_8);
            int index;
            if (core.getMessages("").getInt("version") == 5) {
                index = lines.indexOf("  Multiplier:") - 1;
                lines.add(index, "  Multiplier Create: '%prefix% &cPlease use &f/coins multiplier create <name> <amount> <minutes>'");
                index = lines.indexOf("Multipliers:");
                lines.addAll(index + 1, Arrays.asList(
                        "  Menu:",
                        "    Title: '&6Multipliers GUI'",
                        "  Placeholders:",
                        "    Enabler:",
                        "      Message: '&8➠ Multiplier enabled by &a%enabler%'",
                        "      Anyone: '&8➠ No multiplier active :('"
                ));
                index = lines.indexOf("  Set: '" + core.getMessages("").getString("Multipliers.Set") + "'");
                if (index != -1) {
                    lines.remove(index);
                }
                index = lines.indexOf("version: 5");
                lines.set(index, "version: 6");
                core.log("Updated messages.yml file to v6");
            }
            if (core.getMessages("").getInt("version") == 6) {
                index = lines.indexOf("version: 6");
                lines.set(index, "version: 7");
                index = lines.indexOf("Multipliers:");
                lines.remove(index);
                index = lines.indexOf("  Menu:");
                lines.add(index, "Multipliers:");
                core.log("Updated messages.yml file to v7");
            }
            if (core.getMessages("").getInt("version") == 7) {
                index = lines.indexOf("version: 7");
                lines.set(index, "version: 8");
                index = lines.indexOf("  Menu:") + 2;
                lines.addAll(index, Arrays.asList(
                        "    Confirm:",
                        "      Title: '&8Are you sure?'",
                        "      Accept: '&a¡YES!'",
                        "      Decline: '&cNope'",
                        "    Multipliers:",
                        "      Name: '&6Multiplier &cx%amount%'",
                        "      Lore:",
                        "      - ''",
                        "      - '&7Amount: &c%amount%'",
                        "      - '&7Server: &c%server%'",
                        "      - '&7Minutes: &c%minutes%'",
                        "      - ''",
                        "      - '&7ID: &c#%id%'",
                        "    No Multipliers:",
                        "      Name: '&cYou don''t have any multiplier :('",
                        "      Lore:",
                        "      - ''",
                        "      - '&7You can buy multipliers in our store'",
                        "      - '&6&nstore.servername.net'"
                ));
                core.log("Updated messages.yml file to v8");
            }
            if (core.getMessages("").getInt("version") == 8) {
                index = lines.indexOf("version: 8");
                lines.set(index, "version: 9");
                lines.removeAll(Arrays.asList(
                        "# Coins messages file.",
                        "# If you need support or find a bug open a issuse in",
                        "# the official github repo https://github.com/Beelzebu/Coins/issuses/",
                        "",
                        "# The version of this file, don't edit!"
                ));
                lines.addAll(0, Arrays.asList(
                        "# Coins messages file.",
                        "# If you need support or find a bug open a issuse in",
                        "# the official github repo https://github.com/Beelzebu/Coins/issuses/",
                        "",
                        "# The version of this file, is used to auto update this file, don't change it",
                        "# unless you know what you do."
                ));
                core.log("Updated messages.yml file to v9");
            }
            FileUtils.writeLines(messagesFile, lines);
        } catch (IOException ex) {
            core.log("An unexpected error occurred while updating the messages.yml file.");
            core.debug(ex.getMessage());
        }
        try {
            File messages_esFile = new File(core.getDataFolder(), "messages_es.yml");
            List<String> lines = FileUtils.readLines(messages_esFile, Charsets.UTF_8);
            int index;
            if (core.getMessages("es").getInt("version") == 5) {
                index = lines.indexOf("  Multiplier:") - 1;
                lines.add(index, "  Multiplier Create: '%prefix% &cPor favor usa &f/coins multiplier create <nombre> <cantidad> <minutos>'");
                index = lines.indexOf("Multipliers:");
                lines.addAll(index + 1, Arrays.asList(
                        "  Menu:",
                        "    Title: '&6Menú de multiplicadores'",
                        "  Placeholders:",
                        "    Enabler:",
                        "      Message: '&8➠ Multiplicador activado por &a%enabler%'",
                        "      Anyone: '&8➠ No hay multiplicadores activos :('"
                ));
                index = lines.indexOf("  Set: '" + core.getMessages("es").getString("Multipliers.Set") + "'");
                if (index != -1) {
                    lines.remove(index);
                }
                index = lines.indexOf("version: 5");
                lines.set(index, "version: 6");
                core.log("Updated messages_es.yml file to v6");
            }
            if (core.getMessages("es").getInt("version") == 6) {
                index = lines.indexOf("version: 6");
                lines.set(index, "version: 7");
                index = lines.indexOf("Multipliers:");
                lines.remove(index);
                index = lines.indexOf("  Menu:");
                lines.add(index, "Multipliers:");
                core.log("Updated messages_es.yml file to v7");
            }
            if (core.getMessages("es").getInt("version") == 7) {
                index = lines.indexOf("version: 7");
                lines.set(index, "version: 8");
                index = lines.indexOf("  Menu:") + 2;
                lines.addAll(index, Arrays.asList(
                        "    Confirm:",
                        "      Title: '&8¿Estás seguro?'",
                        "      Accept: '&a¡SI!'",
                        "      Decline: '&cNo'",
                        "    Multipliers:",
                        "      Name: '&6Multiplicador &cx%amount%'",
                        "      Lore:",
                        "      - ''",
                        "      - '&7Cantidad: &c%amount%'",
                        "      - '&7Servidor: &c%server%'",
                        "      - '&7Minutos: &c%minutes%'",
                        "      - ''",
                        "      - '&7ID: &c#%id%'",
                        "    No Multipliers:",
                        "      Name: '&cNo tienes ningún multiplicador :('",
                        "      Lore:",
                        "      - ''",
                        "      - '&7Puedes comprar multiplicadores en nuestra tienda'",
                        "      - '&6&nstore.servername.net'"
                ));
                core.log("Updated messages_es.yml file to v8");
            }
            index = lines.indexOf("      - '&6&nstore.servername.net'\"");
            if (index != -1) {
                lines.set(index, "      - '&6&nstore.servername.net'");
            }
            if (core.getMessages("es").getInt("version") == 8) {
                index = lines.indexOf("version: 8");
                lines.set(index, "version: 9");
                lines.removeAll(Arrays.asList(
                        "# Coins messages file.",
                        "# If you need support or find a bug open a issuse in",
                        "# the official github repo https://github.com/Beelzebu/Coins/issuses/",
                        "",
                        "# The version of this file, don't edit!"
                ));
                lines.addAll(0, Arrays.asList(
                        "# Coins messages file.",
                        "# Si necesitas soporte o encuentras un error por favor abre un ticket en el",
                        "# repositorio oficial de github https://github.com/Beelzebu/Coins/issuses/",
                        "",
                        "# La versión de este archivo, es usado para actualizarlo automáticamente, no lo cambies",
                        "# a menos que sepas lo que haces."
                ));
                core.log("Updated messages_es.yml file to v9");
            }
            FileUtils.writeLines(messages_esFile, lines);
        } catch (IOException ex) {
            core.log("An unexpected error occurred while updating the messages_es.yml file.");
            core.debug(ex.getMessage());
        }
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
        File zh = new File(core.getDataFolder(), "messages_zh.yml");
        if (!es.exists()) {
            copy(core.getResource("messages_zh.yml"), zh);
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
                latestLog.delete();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class
                        .getName()).log(Level.WARNING, "An unexpected error has ocurred while trying to compress the latest log file. {0}", ex.getMessage());
            }
        }
        File[] fList = logsFolder.listFiles();
        // Auto purge for old logs
        for (File file : fList) {
            if (file.isFile() && file.getName().contains(".gz")) {
                if ((System.currentTimeMillis() - file.lastModified()) >= core.getConfig().getInt("General.Purge.Logs.Days") * 86400000L) {
                    file.delete();
                }
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
