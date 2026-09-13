package com.aiplayer.api;

import net.minecraft.entity.player.PlayerEntity;

public interface ChatApi {
    void sendMessage(PlayerEntity player, String message);
    void sendToPlayer(PlayerEntity player, String message);
    void playSound(PlayerEntity player, String sound);
}
