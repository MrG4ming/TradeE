package de.mrg4ming.data.trade;

import com.google.common.collect.Lists;
import de.mrg4ming.control.TradeConfigurator;
import de.mrg4ming.data.BankAccount;
import de.mrg4ming.data.OptionItem;
import de.mrg4ming.data.WindowTitle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Trade {

    /**
     * The modes a {@link de.mrg4ming.data.trade.Trade} is able to have.
     * <br><br>
     * The mode determines what a customer is able to do.
     */
    public enum Mode {
        BUY(0),
        SELL(1),
        BUY_AND_SELL(2);

        public final int id;

        private Mode(int id) {
            this.id = id;
        }
    }

    /**
     * The name of the trade.
     */
    private String name;
    /**
     * The value/price of the trade product.
     */
    private int value;
    /**
     * The trade product.
     */
    private TradeItem product;
    /**
     * The trade mode.
     * <br>It determines what a "customer" is able to  do (sell, buy or both).
     */
    private Mode mode;
    /**
     * An {@link org.bukkit.inventory.Inventory} that contains the {@link de.mrg4ming.data.OptionItem}'s to interact with the trade.
     * <br>(e.g. sell, buy)
     */
    private Inventory tradeOptions;
    /**
     * The trade storage.
     * <br>It determines how many items are currently in stock.
     * <br><br>
     * If the trade is a {@link #constant} trade it is set to -1 (=infinite).
     */
    public int storage;
    /**
     * The bank account that owns this trade.
     * <br>
     * If the trade is {@link #constant} the owner is set to null.
     */
    private BankAccount owner;
    /**
     * It represents the controller of the trade editor.
     * <br>
     * It is used by the trade owner (so he is able to change the values like the {@link #product} item).
     */
    private TradeConfigurator configurator;
    /**
     * This value determines if the trade is a constant trade.
     * <br>
     * If this value is set to <i>true</i> the storage needs to be <i>-1</i> and the owner needs to be set to <i>null</i>!
     */
    private boolean constant;

    /**
     * Creates a new trade with all values (<font color=red>NOT filtered</font>).
     * <br><font color=red>NOT recommended!</font>
     * <br>
     * Use {@link #Trade(String, int, TradeItem, Mode, int, BankAccount)} to create a normal trade instead.
     * <br><br>
     * For {@link #constant} trades use {@link #createConstantTrade(String, int, TradeItem, Mode)}!
     * @param name the name of the trade
     * @param value the value/price of the product
     * @param product the trade product (see: {@link de.mrg4ming.data.trade.TradeItem})
     * @param mode the trade mode (see: {@link de.mrg4ming.data.trade.Trade.Mode})
     * @param tradeOptions the inventory that handles the trade option items
     * @param storage the storage of the trade (see: {@link #storage})
     * @param owner the bank account that owns this trade (see: {@link #owner})
     * @param configurator the trade editor window (see: {@link #configurator})
     * @param constant the type of the trade (see: {@link #constant})
     */
    public Trade(String name, int value, TradeItem product, Mode mode, Inventory tradeOptions, int storage, BankAccount owner, TradeConfigurator configurator, boolean constant) {
        this.name = name;
        this.value = value;
        this.product = product;
        this.mode = mode;
        this.tradeOptions = tradeOptions;
        this.storage = storage;
        this.owner = owner;
        this.configurator = configurator;
        this.constant = constant;
    }

    /**
     * Creates a "normal" trade.
     * <br><br>
     * For {@link #constant} trades use {@link #createConstantTrade(String, int, TradeItem, Mode)}!
     * @param name the name of the trade
     * @param value the value/price of the product
     * @param product the trade product (see: {@link de.mrg4ming.data.trade.TradeItem})
     * @param mode the trade mode (see: {@link de.mrg4ming.data.trade.Trade.Mode})
     * @param storage the storage of the trade (see: {@link #storage})
     * @param owner the bank account that owns this trade (see: {@link #owner})
     */
    public Trade(String name, int value, TradeItem product, Mode mode, int storage, BankAccount owner) {
        this(
                name,
                value,
                product,
                mode,
                createTradeOptionsWindow(name, product, value, mode, String.valueOf(storage)),
                storage,
                owner,
                new TradeConfigurator(name, product.getItemStack(), mode), false
        );
    }

    /**
     * Creates a {@link #constant} trade.
     *
     * @param name the name of the trade
     * @param value the value/price of the product
     * @param product the trade product (see: {@link de.mrg4ming.data.trade.TradeItem})
     * @param mode the trade mode (see: {@link de.mrg4ming.data.trade.Trade.Mode})
     * @return a new constant trade
     */
    public static Trade createConstantTrade(String name, int value, TradeItem product, Mode mode) {
        return new Trade(name, value, product, mode, createTradeOptionsWindow(name, product, value, mode, "Infinite"), -1, null, new TradeConfigurator(name, product.getItemStack(), mode), true);
    }

    /**
     * Creates a {@link #tradeOptions} inventory.
     * @param name the name of the trade (see: {@link #name})
     * @param product the trade product (see: {@link #product})
     * @param value the value/price of the trade product (see: {@link #value})
     * @param mode the trade mode (see: {@link #mode})
     * @param storage the trade storage (see: {@link #storage})
     * @return an {@link org.bukkit.inventory.Inventory} that represents the trade options window
     */
    private static Inventory createTradeOptionsWindow(String name, TradeItem product, int value, Mode mode, String storage) {
        Inventory inv = Bukkit.createInventory(null, 9, WindowTitle.TRADE_OPTIONS_PREFIX.title + name);

        List<String> _infoLore = new ArrayList<>();
        _infoLore.add("§9Name: §d" + name);
        _infoLore.add("§9Product: §d" + product.getAmount() + "x" + product.getMaterial().name());
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

    /**
     * Updates the trade options.
     * <br>Needs to be executed after a value has been changed!
     * <br>(e.g. after the storage is refilled)
     */
    public void updateTradeOptions() {
        List<String> _infoLore = new ArrayList<>();
        _infoLore.add("§9Name: §d" + name);
        _infoLore.add("§9Product: §d" + this.product.getAmount() + "x" + product.getMaterial().name().toLowerCase());
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

    ///region Getter/Setter

    /**
     * @return the name of the trade
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the trade.
     * <br><br>{@link #updateTradeOptions()} needs to be called after the name was set!
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value/price of the trade
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets a new value for the trade.
     * <br><br>{@link #updateTradeOptions()} needs to be called after the value was set!
     * @param value the new name
     */
    public void setValue(int value) {
        this.value = value;
        this.configurator.updateValue(value);
    }

    /**
     * @return the {@link #product} of the trade
     */
    public TradeItem getProduct() {
        return product;
    }

    /**
     * Sets a new product for the trade.
     * <br><br>{@link #updateTradeOptions()} needs to be called after the product was set!
     * @param product the new name
     */
    public void setProduct(TradeItem product) {
        this.product = product;
        this.configurator.updateProduct(product.getMaterial(), product.getAmount());
    }

    /**
     * @return the {@link #mode} of the trade
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Sets a new {@link de.mrg4ming.data.trade.Trade.Mode} for the trade.
     * <br><br>{@link #updateTradeOptions()} and {@link de.mrg4ming.control.TradeConfigurator#updateMode(Mode)} needs to be called after the mode was set!
     * @param mode the new {@link de.mrg4ming.data.trade.Trade.Mode} of the trade
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @return the {@link #tradeOptions} {@link org.bukkit.inventory.Inventory} of the trade
     */
    public Inventory getTradeOptions() {
        return tradeOptions;
    }

    /**
     * Sets a {@link #tradeOptions} inventory for the trade.
     * <br><br>
     * <font color=red>Use </font>{@link #createTradeOptionsWindow(String, TradeItem, int, Mode, String)}<font color=red> to create a new tradeOptions window!</font>
     * @param tradeOptions the new trade options window
     */
    public void setTradeOptions(Inventory tradeOptions) {
        this.tradeOptions = tradeOptions;
    }

    /**
     * @return the {@link #owner} of the trade
     */
    public BankAccount getOwner() {
        return owner;
    }

    /**
     * Sets a new {@link #owner} for the trade.
     * <br><br>{@link #updateTradeOptions()} needs to be called after the product was set!
     * @param owner the new {@link de.mrg4ming.data.BankAccount} that owns the trade
     */
    public void setOwner(BankAccount owner) {
        this.owner = owner;
    }

    /**
     * @return the {@link #configurator} of the trade
     */
    public TradeConfigurator getConfigurator() {
        return configurator;
    }

    /**
     * Sets a new {@link de.mrg4ming.control.TradeConfigurator} for the trade.
     * @param configurator the new {@link de.mrg4ming.control.TradeConfigurator} of the trade
     */
    public void setConfigurator(TradeConfigurator configurator) {
        this.configurator = configurator;
    }

    /**
     * @return if the trade is constant
     */
    public boolean isConstant() {
        return this.constant;
    }

    /**
     * <font color=red>NOT recommended!</font>
     * <br>Sets if the trade is constant or not.
     * @param constant the new state
     */
    public void setConstant(boolean constant) {
        this.constant = constant;
    }
    ///endregion
}
