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

public class TradeCommand implements CommandExecutor {

    public static final String shopVillagerName = "§6Harald§b the Trader";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if(args.length < 1) {
                if(p.isOp()) {
                    //Spawn Villager
                    Villager v = (Villager) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
                    v.setRemoveWhenFarAway(false);
                    v.setCustomName(shopVillagerName);
                    v.setProfession(Villager.Profession.NITWIT);
                    v.setCanPickupItems(false);
                    v.setInvulnerable(true);
                    v.setAI(false);

                    p.sendMessage(Main.PREFiX + "§aTrader spawned.");
                }
            } else if(args.length >= 3){
                if(args[0].equalsIgnoreCase("create")) {
                    String _name = args[1];
                    String _bankAccountName = args[2];

                    if(!Shop.instance.checkIfTradeExists(_name)) {
                        if(Bank.instance.getIdByName(_bankAccountName) > 0) {
                            Trade _trade = new Trade(_name,
                                    0, new OptionItem("§8Placeholder: Product",
                                    Material.COBBLESTONE),
                                    Trade.Mode.SELL, 0,
                                    Bank.instance.accounts.get(Bank.instance.getIdByName(_bankAccountName))
                            );

                            Shop.tempTrades.put(p.getUniqueId().toString(), _trade);

                            p.openInventory(_trade.configurator().open());
                        } else {
                            p.sendMessage(Main.PREFiX + "§cBank account does not exist!");
                        }
                    } else {
                        p.sendMessage(Main.PREFiX + "§cTrade already exists!");
                    }
                } else if(args[0].equalsIgnoreCase("remove")) {

                }
            }else if(args.length == 2){
                if(args[0].equalsIgnoreCase("remove")) {
                    String _name = args[1];

                    if(!Shop.instance.checkIfTradeExists(_name)) {

                    } else {

                    }
                } else if(args[0].equalsIgnoreCase("remove")) {

                }
            }
        }
        return true;
    }
}
