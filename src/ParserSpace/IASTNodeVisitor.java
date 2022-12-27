package ParserSpace;

import Utilities.Node;

public interface IASTNodeVisitor {
    void visit(Node node);

    void backtrack(Node node);
}
