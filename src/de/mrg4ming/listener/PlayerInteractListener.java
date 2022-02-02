package de.mrg4ming.listener;

import de.mrg4ming.commands.TradeCommand;
import de.mrg4ming.control.Shop;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();

        if(e.getRightClicked() instanceof Villager) {
            Villager v = (Villager) e.getRightClicked();
            if(v.getCustomName().equalsIgnoreCase(TradeCommand.shopVillagerName)) {
                //open Trade inventory
                e.setCancelled(true);
                //p.sendMessage("Open shop...");
                p.openInventory(Shop.instance.openInv());
            }
        }

    }

    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent e) {

    }

}
