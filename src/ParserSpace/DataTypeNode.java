package ParserSpace;

import Symbols.TypeInfo;

public class DataTypeNode extends Node {
    protected TypeInfo dataType;

    public DataTypeNode(NodeType nodeType, TypeInfo dataType) {
        super(nodeType);
        this.dataType = dataType;
    }

    public DataTypeNode(NodeType nodeType) {
        this(nodeType, null);
    }

    public TypeInfo getDataType() {
        return dataType;
    }

    public void setDataType(TypeInfo dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return super.toString() + ", data of " + dataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof DataTypeNode dataTypeNode)) {
            return false;
        }
        return dataType == dataTypeNode.dataType;
    }
}
