package com.aiplayer.impl.v1_21;

import com.aiplayer.api.MovementApi;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Minecraft 1.21 版本移动 API 实现
 * 注意: 1.21 的 PlayerInput 可能有变化
 */
public class MovementApiImpl implements MovementApi {
    @Override
    public void moveTowards(PlayerEntity player, double targetX, double targetY, double targetZ) {
        double dx = targetX - player.getX();
        double dz = targetZ - player.getZ();
        double angle = Math.atan2(dx, dz);
        float yaw = (float) (angle * 180.0 / Math.PI);
        player.setYaw(yaw);

        // 1.21 使用 input.movementForward 等字段
        player.input.movementForward = 1.0f;
        player.input.movementLeft = 0.0f;
        player.input.movementUp = 0.0f;
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
        if (player.input.movementForward < 0) return MovementDirection.BACKWARD;
        if (player.input.movementLeft > 0) return MovementDirection.LEFT;
        if (player.input.movementLeft < 0) return MovementDirection.RIGHT;
        return MovementDirection.NONE;
    }
}
