package de.mrg4ming.config;

public interface ConfigData {

    void saveTo(Config cfg, String path);
    void loadFrom(Config cfg, String path);

}
