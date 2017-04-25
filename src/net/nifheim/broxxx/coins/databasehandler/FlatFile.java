package net.nifheim.broxxx.coins.databasehandler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.broxxx.coins.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class FlatFile {

    Main plugin;
    File dataFile;
    YamlConfiguration data;

    public FlatFile(Main plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void createPlayer(Player p) {
        data.set("Players." + p.getUniqueId() + ".Coins", 0);
        data.set("Players." + p.getUniqueId() + ".Name", p.getName());
        data.set("Players." + p.getUniqueId() + ".Lastlogin", System.currentTimeMillis());
        saveData();
    }

    public void setCoins(Player p, Integer coins) {
        data.set("Players." + p.getUniqueId() + ".Coins", coins);
        saveData();
    }

    public void updatePlayer(Player p) {
        data.set("Players." + p.getUniqueId() + ".Name", p.getName());
        saveData();
    }

    public void saveData() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new SaveFileTask(plugin));
    }

    private static class SaveFileTask implements Runnable {

        private final Main plugin;

        private SaveFileTask(Main plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {

            YamlConfiguration dataYml = new YamlConfiguration();

            try {
                dataYml.save(new File(plugin.getDataFolder(), "data.yml"));
            } catch (IOException ex) {
                Logger.getLogger(FlatFile.class.getName()).log(Level.SEVERE, null, ex);
                plugin.getLogger().log(Level.SEVERE, "Unable to write to data.yml!");
            }
        }
    }
}
