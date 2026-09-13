package com.aiplayer.human;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.config.ConfigManager;

import java.util.Random;

/**
 * 拟人化系统 - 让AI行为更像真实玩家
 */
public class Humanizer {
    private final ConfigManager config;
    private final Random random = new Random();

    // 疲劳值 (0-100)
    private int fatigue = 0;

    // 反应延迟 (毫秒)
    private int reactionDelayMin = 150;
    private int reactionDelayMax = 300;

    // 失误概率
    private double mistakeChance = 0.03;

    // 打字节奏
    private int typingDelay = 50;

    // 情绪影响
    private int moodBonus = 0;

    public Humanizer() {
        this.config = AiPlayerMod.getInstance().getConfigManager();
        loadConfig();
    }

    private void loadConfig() {
        try {
            reactionDelayMin = config.getInt("reactionDelayMin", 150);
            reactionDelayMax = config.getInt("reactionDelayMax", 300);
            mistakeChance = config.getDouble("mistakeChance", 0.03);
            typingDelay = config.getInt("typingDelay", 50);
        } catch (Exception e) {
            AiPlayerMod.LOGGER.warn("Failed to load humanization config", e);
        }
    }

    /**
     * 获取反应延迟（考虑疲劳和情绪）
     */
    public int getReactionDelay() {
        int baseDelay = random.nextInt(reactionDelayMax - reactionDelayMin) + reactionDelayMin;
        // 疲劳增加延迟
        int fatigueBonus = (int) (fatigue * 0.5);
        // 情绪影响
        int moodEffect = moodBonus;
        return baseDelay + fatigueBonus + moodEffect;
    }

    /**
     * 检查是否应该发生失误
     */
    public boolean shouldMistake() {
        // 疲劳增加失误概率
        double currentChance = mistakeChance + (fatigue * 0.001);
        return random.nextDouble() < currentChance;
    }

    /**
     * 获取鼠标移动轨迹（模拟人类抖动）
     */
    public float getMouseJitter() {
        return (random.nextFloat() - 0.5f) * 2.0f;
    }

    /**
     * 获取打字延迟
     */
    public int getTypingDelay() {
        return typingDelay + random.nextInt(30);
    }

    /**
     * 增加疲劳
     */
    public void addFatigue(int amount) {
        fatigue = Math.min(100, fatigue + amount);
    }

    /**
     * 减少疲劳（休息）
     */
    public void restFatigue(int amount) {
        fatigue = Math.max(0, fatigue - amount);
    }

    /**
     * 获取当前疲劳值
     */
    public int getFatigue() {
        return fatigue;
    }

    /**
     * 设置情绪加成
     */
    public void setMoodBonus(int bonus) {
        this.moodBonus = bonus;
    }

    /**
     * 获取当前情绪加成
     */
    public int getMoodBonus() {
        return moodBonus;
    }

    /**
     * 获取习惯模式（晨型/夜猫子）
     */
    public HabitPattern getHabitPattern() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return HabitPattern.MORNING;
        } else if (hour >= 12 && hour < 18) {
            return HabitPattern.AFTERNOON;
        } else {
            return HabitPattern.EVENING;
        }
    }

    /**
     * 习惯模式枚举
     */
    public enum HabitPattern {
        MORNING("晨型", 0.9),
        AFTERNOON("午后", 1.0),
        EVENING("夜猫子", 1.1);

        private final String name;
        private final double speedModifier;

        HabitPattern(String name, double speedModifier) {
            this.name = name;
            this.speedModifier = speedModifier;
        }

        public String getName() { return name; }
        public double getSpeedModifier() { return speedModifier; }
    }
}
