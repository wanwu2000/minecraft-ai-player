package com.aiplayer.ai;

import com.aiplayer.chat.ChatSystem;
import com.aiplayer.learning.LearningSystem;
import net.minecraft.server.command.ServerCommandSource;

public class AiController {
    private enum State { IDLE, MOVING, MINING, BUILDING, FIGHTING, CHATTING }
    private State currentState = State.IDLE;
    private final LearningSystem learningSystem;
    private final ChatSystem chatSystem;
    private boolean isRunning = false;

    public AiController() {
        this.learningSystem = new LearningSystem();
        this.chatSystem = new ChatSystem();
    }

    public void processCommand(String command, ServerCommandSource source) {
        String lowerCmd = command.toLowerCase();
        if (lowerCmd.equals("start")) { start(); source.sendFeedback(net.minecraft.text.Text.literal("AI 启动完成"), false); }
        else if (lowerCmd.equals("stop")) { stop(); source.sendFeedback(net.minecraft.text.Text.literal("AI 已停止"), false); }
        else if (lowerCmd.contains("矿") || lowerCmd.contains("挖")) { executeTask("mine", source); }
        else if (lowerCmd.contains("建") || lowerCmd.contains("搭建")) { executeTask("build", source); }
        else if (lowerCmd.contains("保护") || lowerCmd.contains("守护")) { executeTask("protect", source); }
        else if (lowerCmd.contains("高速") || lowerCmd.contains("公路")) { executeTask("highway", source); }
        else if (lowerCmd.contains("学习")) { executeTask("learn", source); }
        else {
            String response = chatSystem.generateResponse(command, "");
            if (response != null) source.sendFeedback(net.minecraft.text.Text.literal("[AI] " + response), false);
        }
    }

    private void start() { isRunning = true; currentState = State.IDLE; }
    private void stop() { isRunning = false; currentState = State.IDLE; }

    private void executeTask(String taskType, ServerCommandSource source) {
        currentState = State.MOVING;
        source.sendFeedback(net.minecraft.text.Text.literal("正在执行: " + taskType), false);
        learningSystem.recordSuccess(taskType);
        source.sendFeedback(net.minecraft.text.Text.literal(taskType + " 任务完成！"), false);
        currentState = State.IDLE;
    }

    public String getStatus() {
        return String.format("状态: %s | 游戏次数: %d | 完成任务: %d",
            currentState.name(), learningSystem.getTotalGamesPlayed(), learningSystem.getTotalTasksCompleted());
    }
}
