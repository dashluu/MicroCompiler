package Operators;

import Utilities.Token;

import java.util.HashMap;

public class OperatorTable {
    private final HashMap<Token.TokenType, Operator> operators = new HashMap<>();
    private final HashMap<Token.TokenType, Token.TokenType> binaryToUnaryOps = new HashMap<>();
    private static OperatorTable opTable;
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
            opTable = new OperatorTable();
            opTable.set(new Operator(Token.TokenType.ADD, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(Token.TokenType.SUB, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(Token.TokenType.PLUS, 2, false, Operator.OperatorType.UNARY));
            opTable.set(new Operator(Token.TokenType.MINUS, 2, false, Operator.OperatorType.UNARY));
            opTable.set(new Operator(Token.TokenType.MULT, 1, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(Token.TokenType.DIV, 1, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(Token.TokenType.DOT, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(Token.TokenType.COLON, 0, true, Operator.OperatorType.BINARY));
            opTable.set(new Operator(Token.TokenType.LPAREN, 0, true, Operator.OperatorType.NONE));
            opTable.set(new Operator(Token.TokenType.RPAREN, 0, true, Operator.OperatorType.NONE));
            opTable.set(new Operator(Token.TokenType.SEMICOLON, 0, false, Operator.OperatorType.NONE));
            opTable.set(new Operator(Token.TokenType.ASSIGNMENT, 0, false, Operator.OperatorType.BINARY));

            // Initialize mapping from binary to unary operators
            opTable.binaryToUnaryOps.put(Token.TokenType.ADD, Token.TokenType.PLUS);
            opTable.binaryToUnaryOps.put(Token.TokenType.SUB, Token.TokenType.MINUS);

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
    private Operator get(Token.TokenType opID) {
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
    public boolean isOperator(Token.TokenType tokenType) {
        return get(tokenType) != null;
    }

    /**
     * Determines if an operator is binary.
     *
     * @param opID the operator ID.
     * @return true if the operator is binary and false otherwise.
     */
    public boolean isOperatorBinary(Token.TokenType opID) {
        Operator op = opTable.get(opID);
        return op.getType() == Operator.OperatorType.BINARY;
    }

    /**
     * Determines if an operator is unary.
     *
     * @param opID the operator ID.
     * @return true if the operator is unary and false otherwise.
     */
    public boolean isOperatorUnary(Token.TokenType opID) {
        return !isOperatorBinary(opID);
    }

    /**
     * Determines if an operator is left-to-right.
     *
     * @param opID the operator ID.
     * @return true if the operator is left-to-right and false otherwise.
     */
    public boolean isOperatorLeftToRight(Token.TokenType opID) {
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
    public int compareOperatorPreced(Token.TokenType opID1, Token.TokenType opID2) {
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

    public Token.TokenType mapBinaryToUnaryOperator(Token.TokenType opID) {
        return binaryToUnaryOps.get(opID);
    }
}
