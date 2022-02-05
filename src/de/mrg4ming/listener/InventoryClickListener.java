package de.mrg4ming.listener;

import de.mrg4ming.Main;
import de.mrg4ming.control.Bank;
import de.mrg4ming.control.Shop;
import de.mrg4ming.control.TradeConfigurator;
import de.mrg4ming.data.ShopInventory;
import de.mrg4ming.data.Trade;
import de.mrg4ming.data.WindowTitle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();

            if(e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
                if(e.getView().getTitle().startsWith(WindowTitle.PAGE.title)) {

                    if(e.isShiftClick()) e.setCancelled(true);

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

                    if(e.isShiftClick()) e.setCancelled(true);

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

                    if(e.isShiftClick()) e.setCancelled(true);

                    if(e.getClickedInventory().equals(e.getView().getTopInventory())) {
                        e.setCancelled(true);
                        String _tradeName = e.getView().getTitle().substring(WindowTitle.TRADE_OPTIONS_PREFIX.title.length());

                        performTradeOptionsAction(p, e, _tradeName);
                    }
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
                if(ShopInventory.currentPageOpenedByPlayer.containsKey(p.getUniqueId().toString())) {
                    p.openInventory(Shop.instance.openInv(ShopInventory.currentPageOpenedByPlayer.get(p.getUniqueId().toString())));
                    return;
                }
                p.openInventory(Shop.instance.openInv());
            }
            case 3 -> { //Buy
                ItemStack _item = new ItemStack(_trade.getProduct().getType(), _trade.getProductAmount());
                if(_trade.storage > 0) { //check if storage is not empty
                    if(Bank.instance.getBankAccountsOfPlayer(p).size() < 1) {
                        p.sendMessage(Main.PREFiX + "§cYou don't own a bank account!");
                        return;
                    }

                    HashMap<Integer, ItemStack> _droppedItems = p.getInventory().addItem(_item);
                    if(_droppedItems.size() > 0) {
                        for(ItemStack i : _droppedItems.values()) {
                            p.getWorld().dropItem(p.getLocation(), i);
                        }
                    }

                    Bank.instance.accounts.get(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString())).transfer(_trade.getOwner(), _trade.getValue());
                    _trade.storage--;
                } else p.sendMessage(Main.PREFiX + "§cTrade storage is empty!");
            }
            case 5 -> { //Sell
                if(p.getItemOnCursor() != null && p.getItemOnCursor().getItemMeta() != null) {
                    if(p.getItemOnCursor().getType() != _trade.getProduct().getType() || p.getItemOnCursor().getAmount() < _trade.getProductAmount()) {
                        p.sendMessage(Main.PREFiX + "§cTrade only accepts §6'" + _trade.getProduct().getType().toString().toLowerCase() + "'! §1At least §6'" + _trade.getProductAmount() + "' §cpieces!");
                        return;
                    }
                    p.getItemOnCursor().setAmount(p.getItemOnCursor().getAmount() - _trade.getProductAmount());
                    _trade.getOwner().transfer(Bank.instance.accounts.get(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString())), _trade.getValue());
                    _trade.storage++;
                } else p.sendMessage(Main.PREFiX + "§cPlease click with an item stack on the sell option.");
            }
        }
    }
}
