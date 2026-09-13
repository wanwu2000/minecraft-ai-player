package com.aiplayer.command;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.chat.ChatSystem;
import com.aiplayer.learning.LearningSystem;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class CommandRegistry {
    private final ChatSystem chatSystem;
    private final LearningSystem learningSystem;

    public CommandRegistry() {
        this.chatSystem = new ChatSystem();
        this.learningSystem = new LearningSystem();
    }

    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registrationEnvironment, env) -> {
            registerAISlashCommands(dispatcher);
            registerSystemCommands(dispatcher);
        });
    }

    private void registerAISlashCommands(CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ai")
            .then(CommandManager.literal("start").executes(context -> { startAI(context.getSource()); return 1; }))
            .then(CommandManager.literal("stop").executes(context -> { stopAI(context.getSource()); return 1; }))
            .then(CommandManager.literal("status").executes(context -> { showStatus(context.getSource()); return 1; }))
            .then(CommandManager.literal("chat")
                .then(CommandManager.argument("message", CommandManager.string()).executes(context -> {
                    handleAIChat(context.getArgument("message", String.class), context.getSource());
                    return 1;
                }))
            )
            .then(CommandManager.literal("learn").executes(context -> {
                learningSystem.incrementGamesPlayed();
                learningSystem.save();
                context.getSource().sendMessage(Text.literal("学习完成！"));
                return 1;
            }))
            .then(CommandManager.literal("task")
                .then(CommandManager.argument("task", CommandManager.string()).executes(context -> {
                    executeTask(context.getArgument("task", String.class), context.getSource());
                    return 1;
                }))
            )
        );
    }

    private void registerSystemCommands(CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("aistat").executes(context -> {
            context.getSource().sendMessage(Text.literal(
                "AI Stats: Games=" + learningSystem.getTotalGamesPlayed() + " Tasks=" + learningSystem.getTotalTasksCompleted()
            ));
            return 1;
        }));
    }

    private void startAI(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(Text.literal("AI 启动完成"), false);
        AiPlayerMod.LOGGER.info("AI player started");
    }

    private void stopAI(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(Text.literal("AI 已停止"), false);
        AiPlayerMod.LOGGER.info("AI player stopped");
    }

    private void showStatus(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(Text.literal(
            "AI 状态: 运行中 | 游戏次数: " + learningSystem.getTotalGamesPlayed() +
            " | 完成任务: " + learningSystem.getTotalTasksCompleted()
        ), false);
    }

    private void handleAIChat(String message, net.minecraft.server.command.ServerCommandSource source) {
        String response = chatSystem.generateResponse(message, "");
        if (response != null) source.sendFeedback(Text.literal("[AI] " + response), false);
    }

    private void executeTask(String task, net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(Text.literal("执行任务: " + task), false);
    }
}
