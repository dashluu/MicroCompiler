package Utilities;

import java.util.ArrayList;

public class Node {
    private final Token token;
    private final ArrayList<Node> children = new ArrayList<>();

    public Node(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
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

    @Override
    public String toString() {
        return token.toString();
    }
}
