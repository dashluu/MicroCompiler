package Types;

import Symbols.TypeInfo;

public class TypeConversion {
    private final TypeInfo leftType;
    private final TypeInfo rightType;
    private final TypeInfo resultType;
    private final boolean implicit;

    public TypeConversion(TypeInfo leftType, TypeInfo rightType, TypeInfo resultType, boolean implicit) {
        this.leftType = leftType;
        this.rightType = rightType;
        this.resultType = resultType;
        this.implicit = implicit;
    }

    public TypeInfo getLeftType() {
        return leftType;
    }

    public TypeInfo getRightType() {
        return rightType;
    }

    public TypeInfo getResultType() {
        return resultType;
    }

    public boolean isImplicit() {
        return implicit;
    }

    @Override
    public int hashCode() {
        String hashStr = leftType.getToken().getType().name() + rightType.getToken().getType().name();
        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeConversion typeConversion)) {
            return false;
        }
        return leftType.equals(typeConversion.leftType) && rightType.equals(typeConversion.rightType);
    }
}
