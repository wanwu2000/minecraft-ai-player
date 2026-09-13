package com.aiplayer.human;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.config.ConfigManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拟人化系统 - 让AI行为更像真实玩家
 *
 * 功能：
 * - 反应延迟模拟（150-300ms）
 * - 鼠标轨迹抖动
 * - 偶发失误注入
 * - 打字节奏模拟
 * - 情绪状态影响
 * - 习惯模式识别
 */
public class Humanizer {
    private static final Random RANDOM = new Random();

    // 配置参数
    private final ConfigManager config;

    // 习惯记录
    private final Map<String, Integer> habitTracker = new ConcurrentHashMap<>();
    private static final int MAX_HABIT_HISTORY = 100;

    // 情绪状态 (0-100)
    private int mood = 50;

    // 疲劳度 (0-100)
    private int fatigue = 0;

    // 上次操作时间
    private long lastActionTime = 0;

    // 鼠标位置历史（用于轨迹模拟）
    private final Deque<Point> mouseHistory = new ArrayDeque<>();
    private static final int MAX_MOUSE_HISTORY = 20;

    // 习惯模式
    private enum HabitType {
        MORNING_ROUTINE, AFTERNOON_SLUMP, NIGHT_OWL, FREQUENT_BREAKS
    }

    private HabitType currentHabit = HabitType.MORNING_ROUTINE;
    private long habitStartTime = System.currentTimeMillis();

    public Humanizer() {
        this.config = AiPlayerMod.getInstance().getConfigManager();
        initializeDefaults();
    }

    /**
     * 初始化默认值
     */
    private void initializeDefaults() {
        // 设置初始习惯（随机）
        currentHabit = HabitType.values()[RANDOM.nextInt(HabitType.values().length)];
    }

    /**
     * 获取反应延迟（毫秒）
     */
    public int getReactionDelay() {
        if (!config.isHumanizationEnabled()) return 0;

        int baseDelay = config.getReactionDelayMin() +
            RANDOM.nextInt(config.getReactionDelayMax() - config.getReactionDelayMin());

        // 疲劳影响
        baseDelay += fatigue / 2;

        // 习惯影响
        if (currentHabit == HabitType.AFTERNOON_SLUMP) {
            baseDelay += 50;
        } else if (currentHabit == HabitType.NIGHT_OWL) {
            baseDelay -= 30;
        }

        return Math.max(50, baseDelay);
    }

    /**
     * 判断是否应该失误
     */
    public boolean shouldMakeMistake() {
        if (!config.isHumanizationEnabled()) return false;

        float baseChance = (float) config.getMistakeChance();

        // 疲劳增加失误概率
        baseChance += fatigue * 0.001f;

        // 情绪低落增加失误
        if (mood < 30) {
            baseChance += 0.02f;
        }

        return RANDOM.nextFloat() < baseChance;
    }

    /**
     * 获取最大每秒点击数
     */
    public int getMaxCPS() {
        return config.getMaxCPS();
    }

    /**
     * 模拟鼠标抖动
     */
    public float getMovementJitter(float input) {
        if (!config.isHumanizationEnabled()) return input;

        float jitter = (RANDOM.nextFloat() * 2 - 1) * 0.05f;

        // 疲劳增加抖动
        jitter *= (1 + fatigue / 100f);

        return Math.max(-1.0f, Math.min(1.0f, input + jitter));
    }

    /**
     * 获取当前疲劳度
     */
    public int getFatigue() {
        return fatigue;
    }

    /**
     * 获取打字延迟（毫秒）
     */
    public int getTypingDelay() {
        if (!config.isHumanizationEnabled()) return 0;

        // 基础打字速度：每人每秒字符数不同
        int baseWPM = 40 + RANDOM.nextInt(30); // 40-70 WPM
        int charsPerMinute = baseWPM * 5; // 平均单词长度5字符
        int delayPerChar = 60000 / charsPerMinute;

        // 疲劳影响
        delayPerChar += fatigue / 5;

        // 习惯影响
        if (currentHabit == HabitType.MORNING_ROUTINE) {
            delayPerChar -= 10;
        }

        return Math.max(30, delayPerChar);
    }

    /**
     * 获取情绪描述
     */
    public String getMoodDescription() {
        if (mood >= 80) return "非常开心";
        if (mood >= 60) return "心情不错";
        if (mood >= 40) return "正常";
        if (mood >= 20) return "有点低落";
        return "状态不佳";
    }

    /**
     * 更新情绪（基于事件）
     */
    public void updateMood(int delta) {
        mood = Math.max(0, Math.min(100, mood + delta));
    }

    /**
     * 增加疲劳度
     */
    public void addFatigue(int amount) {
        fatigue = Math.min(100, fatigue + amount);
    }

    /**
     * 恢复疲劳度
     */
    public void restFatigue(int amount) {
        fatigue = Math.max(0, fatigue - amount);
    }

    /**
     * 记录习惯
     */
    public void recordHabit(String habit, int count) {
        habitTracker.merge(habit, count, Integer::sum);

        // 保持历史记录大小
        if (habitTracker.size() > MAX_HABIT_HISTORY) {
            habitTracker.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .ifPresent(min -> habitTracker.remove(min.getKey()));
        }

        // 更新当前习惯
        updateCurrentHabit();
    }

    /**
     * 获取最常用的习惯
     */
    public String getTopHabit() {
        return habitTracker.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("unknown");
    }

    /**
     * 更新当前习惯模式
     */
    private void updateCurrentHabit() {
        long elapsed = System.currentTimeMillis() - habitStartTime;

        // 根据时间切换习惯
        if (elapsed > 3600000) { // 1小时
            if (isMorning()) {
                currentHabit = HabitType.MORNING_ROUTINE;
            } else if (isAfternoon()) {
                currentHabit = HabitType.AFTERNOON_SLUMP;
            } else {
                currentHabit = HabitType.NIGHT_OWL;
            }
            habitStartTime = System.currentTimeMillis();
        }
    }

    private boolean isMorning() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour >= 6 && hour < 12;
    }

    private boolean isAfternoon() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour >= 12 && hour < 18;
    }

    private boolean isNight() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour >= 18 || hour < 6;
    }

    /**
     * 记录鼠标位置
     */
    public void recordMousePosition(double x, double y) {
        mouseHistory.addLast(new Point(x, y));
        if (mouseHistory.size() > MAX_MOUSE_HISTORY) {
            mouseHistory.removeFirst();
        }
    }

    /**
     * 获取鼠标轨迹平滑值
     */
    public Point getSmoothedMousePosition() {
        if (mouseHistory.isEmpty()) return new Point(0, 0);

        double sumX = 0, sumY = 0;
        for (Point p : mouseHistory) {
            sumX += p.x;
            sumY += p.y;
        }

        return new Point(sumX / mouseHistory.size(), sumY / mouseHistory.size());
    }

    /**
     * 获取最后操作距离
     */
    public double getLastActionDistance() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastActionTime;
        return elapsed / 1000.0; // 秒
    }

    /**
     * 记录操作时间
     */
    public void recordAction() {
        lastActionTime = System.currentTimeMillis();
    }

    /**
     * 获取当前状态摘要
     */
    public String getStatusSummary() {
        return String.format(
            "情绪: %s (%d/100) | 疲劳: %d%% | 习惯: %s",
            getMoodDescription(),
            mood,
            fatigue,
            currentHabit.name().toLowerCase()
        );
    }

    /**
     * 鼠标位置类
     */
    public static class Point {
        public final double x;
        public final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
