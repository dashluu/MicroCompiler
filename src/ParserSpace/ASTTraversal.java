package ParserSpace;

import Utilities.Node;

public class ASTTraversal {
    private final IASTNodeTraversal nodeTraversal;

    public ASTTraversal(IASTNodeTraversal nodeTraversal) {
        this.nodeTraversal = nodeTraversal;
    }

    /**
     * Traverse the AST using preorder traversal.
     *
     * @param node the starting AST node.
     */
    public void preorder(Node node) {
        if (node != null) {
            nodeTraversal.traverse(node);
            for (int i = 0; i < node.getNumChildren(); ++i) {
                preorder(node.getChild(i));
            }
        }
    }

    /**
     * Traverse the AST using postorder traversal.
     *
     * @param node the starting AST node.
     */
    public void postorder(Node node) {
        if (node != null) {
            for (int i = 0; i < node.getNumChildren(); ++i) {
                postorder(node.getChild(i));
            }
            nodeTraversal.traverse(node);
        }
    }
}
