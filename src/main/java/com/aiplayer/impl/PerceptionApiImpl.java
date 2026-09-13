package com.aiplayer.impl;

import com.aiplayer.api.PerceptionApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import java.util.List;

/**
 * 默认感知 API 实现（回退用）
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
        return null;
    }
}
