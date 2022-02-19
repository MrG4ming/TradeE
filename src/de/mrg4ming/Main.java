package de.mrg4ming;

import de.mrg4ming.commands.BankCommand;
import de.mrg4ming.commands.TradeCommand;
import de.mrg4ming.commands.tabcompleter.BankTabCompleter;
import de.mrg4ming.commands.tabcompleter.TradeTabCompleter;
import de.mrg4ming.control.Bank;
import de.mrg4ming.control.Shop;
import de.mrg4ming.listener.InventoryClickListener;
import de.mrg4ming.listener.PlayerInteractListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main plugin;

    public static final String PREFiX = "§8[§bTradeE§8]§r ";

    @Override
    public void onEnable() {
        if(plugin == null) {
            plugin = this;
        } else {
            return;
        }
        try {
            new Bank();
            new Shop();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        getCommand("bank").setExecutor(new BankCommand());
        getCommand("bank").setTabCompleter(new BankTabCompleter());

        getCommand("trade").setExecutor(new TradeCommand());
        getCommand("trade").setTabCompleter(new TradeTabCompleter());

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerInteractListener(), plugin);
        pm.registerEvents(new InventoryClickListener(), plugin);
    }

    @Override
    public void onDisable() {
        Bank.instance.saveToConfig();
        Shop.instance.saveToConfig();
    }
    
    public static Main getPlugin() {
        return plugin;
    }
}
