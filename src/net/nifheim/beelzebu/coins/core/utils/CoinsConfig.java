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
package net.nifheim.beelzebu.coins.core.utils;

import java.util.HashSet;
import java.util.Set;
import net.nifheim.beelzebu.coins.core.executor.Executor;
import org.bukkit.Bukkit;

/**
 *
 * @author Beelzebu
 */
public abstract class CoinsConfig implements IConfiguration {

    private final Set<Executor> executors = new HashSet<>();

    public void loadExecutors() {
        getConfigurationSection("Command executor").forEach((id) -> {
            executors.add(new Executor(id, getString("Command executor." + id + ".Displayname", id), getDouble("Command executor." + id + ".Cost", 0), getStringList("Command executor." + id + ".Command")));
        });
    }

    public Set<Executor> getExecutors() {
        return executors;
    }

    // #EasterEgg
    public boolean vaultMultipliers() {
        return getBoolean("Vault.Use Multipliers", false);
    }

    public boolean useBungee() {
        try {
            return Bukkit.spigot().getConfig().getBoolean("settings.bungeecord");
        } catch (Exception ex) {
            return false;
        }
    }
}
