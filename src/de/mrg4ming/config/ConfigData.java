package de.mrg4ming.config;

public interface ConfigData {

    /**
     * Saves the data of this class in the specified config under the specified path.
     * @param cfg the config
     * @param path the path
     */
    void saveTo(Config cfg, String path);

    /**
     * Loads data from the specified config under the specified path to this class.
     * @param cfg the config
     * @param path the path
     */
    void loadFrom(Config cfg, String path);

}
