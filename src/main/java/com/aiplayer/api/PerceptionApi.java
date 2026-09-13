package com.aiplayer.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public interface PerceptionApi {
    List<Entity> scanEntities(PlayerEntity player, int range, Class<? extends Entity> entityType);
    List<BlockPos> scanBlocks(PlayerEntity player, int range, BlockState blockState);
    boolean isVisible(Entity entity, PlayerEntity player);
    double getDistanceTo(Entity entity, PlayerEntity player);
    ItemStack getHeldItem(PlayerEntity player);
    boolean isBreakable(BlockState state, PlayerEntity player);
}
