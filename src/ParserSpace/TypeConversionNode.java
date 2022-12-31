package ParserSpace;

import Symbols.TypeInfo;

public class TypeConversionNode extends Node {

    private final TypeInfo targetType;

    public TypeConversionNode(TypeInfo targetType) {
        super(NodeType.TYPE_CONVERSION);
        this.targetType = targetType;
    }

    public TypeInfo getTargetType() {
        return targetType;
    }

}
