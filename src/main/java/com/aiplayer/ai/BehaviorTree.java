package com.aiplayer.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * 行为树 - AI决策的核心
 */
public class BehaviorTree {
    private final BehaviorNode root;
    private final List<String> executionLog = new ArrayList<>();

    public BehaviorTree(BehaviorNode root) {
        this.root = root;
    }

    /**
     * 执行一次行为树
     */
    public boolean run() {
        executionLog.clear();
        boolean result = root.execute();
        log("Root result: " + result);
        return result;
    }

    /**
     * 重置行为树状态
     */
    public void reset() {
        resetNode(root);
    }

    private void resetNode(BehaviorNode node) {
        if (node instanceof RepeatNode) {
            ((RepeatNode) node).reset();
        }
        // 递归重置子节点
        if (node instanceof SequenceNode) {
            SequenceNode seq = (SequenceNode) node;
            for (int i = 0; i < seq.getChildCount(); i++) {
                resetNode(seq.getChild(i));
            }
        } else if (node instanceof SelectorNode) {
            SelectorNode sel = (SelectorNode) node;
            for (int i = 0; i < sel.getChildCount(); i++) {
                resetNode(sel.getChild(i));
            }
        }
    }

    /**
     * 记录执行日志
     */
    private void log(String message) {
        executionLog.add(message);
        // 保留最近100条日志
        if (executionLog.size() > 100) {
            executionLog.remove(0);
        }
    }

    /**
     * 获取执行日志
     */
    public List<String> getExecutionLog() {
        return new ArrayList<>(executionLog);
    }

    /**
     * 清空执行日志
     */
    public void clearLog() {
        executionLog.clear();
    }

    public BehaviorNode getRoot() {
        return root;
    }
}
