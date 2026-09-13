package com.aiplayer.ai;

import com.aiplayer.chat.ChatSystem;
import com.aiplayer.comet.CometBridge;
import com.aiplayer.human.Humanizer;
import com.aiplayer.impl.ApiFactory;
import com.aiplayer.learning.LearningSystem;
import net.minecraft.server.command.ServerCommandSource;

/**
 * AI 控制器 - 整合所有子系统
 */
public class AiController {
    private enum State { IDLE, MOVING, MINING, BUILDING, FIGHTING, CHATTING, NAVIGATING }
    private State currentState = State.IDLE;
    private final LearningSystem learningSystem;
    private final ChatSystem chatSystem;
    private final CometBridge cometBridge;
    private final Humanizer humanizer;
    private boolean isRunning = false;
    private BehaviorTree currentTree;

    public AiController() {
        this.learningSystem = new LearningSystem();
        this.chatSystem = new ChatSystem();
        this.cometBridge = new CometBridge();
        this.humanizer = new Humanizer();
        this.currentTree = buildDefaultTree();
    }

    /**
     * 获取 API 实现（版本自适应）
     */
    public com.aiplayer.api.MovementApi getMovementApi() {
        return ApiFactory.getMovementApi();
    }

    public com.aiplayer.api.PerceptionApi getPerceptionApi() {
        return ApiFactory.getPerceptionApi();
    }

    public com.aiplayer.api.ActionApi getActionApi() {
        return ApiFactory.getActionApi();
    }

    public com.aiplayer.api.ChatApi getChatApi() {
        return ApiFactory.getChatApi();
    }

    /**
     * 构建默认行为树
     */
    private BehaviorTree buildDefaultTree() {
        // 主要行为选择器
        SelectorNode mainSelector = new SelectorNode("MainBehavior", List.of(
            // 条件：需要挖矿？
            new ConditionNode("HasMiningTask", () -> false),
            new TaskNode("MineTask", () -> {
                executeMiningTask();
                return true;
            }),

            // 条件：需要建造？
            new ConditionNode("HasBuildingTask", () -> false),
            new TaskNode("BuildTask", () -> {
                executeBuildingTask();
                return true;
            }),

            // 默认：idle
            new TaskNode("Idle", () -> {
                handleIdle();
                return true;
            })
        ));

        return new BehaviorTree(mainSelector);
    }

    /**
     * 处理命令
     */
    public void processCommand(String command, ServerCommandSource source) {
        String lowerCmd = command.toLowerCase().trim();

        if (lowerCmd.equals("start")) {
            start();
            sendFeedback(source, "✅ AI 启动完成！情绪: " + humanizer.getMoodDescription());
        } else if (lowerCmd.equals("stop")) {
            stop();
            sendFeedback(source, "⏹️ AI 已停止");
        } else if (lowerCmd.equals("status")) {
            showStatus(source);
        } else if (lowerCmd.startsWith("chat ")) {
            handleAIChat(lowerCmd.substring(5), source);
        } else if (lowerCmd.equals("learn")) {
            executeLearn(source);
        } else if (lowerCmd.startsWith("task ")) {
            executeTask(lowerCmd.substring(5), source);
        } else if (lowerCmd.startsWith("analyze ")) {
            analyzeSentence(lowerCmd.substring(8), source);
        } else {
            // 尝试作为自然语言任务执行
            executeNaturalLanguageTask(lowerCmd, source);
        }
    }

    /**
     * 自然语言任务执行
     */
    private void executeNaturalLanguageTask(String sentence, ServerCommandSource source) {
        chatSystem.setCurrentTask(sentence);
        sendFeedback(source, "🔍 正在分析: \"" + sentence + "\"");

        // 简单的关键词匹配
        if (sentence.contains("矿") || sentence.contains("挖")) {
            sendFeedback(source, "🎯 检测到挖矿任务，开始执行...");
            executeMiningTask();
        } else if (sentence.contains("建") || sentence.contains("搭建")) {
            sendFeedback(source, "🏗️ 检测到建造任务，开始执行...");
            executeBuildingTask();
        } else if (sentence.contains("高速") || sentence.contains("公路")) {
            sendFeedback(source, "🛤️ 检测到高速公路任务，开始执行...");
            executeHighwayTask();
        } else if (sentence.contains("保护") || sentence.contains("守护")) {
            sendFeedback(source, "⚔️ 进入保护模式...");
            executeProtectTask();
        } else if (sentence.contains("家") || sentence.contains("回家")) {
            sendFeedback(source, "🏠 开始回家...");
            executeHomeTask();
        } else if (sentence.contains("学习")) {
            executeLearn(source);
        } else {
            // 作为聊天处理
            String response = chatSystem.generateResponse(sentence, "");
            if (response != null) {
                sendFeedback(source, "[AI] " + response);
            }
        }

        chatSystem.clearCurrentTask();
    }

    /**
     * 分析句子结构
     */
    private void analyzeSentence(String sentence, ServerCommandSource source) {
        sendFeedback(source, "📝 句子分析: \"" + sentence + "\"");

        // 提取关键词
        java.util.Set<String> keywords = new java.util.LinkedHashSet<>();
        String[] words = sentence.split("[\\s,，]+");
        for (String word : words) {
            if (word.length() > 1) {
                keywords.add(word);
            }
        }

        sendFeedback(source, "🔑 关键词: " + keywords);
    }

