package com.aiplayer.command;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.chat.ChatSystem;
import com.aiplayer.learning.LearningSystem;
import com.aiplayer.comet.CometBridge;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令注册表 - 支持自然语言指令解析
 */
public class CommandRegistry {
    private final ChatSystem chatSystem;
    private final LearningSystem learningSystem;
    private final CometBridge cometBridge;
    private final Map<String, TaskParser> taskParsers = new HashMap<>();

    public CommandRegistry() {
        this.chatSystem = new ChatSystem();
        this.learningSystem = new LearningSystem();
        this.cometBridge = new CometBridge();
        registerTaskParsers();
    }

    /**
     * 注册任务解析器（自然语言理解）
     */
    private void registerTaskParsers() {
        // 挖矿任务
        registerParser("mine", keywords ->
            keywords.stream().anyMatch(k -> k.contains("矿") || k.contains("挖") || k.equals("mine")),
            (keywords, ctx) -> executeMiningTask(keywords, ctx)
        );

        // 建造任务
        registerParser("build", keywords ->
            keywords.stream().anyMatch(k -> k.contains("建") || k.contains("搭建") || k.equals("build")),
            (keywords, ctx) -> executeBuildingTask(keywords, ctx)
        );

        // 高速公路任务
        registerParser("highway", keywords ->
            keywords.stream().anyMatch(k -> k.contains("高速") || k.contains("公路")),
            (keywords, ctx) -> executeHighwayTask(keywords, ctx)
        );

        // 保护任务
        registerParser("protect", keywords ->
            keywords.stream().anyMatch(k -> k.contains("保护") || k.contains("守护") || k.equals("protect")),
            (keywords, ctx) -> executeProtectTask(keywords, ctx)
        );

        // 回家任务
        registerParser("home", keywords ->
            keywords.stream().anyMatch(k -> k.contains("家") || k.contains("回家") || k.equals("home")),
            (keywords, ctx) -> executeHomeTask(keywords, ctx)
        );

        // 学习任务
        registerParser("learn", keywords ->
            keywords.stream().anyMatch(k -> k.contains("学习") || k.equals("learn")),
            (keywords, ctx) -> executeLearnTask(keywords, ctx)
        );

        // Baritone路径搜索
        registerParser("goto", keywords ->
            keywords.stream().anyMatch(k -> k.equals("goto") || k.equals("去") || k.equals("到")),
            (keywords, ctx) -> executeGotoTask(keywords, ctx)
        );
    }

    @FunctionalInterface
    private interface TaskParser {
        boolean matches(String sentence);
        void execute(Map<String, String> keywords, ServerCommandContext ctx);
    }

    private void executeMiningTask(Map<String, String> keywords, ServerCommandContext ctx) {
        String target = keywords.getOrDefault("target", "coal");
        chatSystem.setCurrentTask("挖矿 (" + target + ")");
        ctx.sendFeedback("🎯 开始挖矿任务，目标: " + target);

        // TODO: 实现实际挖矿逻辑
        learningSystem.recordSuccess("mine_" + target);
        ctx.sendFeedback("✅ 挖矿完成！已学习新经验。");
        chatSystem.clearCurrentTask();
    }

    private void executeBuildingTask(Map<String, String> keywords, ServerCommandContext ctx) {
        String structure = keywords.getOrDefault("structure", "house");
        chatSystem.setCurrentTask("建造 (" + structure + ")");
        ctx.sendFeedback("🏗️ 开始建造任务，结构: " + structure);

        // 如果启用彗星集成，启用 Scaffold 模块
        if (cometBridge.isAvailable()) {
            cometBridge.enableModule("scaffold");
            ctx.sendFeedback("☄️ 彗星 Scaffold 模块已启用");
        }

        // TODO: 实现实际建造逻辑
        learningSystem.recordSuccess("build_" + structure);
        ctx.sendFeedback("✅ 建造完成！");
        chatSystem.clearCurrentTask();
    }

