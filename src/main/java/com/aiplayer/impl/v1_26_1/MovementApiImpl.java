package com.aiplayer.impl.v1_26_1;

import com.aiplayer.api.MovementApi;
import net.minecraft.entity.player.PlayerEntity;

public class MovementApiImpl implements MovementApi {
    @Override
    public void moveTowards(PlayerEntity player, double targetX, double targetY, double targetZ) {
        double dx = targetX - player.getX();
        double dz = targetZ - player.getZ();
        double angle = Math.atan2(dx, dz);
        float yaw = (float) (angle * 180.0 / Math.PI);
        player.setYaw(yaw);
        player.input.forwardImpulse = 1.0f;
        player.input.leftImpulse = 0.0f;
        player.input.rightImpulse = 0.0f;
    }

    @Override
    public void jump(PlayerEntity player) { if (player.isOnGround()) player.jump(); }

    @Override
    public void sprint(PlayerEntity player, boolean sprinting) { player.setSprinting(sprinting); }

    @Override
    public MovementDirection getMovementDirection(PlayerEntity player) {
        if (player.input.forwardImpulse > 0) return MovementDirection.FORWARD;
        if (player.input.forwardImpulse < 0) return MovementDirection.BACKWARD;
        if (player.input.leftImpulse > 0) return MovementDirection.LEFT;
        if (player.input.leftImpulse < 0) return MovementDirection.RIGHT;
        if (player.input.upImpulse > 0) return MovementDirection.UP;
        if (player.input.downImpulse > 0) return MovementDirection.DOWN;
        return MovementDirection.NONE;
    }
}
