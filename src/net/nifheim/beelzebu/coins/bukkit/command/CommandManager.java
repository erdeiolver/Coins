/**
 * This file is part of Coins
 *
 * Copyright (C) 2017 Beelzebu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.nifheim.beelzebu.coins.bukkit.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

/**
 *
 * @author Beelzebu
 */
public class CommandManager {

    private final Main plugin;
    private final Core core;
    private final String commandName;
    private final List<String> commandAliases = new ArrayList<>();

    public CommandManager(Main main) {
        plugin = main;
        core = Core.getInstance();
        commandName = core.getConfig().getString("General.Command.Name", "coins");
    }

    public void registerCommand() {
        try {
            core.getConfig().getStringList("General.Command.Aliases").forEach((str) -> {
                commandAliases.add(str);
            });

            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            String commandDescription = core.getConfig().getString("General.Command.Description", "Base command of the Coins plugin");
            String commandUsage = core.getConfig().getString("General.Command.Usage", "/coins");
            String commandPermission = core.getConfig().getString("General.Command.Permission", "coins.use");

            commandAliases.forEach((str) -> {
                unregisterCommand(str);
            });
            unregisterCommand(commandName);

            commandMap.register(commandName, new CoinsCommand(commandName, commandDescription, commandUsage, commandPermission, commandAliases));

        } catch (SecurityException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException ex) {
            core.getMethods().log("An internal error has ocurred while registering the command for the plugin.");
            core.debug(ex.getCause().getMessage());
        }
    }

    public void unregisterCommand(final String command) {
        if (plugin.getServer() != null && plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            final SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
            try {
                final Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                CommandMap map = (CommandMap) field.get(manager);
                final Field field2 = SimpleCommandMap.class.getDeclaredField("knownCommands");
                field2.setAccessible(true);
                final Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) field2.get(map);
                knownCommands.entrySet().stream().filter((entry) -> (entry.getKey().equals(command))).forEachOrdered((entry) -> {
                    entry.getValue().unregister(map);
                });
                knownCommands.remove(command);
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException ex) {
                core.getMethods().log("An internal error has ocurred while registering the command for the plugin.");
                core.debug(ex.getCause().getMessage());
            }
        }
    }
}
