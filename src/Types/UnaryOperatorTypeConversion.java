package Types;

import Symbols.TypeInfo;
import Utilities.TokenType;

public class UnaryOperatorTypeConversion extends OperatorTypeConversion {

    private final TypeInfo targetType;

    public UnaryOperatorTypeConversion(TokenType opId, TypeInfo targetType) {
        super(opId);
        this.targetType = targetType;
    }

    public TypeInfo getTargetType() {
        return targetType;
    }

    @Override
    public int hashCode() {
        String hashStr = opId.name() + targetType.getToken().getType().name();
        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof UnaryOperatorTypeConversion unOpTypeConversion)) {
            return false;
        }
        return targetType.equals(unOpTypeConversion.targetType);
    }

}
