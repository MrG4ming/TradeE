package de.mrg4ming.listener;

import de.mrg4ming.Main;
import de.mrg4ming.control.Shop;
import de.mrg4ming.control.TradeConfigurator;
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

                ///region get trade to modify
                if(Shop.tempTrades.containsKey(p.getUniqueId().toString())) {
                    performAction(p, e.getRawSlot(), Shop.tempTrades.get(p.getUniqueId().toString()), true);
                } else if(Shop.instance.checkIfTradeExists(_tradeName)) {
                    performAction(p, e.getRawSlot(), Shop.instance.getTrade(_tradeName), true);
                } else {
                    p.sendMessage(Main.PREFiX + "§cThe trade to be modified does not exist!");
                }
                ///endregion



            }
        }
    }

    private void performAction(Player p, int rawSlot, Trade _trade, boolean _isNewTrade) {
        switch (rawSlot) {
            case 3 -> { //select Value
                _trade.configurator().setCurrentSelectedValue(TradeConfigurator.Value.PRICE);
            }
            case 5 -> { //select Product
                _trade.configurator().setCurrentSelectedValue(TradeConfigurator.Value.PRODUCT);
            }
            case 8+3 -> { //remove 10
                switch (_trade.configurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        if(_trade.value() > 10) {
                            _trade.configurator().updateValue(_trade.value() - 10);
                            _trade = _trade.copy(_trade.value() - 10);
                        }
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.product().getAmount() - 10);
                    }
                }
            }
            case 8+4 -> { //remove 1
                switch (_trade.configurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        if(_trade.value() >= 1) {
                            _trade.configurator().updateValue(_trade.value() - 1);
                            _trade = _trade.copy(_trade.value() - 1);
                        } else {
                            _trade = _trade.copy(0);
                        }
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.product().getAmount() - 1);
                    }
                }
            }
            case 8+5 -> { //reset value
                _trade = _trade.copy(1);
                _trade = _trade.copy(1);
            }
            case 8+6 -> { //add 1
                switch (_trade.configurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        _trade.configurator().updateValue(_trade.value() + 1);
                        _trade = _trade.copy(_trade.value() + 1);
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.product().getAmount() + 1);
                    }
                }
            }
            case 8+7 -> { //add 10
                switch (_trade.configurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        _trade.configurator().updateValue(_trade.value() + 10);
                        _trade = _trade.copy(_trade.value() + 10);
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.product().getAmount() + 10);
                    }
                }
            }
            case 8+9 -> { //confirm
                if(Shop.instance.isFull() && _isNewTrade) {
                    p.sendMessage(Main.PREFiX + "§cMax number of Trades reached!");
                    return;
                }

                Shop.instance.addTrade(_trade);
                Shop.tempTrades.remove(p.getUniqueId().toString());
            }
        }
    }
}
