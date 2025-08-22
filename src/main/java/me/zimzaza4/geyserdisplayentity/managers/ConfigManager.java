package me.zimzaza4.geyserdisplayentity.managers;

import me.zimzaza4.geyserdisplayentity.util.FileConfiguration;

public class ConfigManager {

    private FileConfiguration config;

    public ConfigManager() {
        load();
    }

    public void load() {
        this.config = new FileConfiguration("config.yml");
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
