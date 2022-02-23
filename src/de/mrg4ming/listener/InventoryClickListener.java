package de.mrg4ming.listener;

import de.mrg4ming.Main;
import de.mrg4ming.control.*;
import de.mrg4ming.data.ShopInventory;
import de.mrg4ming.data.WindowTitle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();

            if(e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) { //check if the current clicked item slot is empty -> if so skip click perform control
                if(e.getView().getTitle().startsWith(WindowTitle.PAGE.title)) {
                    if(e.isShiftClick()) {
                        e.setCancelled(true);
                        return;
                    }

                    if(p.getItemOnCursor() != null && p.getItemOnCursor().getItemMeta() != null) {
                        e.setCancelled(true);
                        return;
                    }

                    tradePageWindow(e, p);

                } else if(e.getView().getTitle().startsWith(WindowTitle.TRADE_EDITOR_PREFIX.title)) { //trade editor window

                    if(e.isShiftClick()) {
                        e.setCancelled(true);
                        return;
                    }

                    tradeEditorWindow(e, p);

                } else if(e.getView().getTitle().startsWith(WindowTitle.TRADE_OPTIONS_PREFIX.title)) { //trading window

                    if(e.isShiftClick()) { //TODO: replace this later with shift click sell/refill method
                        e.setCancelled(true);
                        return;
                    }

                    tradeOptionsWindow(e, p);
                }
            }
        }
    }



    private void tradePageWindow(InventoryClickEvent e, Player p) {
        //Trade page menu

        if(e.getRawSlot() < e.getView().getTopInventory().getSize()) {
            if(!e.getCurrentItem().getType().equals(Material.PAPER)) {
                e.setCancelled(false);
                return;
            }
            e.setCancelled(true);
            //trade selection
            if(e.getRawSlot() < 45) {
                String _tradeName = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                if(Shop.instance.checkIfTradeExists(_tradeName)) {
                    p.openInventory(Shop.instance.getTrade(_tradeName).getTradeOptions());
                } else {
                    p.sendMessage(Main.PREFiX + "§cTrade does not exist!");
                }
            }

            ///region manage Shop inventory navigation
            if(Shop.instance.getShopInvData().pages.size() > 1) {
                switch (e.getRawSlot()) {
                    case 5*9+0 -> {
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
                    }
                    case 5*9 + 8 -> {
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
            }
            ///endregion
        }
    }

    private void tradeEditorWindow(InventoryClickEvent e, Player p) {
        if(e.getRawSlot() < e.getView().getTopInventory().getSize()) {
            e.setCancelled(true);
            String _tradeName = e.getView().getTitle().substring(WindowTitle.TRADE_EDITOR_PREFIX.title.length());

            ///region get trade to modify
            if(Shop.tempTrades.containsKey(p.getUniqueId().toString())) {
                ClickPerformManager.performTradeEditorAction(p, e, Shop.tempTrades.get(p.getUniqueId().toString()), true, _tradeName);
            } else if(Shop.instance.checkIfTradeExists(_tradeName)) {
                ClickPerformManager.performTradeEditorAction(p, e, Shop.instance.getTrade(_tradeName), false, _tradeName);
            } else {
                p.sendMessage(Main.PREFiX + "§cThe trade to be modified does not exist!");
            }
            ///endregion
        }
    }

    private void tradeOptionsWindow(InventoryClickEvent e, Player p) {
        if(e.getRawSlot() < e.getView().getTopInventory().getSize()) {
            e.setCancelled(true);
            String _tradeName = e.getView().getTitle().substring(WindowTitle.TRADE_OPTIONS_PREFIX.title.length());

            ClickPerformManager.performTradeOptionsAction(p, e, _tradeName);
        }
    }

}
