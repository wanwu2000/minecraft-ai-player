package com.aiplayer.chat;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.config.ConfigManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 增强版对话系统 - 支持情境理解、多轮对话、记忆追踪
 */
public class ChatSystem {
    private static final Random RANDOM = new Random();
    private final ConfigManager config;

    // 对话历史（用于上下文理解）
    private final Deque<String> conversationHistory = new ArrayDeque<>();
    private static final int MAX_HISTORY = 50;

    // 用户偏好记忆
    private final Map<String, Object> memory = new ConcurrentHashMap<>();

    // 当前情绪状态 (0-100)
    private int mood = 50;

    // 最近任务跟踪
    private String currentTask = null;
    private long taskStartTime = 0;

    // 触发关键词 → 响应映射
    private static final Map<String, List<String>> RESPONSE_TEMPLATES = new HashMap<>();

    static {
        // 问候语
        addResponses("greeting", List.of(
            "你好！我是你的AI助手，有什么需要帮忙的吗？",
            "嗨~ 准备开始冒险了吗？",
            "你好呀！今天想玩点什么？"
        ));

        // 挖矿相关
        addResponses("mine", List.of(
            "好的，我去挖矿！记得给我准备镐子~",
            "挖矿模式启动！目标是煤炭还是钻石？",
            "开始采矿作业...注意安全！"
        ));

        // 建造相关
        addResponses("build", List.of(
            "收到建造任务！我会使用彗星的Scaffold模块。",
            "开始建造...需要哪种建筑？房子还是桥梁？",
            "动手了！先收集材料，然后开始建造~"
        ));

        // 高速公路
        addResponses("highway", List.of(
            "好的，建造高速公路！我会铺设主世界高速公路网络。",
            "高速公路计划启动！目标是远程传输还是观光路线？",
            "正在规划高速路线...这会很壮观！"
        ));

        // 保护/战斗
        addResponses("protect", List.of(
            "收到！我会守护你，有任何危险我会优先处理。",
            "警戒模式启动！怪物的位置我来盯~",
            "放心，有我在你身边！"
        ));

        // 回家
        addResponses("home", List.of(
            "好的，返回基地！我会记住路径。",
            "回家模式激活...路上小心！",
            "导航到安全屋，正在规划路线..."
        ));

        // 状态查询
        addResponses("status", List.of(
            "系统运行正常 | 情绪稳定 | 任务完成率高",
            "一切就绪，随时准备执行新指令！",
            "AI状态良好，学习进度正常~"
        ));

        // 学习相关
        addResponses("learn", List.of(
            "好的，我会记住这次经验，下次做得更好！",
            "学习完成！已更新我的行为模型。",
            "经验已记录，感激您的指导~"
        ));

        // 在干嘛
        addResponses("doing", List.of(
            "我正在{currentTask}，需要我停下来吗？",
            "当前正在执行任务，稍等片刻~",
            "专注于手上的工作，需要帮助请叫我！"
        ));

        // 感谢
        addResponses("thanks", List.of(
            "不客气！能帮到你我很开心~",
            "应该的！有需要随时叫我。",
            "为您服务是我的荣幸！"
        ));

        // 再见
        addResponses("bye", List.of(
            "再见！我会在这里等你回来~",
            "下次见！随时呼叫我！",
            "拜拜，记得保存进度哦~"
        ));

        // 默认响应
        addResponses("default", List.of(
            "我明白了，正在处理...",
            "好的，让我想想...",
            "收到指令！我会尽力完成。",
            "嗯，明白了~"
        ));
    }

    private static void addResponses(String key, List<String> responses) {
        RESPONSE_TEMPLATES.put(key, responses);
    }

    public ChatSystem() {
        config = AiPlayerMod.getInstance().getConfigManager();
        loadMemory();
    }

    /**
     * 生成AI回复 - 支持上下文理解
     */
    public String generateResponse(String userInput, String context) {
        if (!config.enableChat()) return null;

        String lowerInput = userInput.toLowerCase().trim();
        conversationHistory.addLast(userInput);
        if (conversationHistory.size() > MAX_HISTORY) {
            conversationHistory.removeFirst();
        }

        // 检查是否在任务中
        if (lowerInput.contains("任务") || lowerInput.contains("做")) {
            return formatResponse("doing");
        }

        // 关键词匹配
        String responseType = detectResponseType(lowerInput);
        String response = formatResponse(responseType);

        // 记录用户提到的关键信息
        recordMemory(userInput);

        // 更新情绪
        updateMood(userInput);

        return response;
    }

