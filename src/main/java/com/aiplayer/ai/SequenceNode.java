package com.aiplayer.ai;

/**
 * 顺序执行节点 - 所有子节点成功才返回成功
 */
public class SequenceNode extends BehaviorNode {
    private final java.util.List<BehaviorNode> children;

    public SequenceNode(String name, java.util.List<BehaviorNode> children) {
        super(name);
        this.children = children;
        children.forEach(c -> c.setParent(this));
    }

    @Override
    public boolean execute() {
        for (BehaviorNode child : children) {
            if (!child.execute()) {
                return false;
            }
        }
        return true;
    }

    public int getChildCount() {
        return children.size();
    }

    public BehaviorNode getChild(int index) {
        return children.get(index);
    }
}
