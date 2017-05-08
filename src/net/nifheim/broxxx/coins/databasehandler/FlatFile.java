package net.nifheim.broxxx.coins.databasehandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public FlatFile(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        try {
            if (dataFile == null) {
                Files.createFile(dataFile.toPath());
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

    public Double getOfflineCoins(OfflinePlayer p) {
        String localplayer = player(p);
        return data.getDouble("Players." + localplayer + ".Coins");
    }

    public String getCoinsString(Player p) {
        String localplayer = player(p);
        String coins = String.valueOf(data.getDouble("Players." + localplayer + ".Coins"));
        if (coins != null || !coins.equals("0.0")) {
            return coins;
        } else {
            return "0.0";
        }
    }

    public String getCoinsStringOffline(OfflinePlayer p) {
        String localplayer = player(p);
        String coins = String.valueOf(data.getDouble("Players." + localplayer + ".Coins"));
        if (coins != null || !coins.equals("0.0")) {
            return coins;
        } else {
            return "0.0";
        }
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
        if (plugin.getConfig().getString("Players." + localplayer) != null) {
            data.set("Players." + localplayer + ".Name", p.getName());
            data.set("Players." + localplayer + ".Lastlogin", System.currentTimeMillis());
        } else {
            data.set("Players." + localplayer + ".Coins", 0.0);
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
