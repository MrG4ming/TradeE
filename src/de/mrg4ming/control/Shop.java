package de.mrg4ming.control;

import de.mrg4ming.config.Config;
import de.mrg4ming.config.ConfigData;
import de.mrg4ming.data.BankAccount;
import de.mrg4ming.data.ShopInventory;
import de.mrg4ming.data.trade.Trade;
import de.mrg4ming.data.trade.TradeItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.*;

public class Shop implements ConfigData {

    public enum SyncMode {
        OVERWRITE_SHOP_DATA,
        OVERWRITE_SHOP_INV_DATA;
    }

    public static final SyncMode DEFAULT_SYNC_MODE = SyncMode.OVERWRITE_SHOP_DATA;
    public static Shop instance;

    private List<Integer> usedIDs = new ArrayList<>();
    public HashMap<Integer, Trade> trades = new HashMap<>();
    public static HashMap<String, Trade> tempTrades = new HashMap<>();

    private Config cfg;
    private ShopInventory inv;

    /**
     * Use this method only once in the main/onEnabled method!
     * @throws IllegalAccessException
     */
    public Shop() throws IllegalAccessException {
        if(instance != null) throw new IllegalAccessException("The Shop is already instantiated! Please use 'Bank.instance' instead.");

        instance = this;

        cfg = new Config("shop");
        loadFromConfig();

        inv = new ShopInventory(new ArrayList<>(trades.values()));
    }

    /**
     * Add a trade to the shop system. And sync the shop system with the shop inventory.
     * @param _trade the trade
     * @param _mode the sync mode
     * @return false if the max amount of trades is reached
     */
    public boolean addTrade(Trade _trade, SyncMode _mode) {
        if(Shop.instance.getShopInvData().getTrades().size() + 1 > (ShopInventory.MAX_TRADES_PER_PAGE * ShopInventory.MAX_PAGES)) return false;

        Shop.instance.getShopInvData().getTrades().add(_trade);
        syncShopDataWithInv(_mode);

        return true;
    }

    /**
     * Add a trade to the shop system. (uses the default sync mode: {@link #DEFAULT_SYNC_MODE})
     * @param _trade the trade
     * @return
     */
    public boolean addTrade(Trade _trade) {
        if(Shop.instance.getShopInvData().getTrades().size() + 1 > (ShopInventory.MAX_TRADES_PER_PAGE * ShopInventory.MAX_PAGES)) return false;

        Shop.instance.getShopInvData().getTrades().add(_trade);
        syncShopDataWithInv(DEFAULT_SYNC_MODE);


        inv.updatePages();
        return true;
    }

    /**
     * @param _id the trade id
     * @return the trade with the given id (null if the trade does not exist)
     */
    public Trade getTrade(int _id) {
        if(!trades.containsKey(_id)) return trades.get(_id);
        return null;
    }

