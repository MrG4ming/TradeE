package de.mrg4ming.control;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

}
