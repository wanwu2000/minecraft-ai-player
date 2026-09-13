package com.aiplayer.impl;

import com.aiplayer.api.ChatApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

/**
 * 默认聊天 API 实现（回退用）
 */
public class ChatApiImpl implements ChatApi {
    @Override
    public void sendMessage(PlayerEntity player, String message) {
        if (player.getServer() != null) {
            player.getServer().getPlayerManager().sendToAll(
                Text.literal("[AI] " + message)
            );
        }
    }

    @Override
    public void sendToPlayer(PlayerEntity player, String message) {
        if (player.getServer() != null) {
            player.getServer().getPlayerManager().sendToPlayer(
                player,
                Text.literal("[AI] " + message)
            );
        }
    }

    @Override
    public void playSound(PlayerEntity player, String sound) {
        player.playSound(net.minecraft.sound.SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
    }
}
