package de.mrg4ming.commands;

import de.mrg4ming.Main;
import de.mrg4ming.control.Bank;
import de.mrg4ming.control.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.isOp()) {
            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("confirm")) {
                    Bank.instance.loadFromConfig();
                    Shop.instance.loadFromConfig();
                    sender.sendMessage(Main.PREFiX + "§aConfigurations successfully reloaded.");
                    return true;
                }
            }
            sender.sendMessage(Main.PREFiX + "§cAre you sure to reload the config? It will be overwritten. Write §6'reload confirm'§c to confirm the config reload!");
        }

        return true;
    }
}
