package com.aiplayer.comet;

import com.aiplayer.AiPlayerMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Meteor Client 集成桥接器 - 纯客户端实现
 * 使用反射安全调用 Meteor Client API
 * 官网: https://www.meteorclient.com/
 */
public class CometBridge {
    private static final Logger LOGGER = LoggerFactory.getLogger("CometBridge");
    private static final String BARITONE_PREFIX = "#";

    private boolean meteorAvailable = false;
    private Object meteorClientInstance;
    private Object modules;

    public CometBridge() {
        checkAvailability();
    }

    private void checkAvailability() {
        try {
            Class<?> meteorClientClass = Class.forName("meteordevelopment.meteorclient.MeteorClient");
            meteorAvailable = true;
            meteorClientInstance = meteorClientClass.getField("INSTANCE").get(null);
            modules = meteorClientInstance.getClass().getMethod("getModules").invoke(meteorClientInstance);
            LOGGER.info("Meteor Client detected successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.info("Meteor Client not found, using built-in AI controls");
        } catch (Exception e) {
            LOGGER.warn("Failed to initialize Meteor Client integration", e);
        }
    }

    public boolean isAvailable() { return meteorAvailable; }

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

    public boolean disableModule(String moduleName) {
        if (!meteorAvailable) return false;
        try {
            Object module = modules.getClass().getMethod("get", String.class)
                .invoke(modules, moduleName.toLowerCase());
            if (module != null) {
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

    public void sendBaritoneCommand(String command) {
        if (!meteorAvailable) return;
        try {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.getNetworkHandler() != null) {
                String fullCommand = BARITONE_PREFIX + command;
                client.getNetworkHandler().sendChatMessage(fullCommand);
                LOGGER.info("Sent Baritone command: {}", fullCommand);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to send Baritone command", e);
        }
    }

    public void setBaritoneTarget(double x, double y, double z) {
        sendBaritoneCommand(String.format("goto %d %d %d", (int)x, (int)y, (int)z));
    }

    public void stopBaritone() {
        sendBaritoneCommand("stop");
    }

    public String listModules() {
        if (!meteorAvailable) return "Meteor Client 未安装";
        try {
            StringBuilder sb = new StringBuilder("可用模块:\n");
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

    public String getBaritoneStatus() {
        if (!meteorAvailable) return "Baritone 不可用";
        return "Baritone 已连接";
    }
}
