package com.aiplayer.client;

import com.aiplayer.chat.ChatSystem;
import com.aiplayer.human.Humanizer;
import com.aiplayer.learning.LearningSystem;
import com.aiplayer.comet.CometBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * AI 控制器 - 纯客户端实现
 */
public class AiController {
    private enum State { IDLE, MOVING, MINING, BUILDING, FIGHTING, CHATTING, NAVIGATING }
    private State currentState = State.IDLE;
    
    private final ChatSystem chatSystem;
    private final CometBridge cometBridge;
    private final Humanizer humanizer;
    private final LearningSystem learningSystem;
    
    private final Random random = new Random();
    private BlockPos currentTarget = null;
    private long lastMoveTime = 0;
    private static final long MOVE_INTERVAL = 100L;

    public AiController(ChatSystem chatSystem, CometBridge cometBridge, 
                       Humanizer humanizer, LearningSystem learningSystem) {
        this.chatSystem = chatSystem;
        this.cometBridge = cometBridge;
        this.humanizer = humanizer;
        this.learningSystem = learningSystem;
    }

    /**
     * 主循环 - 每帧调用
     */
    public void tick(PlayerEntity player) {
        if (player == null || player.getWorld() == null) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < humanizer.getReactionDelay()) return;
        
        // 检查是否需要失误
        if (humanizer.shouldMakeMistake()) {
            handleMistake(player);
            return;
        }
        
        // 根据当前状态执行行为
        switch (currentState) {
            case IDLE:
                handleIdle(player);
                break;
            case MOVING:
                handleMovement(player);
                break;
            case MINING:
                handleMining(player);
                break;
            case BUILDING:
                handleBuilding(player);
                break;
            case FIGHTING:
                handleFighting(player);
                break;
            case NAVIGATING:
                handleNavigation(player);
                break;
        }
        
        lastMoveTime = currentTime;
    }

    /**
     * 空闲状态 - 随机探索
     */
    private void handleIdle(PlayerEntity player) {
        // 随机决定是否开始移动
        if (random.nextBoolean()) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 5 + random.nextDouble() * 10;
            double targetX = player.getX() + Math.cos(angle) * distance;
            double targetZ = player.getZ() + Math.sin(angle) * distance;
            setCurrentTarget(targetX, player.getY(), targetZ);
            currentState = State.MOVING;
        }
        
        // 夜晚寻找庇护所
        if (player.getWorld().isNight() && random.nextInt(100) == 0) {
            findShelter(player);
        }
        
        // 饥饿时寻找食物
        if (player.getHungerManager().getFoodLevel() < 6) {
            findFood(player);
        }
    }

    /**
     * 移动状态
     */
    private void handleMovement(PlayerEntity player) {
        if (currentTarget == null) {
            currentState = State.IDLE;
            return;
        }
        
        double dx = currentTarget.getX() - player.getX();
        double dz = currentTarget.getZ() - player.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        
        // 到达目标
        if (distance < 1.5) {
            currentState = State.IDLE;
            currentTarget = null;
            player.input.movementForward = 0;
            return;
        }
        
        // 计算朝向
        float yaw = (float) (Math.atan2(dx, dz) * 180.0 / Math.PI);
        player.setYaw(yaw);
        
        // 应用移动
        player.input.movementForward = 1.0f;
        
        // 跳跃逻辑
        if (distance < 3 && player.isOnGround() && random.nextBoolean()) {
            player.jump();
        }
    }

    /**
     * 挖矿状态
     */
    private void handleMining(PlayerEntity player) {
        // TODO: 实现挖矿逻辑
        chatSystem.updateMood(2);
        humanizer.addFatigue(1);
        
        if (random.nextInt(20) == 0) {
            currentState = State.IDLE;
            chatSystem.clearCurrentTask();
            learningSystem.recordSuccess("mine");
        }
    }

    /**
     * 建造状态
     */
    private void handleBuilding(PlayerEntity player) {
        // 使用彗星Scaffold模块
        if (cometBridge.isAvailable()) {
            cometBridge.enableModule("scaffold");
        }
        
        chatSystem.updateMood(3);
        humanizer.addFatigue(2);
        
        if (random.nextInt(30) == 0) {
            currentState = State.IDLE;
            chatSystem.clearCurrentTask();
            learningSystem.recordSuccess("build");
        }
    }

    /**
     * 战斗状态
     */
    private void handleFighting(PlayerEntity player) {
        PlayerEntity target = findNearestEnemy(player, 8.0);
        
        if (target != null) {
            // 面向目标
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            float yaw = (float) (Math.atan2(dx, dz) * 180.0 / Math.PI);
            player.setYaw(yaw);
            
            // 保持距离
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < 2.0) {
                player.input.movementForward = -1.0f;
            } else {
                player.input.movementForward = 1.0f;
            }
            
            // 攻击
            if (distance < 3.0 && random.nextInt(5) == 0) {
                player.attack(target);
            }
        } else {
            currentState = State.IDLE;
        }
    }

    /**
     * 导航状态 - 使用Baritone
     */
    private void handleNavigation(PlayerEntity player) {
        if (cometBridge.isAvailable() && currentTarget != null) {
            cometBridge.setBaritoneTarget(currentTarget.getX(), currentTarget.getY(), currentTarget.getZ());
            currentState = State.IDLE;
            chatSystem.clearCurrentTask();
        }
    }

    /**
     * 处理失误
     */
    private void handleMistake(PlayerEntity player) {
        int mistakeType = random.nextInt(3);
        switch (mistakeType) {
            case 0: // 随机转向
                player.setYaw(player.getYaw() + (random.nextFloat() - 0.5f) * 90);
                break;
            case 1: // 随机跳跃
                if (player.isOnGround()) {
                    player.jump();
                }
                break;
            case 2: // 随机前后移动
                player.input.movementForward = random.nextBoolean() ? 1.0f : -1.0f;
                break;
        }
    }

    /**
     * 寻找最近敌人
     */
    private PlayerEntity findNearestEnemy(PlayerEntity player, double range) {
        World world = player.getWorld();
        PlayerEntity nearest = null;
        double nearestDist = range;
        
        for (Entity entity : world.getEntitiesByClass(PlayerEntity.class, 
                player.getBoundingBox().expand(range), e -> e != player)) {
            double dist = entity.squaredDistanceTo(player);
            if (dist < nearestDist * nearestDist) {
                nearest = (PlayerEntity) entity;
                nearestDist = Math.sqrt(dist);
            }
        }
        
        return nearest;
    }

    /**
     * 寻找庇护所
     */
    private void findShelter(PlayerEntity player) {
        // TODO: 实现寻找庇护所逻辑
    }

    /**
     * 寻找食物
     */
    private void findFood(PlayerEntity player) {
        // TODO: 实现寻找食物逻辑
    }

    private void setCurrentTarget(double x, double y, double z) {
        this.currentTarget = new BlockPos((int)x, (int)y, (int)z);
    }

    // Getters
    public State getCurrentState() { return currentState; }
    public ChatSystem getChatSystem() { return chatSystem; }
    public LearningSystem getLearningSystem() { return learningSystem; }
}
