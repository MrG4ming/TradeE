package de.mrg4ming.data;

import de.mrg4ming.control.Bank;

import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    /**
     * The name of the bank account.
     */
    public String name;

    /**
     * The owner of the bank account / the UUID's of the players who own this account.
     * <br><i>Most of the time the account is owned by just one player!</i>
     */
    private List<String> owners;

    /**
     * The amount of money the account owns.
     */
    private double capital;

    /**
     * Creates a new {@link de.mrg4ming.data.BankAccount}.
     * @param _name the {@link #name}
     * @param _owner the owner uuid
     */
    public BankAccount(String _name, String _owner) {
        owners = new ArrayList<>();

        this.name = _name;
        this.owners.add(_owner);
    }

    /**
     * Creates a new {@link de.mrg4ming.data.BankAccount}.
     * @param _name the {@link #name}
     * @param _owner the owner uuid
     * @param _capital the {@link #capital}
     */
    public BankAccount(String _name, String _owner, double _capital) {
        owners = new ArrayList<>();

        this.name = _name;
        this.owners.add(_owner);
        this.capital = _capital;
    }

    /**
     * Creates a new {@link de.mrg4ming.data.BankAccount}.
     * @param _name the {@link #name}
     * @param _owner the {@link #owners} uuid's
     */
    public BankAccount(String _name, List<String> _owner) {
        owners = new ArrayList<>();

        this.name = _name;
        if(_owner.size() > 0) {
            this.owners = _owner;
        }
    }

    /**
     * Creates a new {@link de.mrg4ming.data.BankAccount}.
     * @param _name the {@link #name}
     * @param _owner the {@link #owners} uuid's
     * @param _capital the {@link #capital}
     */
    public BankAccount(String _name, List<String> _owner, double _capital) {
        owners = new ArrayList<>();

        this.name = _name;
        if(_owner.size() > 0) {
            this.owners = _owner;
        }
        this.capital = _capital;
    }

    /**
     * Deposits/Adds money to the account.
     * @param _amount the amount of money
     */
    public void deposit(double _amount)  {
        this.capital += _amount;
    }

    /**
     * Withdraws/Subtracts money from the account.
     * @param _amount the amount of money
     */
    public void withdraw(double _amount)  {
        this.capital -= _amount;
    }

    /**
     * Transfers money from this account to the target {@link de.mrg4ming.data.BankAccount}.
     * @param _target the target {@link de.mrg4ming.data.BankAccount}
     * @param _amount the amount to transfer
     * @return false if the target account does not exist
     */
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
    public List<String> getOwners() {
        return owners;
    }
}
