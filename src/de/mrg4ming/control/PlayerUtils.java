package de.mrg4ming.control;

import com.sun.jna.platform.win32.Winevt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlayerUtils {

    public static void givePlayerItems(Player p, ItemStack item, int amount) {
        List<ItemStack> _items = new ArrayList<>();

        int a = amount / item.getMaxStackSize();
        int b = amount % item.getMaxStackSize();

        for(int i = 0; i < a; i++) {
            ItemStack _item = new ItemStack(item.getType(), item.getMaxStackSize());
            _item.addEnchantments(item.getEnchantments());
            _items.add(_item);
        }
        if(b > 0) {
            ItemStack _item = new ItemStack(item.getType(), b);
            _item.addEnchantments(item.getEnchantments());
            _items.add(_item);
        }


        for(ItemStack i : _items) {
            HashMap<Integer, ItemStack> _droppedItems = p.getInventory().addItem(i);
            if(_droppedItems.size() > 0) {
                for(ItemStack d : _droppedItems.values()) {
                    p.getWorld().dropItem(p.getLocation(), d);
                }
            }
        }
    }
    public static void givePlayerItems(Player p, ItemStack item, int amount, Map<Enchantment, Integer> enchantments) {
        List<ItemStack> _items = new ArrayList<>();

        int a = amount / item.getMaxStackSize();
        int b = amount % item.getMaxStackSize();

        for(int i = 0; i < a; i++) {
            ItemStack _item = new ItemStack(item.getType(), item.getMaxStackSize());
            EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) _item.getItemMeta();
            for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                _enchMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
            _item.setItemMeta(_enchMeta);
            _items.add(_item);
        }
        if(b > 0) {
            ItemStack _item = new ItemStack(item.getType(), b);
            EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) _item.getItemMeta();
            for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                _enchMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
            _item.setItemMeta(_enchMeta);
            _items.add(_item);
        }


        for(ItemStack i : _items) {
            HashMap<Integer, ItemStack> _droppedItems = p.getInventory().addItem(i);
            if(_droppedItems.size() > 0) {
                for(ItemStack d : _droppedItems.values()) {
                    p.getWorld().dropItem(p.getLocation(), d);
                }
            }
        }
    }

    public static boolean itemContainsEnchantments(ItemStack item, Map<Enchantment, Integer> enchantments) {
        boolean _value = true;

        EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) item.getItemMeta();

        for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if(!_enchMeta.getStoredEnchants().entrySet().contains(entry)) {
                _value = false;
            }
        }

        return _value;
    }

}
