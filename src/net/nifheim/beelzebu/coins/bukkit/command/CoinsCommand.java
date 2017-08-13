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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class CoinsCommand extends BukkitCommand {

    public Main plugin = Main.getInstance();
    private final FileConfiguration config = (FileConfiguration) Core.getInstance().getMethods().getConfig();
    private final FileConfiguration messages = (FileConfiguration) Core.getInstance().getMethods().getMessages();

    public CoinsCommand(String command, String desc, String usage, String permission, List<String> aliases) {
        super(command);
        description = desc;
        usageMessage = usage;
        setPermission(permission);
        setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                String coins = CoinsAPI.getCoinsString(p.getName());
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Own coins")).replaceAll("%coins%", coins));
                return true;
            } else if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Console")));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("execute")) {
            return _execute(sender, args);
        } else if ((args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("ayuda")) && args.length == 1) {
            return _help(sender, args);
        } else if ((args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("pagar"))) {
            return _pay(sender, args);
        } else if ((args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar"))) {
            return _give(sender, args);
        } else if ((args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("quitar"))) {
            return _take(sender, args);
        } else if ((args[0].equalsIgnoreCase("reset"))) {
            return _reset(sender, args);
        } else if ((args[0].equalsIgnoreCase("set"))) {
            return _set(sender, args);
        } else if (args[0].equalsIgnoreCase("multiplier")) {
            return _multiplier(sender, args);
        } else if (args[0].equalsIgnoreCase("top") && args.length == 1) {
            return _top(sender, args);
        } else if (args.length == 1) {
            if (CoinsAPI.isindb(args[0])) {
                return _target(sender, args);
            } else {
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow command")));
            }
        } else {
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow command")));
        }
        return true;
    }

    public boolean _help(CommandSender sender, String[] args) {
        ((FileConfiguration) Core.getInstance().getMethods().getMessages()).getStringList("Help.User").forEach((str) -> {
            sender.sendMessage(Core.getInstance().rep(str.replaceAll("%prefix%", ((FileConfiguration) Core.getInstance().getMethods().getMessages()).getString("Prefix"))));
        });
        if (sender.hasPermission("nifheim.admin")) {
            ((FileConfiguration) Core.getInstance().getMethods().getMessages()).getStringList("Help.Admin").forEach((str) -> {
                sender.sendMessage(Core.getInstance().rep(str.replaceAll("%prefix%", ((FileConfiguration) Core.getInstance().getMethods().getMessages()).getString("Prefix"))));
            });
        }
        return true;
    }

    public boolean _target(CommandSender sender, String[] args) {
        String coins = CoinsAPI.getCoinsString(args[0]);
        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Get").replaceAll("%coins%", coins).replaceAll("%target%", args[0])));
        return true;
    }

    public boolean _pay(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (args.length < 3 || args[1].equalsIgnoreCase("?")) {
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Help.Pay Usage")));
        }
        if (sender instanceof Player) {
            if (args.length == 3 && !args[1].equalsIgnoreCase(sender.getName())) {
                if (CoinsAPI.isindb(args[1])) {
                    Player p = (Player) sender;
                    double coins = Double.parseDouble(args[2]);
                    String coins_string = "" + coins;
                    if ((coins > 0)) {
                        if ((CoinsAPI.getCoins(p.getName()) > 0) && (CoinsAPI.getCoins(p.getName()) > coins)) {
                            if (target != null) {
                                CoinsAPI.takeCoins(p.getName(), coins);
                                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Pay")).replaceAll("%coins%", coins_string).replaceAll("%target%", target.getName()));
                                CoinsAPI.addCoins(args[1], coins, false);
                                target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Pay target")).replaceAll("%coins%", coins_string).replaceAll("%from%", p.getName()));
                            } else {
                                p.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow Player")));
                            }
                        } else {
                            p.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No Coins")));
                        }
                    } else {
                        p.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No Zero")));
                    }
                }

            } else {
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Help.Pay Usage")));
            }
        }
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Console")));
        }
        return true;
    }

    public boolean _give(CommandSender sender, String[] args) {
        String multiplier = "";
        boolean multiply = false;
        if (args.length == 4 && args[3] != null) {
            if (args[3].equalsIgnoreCase("true")) {
                int amount = config.getInt("Multipliers.Amount");
                if (amount > 1) {
                    multiplier = Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Multipliers.Format", "&8(&bx%multiplier%&8) &aMultiplier enabled by &c%enabler%&a!").replaceAll("%multiplier%", String.valueOf(amount)).replaceAll("%enabler%", CoinsAPI.getMultiplier(config.getString("Multipliers.Server")).getEnabler()));
                }
                multiply = true;
            }
        }
        if (args.length == 3 || args.length == 4) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No permissions")));
            }
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar")) {
                double coins = Double.parseDouble(args[2]);
                if (args[1] == null) {
                    if (CoinsAPI.isindb(args[1])) {
                        CoinsAPI.addCoins(args[1], coins, false);
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Give").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", args[1])));
                    }
                    sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow player").replaceAll("%target%", args[1])));
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (multiply) {
                        if (target.hasPermission("coins.multiplier.x4")
                                || target.hasPermission("nifheim.vip.king")) {
                            coins = coins * 4;
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Give target").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        } else if (target.hasPermission("coins.multiplier.x3")
                                || target.hasPermission("nifheim.vip.lord")
                                || target.hasPermission("nifheim.vip.sir")) {
                            coins = coins * 3;
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Give target").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        } else if (target.hasPermission("coins.multiplier.x2")
                                || target.hasPermission("nifheim.vip.earl")
                                || target.hasPermission("nifheim.vip.marques")) {
                            coins = coins * 2;
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Give target").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        } else {
                            CoinsAPI.addCoins(args[1], coins, multiply);
                            target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Give target").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                        }
                    } else {
                        CoinsAPI.addCoins(args[1], coins, multiply);
                        target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Give target").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%multiplier_format%", multiplier)));
                    }
                }
            }
        } else {
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow command")));
        }
        return true;
    }

    public boolean _take(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (args.length == 3) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No permissions")));
            }
            if (args[0].equalsIgnoreCase("take")) {
                double coins = Double.parseDouble(args[2]);
                double finalcoins = coins - Double.parseDouble(args[2]);
                if (target == null) {
                    if (CoinsAPI.getCoins(args[1]) < coins) {
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No Negative")));
                    } else if (CoinsAPI.getCoins(args[1]) >= coins) {
                        if (CoinsAPI.isindb(args[1])) {
                            CoinsAPI.takeCoins(args[1], coins);
                            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Take target").replaceAll("%coins%", String.valueOf(finalcoins)).replaceAll("%target%", args[1])));
                        }
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow player").replaceAll("%target%", args[1])));
                    }
                } else {
                    if (CoinsAPI.getCoins(args[1]) < coins) {
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No Negative")));
                    }
                    if (CoinsAPI.getCoins(args[1]) >= coins) {
                        CoinsAPI.takeCoins(args[1], coins);
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Take").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", target.getName()).replaceAll("%newcoins%", String.valueOf(finalcoins))));
                        target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Take target").replaceAll("%coins%", String.valueOf(finalcoins))));
                    }
                }
            }
        }
        return true;
    }

    public boolean _reset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nifheim.admin")) {
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No permissions")));
        }
        if (args.length < 2) {
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Help.Reset Usage")));
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            if (CoinsAPI.isindb(args[1])) {
                CoinsAPI.resetCoins(args[1]);
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Reset").replaceAll("%target%", args[1])));
            } else {
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow player").replaceAll("%target%", args[1])));
            }
        } else if (target.isOnline()) {
            CoinsAPI.resetCoins(args[1]);
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Reset").replaceAll("%target%", target.getName())));
            target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Reset target")));
        }
        return true;
    }

    public boolean _set(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (args.length == 3) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No permissions")));
            }
            if (args[0].equalsIgnoreCase("set")) {
                double coins = Double.parseDouble(args[2]);
                if (target == null) {
                    if (CoinsAPI.isindb(args[1])) {
                        CoinsAPI.setCoins(args[1], coins);
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Set").replaceAll("%target%", args[1]).replaceAll("%coins%", String.valueOf(coins))));
                    } else {
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.Unknow player").replaceAll("%target%", args[1])));
                    }
                } else if (target.isOnline()) {
                    CoinsAPI.setCoins(args[1], coins);
                    sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Set").replaceAll("%target%", target.getName()).replaceAll("%coins%", String.valueOf(coins))));
                    target.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Set target").replaceAll("%coins%", args[2])));
                }
            }
        }
        return true;
    }

    public boolean _top(CommandSender sender, String[] args) {
        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Coins.Top.Header")));
        List<String> top = CoinsAPI.getTop(10);
        int i = 0;
        for (String t : top) {
            if (i < top.size()) {
                i++;
            }
            String[] pc = t.split(", ");
            String str = Core.getInstance().getMethods().getString(sender, "Coins.Top.List")
                    .replaceAll("%top%", String.valueOf(i))
                    .replaceAll("%player%", pc[0])
                    .replaceAll("%coins%", String.valueOf(pc[1]));
            sender.sendMessage(Core.getInstance().rep(str));
        }
        return true;
    }

    private boolean _multiplier(CommandSender sender, String[] args) {
        // TODO: finish this
        // TODO: messages
        // 0(multiplier) 1(use|set|create) 2(id|player|amount) 3(multiplier)
        if (sender.hasPermission("nifheim.admin")) {
            Core.getInstance().getMethods().log("tienes permisos");
            Core.getInstance().getMethods().log(args.length);
            if (args.length >= 3 && args[1].equalsIgnoreCase("create")) {
                if (Bukkit.getPlayer(args[2]) != null) {
                    try {
                        int multiplier = Integer.parseInt(args[3]);
                        int minutes = Integer.parseInt(args[4]);
                        CoinsAPI.getMultiplier(config.getString("Multipliers.Server")).createMultiplier(Bukkit.getPlayer(args[2]).getUniqueId(), multiplier, minutes);
                        sender.sendMessage(Core.getInstance().getMethods().getString(sender, "Multipliers.Created").replace("%player%", Bukkit.getPlayer(args[2]).getName()));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Core.getInstance().rep(String.valueOf(e.getCause().getMessage())));
                    }
                }
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
                if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 5) {
                    config.set("Multiplier.Amount", Integer.parseInt(args[1]));
                    Main.getInstance().saveConfig();
                    sender.sendMessage(Core.getInstance().getMethods().getString(sender, "Multipliers.Set"));
                }
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("use") && sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 3) {
                    if (CoinsAPI.getMultiplier(config.getString("Multipliers.Server", "default")).getMultiplierAmount() <= 1) {
                        CoinsAPI.getMultiplier(config.getString("Multipliers.Server", "default")).useMultiplier(((Player) sender).getUniqueId(), Integer.parseInt(args[2]), MultiplierType.SERVER);
                    } else {
                        p.sendMessage(Core.getInstance().getMethods().getString(sender, "Multipliers.Already active"));
                    }
                } else {
                    CoinsAPI.getMultiplier(config.getString("Multipliers.Server", "default")).getMultipliersFor(p.getUniqueId()).forEach((i) -> {
                        p.sendMessage("" + i);
                    });
                }
            }
            if (args.length == 2 && args[1].equalsIgnoreCase("get")) {
                sender.sendMessage(CoinsAPI.getMultiplier(config.getString("Multipliers.Server", "default")).getMultiplierTimeFormated());
            }
            if (args.length == 1 && sender instanceof Player) {
                Core.getInstance().getMethods().log("abriendo menú");
                Player p = (Player) sender;
                new MultipliersGUI(p, "Menú de multiplicadores").open(p);
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
                        sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No Coins")));
                    } else {
                        CoinsAPI.takeCoins(sender.getName(), (config.getDouble("Command executor." + args[1] + ".Cost")));
                        if (config.getStringList("Command executor." + args[1] + ".Command") != null) {
                            for (String str : config.getStringList("Command executor." + args[1] + ".Command")) {
                                command = Core.getInstance().rep(str).replaceAll("%player%", sender.getName());
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
                            command = Core.getInstance().rep(str).replaceAll("%player%", sender.getName());
                            Core.getInstance().getMethods().log(command);
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
                sender.sendMessage(Core.getInstance().getMethods().getString(sender, "Errors.Console"));
            }
        } else {
            sender.sendMessage(Core.getInstance().rep(Core.getInstance().getMethods().getString(sender, "Errors.No Execute")));
        }
        return true;
    }
}
