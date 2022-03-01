package de.mrg4ming.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {

    private File file;
    private YamlConfiguration cfg;

    private String name;

    /**
     * Create a new Config file to save variables.
     * @param _name the name of the config
     */
    public Config(String _name) {
        this.name = _name;
        load(_name);
    }

    /**
     * Loads the config values from a config.
     * @param _name the name of the config to load in
     */
    public void load(String _name) {
        File dir = new File("./plugins/TradeE/");

        if(!dir.exists()) {
            dir.mkdirs();
        }

        file = new File(dir, Objects.requireNonNull(_name) + ".yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cfg = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Reloads the config values from the config without overwriting the config file.
     */
    public void reload() {
        load(this.name);
    }

    /**
     * Checks if the config file contains the given path/value.
     * @param _path the path where a value should be in the config
     * @return true if the path/value exists
     */
    public boolean contains(String _path) {
        return cfg.contains(_path);
    }

    /**
     * Saves a value/object to the config file.
     * @param _path the path in the config where the value should be saved
     * @param _value the value
     * @throws IOException
     */
    public void set(String _path, Object _value) throws IOException {
        cfg.set(_path, _value);
        cfg.save(file);
    }

    /**
     * @param _path the path where the value is located
     * @return the object from the config (returns null if it does not exist)
     */
    public Object get(String _path) {
        if(!contains(_path)) return null;
        return cfg.get(_path);
    }

    /**
     * To load lists from config.
     * @param _path the path where the list should be located
     * @return a lists loaded from the config (returns null if it does not exist)
     */
    public List getList(String _path) {
        if(!contains(_path)) return null;
        return cfg.getList(_path);
    }

    /**
     * @deprecated <font color="red">As of the default ItemStack for trades is replaced by TradeItem</font>
     * @param _path the path where the ItemStack should be located
     * @return an ItemStack (returns null if it does not exist)
     */
    @Deprecated
    public ItemStack getItemStack(String _path) {
        if(!contains(_path)) return null;
        return new ItemStack(cfg.getItemStack(_path));
    }

    /**
     * @return the name of the config
     */
    public String getName() {
        return name;
    }
}
