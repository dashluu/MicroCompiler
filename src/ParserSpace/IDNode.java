package ParserSpace;

import Symbols.TypeInfo;

public class IDNode extends DataTypeNode {
    private final String id;
    private final boolean mutable;

    public IDNode(NodeType nodeType, String id, TypeInfo dataType, boolean mutable) {
        super(nodeType, dataType);
        this.id = id;
        this.mutable = mutable;
    }

    public String getId() {
        return id;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public String toString() {
        return super.toString() + ", id '" + id + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof IDNode idNode)) {
            return false;
        }
        return id.equals(idNode.id);
    }
}
