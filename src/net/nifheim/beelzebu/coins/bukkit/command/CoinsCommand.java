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
package net.nifheim.beelzebu.coins.bukkit.command;

import java.util.List;

import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.bukkit.Main;
import net.nifheim.beelzebu.coins.bukkit.utils.MultipliersGUI;
import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.multiplier.MultiplierType;
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
                lang = "_" + ((Player) sender).spigot().getLocale().split("_")[0];
            }
            if (args.length < 1) {
                if (sender instanceof Player) {
                    String coins = CoinsAPI.getCoinsString(sender.getName());
                    sender.sendMessage(core.rep(core.getString("Coins.Own coins", "_" + ((Player) sender).spigot().getLocale().split("_")[0]).replaceAll("%coins%", coins)));
                } else if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(core.rep(core.getString("Errors.Console", "")));
                }
            } else if (args[0].equalsIgnoreCase("execute")) {
                _execute(sender, args);
            } else if ((args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("ayuda")) && args.length == 1) {
                _help(sender, args);
            } else if ((args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("pagar"))) {
                _pay(sender, args);
            } else if ((args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar"))) {
                _give(sender, args);
            } else if ((args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("quitar"))) {
                _take(sender, args);
            } else if ((args[0].equalsIgnoreCase("reset"))) {
                _reset(sender, args);
            } else if ((args[0].equalsIgnoreCase("set"))) {
                _set(sender, args);
            } else if (args[0].equalsIgnoreCase("multiplier")) {
                _multiplier(sender, args);
            } else if (args[0].equalsIgnoreCase("top") && args.length == 1) {
                _top(sender, args);
            } else if (args.length == 1) {
                if (CoinsAPI.isindb(args[0])) {
                    _target(sender, args);
                }
            } else {
                sender.sendMessage(core.rep(core.getString("Errors.Unknow command", lang)));
            }
        });
        return true;
    }

    public boolean _help(CommandSender sender, String[] args) {
        core.getMessages(lang).getStringList("Help.User").forEach((str) -> {
            sender.sendMessage(core.rep(str));
        });
        if (sender.hasPermission("nifheim.admin")) {
            core.getMessages(lang).getStringList("Help.Admin").forEach((str) -> {
                sender.sendMessage(core.rep(str));
            });
        }
        return true;
    }

    public boolean _target(CommandSender sender, String[] args) {
        String coins = CoinsAPI.getCoinsString(args[0]);
        sender.sendMessage(core.rep(core.getString("Coins.Get", lang).replaceAll("%coins%", coins).replaceAll("%target%", args[0])));
        return true;
    }

    public boolean _pay(CommandSender sender, String[] args) {
        if (args.length < 3 || args[1].equalsIgnoreCase("?")) {
            sender.sendMessage(core.rep(core.getString("Help.Pay Usage", lang)));
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
                                target.sendMessage(core.rep(core.getString("Coins.Pay target", lang)).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%from%", sender.getName()));
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

    public boolean _give(CommandSender sender, String[] args) {
        String multiplier = "";
        boolean multiply = false;
        if (args.length == 4 && args[3] != null) {
            if (args[3].equalsIgnoreCase("true")) {
                int amount = CoinsAPI.getMultiplier().getAmount();
                if (amount > 1) {
                    multiplier = core.rep(core.getConfig().getString("Multipliers.Format", lang).replaceAll("%multiplier%", String.valueOf(amount)).replaceAll("%enabler%", CoinsAPI.getMultiplier(config.getString("Multipliers.Server")).getEnabler()));
                }
                multiply = true;
            }
        }
        if (args.length == 3 || args.length == 4) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
                return false;
            }
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar")) {
                double coins = Double.parseDouble(args[2]);
                if (args[1] == null) {
                    if (CoinsAPI.isindb(args[1])) {
                        CoinsAPI.addCoins(args[1], coins, false);
                        sender.sendMessage(core.rep(core.getString("Coins.Give", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", args[1])));
                    }
                    sender.sendMessage(core.rep(core.getString("Errors.Unknow player", lang).replaceAll("%target%", args[1])));
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (multiply) {
                        if (target.hasPermission("coins.multiplier.x4")
                                || target.hasPermission("nifheim.vip.king")) {
                            coins = coins * 4;
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(core.rep(core.getString("Coins.Give target", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        } else if (target.hasPermission("coins.multiplier.x3")
                                || target.hasPermission("nifheim.vip.lord")
                                || target.hasPermission("nifheim.vip.sir")) {
                            coins = coins * 3;
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(core.rep(core.getString("Coins.Give target", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        } else if (target.hasPermission("coins.multiplier.x2")
                                || target.hasPermission("nifheim.vip.earl")
                                || target.hasPermission("nifheim.vip.marques")) {
                            coins = coins * 2;
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(core.rep(core.getString("Coins.Give target", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        } else {
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(core.rep(core.getString("Coins.Give target", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        }
                    } else {
                        CoinsAPI.addCoins(args[1], coins, multiply);
                        target.sendMessage(core.rep(core.getString("Coins.Give target", lang).replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                    }
                }
            }
        } else {
            sender.sendMessage(core.rep(core.getString("Errors.Unknow command", lang)));
        }
        return true;
    }

    public boolean _take(CommandSender sender, String[] args) {
        if (args.length == 3) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
                return false;
            }
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
                        target.sendMessage(core.rep(core.getString("Coins.Take target", lang).replaceAll("%coins%", String.valueOf(finalcoins))));
                    }
                }
            }
        }
        return true;
    }

    public boolean _reset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nifheim.admin")) {
            sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(core.rep(core.getString("Help.Reset Usage", lang)));
            return false;
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
            target.sendMessage(core.rep(core.getString("Coins.Reset target", lang)));
        }
        return true;
    }

    public boolean _set(CommandSender sender, String[] args) {
        if (args.length == 3) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(core.rep(core.getString("Errors.No permissions", lang)));
                return false;
            }
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
                    target.sendMessage(core.rep(core.getString("Coins.Set target", lang).replaceAll("%coins%", args[2])));
                }
            }
        }
        return true;
    }

    public boolean _top(CommandSender sender, String[] args) {
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

    private boolean _multiplier(CommandSender sender, String[] args) {
        // TODO: finish this
        // TODO: messages
        // 0(multiplier) 1(use|set|create) 2(id|player|amount) 3(multiplier)
        if (sender.hasPermission("nifheim.admin")) {
            core.getMethods().log("tienes permisos");
            core.getMethods().log(args.length);
            if (args.length >= 3 && args[1].equalsIgnoreCase("create")) {
                if (Bukkit.getPlayer(args[2]) != null) {
                    try {
                        int multiplier = Integer.parseInt(args[3]);
                        int minutes = Integer.parseInt(args[4]);
                        CoinsAPI.getMultiplier(config.getString("Multipliers.Server")).createMultiplier(Bukkit.getPlayer(args[2]).getUniqueId(), multiplier, minutes);
                        sender.sendMessage(core.getString("Multipliers.Created", lang).replaceAll("%player%", Bukkit.getPlayer(args[2]).getName()));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(core.rep(String.valueOf(e.getCause().getMessage())));
                    }
                }
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
                if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 5) {
                    config.set("Multiplier.Amount", Integer.parseInt(args[1]));
                    Main.getInstance().saveConfig();
                    sender.sendMessage(core.getString("Multipliers.Set", lang));
                }
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("use") && sender instanceof Player) {
                if (args.length == 3) {
                    if (CoinsAPI.getMultiplier().getAmount() <= 1) {
                        CoinsAPI.getMultiplier().useMultiplier(((Player) sender).getUniqueId(), Integer.parseInt(args[2]), MultiplierType.SERVER);
                    } else {
                        sender.sendMessage(core.getString("Multipliers.Already active", lang));
                    }
                } else {
                    CoinsAPI.getMultiplier(config.getString("Multipliers.Server", "default")).getMultipliersFor(((Player) sender).getUniqueId()).forEach((i) -> {
                        sender.sendMessage("" + i);
                    });
                }
            }
            if (args.length == 2 && args[1].equalsIgnoreCase("get")) {
                sender.sendMessage(CoinsAPI.getMultiplier(config.getString("Multipliers.Server", "default")).getMultiplierTimeFormated());
            }
            if (args.length == 1 && sender instanceof Player) {
                core.getMethods().log("abriendo menú");
                new MultipliersGUI((Player) sender, "Menú de multiplicadores").open((Player) sender);
            }
        }
        return true;
    }

    private boolean _execute(CommandSender sender, String[] args) {
        if (config.getConfigurationSection("Command executor." + String.valueOf(args[1])) != null) {
            if (sender instanceof Player) {
                String command;
                if (config.getDouble("Command executor." + args[1] + ".Cost") > 0) {
                    if (CoinsAPI.getCoins(sender.getName()) < (config.getDouble("Command executor." + args[1] + ".Cost"))) {
                        sender.sendMessage(core.rep(core.getString("Errors.No Coins", lang)));
                    } else {
                        CoinsAPI.takeCoins(sender.getName(), (config.getDouble("Command executor." + args[1] + ".Cost")));
                        if (config.getStringList("Command executor." + args[1] + ".Command") != null) {
                            for (String str : config.getStringList("Command executor." + args[1] + ".Command")) {
                                command = core.rep(str).replaceAll("%player%", sender.getName());
                                if (command.startsWith("message: ")) {
                                    sender.sendMessage(command.replaceFirst("message: ", ""));
                                } else if (command.startsWith("broadcast: ")) {
                                    Bukkit.getServer().broadcastMessage(command.replaceFirst("broadcast: ", ""));
                                } else {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                }
                            }
                        }
                    }
                } else {
                    if (config.getStringList("Command executor." + args[1] + ".Command") != null) {
                        for (String str : config.getStringList("Command executor." + args[1] + ".Command")) {
                            command = core.rep(str).replaceAll("%player%", sender.getName());
                            core.getMethods().log(command);
                            if (command.startsWith("message: ")) {
                                sender.sendMessage(command.replaceFirst("message: ", ""));
                            } else if (command.startsWith("broadcast: ")) {
                                Bukkit.getServer().broadcastMessage(command.replaceFirst("broadcast: ", ""));
                            } else {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            }
                        }
                    }
                }
            } else {
                sender.sendMessage(core.getString("Errors.Console", lang));
            }
        } else {
            sender.sendMessage(core.rep(core.getString("Errors.No Execute", lang)));
        }
        return true;
    }
}
