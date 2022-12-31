package Types;

import Utilities.TokenType;

public class BinaryOperatorTypeConversion extends OperatorTypeConversion {

    private final TypeConversion typeConversion;

    public BinaryOperatorTypeConversion(TokenType opId, TypeConversion typeConversion) {
        super(opId);
        this.typeConversion = typeConversion;
    }

    public TypeConversion getTypeConversion() {
        return typeConversion;
    }

    @Override
    public int hashCode() {
        String hashStr = opId.name() +
                typeConversion.getLeftType().getToken().getType().name() +
                typeConversion.getRightType().getToken().getType().name();
        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof BinaryOperatorTypeConversion binOpTypeConversion)) {
            return false;
        }
        return typeConversion.equals(binOpTypeConversion.typeConversion);
    }
}
