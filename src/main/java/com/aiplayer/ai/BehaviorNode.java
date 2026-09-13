package com.aiplayer.ai;

/**
 * 行为树节点基类
 */
public abstract class BehaviorNode {
    protected String name;
    protected BehaviorNode parent;

    public BehaviorNode(String name) {
        this.name = name;
    }

    public abstract boolean execute();

    public String getName() {
        return name;
    }

    public void setParent(BehaviorNode parent) {
        this.parent = parent;
    }

    public BehaviorNode getParent() {
        return parent;
    }
}
