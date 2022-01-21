package de.mrg4ming.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OptionItem extends ItemStack {

    public OptionItem(String _name, Material _material) {
        super(_material, 1);
        ItemMeta _meta = this.getItemMeta();
        _meta.setDisplayName(_name);
        this.setItemMeta(_meta);
    }

    public OptionItem(String _name, List<String> _lore, Material _material) {
        super(_material, 1);
        ItemMeta _meta = this.getItemMeta();
        _meta.setDisplayName(_name);
        _meta.setLore(_lore);
        this.setItemMeta(_meta);
    }

    public OptionItem(String _name, Material _material, int _amount) {
        super(_material, _amount);
        ItemMeta _meta = this.getItemMeta();
        _meta.setDisplayName(_name);
        this.setItemMeta(_meta);
    }

    public boolean setLore(List<String> _lore) {
        if(_lore.size() > 0) {
            ItemMeta _meta = this.getItemMeta();
            _meta.setLore(_lore);
            this.setItemMeta(_meta);
            return true;
        }
        return false;
    }
    public boolean setLore(String _loreItem) {
        if(_loreItem.length() > 0) {
            List<String> _lore = new ArrayList<>();
            _lore.add(_loreItem);

            ItemMeta _meta = this.getItemMeta();
            _meta.setLore(_lore);
            this.setItemMeta(_meta);
            return true;
        }
        return false;
    }
}
