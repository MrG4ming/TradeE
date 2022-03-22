package de.mrg4ming.config;

/**
 * For data that you would like to store in the config of this class.
 */
public interface ConfigData {

    /**
     * Loads the config of this class into the variables of this class.
     */
    void loadFromConfig();

    /**
     * Saves the variables of this class into the config of this class.
     */
    void saveToConfig();
}
