package ParserSpace;

public interface IASTNodeVisitor {
    void visit(Node node);

    void backtrack(Node node);
}
