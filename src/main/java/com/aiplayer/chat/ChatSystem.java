package com.aiplayer.chat;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.config.ConfigManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatSystem {
    private static final Random RANDOM = new Random();
    private final ConfigManager config;
    private final List<String> recentMessages = new ArrayList<>();
    private static final int MAX_RECENT = 20;

    public ChatSystem() { config = AiPlayerMod.getInstance().getConfigManager(); }

    public String generateResponse(String userInput, String context) {
        if (!config.enableChat()) return null;
        String lowerInput = userInput.toLowerCase();

        if (isGreeting(lowerInput)) return pickRandom(greetResponses());
        if (lowerInput.contains("矿") || lowerInput.contains("挖")) return "好的，我去挖矿！";
        if (lowerInput.contains("建") || lowerInput.contains("搭建")) return "明白，开始建造任务！";
        if (lowerInput.contains("保护") || lowerInput.contains("守护")) return "收到，我会保护你的！";
        if (lowerInput.contains("家") || lowerInput.contains("回家")) return "好的，返回基地！";
        if (lowerInput.contains("高速") || lowerInput.contains("公路")) return "开始建造高速公路...";
        if (lowerInput.contains("在干嘛") || lowerInput.contains("做什么")) return "我正在执行当前任务。需要我停止吗？";
        if (lowerInput.contains("状态") || lowerInput.contains("status")) return "AI 系统运行正常 | 聊天功能已启用";
        if (lowerInput.contains("学习") || lowerInput.contains("learn")) return "好的，我会记录这次经验！";

        return pickRandom(defaultResponses());
    }

    private boolean isGreeting(String input) {
        return input.matches("^(你好|嗨|hello|hi|hey|早上好|晚上好|下午好)")
            || input.startsWith("你好") || input.startsWith("嗨");
    }

    private List<String> greetResponses() {
        return List.of("你好！有什么需要帮忙的吗？", "嗨！我是你的AI助手~", "你好呀！准备开始游戏了吗？", "嘿！今天想玩点什么？");
    }

    private List<String> defaultResponses() {
        return List.of("我明白了，正在处理...", "好的，让我想想...", "收到指令！", "嗯，我会照做的~");
    }

    private String pickRandom(List<String> options) { return options.get(RANDOM.nextInt(options.size())); }

    public void recordMessage(String message, String sender) {
        recentMessages.add(sender + ": " + message);
        if (recentMessages.size() > MAX_RECENT) recentMessages.remove(0);
    }

    public List<String> getRecentMessages() { return new ArrayList<>(recentMessages); }
}
