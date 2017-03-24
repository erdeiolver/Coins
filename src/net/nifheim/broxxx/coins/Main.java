package net.nifheim.broxxx.coins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.nifheim.broxxx.coins.command.CoinsCommand;

import net.nifheim.broxxx.coins.listener.PlayerJoinListener;
import net.nifheim.broxxx.coins.listener.CommandListener;

import net.nifheim.broxxx.coins.hooks.MVdWPlaceholderAPIHook;
import net.nifheim.broxxx.coins.hooks.PlaceholderAPI;

import net.nifheim.broxxx.coins.databasehandler.MySQL;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Main extends JavaPlugin {

    public static String replacener;
    public final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private PlaceholderAPI placeholderAPI;

    private static Main instance;
    private final int checkdb = getConfig().getInt("MySQL.Connection Interval") * 1200;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        /*
        Test
        */
        this.loadMessages();

        instance = this;
        saveDefaultConfig();
        getCommand("coins").setExecutor(new CoinsCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);

        try {
            SQLConnection();
        } catch (Exception e) {
        }

        /*
        Hook placeholders
         */
        if (getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            console.sendMessage(replacener("%prefix% MVdWPlaceholderAPI found, hooking in this."));
            MVdWPlaceholderAPIHook.hook(this);
        }
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            console.sendMessage(replacener("%prefix% PlaceholderAPI found, hooking in this."));
            placeholderAPI = new PlaceholderAPI(this);
            placeholderAPI.hook();
        }

        /*
        Console message
         */
        if (this.getDescription().getVersion().contains("BETA")) {
            console.sendMessage(replacener(""));
            console.sendMessage(replacener("    §c+=======================+"));
            console.sendMessage(replacener("    §c|   §4Coins §fBy: §7Broxxx§c    |"));
            console.sendMessage(replacener("    §c|-----------------------|"));
            console.sendMessage(replacener("    §c|     §4v:§f" + getDescription().getVersion() + "      §c|"));
            console.sendMessage(replacener("    §c+=======================+"));
            console.sendMessage(replacener(""));
        } else {
            console.sendMessage(replacener(""));
            console.sendMessage(replacener("    §c+==================+"));
            console.sendMessage(replacener("    §c| §4Coins §fBy: §7Broxxx§c |"));
            console.sendMessage(replacener("    §c|------------------|"));
            console.sendMessage(replacener("    §c|     §4v:§f" + getDescription().getVersion() + "      §c|"));
            console.sendMessage(replacener("    §c+==================+"));
            console.sendMessage(replacener(""));
        }
    }

    @Override
    public void onDisable() {

        if (getConfig().getBoolean("Use MySQL")) {
            MySQL.Disconnect();
        }

        Bukkit.getScheduler().cancelTasks(this);

        if (this.getDescription().getVersion().contains("BETA")) {
            console.sendMessage(replacener(""));
            console.sendMessage(replacener("    §c+=======================+"));
            console.sendMessage(replacener("    §c|   §4Coins §fBy: §7Broxxx§c    |"));
            console.sendMessage(replacener("    §c|-----------------------|"));
            console.sendMessage(replacener("    §c|     §4v:§f" + getDescription().getVersion() + "      §c|"));
            console.sendMessage(replacener("    §c+=======================+"));
            console.sendMessage(replacener(""));
        } else {
            console.sendMessage(replacener(""));
            console.sendMessage(replacener("    §c+==================+"));
            console.sendMessage(replacener("    §c| §4Coins §fBy: §7Broxxx§c |"));
            console.sendMessage(replacener("    §c|------------------|"));
            console.sendMessage(replacener("    §c|     §4v:§f" + getDescription().getVersion() + "      §c|"));
            console.sendMessage(replacener("    §c+==================+"));
            console.sendMessage(replacener(""));
        }
    }

    private void loadConfig() {
        if (getConfig() == null) {
            getConfig().addDefault("version", "1");
            getConfig().addDefault("Online Mode", "true");
            getConfig().createSection("MySQL");
            getConfig().addDefault("MySQL.Host", "localhost");
            getConfig().addDefault("MySQL.Port", "3306");
            getConfig().addDefault("MySQL.Database", "minecraft");
            getConfig().addDefault("MySQL.User", "root");
            getConfig().addDefault("MySQL.Password", "warlus");
            getConfig().addDefault("MySQL.Connection Invterval", 1);
            saveConfig();
        }
        saveConfig();
    }

    private void loadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            
        }
        FileConfiguration msgs = YamlConfiguration.loadConfiguration(messagesFile);
        msgs.options().header("dlhjnkfkhflgjh");
        msgs.addDefault("Prefix", "&8&l[&c&lCoins&8&l]&7");
        msgs.addDefault("#cfhgh", "");
        try {
            msgs.save(messagesFile);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Can't save messages file", ex);
        }
    }

    /*
    public void startMultiplierTime() {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
    }

    public String getMultiplierTime() {
        clockTime = now - startTime;
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (clockTime >= multiplierTime) {
                    clockTime = multiplierTime;
                    startTime = -1;
                }
            }
        }, 0L, 20L);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return (df.format(multiplierTime - clockTime));
    }
     */
    public void SQLConnection() {
        try {
            MySQL.Connect();

            if (!MySQL.getConnection().isClosed()) {
                console.sendMessage(replacener("%prefix%Plugin conected sucesful to the MySQL."));
            }
        } catch (Exception e) {
            console.sendMessage(replacener("%prefix%Can't connect to the database, disabling plugin..."));
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
        }

        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), () -> {
            console.sendMessage(replacener("%prefix%Checking the database connection ..."));
            if (MySQL.getConnection() == null) {
                console.sendMessage(replacener("%prefix%The database connection is null, reconnecting ..."));
                MySQL.Reconnect();
            } else {
                console.sendMessage(replacener("%prefix%The connection to the database is still active."));
            }
        }, 0L, checkdb);
    }

    public String replacener(String str) {
        return str.replaceAll("%prefix%", getConfig().getString("Messages.Prefix"))
                .replaceAll("&", "§");
    }
}
