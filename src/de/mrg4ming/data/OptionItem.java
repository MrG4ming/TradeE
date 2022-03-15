package de.mrg4ming.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * An easy way to configure an {@link org.bukkit.inventory.ItemStack}.
 */
public class OptionItem extends ItemStack {

    /**
     * Creates a new item.
     * @param _name the displayName of the item
     * @param _material the material
     */
    public OptionItem(String _name, Material _material) {
        super(_material, 1);
        ItemMeta _meta = this.getItemMeta();
        _meta.setDisplayName(_name);
        this.setItemMeta(_meta);
    }

    /**
     * Creates a new item.
     * @param _name the displayName of the item
     * @param _lore the lore of the item
     * @param _material the material
     */
    public OptionItem(String _name, List<String> _lore, Material _material) {
        super(_material, 1);
        ItemMeta _meta = this.getItemMeta();
        _meta.setDisplayName(_name);
        _meta.setLore(_lore);
        this.setItemMeta(_meta);
    }

    /**
     * Creates a new item.
     * @param _name the displayName of the item
     * @param _material the material
     * @param _amount the amount of the item
     **/
    public OptionItem(String _name, Material _material, int _amount) {
        super(_material, _amount);
        ItemMeta _meta = this.getItemMeta();
        _meta.setDisplayName(_name);
        this.setItemMeta(_meta);
    }

    /**
     * Sets the lore of the item.
     * @param _lore the new lore; every entry stands for a line.
     * @return false if the length of the given lore is smaller than 1
     */
    public boolean setLore(List<String> _lore) {
        if(_lore.size() > 0) {
            ItemMeta _meta = this.getItemMeta();
            _meta.setLore(_lore);
            this.setItemMeta(_meta);
            return true;
        }
        return false;
    }

    /**
     * To set just one line of the lore.
     * @param _loreItem the lore line
     * @return false if the length of the given lore is smaller than 1
     */
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
