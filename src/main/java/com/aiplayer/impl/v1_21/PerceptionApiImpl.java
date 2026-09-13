package com.aiplayer.impl.v1_21;

import com.aiplayer.api.PerceptionApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import java.util.List;

/**
 * Minecraft 1.21 感知 API 实现
 */
public class PerceptionApiImpl implements PerceptionApi {
    @Override
    public List<Entity> getNearbyEntities(PlayerEntity player, double range) {
        return player.getWorld().getEntitiesByClass(
            Entity.class,
            player.getBoundingBox().expand(range),
            e -> e != player && e.isAlive()
        );
    }

    @Override
    public List<PlayerEntity> getNearbyPlayers(PlayerEntity player, double range) {
        return player.getWorld().getEntitiesByClass(
            PlayerEntity.class,
            player.getBoundingBox().expand(range),
            e -> e != player
        );
    }

    @Override
    public boolean isBlockSolid(BlockPos pos, PlayerEntity player) {
        return player.getWorld().getBlockState(pos).isSolidBlock(
            player.getWorld(), pos
        );
    }

    @Override
    public BlockPos getTargetBlock(PlayerEntity player, double range) {
        // 1.21 使用 raycast
        net.minecraft.util.hit.BlockHitResult hit = player.getWorld().raycast(
            new net.minecraft.util.math.RaycastContext(
                player.getEyePos(),
                player.getEyePos().add(player.getRotationVector().multiply(range)),
                net.minecraft.util.math.RaycastContext.ShapeType.COLLIDER,
                net.minecraft.util.math.RaycastContext.FluidHandling.NONE,
                player
            )
        );
        return hit.getBlockPos();
    }
}
