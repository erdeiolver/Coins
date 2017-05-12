package net.nifheim.broxxx.coins.command;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.broxxx.coins.CoinsAPI;
import net.nifheim.broxxx.coins.Main;

import org.bukkit.Bukkit;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.configuration.file.FileConfiguration;

public class CoinsCommand implements CommandExecutor {

    public Main plugin = Main.getInstance();
    private final FileConfiguration config = Main.getInstance().getConfig();
    private final FileConfiguration messages = plugin.getMessages();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String CommandLabel, String[] args) {
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
            try {
                return this._top(sender, args);
            } catch (SQLException ex) {
                Logger.getLogger(CoinsCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        for (String str : messages.getStringList("Help.User")) {
            sender.sendMessage(plugin.rep(str));
        }
        if (sender.hasPermission("nifheim.admin")) {
            //sender.sendMessage(plugin.rep(messages.getString("Help.Admin").replaceAll("\\[|\\]", "").replaceAll(", ", "\t")));
            for (String str : messages.getStringList("Help.Admin")) {
                sender.sendMessage(plugin.rep(str));
            }
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
            multiplier = " §8(§bx" + config.getInt("Multiplier") + "§8)";
        } else {
            multiplier = "";
        }
        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(plugin.rep(messages.getString("Errors.No permissions")));
                return true;
            }
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("dar")) {
                double coins = Double.parseDouble(args[2]);

                if (target == null) {
                    OfflinePlayer targetOffline = Bukkit.getServer().getOfflinePlayer(args[1]);
                    if (CoinsAPI.isindb(targetOffline)) {
                        CoinsAPI.addCoinsOffline(targetOffline, coins);
                        sender.sendMessage(plugin.rep(messages.getString("Coins.Give").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", targetOffline.getName())));
                        return true;
                    }
                    sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow player").replaceAll("%target%", targetOffline.getName())));
                    return true;
                }
                if (config.getBoolean("Options.Give is multiplied")) {
                    if (target.hasPermission("coins.multiplier.x4")
                            || target.hasPermission("nifheim.vip.king")) {
                        coins = coins * 4;
                        CoinsAPI.addCoins(target, coins);
                        target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                    } else if (target.hasPermission("coins.multiplier.x3")
                            || target.hasPermission("nifheim.vip.lord")
                            || target.hasPermission("nifheim.vip.sir")) {
                        coins = coins * 3;
                        CoinsAPI.addCoins(target, coins);
                        target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                    } else if (target.hasPermission("coins.multiplier.x2")
                            || target.hasPermission("nifheim.vip.earl")
                            || target.hasPermission("nifheim.vip.marques")) {
                        coins = coins * 2;
                        CoinsAPI.addCoins(target, coins);
                        target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                    }
                } else {
                    CoinsAPI.addCoins(target, coins);
                    target.sendMessage(plugin.rep(messages.getString("Coins.Give target").replaceAll("%coins%", String.valueOf(coins))) + multiplier);
                }
                sender.sendMessage(plugin.rep(messages.getString("Coins.Give").replaceAll("%coins%", String.valueOf(coins)).replaceAll("%target%", target.getName())) + multiplier);
                return true;
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
                return true;
            }
            if (args[0].equalsIgnoreCase("take")) {
                double coins = Double.parseDouble(args[2]);
                double coins_f = coins - Double.parseDouble(args[2]);
                Double coins_fi = coins_f;
                String coins_fin = coins_fi.toString();
                Double coins_int = coins;
                String coins_string = coins_int.toString();
                if (target == null) {
                    OfflinePlayer targetOffline = Bukkit.getServer().getOfflinePlayer(args[1]);
                    if (CoinsAPI.getCoinsOffline(targetOffline) < coins) {
                        sender.sendMessage(plugin.rep(messages.getString("Errors.No Negative").replaceAll("%target%", targetOffline.getName()).replaceAll("%coins%", coins_string)));
                    } else if (CoinsAPI.getCoinsOffline(targetOffline) >= coins) {
                        if (CoinsAPI.isindb(targetOffline)) {
                            CoinsAPI.takeCoinsOffline(targetOffline, coins);
                            sender.sendMessage(plugin.rep(messages.getString("Coins.Take target").replaceAll("%coins%", coins_fin).replaceAll("%target%", targetOffline.getName())));
                            return true;
                        }
                        sender.sendMessage(plugin.rep(messages.getString("Errors.Unknow player").replaceAll("%target%", targetOffline.getName())));
                        return true;
                    }
                } else {
                    if (CoinsAPI.getCoins(target) < coins) {
                        sender.sendMessage(plugin.rep(messages.getString("Errors.No Negative").replaceAll("%target%", target.getName()).replaceAll("%coins%", coins_string)));
                        return true;
                    }
                    if (CoinsAPI.getCoins(target) >= coins) {
                        CoinsAPI.takeCoins(target, coins);
                        sender.sendMessage(plugin.rep(messages.getString("Coins.Take").replaceAll("%coins%", coins_string).replaceAll("%target%", target.getName())));
                        target.sendMessage(plugin.rep(messages.getString("Coins.Take target").replaceAll("%coins%", coins_string)));
                        return true;
                    }
                }
                return true;
            }
        }
        return true;
    }

    public boolean _reset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nifheim.admin")) {
            sender.sendMessage(plugin.rep(messages.getString("Errors.No permissions")));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.rep(messages.getString("Help.Reset Usage")));
            return true;
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
            return true;
        }
        return true;
    }

    public boolean _set(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (args.length == 3) {
            if (!sender.hasPermission("nifheim.admin")) {
                sender.sendMessage(plugin.rep(messages.getString("Errors.No permissions")));
                return true;
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
                    return true;
                }
            }
        }
        return true;
    }

    public boolean _top(CommandSender sender, String[] args) throws SQLException {
        ResultSet res = CoinsAPI.getDataTop(10);
        ArrayList top = new ArrayList();
        int i = 0;
        sender.sendMessage(plugin.rep(messages.getString("Coins.Top.Header")).split("\\n"));
        while (res.next()) {
            i++;

            String str = res.getString("nick");
            int j = res.getInt("balance");
            top.add(messages.getString("Coins.Top.List").replaceAll("%top%", String.valueOf(i)).replaceAll("%player%", str).replaceAll("%coins%", String.valueOf(j)));
        }

        List<String> toplist = top;
        for (String str : toplist) {
            sender.sendMessage(plugin.rep(str));
        }
        return true;
    }

    private boolean _multiplier(CommandSender sender, String[] args) {
        // TODO: finish this
        if (sender.hasPermission("nifheim.admin")) {
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("set")) {
                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[1]) < 5) {
                        config.set("Multiplier.Amount", Integer.parseInt(args[1]));
                        config.set("Multiplier.Enabler", "");
                        Main.getInstance().saveConfig();
                        sender.sendMessage("Multiplicador establecido");
                    }
                } else if (args[1].equalsIgnoreCase("use")) {

                } else {
                    sender.sendMessage(plugin.rep(messages.getString("")));
                }
            }
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("getmultiplier")) {
                    sender.sendMessage(String.valueOf(CoinsAPI.getMultiplierTime()));
                }
            }
        }
        return true;
    }

    private boolean _reload(CommandSender sender, String[] args) {
        if (sender.hasPermission("nifheim.admin")) {
            Bukkit.getScheduler().cancelTasks(Main.getInstance());
            Main.getInstance().reloadConfig();
            Main.getInstance().saveConfig();
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Bukkit.getPluginManager().enablePlugin(Main.getInstance());
            sender.sendMessage(plugin.rep("&8&l[&c&lCoins&8&l] &7Plugin reloaded."));
        }
        return true;
    }

    private boolean _execute(CommandSender sender, String[] args) {
        if (config.getConfigurationSection("Command executor." + "" + args[1]) != null) {
            if (sender instanceof Player) {
                String comando = (config.getString("Command executor." + args[1] + ".Command")).replaceAll("%player%", sender.getName());
                if (config.getDouble("Command executor" + args[1] + ".Cost") > 0) {
                    if (CoinsAPI.getCoins((Player) sender) < config.getDouble("Command executor" + args[1] + ".Cost")) {
                        sender.sendMessage(plugin.rep(messages.getString("Errors.No Coins")));
                    } else {
                        CoinsAPI.takeCoins(((Player) sender).getPlayer(), config.getDouble("Command executor" + args[1] + ".Cost"));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comando);
                    }
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comando);
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
