package de.mrg4ming.commands;

import de.mrg4ming.Main;
import de.mrg4ming.control.Bank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("create")) {
                    if(p.hasPermission("tradee.bank.manager")) {
                        if(args.length == 2) {
                            System.out.println("Creating new account named '" + args[1] + "'...");
                            int _id = Bank.instance.createBankAccount(args[1], p.getUniqueId().toString());
                            if(_id > 0) {
                                System.out.println("New account has been created with name '" + args[1] + "' and id '" + _id + "'.");
                                p.sendMessage(Main.PREFiX + "§aAccount successfully created.");
                            } else {
                                p.sendMessage(Main.PREFiX + "§cAccount creation failed! §7Account with this name already exists or max number of accounts is reached.");
                            }
                        } else {
                            return false;
                        }
                    } else {
                        p.sendMessage(Main.PREFiX + "§4You don't have the permission to use this command!");
                    }
                } else if(args[0].equalsIgnoreCase("close")) {
                    if(p.hasPermission("tradee.bank.manager")) {
                        if(args.length == 2) {
                            int _id = Bank.instance.getIdByName(args[1]);
                            if(_id > 0) {
                                if(Bank.instance.accounts.get(_id).getOwners().contains(p.getUniqueId().toString())) {
                                    Bank.instance.removeAccount(_id);
                                    p.sendMessage(Main.PREFiX + "§aAccount successfully §cremoved§a.");
                                } else {
                                    p.sendMessage(Main.PREFiX + "§4You don't own this bank account!");
                                }
                            } else {
                                p.sendMessage(Main.PREFiX + "§cThis bank account doesn't exist!");
                            }
                        } else {
                            return false;
                        }
                    } else {
                        p.sendMessage(Main.PREFiX + "§4You don't have the permission to use this command!");
                    }
                } else if(args[0].equalsIgnoreCase("info")) {
                    if(args.length == 2) {
                        int _id = Bank.instance.getIdByName(args[1]);
                        if(_id > 0) {
                            List<String> _ownerNames = new ArrayList<>();
                            for(Object o : Bank.instance.accounts.get(_id).getOwners()) {
                                if(o instanceof String) {
                                    UUID _uuid = UUID.fromString((String) o);
                                    _ownerNames.add(Bukkit.getOfflinePlayer(_uuid).getName());
                                }
                            }
                            String _owners = String.join(", ", _ownerNames);
                            p.sendMessage(
                                    "§6-------------§3» Bank account info «§6-------------\n" +
                                    " \n" +
                                    "§9Name: §d" + Bank.instance.accounts.get(_id).name + "$\n" +
                                    "§9Capital: §d" + Bank.instance.accounts.get(_id).getCapital() + "$\n" +
                                    "§9Owner: §d" + _owners + "\n" +
                                    " \n " +
                                    "§9Current main account: §d" + Bank.instance.accounts.get(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString())).name
                                            + "\n" +
                                    " \n " +
                                    "§6---------------------------------------------\n");
                        } else {
                            p.sendMessage(Main.PREFiX + "§cThis bank account does not exist!");
                        }
                    } else {
                        return false;
                    }
                } else if(args[0].equalsIgnoreCase("transfer")) {
                    if(args.length == 4) {
                        int _id = Bank.instance.getIdByName(args[1]);
                        if(_id > 0) {
                            int _targetId = Bank.instance.getIdByName(args[3]);
                            if(_targetId > 0) {
                                try {
                                    int _amount = Integer.parseInt(args[2]);
                                    Bank.instance.accounts.get(_id).transfer(Bank.instance.accounts.get(_targetId), _amount);
                                    p.sendMessage(Main.PREFiX + "§aSuccessfully transferred §6'" + _amount + "'§a$ " +
                                            "from §6'" + Bank.instance.accounts.get(_id).name + "'§a " +
                                            "to §6'" + Bank.instance.accounts.get(_targetId).name + "'§a.");
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Main.PREFiX + "§4'" + args[2] + "' is not an integer!");
                                }
                            } else {
                                p.sendMessage(Main.PREFiX + "§cThe bank account '" + args[3] + "' does not exist!");
                            }
                        } else {
                            p.sendMessage(Main.PREFiX + "§cThe bank account '" + args[1] + "' does not exist!");
                        }
                    } else {
                        return false;
                    }
                } else if(args[0].equalsIgnoreCase("setmain")) {
                    if(args.length == 2) {
                        int _targetId = Bank.instance.getIdByName(args[1]);

                        if(_targetId > 0) {
                            Bank.instance.setMainAccountOfPlayer(p.getUniqueId().toString(), Bank.instance.accounts.get(_targetId));
                        } else {
                            p.sendMessage(Main.PREFiX + "§cThe bank account '" + args[1] + "' does not exist!");
                        }

                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }
}
