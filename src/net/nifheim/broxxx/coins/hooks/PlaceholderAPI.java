package net.nifheim.broxxx.coins.hooks;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends EZPlaceholderHook {

    private final Main plugin;

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
            String coinsamount = CoinsAPI.getCoinsString(p);
            return coinsamount;
        }
        return "0";
    }
}
