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
package net.nifheim.broxxx.coins.databasehandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.Main;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class FlatFile {

    private final Main plugin;
    private final File dataFile;
    private final YamlConfiguration data;
    private String player;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public FlatFile(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        try {
            if (dataFile == null) {
                Files.createFile(dataFile.toPath());
                plugin.console.sendMessage("§4CREATING DATA FILE");
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Can't save data file", ex);
        }
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    private String player(OfflinePlayer p) {
        player = p.getUniqueId().toString();
        return player;
    }

    public void setCoins(Player p, Integer coins) {
        data.set("Players." + p.getUniqueId() + ".Coins", coins);
        saveData();
    }

    public void setCoinsOffline(OfflinePlayer p, Integer coins) {
        data.set("Players." + p.getUniqueId() + ".Coins", coins);
        saveData();
    }

    public Double getCoins(Player p) {
        String localplayer = player(p);
        return data.getDouble("Players." + localplayer + ".Coins");
    }

    public Double getCoinsOffline(OfflinePlayer p) {
        String localplayer = player(p);
        return data.getDouble("Players." + localplayer + ".Coins");
    }

    public void addCoins(Player p, double coins) {
        String localplayer = player(p);
        double oldcoins = data.getDouble("Players." + localplayer + ".Coins");
        data.set("Players." + localplayer + ".Coins", oldcoins + coins);
        saveData();
    }

    public void addCoinsOffline(OfflinePlayer p, double coins) {
        String localplayer = player(p);
        double oldcoins = data.getDouble("Players." + localplayer + ".Coins");
        data.set("Players." + localplayer + ".Coins", oldcoins + coins);
        saveData();
    }

    public void takeCoins(Player p, double coins) {
        String localplayer = player(p);
        double oldcoins = data.getDouble("Players." + localplayer + ".Coins");
        data.set("Players." + localplayer + ".Coins", oldcoins - coins);
        saveData();
    }

    public void takeCoinsOffline(OfflinePlayer p, double coins) {
        String localplayer = player(p);
        double oldcoins = data.getDouble("Players." + localplayer + ".Coins");
        data.set("Players." + localplayer + ".Coins", oldcoins - coins);
        saveData();
    }

    public void resetCoins(Player p) {
        String localplayer = player(p);
        data.set("Players." + localplayer + ".Coins", 0.0);
        saveData();
    }

    public void resetCoinsOffline(OfflinePlayer p) {
        String localplayer = player(p);
        data.set("Players." + localplayer + ".Coins", 0.0);
        saveData();
    }

    public void setCoins(Player p, double coins) {
        String localplayer = player(p);
        data.set("Players." + localplayer + ".Coins", coins);
        saveData();
    }

    public void setCoinsOffline(OfflinePlayer p, double coins) {
        String localplayer = player(p);
        data.set("Players." + localplayer + ".Coins", coins);
        saveData();
    }

    public boolean isindb(OfflinePlayer p) {
        String localplayer = player(p);
        return (data.getString("Players." + localplayer) != null);
    }

    public void createPlayer(Player p) {
        String localplayer = player(p);
        if (data.getConfigurationSection("Players." + localplayer) == null) {
            data.set("Players." + localplayer + ".Coins", 0.0);
            data.set("Players." + localplayer + ".Name", p.getName());
            data.set("Players." + localplayer + ".Lastlogin", System.currentTimeMillis());
        } else {
            data.set("Players." + localplayer + ".Name", p.getName());
            data.set("Players." + localplayer + ".Lastlogin", System.currentTimeMillis());
        }
        saveData();
    }

    public void saveData() {
        try {
            data.save(dataFile);

        } catch (IOException ex) {
            Logger.getLogger(FlatFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
