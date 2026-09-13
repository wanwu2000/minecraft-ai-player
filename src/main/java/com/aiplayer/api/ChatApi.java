package com.aiplayer.api;

public interface ChatApi {
    void sendMessage(String message);
    void sendCommand(String command);
    void sendSystemMessage(String message);
    void playSound(String sound, float volume, float pitch);
}
