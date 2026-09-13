package com.aiplayer.ai;

/**
 * 重复节点 - 循环执行子节点直到失败或达到限制
 */
public class RepeatNode extends BehaviorNode {
    private final BehaviorNode child;
    private int maxIterations;
    private int currentIteration = 0;

    public RepeatNode(String name, BehaviorNode child, int maxIterations) {
        super(name);
        this.child = child;
        child.setParent(this);
        this.maxIterations = maxIterations;
    }

    public RepeatNode(String name, BehaviorNode child) {
        this(name, child, Integer.MAX_VALUE);
    }

    @Override
    public boolean execute() {
        if (currentIteration >= maxIterations) {
            return false;
        }
        boolean result = child.execute();
        currentIteration++;
        return result;
    }

    public void reset() {
        currentIteration = 0;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    public int getMaxIterations() {
        return maxIterations;
    }
}