    /**
     * @param _name the trade name
     * @return the trade with the given name (null if the trade does not exist)
     */
    public Trade getTrade(String _name) {
        if(checkIfTradeExists(_name)) {
            for(Trade t : trades.values()) {
                if(t.getName().equalsIgnoreCase(_name)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Removes a trade from the shop system.
     * @param _id
     * @return false if the trade with the given id does not exist
     */
    public boolean removeTrade(int _id) {
        if(!trades.containsKey(_id)) return false;
        trades.remove(_id);
        usedIDs.remove(_id);
        syncShopDataWithInv(SyncMode.OVERWRITE_SHOP_INV_DATA);
        inv.updatePages();
        return true;
    }

    /**
     * Checks if the trade with the given name exists.
     * @param _name the name of the trade
     * @return false if it does not exist
     */
    public boolean checkIfTradeExists(String _name) {
        for(Trade t : trades.values()) {
            if(t.getName().equalsIgnoreCase(_name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param _account the bank account
     * @return the trades the given bank account owns
     */
    public List<Trade> getTradesOfBankAccount(BankAccount _account) {
        List<Trade> _trades = new ArrayList<>();
        for(Trade _trade : trades.values()) {
            if(!_trade.isConstant()) {
                if(_trade.getOwner().equals(_account)) {
                    _trades.add(_trade);
                }
            }
        }
        return _trades;
    }

    /**
     * @param p the player
     * @return all trades of the given player
     */
    public List<Trade> getTradesOfPlayer(Player p) {
        List<Trade> _trades = new ArrayList<>();
        for(Trade _trade : trades.values()) {
            if(_trade.getOwner() != null) {
                if(_trade.getOwner().getOwners().contains(p.getUniqueId().toString())) {
                    _trades.add(_trade);
                }
            } else if(p.hasPermission("tradee.trade.manager")) {
                _trades.add(_trade);
            }
        }
        return _trades;
    }

    /**
     * Checks if the shop is full.
     * @return true if the shop is full
     */
    public boolean isFull() {
        if(trades.size() >= (ShopInventory.MAX_TRADES_PER_PAGE * ShopInventory.MAX_PAGES)) {
            return true;
        }
        return false;
    }

    ///region sync shop data
    private void syncShopDataWithInv(SyncMode _sMode) {
        switch(_sMode) {
            case OVERWRITE_SHOP_INV_DATA -> {
                overwriteShopInvData();
            }
            default -> {
                overwriteShopData();
            }
        }

        usedIDs.clear();
        for(int key : trades.keySet()) {
            usedIDs.add(key);
        }

        //this.saveToConfig();
    }
    private void overwriteShopData() {
        trades.clear();
        int i = 0;
        for (Trade _t : getShopInvData().getTrades()) {
            trades.put(i, _t);
            i++;
        }
    }
    private void overwriteShopInvData() {
        getShopInvData().getTrades().clear();
        for(Trade _t : trades.values()) {
            getShopInvData().getTrades().add(_t);
        }
    }
    ///endregion

    /**
     * Loads the shop system and trades from the config (=> "./TradeE/shop.yml").
     */
    @Override
    public void loadFromConfig() {
        cfg.reload();

        if(cfg.contains("usedIDs")) {
            usedIDs = cfg.getList("usedIDs");
        }
        if(cfg.contains("trades") && !usedIDs.isEmpty()) {
            for(int id : usedIDs) {
                String _name = (String) cfg.get("trades." + id + ".name");
                int _value = (int) cfg.get("trades." + id + ".value");
                TradeItem _product = new TradeItem();
                _product.loadFrom(cfg, "trades." + id + ".product");
                int _mode = (int) cfg.get("trades." + id + ".mode");
                int _storage = (Integer) cfg.get("trades." + id + ".storage");
                boolean _isConstant = (boolean) cfg.get("trades." + id + ".isConstant");
                if(!_isConstant) {
                    int _bankOwnerId = (int) cfg.get("trades." + id + ".bankOwnerId");

                    try {
                        Trade _trade = new Trade(_name, _value, _product, Trade.Mode.values()[_mode], _storage, Bank.instance.accounts.get(_bankOwnerId));
                        trades.put(id, _trade);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Trade _trade = Trade.createConstantTrade(_name, _value, _product, Trade.Mode.values()[_mode]);
                        trades.put(id, _trade);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Saves the shop system and trades to the config (=> "./TradeE/shop.yml").
     */
    @Override
    public void saveToConfig() {
        try {
            cfg.set("usedIDs", null);
            cfg.set("usedIDs", usedIDs);
            cfg.set("trades", null);
            for(int id : usedIDs) {
                cfg.set("trades." + id + ".name", trades.get(id).getName());
                cfg.set("trades." + id + ".value", trades.get(id).getValue());
                trades.get(id).getProduct().saveTo(cfg, "trades." + id + ".product");
                cfg.set("trades." + id + ".mode", trades.get(id).getMode().id);
                cfg.set("trades." + id + ".storage", trades.get(id).storage);
                if(trades.get(id).isConstant()) {
                    cfg.set("trades." + id + ".isConstant", true);
                    cfg.set("trades." + id + ".bankOwnerId", null);
                } else {
                    cfg.set("trades." + id + ".isConstant", false);
                    cfg.set("trades." + id + ".bankOwnerId", Bank.instance.getIdByName(trades.get(id).getOwner().name));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the main shop page
     */
    public Inventory openInv() {
        return inv.pages.get(0);
    }

    /**
     * @param _page the shop page
     * @return the given shop page
     */
    public Inventory openInv(int _page) {
        return inv.pages.get(_page);
    }

    /**
     * @return the shop page inventory container
     */
    public ShopInventory getShopInvData() {
        return inv;
    }
}
