package me.zimzaza4.geyserdisplayentity.managers;

import me.zimzaza4.geyserdisplayentity.GeyserDisplayEntity;
import me.zimzaza4.geyserdisplayentity.util.FileConfiguration;
import me.zimzaza4.geyserdisplayentity.util.FileUtils;

import java.io.File;
import java.util.HashMap;

public class ConfigManager {

    private FileConfiguration config;

    private HashMap<String, FileConfiguration> configMappingsCache;

    public ConfigManager() {
        load();

        loadConfigMappings();
    }

    public void load() {
        this.config = new FileConfiguration("config.yml");

        FileUtils.createFiles(GeyserDisplayEntity.getExtension(), "Mappings/example.yml");

        loadConfigMappings();
    }

    private void loadConfigMappings() {
        HashMap<String, FileConfiguration> tempConfigMappingsCache = new HashMap<>();

        for (File file : FileUtils.getAllFiles(GeyserDisplayEntity.getExtension().dataFolder().resolve("Mappings").toFile(), ".yml")) {
            FileConfiguration mappingsConfigFile = new FileConfiguration(file.getName());
            FileConfiguration mappingsConfig = mappingsConfigFile.getConfigurationSection("mappings");

            configMappingsCache.put(file.getName().replace(".yml", ""), mappingsConfig);
        }

        configMappingsCache = tempConfigMappingsCache;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public HashMap<String, FileConfiguration> getConfigMappingsCache() {
        return configMappingsCache;
    }
}
