package de.mrg4ming.control;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public final class PlayerUtils {

    /**
     * Gives the player a specified amount of the given item. (to add extra enchantments while giving player the items see {@link #givePlayerItems(Player, ItemStack, int, Map)}
     * @param p the player
     * @param item the item
     * @param amount the amount
     */
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
    /**
     * Gives the player a specified amount of the given an item while adding enchantments to the item.
     * @param p the player
     * @param item the item
     * @param amount the amount
     */
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

    /**
     * Checks if the item contains all the given enchantments
     * @param item the item
     * @param enchantments the enchantments
     * @return false if the item does not contain ONE of the given enchantments
     */
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

    /**
     * Checks if the item contains all the given enchantments
     * @param item the item
     * @param enchantments the enchantments
     * @return false if the item can not be enchanted with ONE of the given enchantments
     */
    public static boolean itemCanBeEnchanted(ItemStack item, Map<Enchantment, Integer> enchantments) {
        if(item.getType().equals(Material.ENCHANTED_BOOK)) return true;
        for(Enchantment _ench : enchantments.keySet()) {
            if(!_ench.canEnchantItem(item)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if item1 is similar to item2
     * @param item1 the first item
     * @param item2 the second item
     * @return false if the items don't match
     */
    public static boolean itemsAreSimilarExceptAmount(ItemStack item1, ItemStack item2) {
        boolean _value = true;

        if(!item1.getType().equals(item2.getType())) _value = false;

        if(item1.getEnchantments().isEmpty() ^ item2.getEnchantments().isEmpty()) _value = false;

        if(!item1.getEnchantments().equals(item2.getEnchantments())) _value = false;

        return _value;
    }

    /**
     * Returns the Key of a Map with by the value
     * @param map the map
     * @param value the value
     * @param <T>
     * @param <E>
     * @return null if the map does not contain the given value
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
