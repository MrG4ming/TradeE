package de.mrg4ming.data;

import de.mrg4ming.control.TradeConfigurator;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record Trade(String name, int value, ItemStack product, Mode mode, Inventory tradeOptions, int storage, BankAccount owner, TradeConfigurator configurator) {

    public enum Mode {
        BUY(0),
        SELL(1),
        BUY_AND_SELL(3);

        public final int id;

        private Mode(int id) {
            this.id = id;
        }
    }

    public Trade(String name, int value, ItemStack product, Mode mode, int storage, BankAccount owner) {
        this(name, value, product, mode, createTradeOptionsWindow(name, product, value, mode, storage), storage, owner, new TradeConfigurator(name));
    }

    private static Inventory createTradeOptionsWindow(String name, ItemStack product, int value, Mode mode, int storage) {
        Inventory inv = Bukkit.createInventory(null, 9, WindowTitle.TRADE_OPTIONS_PREFIX.title + name);

        List<String> _infoLore = new ArrayList<>();
        _infoLore.add("§9Name: §d" + name);
        _infoLore.add("§9Product: §d" + product.getAmount() + "x" + product.getData().getItemType().toString());
        _infoLore.add("§9Value: §d" + value);

        //insert option item buttons
        inv.setItem(0, new OptionItem("§4Back", Material.BARRIER));
        switch (mode) {
            case BUY -> {
                inv.setItem(1, new OptionItem("§bBuy", Material.PAPER));
                inv.setItem(2, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                inv.setItem(3, new OptionItem("§8Not active", Material.STRUCTURE_VOID));
            }
            case SELL -> {
                inv.setItem(1, new OptionItem("§8Not active", Material.STRUCTURE_VOID));
                inv.setItem(2, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                inv.setItem(3, new OptionItem("§dSell", Material.PAPER));
            }
            case BUY_AND_SELL -> {
                inv.setItem(1, new OptionItem("§bBuy", Material.PAPER));
                inv.setItem(2, new OptionItem("§6Info", _infoLore, Material.ANVIL));
                inv.setItem(3, new OptionItem("§dSell", Material.PAPER));
            }
        }

        return inv;
    }

    public Trade copy(int _newValue) {
        return new Trade(name, _newValue, product, mode, tradeOptions, storage, owner, configurator);
    }

    public void setProductAmount(int _amount) {
        this.product.setAmount(_amount);
        this.configurator.updateProduct(this.product.getData().getItemType(), _amount);
    }
}
