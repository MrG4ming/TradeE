package de.mrg4ming.control;

import org.bukkit.Material;
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
            ItemStack _item = new ItemStack(item);
            _item.setAmount(_item.getMaxStackSize());
            _items.add(_item);
        }
        if(b > 0) {
            ItemStack _item = new ItemStack(item);
            _item.setAmount(b);
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

        if(itemCanBeEnchanted(item, enchantments)) {
            for(int i = 0; i < a; i++) {
                ItemStack _item = new ItemStack(item.getType(), item.getMaxStackSize());
                if(_item.getItemMeta() instanceof EnchantmentStorageMeta) {
                    EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) _item.getItemMeta();
                    for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                        _enchMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                    }
                    _item.setItemMeta(_enchMeta);
                } else {
                    _item.addEnchantments(enchantments);
                }
                _items.add(_item);
            }
        } else {
            for(int i = 0; i < a; i++) {
                ItemStack _item = new ItemStack(item.getType(), item.getMaxStackSize());
                _items.add(_item);
            }
            System.out.println("Enchantments cannot be applied to ItemStack.");
        }

        if(b > 0) {
            ItemStack _item = new ItemStack(item.getType(), b);
            if(itemCanBeEnchanted(item, enchantments)) {
                if(_item.getItemMeta() instanceof EnchantmentStorageMeta) {
                    EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) _item.getItemMeta();
                    for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                        _enchMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                    }
                    _item.setItemMeta(_enchMeta);
                } else {
                    _item.addEnchantments(enchantments);
                }
            } else {
                System.out.println("Enchantments cannot be applied to ItemStack.");
            }
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

        if(itemCanBeEnchanted(item, enchantments)) {
            if(item.getItemMeta() instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) item.getItemMeta();

                for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    if(!_enchMeta.getStoredEnchants().entrySet().contains(entry)) {
                        _value = false;
                    }
                }
            } else {
                for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    if(item.getItemMeta().hasEnchant(entry.getKey())) {
                        if(item.getItemMeta().getEnchantLevel(entry.getKey()) != entry.getValue()) {
                            _value = false;
                        }
                    } else _value = false;
                }
            }
        } else _value = false;


        return _value;
    }

    public static boolean itemCanBeEnchanted(ItemStack item, Map<Enchantment, Integer> enchantments) {
        if(item.getType().equals(Material.ENCHANTED_BOOK)) return true;
        for(Enchantment _ench : enchantments.keySet()) {
            if(!_ench.canEnchantItem(item)) {
                return false;
            }
        }

        return true;
    }

    public static boolean itemsAreSimilarExceptAmount(ItemStack item1, ItemStack item2) {
        boolean _value = true;

        if(!item1.getType().equals(item2.getType())) _value = false;

        if(item1.getEnchantments().isEmpty() ^ item2.getEnchantments().isEmpty()) _value = false;

        if(!item1.getEnchantments().equals(item2.getEnchantments())) _value = false;

        return _value;
    }

}
