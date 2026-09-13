package com.aiplayer.ai;

import java.util.Random;

/**
 * 随机节点 - 随机选择一个子节点执行
 */
public class RandomNode extends BehaviorNode {
    private static final Random RANDOM = new Random();
    private final java.util.List<BehaviorNode> children;

    public RandomNode(String name, java.util.List<BehaviorNode> children) {
        super(name);
        this.children = children;
        children.forEach(c -> c.setParent(this));
    }

    @Override
    public boolean execute() {
        if (children.isEmpty()) return true;
        BehaviorNode child = children.get(RANDOM.nextInt(children.size()));
        return child.execute();
    }

    public int getChildCount() {
        return children.size();
    }
}
