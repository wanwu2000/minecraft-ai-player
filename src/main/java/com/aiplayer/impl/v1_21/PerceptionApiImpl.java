package com.aiplayer.impl.v1_21;

import com.aiplayer.api.PerceptionApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

/**
 * Minecraft 1.21 版本感知 API 实现
 */
public class PerceptionApiImpl implements PerceptionApi {
    @Override
    public List<Entity> scanEntities(PlayerEntity player, int range, Class<? extends Entity> entityType) {
        List<Entity> entities = new ArrayList<>();
        net.minecraft.world.World world = player.getWorld();
        if (world == null) return entities;

        net.minecraft.util.math.Box box = player.getBoundingBox().expand(range);
        for (Entity entity : world.getEntitiesByClass(entityType, box, e -> true)) {
            if (entity.distanceTo(player) <= range) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public List<BlockPos> scanBlocks(PlayerEntity player, int range, BlockState blockState) {
        List<BlockPos> blocks = new ArrayList<>();
        net.minecraft.world.World world = player.getWorld();
        if (world == null) return blocks;

        int x = player.getBlockX();
        int y = player.getBlockY();
        int z = player.getBlockZ();

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
                    if (world.getBlockState(pos).equals(blockState)) {
                        blocks.add(pos);
                    }
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean isVisible(Entity entity, PlayerEntity player) {
        net.minecraft.world.World world = player.getWorld();
        if (world == null || entity == null) return false;

        net.minecraft.util.math.Vec3d start = player.getCameraPosVec(1.0f);
        net.minecraft.util.math.Vec3d end = entity.getPos().subtract(start).normalize();

        net.minecraft.util.hit.HitResult result = world.raycast(
            start,
            end.add(start),
            false,
            true,
            64.0
        );

        return result.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY &&
               ((net.minecraft.util.hit.EntityHitResult) result).getEntity() == entity;
    }

    @Override
    public double getDistanceTo(Entity entity, PlayerEntity player) {
        return player.distanceTo(entity);
    }

    @Override
    public ItemStack getHeldItem(PlayerEntity player) {
        return player.getMainHandStack();
    }

    @Override
    public boolean isBreakable(BlockState state, PlayerEntity player) {
        net.minecraft.world.World world = player.getWorld();
        if (world == null) return false;
        return state.getHardness(world, new BlockPos(player.getX(), player.getY(), player.getZ())) >= 0;
    }
}
