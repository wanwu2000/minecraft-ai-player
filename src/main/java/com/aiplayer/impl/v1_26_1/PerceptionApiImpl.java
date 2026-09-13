package com.aiplayer.impl.v1_26_1;

import com.aiplayer.api.PerceptionApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class PerceptionApiImpl implements PerceptionApi {
    @Override
    public List<Entity> scanEntities(PlayerEntity player, int range, Class<? extends Entity> entityType) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : player.getWorld().getEntitiesByClass(entityType, player.getBoundingBox().expand(range), e -> true)) {
            if (entity.distanceTo(player) <= range) entities.add(entity);
        }
        return entities;
    }

    @Override
    public List<BlockPos> scanBlocks(PlayerEntity player, int range, BlockState blockState) {
        List<BlockPos> blocks = new ArrayList<>();
        int x = player.getBlockX(), y = player.getBlockY(), z = player.getBlockZ();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
                    if (player.getWorld().getBlockState(pos).equals(blockState)) blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean isVisible(Entity entity, PlayerEntity player) {
        return player.getWorld().raycast(
            player.getCameraPosVec(1.0f),
            entity.getVecBetweenMiddleAndPos(0.5f).subtract(player.getCameraPosVec(1.0f)).normalize(),
            64.0
        ) == entity;
    }

    @Override
    public double getDistanceTo(Entity entity, PlayerEntity player) { return player.distanceTo(entity); }

    @Override
    public ItemStack getHeldItem(PlayerEntity player) { return player.getMainHandStack(); }

    @Override
    public boolean isBreakable(BlockState state, PlayerEntity player) {
        return state.getHardness(player.getWorld(), new BlockPos(player)) >= 0;
    }
}