    /**
     * 执行挖矿任务
     */
    private void executeMiningTask() {
        currentState = State.MINING;
        sendDebug("开始挖矿任务");
        learningSystem.recordSuccess("mine");
        chatSystem.updateMood(5);
        humanizer.addFatigue(10);
        currentState = State.IDLE;
    }

    /**
     * 执行建造任务
     */
    private void executeBuildingTask() {
        currentState = State.BUILDING;
        sendDebug("开始建造任务");

        // 如果彗星可用，启用 Scaffold
        if (cometBridge.isAvailable()) {
            cometBridge.enableModule("scaffold");
            sendDebug("彗星 Scaffold 模块已启用");
        }

        learningSystem.recordSuccess("build");
        chatSystem.updateMood(10);
        humanizer.addFatigue(15);
        currentState = State.IDLE;
    }

    /**
     * 执行高速公路任务
     */
    private void executeHighwayTask() {
        currentState = State.BUILDING;
        sendDebug("开始高速公路建造任务");

        if (cometBridge.isAvailable()) {
            cometBridge.enableModule("scaffold");
            cometBridge.enableModule("autobuild");
            sendDebug("彗星自动建筑模块已启用");
        }

        learningSystem.recordSuccess("highway");
        chatSystem.updateMood(15);
        humanizer.addFatigue(20);
        currentState = State.IDLE;
    }

    /**
     * 执行保护任务
     */
    private void executeProtectTask() {
        currentState = State.FIGHTING;
        sendDebug("进入保护模式");
        learningSystem.recordSuccess("protect");
        chatSystem.updateMood(5);
        humanizer.addFatigue(5);
        currentState = State.IDLE;
    }

    /**
     * 执行回家任务
     */
    private void executeHomeTask() {
        currentState = State.NAVIGATING;
        sendDebug("开始回家导航");

        if (cometBridge.isAvailable()) {
            cometBridge.setBaritoneTarget(
                0, 64, 0  // TODO: 获取玩家家的坐标
            );
            sendDebug("Baritone 导航已启动");
        }

        learningSystem.recordSuccess("home");
        chatSystem.updateMood(8);
        humanizer.restFatigue(10);
        currentState = State.IDLE;
    }

    /**
     * 执行学习任务
     */
    private void executeLearn(ServerCommandSource source) {
        learningSystem.incrementGamesPlayed();
        learningSystem.save();
        chatSystem.updateMood(10);
        sendFeedback(source, "🧠 学习完成！游戏次数: " + learningSystem.getTotalGamesPlayed());
    }

    /**
     * 处理 Idle 状态
     */
    private void handleIdle() {
        // 随机行为
        if (RANDOM.nextBoolean()) {
            sendDebug("空闲中...等待指令");
        }
    }

    /**
     * 处理聊天
     */
    private void handleAIChat(String message, ServerCommandSource source) {
        currentState = State.CHATTING;
        String response = chatSystem.generateResponse(message, "");
        if (response != null) {
            sendFeedback(source, "[AI] " + response);
        }
        currentState = State.IDLE;
    }

    /**
     * 启动 AI
     */
    private void start() {
        isRunning = true;
        currentState = State.IDLE;
        chatSystem.updateMood(10);
    }

    /**
     * 停止 AI
     */
    private void stop() {
        isRunning = false;
        currentState = State.IDLE;
        chatSystem.clearCurrentTask();
        cometBridge.stopBaritone();
    }

    /**
     * 显示状态
     */
    private void showStatus(ServerCommandSource source) {
        String status = String.format(
            "📊 AI 状态:\n" +
            "  运行中: %b\n" +
            "  当前状态: %s\n" +
            "  情绪: %s (%d/100)\n" +
            "  疲劳: %d%%\n" +
            "  当前任务: %s\n" +
            "  游戏次数: %d\n" +
            "  完成任务: %d\n" +
            "  彗星集成: %s",
            isRunning,
            currentState,
            chatSystem.getMoodDescription(),
            chatSystem.getMood(),
            humanizer.getFatigue(),
            chatSystem.getCurrentTask() != null ? chatSystem.getCurrentTask() : "无",
            learningSystem.getTotalGamesPlayed(),
            learningSystem.getTotalTasksCompleted(),
            cometBridge.isAvailable() ? "已连接" : "未安装"
        );
        sendFeedback(source, status);
    }

    /**
     * 发送反馈
     */
    private void sendFeedback(ServerCommandSource source, String message) {
        source.sendFeedback(net.minecraft.text.Text.literal(message), false);
    }

    /**
     * 发送调试信息
     */
    private void sendDebug(String message) {
        com.aiplayer.AiPlayerMod.LOGGER.info("[AI] {}", message);
    }

    // Getters
    public State getCurrentState() { return currentState; }
    public ChatSystem getChatSystem() { return chatSystem; }
    public LearningSystem getLearningSystem() { return learningSystem; }
    public CometBridge getCometBridge() { return cometBridge; }
    public Humanizer getHumanizer() { return humanizer; }
    public boolean isRunning() { return isRunning; }

    // Static random for simple decisions
    private static final java.util.Random RANDOM = new java.util.Random();
}
