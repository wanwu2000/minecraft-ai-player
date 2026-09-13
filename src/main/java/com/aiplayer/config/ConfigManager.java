package com.aiplayer.config;

import com.aiplayer.AiPlayerMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 配置管理器
 */
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private ConfigData data;

    public ConfigManager() {
        Path path = Paths.get("config", "aiplayer", "config.json");
        path.toFile().getParentFile().mkdirs();
        this.configFile = path.toFile();
        load();
    }

    public void load() {
        if (!configFile.exists()) {
            data = new ConfigData();
            save();
            return;
        }
        try (FileReader reader = new FileReader(configFile, StandardCharsets.UTF_8)) {
            data = GSON.fromJson(reader, ConfigData.class);
            if (data == null) data = new ConfigData();
        } catch (IOException e) {
            AiPlayerMod.LOGGER.warn("Failed to load config", e);
            data = new ConfigData();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            AiPlayerMod.LOGGER.warn("Failed to save config", e);
        }
    }

    public boolean enableCometIntegration() { return data.enableCometIntegration; }
    public boolean enableChat() { return data.enableChat; }
    public boolean isHumanizationEnabled() { return data.humanizationEnabled; }
    public int getReactionDelayMin() { return data.reactionDelayMin; }
    public int getReactionDelayMax() { return data.reactionDelayMax; }
    public double getMistakeChance() { return data.mistakeChance; }
    public int getMaxCPS() { return data.maxCPS; }
    public int getInt(String key, int defaultVal) {
        switch (key) {
            case "reactionDelayMin": return data.reactionDelayMin;
            case "reactionDelayMax": return data.reactionDelayMax;
            case "typingDelay": return data.typingDelay;
            case "maxCPS": return data.maxCPS;
            default: return defaultVal;
        }
    }
    public double getDouble(String key, double defaultVal) {
        if ("mistakeChance".equals(key)) return data.mistakeChance;
        return defaultVal;
    }
    public String getVersion() { return "1.0.0"; }

    static class ConfigData {
        boolean enableCometIntegration = true;
        boolean enableChat = true;
        boolean humanizationEnabled = true;
        int reactionDelayMin = 150;
        int reactionDelayMax = 300;
        int typingDelay = 50;
        double mistakeChance = 0.03;
        int maxCPS = 10;
    }
}
