package com.aiplayer.ai;

/**
 * Base behavior node for AI decision making.
 */
public abstract class BehaviorNode {
    protected String name;

    public BehaviorNode(String name) {
        this.name = name;
    }

    public abstract boolean execute();

    public String getName() {
        return name;
    }
}