    /**
     * 检测响应类型
     */
    private String detectResponseType(String input) {
        if (input.matches("^(你好|嗨|hello|hi|hey|早上好|晚上好|下午好)")) {
            return "greeting";
        }
        if (input.contains("矿") || input.contains("挖") || input.contains("coal") || input.contains("diamond")) {
            return "mine";
        }
        if (input.contains("建") || input.contains("搭建") || input.contains("build")) {
            return "build";
        }
        if (input.contains("高速") || input.contains("公路")) {
            return "highway";
        }
        if (input.contains("保护") || input.contains("守护") || input.contains("fight")) {
            return "protect";
        }
        if (input.contains("家") || input.contains("回家") || input.contains("home")) {
            return "home";
        }
        if (input.contains("状态") || input.contains("status")) {
            return "status";
        }
        if (input.contains("学习") || input.contains("learn")) {
            return "learn";
        }
        if (input.contains("谢谢") || input.contains("thanks")) {
            return "thanks";
        }
        if (input.contains("再见") || input.contains("拜拜") || input.contains("bye")) {
            return "bye";
        }
        if (input.contains("在干嘛") || input.contains("做什么") || input.contains("doing")) {
            return "doing";
        }
        return "default";
    }

    /**
     * 格式化响应（支持占位符替换）
     */
    private String formatResponse(String type) {
        List<String> responses = RESPONSE_TEMPLATES.getOrDefault(type, List.of("..."));
        String response = responses.get(RANDOM.nextInt(responses.size()));

        // 替换占位符
        response = response.replace("{currentTask}", currentTask != null ? currentTask : "等待指令");
        response = response.replace("{mood}", getMoodDescription());

        return response;
    }

    /**
     * 记录用户提到的关键信息
     */
    private void recordMemory(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("喜欢") || lower.contains("爱")) {
            memory.put("preference_positive", true);
        }
        if (lower.contains("不喜欢") || lower.contains("讨厌")) {
            memory.put("preference_negative", true);
        }
        if (lower.contains("服务器") || lower.contains("server")) {
            memory.put("server_type", extractServerType(lower));
        }
    }

    private String extractServerType(String input) {
        if (input.contains("养老") || input.contains("生存")) return "survival";
        if (input.contains("空岛")) return "skyblock";
        if (input.contains("建筑")) return "building";
        if (input.contains("pvp")) return "pvp";
        return "unknown";
    }

    /**
     * 更新情绪状态
     */
    private void updateMood(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("谢谢") || lower.contains("棒") || lower.contains("好")) {
            mood = Math.min(100, mood + 5);
        } else if (lower.contains("笨") || lower.contains("差") || lower.contains("错")) {
            mood = Math.max(0, mood - 5);
        }
    }

    private String getMoodDescription() {
        if (mood >= 80) return "非常愉快";
        if (mood >= 60) return "心情不错";
        if (mood >= 40) return "正常";
        if (mood >= 20) return "有点低落";
        return "状态不佳";
    }

    /**
     * 设置当前任务
     */
    public void setCurrentTask(String task) {
        this.currentTask = task;
        this.taskStartTime = System.currentTimeMillis();
        AiPlayerMod.LOGGER.info("AI started task: {}", task);
    }

    /**
     * 清除当前任务
     */
    public void clearCurrentTask() {
        this.currentTask = null;
    }

    /**
     * 获取对话历史
     */
    public List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    /**
     * 获取情绪状态
     */
    public int getMood() {
        return mood;
    }

    /**
     * 获取记忆
     */
    public Map<String, Object> getMemory() {
        return new HashMap<>(memory);
    }

    /**
     * 保存记忆到文件
     */
    public void saveMemory() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(
                "config", "aiplayer_memory.json"
            );
            Map<String, Object> saveData = new HashMap<>();
            saveData.put("mood", mood);
            saveData.put("memory", memory);
            saveData.put("task", currentTask);

            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            java.nio.file.Files.writeString(path, gson.toJson(saveData));
        } catch (Exception e) {
            AiPlayerMod.LOGGER.error("Failed to save memory", e);
        }
    }

    /**
     * 加载记忆
     */
    private void loadMemory() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("config", "aiplayer_memory.json");
            if (java.nio.file.Files.exists(path)) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                Map<String, Object> data = gson.fromJson(
                    java.nio.file.Files.readString(path), Map.class
                );
                if (data.containsKey("mood")) mood = (int) data.get("mood");
                if (data.containsKey("memory")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> mem = (Map<String, Object>) data.get("memory");
                    memory.putAll(mem);
                }
                if (data.containsKey("task")) currentTask = (String) data.get("task");
            }
        } catch (Exception e) {
            AiPlayerMod.LOGGER.error("Failed to load memory", e);
        }
    }
}
