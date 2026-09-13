package com.aiplayer.human;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.config.ConfigManager;
import java.util.Random;

public class Humanizer {
    private static final Random RANDOM = new Random();
    private final ConfigManager config;

    public Humanizer() { config = AiPlayerMod.getInstance().getConfigManager(); }

    public int getReactionDelay() {
        if (!config.isHumanizationEnabled()) return 0;
        int min = config.getReactionDelayMin();
        int max = config.getReactionDelayMax();
        return min + RANDOM.nextInt(max - min);
    }

    public boolean shouldMakeMistake() {
        if (!config.isHumanizationEnabled()) return false;
        return RANDOM.nextFloat() < config.getMistakeChance();
    }

    public int getMaxCPS() { return config.getMaxCPS(); }
    public float getMovementJitter(float input) {
        if (!config.isHumanizationEnabled()) return input;
        float jitter = (RANDOM.nextFloat() * 2 - 1) * 0.05f;
        return Math.max(-1.0f, Math.min(1.0f, input + jitter));
    }
    public int getTypingDelay() { return 50 + RANDOM.nextInt(100); }
}
