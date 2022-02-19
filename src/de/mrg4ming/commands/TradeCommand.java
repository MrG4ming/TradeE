package de.mrg4ming.commands;

import de.mrg4ming.Main;
import de.mrg4ming.control.Bank;
import de.mrg4ming.control.Shop;
import de.mrg4ming.data.OptionItem;
import de.mrg4ming.data.ShopInventory;
import de.mrg4ming.data.Trade;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TradeCommand implements CommandExecutor {

    public static final String shopVillagerName = "§6Harald§b the Trader";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if(args.length < 1) {
                if(p.hasPermission("tradee.trade.spawn")) {
                    //Spawn Villager
                    Villager v = (Villager) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
                    v.setRemoveWhenFarAway(false);
                    v.setCustomName(shopVillagerName);
                    v.setProfession(Villager.Profession.NITWIT);
                    v.setCanPickupItems(false);
                    v.setInvulnerable(true);
                    v.setAI(false);

                    p.sendMessage(Main.PREFiX + "§aTrader spawned.");
                } else return false;
            } else if(args.length >= 3) {
                if(p.hasPermission("tradee.trade.user")) {
                    if(args[0].equalsIgnoreCase("create")) {
                        String _name = args[1];
                        String _bankAccountName = args[2];

                        if(!Shop.instance.checkIfTradeExists(_name)) {
                            if(Bank.instance.getIdByName(_bankAccountName) > 0) {
                                Trade _trade = new Trade(_name,
                                        0, new OptionItem("§8Placeholder: Product",
                                        Material.DIRT),
                                        Trade.Mode.BUY_AND_SELL, 0,
                                        Bank.instance.accounts.get(Bank.instance.getIdByName(_bankAccountName))
                                );

                                Shop.tempTrades.put(p.getUniqueId().toString(), _trade);

                                p.openInventory(_trade.getConfigurator().open());
                            } else {
                                p.sendMessage(Main.PREFiX + "§cBank account does not exist!");
                            }
                        } else {
                            p.sendMessage(Main.PREFiX + "§cTrade already exists!");
                        }
                    } else return false;
                } else p.sendMessage(Main.PREFiX + "§4You don't have the permission to use this command!");
            } else if(args.length == 2) {
                if(p.hasPermission("tradee.trade.user")) {
                    if(args[0].equalsIgnoreCase("remove")) {
                        String _name = args[1];

                        if(!Shop.instance.checkIfTradeExists(_name)) {
                            p.sendMessage(Main.PREFiX + "§cTrade does not exist!");
                            return false;
                        }
                        Trade _trade = Shop.instance.getTrade(_name);

                        if(!_trade.isConstant()) {
                            if(!_trade.getOwner().getOwners().contains(p.getUniqueId().toString())) {
                                p.sendMessage(Main.PREFiX + "§cYou don't own this trade!");
                                return true;
                            }

                            while(_trade.storage > 0) {
                                HashMap<Integer, ItemStack> _droppedItems = p.getInventory().addItem(new ItemStack(_trade.getProduct().getType(), _trade.getProductAmount()));

                                if(_droppedItems.size() > 0) {
                                    for(ItemStack i : _droppedItems.values()) {
                                        p.getWorld().dropItem(p.getLocation(), i);
                                    }
                                }
                                _trade.storage--;
                            }
                        }

                        Shop.instance.removeTrade(Shop.getKeyByValue(Shop.instance.trades, Shop.instance.getTrade(_name)));
                        p.sendMessage(Main.PREFiX + "§aTrade successfully §cremoved§a.");
                    } else if(args[0].equalsIgnoreCase("changeowner")) {

                        //feature will be added in a future version

                    }
                } else {
                    p.sendMessage(Main.PREFiX + "§4You don't have the permission to use this command!");
                }
                if(p.hasPermission("tradee.trade.manager")) {
                    if(args[0].equalsIgnoreCase("constant")) {
                        String _name = args[1];

                        if(!Shop.instance.checkIfTradeExists(_name)) {
                            Trade _trade = Trade.createConstantTrade(_name,
                                    0, new OptionItem("§8Placeholder: Product", Material.DIRT),
                                    Trade.Mode.SELL
                            );

                            Shop.tempTrades.put(p.getUniqueId().toString(), _trade);

                            p.openInventory(_trade.getConfigurator().open());
                            p.sendMessage("Open constant trade editor");
                        } else {
                            p.sendMessage(Main.PREFiX + "§cTrade already exists!");
                        }
                    } else if(!args[0].equalsIgnoreCase("remove")) return false;
                } else {
                    p.sendMessage(Main.PREFiX + "§4You don't have the permission to use this command!");
                }
            } else return false;
        }
        return true;
    }
}