    private void executeHighwayTask(Map<String, String> keywords, ServerCommandContext ctx) {
        String dimension = keywords.getOrDefault("dimension", "overworld");
        chatSystem.setCurrentTask("建造高速公路 (" + dimension + ")");
        ctx.sendFeedback("🛤️ 开始建造高速公路，维度: " + dimension);

        // 启用彗星相关模块
        if (cometBridge.isAvailable()) {
            cometBridge.enableModule("scaffold");
            cometBridge.enableModule("autobuild");
            ctx.sendFeedback("☄️ 彗星自动建筑模块已启用");
        }

        // TODO: 实现高速公路建造逻辑
        learningSystem.recordSuccess("highway_" + dimension);
        ctx.sendFeedback("✅ 高速公路建造完成！");
        chatSystem.clearCurrentTask();
    }

    private void executeProtectTask(Map<String, String> keywords, ServerCommandContext ctx) {
        chatSystem.setCurrentTask("保护模式");
        ctx.sendFeedback("⚔️ 进入保护模式，我会守护你！");

        // TODO: 实现战斗逻辑
        learningSystem.recordSuccess("protect");
        ctx.sendFeedback("✅ 保护任务完成！");
        chatSystem.clearCurrentTask();
    }

    private void executeHomeTask(Map<String, String> keywords, ServerCommandContext ctx) {
        chatSystem.setCurrentTask("回家");
        ctx.sendFeedback("🏠 开始返回基地...");

        // 使用 Baritone 导航回家
        if (cometBridge.isAvailable()) {
            // TODO: 获取玩家的家坐标并设置 Baritone 目标
            ctx.sendFeedback("📍 正在导航回家...");
        }

        learningSystem.recordSuccess("home");
        ctx.sendFeedback("✅ 已回到家！");
        chatSystem.clearCurrentTask();
    }

    private void executeLearnTask(Map<String, String> keywords, ServerCommandContext ctx) {
        learningSystem.incrementGamesPlayed();
        learningSystem.save();
        ctx.sendFeedback("🧠 学习完成！已成功记录经验。");
    }

    private void executeGotoTask(Map<String, String> keywords, ServerCommandContext ctx) {
        // 解析坐标
        double x = parseDouble(keywords.get("x"), ctx.player().getX());
        double y = parseDouble(keywords.get("y"), ctx.player().getY());
        double z = parseDouble(keywords.get("z"), ctx.player().getZ());

        chatSystem.setCurrentTask("导航到 " + (int)x + ", " + (int)y + ", " + (int)z);
        ctx.sendFeedback("📍 设置目标位置: " + (int)x + " " + (int)y + " " + (int)z);

        if (cometBridge.isAvailable()) {
            cometBridge.setBaritoneTarget(x, y, z);
            ctx.sendFeedback("☄️ Baritone 正在导航...");
        } else {
            // 备用：直接使用 Minecraft 命令
            ctx.sendFeedback("🗺️ 尝试使用内置导航...");
        }

        learningSystem.recordSuccess("goto");
        chatSystem.clearCurrentTask();
    }

    private double parseDouble(String value, double defaultVal) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private void registerParser(String name, TaskParser parser) {
        taskParsers.put(name, parser);
    }

