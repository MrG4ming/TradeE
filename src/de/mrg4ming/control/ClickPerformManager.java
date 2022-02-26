package de.mrg4ming.control;

import de.mrg4ming.Main;
import de.mrg4ming.data.ShopInventory;
import de.mrg4ming.data.trade.Trade;
import de.mrg4ming.data.trade.TradeItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public final class ClickPerformManager {

    public static void performTradeEditorAction(Player p, InventoryClickEvent e, Trade _trade, boolean _isNewTrade, String _tradeName) {
        switch (e.getRawSlot()) {
            case 0 -> { //switch mode
                switch (_trade.getMode()) {
                    case BUY -> {
                        _trade.setMode(Trade.Mode.SELL);
                        _trade.getConfigurator().updateMode(Trade.Mode.SELL);
                    }
                    case SELL -> {
                        _trade.setMode(Trade.Mode.BUY_AND_SELL);
                        _trade.getConfigurator().updateMode(Trade.Mode.BUY_AND_SELL);
                    }
                    case BUY_AND_SELL -> {
                        _trade.setMode(Trade.Mode.BUY);
                        _trade.getConfigurator().updateMode(Trade.Mode.BUY);
                    }
                }
            }
            case 3 -> { //select Value
                _trade.getConfigurator().setCurrentSelectedValue(TradeConfigurator.Value.PRICE);
            }
            case 5 -> { //select/set Product
                if(p.getItemOnCursor() != null && p.getItemOnCursor().getItemMeta() != null) {
                    TradeItem _item = new TradeItem(p.getItemOnCursor().getType(), 1, p.getItemOnCursor().getEnchantments());

                    _trade.setProduct(_item);
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
                        _trade.getProduct().setAmount(_trade.getProduct().getAmount() - 10);
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
                        _trade.getProduct().setAmount(_trade.getProduct().getAmount() - 1);
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
                        _trade.getProduct().setAmount(1);
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
                        _trade.getProduct().setAmount(_trade.getProduct().getAmount() + 1);
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
                        _trade.getProduct().setAmount(_trade.getProduct().getAmount() + 10);
                    }
                }
            }
            case 8+9 -> { //confirm
                if((Shop.instance.isFull() && _isNewTrade)) {
                    p.sendMessage(Main.PREFiX + "§cMax number of Trades reached!");
                    return;
                }

                _trade.updateTradeOptions();

                if(_trade.isConstant()) {
                    if(_isNewTrade) {
                        Shop.instance.addTrade(_trade);
                        Shop.tempTrades.remove(p.getUniqueId().toString());
                    } else {
                        Shop.instance.trades.replace(Shop.getKeyByValue(Shop.instance.trades, Shop.instance.getTrade(_tradeName)), _trade);
                    }
                } else {
                    if(_isNewTrade) {
                        Shop.instance.addTrade(_trade);
                        Shop.tempTrades.remove(p.getUniqueId().toString());
                    } else {
                        Shop.instance.trades.replace(Shop.getKeyByValue(Shop.instance.trades, Shop.instance.getTrade(_tradeName)), _trade);
                    }
                }
                p.closeInventory();
            }
            default -> {
                e.setCancelled(false);
            }
        }
    }

    public static void performTradeOptionsAction(Player p, InventoryClickEvent e, String _tradeName) {
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
                if(_trade.getMode().equals(Trade.Mode.BUY) || _trade.getMode().equals(Trade.Mode.BUY_AND_SELL)) {
                    ClickPerformManager.performBuyAction(p, _trade);
                }
            }
            case 5 -> { //Sell
                if(_trade.getMode().equals(Trade.Mode.SELL) || _trade.getMode().equals(Trade.Mode.BUY_AND_SELL)) {
                    ClickPerformManager.performSellAction(p, _trade);
                }
            }
            case 8 -> { //storage refill/empty
                if(!_trade.isConstant()) {
                    performStorageRefillAndEmptyAction(p, _trade);
                }
            }
            default -> {
                e.setCancelled(false);
            }
        }
    }

    public static void performBuyAction(Player p, Trade trade) {
        if(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()) == null) { //check if player has a bank account and if so is it his main account
            p.sendMessage(Main.PREFiX + "§cYou don't own a bank account! §8Please ask an operator to create one for you.");
            return;
        }

        double _minValue = trade.getValue() / trade.getProduct().getAmount(); //calculate what's needed to buy one item of the given product type
        if(_minValue < 0.01) _minValue = 0.01f; //clamp value so a player can not pay 0$

        int _maxAmount = Math.toIntExact(Math.round(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()).getCapital() / _minValue)); //calculate how many items the player can buy with his current capital

        if(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()).getCapital() > _minValue) { //check if the player has enough money to buy at least one item

            if(trade.storage == 0) { //check if the storage is empty
                p.sendMessage(Main.PREFiX + "§aGood News: you are not broke! §cBad news: the trade storage is empty.");
                return;
            }

            if((trade.storage > trade.getProduct().getAmount()) || (trade.storage == -1)) { //check if the storage of the trade has enough items in it (or is infinite) to give the player one "stack" of the given size (productAmount)

                if((Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()).getCapital() > trade.getValue())) { //check if player can buy the full amount with his current capital

                    PlayerUtils.givePlayerItems(p, trade.getProduct().getItemStack(), trade.getProduct().getAmount()); //give player product of specific amount
                    if(!trade.isConstant()) trade.storage -= trade.getProduct().getAmount(); //remove items from storage if the trade is nor constant
                    trade.updateTradeOptions();
                    if(!trade.isConstant()) {
                        Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()).transfer(trade.getOwner(), trade.getValue()); //transfer the full value of the trade from player to trade owner
                    } else {
                        Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()).withdraw(trade.getValue()); //withdraw the full value of the trade from player 'cause it is an infinite trade
                    }
                }

            } else {
                if(trade.storage < _maxAmount) _maxAmount = trade.storage; //clamp the max amount to the storage value so the player can't buy more than what's in stock
                PlayerUtils.givePlayerItems(p, trade.getProduct().getItemStack(), _maxAmount); //give the player the few items in the storage
                trade.storage -= _maxAmount; //remove items from storage
                trade.updateTradeOptions();
                Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()).transfer(trade.getOwner(), _minValue * _maxAmount); //transfer the relative value of the trade items from player to trade owner
            }
        } else {
            p.sendMessage(Main.PREFiX + "§cYou're broke af! Get some money before you talk to this trader again.");
        }
    }

    public static void performSellAction(Player p, Trade trade) {
        if(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()) == null) { //check if player has a bank account and if so is it his main account
            p.sendMessage(Main.PREFiX + "§cYou don't own a bank account! §8Please ask an operator to create one for you.");
            return;
        }

        float _minValue = trade.getValue() / trade.getProduct().getAmount(); //calculate what's needed to buy one item of the given product type
        if(_minValue < 0.01) _minValue = 0.01f; //clamp value so a player can not get less than 0.01$

        if(p.getItemOnCursor() != null && p.getItemOnCursor().getItemMeta() != null) {
            if(trade.getProduct().equals(TradeItem.itemStackToTradeItem(p.getItemOnCursor()))) { //check if item on cursor is equal to the trade product

                //System.out.println("is Trade constant: " + trade.isConstant());
                if(!trade.isConstant()) { //check if trade is constant (affects the payment and storage)
                    trade.getOwner().transfer(Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()), _minValue * p.getItemOnCursor().getAmount()); //transfer money to trade owner
                    trade.storage += p.getItemOnCursor().getAmount();
                    trade.updateTradeOptions();
                } else {
                    Bank.instance.getMainAccountOfPlayer(p.getUniqueId().toString()).deposit(_minValue * p.getItemOnCursor().getAmount()); //deposit money from player
                }

                p.getItemOnCursor().setAmount(0);
            } else p.sendMessage(Main.PREFiX + "§cItem doesn't match the item in the storage.");
        }
    }

    public static void performStorageRefillAndEmptyAction(Player p, Trade trade) {
        if(trade.getOwner().getOwners().contains(p.getUniqueId().toString())) { //check if player is the owner of this trade
            if(trade.isConstant()) {
                p.sendMessage(Main.PREFiX + "§bThis is an constant trade! It doesn't need to be refilled.");
                return;
            }
            if(p.getItemOnCursor() != null && p.getItemOnCursor().getItemMeta() != null) { //check if player has an item on the cursor
                if(trade.getProduct().equals(TradeItem.itemStackToTradeItem(p.getItemOnCursor()))) { //check if item on cursor is similar to the trade product
                    trade.storage += p.getItemOnCursor().getAmount();
                    p.getItemOnCursor().setAmount(0);
                    trade.updateTradeOptions();
                }
            } else {
                PlayerUtils.givePlayerItems(p, trade.getProduct().getItemStack(), trade.storage);
                trade.storage -= trade.storage;
                p.sendMessage(Main.PREFiX + "§aTrade storage successfully emptied.");
                trade.updateTradeOptions();
            }

        }
    }
}
