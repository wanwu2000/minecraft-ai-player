package com.aiplayer;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiPlayerMod implements ModInitializer {
    public static final String MOD_ID = "aiplayer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static AiPlayerMod instance;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("Minecraft AI Player mod initialized!");
    }

    public static AiPlayerMod getInstance() { return instance; }
}
