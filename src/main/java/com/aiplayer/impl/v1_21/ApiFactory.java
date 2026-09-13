package com.aiplayer.impl.v1_21;

import com.aiplayer.api.ActionApi;
import com.aiplayer.api.ChatApi;
import com.aiplayer.api.MovementApi;
import com.aiplayer.api.PerceptionApi;

/**
 * Minecraft 1.21 版本 API 工厂
 */
public class ApiFactory {
    private static MovementApi movementApi;
    private static PerceptionApi perceptionApi;
    private static ActionApi actionApi;
    private static ChatApi chatApi;

    public static MovementApi getMovementApi() {
        if (movementApi == null) movementApi = new MovementApiImpl();
        return movementApi;
    }

    public static PerceptionApi getPerceptionApi() {
        if (perceptionApi == null) perceptionApi = new PerceptionApiImpl();
        return perceptionApi;
    }

    public static ActionApi getActionApi() {
        if (actionApi == null) actionApi = new ActionApiImpl();
        return actionApi;
    }

    public static ChatApi getChatApi() {
        if (chatApi == null) chatApi = new ChatApiImpl();
        return chatApi;
    }
}
