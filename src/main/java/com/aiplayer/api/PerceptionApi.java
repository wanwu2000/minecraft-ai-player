package com.aiplayer.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public interface PerceptionApi {
    List<Entity> getNearbyEntities(PlayerEntity player, double range);
    List<PlayerEntity> getNearbyPlayers(PlayerEntity player, double range);
    boolean isBlockSolid(BlockPos pos, PlayerEntity player);
    BlockPos getTargetBlock(PlayerEntity player, double range);
}
