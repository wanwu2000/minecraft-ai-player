package com.aiplayer.impl.v1_21;

import com.aiplayer.api.ActionApi;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Minecraft 1.21 版本行动 API 实现
 */
public class ActionApiImpl implements ActionApi {
    @Override
    public void attackEntity(net.minecraft.entity.Entity entity) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null && entity != null) {
            client.player.attack(entity);
        }
    }

    @Override
    public void breakBlock(BlockPos pos) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            client.interactionManager.breakBlock(pos);
        }
    }

    @Override
    public void placeBlock(BlockPos pos, ItemStack item) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            client.interactionManager.placeBlock(
                client.world,
                client.player,
                client.world,
                client.player.getMainHandStack(),
                net.minecraft.util.hit.BlockHitResult.ofPosAndSide(
                    pos,
                    net.minecraft.util.math.Direction.UP,
                    false
                )
            );
        }
    }

    @Override
    public void useItem(ItemStack item) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.useItem();
        }
    }

    @Override
    public void openInventory() {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.openInventory();
        }
    }

    @Override
    public void closeInventory() {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.closeScreen();
        }
    }

    @Override
    public void swapHotbarSlot(int slot) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.getInventory().selectedSlot = slot;
        }
    }
}
