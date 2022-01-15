package de.mrg4ming.commands;

import de.mrg4ming.Main;
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
        }
        return true;
    }
}
