package com.aiplayer.impl;

import com.aiplayer.api.ActionApi;
import com.aiplayer.api.ChatApi;
import com.aiplayer.api.MovementApi;
import com.aiplayer.api.PerceptionApi;
import com.aiplayer.util.VersionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 版本自适应 API 工厂
 * 根据实际 Minecraft 版本自动选择正确的实现
 */
public class ApiFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger("ApiFactory");

    private static MovementApi movementApi;
    private static PerceptionApi perceptionApi;
    private static ActionApi actionApi;
    private static ChatApi chatApi;

    /**
     * 获取移动 API 实现
     */
    public static MovementApi getMovementApi() {
        if (movementApi == null) {
            movementApi = createVersionedApi("movement");
        }
        return movementApi;
    }

    /**
     * 获取感知 API 实现
     */
    public static PerceptionApi getPerceptionApi() {
        if (perceptionApi == null) {
            perceptionApi = createVersionedApi("perception");
        }
        return perceptionApi;
    }

    /**
     * 获取行动 API 实现
     */
    public static ActionApi getActionApi() {
        if (actionApi == null) {
            actionApi = createVersionedApi("action");
        }
        return actionApi;
    }

    /**
     * 获取聊天 API 实现
     */
    public static ChatApi getChatApi() {
        if (chatApi == null) {
            chatApi = createVersionedApi("chat");
        }
        return chatApi;
    }

    /**
     * 根据当前 MC 版本创建对应的 API 实例
     */
    private static Object createVersionedApi(String type) {
        boolean is1_21OrHigher = VersionHelper.isVersion1_21OrHigher();
        String implPackage = is1_21OrHigher ? "com.aiplayer.impl.v1_21" : "com.aiplayer.impl.v1_26_1";

        try {
            String className = implPackage + "." + capitalize(type) + "ApiImpl";
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            LOGGER.info("Loaded {} API from {}", type, implPackage);
            return instance;
        } catch (Exception e) {
            LOGGER.error("Failed to load {} API from {}, using fallback", type, implPackage, e);
            return getFallbackApi(type);
        }
    }

    private static Object getFallbackApi(String type) {
        switch (type) {
            case "movement": return new MovementApiImpl();
            case "perception": return new PerceptionApiImpl();
            case "action": return new ActionApiImpl();
            case "chat": return new ChatApiImpl();
            default: return null;
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
