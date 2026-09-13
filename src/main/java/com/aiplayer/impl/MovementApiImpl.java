package com.aiplayer.impl;

import com.aiplayer.api.MovementApi;
import net.minecraft.entity.player.PlayerEntity;

/**
 * 默认移动 API 实现（回退用）
 */
public class MovementApiImpl implements MovementApi {
    @Override
    public void moveTowards(PlayerEntity player, double targetX, double targetY, double targetZ) {
        double dx = targetX - player.getX();
        double dz = targetZ - player.getZ();
        double angle = Math.atan2(dx, dz);
        float yaw = (float) (angle * 180.0 / Math.PI);
        player.setYaw(yaw);
        player.input.movementForward = 1.0f;
    }

    @Override
    public void jump(PlayerEntity player) {
        if (player.isOnGround()) {
            player.jump();
        }
    }

    @Override
    public void sprint(PlayerEntity player, boolean sprinting) {
        player.setSprinting(sprinting);
    }

    @Override
    public MovementDirection getMovementDirection(PlayerEntity player) {
        if (player.input.movementForward > 0) return MovementDirection.FORWARD;
        return MovementDirection.NONE;
    }
}
