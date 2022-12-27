package ParserSpace;

import Utilities.Node;

public class ASTTraversal {
    private final IASTNodeVisitor nodeVisitor;

    public ASTTraversal(IASTNodeVisitor nodeVisitor) {
        this.nodeVisitor = nodeVisitor;
    }

    /**
     * Traverse the AST by visiting each node in the tree.
     *
     * @param node the starting AST node.
     */
    public void traverse(Node node) {
        if (node != null) {
            nodeVisitor.visit(node);
            for (int i = 0; i < node.getNumChildren(); ++i) {
                traverse(node.getChild(i));
            }
            nodeVisitor.backtrack(node);
        }
    }
}
