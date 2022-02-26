package de.mrg4ming.data;

import de.mrg4ming.data.trade.Trade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopInventory {

    public static final int MAX_TRADES_PER_PAGE = 5*9;
    public static final int MAX_PAGES = 6;

    public static HashMap<String, Integer> currentPageOpenedByPlayer = new HashMap<>();

    private List<Trade> trades;
    private int pageCount = 0;

    public List<Inventory> pages;


    public ShopInventory(List<Trade> _trades) {
        trades = _trades;
        pages = new ArrayList<>();

        updatePages();
    }

    public void updatePages() {
        pageCount = (int) Math.floor(trades.size() / MAX_TRADES_PER_PAGE) + 1;
        pages = new ArrayList<>();

        for(int i = 0; i < pageCount; i++) {
            Inventory _page = Bukkit.createInventory(null, 6*9, WindowTitle.PAGE.title + i + 1);

            //create and add page selection items to page
            ItemStack _nextPageItem = new ItemStack(Material.PAPER);
            ItemMeta _nextPageMeta = _nextPageItem.getItemMeta();
            _nextPageMeta.setDisplayName("§b>");
            _nextPageItem.setItemMeta(_nextPageMeta);
            _page.setItem(5*9 + 8, _nextPageItem);

            ItemStack _previousPageItem = new ItemStack(Material.PAPER);
            ItemMeta _previousPageMeta = _nextPageItem.getItemMeta();
            _previousPageMeta.setDisplayName("§b<");
            _previousPageItem.setItemMeta(_previousPageMeta);
            _page.setItem(5*9 + 0, _previousPageItem);

            //create and add trade items to page
            for(int j = 0; j < MAX_TRADES_PER_PAGE; j++) {
                int tradeNumber = i*MAX_TRADES_PER_PAGE + j;
                if(trades.size() > tradeNumber) {
                    //create trade item
                    Trade _t = trades.get(tradeNumber);
                    ItemStack _tradeItem = new ItemStack(Material.PAPER);
                    ItemMeta _tradeMeta = _tradeItem.getItemMeta();
                    _tradeMeta.setDisplayName("§b" + _t.getName());

                    List<String> _lore = new ArrayList<>();
                    _lore.add("§9Value: §d" + _t.getValue());
                    _lore.add("§9Product: §d" + _t.getProduct().getAmount() + "x" + _t.getProduct().getMaterial().name().toLowerCase());
                    _tradeMeta.setLore(_lore);

                    _tradeItem.setItemMeta(_tradeMeta);

                    //add trade item to page
                    _page.setItem(tradeNumber, _tradeItem);
                }
            }

            //add page to ist
            pages.add(_page);
        }

    }


    public List<Trade> getTrades() {
        return trades;
    }
}
