package me.zimzaza4.geyserdisplayentity.managers;

import me.zimzaza4.geyserdisplayentity.GeyserDisplayEntity;
import me.zimzaza4.geyserdisplayentity.util.FileConfiguration;
import me.zimzaza4.geyserdisplayentity.util.FileUtils;

public class ConfigManager {

    private FileConfiguration config;

    public ConfigManager() {
        load();
    }

    public void load() {
        this.config = new FileConfiguration("config.yml");

        FileUtils.createFiles(GeyserDisplayEntity.getExtension(), "Mappings/example.yml");
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
