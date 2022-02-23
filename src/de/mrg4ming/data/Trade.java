package de.mrg4ming.data;

import com.google.common.collect.Lists;
import de.mrg4ming.control.TradeConfigurator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trade {

    public enum Mode {
        BUY(0),
        SELL(1),
        BUY_AND_SELL(2);

        public final int id;

        private Mode(int id) {
            this.id = id;
        }
    }

    private String name;
    private int value;
    private ItemStack product;
    private int productAmount;
    private Map<Enchantment, Integer> productEnchantments;
    private Mode mode;
    private Inventory tradeOptions;
    public int storage;
    private BankAccount owner;
    private TradeConfigurator configurator;
    private boolean constant;


    public Trade(String name, int value, ItemStack product, int productAmount, Map<Enchantment, Integer> productEnchantments, Mode mode, Inventory tradeOptions, int storage, BankAccount owner, TradeConfigurator configurator, boolean constant) {
        this.name = name;
        this.value = value;
        this.product = product;
        this.productAmount = productAmount;
        this.productEnchantments = productEnchantments;
        this.mode = mode;
        this.tradeOptions = tradeOptions;
        this.storage = storage;
        this.owner = owner;
        this.configurator = configurator;
        this. constant = constant;
    }

    public Trade(String name, int value, ItemStack product, int productAmount, Map<Enchantment, Integer> productEnchantments, Mode mode, int storage, BankAccount owner) {
        this(name, value, product, productAmount, productEnchantments, mode, createTradeOptionsWindow(name, product, productAmount, value, mode, String.valueOf(storage)), storage, owner, new TradeConfigurator(name, product, mode), false);
    }

    public static Trade createConstantTrade(String name, int value, ItemStack product, int productAmount, Map<Enchantment, Integer> productEnchantments, Mode mode) {
        return new Trade(name, value, product, productAmount, productEnchantments, mode, createTradeOptionsWindow(name, product, productAmount, value, mode, "Infinite"), -1, null, new TradeConfigurator(name, product, mode), true);
    }
    private static Inventory createTradeOptionsWindow(String name, ItemStack product, int productAmount, int value, Mode mode, String storage) {
        Inventory inv = Bukkit.createInventory(null, 9, WindowTitle.TRADE_OPTIONS_PREFIX.title + name);

        List<String> _infoLore = new ArrayList<>();
        _infoLore.add("§9Name: §d" + name);
        _infoLore.add("§9Product: §d" + productAmount + "x" + product.getType().toString());
        _infoLore.add("§9Value: §d" + value);

        //insert option item buttons
        inv.setItem(0, new OptionItem("§4Back", Material.BARRIER));
        switch (mode) {
            case BUY -> {
                inv.setItem(3, new OptionItem("§bBuy", Material.PAPER));
                inv.setItem(4, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                inv.setItem(5, new OptionItem("§8Not active", Material.STRUCTURE_VOID));
            }
            case SELL -> {
                inv.setItem(3, new OptionItem("§8Not active", Material.STRUCTURE_VOID));
                inv.setItem(4, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                inv.setItem(5, new OptionItem("§dSell", Material.PAPER));
            }
            case BUY_AND_SELL -> {
                inv.setItem(3, new OptionItem("§bBuy", Material.PAPER));
                inv.setItem(4, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                inv.setItem(5, new OptionItem("§dSell", Material.PAPER));
            }
        }

        OptionItem _storage = new OptionItem("§6Storage:", Lists.newArrayList("§d" + storage), Material.CHEST);
        inv.setItem(8, _storage);

        return inv;
    }

    public void updateTradeOptions() {
        List<String> _infoLore = new ArrayList<>();
        _infoLore.add("§9Name: §d" + name);
        _infoLore.add("§9Product: §d" + productAmount + "x" + product.getType().toString());
        _infoLore.add("§9Value: §d" + value);

        //insert option item buttons
        this.tradeOptions.setItem(0, new OptionItem("§4Back", Material.BARRIER));
        switch (mode) {
            case BUY -> {
                this.tradeOptions.setItem(3, new OptionItem("§bBuy", Material.PAPER));
                this.tradeOptions.setItem(4, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                this.tradeOptions.setItem(5, new OptionItem("§8Not active", Material.STRUCTURE_VOID));
            }
            case SELL -> {
                this.tradeOptions.setItem(3, new OptionItem("§8Not active", Material.STRUCTURE_VOID));
                this.tradeOptions.setItem(4, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                this.tradeOptions.setItem(5, new OptionItem("§dSell", Material.PAPER));
            }
            case BUY_AND_SELL -> {
                this.tradeOptions.setItem(3, new OptionItem("§bBuy", Material.PAPER));
                this.tradeOptions.setItem(4, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                this.tradeOptions.setItem(5, new OptionItem("§dSell", Material.PAPER));
            }
        }

        String _storageValue = "";
        if(this.isConstant()) {
            _storageValue += "Infinite";
        } else {
            _storageValue += String.valueOf(this.storage);
        }
        OptionItem _storage = new OptionItem("§6Storage:", Lists.newArrayList("§d" + _storageValue), Material.CHEST);
        this.tradeOptions.setItem(8, _storage);
    }

    public void setProductAmount(int _amount) {
        if(_amount > this.product.getMaxStackSize()) {
            this.productAmount = this.product.getMaxStackSize();
        } else {
            this.productAmount = _amount;
        }
        //System.out.println("Material: " + this.product.getType().toString());
        this.configurator.updateProduct(this.product.getType(), _amount);
    }


    ///region Getter/Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.configurator.updateValue(value);
    }

    public ItemStack getProduct() {
        return product;
    }

    public void setProduct(ItemStack product) {
        this.product.setType(product.getType());
        this.configurator.updateProduct(product.getType(), this.productAmount);
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductEnchantments(Map<Enchantment, Integer> enchantments) {
        this.productEnchantments = enchantments;
    }

    public Map<Enchantment, Integer> getProductEnchantments() {
        return productEnchantments;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Inventory getTradeOptions() {
        return tradeOptions;
    }

    public void setTradeOptions(Inventory tradeOptions) {
        this.tradeOptions = tradeOptions;
    }

    public BankAccount getOwner() {
        return owner;
    }

    public void setOwner(BankAccount owner) {
        this.owner = owner;
    }

    public TradeConfigurator getConfigurator() {
        return configurator;
    }

    public void setConfigurator(TradeConfigurator configurator) {
        this.configurator = configurator;
    }

    public boolean isConstant() {
        return this.constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }
    ///endregion
}
