package net.nifheim.broxxx.coins.listener;

import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    private final Main plugin = Main.getInstance();
    private final FileConfiguration config = plugin.getConfig();
    private final FileConfiguration messages = plugin.getMessages();

    @EventHandler
    public void onCommandEvent(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().toLowerCase();
        if (config.getDouble("Command Cost." + msg) != 0) {
            if (CoinsAPI.getCoins(e.getPlayer()) < config.getDouble("Command Cost." + msg)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(plugin.rep(messages.getString("Errors.No Coins")));
            } else {
                CoinsAPI.takeCoins(e.getPlayer(), config.getDouble("Command Cost." + msg));
            }
        }
    }
}
