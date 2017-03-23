package net.nifheim.broxxx.coins.hooks;

import java.sql.SQLException;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends EZPlaceholderHook {

    private final Main plugin;
    private CoinsAPI api;

    public PlaceholderAPI(Main plugin) {
        super(plugin, "coins");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String coins) {
        if (p == null) {
            return "Player needed!";
        }
        if (coins.equalsIgnoreCase("amount")) {
            String coinsamount = "";
            try {
                coinsamount = api.getCoinsString(p);
            } catch (SQLException ex) {
            }
            return coinsamount;
        }
        return "0";
    }
}
