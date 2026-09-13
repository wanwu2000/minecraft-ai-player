package com.aiplayer.command;

import com.aiplayer.client.AiPlayerClient;
import com.aiplayer.chat.ChatSystem;
import com.aiplayer.learning.LearningSystem;
import com.aiplayer.comet.CometBridge;
import com.aiplayer.human.Humanizer;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端命令注册表 - 纯客户端实现
 */
public class CommandRegistry {
    private final ChatSystem chatSystem;
    private final LearningSystem learningSystem;
    private final CometBridge cometBridge;
    private final Humanizer humanizer;
    private final Map<String, TaskParser> taskParsers = new HashMap<>();

    public CommandRegistry() {
        this.chatSystem = new ChatSystem();
        this.learningSystem = new LearningSystem();
        this.cometBridge = new CometBridge();
        this.humanizer = new Humanizer();
        registerTaskParsers();
    }

    /**
     * 注册命令处理器 - 使用 Fabric 客户端命令 API
     */
    public void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerAISlashCommands(dispatcher);
            registerCometCommands(dispatcher);
            registerSystemCommands(dispatcher);
        });
    }

    /**
     * 注册 AI 相关命令
     */
    private void registerAISlashCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("ai")
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("start").executes(context -> {
                AiPlayerClient.getInstance().startAI();
                context.getSource().sendFeedback(Text.literal("✅ AI 启动完成！情绪: " + humanizer.getMoodDescription()));
                return 1;
            }))
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("stop").executes(context -> {
                AiPlayerClient.getInstance().stopAI();
                context.getSource().sendFeedback(Text.literal("⏹️ AI 已停止"));
                return 1;
            }))
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("status").executes(context -> {
                showStatus(context);
                return 1;
            }))
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("chat")
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument("message", net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.string()).executes(context -> {
                    String message = context.getArgument("message", String.class);
                    handleAIChat(message, context);
                    return 1;
                }))
            )
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("learn").executes(context -> {
                learningSystem.incrementGamesPlayed();
                learningSystem.save();
                context.getSource().sendFeedback(
                    Text.literal("🧠 学习完成！游戏次数: " + learningSystem.getTotalGamesPlayed())
                        .formatted(Formatting.GREEN), false
                );
                return 1;
            }))
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("task")
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument("task", net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.string()).executes(context -> {
                    executeNaturalLanguageTask(context.getArgument("task", String.class), context);
                    return 1;
                }))
            )
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("analyze")
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument("sentence", net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.string()).executes(context -> {
                    analyzeSentence(context.getArgument("sentence", String.class), context);
                    return 1;
                }))
            )
        );
    }

    /**
     * 注册彗星模块控制命令
     */
    private void registerCometCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("mc")
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("enable")
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument("module", net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.string()).executes(context -> {
                    String module = context.getArgument("module", String.class);
                    boolean success = cometBridge.enableModule(module);
                    context.getSource().sendFeedback(
                        Text.literal(success ? "✅ 已启用模块: " + module : "❌ 模块不存在: " + module)
                            .formatted(success ? Formatting.GREEN : Formatting.RED), false
                    );
                    return success ? 1 : 0;
                }))
            )
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("disable")
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument("module", net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.string()).executes(context -> {
                    String module = context.getArgument("module", String.class);
                    boolean success = cometBridge.disableModule(module);
                    context.getSource().sendFeedback(
                        Text.literal(success ? "✅ 已禁用模块: " + module : "❌ 模块不存在: " + module)
                            .formatted(success ? Formatting.GREEN : Formatting.RED), false
                    );
                    return success ? 1 : 0;
                }))
            )
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("list").executes(context -> {
                context.getSource().sendFeedback(
                    Text.literal(cometBridge.listModules()), false
                );
                return 1;
            }))
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("baritone")
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("goto")
                    .executes(context -> {
                        // 解析坐标参数
                        String[] args = context.getInput().split("\\s+");
                        if (args.length >= 4) {
                            try {
                                double x = Double.parseDouble(args[2]);
                                double y = Double.parseDouble(args[3]);
                                double z = args.length > 4 ? Double.parseDouble(args[4]) : 64;
                                cometBridge.setBaritoneTarget(x, y, z);
                                context.getSource().sendFeedback(
                                    Text.literal("📍 Baritone 目标已设置: " + (int)x + " " + (int)y + " " + (int)z)
                                        .formatted(Formatting.AQUA), false
                                );
                            } catch (NumberFormatException e) {
                                context.getSource().sendFeedback(
                                    Text.literal("❌ 坐标格式错误，请使用数字").formatted(Formatting.RED), false
                                );
                            }
                        }
                        return 1;
                    })
                )
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("stop").executes(context -> {
                    cometBridge.stopBaritone();
                    context.getSource().sendFeedback(
                        Text.literal("⏹️ Baritone 已停止").formatted(Formatting.RED), false
                    );
                    return 1;
                }))
                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("status").executes(context -> {
                    context.getSource().sendFeedback(
                        Text.literal("📊 " + cometBridge.getBaritoneStatus()), false
                    );
                    return 1;
                }))
            )
        );
    }

    /**
     * 注册系统命令
     */
    private void registerSystemCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("aistat").executes(context -> {
            context.getSource().sendFeedback(Text.literal(
                "📊 AI Stats:\n" +
                "  游戏次数: " + learningSystem.getTotalGamesPlayed() + "\n" +
                "  完成任务: " + learningSystem.getTotalTasksCompleted() + "\n" +
                "  聊天功能: " + (chatSystem.getMood() >= 60 ? "正常" : "需关注")
            ).formatted(Formatting.GOLD), false);
            return 1;
        }));

        dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("exec")
            .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument("command", net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.string()).executes(context -> {
                executeNaturalLanguageTask(context.getArgument("command", String.class), context);
                return 1;
            }))
        );
    }

    /**
     * 显示 AI 状态
     */
    private void showStatus(net.fabricmc.fabric.api.client.command.v2.ClientCommandContext<FabricClientCommandSource> context) {
        var source = context.getSource();
        var player = source.getPlayer();
        
        String status = String.format(
            "📊 AI 状态:\n" +
            "  运行中: %b\n" +
            "  情绪: %s (%d/100)\n" +
            "  疲劳: %d%%\n" +
            "  当前任务: %s\n" +
            "  游戏次数: %d\n" +
            "  完成任务: %d\n" +
            "  彗星集成: %s",
            AiPlayerClient.getInstance().isRunning(),
            chatSystem.getMoodDescription(),
            chatSystem.getMood(),
            humanizer.getFatigue(),
            chatSystem.getCurrentTask() != null ? chatSystem.getCurrentTask() : "无",
            learningSystem.getTotalGamesPlayed(),
            learningSystem.getTotalTasksCompleted(),
            cometBridge.isAvailable() ? "已连接" : "未安装"
        );
        source.sendFeedback(Text.literal(status), false);
    }

    /**
     * 处理聊天
     */
    private void handleAIChat(String message, net.fabricmc.fabric.api.client.command.v2.ClientCommandContext<FabricClientCommandSource> context) {
        var source = context.getSource();
        String response = chatSystem.generateResponse(message, "");
        if (response != null) {
            source.sendFeedback(Text.literal("[AI] " + response), false);
        }
    }

    /**
     * 自然语言任务执行
     */
    private void executeNaturalLanguageTask(String sentence, net.fabricmc.fabric.api.client.command.v2.ClientCommandContext<FabricClientCommandSource> context) {
        var source = context.getSource();
        Map<String, String> keywords = parseKeywords(sentence);
        
        source.sendFeedback(Text.literal("🔍 解析结果: " + keywords), false);

        for (Map.Entry<String, TaskParser> entry : taskParsers.entrySet()) {
            TaskParser parser = entry.getValue();
            if (parser.matches(sentence)) {
                source.sendFeedback(Text.literal("🎯 匹配任务类型: " + entry.getKey()), false);
                parser.execute(keywords);
                return;
            }
        }

        // 未匹配，作为聊天处理
        String response = chatSystem.generateResponse(sentence, "");
        if (response != null) {
            source.sendFeedback(Text.literal("[AI] " + response), false);
        }
    }

    /**
     * 分析句子结构
     */
    private void analyzeSentence(String sentence, net.fabricmc.fabric.api.client.command.v2.ClientCommandContext<FabricClientCommandSource> context) {
        var source = context.getSource();
        Map<String, String> keywords = parseKeywords(sentence);
        source.sendFeedback(Text.literal(
            "📝 句子分析:\n" +
            "原始: " + sentence + "\n" +
            "关键词: " + keywords
        ), false);
    }

    /**
     * 解析关键词
     */
    private Map<String, String> parseKeywords(String sentence) {
        Map<String, String> keywords = new HashMap<>();
        String lower = sentence.toLowerCase();

        if (lower.contains("矿") || lower.contains("挖")) keywords.put("action", "mine");
        if (lower.contains("建") || lower.contains("搭建")) keywords.put("action", "build");
        if (lower.contains("高速") || lower.contains("公路")) keywords.put("action", "highway");
        if (lower.contains("保护") || lower.contains("守护")) keywords.put("action", "protect");
        if (lower.contains("家") || lower.contains("回家")) keywords.put("action", "home");

        if (lower.contains("煤")) keywords.put("target", "coal");
        if (lower.contains("铁")) keywords.put("target", "iron");
        if (lower.contains("钻石") || lower.contains("diamond")) keywords.put("target", "diamond");

        return keywords;
    }

    /**
     * 注册任务解析器
     */
    private void registerTaskParsers() {
        registerParser("mine", sentence ->
            sentence.contains("矿") || sentence.contains("挖"),
            () -> {
                chatSystem.setCurrentTask("挖矿");
                learningSystem.recordSuccess("mine");
                chatSystem.updateMood(5);
            }
        );

        registerParser("build", sentence ->
            sentence.contains("建") || sentence.contains("搭建"),
            () -> {
                chatSystem.setCurrentTask("建造");
                if (cometBridge.isAvailable()) {
                    cometBridge.enableModule("scaffold");
                }
                learningSystem.recordSuccess("build");
            }
        );

        registerParser("protect", sentence ->
            sentence.contains("保护") || sentence.contains("守护"),
            () -> {
                chatSystem.setCurrentTask("保护");
                learningSystem.recordSuccess("protect");
            }
        );

        registerParser("home", sentence ->
            sentence.contains("家") || sentence.contains("回家"),
            () -> {
                chatSystem.setCurrentTask("回家");
                if (cometBridge.isAvailable()) {
                    cometBridge.stopBaritone();
                }
                learningSystem.recordSuccess("home");
            }
        );
    }

    @FunctionalInterface
    private interface TaskParser {
        boolean matches(String sentence);
        void execute();
    }

    private void registerParser(String name, java.util.function.Predicate<String> matcher, TaskParser parser) {
        taskParsers.put(name, new TaskParser() {
            @Override
            public boolean matches(String sentence) { return matcher.test(sentence); }
            @Override
            public void execute() { parser.execute(); }
        });
    }
}
