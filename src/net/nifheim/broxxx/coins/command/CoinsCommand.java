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
package net.nifheim.broxxx.coins.command;

import java.util.List;

import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    private final FileConfiguration config = Main.getInstance().getConfig();
    private final FileConfiguration messages = plugin.getMessages();

    public CoinsCommand(String command, String description, String usage, String permission, List<String> aliases) {
        super(command);
        this.description = description;
        this.usageMessage = usage;
        this.setPermission(permission);
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                String coins = CoinsAPI.getCoinsString(p);
                sender.sendMessage(plugin.rep(messages.getString("Coins.Own coins")).replaceAll("%coins%", coins));
                return true;
            } else if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(plugin.rep(messages.getString("Errors.Console")));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("execute")) {
            return this._execute(sender, args);
        } else if ((args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("ayuda")) && args.length == 1) {
            return this._help(sender, args);
        } else if ((args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("pagar"))) {
            return this._pay(sender, args);
        } else if ((args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar"))) {
            return this._give(sender, args);
        } else if ((args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("quitar"))) {
            return this._take(sender, args);
        } else if ((args[0].equalsIgnoreCase("reset"))) {
            return this._reset(sender, args);
        } else if ((args[0].equalsIgnoreCase("set"))) {
            return this._set(sender, args);
        } else if (args[0].equalsIgnoreCase("multiplier")) {
            return this._multiplier(sender, args);
        } else if (args[0].equalsIgnoreCase("reload")) {
            return this._reload(sender, args);
        } else if (args[0].equalsIgnoreCase("top") && args.length == 1) {
            return this._top(sender, args);
        } else if (args.length == 1) {
            if (CoinsAPI.isindb(Bukkit.getOfflinePlayer(args[0]))) {
                if (Bukkit.getPlayer(args[0]) != null || Bukkit.getOfflinePlayer(args[0]) != null) {
                    return this._target(sender, args);
                }
            } else {
                sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow command")));
            }
        } else {
            sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow command")));
        }
        return true;
    }

    public boolean _help(CommandSender sender, String[] args) {
        messages.getStringList("Help.User").forEach((str) -> {
            sender.sendMessage(plugin.rep(str));
        });
        if (sender.hasPermission("nifheim.admin")) {
            messages.getStringList("Help.Admin").forEach((str) -> {
                sender.sendMessage(plugin.rep(str));
            });
        }
        return true;
    }

    public boolean _target(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            OfflinePlayer target1 = Bukkit.getOfflinePlayer(args[0]);
            if (target1 != null) {
                String coins = CoinsAPI.getCoinsStringOffline(target1);
                sender.sendMessage(plugin.rep(messages.getString("Coins.Get").replaceAll("%coins%", coins).replaceAll("%target%", target1.getName())));
            } else {
                sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow player")));
            }
        } else {
            String coins = CoinsAPI.getCoinsString(target);
            sender.sendMessage(plugin.rep(messages.getString("Coins.Get").replaceAll("%coins%", coins).replaceAll("%target%", target.getName())));
        }

        return true;
    }

    public boolean _pay(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (args.length < 3 || args[1].equalsIgnoreCase("?")) {
            sender.sendMessage(plugin.rep(messages.getString("Help.Pay Usage")));
        }
        if (sender instanceof Player) {
            if (args.length == 3 && !args[1].equalsIgnoreCase(sender.getName())) {
                if (CoinsAPI.isindb(target)) {
                    Player p = (Player) sender;
                    double coins = Double.parseDouble(args[2]);
                    String coins_string = "" + coins;
                    if ((coins > 0)) {
                        if ((CoinsAPI.getCoins(p) > 0) && (CoinsAPI.getCoins(p) > coins)) {
                            if (target != null) {
                                CoinsAPI.takeCoins(p, coins);
                                sender.sendMessage(plugin.rep(messages.getString("Coins.Pay")).replaceAll("%coins%", coins_string).replaceAll("%target%", target.getName()));
                                CoinsAPI.addCoins(target, coins);
                                target.sendMessage(plugin.rep(messages.getString("Coins.Pay target")).replaceAll("%coins%", coins_string).replaceAll("%from%", p.getName()));
                            } else {
                                p.sendMessage(plugin.rep(messages.getString("Errors.Unknow Player")));
                            }
                        } else {
                            p.sendMessage(plugin.rep(messages.getString("Errors.No Coins")));
                        }
                    } else {
                        p.sendMessage(plugin.rep(messages.getString("Errors.No Zero")));
                    }
                }

            } else {
                sender.sendMessage(plugin.rep(messages.getString("Help.Pay Usage")));
            }
        }
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(plugin.rep(messages.getString("Errors.Console")));
        }
        return true;
    }

    public boolean _give(CommandSender sender, String[] args) {
        String multiplier;
        if (config.getInt("Multiplier") > 1) {
            multiplier = " §8(§bx" + config.getInt("Multipliers.Amount") + "§8)";
        } else {
            multiplier = "";
        }
        if (args.length == 3 || args.length == 4) {
            Player target = Bukkit.getPlayer(args[1]);
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(plugin.rep(messages.getString("Errors.No permissions")));
            }
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar")) {
                double coins = Double.parseDouble(args[2]);
                if (target == null) {
                    OfflinePlayer targetOffline = Bukkit.getServer().getOfflinePlayer(args[1]);
                    if (CoinsAPI.isindb(targetOffline)) {
                        CoinsAPI.addCoinsOffline(targetOffline, coins);
                        sender.sendMessage(plugin.rep(messages.getString("Coins.Give").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", targetOffline.getName())));
                    }
                    sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow player").replaceAll("%target%", targetOffline.getName())));
                } else {
                    boolean multiply = false;
                    if (args.length == 4 && args[3] != null) {
                        try {
                            multiply = Boolean.getBoolean(args[3]);
                        } catch (Exception ex) {

                        }
                    }
                    if (target.hasPermission("coins.multiplier.x4")
                            || target.hasPermission("nifheim.vip.king")) {
                        coins = coins * 4;
                        CoinsAPI.addCoins(target, coins, multiply);
                        target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                    } else if (target.hasPermission("coins.multiplier.x3")
                            || target.hasPermission("nifheim.vip.lord")
                            || target.hasPermission("nifheim.vip.sir")) {
                        coins = coins * 3;
                        CoinsAPI.addCoins(target, coins, multiply);
                        target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                    } else if (target.hasPermission("coins.multiplier.x2")
                            || target.hasPermission("nifheim.vip.earl")
                            || target.hasPermission("nifheim.vip.marques")) {
                        coins = coins * 2;
                        CoinsAPI.addCoins(target, coins, multiply);
                        target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                    } else {
                        CoinsAPI.addCoins(target, coins, multiply);
                        target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                    }
                }
            }
        } else {
            sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow command")));
        }
        return true;
    }

    public boolean _take(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (args.length == 3) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(plugin.rep(messages.getString("Errors.No permissions")));
            }
            if (args[0].equalsIgnoreCase("take")) {
                double coins = Double.parseDouble(args[2]);
                double finalcoins = coins - Double.parseDouble(args[2]);
                if (target == null) {
                    OfflinePlayer targetOffline = Bukkit.getServer().getOfflinePlayer(args[1]);
                    if (CoinsAPI.getCoinsOffline(targetOffline) < coins) {
                        sender.sendMessage(plugin.rep(messages.getString("Errors.No Negative")));
                    } else if (CoinsAPI.getCoinsOffline(targetOffline) >= coins) {
                        if (CoinsAPI.isindb(targetOffline)) {
                            CoinsAPI.takeCoinsOffline(targetOffline, coins);
                            sender.sendMessage(plugin.rep(messages.getString("Coins.Take target").replaceAll("%coins%", String.valueOf(finalcoins)).replaceAll("%target%", targetOffline.getName())));
                        }
                        sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow player").replaceAll("%target%", targetOffline.getName())));
                    }
                } else {
                    if (CoinsAPI.getCoins(target) < coins) {
                        sender.sendMessage(plugin.rep(messages.getString("Errors.No Negative")));
                    }
                    if (CoinsAPI.getCoins(target) >= coins) {
                        CoinsAPI.takeCoins(target, coins);
                        sender.sendMessage(plugin.rep(messages.getString("Coins.Take").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", target.getName()).replaceAll("%newcoins%", String.valueOf(finalcoins))));
                        target.sendMessage(plugin.rep(messages.getString("Coins.Take target").replaceAll("%coins%", String.valueOf(finalcoins))));
                    }
                }
            }
        }
        return true;
    }

    public boolean _reset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nifheim.admin")) {
            sender.sendMessage(plugin.rep(messages.getString("Errors.No permissions")));
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.rep(messages.getString("Help.Reset Usage")));
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            OfflinePlayer targetOffline = Bukkit.getServer().getOfflinePlayer(args[1]);
            if (CoinsAPI.isindb(targetOffline)) {
                CoinsAPI.resetCoinsOffline(targetOffline);
                sender.sendMessage(plugin.rep(messages.getString("Coins.Reset").replaceAll("%target%", targetOffline.getName())));
            } else {
                sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow player").replaceAll("%target%", targetOffline.getName())));
            }
        } else if (target.isOnline()) {
            CoinsAPI.resetCoins(target);
            sender.sendMessage(plugin.rep(messages.getString("Coins.Reset").replaceAll("%target%", target.getName())));
            target.sendMessage(plugin.rep(messages.getString("Coins.Reset target")));
        }
        return true;
    }

    public boolean _set(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (args.length == 3) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(plugin.rep(messages.getString("Errors.No permissions")));
            }
            if (args[0].equalsIgnoreCase("set")) {
                double coins = Double.parseDouble(args[2]);
                if (target == null) {
                    OfflinePlayer targetOffline = Bukkit.getServer().getOfflinePlayer(args[1]);
                    if (CoinsAPI.isindb(targetOffline)) {
                        CoinsAPI.setCoinsOffline(targetOffline, coins);
                        sender.sendMessage(plugin.rep(messages.getString("Coins.Set").replaceAll("%target%", targetOffline.getName()).replaceAll("%coins%", String.valueOf(coins))));
                    } else {
                        sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow player").replaceAll("%target%", targetOffline.getName())));
                    }
                } else if (target.isOnline()) {
                    CoinsAPI.setCoins(target, coins);
                    sender.sendMessage(plugin.rep(messages.getString("Coins.Set").replaceAll("%target%", target.getName()).replaceAll("%coins%", String.valueOf(coins))));
                    target.sendMessage(plugin.rep(messages.getString("Coins.Set target").replaceAll("%coins%", args[2])));
                }
            }
        }
        return true;
    }

    public boolean _top(CommandSender sender, String[] args) {
        sender.sendMessage(plugin.rep(messages.getString("Coins.Top.Header")));
        for (int i = 0; i < 10; i++) {
            double a = CoinsAPI.getCoinsOffline(Bukkit.getOfflinePlayer(CoinsAPI.getTop(10).get(i)));
            int coins = (int) a;
            String str = plugin.getMessages().getString("Coins.Top.List")
                    .replaceAll("%top%", String.valueOf(i + 1))
                    .replaceAll("%player%", CoinsAPI.getTop(10).get(i))
                    .replaceAll("%coins%", String.valueOf(coins));
            sender.sendMessage(plugin.rep(str));
        }
        return true;
    }

    private boolean _multiplier(CommandSender sender, String[] args) {
        // TODO: finish this
        if (sender.hasPermission("nifheim.admin")) {
            if (args.length == 5) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (Bukkit.getPlayer(args[2]) != null) {
                        try {
                            int multiplier = Integer.parseInt(args[3]);
                            int minutes = Integer.parseInt(args[4]);
                            CoinsAPI.createMultiplier(Bukkit.getPlayer(args[2]), multiplier, minutes);
                            sender.sendMessage(plugin.getString("Multipliers.Created"));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(plugin.rep(String.valueOf(e.getCause().getMessage())));
                        }
                    }
                }
            }
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("set")) {
                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[1]) < 5) {
                        config.set("Multiplier.Amount", Integer.parseInt(args[1]));
                        Main.getInstance().saveConfig();
                        sender.sendMessage(plugin.getString("Multipliers.Set"));
                    }
                } else if (args[1].equalsIgnoreCase("use")) {
                    CoinsAPI.getMultiplierFor(config.getString("Multipliers.Server"));
                } else {
                    sender.sendMessage(plugin.rep(messages.getString("")));
                }
            }
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("get")) {
                    sender.sendMessage(CoinsAPI.getMultiplierTimeFormated(config.getString("Multipliers.Server")));
                }
            }
        }
        return true;
    }

    private boolean _reload(CommandSender sender, String[] args) {
        if (sender.hasPermission("nifheim.admin")) {
            plugin.reload();
            sender.sendMessage(plugin.rep("&8&l[&c&lCoins&8&l] &7Plugin reloaded."));
        }
        return true;
    }

    private boolean _execute(CommandSender sender, String[] args) {
        if (config.getConfigurationSection("Command executor." + "" + args[1]) != null) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                String command = (config.getString("Command executor." + args[1] + ".Command")).replaceAll("%player%", sender.getName());
                if (config.getDouble("Command executor." + args[1] + ".Cost") > 0) {
                    if (CoinsAPI.getCoins(p) < (config.getDouble("Command executor." + args[1] + ".Cost"))) {
                        sender.sendMessage(plugin.rep(messages.getString("Errors.No Coins")));
                    } else {
                        CoinsAPI.takeCoins(p, (config.getDouble("Command executor." + args[1] + ".Cost")));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("Command executor." + args[1] + ".Command"));
            }
        } else {
            sender.sendMessage(plugin.rep(messages.getString("Errors.No Execute")));
        }
        return true;
    }
}
