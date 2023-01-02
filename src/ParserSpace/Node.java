package ParserSpace;

import java.util.ArrayList;

public class Node {

    private final NodeType nodeType;
    private final ArrayList<Node> children = new ArrayList<>();

    public Node(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public Node getChild(int index) {
        if (index < 0 || index >= children.size()) {
            throw new IndexOutOfBoundsException("Invalid index for the child node");
        }
        return children.get(index);
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public int getNumChildren() {
        return children.size();
    }

    @Override
    public String toString() {
        return "node of type '" + nodeType + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node node)) {
            return false;
        }
        return nodeType == node.nodeType;
    }

}
