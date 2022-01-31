package de.mrg4ming.control;

import de.mrg4ming.data.OptionItem;
import de.mrg4ming.data.WindowTitle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class TradeConfigurator {
    public enum Value {
        NOTHING,
        PRICE,
        PRODUCT
    }

    private Inventory inv;

    public TradeConfigurator(String name) {
        this.inv = Bukkit.createInventory(null, 9*2, WindowTitle.TRADE_EDITOR_PREFIX.title + name);

        setCurrentSelectedValue(Value.NOTHING);
        updateValue(0);
        updateProduct(Material.DIRT, 1);

        OptionItem remove10Value = new OptionItem("-10", Material.FEATHER);
        OptionItem remove1Value = new OptionItem("-1", Material.FEATHER);
        OptionItem resetValue = new OptionItem("0", Material.ANVIL);
        OptionItem add1Value = new OptionItem("+1", Material.FEATHER);
        OptionItem add10Value = new OptionItem("+10", Material.FEATHER);

        inv.setItem(8+3, remove10Value);
        inv.setItem(8+4, remove1Value);
        inv.setItem(8+5, resetValue);
        inv.setItem(8+6, add1Value);
        inv.setItem(8+7, add10Value);

        OptionItem confirmItem = new OptionItem("§aConfirm", Material.LIME_DYE);
        inv.setItem(8+9, confirmItem);
    }

    public Inventory open() {
        return inv;
    }

    public void setCurrentSelectedValue(Value _value) {
        List<String> lore = new ArrayList<>();
        switch (_value) {
            case NOTHING -> {
                lore.add("§6Nothing");
            }
            case PRICE -> {
                lore.add("§6Price");
            }
            case PRODUCT -> {
                lore.add("§6Product");
            }
        }

        OptionItem selectedValue = new OptionItem("§dCurrent selected:", lore, Material.PAPER);
        inv.setItem(4, selectedValue);
    }

    public void updateValue(int _value) {
        OptionItem _selectValue = new OptionItem("§aSelect §dValue", Material.PAPER);
        List<String> _selectedValueLore = new ArrayList<>();
        _selectedValueLore.add("§6" + 0);
        _selectValue.setLore(_selectedValueLore);
        inv.setItem(3, _selectValue);
    }

    public OptionItem updateProduct(Material _material, int _amount) {
        OptionItem _product = new OptionItem("§dProduct: ", _material, _amount);
        _product.setLore("§6" + _amount + " §bx §6" + _material.toString());
        this.inv.setItem(5, _product);
        return _product;
    }
}
