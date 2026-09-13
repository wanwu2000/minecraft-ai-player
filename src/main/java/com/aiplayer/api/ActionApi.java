package com.aiplayer.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface ActionApi {
    void swingArm(net.minecraft.entity.player.PlayerEntity player, Hand hand);
    boolean isHoldingItem(net.minecraft.entity.player.PlayerEntity player);
    ItemStack getHeldItem(net.minecraft.entity.player.PlayerEntity player, Hand hand);
    void equipItem(net.minecraft.entity.player.PlayerEntity player, int slot);
}
