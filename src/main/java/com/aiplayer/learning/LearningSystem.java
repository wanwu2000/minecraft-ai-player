package com.aiplayer.learning;

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
import java.util.HashMap;
import java.util.Map;

/**
 * 学习系统 - 记录游戏经验和偏好
 */
public class LearningSystem {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    
    private int gamesPlayed = 0;
    private int tasksCompleted = 0;
    private final Map<String, Integer> skillLevels = new HashMap<>();
    private final Map<String, Double> qTable = new HashMap<>();

    public LearningSystem() {
        Path path = Paths.get("config", "aiplayer", "learning_data.json");
        path.toFile().getParentFile().mkdirs();
        this.dataFile = path.toFile();
    }

    public void load() {
        if (!dataFile.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(dataFile, StandardCharsets.UTF_8)) {
            LearningData data = GSON.fromJson(reader, LearningData.class);
            if (data != null) {
                gamesPlayed = data.gamesPlayed;
                tasksCompleted = data.tasksCompleted;
                if (data.skillLevels != null) {
                    skillLevels.putAll(data.skillLevels);
                }
                if (data.qTable != null) {
                    qTable.putAll(data.qTable);
                }
                AiPlayerMod.LOGGER.info("Loaded learning data: {} games, {} tasks", gamesPlayed, tasksCompleted);
            }
        } catch (IOException e) {
            AiPlayerMod.LOGGER.warn("Failed to load learning data", e);
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(dataFile, StandardCharsets.UTF_8)) {
            LearningData data = new LearningData();
            data.gamesPlayed = gamesPlayed;
            data.tasksCompleted = tasksCompleted;
            data.skillLevels = skillLevels;
            data.qTable = qTable;
            GSON.toJson(data, writer);
            AiPlayerMod.LOGGER.info("Saved learning data");
        } catch (IOException e) {
            AiPlayerMod.LOGGER.warn("Failed to save learning data", e);
        }
    }

    public void incrementGamesPlayed() {
        gamesPlayed++;
    }

    public void recordSuccess(String task) {
        tasksCompleted++;
        skillLevels.merge(task, 1, Integer::sum);
        AiPlayerMod.LOGGER.debug("Recorded success for task: {}, total: {}", task, tasksCompleted);
    }

    public void updateQValue(String state, String action, double newValue) {
        qTable.put(state + ":" + action, newValue);
    }

    public double getQValue(String state, String action) {
        return qTable.getOrDefault(state + ":" + action, 0.0);
    }

    public int getTotalGamesPlayed() { return gamesPlayed; }
    public int getTotalTasksCompleted() { return tasksCompleted; }
    public Map<String, Integer> getSkillLevels() { return skillLevels; }
}

class LearningData {
    int gamesPlayed;
    int tasksCompleted;
    Map<String, Integer> skillLevels;
    Map<String, Double> qTable;
}
