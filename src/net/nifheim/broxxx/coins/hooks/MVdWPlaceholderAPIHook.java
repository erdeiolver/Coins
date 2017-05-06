package net.nifheim.broxxx.coins.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MVdWPlaceholderAPIHook {

    public static void hook(Main plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceHolderAPI")) {
            PlaceholderAPI.registerPlaceholder(plugin, "coins", (PlaceholderReplaceEvent e) -> {
                final Player p = e.getPlayer();
                final OfflinePlayer offp = e.getPlayer();
                if (offp == null) {
                    return "Player is needed!";
                }
                String coins = CoinsAPI.getCoinsString(p);
                if (coins != null) {
                    return coins;
                } else {
                    return "0";
                }
            });
        }
    }
}
