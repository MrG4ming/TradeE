package de.mrg4ming.commands.tabcompleter;

import de.mrg4ming.control.Bank;
import de.mrg4ming.data.BankAccount;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankTabCompleter implements TabCompleter {

    //String[] command = {"create", "withdraw", "deposit", "transfer", "close", "info"};
    String[] command = {"create", "transfer", "close", "info"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            List<String> accounts = new ArrayList<>();
            List<String> allAccounts = new ArrayList<>();

            if(!accounts.isEmpty()) accounts.clear();
            if(!allAccounts.isEmpty()) allAccounts.clear();

            for(BankAccount ba : Bank.instance.accounts.values()) {
                allAccounts.add(ba.name);
                if(ba.getOwners().contains(p.getUniqueId().toString())) {
                    accounts.add(ba.name);
                }
            }

            List<String> result = new ArrayList<>();

            if(args.length == 1) {
                for(String s : command) {
                    if(p.hasPermission("tradee.bank.manager")) {
                        if(s.toLowerCase().startsWith(args[0].toLowerCase())) {
                            result.add(s);
                        }
                    } else {
                        if(s.toLowerCase().startsWith(args[0].toLowerCase())) {
                            if(!(s.equalsIgnoreCase("create") || s.equalsIgnoreCase("close"))){
                                result.add(s);
                            }
                        }
                    }
                }
            }

            if(args.length == 2) {
                if(!args[0].equalsIgnoreCase("create")) {
                    for(String s : accounts) {
                        if(s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            result.add(s);
                        }
                    }
                }
            }

            if(args.length == 4) {
                for(String s : allAccounts) {
                    if(s.toLowerCase().startsWith(args[3].toLowerCase())) {
                        result.add(s);
                    }
                }
            }



            return result;
        }
        return null;
    }
}
