package de.mrg4ming.listener;

import de.mrg4ming.control.Shop;
import de.mrg4ming.data.ShopInventory;
import de.mrg4ming.data.Trade;
import de.mrg4ming.data.WindowTitle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if(e.getView().getTitle().startsWith(WindowTitle.PAGE.title)) {
                e.setCancelled(true);
                //manage Shop inventory navigation
                if(Shop.instance.getShopInvData().pages.size() > 1) {
                    if(e.getRawSlot() == 5*9+0) {
                        if(!ShopInventory.currentPageOpenedByPlayer.containsKey(p.getUniqueId().toString())) {
                            ShopInventory.currentPageOpenedByPlayer.put(p.getUniqueId().toString(), 0);
                            p.openInventory(Shop.instance.openInv());
                        } else if(ShopInventory.currentPageOpenedByPlayer.get(p.getUniqueId().toString()) <=0 ) {
                            p.openInventory(Shop.instance.openInv());
                        } else {
                            int _page = ShopInventory.currentPageOpenedByPlayer.get(p.getUniqueId().toString());
                            p.openInventory(Shop.instance.openInv(_page-1));
                            ShopInventory.currentPageOpenedByPlayer.put(p.getUniqueId().toString(), _page - 1);
                        }

                    } else if(e.getRawSlot() == 5*9+8) {
                        if(!ShopInventory.currentPageOpenedByPlayer.containsKey(p.getUniqueId().toString())) {
                            ShopInventory.currentPageOpenedByPlayer.put(p.getUniqueId().toString(), 0);
                            p.openInventory(Shop.instance.openInv());
                        } else if(ShopInventory.currentPageOpenedByPlayer.get(p.getUniqueId().toString()) >= Shop.instance.getShopInvData().pages.size() ) {
                            ShopInventory.currentPageOpenedByPlayer.put(p.getUniqueId().toString(), Shop.instance.getShopInvData().pages.size());
                            p.openInventory(Shop.instance.openInv(Shop.instance.getShopInvData().pages.size()));
                        } else {
                            int _page = ShopInventory.currentPageOpenedByPlayer.get(p.getUniqueId().toString());
                            p.openInventory(Shop.instance.openInv(_page + 1));
                            ShopInventory.currentPageOpenedByPlayer.put(p.getUniqueId().toString(), _page + 1);
                        }
                    }
                }
            } else if(e.getView().getTitle().startsWith(WindowTitle.TRADE_EDITOR_PREFIX.title)) {
                e.setCancelled(true);
                String _tradeName = e.getView().getTitle().substring(WindowTitle.TRADE_EDITOR_PREFIX.title.length());
                Trade _trade = Shop.instance.getTrade(_tradeName);
                switch (e.getRawSlot()) {
                    case 2 -> {

                    }
                    case 6 -> {

                    }
                    case 8+3 -> { //remove 10

                    }
                    case 8+4 -> { //remove 1

                    }
                    case 8+5 -> { //reset value

                    }
                    case 8+6 -> { //add 1

                    }
                    case 8+7 -> { //add 10

                    }
                    case 8+9 -> { //confirm

                    }
                }
            }
        }
    }
}
