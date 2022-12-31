package Types;

import Symbols.SymbolTable;
import Symbols.TypeInfo;
import Utilities.Global;
import Utilities.TokenType;

import java.util.HashMap;
import java.util.HashSet;

public class TypeTable {
    private final HashSet<OperatorTypeConversion> opTypeConversions = new HashSet<>();
    private final HashMap<TypeConversion, TypeConversion> typeConversions = new HashMap<>();
    private final static TypeTable typeTable = new TypeTable();
    private static boolean init = false;

    private TypeTable() {
    }

    public static TypeTable getInstance() {
        if (!init) {
            SymbolTable symbolTable = SymbolTable.getInstance();
            TypeInfo intType = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
            TypeInfo floatType = (TypeInfo) symbolTable.getType(Global.FLOAT_TYPE_ID);

            // Initialize type conversions
            TypeConversion intIntConversion = new TypeConversion(intType, intType, intType, true);
            TypeConversion intFloatConversion = new TypeConversion(intType, floatType, floatType, true);
            TypeConversion floatIntConversion = new TypeConversion(floatType, intType, floatType, true);
            TypeConversion floatFloatConversion = new TypeConversion(floatType, floatType, floatType, true);
            typeTable.setTypeConversion(intFloatConversion);
            typeTable.setTypeConversion(floatIntConversion);

            // Initialize operator-type conversions
            typeTable.setOperatorTypeConversion(new UnaryOperatorTypeConversion(TokenType.PLUS, intType));
            typeTable.setOperatorTypeConversion(new UnaryOperatorTypeConversion(TokenType.MINUS, intType));
            typeTable.setOperatorTypeConversion(new UnaryOperatorTypeConversion(TokenType.PLUS, floatType));
            typeTable.setOperatorTypeConversion(new UnaryOperatorTypeConversion(TokenType.MINUS, floatType));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ADD, intIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ADD, intFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ADD, floatIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ADD, floatFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.SUB, intIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.SUB, intFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.SUB, floatIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.SUB, floatFloatConversion));

            init = true;
        }
        return typeTable;
    }

    private TypeConversion setTypeConversion(TypeConversion typeConversion) {
        return typeConversions.put(typeConversion, typeConversion);
    }

    private boolean setOperatorTypeConversion(OperatorTypeConversion opTypeConversion) {
        return opTypeConversions.add(opTypeConversion);
    }

    public boolean IsTypeCompatibleUsingUnaryOperator(TokenType opId, TypeInfo targetType) {
        UnaryOperatorTypeConversion dummy = new UnaryOperatorTypeConversion(opId, targetType);
        return opTypeConversions.contains(dummy);
    }

    public boolean AreTypesCompatibleUsingBinaryOperator(TokenType opId, TypeInfo leftType, TypeInfo rightType) {
        TypeConversion dummyTypeConversion = new TypeConversion(leftType, rightType, null, false);
        BinaryOperatorTypeConversion dummyOpTypeConversion = new BinaryOperatorTypeConversion(opId, dummyTypeConversion);
        return opTypeConversions.contains(dummyOpTypeConversion);
    }

    public TypeConversion getTypeConversion(TypeInfo leftType, TypeInfo rightType) {
        TypeConversion dummy = new TypeConversion(leftType, rightType, null, false);
        return typeConversions.get(dummy);
    }

    public boolean isTypeConversionImplicit(TypeInfo leftType, TypeInfo rightType) {
        TypeConversion typeConversion = getTypeConversion(leftType, rightType);
        return typeConversion.isImplicit();
    }
}
