package com.aiplayer.ai;

/**
 * 条件节点 - 检查特定条件是否满足
 */
public class ConditionNode extends BehaviorNode {
    private final ConditionChecker checker;

    @FunctionalInterface
    public interface ConditionChecker {
        boolean check();
    }

    public ConditionNode(String name, ConditionChecker checker) {
        super(name);
        this.checker = checker;
    }

    @Override
    public boolean execute() {
        return checker.check();
    }
}
