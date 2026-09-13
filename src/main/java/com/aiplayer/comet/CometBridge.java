package com.aiplayer.comet;

import com.aiplayer.AiPlayerMod;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.misc.BaritonePaths;
import meteordevelopment.meteorclient.systems.modules.player.AutoFall;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.world.Scaffold;
import meteordevelopment.meteorclient.systems.modules.render.ClickGUI;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;

/**
 * Meteor Client 集成桥接器
 * 通过反射调用 Meteor Client 模块和 Baritone 路径搜索
 * 官网: https://www.meteorclient.com/
 */
public class CometBridge {
    private static final String MODULE_PREFIX = "mc.";

    // 支持的 Meteor 模块映射
    private final Map<String, String> moduleMap = new HashMap<>();

    // Baritone 命令前缀
    private static final String BARITONE_PREFIX = "#";

    public CometBridge() {
        initModuleMap();
        checkAvailability();
    }

    private void initModuleMap() {
        // 常用模块名称映射
        moduleMap.put("auto_build", "Scaffold");
        moduleMap.put("pathfinding", "BaritonePaths");
        moduleMap.put("no_slow", "NoSlow");
        moduleMap.put("auto_fall", "AutoFall");
        moduleMap.put("click_gui", "ClickGUI");
    }

    private void checkAvailability() {
        try {
            Class.forName("meteordevelopment.meteorclient.MeteorClient");
            AiPlayerMod.LOGGER.info("Meteor Client detected successfully");
        } catch (ClassNotFoundException e) {
            AiPlayerMod.LOGGER.warn("Meteor Client not found, AI will use built-in pathfinding");
        }
    }

    /**
     * 检查 Meteor Client 是否可用
     */
    public boolean isAvailable() {
        try {
            return Class.forName("meteordevelopment.meteorclient.MeteorClient") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 通过名称启用模块
     * @param moduleName 模块名（如 "scaffold", "autobuild"）
     * @return 是否成功
     */
    public boolean enableModule(String moduleName) {
        if (!isAvailable()) return false;

        try {
            Modules modules = MeteorClient.INSTANCE.getModules();
            meteordevelopment.meteorclient.systems.modules.Module meteorModule =
                modules.get(moduleName.toLowerCase());

            if (meteorModule != null) {
                meteorModule.toggle();
                AiPlayerMod.LOGGER.info("Enabled Meteor module: {}", moduleName);
                return true;
            }
        } catch (Exception e) {
            AiPlayerMod.LOGGER.error("Failed to enable module: {}", moduleName, e);
        }
        return false;
    }

    /**
     * 通过名称禁用模块
     */
    public boolean disableModule(String moduleName) {
        if (!isAvailable()) return false;

        try {
            Modules modules = MeteorClient.INSTANCE.getModules();
            meteordevelopment.meteorclient.systems.modules.Module meteorModule =
                modules.get(moduleName.toLowerCase());

            if (meteorModule != null && meteorModule.isActive()) {
                meteorModule.toggle();
                AiPlayerMod.LOGGER.info("Disabled Meteor module: {}", moduleName);
                return true;
            }
        } catch (Exception e) {
            AiPlayerMod.LOGGER.error("Failed to disable module: {}", moduleName, e);
        }
        return false;
    }

    /**
     * 发送 Baritone 命令（自动添加 # 前缀）
     * @param command Baritone 命令内容（不含 # 前缀）
     */
    public void sendBaritoneCommand(String command) {
        if (!isAvailable()) return;

        String fullCommand = BARITONE_PREFIX + command;
        try {
            net.minecraft.server.MinecraftServer server =
                net.minecraft.client.MinecraftClient.getInstance().server;

            if (server != null) {
                server.getCommandManager().performCommand(
                    server.getCommandSource().withSuppressedOutput(),
                    fullCommand
                );
                AiPlayerMod.LOGGER.info("Sent Baritone command: {}", fullCommand);
            }
        } catch (Exception e) {
            AiPlayerMod.LOGGER.error("Failed to send Baritone command", e);
        }
    }

    /**
     * 设置 Baritone 目标位置
     * @param x X 坐标
     * @param y Y 坐标
     * @param z Z 坐标
     */
    public void setBaritoneTarget(double x, double y, double z) {
        sendBaritoneCommand(String.format("goto %d %d %d", (int)x, (int)y, (int)z));
    }

    /**
     * 停止 Baritone 路径搜索
     */
    public void stopBaritone() {
        sendBaritoneCommand("stop");
    }

    /**
     * 列出所有可用的 Meteor 模块
     * @return 模块列表字符串
     */
    public String listModules() {
        if (!isAvailable()) return "Meteor Client 未安装";

        try {
            Modules modules = MeteorClient.INSTANCE.getModules();
            StringBuilder sb = new StringBuilder("可用模块:\n");

            for (Category category : Category.values()) {
                sb.append("[").append(category.getName()).append("]\n");
                modules.getByCategory(category).forEach(m ->
                    sb.append("  - ").append(m.getName())
                      .append(" (").append(m.isActive() ? "ON" : "OFF").append(")\n"));
            }
            return sb.toString();
        } catch (Exception e) {
            return "获取模块列表失败: " + e.getMessage();
        }
    }

    /**
     * 获取 Baritone 状态
     * @return 状态信息
     */
    public String getBaritoneStatus() {
        if (!isAvailable()) return "Baritone 不可用";

        try {
            meteordevelopment.meteorclient.systems.modules.misc.BaritonePaths paths =
                MeteorClient.INSTANCE.getModules().getByCategory(Category.WORLD)
                    .stream()
                    .filter(m -> m instanceof meteordevelopment.meteorclient.systems.modules.misc.BaritonePaths)
                    .findFirst()
                    .map(m -> (meteordevelopment.meteorclient.systems.modules.misc.BaritonePaths) m)
                    .orElse(null);

            if (paths != null) {
                return "Baritone: 路径搜索中 - 目标: " + paths.getTarget();
            }
            return "Baritone: 未运行";
        } catch (Exception e) {
            return "Baritone 状态获取失败";
        }
    }
}
