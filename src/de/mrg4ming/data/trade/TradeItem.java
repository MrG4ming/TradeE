package de.mrg4ming.data.trade;

import de.mrg4ming.config.Config;
import de.mrg4ming.config.ConfigItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeItem implements ConfigItem {

    private static final String CFG_SPLITTER = "-";

    private ItemStack itemStack;

    private Material material;
    private int amount;
    private Map<Enchantment, Integer> enchantments;

    public TradeItem() {
        this.material = Material.AIR;
        this.amount = 0;
        this.enchantments = new HashMap<>();
    }

    public TradeItem(Material material, int amount, Map<Enchantment, Integer> enchantments) {
        this.setMaterial(material);
        this.setAmount(amount);
        this.setEnchantments(enchantments);
    }

    @Override
    public void saveTo(Config cfg, String path) {
        try {
            cfg.set(path + ".material", this.material.name());
            cfg.set(path + ".amount", this.amount);

            List<String> _enchants = new ArrayList<>();
            for(Enchantment e : this.enchantments.keySet()) {
                _enchants.add(e.getKey().toString() + CFG_SPLITTER + this.enchantments.get(e).intValue());
            }

            cfg.set(path + ".enchantments", _enchants);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadFrom(Config cfg, String path) {

        this.material = Material.valueOf((String) cfg.get(path + ".material"));
        this.amount = (Integer) cfg.get(path + ".amount");

        List<String> _enchantsRaw = cfg.getList(path + ".enchantments");
        Map<Enchantment, Integer> _enchants = new HashMap<>();
        //System.out.println(enchantments);
        if(_enchantsRaw != null || _enchantsRaw.size() > 0) {
            for(String s : _enchantsRaw) {
                //System.out.println("String: " + s);
                Enchantment e = Enchantment.getByKey(NamespacedKey.fromString(s.split(CFG_SPLITTER)[0]));
                int level = Integer.parseInt(s.split(CFG_SPLITTER)[1]);
                //System.out.println("Enchantment: " + e.getKey().toString() + " Level: " + level);
                _enchants.put(e, level);
            }
        }  else {
            System.out.println("Enchantments empty");
        }

        this.setEnchantments(_enchants);
    }

    /**
     * Checks if this TradeItem is equal to the given object.
     *
     * Note: it does NOT compare the amount of both Objects!
     * @param obj the object to compare this TradeItem with
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final TradeItem other = (TradeItem) obj;
        if(!this.material.equals(other.getMaterial())) return false;

        //if(this.amount != other.getAmount()) return false;

        if(!this.enchantments.equals(other.getEnchantments())) return false;

        if(this.itemStack == null) this.generateItemStack();
        if(!this.itemStack.equals(other.getItemStack())) return false;


        return true;
    }



    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.itemStack = new ItemStack(material);
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.itemStack = new ItemStack(this.material);

        Map<Enchantment, Integer> _enchants = new HashMap<>();

        if(this.itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) this.itemStack.getItemMeta();
            for(Enchantment e : enchantments.keySet()) {
                if (e.canEnchantItem(this.itemStack) || this.getMaterial().equals(Material.ENCHANTED_BOOK)) {
                    //System.out.println("Enchantment: "  + e.getKey().getNamespace());
                    _enchants.put(e, enchantments.get(e));
                    _enchMeta.addStoredEnchant(e, enchantments.get(e), true);
                }
            }
            this.itemStack.setItemMeta(_enchMeta);
        } else {
            for(Enchantment e : enchantments.keySet()) {
                if (e.canEnchantItem(this.itemStack)) {
                    if(enchantments.containsKey(e)) {
                        _enchants.put(e, enchantments.get(e));
                    }
                }
            }

            this.itemStack.addEnchantments(_enchants);
        }

        this.enchantments = _enchants;
    }


    public ItemStack getItemStack() {
        if(itemStack == null) generateItemStack();
        return itemStack;
    }

    /**
     * This Method is only there to set the default trade editor item.
     * So please do NOT use this method if you! (except you know what you're doing)
     *
     * @param  item  the new ItemStack
     */
    public void setItemStack(ItemStack item) {
        this.itemStack = item;
    }

    private void generateItemStack() {
        this.setEnchantments(this.enchantments);
    }

    /**
     * Converts a normal ItemStack to an TradeItem.
     *
     * Note: It sets the TradeItem product amount to the amount of the ItemStack
     * @param item the item to convert
     * @return
     */
    public static TradeItem itemStackToTradeItem(ItemStack item) {
        TradeItem _result = new TradeItem();
        _result.setMaterial(item.getType());
        _result.setAmount(item.getAmount());
        if(item.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) item.getItemMeta();
            _result.setEnchantments(_enchMeta.getStoredEnchants());
        } else {
            _result.setEnchantments(item.getEnchantments());
        }

        return _result;
    }
    /**
     * Converts a normal ItemStack to an TradeItem.
     *
     * Note: It sets the TradeItem product amount to the amount of the ItemStack
     * @param item the item to convert
     * @param amount sets the amount of the item
     * @return
     */
    public static TradeItem itemStackToTradeItem(ItemStack item, int amount) {
        TradeItem _result = new TradeItem();
        _result.setMaterial(item.getType());
        _result.setAmount(amount);
        if(item.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta _enchMeta = (EnchantmentStorageMeta) item.getItemMeta();
            _result.setEnchantments(_enchMeta.getStoredEnchants());
        } else {
            _result.setEnchantments(item.getEnchantments());
        }

        return _result;
    }

}
