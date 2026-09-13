package com.aiplayer.impl.v1_26_1;

import com.aiplayer.api.ActionApi;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ActionApiImpl implements ActionApi {
    @Override
    public void attackEntity(net.minecraft.entity.Entity entity) { entity.attack(null); }
    @Override
    public void breakBlock(BlockPos pos) { }
    @Override
    public void placeBlock(BlockPos pos, ItemStack item) { }
    @Override
    public void useItem(ItemStack item) { }
    @Override
    public void openInventory() { }
    @Override
    public void closeInventory() { }
    @Override
    public void swapHotbarSlot(int slot) { }
}
