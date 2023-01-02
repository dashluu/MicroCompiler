package Operators;

import Utilities.TokenType;

import java.util.HashMap;

public class OperatorTable {
    private final HashMap<TokenType, Operator> operators = new HashMap<>();
    private final HashMap<TokenType, TokenType> binaryToUnaryOps = new HashMap<>();
    private static final OperatorTable opTable = new OperatorTable();
    private static boolean init = false;

    private OperatorTable() {
    }

    /**
     * Initializes the only instance of the operator table if it has not been initialized and then returns it.
     *
     * @return an OperatorTable object.
     */
    public static OperatorTable getInstance() {
        if (!init) {
            // Initialize the operator table
            opTable.set(new Operator(TokenType.ADD, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(TokenType.SUB, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(TokenType.PLUS, 2, false, Operator.OperatorType.UNARY));
            opTable.set(new Operator(TokenType.MINUS, 2, false, Operator.OperatorType.UNARY));
            opTable.set(new Operator(TokenType.MULT, 1, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(TokenType.DIV, 1, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(TokenType.DOT, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(TokenType.COLON, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(TokenType.LPAREN, 0, true, Operator.OperatorType.NONE));
            opTable.set(new Operator(TokenType.RPAREN, 0, true, Operator.OperatorType.NONE));
            opTable.set(new Operator(TokenType.SEMICOLON, 0, false, Operator.OperatorType.NONE));
            opTable.set(new Operator(TokenType.ASSIGNMENT, 0, false, Operator.OperatorType.BINARY));

            // Initialize mapping from binary to unary operators
            opTable.binaryToUnaryOps.put(TokenType.ADD, TokenType.PLUS);
            opTable.binaryToUnaryOps.put(TokenType.SUB, TokenType.MINUS);

            init = true;
        }
        return opTable;
    }

    /**
     * Gets the operator in the table associated with the given ID.
     *
     * @param opID the operator ID.
     * @return the operator associated with the given ID.
     */
    private Operator get(TokenType opID) {
        return operators.get(opID);
    }

    /**
     * Inserts a new operator into the table if it does not exist, otherwise, replace the old operator with the new one.
     *
     * @param op the operator to be set.
     * @return the old operator if one exists and null otherwise.
     */
    public Operator set(Operator op) {
        return operators.put(op.getID(), op);
    }

    /**
     * Determines if a token whose type is given is an operator.
     *
     * @param tokenType type of the token.
     * @return true if the token is an operator and false otherwise.
     */
    public boolean isOperator(TokenType tokenType) {
        return get(tokenType) != null;
    }

    /**
     * Determines if an operator is binary.
     *
     * @param opID the operator ID.
     * @return true if the operator is binary and false otherwise.
     */
    public boolean isOperatorBinary(TokenType opID) {
        Operator op = opTable.get(opID);
        return op.getType() == Operator.OperatorType.BINARY;
    }

    /**
     * Determines if an operator is unary.
     *
     * @param opID the operator ID.
     * @return true if the operator is unary and false otherwise.
     */
    public boolean isOperatorUnary(TokenType opID) {
        Operator op = opTable.get(opID);
        return op.getType() == Operator.OperatorType.UNARY;
    }

    /**
     * Determines if an operator is left-to-right.
     *
     * @param opID the operator ID.
     * @return true if the operator is left-to-right and false otherwise.
     */
    public boolean isOperatorLeftToRight(TokenType opID) {
        Operator op = opTable.get(opID);
        return op.isLeftToRight();
    }

    /**
     * Compares two operators' precedences. If they have the same precedence, compare them using the left-to-right rule.
     *
     * @param opID1 the first operator ID.
     * @param opID2 the second operator ID.
     * @return -1, 0, 1 if the first operator's precedence is less than, equal to, or greater than
     * that of the second operator respectively.
     */
    public int compareOperatorPreced(TokenType opID1, TokenType opID2) {
        Operator op1 = opTable.get(opID1);
        Operator op2 = opTable.get(opID2);
        int precedCmp = Integer.compare(op1.getPreced(), op2.getPreced());
        if (precedCmp != 0) {
            // If the comparison is non-zero, return the comparison result
            return precedCmp;
        }
        // The precedences of the two operators are the same
        // If the first operator is left-to-right, it has higher precedence
        // Otherwise, the second operator has higher precedence
        return op1.isLeftToRight() ? 1 : -1;
    }

    /**
     * Maps a binary operator to a unary operator with the same string representation.
     *
     * @param opID the operator's binary ID.
     * @return the operator's unary ID.
     */
    public TokenType mapBinaryToUnaryOperator(TokenType opID) {
        return binaryToUnaryOps.get(opID);
    }
}
