package net.nifheim.broxxx.coins.listener;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    //    private Main plugin;
    private FileConfiguration config = Main.getInstance().getConfig();
    private static CoinsAPI api;

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().toLowerCase();
        if (config.getInt("Command Cost."+msg) != 0) {
            try {
                if (api.getCoins(e.getPlayer()) < config.getInt("Command Cost."+msg)) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Main.getInstance().replacener(config.getString("Messages.Errors.No Coins")));
                }
                else {
                    api.takeCoins(e.getPlayer(), config.getInt("Command Cost."+msg));
                }
            } catch (SQLException ex) {}
        }
    }
}
