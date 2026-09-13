package com.aiplayer.impl.v1_21;

import com.aiplayer.api.ActionApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Minecraft 1.21 行动 API 实现
 */
public class ActionApiImpl implements ActionApi {
    @Override
    public void swingArm(PlayerEntity player, Hand hand) {
        player.swingHand(hand);
    }

    @Override
    public boolean isHoldingItem(PlayerEntity player) {
        return !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
    }

    @Override
    public ItemStack getHeldItem(PlayerEntity player, Hand hand) {
        return hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();
    }

    @Override
    public void equipItem(PlayerEntity player, int slot) {
        player.getInventory().selectedSlot = slot;
    }
}
