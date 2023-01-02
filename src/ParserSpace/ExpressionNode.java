package ParserSpace;

import Symbols.TypeInfo;
import Utilities.Token;
import Utilities.TokenType;

public class ExpressionNode extends DataTypeNode {

    private final String value;
    private final TokenType valueType;

    public ExpressionNode(String value, TokenType valueType) {
        this(value, valueType, null);
    }

    public ExpressionNode(String value, TokenType valueType, TypeInfo dataType) {
        super(NodeType.EXPR);
        this.value = value;
        this.valueType = valueType;
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public TokenType getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", value: " + value +
                ", value type: " + valueType;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof ExpressionNode expressionNode)) {
            return false;
        }
        return value.equals(expressionNode.value) && valueType == expressionNode.valueType;
    }

}
