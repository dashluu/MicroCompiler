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

    /**
     * Initializes the only instance of the type table if it has not been initialized and then returns it.
     *
     * @return a TypeTable object.
     */
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
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.MULT, intIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.MULT, intFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.MULT, floatIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.MULT, floatFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.DIV, intIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.DIV, intFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.DIV, floatIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.DIV, floatFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ASSIGNMENT, intIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ASSIGNMENT, intFloatConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ASSIGNMENT, floatIntConversion));
            typeTable.setOperatorTypeConversion(new BinaryOperatorTypeConversion(TokenType.ASSIGNMENT, floatFloatConversion));

            init = true;
        }
        return typeTable;
    }

    /**
     * Inserts a new type conversion into the type conversion map if it does not exist, and replaces the old one if it does.
     *
     * @param typeConversion the type conversion to be set.
     * @return the old type conversion object if it exists and null otherwise.
     */
    private TypeConversion setTypeConversion(TypeConversion typeConversion) {
        return typeConversions.put(typeConversion, typeConversion);
    }

    /**
     * Inserts a new operator-type conversion into the operator-type conversion map if it does not exist,
     * and replaces the old one if it does.
     *
     * @param opTypeConversion the operator-type conversion to be set.
     * @return the old operator-type conversion object if it exists and null otherwise.
     */
    private boolean setOperatorTypeConversion(OperatorTypeConversion opTypeConversion) {
        return opTypeConversions.add(opTypeConversion);
    }

    /**
     * Determines if a data type is compatible with a certain operator.
     *
     * @param opId       the operator ID.
     * @param targetType the data type to be checked.
     * @return true if the given data type is compatible with the given operator and false otherwise.
     */
    public boolean IsTypeCompatibleUsingUnaryOperator(TokenType opId, TypeInfo targetType) {
        UnaryOperatorTypeConversion dummy = new UnaryOperatorTypeConversion(opId, targetType);
        return opTypeConversions.contains(dummy);
    }

    /**
     * Determines if two data types are compatible using a certain operator.
     *
     * @param opId      the operator ID.
     * @param leftType  the left-hand side data type.
     * @param rightType the right-hand side data type.
     * @return true if the two given data types are compatible using the given operator and false otherwise.
     */
    public boolean AreTypesCompatibleUsingBinaryOperator(TokenType opId, TypeInfo leftType, TypeInfo rightType) {
        TypeConversion dummyTypeConversion = new TypeConversion(leftType, rightType, null, false);
        BinaryOperatorTypeConversion dummyOpTypeConversion = new BinaryOperatorTypeConversion(opId, dummyTypeConversion);
        return opTypeConversions.contains(dummyOpTypeConversion);
    }

    /**
     * Retrieves a type conversion with the given left-hand side type and right-hand side type.
     *
     * @param leftType  the left-hand side data type.
     * @param rightType the right-hand side data type.
     * @return a type conversion object if it exists and null otherwise.
     */
    public TypeConversion getTypeConversion(TypeInfo leftType, TypeInfo rightType) {
        TypeConversion dummy = new TypeConversion(leftType, rightType, null, false);
        return typeConversions.get(dummy);
    }

    /**
     * Determines if a type conversion between the given left-hand side type and right-hand side type is implicit.
     *
     * @param leftType  the left-hand side data type.
     * @param rightType the right-hand side data type.
     * @return true if the conversion is implicit and false otherwise.
     */
    public boolean isTypeConversionImplicit(TypeInfo leftType, TypeInfo rightType) {
        TypeConversion typeConversion = getTypeConversion(leftType, rightType);
        return typeConversion.isImplicit();
    }
}
