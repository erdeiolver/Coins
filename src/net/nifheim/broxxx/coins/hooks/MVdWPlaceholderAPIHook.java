package net.nifheim.broxxx.coins.hooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import java.sql.SQLException;
import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MVdWPlaceholderAPIHook {
    private static CoinsAPI api;
    public static void hook(Main plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceHolderAPI")) {
            PlaceholderAPI.registerPlaceholder(plugin, "coins",
                    new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            final Player p = e.getPlayer();
                            final OfflinePlayer offp = e.getPlayer();
                            if (offp == null) {
                                return "Player is needed!";
                            }
                            String coins = "";
                            try {
                                coins = api.getCoinsString(p);
                            } catch (SQLException ex) {}
                            if (coins != null){
                                return coins;
                            }
                            else {
                            return "0";
                            }
                        }
                    });
        }
    }
}
