package com.aiplayer.impl.v1_26_1;

import com.aiplayer.api.ChatApi;
import net.minecraft.text.Text;

public class ChatApiImpl implements ChatApi {
    @Override
    public void sendMessage(String message) {
        net.minecraft.client.network.ClientPlayNetworkHandler player = net.minecraft.client.MinecraftClient.getInstance().getNetworkHandler();
        if (player != null) player.sendChatMessage(message);
    }

    @Override
    public void sendCommand(String command) {
        net.minecraft.client.network.ClientPlayNetworkHandler player = net.minecraft.client.MinecraftClient.getInstance().getNetworkHandler();
        if (player != null) player.sendCommand(command);
    }

    @Override
    public void sendSystemMessage(String message) {
        net.minecraft.client.MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("[AI System] " + message));
    }

    @Override
    public void playSound(String sound, float volume, float pitch) { }
}
