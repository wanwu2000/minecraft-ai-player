package com.aiplayer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.*;

public class ConfigManager {
    private static final String CONFIG_FILENAME = "aiplayer.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private JsonObject config;

    public ConfigManager() {
        configFile = new File("config/aiplayer.json");
        load();
    }

    public void load() {
        if (!configFile.exists()) { createDefault(); }
        else {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                AiPlayerMod.LOGGER.error("Failed to load config", e);
                createDefault();
            }
        }
    }

    private void createDefault() {
        config = new JsonObject();
        config.addProperty("enableCometIntegration", true);
        config.addProperty("enableChat", true);
        config.addProperty("humanizationEnabled", true);
        config.addProperty("reactionDelayMin", 150);
        config.addProperty("reactionDelayMax", 300);
        config.addProperty("mistakeChance", 0.03);
        config.addProperty("maxCPS", 10);
        save();
    }

    public void save() {
        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            AiPlayerMod.LOGGER.error("Failed to save config", e);
        }
    }

    public boolean enableCometIntegration() { return config != null && config.getAsBoolean("enableCometIntegration"); }
    public boolean enableChat() { return config != null && config.getAsBoolean("enableChat"); }
    public boolean isHumanizationEnabled() { return config != null && config.getAsBoolean("humanizationEnabled"); }
    public int getReactionDelayMin() { return config != null ? config.get("reactionDelayMin").getAsInt() : 150; }
    public int getReactionDelayMax() { return config != null ? config.get("reactionDelayMax").getAsInt() : 300; }
    public double getMistakeChance() { return config != null ? config.get("mistakeChance").getAsDouble() : 0.03; }
    public int getMaxCPS() { return config != null ? config.get("maxCPS").getAsInt() : 10; }
}
