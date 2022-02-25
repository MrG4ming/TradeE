package de.mrg4ming.config;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {

    private File file;
    private YamlConfiguration cfg;

    private String name;

    public Config(String _name) {
        this.name = _name;
        load(_name);
    }

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

    public void reload() {
        load(this.name);
    }

    public boolean contains(String _path) {
        return cfg.contains(_path);
    }

    public void set(String _path, Object _value) throws IOException {
        cfg.set(_path, _value);
        cfg.save(file);
    }

    public void setEnchants(String path, Map<Enchantment, Integer> enchants) {
        List<String> enchantments = new ArrayList<>();
        for(Enchantment e : enchants.keySet()) {
            enchantments.add(e.getKey().toString() + "." + enchants.get(e).intValue());
        }
        cfg.set(path, enchantments);
    }

    public Object get(String _path) {
        if(!contains(_path)) return null;
        return cfg.get(_path);
    }

    public List getList(String _path) {
        if(!contains(_path)) return null;
        return cfg.getList(_path);
    }

    public ItemStack getItemStack(String _path) {
        if(!contains(_path)) return null;
        return new ItemStack(cfg.getItemStack(_path));
    }

    public Map<Enchantment, Integer> getEnchants(String path) {
        List<String> enchantments = this.getList(path);
        //System.out.println(enchantments);
        if(enchantments != null) {
            Map<Enchantment, Integer> enchants = new HashMap<>();
            for(String s : enchantments) {
                //System.out.println("String: " + s);
                Enchantment e = Enchantment.getByKey(NamespacedKey.fromString(s.split("\\.")[0]));
                int level = Integer.parseInt(s.split("\\.")[1]);
                //System.out.println("Enchantment: " + e.getKey().toString() + " Level: " + level);
                enchants.put(e, level);
            }

            return enchants;
        }  else {
            System.out.println("Enchantments empty");
        }

        return new HashMap<>();
    }

    public String getName() {
        return name;
    }
}
