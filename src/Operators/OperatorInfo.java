package Operators;

public class OperatorInfo {
    public enum OperatorType {
        UNKNOWN, BINARY, LPAREN, RPAREN, COLON, SEMICOLON, DOT, ASSIGNMENT
    }

    private final String id;
    private final OperatorType operatorType;
    private final int preced;
    private final boolean isLeftToRight;

    public OperatorInfo(String id, OperatorType operatorType, int preced, boolean isLeftToRight) {
        this.id = id;
        this.operatorType = operatorType;
        this.preced = preced;
        this.isLeftToRight = isLeftToRight;
    }

    public String getId() {
        return id;
    }

    public OperatorType getType() {
        return operatorType;
    }

    public int getPreced() {
        return preced;
    }

    public boolean isLeftToRight() {
        return isLeftToRight;
    }
}
