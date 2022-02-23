package de.mrg4ming.data;

import de.mrg4ming.control.Bank;

import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    public String name;

    private List<String> owners;
    private double capital;

    public BankAccount(String _name, String _owner) {
        owners = new ArrayList<>();

        this.name = _name;
        this.owners.add(_owner);
    }
    public BankAccount(String _name, String _owner, double _capital) {
        owners = new ArrayList<>();

        this.name = _name;
        this.owners.add(_owner);
        this.capital = _capital;
    }

    public BankAccount(String _name, List<String> _owner) {
        owners = new ArrayList<>();

        this.name = _name;
        if(_owner.size() > 0) {
            this.owners = _owner;
        }
    }
    public BankAccount(String _name, List<String> _owner, double _capital) {
        owners = new ArrayList<>();

        this.name = _name;
        if(_owner.size() > 0) {
            this.owners = _owner;
        }
        this.capital = _capital;
    }


    public void deposit(double _amount)  {
        this.capital += _amount;
    }
    public void withdraw(double _amount)  {
        this.capital -= _amount;
    }

    public boolean transfer(BankAccount _target, double _amount) {
        if(Bank.instance.accounts.containsValue(_target)) {
            this.withdraw(_amount);
            _target.deposit(_amount);
            return true;
        }
        return false;
    }

    public double getCapital() {
        return this.capital;
    }
    public List getOwners() {
        return owners;
    }
}
