package com.aiplayer.comet;

import com.aiplayer.AiPlayerMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Meteor Client 集成桥接器
 * 使用反射安全调用 Meteor Client API
 * 官网: https://www.meteorclient.com/
 */
public class CometBridge {
    private static final Logger LOGGER = LoggerFactory.getLogger("CometBridge");
    private static final String MODULE_PREFIX = "mc.";
    private static final String BARITONE_PREFIX = "#";

    private boolean meteorAvailable = false;
    private Object meteorClientInstance;
    private Object modules;

    public CometBridge() {
        checkAvailability();
    }

    /**
     * 检查 Meteor Client 是否可用
     */
    private void checkAvailability() {
        try {
            // 尝试加载 Meteor Client 主类
            Class<?> meteorClientClass = Class.forName("meteordevelopment.meteorclient.MeteorClient");
            meteorAvailable = true;

            // 获取单例实例
            meteorClientInstance = meteorClientClass.getField("INSTANCE").get(null);

            // 获取模块管理器
            Class<?> modulesClass = Class.forName("meteordevelopment.meteorclient.systems.modules.Modules");
            modules = meteorClientInstance.getClass().getMethod("getModules").invoke(meteorClientInstance);

            LOGGER.info("Meteor Client detected successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.info("Meteor Client not found, using built-in AI controls");
        } catch (Exception e) {
            LOGGER.warn("Failed to initialize Meteor Client integration", e);
        }
    }

    /**
     * 检查 Meteor Client 是否可用
     */
    public boolean isAvailable() {
        return meteorAvailable;
    }

    /**
     * 启用 Meteor 模块
     */
    public boolean enableModule(String moduleName) {
        if (!meteorAvailable) return false;

        try {
            Object module = modules.getClass().getMethod("get", String.class)
                .invoke(modules, moduleName.toLowerCase());

            if (module != null) {
                module.getClass().getMethod("toggle").invoke(module);
                LOGGER.info("Enabled Meteor module: {}", moduleName);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to enable module: {}", moduleName, e);
        }
        return false;
    }

    /**
     * 禁用 Meteor 模块
     */
    public boolean disableModule(String moduleName) {
        if (!meteorAvailable) return false;

        try {
            Object module = modules.getClass().getMethod("get", String.class)
                .invoke(modules, moduleName.toLowerCase());

            if (module != null) {
                // 检查是否已启用
                boolean active = (boolean) module.getClass().getMethod("isActive").invoke(module);
                if (active) {
                    module.getClass().getMethod("toggle").invoke(module);
                    LOGGER.info("Disabled Meteor module: {}", moduleName);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to disable module: {}", moduleName, e);
        }
        return false;
    }

    /**
     * 发送 Baritone 命令
     */
    public void sendBaritoneCommand(String command) {
        if (!meteorAvailable) return;

        try {
            net.minecraft.server.MinecraftServer server =
                net.minecraft.client.MinecraftClient.getInstance().server;

            if (server != null) {
                String fullCommand = BARITONE_PREFIX + command;
                server.getCommandManager().performCommand(
                    server.getCommandSource().withSuppressedOutput(),
                    fullCommand
                );
                LOGGER.info("Sent Baritone command: {}", fullCommand);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to send Baritone command", e);
        }
    }

    /**
     * 设置 Baritone 目标位置
     */
    public void setBaritoneTarget(double x, double y, double z) {
        sendBaritoneCommand(String.format("goto %d %d %d", (int)x, (int)y, (int)z));
    }

    /**
     * 停止 Baritone
     */
    public void stopBaritone() {
        sendBaritoneCommand("stop");
    }

    /**
     * 列出所有 Meteor 模块
     */
    public String listModules() {
        if (!meteorAvailable) return "Meteor Client 未安装";

        try {
            StringBuilder sb = new StringBuilder("可用模块:\n");

            // 使用反射遍历所有模块
            Object[] allModules = (Object[]) modules.getClass().getMethod("getAll").invoke(modules);
            if (allModules != null) {
                for (Object module : allModules) {
                    String name = (String) module.getClass().getMethod("getName").invoke(module);
                    boolean active = (boolean) module.getClass().getMethod("isActive").invoke(module);
                    sb.append("  - ").append(name).append(" [").append(active ? "ON" : "OFF").append("]\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "获取模块列表失败: " + e.getMessage();
        }
    }
}
