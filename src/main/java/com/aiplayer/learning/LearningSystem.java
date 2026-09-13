package com.aiplayer.learning;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LearningSystem {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_FILE = "config/aiplayer/learning_data.json";
    private final Map<String, Integer> successCounts = new HashMap<>();
    private final Map<String, Integer> failureCounts = new HashMap<>();
    private final List<String> learnedBehaviors = new ArrayList<>();
    private int totalGamesPlayed = 0;
    private int totalTasksCompleted = 0;

    public LearningSystem() { load(); }

    public void recordSuccess(String taskType) {
        successCounts.merge(taskType, 1, Integer::sum);
        totalTasksCompleted++;
        learnedBehaviors.add(taskType);
        if (learnedBehaviors.size() > 50) learnedBehaviors.remove(0);
    }

    public void recordFailure(String taskType) { failureCounts.merge(taskType, 1, Integer::sum); }
    public double getSuccessRate(String taskType) {
        int success = successCounts.getOrDefault(taskType, 0);
        int failure = failureCounts.getOrDefault(taskType, 0);
        int total = success + failure;
        return total == 0 ? 0.5 : (double) success / total;
    }
    public String getRecommendedBehavior(String taskType) {
        double rate = getSuccessRate(taskType);
        if (rate > 0.8) return "repeat";
        if (rate > 0.5) return "adjust";
        return "explore";
    }
    public void incrementGamesPlayed() { totalGamesPlayed++; }

    public void save() {
        try {
            Files.createDirectories(Paths.get(DATA_FILE).getParent());
            Map<String, Object> data = new HashMap<>();
            data.put("successCounts", successCounts);
            data.put("failureCounts", failureCounts);
            data.put("learnedBehaviors", learnedBehaviors);
            data.put("totalGamesPlayed", totalGamesPlayed);
            data.put("totalTasksCompleted", totalTasksCompleted);
            try (Writer writer = new FileWriter(DATA_FILE)) { GSON.toJson(data, writer); }
        } catch (IOException e) { /* Ignore */ }
    }

    public void load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            successCounts.clear(); failureCounts.clear(); learnedBehaviors.clear();
            json.getAsJsonObject("successCounts").entrySet().forEach(e -> successCounts.put(e.getKey(), e.getValue().getAsInt()));
            json.getAsJsonObject("failureCounts").entrySet().forEach(e -> failureCounts.put(e.getKey(), e.getValue().getAsInt()));
            json.getAsJsonArray("learnedBehaviors").forEach(e -> learnedBehaviors.add(e.getAsString()));
            totalGamesPlayed = json.get("totalGamesPlayed").getAsInt();
            totalTasksCompleted = json.get("totalTasksCompleted").getAsInt();
        } catch (IOException e) { /* Start fresh */ }
    }

    public int getTotalGamesPlayed() { return totalGamesPlayed; }
    public int getTotalTasksCompleted() { return totalTasksCompleted; }
    public List<String> getLearnedBehaviors() { return learnedBehaviors; }
}
