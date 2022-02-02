package de.mrg4ming.control;

import de.mrg4ming.config.Config;
import de.mrg4ming.config.ConfigItem;
import de.mrg4ming.data.BankAccount;
import de.mrg4ming.data.ShopInventory;
import de.mrg4ming.data.Trade;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class Shop implements ConfigItem {

    public enum SyncMode {
        OVERWRITE_SHOP_DATA,
        OVERWRITE_SHOP_INV_DATA;
    }

    public static Shop instance;

    private List<Integer> usedIDs = new ArrayList<>();
    public HashMap<Integer, Trade> trades = new HashMap<>();
    public static HashMap<String, Trade> tempTrades = new HashMap<>();

    private Config cfg;
    private ShopInventory inv;

    public Shop() throws IllegalAccessException {
        if(instance != null) throw new IllegalAccessException("The Shop is already instantiated! Please use 'Bank.instance' instead.");

        instance = this;

        cfg = new Config("shop");
        loadFromConfig();

        inv = new ShopInventory(new ArrayList<>(trades.values()));
    }

    //Create Trade and remove Trade functions
    public boolean addTrade(Trade _trade, SyncMode _mode) {
        if(Shop.instance.getShopInvData().getTrades().size() + 1 > (ShopInventory.MAX_TRADES_PER_PAGE * ShopInventory.MAX_PAGES)) return false;

        Shop.instance.getShopInvData().getTrades().add(_trade);
        syncShopDataWithInv(_mode);

        return true;
    }
    public boolean addTrade(Trade _trade) {
        if(Shop.instance.getShopInvData().getTrades().size() + 1 > (ShopInventory.MAX_TRADES_PER_PAGE * ShopInventory.MAX_PAGES)) return false;

        Shop.instance.getShopInvData().getTrades().add(_trade);
        syncShopDataWithInv(SyncMode.OVERWRITE_SHOP_DATA);


        inv.updatePages();
        return true;
    }

    public Trade getTrade(int _id) {
        if(!trades.containsKey(_id)) return trades.get(_id);
        return null;
    }
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

    public boolean removeTrade(int _id) {
        if(!trades.containsKey(_id)) return false;
        trades.remove(_id);
        syncShopDataWithInv(SyncMode.OVERWRITE_SHOP_INV_DATA);
        inv.updatePages();
        return true;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean checkIfTradeExists(String _name) {
        for(Trade t : trades.values()) {
            if(t.getName().equalsIgnoreCase(_name)) {
                return true;
            }
        }
        return false;
    }

    public List<Trade> getTradesOfBankAccount(BankAccount _account) {
        List<Trade> _trades = new ArrayList<>();
        for(Trade _trade : trades.values()) {
            if(_trade.getOwner().equals(_account)) {
                _trades.add(_trade);
            }
        }
        return _trades;
    }

    public List<Trade> getTradesOfPlayer(Player p) {
        List<Trade> _trades = new ArrayList<>();
        for(Trade _trade : trades.values()) {
            if(_trade.getOwner().getOwners().contains(p.getUniqueId().toString())) {
                _trades.add(_trade);
            }
        }
        return _trades;
    }

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
                overwriteShoInvData();
            }
            default -> {
                overwriteShopData();
            }
        }

        usedIDs.clear();
        for(int key : trades.keySet()) {
            usedIDs.add(key);
        }
    }
    private void overwriteShopData() {
        trades.clear();
        int i = 0;
        for (Trade _t : getShopInvData().getTrades()) {
            trades.put(i, _t);
            i++;
        }
    }
    private void overwriteShoInvData() {
        getShopInvData().getTrades().clear();
        for(Trade _t : trades.values()) {
            getShopInvData().getTrades().add(_t);
        }
    }
    ///endregion

    @Override
    public void loadFromConfig() {
        if(cfg.contains("usedIDs")) {
            usedIDs = cfg.getList("usedIDs");
        }
        if(cfg.contains("trades") && !usedIDs.isEmpty()) {
            for(int id : usedIDs) {
                String _name = (String) cfg.get("trades." + id + ".name");
                int _value = (int) cfg.get("trades." + id + ".value");
                ItemStack _product = cfg.getItemStack("trades." + id + ".product");
                int _mode = (int) cfg.get("trades." + id + ".mode");
                int _storage = (int) cfg.get("trades." + id + ".storage");
                int _bankOwnerId = (int) cfg.get("trades." + id + ".bankOwnerId");

                try {
                    Trade _trade = new Trade(_name, _value, _product, Trade.Mode.values()[_mode - 1], _storage, Bank.instance.accounts.get(_bankOwnerId));
                    trades.put(id, _trade);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void saveToConfig() {
        try {
            cfg.set("usedIDs", null);
            cfg.set("usedIDs", usedIDs);
            cfg.set("accounts", null);
            for(int id : usedIDs) {
                cfg.set("trades." + id + ".name", trades.get(id).getName());
                cfg.set("trades." + id + ".value", trades.get(id).getValue());
                cfg.set("trades." + id + ".product", new ItemStack(trades.get(id).getProduct().getType(), trades.get(id).getProductAmount()));
                cfg.set("trades." + id + ".mode", trades.get(id).getMode().id);
                cfg.set("trades." + id + ".storage", trades.get(id).storage);
                cfg.set("trades." + id + ".bankOwnerId", Bank.instance.getIdByName(trades.get(id).getOwner().name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Inventory openInv() {
        return inv.pages.get(0);
    }

    public Inventory openInv(int _page) {
        return inv.pages.get(_page);
    }

    public ShopInventory getShopInvData() {
        return inv;
    }
}
