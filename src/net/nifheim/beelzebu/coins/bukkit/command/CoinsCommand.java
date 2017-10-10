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

import java.util.List;
import net.milkbowl.vault.permission.Permission;
import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.bukkit.utils.MultipliersGUI;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.executor.Executor;
import net.nifheim.beelzebu.coins.core.utils.IConfiguration;
import net.nifheim.beelzebu.coins.core.utils.IMethods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class CoinsCommand extends BukkitCommand {

    private final Core core = Core.getInstance();
    private final IConfiguration config = core.getConfig();
    private final IMethods im = core.getMethods();
    private String lang = "";

    public CoinsCommand(String command, String desc, String usage, String permission, List<String> aliases) {
        super(command);
        description = desc;
        usageMessage = usage;
        setPermission(permission);
        setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        im.runAsync(() -> {
            if (sender instanceof Player) {
                lang = ((Player) sender).spigot().getLocale().split("_")[0];
            }
            if (args.length == 0) {
                if (sender instanceof Player) {
                    sender.sendMessage(core.rep(core.getString("Coins.Own coins", lang).replaceAll("%coins%", CoinsAPI.getCoinsString(sender.getName()))));
                } else if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(core.rep(core.getString("Errors.Console", "")));
                }
            } else if (args[0].equalsIgnoreCase("execute")) {
                execute(sender, args);
            } else if ((args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("ayuda")) && args.length == 1) {
                help(sender, args);
            } else if ((args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("pagar"))) {
                pay(sender, args);
            } else if ((args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar"))) {
                give(sender, args);
            } else if ((args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("quitar"))) {
                take(sender, args);
            } else if ((args[0].equalsIgnoreCase("reset"))) {
                reset(sender, args);
            } else if ((args[0].equalsIgnoreCase("set"))) {
                set(sender, args);
            } else if (args[0].equalsIgnoreCase("multiplier") || args[0].equalsIgnoreCase("multipliers")) {
                multiplier(sender, args);
            } else if (args[0].equalsIgnoreCase("top") && args.length == 1) {
                top(sender, args);
            } else if (args.length == 1 && CoinsAPI.isindb(args[0])) {
                target(sender, args);
            } else {
                sender.sendMessage(core.rep(core.getString("Errors.Unknow command", lang)));
            }
        });
        return true;
    }

    public boolean help(CommandSender sender, String[] args) {
        core.getMessages(lang).getStringList("Help.User").forEach((str) -> {
            sender.sendMessage(core.rep(str));
        });
        if (sender.hasPermission("coins.admin")) {
            core.getMessages(lang).getStringList("Help.Admin").forEach((str) -> {
                sender.sendMessage(core.rep(str));
            });
        }
        return true;
    }

    public boolean target(CommandSender sender, String[] args) {
        sender.sendMessage(core.rep(core.getString("Coins.Get", lang).replaceAll("%coins%", CoinsAPI.getCoinsString(args[0])).replaceAll("%target%", args[0])));
        return true;
    }

    public boolean pay(CommandSender sender, String[] args) {
        if (args.length < 3 || args[1].equalsIgnoreCase("?")) {
            sender.sendMessage(core.rep(core.getString("Help.Pay Usage", lang)));
            return true;
        }
        if (sender instanceof Player) {
            if (args.length == 3 && !args[1].equalsIgnoreCase(sender.getName())) {
                Player target = Bukkit.getPlayer(args[1]);
                if (CoinsAPI.isindb(args[1])) {
                    double coins = Double.parseDouble(args[2]);
                    if ((coins > 0)) {
                        if ((CoinsAPI.getCoins(sender.getName()) > 0) && (CoinsAPI.getCoins(sender.getName()) >= coins)) {
                            if (target != null) {
                                CoinsAPI.takeCoins(sender.getName(), coins);
                                sender.sendMessage(core.rep(core.getString("Coins.Pay", lang)).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", target.getName()));
                                CoinsAPI.addCoins(args[1], coins, false);
                                target.sendMessage(core.getString("Coins.Pay target", target.spigot().getLocale()).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%from%", sender.getName()));
                            } else {
                                sender.sendMessage(core.rep(core.getString("Errors.Unknow Player", lang)));
                            }
                        } else {
                            sender.sendMessage(core.rep(core.getString("Errors.No Coins", lang)));
                        }
                    } else {
                        sender.sendMessage(core.rep(core.getString("Errors.No Zero", lang)));
                    }
                }

            }
        }
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(core.rep(core.getString("Errors.Console", lang)));
        }
        return true;
    }

    public boolean give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coins.admin")) {
            sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
            return true;
        }
        if (!CoinsAPI.isindb(args[1])) {
            sender.sendMessage(core.rep(core.getString("Errors.Unknow player", lang).replaceAll("%target%", args[1])));
            return true;
        }
        Player target = Bukkit.getPlayer(core.getUUID(args[1]));
        String multiplier = "";
        boolean multiply = false;
        if (args.length == 3 || args.length == 4) {
            double coins = Double.parseDouble(args[2]);
            if (args.length == 4 && target != null && args[3] != null && args[3].equalsIgnoreCase("true")) {
                int amount = CoinsAPI.getMultiplier().getAmount();
                if (amount > 1) {
                    multiplier = core.rep(core.getString("Multipliers.Format", target.spigot().getLocale()).replaceAll("%multiplier%", String.valueOf(amount)).replaceAll("%enabler%", CoinsAPI.getMultiplier().getEnabler()));
                }
                for (int i = 2; i < 100; i++) {
                    try {
                        Permission perm = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
                        if (perm.has(target, "coins.multiplier.x" + i)) {
                            coins *= i;
                            break;
                        }
                    } catch (Exception ex) {
                        if (target.hasPermission("coins.multiplier.x" + i)) {
                            coins *= i;
                            break;
                        }
                    }
                }
                coins *= amount;
                multiply = true;
            }
            CoinsAPI.addCoins(args[1], coins, multiply);
            if (target != null) {
                target.sendMessage(core.getString("Coins.Give target", target.spigot().getLocale()).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier));
            }
            sender.sendMessage(core.rep(core.getString("Coins.Give", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", args[1])));
        } else {
            sender.sendMessage(core.rep(core.getString("Errors.Unknow command", lang)));
        }
        return true;
    }

    public boolean take(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coins.admin")) {
            sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
            return true;
        }
        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (args[0].equalsIgnoreCase("take")) {
                double coins = Double.parseDouble(args[2]);
                double finalcoins = coins - Double.parseDouble(args[2]);
                if (target == null) {
                    if (CoinsAPI.getCoins(args[1]) < coins) {
                        sender.sendMessage(core.rep(core.getString("Errors.No Negative", lang)));
                    } else if (CoinsAPI.getCoins(args[1]) >= coins) {
                        if (CoinsAPI.isindb(args[1])) {
                            CoinsAPI.takeCoins(args[1], coins);
                            sender.sendMessage(core.rep(core.getString("Coins.Take target", lang).replaceAll("%coins%", String.valueOf(finalcoins)).replaceAll("%target%", args[1])));
                        }
                        sender.sendMessage(core.rep(core.getString("Errors.Unknow player", lang).replaceAll("%target%", args[1])));
                    }
                } else {
                    if (CoinsAPI.getCoins(args[1]) < coins) {
                        sender.sendMessage(core.rep(core.getString("Errors.No Negative", lang)));
                    }
                    if (CoinsAPI.getCoins(args[1]) >= coins) {
                        CoinsAPI.takeCoins(args[1], coins);
                        sender.sendMessage(core.rep(core.getString("Coins.Take", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", target.getName()).replaceAll("%newcoins%", String.valueOf(finalcoins))));
                        target.sendMessage(core.getString("Coins.Take target", target.spigot().getLocale()).replaceAll("%coins%", String.valueOf(finalcoins)));
                    }
                }
            }
        }
        return true;
    }

    public boolean reset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coins.admin")) {
            sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(core.rep(core.getString("Help.Reset Usage", lang)));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            if (CoinsAPI.isindb(args[1])) {
                CoinsAPI.resetCoins(args[1]);
                sender.sendMessage(core.rep(core.getString("Coins.Reset", lang).replaceAll("%target%", args[1])));
            } else {
                sender.sendMessage(core.rep(core.getString("Errors.Unknow player", lang).replaceAll("%target%", args[1])));
            }
        } else if (target.isOnline()) {
            CoinsAPI.resetCoins(args[1]);
            sender.sendMessage(core.rep(core.getString("Coins.Reset", lang).replaceAll("%target%", target.getName())));
            target.sendMessage(core.getString("Coins.Reset target", target.spigot().getLocale()));
        }
        return true;
    }

    public boolean set(CommandSender sender, String[] args) {
        if (!sender.hasPermission("coins.admin")) {
            sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
            return true;
        }
        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (args[0].equalsIgnoreCase("set")) {
                double coins = Double.parseDouble(args[2]);
                if (target == null) {
                    if (CoinsAPI.isindb(args[1])) {
                        CoinsAPI.setCoins(args[1], coins);
                        sender.sendMessage(core.rep(core.getString("Coins.Set", lang).replaceAll("%target%", args[1]).replaceAll("%coins%", String.valueOf(coins))));
                    } else {
                        sender.sendMessage(core.rep(core.getString("Errors.Unknow player", lang).replaceAll("%target%", args[1])));
                    }
                } else if (target.isOnline()) {
                    CoinsAPI.setCoins(args[1], coins);
                    sender.sendMessage(core.rep(core.getString("Coins.Set", lang).replaceAll("%target%", target.getName()).replaceAll("%coins%", String.valueOf(coins))));
                    target.sendMessage(core.getString("Coins.Set target", target.spigot().getLocale()).replaceAll("%coins%", args[2]));
                }
            }
        }
        return true;
    }

    public boolean top(CommandSender sender, String[] args) {
        sender.sendMessage(core.rep(core.getString("Coins.Top.Header", lang)));
        List<String> top = CoinsAPI.getTop(10);
        int i = 0;
        for (String t : top) {
            if (i < top.size()) {
                i++;
            }
            String[] pc = t.split(", ");
            String str = core.getString("Coins.Top.List", lang)
                    .replaceAll("%top%", String.valueOf(i))
                    .replaceAll("%player%", pc[0])
                    .replaceAll("%coins%", String.valueOf(pc[1]));
            sender.sendMessage(core.rep(str));
        }
        return true;
    }

    private boolean multiplier(CommandSender sender, String[] args) {
        if (sender.hasPermission("coins.admin")) {
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("help")) {
                    sender.sendMessage(core.getString("Help.Multiplier", lang));
                }
                if (args[1].equalsIgnoreCase("create")) {
                    if (args.length >= 5 && CoinsAPI.isindb(args[2])) {
                        try {
                            int multiplier = Integer.parseInt(args[3]);
                            int minutes = Integer.parseInt(args[4]);
                            CoinsAPI.getMultiplier(args[5] != null ? args[0] : core.getConfig().getString("Multipliers.Server")).createMultiplier(Bukkit.getPlayer(args[2]).getUniqueId(), multiplier, minutes);
                            sender.sendMessage(core.getString("Multipliers.Created", lang).replaceAll("%player%", Bukkit.getPlayer(args[2]).getName()));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(core.rep(String.valueOf(e.getCause().getMessage())));
                        }
                    } else {
                        sender.sendMessage(core.getString("Help.Multiplier Create", lang));
                    }
                }
                if (args[1].equalsIgnoreCase("get")) {
                    sender.sendMessage(CoinsAPI.getMultiplier().getMultiplierTimeFormated());
                }
                if (args[1].equalsIgnoreCase("set")) {
                    // TODO: add a way to fake multipliers data.
                }
                return true;
            }
        }
        if (args.length == 1) {
            if (sender instanceof Player) {
                new MultipliersGUI((Player) sender, core.getString("Multipliers.Menu.Title", lang)).open((Player) sender);
            } else {
                sender.sendMessage(core.getString("Errors.Console", lang));
            }
        } else {
            sender.sendMessage(core.getString("Help.Multiplier Usage", lang));
        }
        return true;
    }

    private boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Executor ex = core.getExecutorManager().getExecutor(args[1]);
            if (ex == null) {
                sender.sendMessage(core.rep(core.getString("Errors.No Execute", lang)));
                return true;
            } else {
                if (ex.getCost() > 0) {
                    if (CoinsAPI.getCoins(sender.getName()) >= ex.getCost()) {
                        CoinsAPI.takeCoins(sender.getName(), ex.getCost());
                    } else {
                        sender.sendMessage(core.rep(core.getString("Errors.No Coins", lang)));
                        return true;
                    }
                }
                if (!ex.getCommands().isEmpty()) {
                    core.getMethods().runSync(() -> {
                        String command;
                        for (String str : ex.getCommands()) {
                            command = core.rep(str).replaceAll("%player%", sender.getName());
                            if (command.startsWith("message: ")) {
                                sender.sendMessage(command.replaceFirst("message: ", ""));
                            } else if (command.startsWith("broadcast: ")) {
                                Bukkit.getServer().broadcastMessage(command.replaceFirst("broadcast: ", ""));
                            } else {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            }
                        }
                    });
                }
            }
        } else {
            sender.sendMessage(core.getString("Errors.Console", lang));
        }
        return true;
    }
}
