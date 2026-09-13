package com.aiplayer.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface ActionApi {
    void attackEntity(net.minecraft.entity.Entity entity);
    void breakBlock(BlockPos pos);
    void placeBlock(BlockPos pos, ItemStack item);
    void useItem(ItemStack item);
    void openInventory();
    void closeInventory();
    void swapHotbarSlot(int slot);
}
