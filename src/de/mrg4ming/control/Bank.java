package de.mrg4ming.control;

import de.mrg4ming.config.Config;
import de.mrg4ming.config.ConfigItem;
import de.mrg4ming.data.BankAccount;
import de.mrg4ming.data.trade.Trade;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bank implements ConfigItem {

    public static Bank instance;
    public Config config;

    private final int START_CAPITAL = 5000;

    public HashMap<Integer, BankAccount> accounts = new HashMap<>();
    public HashMap<String, Integer> mainAccounts = new HashMap<>();
    private List<Integer> usedIDs = new ArrayList<>();
    private List<String> usedUUIDs = new ArrayList<>();

    /**
     * Use this method only once in the main/onEnabled method!
     * @throws IllegalAccessException
     */
    public Bank() throws IllegalAccessException {
        if(instance != null) throw new IllegalAccessException("The Bank is already instantiated! Please use 'Bank.instance' instead.");

        instance = this;

        config = new Config("bank");
        loadFromConfig();
    }


    /**
     * Creates a bank account.
     * @param _name the name of the bank account (if already used it returns 0 as id)
     * @param _owner the uuid of the player who should own this bank account
     * @return the id of the bank account
     */
    public int createBankAccount(String _name, String _owner) {
        for(BankAccount ac : accounts.values()) {
            if(ac.name.equals(_name)) {
                return 0;
            }
        }

        int id = 1;
        boolean search = true;
        while(search) {
            if(!usedIDs.contains(id)) {
                accounts.put(id, new BankAccount(_name, _owner, START_CAPITAL));
                usedIDs.add(id);

                if(getMainAccountIdOfPlayer(_owner) < 0) {
                    setMainAccountOfPlayer(_owner, accounts.get(id));
                } else if(getMainAccountIdOfPlayer(_owner) < 1) {
                    mainAccounts.put(_owner, id);
                }

                search = false;
                return id;
            } /*else {
                System.out.println(id);
            }*/
            id++;
        }
        return 0;
    }

    /**
     * Removes a bank account safely (=> also removed from trades).
     * @param _id the id of the bank account to be removed
     */
    public void removeAccount(int _id) {
        if(accounts.containsKey(_id) && usedIDs.contains(_id)) {

            accounts.get(_id).getOwners().forEach(s -> {
                String _owner = (String) s;
                if(getMainAccountIdOfPlayer(_owner) == _id) {
                    for(BankAccount _bankAccount : getBankAccountsOfPlayer(_owner)) {
                        if(getIdByName(_bankAccount.name) != _id) {
                            setMainAccountOfPlayer(_owner, _bankAccount);
                            break;
                        }
                    }
                }
            });

            for(Trade t : Shop.instance.getTradesOfBankAccount(accounts.get(_id))) {
                Shop.instance.removeTrade(PlayerUtils.getKeyByValue(Shop.instance.trades, t));
            }
            Shop.tempTrades.forEach((s, trade) -> {
                if(trade.getOwner().equals(accounts.get(_id).getOwners().get(0))) { //later when/if multiple owners are added this needs to be changed
                    //closing all inventories to prevent erroneous confirmation of the creation of a trade
                    for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if(p.getUniqueId().toString().equals(s)) {
                            p.closeInventory();
                        }
                    }

                    //removing temp trades
                    Shop.tempTrades.remove(s);
                }
            });

            accounts.remove(_id);
            //System.out.println("List of used id's: " + usedIDs);
            usedIDs.remove(Integer.valueOf(_id));

            saveToConfig();
        } else {
            throw new NullPointerException("Bank account id does not exist or is not registered!");
        }
    }


    /**
     * @param _uuid the uuid of the player, whose main account id is searched
     * @return the main account id of the player
     */
    public int getMainAccountIdOfPlayer(String _uuid) {
        if(usedUUIDs.contains(_uuid)) {
            if(mainAccounts.containsKey(_uuid)) {
                return mainAccounts.get(_uuid);
            } else {
                mainAccounts.put(_uuid, 0);
                return 0;
            }
        }
        return -1;
    }

    /**
     * @param _uuid the uuid of the player, whose main account is searched
     * @return the main account of the player
     */
    public BankAccount getMainAccountOfPlayer(String _uuid) {
        if(usedUUIDs.contains(_uuid)) {
            if(mainAccounts.containsKey(_uuid)) {
                return Bank.instance.accounts.get(mainAccounts.get(_uuid));
            } else if(getBankAccountsOfPlayer(_uuid).size() > 0) {
                mainAccounts.put(_uuid, getIdByName(getBankAccountsOfPlayer(_uuid).get(0).name));
                return Bank.instance.accounts.get(mainAccounts.get(_uuid));
            }
        }
        return null;
    }

    /**
     * Sets the main bank account of a player.
     * @param _uuid the uuid of the player, whose main account you want to set
     * @param _account the new main account of the player
     * @return false if the given account does not exist or is not owned by the player
     */
    public boolean setMainAccountOfPlayer(String _uuid, BankAccount _account) {
        if(accounts.containsValue(_account) && _account.getOwners().contains(_uuid)) {
            usedUUIDs.add(_uuid);
            mainAccounts.put(_uuid, getIdByName(_account.name));
            return true;
        }
        return false;
    }

    /**
     * @param _name the name of a bank account
     * @return the id of the bank account, whose name is _name (0 if account does not exist)
     */
    public int getIdByName(String _name) {
        for(int id : usedIDs) {
            if(accounts.get(id).name.equals(_name)) {
                return id;
            }
        }
        return 0;
    }

    /**
     * @param p the player
     * @return all bank accounts owned by the player
     */
    public List<BankAccount> getBankAccountsOfPlayer(Player p) {
        List<BankAccount> _accounts = new ArrayList<>();
        for(BankAccount _account : accounts.values()) {
            if(_account.getOwners().contains(p.getUniqueId().toString())) {
                _accounts.add(_account);
            }
        }
        return _accounts;
    }

    /**
     * @param _uuid uuid of the player
     * @return all bank accounts owned by the player
     */
    public List<BankAccount> getBankAccountsOfPlayer(String _uuid) {
        List<BankAccount> _accounts = new ArrayList<>();
        for(BankAccount _account : accounts.values()) {
            if(_account.getOwners().contains(_uuid)) {
                _accounts.add(_account);
            }
        }
        return _accounts;
    }

    /**
     * Saves the bank system and accounts to the config (=> "./TradeE/bank.yml").
     */
    public void saveToConfig() {
        try {
            config.set("usedIDs", null);
            config.set("usedIDs", usedIDs);
            config.set("accounts", null);
            for(int id : usedIDs) {
                config.set("accounts." + id + ".owner", accounts.get(id).getOwners());
                config.set("accounts." + id + ".capital", Math.round(accounts.get(id).getCapital() * 100f) / 100f);
                config.set("accounts." + id + ".name", accounts.get(id).name);
            }

            config.set("usedUUIDs", null);
            config.set("usedUUIDs", usedUUIDs);
            config.set("mainAccounts", null);
            for(String s : usedUUIDs) {
                config.set("mainAccounts." + s, mainAccounts.get(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the bank system and accounts from the config (=> "./TradeE/bank.yml").
     */
    public void loadFromConfig() {
        config.reload();

        if(config.contains("usedIDs")) {
            usedIDs = config.getList("usedIDs");
        }
        if(config.contains("usedUUIDs")) {
            usedUUIDs = config.getList("usedUUIDs");
        }
        if(config.contains("accounts") && !usedIDs.isEmpty()) {
            for(int id : usedIDs) {
                List<String> _owners = config.getList("accounts." + id + ".owner");
                double _capital = Math.round((double) config.get("accounts." + id + ".capital") * 100) / 100;
                String _name = (String) config.get("accounts." + id + ".name");

                accounts.put(id, new BankAccount(_name, _owners, _capital));
            }
        }
        if(config.contains("mainAccounts") && !usedUUIDs.isEmpty()) {
            for(String s : usedUUIDs) {
                int _id = (int) config.get("mainAccounts." + s);
                mainAccounts.put(s, _id);
            }
        }
    }

}
