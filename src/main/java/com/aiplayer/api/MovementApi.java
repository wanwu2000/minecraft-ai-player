package com.aiplayer.api;

public interface MovementApi {
    void moveTowards(net.minecraft.entity.player.PlayerEntity player, double targetX, double targetY, double targetZ);
    void jump(net.minecraft.entity.player.PlayerEntity player);
    void sprint(net.minecraft.entity.player.PlayerEntity player, boolean sprinting);
    MovementDirection getMovementDirection(net.minecraft.entity.player.PlayerEntity player);
    enum MovementDirection { FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN, NONE }
}
