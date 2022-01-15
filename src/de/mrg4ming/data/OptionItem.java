package de.mrg4ming.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
}
