package com.aiplayer.ai;

/**
 * 任务节点 - 执行具体操作
 */
public class TaskNode extends BehaviorNode {
    private final TaskExecutor executor;

    @FunctionalInterface
    public interface TaskExecutor {
        boolean executeTask();
    }

    public TaskNode(String name, TaskExecutor executor) {
        super(name);
        this.executor = executor;
    }

    @Override
    public boolean execute() {
        try {
            return executor.executeTask();
        } catch (Exception e) {
            return false;
        }
    }
}