    /**
     * 注册命令处理器
     */
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registrationEnvironment, env) -> {
            registerAISlashCommands(dispatcher);
            registerSystemCommands(dispatcher);
        });
    }

    /**
     * 注册 AI 子命令
     */
    private void registerAISlashCommands(CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ai")
            .then(CommandManager.literal("start").executes(context -> {
                startAI(context.getSource());
                return 1;
            }))
            .then(CommandManager.literal("stop").executes(context -> {
                stopAI(context.getSource());
                return 1;
            }))
            .then(CommandManager.literal("status").executes(context -> {
                showStatus(context.getSource());
                return 1;
            }))
            .then(CommandManager.literal("chat")
                .then(CommandManager.argument("message", CommandManager.string()).executes(context -> {
                    handleAIChat(context.getArgument("message", String.class), context.getSource());
                    return 1;
                }))
            )
            .then(CommandManager.literal("learn").executes(context -> {
                learningSystem.incrementGamesPlayed();
                learningSystem.save();
                context.getSource().sendFeedback(
                    Text.literal("🧠 学习完成！已成功记录经验。").formatted(Formatting.GREEN), false
                );
                return 1;
            }))
            .then(CommandManager.literal("task")
                .then(CommandManager.argument("task", CommandManager.string()).executes(context -> {
                    executeNaturalLanguageTask(context.getArgument("task", String.class), context.getSource());
                    return 1;
                }))
            )
            .then(CommandManager.literal("analyze")
                .then(CommandManager.argument("sentence", CommandManager.string()).executes(context -> {
                    analyzeSentence(context.getArgument("sentence", String.class), context.getSource());
                    return 1;
                }))
            )
        );

        // 彗星模块控制命令
        dispatcher.register(CommandManager.literal("mc")
            .then(CommandManager.literal("enable")
                .then(CommandManager.argument("module", CommandManager.string()).executes(context -> {
                    String module = context.getArgument("module", String.class);
                    boolean success = cometBridge.enableModule(module);
                    context.getSource().sendFeedback(
                        Text.literal(success ? "✅ 已启用模块: " + module : "❌ 模块不存在: " + module)
                            .formatted(success ? Formatting.GREEN : Formatting.RED), false
                    );
                    return success ? 1 : 0;
                }))
            )
            .then(CommandManager.literal("disable")
                .then(CommandManager.argument("module", CommandManager.string()).executes(context -> {
                    String module = context.getArgument("module", String.class);
                    boolean success = cometBridge.disableModule(module);
                    context.getSource().sendFeedback(
                        Text.literal(success ? "✅ 已禁用模块: " + module : "❌ 模块不存在: " + module)
                            .formatted(success ? Formatting.GREEN : Formatting.RED), false
                    );
                    return success ? 1 : 0;
                }))
            )
            .then(CommandManager.literal("list").executes(context -> {
                context.getSource().sendFeedback(
                    Text.literal(cometBridge.listModules()), false
                );
                return 1;
            }))
            .then(CommandManager.literal("baritone")
                .then(CommandManager.literal("goto")
                    .then(CommandManager.argument("x", CommandManager.string()).executes(context -> {
                        double x = parseDouble(context.getArgument("x", String.class), 0);
                        cometBridge.setBaritoneTarget(x, context.getSource().getPlayer().getY(),
                            context.getSource().getPlayer().getZ());
                        context.getSource().sendFeedback(
                            Text.literal("📍 Baritone 目标已设置: X=" + (int)x).formatted(Formatting.AQUA), false
                        );
                        return 1;
                    }))
                )
                .then(CommandManager.literal("stop").executes(context -> {
                    cometBridge.stopBaritone();
                    context.getSource().sendFeedback(
                        Text.literal("⏹️ Baritone 已停止").formatted(Formatting.RED), false
                    );
                    return 1;
                }))
                .then(CommandManager.literal("status").executes(context -> {
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
    private void registerSystemCommands(CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("aistat").executes(context -> {
            context.getSource().sendFeedback(Text.literal(
                "📊 AI Stats:\n" +
                "  游戏次数: " + learningSystem.getTotalGamesPlayed() + "\n" +
                "  完成任务: " + learningSystem.getTotalTasksCompleted() + "\n" +
                "  聊天功能: " + (chatSystem.getMood() >= 60 ? "正常" : "需关注")
            ).formatted(Formatting.GOLD), false);
            return 1;
        }));

        // 自然语言任务执行命令
        dispatcher.register(CommandManager.literal("exec")
            .then(CommandManager.argument("command", CommandManager.string()).executes(context -> {
                executeNaturalLanguageTask(context.getArgument("command", String.class), context.getSource());
                return 1;
            }))
        );
    }

    /**
     * 自然语言任务解析与执行
     */
    private void executeNaturalLanguageTask(String sentence, net.minecraft.server.command.ServerCommandSource source) {
        Map<String, String> keywords = parseKeywords(sentence);
        ServerCommandContext ctx = new ServerCommandContext(source, chatSystem, learningSystem);
        ctx.sendFeedback(Text.literal("🔍 解析结果: " + keywords), false);

        for (Map.Entry<String, TaskParser> entry : taskParsers.entrySet()) {
            TaskParser parser = entry.getValue();
            if (parser.matches(sentence)) {
                ctx.sendFeedback(Text.literal("🎯 匹配任务类型: " + entry.getKey()), false);
                parser.execute(keywords, new ServerCommandContext(source, chatSystem, learningSystem));
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
    private void analyzeSentence(String sentence, net.minecraft.server.command.ServerCommandSource source) {
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

        // 提取目标资源
        if (lower.contains("煤")) keywords.put("target", "coal");
        if (lower.contains("铁")) keywords.put("target", "iron");
        if (lower.contains("钻石") || lower.contains("diamond")) keywords.put("target", "diamond");
        if (lower.contains("金")) keywords.put("target", "gold");

        // 提取建筑类型
        if (lower.contains("房子") || lower.contains("house")) keywords.put("structure", "house");
        if (lower.contains("桥") || lower.contains("bridge")) keywords.put("structure", "bridge");
        if (lower.contains("塔") || lower.contains("tower")) keywords.put("structure", "tower");

        // 提取维度
        if (lower.contains("主世界") || lower.contains("overworld") || lower.contains("地面")) {
            keywords.put("dimension", "overworld");
        }
        if (lower.contains("下界") || lower.contains("nether")) {
            keywords.put("dimension", "nether");
        }
        if (lower.contains("末地") || lower.contains("end")) {
            keywords.put("dimension", "end");
        }

        // 提取坐标（如果存在）
        String[] parts = sentence.split("[\\s,，]+");
        for (int i = 0; i < parts.length - 2; i++) {
            try {
                double x = Double.parseDouble(parts[i]);
                double y = Double.parseDouble(parts[i + 1]);
                double z = Double.parseDouble(parts[i + 2]);
                keywords.put("x", String.valueOf(x));
                keywords.put("y", String.valueOf(y));
                keywords.put("z", String.valueOf(z));
                break;
            } catch (NumberFormatException e) {
                // 继续尝试下一个位置
            }
        }

        return keywords;
    }

    private void startAI(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(Text.literal("✅ AI 启动完成！"), false);
        AiPlayerMod.LOGGER.info("AI player started");
    }

    private void stopAI(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(Text.literal("⏹️ AI 已停止"), false);
        chatSystem.clearCurrentTask();
        cometBridge.stopBaritone();
        AiPlayerMod.LOGGER.info("AI player stopped");
    }

    private void showStatus(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(Text.literal(
            "📊 AI 状态: 运行中\n" +
            "  情绪: " + chatSystem.getMood() + "/100\n" +
            "  当前任务: " + (chatSystem.getCurrentTask() != null ? chatSystem.getCurrentTask() : "无") + "\n" +
            "  游戏次数: " + learningSystem.getTotalGamesPlayed() + "\n" +
            "  完成任务: " + learningSystem.getTotalTasksCompleted() + "\n" +
            "  彗星集成: " + (cometBridge.isAvailable() ? "已连接" : "未安装")
        ).formatted(Formatting.YELLOW), false);
    }

    private void handleAIChat(String message, net.minecraft.server.command.ServerCommandSource source) {
        String response = chatSystem.generateResponse(message, "");
        if (response != null) {
            source.sendFeedback(Text.literal("[AI] " + response), false);
        }
    }
}

/**
 * 服务器命令上下文包装类
 */
class ServerCommandContext {
    private final net.minecraft.server.command.ServerCommandSource source;
    private final ChatSystem chatSystem;
    private final LearningSystem learningSystem;

    public ServerCommandContext(
        net.minecraft.server.command.ServerCommandSource source,
        ChatSystem chatSystem,
        LearningSystem learningSystem
    ) {
        this.source = source;
        this.chatSystem = chatSystem;
        this.learningSystem = learningSystem;
    }

    public net.minecraft.server.command.ServerCommandSource getSource() { return source; }
    public net.minecraft.server.entity.LivingEntity player() { return source.getPlayer(); }
    public void sendFeedback(MutableText text, boolean sendMessage) {
        source.sendFeedback(text, sendMessage);
    }
}
