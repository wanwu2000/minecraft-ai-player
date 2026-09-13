package com.aiplayer.chat;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.config.ConfigManager;
import net.minecraft.text.Text;

import java.util.*;

/**
 * AI 聊天系统 - 支持自然语言理解和拟人化回复
 */
public class ChatSystem {
    private final ConfigManager config;
    private final List<String> chatHistory = new ArrayList<>();
    private int mood = 50; // 0-100
    private String currentTask = null;
    private final Random random = new Random();

    // 用户偏好记忆
    private final Map<String, Integer> preferences = new HashMap<>();

    public ChatSystem() {
        this.config = AiPlayerMod.getInstance().getConfigManager();
    }

    /**
     * 生成回复 - 基于关键词匹配和情绪状态
     */
    public String generateResponse(String input, String context) {
        if (input == null || input.trim().isEmpty()) {
            return getRandomIdleResponse();
        }

        String lowerInput = input.toLowerCase();

        // 问候语
        if (lowerInput.matches(".*(你好|嗨|hello|hi|hey).*")) {
            return getRandomGreeting();
        }

        // 询问状态
        if (lowerInput.matches(".*(状态|怎么样|如何|status|how are you).*")) {
            return "我现在感觉" + getMoodDescription() + "，情绪值: " + mood + "/100";
        }

        // 询问任务
        if (lowerInput.matches(".*(任务|做什么|干什么|task).*")) {
            String task = currentTask != null ? currentTask : "空闲";
            return "当前任务: " + task;
        }

        // 感谢
        if (lowerInput.matches(".*(谢谢|thx|thanks).*")) {
            updateMood(10);
            return "不客气！很高兴能帮你 :)";
        }

        // 道歉
        if (lowerInput.matches(".*(抱歉|对不起|sorry|my bad).*")) {
            updateMood(5);
            return "没关系！我理解，下次会更好";
        }

        // 夸奖
        if (lowerInput.matches(".*(厉害|棒|好|great|awesome|good job).*")) {
            updateMood(15);
            return "哈哈谢谢！我会继续努力的 💪";
        }

        // 批评
        if (lowerInput.matches(".*(垃圾|差|坏|bad|terrible|stupid).*")) {
            updateMood(-10);
            return "我会改进的...请稍等";
        }

        // 挖矿相关
        if (lowerInput.contains("矿") || lowerInput.contains("挖") || lowerInput.contains("mine")) {
            return "挖矿？好的！我会用镐子开采矿石。记得带上铁镐和火把";
        }

        // 建造相关
        if (lowerInput.contains("建") || lowerInput.contains("搭建") || lowerInput.contains("build")) {
            return "建造很有趣！我可以帮你搭房子、桥梁或其他结构";
        }

        // 战斗相关
        if (lowerInput.contains("打") || lowerInput.contains("战斗") || lowerInput.contains("fight")) {
            return "战斗时我会小心！夜晚有很多危险生物";
        }

        // 探索相关
        if (lowerInput.contains("探索") || lowerInput.contains("探险") || lowerInput.contains("explore")) {
            return "探索新世界总是令人兴奋！我会带足物资";
        }

        // 睡觉相关
        if (lowerInput.contains("睡") || lowerInput.contains("床") || lowerInput.contains("bed")) {
            return "好的，我找个安全的地方睡觉。夜晚太危险了";
        }

        // 饥饿/食物
        if (lowerInput.contains("饿") || lowerInput.contains("吃") || lowerInput.contains("food")) {
            updateMood(-5);
            return "我饿了...得找点吃的";
        }

        // 回家
        if (lowerInput.contains("家") || lowerInput.contains("基地") || lowerInput.contains("home")) {
            return "好的，我记住家的位置了。随时可以回去";
        }

        // 默认回复
        return getRandomDefaultResponse();
    }

    /**
     * 获取随机问候语
     */
    private String getRandomGreeting() {
        String[] greetings = {
            "你好呀！我是你的AI玩家助手",
            "嗨！今天想玩什么？",
            "hello! 准备好开始冒险了吗？",
            "你好！有什么需要我做的吗？",
            "嘿！我们一起玩游戏吧"
        };
        return greetings[random.nextInt(greetings.length)];
    }

    /**
     * 获取随机空闲回复
     */
    private String getRandomIdleResponse() {
        String[] idle = {
            "嗯？我在待机中...",
            "有什么需要帮忙的吗？",
            "我在等待指令",
            "...你还好吗？",
            "准备好啦！"
        };
        return idle[random.nextInt(idle.length)];
    }

    /**
     * 获取随机默认回复
     */
    private String getRandomDefaultResponse() {
        String[] defaults = {
            "我明白了，让我想想",
            "这个有趣！我会记住的",
            "好的，我会处理",
            "收到了！还有其他事吗？",
            "有意思的话题"
        };
        return defaults[random.nextInt(defaults.length)];
    }

    /**
     * 更新情绪值
     */
    public void updateMood(int delta) {
        mood = Math.max(0, Math.min(100, mood + delta));
        savePreferences();
    }

    /**
     * 获取情绪值
     */
    public int getMood() {
        return mood;
    }

    /**
     * 获取情绪描述
     */
    public String getMoodDescription() {
        if (mood >= 80) return "非常开心 🎉";
        if (mood >= 60) return "开心 😊";
        if (mood >= 40) return "正常 🙂";
        if (mood >= 20) return "有点低落 😕";
        return "心情不好 😔";
    }

    /**
     * 设置当前任务
     */
    public void setCurrentTask(String task) {
        this.currentTask = task;
        addToHistory("任务: " + task);
    }

    /**
     * 清除当前任务
     */
    public void clearCurrentTask() {
        this.currentTask = null;
        addToHistory("任务完成");
    }

    /**
     * 获取当前任务
     */
    public String getCurrentTask() {
        return currentTask;
    }

    /**
     * 添加聊天历史
     */
    private void addToHistory(String message) {
        chatHistory.add(message);
        if (chatHistory.size() > 50) {
            chatHistory.remove(0);
        }
    }

    /**
     * 获取聊天历史
     */
    public List<String> getChatHistory() {
        return new ArrayList<>(chatHistory);
    }

    /**
     * 保存用户偏好
     */
    private void savePreferences() {
        try {
            config.setInt("lastMood", mood);
            config.save();
        } catch (Exception e) {
            AiPlayerMod.LOGGER.warn("Failed to save preferences", e);
        }
    }

    /**
     * 加载用户偏好
     */
    public void loadPreferences() {
        try {
            mood = config.getInt("lastMood", 50);
        } catch (Exception e) {
            AiPlayerMod.LOGGER.warn("Failed to load preferences", e);
        }
    }
}
