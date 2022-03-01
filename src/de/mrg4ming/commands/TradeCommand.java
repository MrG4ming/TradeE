package de.mrg4ming.commands;

import de.mrg4ming.Main;
import de.mrg4ming.control.Bank;
import de.mrg4ming.control.PlayerUtils;
import de.mrg4ming.control.Shop;
import de.mrg4ming.data.OptionItem;
import de.mrg4ming.data.trade.Trade;
import de.mrg4ming.data.trade.TradeItem;
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
                                TradeItem _placeholderItem = new TradeItem(Material.DIRT, 1, new HashMap<>());
                                _placeholderItem.setItemStack(new OptionItem("§8Placeholder: Product", Material.DIRT));

                                Trade _trade = new Trade(_name,
                                        0,
                                        _placeholderItem,
                                        Trade.Mode.SELL,
                                        0,
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

                            PlayerUtils.givePlayerItems(p, _trade.getProduct().getItemStack(), _trade.storage);
                        }

                        Shop.instance.removeTrade(PlayerUtils.getKeyByValue(Shop.instance.trades, Shop.instance.getTrade(_name)));
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
                            TradeItem _placeholderItem = new TradeItem(Material.DIRT, 1, new HashMap<>());
                            _placeholderItem.setItemStack(new OptionItem("§8Placeholder: Product", Material.DIRT));

                            Trade _trade = Trade.createConstantTrade(_name,
                                    0,
                                    _placeholderItem,
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
