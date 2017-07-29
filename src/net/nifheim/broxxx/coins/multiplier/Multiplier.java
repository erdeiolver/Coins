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
package net.nifheim.broxxx.coins.multiplier;

import java.util.Set;

import net.nifheim.broxxx.coins.Main;
import net.nifheim.broxxx.coins.databasehandler.MySQL;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class Multiplier {

    private final FileConfiguration CONFIG = Main.getInstance().getConfig();
    private final MySQL MYSQL = Main.mysql;
    private String SERVER = CONFIG.getString("Multipliers.Server");
    private final Player ENABLER;
    private Boolean enabled;
    private Integer amount;
    private Long timeleft;

    public Multiplier() {
        ENABLER = MYSQL.getEnabler(SERVER);
    }

    public Multiplier(String sv) {
        SERVER = sv;
        ENABLER = MYSQL.getEnabler(SERVER);
    }

    /**
     * Create a multiplier for a player with the specified time.
     *
     * @param p The player to create the multiplier.
     * @param multiplier The multiplier.
     * @param minutes The time for the multiplier.
     */
    public void createMultiplier(Player p, Integer multiplier, Integer minutes) {
        MYSQL.createMultiplier(p, multiplier, minutes);
    }

    /**
     * Get the active multiplier countdown time formated in HHH:mm:ss
     *
     * @return The multiplier time formated.
     */
    public String getMultiplierTimeFormated() {
        Long endtime = MYSQL.getMultiplierTime(SERVER);
        String format;
        Long time = -75600000L + MYSQL.getMultiplierTime(SERVER);
        if (endtime > 86400000) {
            format = "%1$td, %1$tH:%1$tM:%1$tS";
        } else if (endtime > 3600000) {
            format = "%1$tH:%1$tM:%1$tS";
        } else {
            format = "%1$tM:%1$tS";
        }
        if (time <= 0) {
            return String.format(format, time);
        }
        return "Ninguno :(";
    }

    /**
     * Get the enabled multiplier amount for the Server.
     *
     * @return
     */
    public Integer getMultiplierAmount() {
        return MYSQL.getMultiplierAmount(SERVER);
    }

    /**
     * Use the multiplier of a player in the server by the multiplier id.
     *
     * @param p Player that has a multiplier.
     * @param id The id of the multiplier.
     * @param type The type of the multiplier.
     */
    public void useMultiplier(Player p, Integer id, MultiplierType type) {
        MYSQL.useMultiplier(p, id, SERVER, type);
    }

    /**
     * Get the multipliers of a player in this server.
     * <p>
     * If the server is set to null it shows all the multipliers for this
     * player</p>
     *
     * @param p The player to get the multipliers.
     * @return
     */
    public Set<Integer> getMultipliersFor(Player p) {
        return MYSQL.getMultipliersFor(p, SERVER);
    }

    /**
     * Get the enabler of a multiplier for this server.
     *
     * @return
     * @throws NullPointerException if the server doesn't has a multiplier
     * active or the multiplier wasn't enabled by a player.
     */
    public Player getEnabler() throws NullPointerException {
        return ENABLER;
    }
}
