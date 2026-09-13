package com.aiplayer.ai;

/**
 * 选择节点 - 第一个成功的子节点返回成功
 */
public class SelectorNode extends BehaviorNode {
    private final java.util.List<BehaviorNode> children;

    public SelectorNode(String name, java.util.List<BehaviorNode> children) {
        super(name);
        this.children = children;
        children.forEach(c -> c.setParent(this));
    }

    @Override
    public boolean execute() {
        for (BehaviorNode child : children) {
            if (child.execute()) {
                return true;
            }
        }
        return false;
    }

    public int getChildCount() {
        return children.size();
    }

    public BehaviorNode getChild(int index) {
        return children.get(index);
    }
}
