package ParserSpace;

import Symbols.TypeInfo;

public class IDNode extends DataTypeNode {
    private final String id;
    private final boolean isDecl;

    public IDNode(NodeType nodeType, String id, boolean isDecl, TypeInfo dataType) {
        super(nodeType, dataType);
        this.id = id;
        this.isDecl = isDecl;
    }

    public String getId() {
        return id;
    }

    public boolean isDeclaration() {
        return isDecl;
    }
}
