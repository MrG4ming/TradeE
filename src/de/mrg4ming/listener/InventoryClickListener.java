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
                if(e.getClickedInventory().equals(e.getView().getTopInventory())) {
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
                    ///endregion
                }
            } else if(e.getView().getTitle().startsWith(WindowTitle.TRADE_EDITOR_PREFIX.title)) {
                if(e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    e.setCancelled(true);
                    String _tradeName = e.getView().getTitle().substring(WindowTitle.TRADE_EDITOR_PREFIX.title.length());

                    ///region get trade to modify
                    if(Shop.tempTrades.containsKey(p.getUniqueId().toString())) {
                        performTradeEditorAction(p, e, Shop.tempTrades.get(p.getUniqueId().toString()), true, _tradeName);
                    } else if(Shop.instance.checkIfTradeExists(_tradeName)) {
                        performTradeEditorAction(p, e, Shop.instance.getTrade(_tradeName), false, _tradeName);
                    } else {
                        p.sendMessage(Main.PREFiX + "§cThe trade to be modified does not exist!");
                    }
                    ///endregion
                }
            } else if(e.getView().getTitle().startsWith(WindowTitle.TRADE_OPTIONS_PREFIX.title)) {
                if(e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    e.setCancelled(true);
                    String _tradeName = e.getView().getTitle().substring(WindowTitle.TRADE_EDITOR_PREFIX.title.length());

                    performTradeOptionsAction(p, e, _tradeName);
                }
            }
        }
    }

    private void performTradeEditorAction(Player p, InventoryClickEvent e, Trade _trade, boolean _isNewTrade, String _tradeName) {
        switch (e.getRawSlot()) {
            case 3 -> { //select Value
                _trade.getConfigurator().setCurrentSelectedValue(TradeConfigurator.Value.PRICE);
            }
            case 5 -> { //select/set Product
                if(p.getItemOnCursor() != null && p.getItemOnCursor().getItemMeta() != null) {
                    _trade.setProduct(p.getItemOnCursor());
                    return;
                }
                _trade.getConfigurator().setCurrentSelectedValue(TradeConfigurator.Value.PRODUCT);
            }
            case 8+3 -> { //remove 10
                switch (_trade.getConfigurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        if(_trade.getValue() > 10) {
                            _trade.getConfigurator().updateValue(_trade.getValue() - 10);
                            _trade.setValue(_trade.getValue() - 10);
                        }
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.getProductAmount() - 10);
                    }
                }
            }
            case 8+4 -> { //remove 1
                switch (_trade.getConfigurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        if(_trade.getValue() >= 1) {
                            _trade.getConfigurator().updateValue(_trade.getValue() - 1);
                            _trade.setValue(_trade.getValue() - 1);
                        } else {
                            _trade.setValue(0);
                        }
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.getProductAmount() - 1);
                    }
                }
            }
            case 8+5 -> { //reset value
                switch (_trade.getConfigurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        _trade.getConfigurator().updateValue(0);
                        _trade.setValue(0);
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(1);
                    }
                }

            }
            case 8+6 -> { //add 1
                switch (_trade.getConfigurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        _trade.getConfigurator().updateValue(_trade.getValue() + 1);
                        _trade.setValue(_trade.getValue() + 1);
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.getProductAmount() + 1);
                    }
                }
            }
            case 8+7 -> { //add 10
                switch (_trade.getConfigurator().getCurrentSelected()) {
                    case NOTHING -> {
                        p.sendMessage(Main.PREFiX + "§cPlease select a value you want to change first!");
                    }
                    case PRICE -> {
                        _trade.getConfigurator().updateValue(_trade.getValue() + 10);
                        _trade.setValue(_trade.getValue() + 10);
                    }
                    case PRODUCT -> {
                        _trade.setProductAmount(_trade.getProductAmount() + 10);
                    }
                }
            }
            case 8+9 -> { //confirm
                if((Shop.instance.isFull() && _isNewTrade)) {
                    p.sendMessage(Main.PREFiX + "§cMax number of Trades reached!");
                    return;
                }

                _trade.updateTradeOptions();

                if(_isNewTrade) {
                    Shop.instance.addTrade(_trade);
                    Shop.tempTrades.remove(p.getUniqueId().toString());
                } else {
                    Shop.instance.trades.replace(Shop.getKeyByValue(Shop.instance.trades, Shop.instance.getTrade(_tradeName)), _trade);
                }
                p.closeInventory();
            }
        }
    }

    private void performTradeOptionsAction(Player p, InventoryClickEvent e, String _tradeName) {
        if(!Shop.instance.checkIfTradeExists(_tradeName)) return;
        Trade _trade = Shop.instance.getTrade(_tradeName);

        switch (e.getRawSlot()) {
            case 0 -> {
                p.openInventory(Shop.instance.openInv(ShopInventory.currentPageOpenedByPlayer.get(p.getUniqueId().toString())));
            }
        }
    }
}
