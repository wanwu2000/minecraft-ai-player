package com.aiplayer;

import com.aiplayer.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiPlayerMod implements ModInitializer {
    public static final String MOD_ID = "aiplayer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AiPlayerMod instance;
    private ConfigManager configManager;

    @Override
    public void onInitialize() {
        instance = this;
        configManager = new ConfigManager();
        configManager.load();
        LOGGER.info("Minecraft AI Player mod initialized! (Server-side)");
    }

    public static AiPlayerMod getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
}
