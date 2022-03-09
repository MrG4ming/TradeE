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

    /**
     * The string/char that is used to split the config values (the enchantments e.g.)
     */
    private static final String CFG_SPLITTER = "-";

    /**
     * The {@link org.bukkit.inventory.ItemStack} that contains all formatted information of this TradeItem.
     * <br><br>Use this variable to add this TradeItem to an {@link org.bukkit.inventory.Inventory}.
     */
    private ItemStack itemStack;

    /**
     * The material of this TradeItem.
     */
    private Material material;

    /**
     * The amount of this TradeItem. <br>(it is not saved in the {@link #itemStack} variable!)
     */
    private int amount;
    /**
     * The {@link org.bukkit.enchantments.Enchantment}'s of the TradeItem.
     */
    private Map<Enchantment, Integer> enchantments;

    /**
     * Creates a default TradeItem
     * <br>({@link #material} = AIR; {@link #amount} = 0; {@link #enchantments} = new HashMap<>()).
     * <br><br><font color=red>Use this only if you want to load a TradItem from a config!</font>
     */
    public TradeItem() {
        this.material = Material.AIR;
        this.amount = 0;
        this.enchantments = new HashMap<>();
    }

    /**
     * Creates a new TradeItem.
     * @param material the {@link #material}
     * @param amount the {@link #amount}
     * @param enchantments {@link #enchantments}
     */
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

    /**
     * Sets the {@link #material} for the TradeItem.
     * @param material the new {@link org.bukkit.Material} of the TradeItem
     */
    public void setMaterial(Material material) {
        this.itemStack = new ItemStack(material);
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Sets the {@link #amount} for the TradeItem.
     * @param amount the new amount of the TradeItem
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    /**
     * Sets the {@link #enchantments} for the TradeItem and regenerates the {@link #itemStack} of this TradeItem.
     * <br>Enchantments that don't fit onto the item will be ignored!
     * @param enchantments the new {@link org.bukkit.enchantments.Enchantment}'s of the TradeItem
     */
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

    /**
     * If the {@link #itemStack} is null it will be generated.
     * @return the {@link #itemStack} of this TradeItem
     */
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

    /**
     * Generates the {@link #itemStack} of this TradeItem by simply reapplying the enchantments to this TradeItem.
     */
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
