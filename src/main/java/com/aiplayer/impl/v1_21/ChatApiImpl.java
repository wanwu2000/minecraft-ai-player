package com.aiplayer.impl.v1_21;

import com.aiplayer.api.ChatApi;
import net.minecraft.text.Text;

/**
 * Minecraft 1.21 版本聊天 API 实现
 * 注意: 1.21 仍然使用 net.minecraft.text.Text
 */
public class ChatApiImpl implements ChatApi {
    @Override
    public void sendMessage(String message) {
        net.minecraft.client.network.ClientPlayNetworkHandler networkHandler =
            net.minecraft.client.MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler != null) {
            networkHandler.sendChatMessage(message);
        }
    }

    @Override
    public void sendCommand(String command) {
        net.minecraft.client.network.ClientPlayNetworkHandler networkHandler =
            net.minecraft.client.MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler != null) {
            networkHandler.sendCommand(command);
        }
    }

    @Override
    public void sendSystemMessage(String message) {
        net.minecraft.client.MinecraftClient.getInstance().inGameHud.getChatHud()
            .addMessage(Text.literal("[AI System] " + message));
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        // 1.21 音效播放
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.world != null) {
            client.world.playSound(null, client.player.getX(), client.player.getY(), client.player.getZ(),
                net.minecraft.sound.SoundEvent.of(net.minecraft.registry.RegistryKey.of(
                    net.minecraft.registry.Registry.SOUND_EVENT_KEY,
                    net.minecraft.util.Identifier.of("minecraft", sound.toLowerCase()))),
                net.minecraft.sound.SoundCategory.PLAYER, volume, pitch);
        }
    }
}
