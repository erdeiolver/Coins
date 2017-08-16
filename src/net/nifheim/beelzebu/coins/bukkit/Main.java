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
package net.nifheim.beelzebu.coins.bukkit;

import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.bukkit.command.CommandManager;
import net.nifheim.beelzebu.coins.bukkit.listener.*;
import net.nifheim.beelzebu.coins.bukkit.utils.Configuration;
import net.nifheim.beelzebu.coins.bukkit.utils.placeholders.PlaceholderAPI;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.utils.IConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static Main instance;
    private CommandManager commandManager;

    private PlaceholderAPI placeholderAPI;
    private Configuration configuration;
    private Core core = Core.getInstance();

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        configuration = new Configuration(this);
        core.setup(new BukkitMethods());
        commandManager = new CommandManager(this);
        motd(true);
        loadManagers();

        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            Bukkit.getOnlinePlayers().forEach((p) -> {
                CoinsAPI.createPlayer(p.getName(), p.getUniqueId());
            });
        }, 30);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        motd(false);
    }

    private void loadManagers() {
        // Create the command
        commandManager.registerCommand();
        // Hook placeholders
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            core.getMethods().log("PlaceholderAPI found, hooking in ");
            placeholderAPI = new PlaceholderAPI(this);
            placeholderAPI.hook();
        }
    }

    private void motd(Boolean enable) {
        if (getDescription().getVersion().contains("BETA")) {
            console.sendMessage(core.rep(""));
            console.sendMessage(core.rep("    &c+==========================+"));
            console.sendMessage(core.rep("    &c|    &4Coins &fBy: &7Beelzebu&c    |"));
            console.sendMessage(core.rep("    &c|--------------------------|"));
            console.sendMessage(core.rep("    &c|       &4v:&f" + getDescription().getVersion() + "       &c|"));
            console.sendMessage(core.rep("    &c+==========================+"));
            console.sendMessage(core.rep(""));
            console.sendMessage(core.rep("&cThis is a BETA, please report bugs!"));
        } else {
            console.sendMessage(core.rep(""));
            console.sendMessage(core.rep("    &c+======================+"));
            console.sendMessage(core.rep("    &c|   &4Coins &fBy: &7Beelzebu&c   |"));
            console.sendMessage(core.rep("    &c|----------------------|"));
            console.sendMessage(core.rep("    &c|       &4v:&f" + getDescription().getVersion() + "        &c|"));
            console.sendMessage(core.rep("    &c+====================+"));
            console.sendMessage(core.rep(""));
        }
        // Only send this in the onEnable
        if (enable) {
            if (getConfig().getBoolean("Debug", false)) {
                core.getMethods().log("Debug mode is enabled.");
            }
        }
    }

    public IConfiguration getConfiguration() {
        return configuration;
    }
}
