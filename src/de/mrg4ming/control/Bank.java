package de.mrg4ming.control;

import de.mrg4ming.config.Config;
import de.mrg4ming.config.ConfigItem;
import de.mrg4ming.data.BankAccount;
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
    private List<Integer> usedIDs = new ArrayList<>();

    public Bank() throws IllegalAccessException {
        if(instance != null) throw new IllegalAccessException("The Bank is already instantiated! Please use 'Bank.instance' instead.");

        instance = this;

        config = new Config("bank");
        loadFromConfig();
    }

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
                search = false;
                return id;
            } else {
                System.out.println(id);
            }
            id++;
        }
        return 0;
    }

    public void removeAccount(int _id) {
        if(accounts.containsKey(_id) && usedIDs.contains(_id)) {
            accounts.remove(_id);
            //System.out.println("List of used id's: " + usedIDs);
            usedIDs.remove(Integer.valueOf(_id));
            saveToConfig();
        } else {
            throw new NullPointerException("Bank account id does not exist or is not registered!");
        }
    }

    public int getIdByName(String _name) {
        for(int id : usedIDs) {
            if(accounts.get(id).name.equals(_name)) {
                return id;
            }
        }
        return 0;
    }

    public List<BankAccount> getBankAccountsOfPlayer(Player p) {
        List<BankAccount> _accounts = new ArrayList<>();
        for(BankAccount _account : accounts.values()) {
            if(_account.getOwners().contains(p.getUniqueId().toString())) {
                _accounts.add(_account);
            }
        }
        return _accounts;
    }

    public void saveToConfig() {
        try {
            config.set("usedIDs", null);
            config.set("usedIDs", usedIDs);
            config.set("accounts", null);
            for(int id : usedIDs) {
                config.set("accounts." + id + ".owner", accounts.get(id).getOwners());
                config.set("accounts." + id + ".capital", accounts.get(id).getCapital());
                config.set("accounts." + id + ".name", accounts.get(id).name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromConfig() {
        if(config.contains("usedIDs")) {
            usedIDs = config.getList("usedIDs");
        }
        if(config.contains("accounts") && !usedIDs.isEmpty()) {
            for(int id : usedIDs) {
                List<String> _owners = config.getList("accounts." + id + ".owner");
                int _capital = (Integer) config.get("accounts." + id + ".capital");
                String _name = (String) config.get("accounts." + id + ".name");

                accounts.put(id, new BankAccount(_name, _owners, _capital));
            }
        }
    }

}
