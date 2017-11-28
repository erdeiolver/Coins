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
package net.nifheim.beelzebu.coins.core.importer;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.database.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Beelzebu
 */
public class ImportManager {

    private final Core core;

    public ImportManager(Core core) {
        this.core = core;
    }

    public void importFrom(PluginToImport plugin) {
        switch (plugin) {
            case PLAYER_POINTS:
                if (Bukkit.getPluginManager().getPlugin("PlayerPoints") == null) {
                    core.log("Seems that PlayerPoints is not installed in this server, you need to have this plugin installed to start the migration, you can remove it when it is finished.");
                    return;
                }
                core.log("Starting the migration of playerpoints data to coins, this may take a moment.");
                ConfigurationSection storage = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("PlayerPoints").getDataFolder(), "storage.yml")).getConfigurationSection("Points");
                storage.getKeys(false).forEach(uuid -> {
                    try {
                        CoinsAPI.createPlayer("unknow_player_from_pp", UUID.fromString(uuid), storage.getDouble(uuid, 0));
                        core.debug("Migrated the data for: " + uuid);
                    } catch (Exception ex) {
                        core.log("An error has ocurred while migrating the data for: " + uuid);
                        core.debug(ex);
                    }
                });
                core.log("The migration has completed, check the plugin logs for more information.");
                break;
            default:
                break;
        }
    }

    public void importFromStorage(StorageType storage) {
        switch (storage) {
            case MYSQL:
                Database mysql = new MySQL(core);
                if (core.getDatabase() instanceof MySQL) {
                    core.log("You can't migrate information from the same database that you are using.");
                    return;
                }
                Map<String, Double> mysqlData = mysql.getAllPlayers();
                if (!mysqlData.isEmpty()) {
                    core.log("Starting the migration from SQLite, this may take a moment.");
                    mysqlData.entrySet().forEach(entry -> {
                        String nick = null;
                        UUID uuid = null;
                        try {
                            nick = entry.getKey().split(",")[0];
                            uuid = UUID.fromString(entry.getKey().split(",")[1]);
                            double balance = entry.getValue();
                            CoinsAPI.createPlayer(nick, uuid, balance);
                            core.debug("Migrated the data for: " + uuid);
                        } catch (Exception ex) {
                            core.log("An error has ocurred while migrating the data for: " + nick + " (" + uuid + ")");
                            core.debug(ex);
                        }
                    });
                    core.log("The migration has completed, check the plugin logs for more information.");
                } else {
                    core.log("There are no users to migrate in the database.");
                }
                break;
            case SQLITE:
                Database sqlite = new SQLite(core);
                if (core.getDatabase() instanceof SQLite) {
                    if (core.getDatabase() instanceof MySQL) {
                        core.log("You can't migrate information from the same database that you are using.");
                        return;
                    }
                }
                Map<String, Double> sqliteData = sqlite.getAllPlayers();
                if (!sqliteData.isEmpty()) {
                    core.log("Starting the migration from SQLite, this may take a moment.");
                    sqliteData.entrySet().forEach(entry -> {
                        String nick = null;
                        UUID uuid = null;
                        try {
                            nick = entry.getKey().split(",")[0];
                            uuid = UUID.fromString(entry.getKey().split(",")[1]);
                            double balance = entry.getValue();
                            CoinsAPI.createPlayer(nick, uuid, balance);
                            core.debug("Migrated the data for: " + uuid);
                        } catch (Exception ex) {
                            core.log("An error has ocurred while migrating the data for: " + nick + " (" + uuid + ")");
                            core.debug(ex);
                        }
                    });
                    core.log("The migration has completed, check the plugin logs for more information.");
                } else {
                    core.log("There are no users to migrate in the database.");
                }
                break;
            default:
                break;
        }
    }

    public enum PluginToImport {
        PLAYER_POINTS;
    }

    public enum StorageType {
        MYSQL,
        SQLITE;
    }
}
