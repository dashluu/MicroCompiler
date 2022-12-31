package Operators;

import Utilities.TokenType;

public class Operator {

    public enum OperatorType {
        NONE, UNARY, BINARY
    }

    private final TokenType id;
    private final int preced;
    private final boolean LeftToRight;
    private final OperatorType opType;

    public Operator(TokenType id, int preced, boolean LeftToRight, OperatorType opType) {
        this.id = id;
        this.preced = preced;
        this.LeftToRight = LeftToRight;
        this.opType = opType;
    }

    public TokenType getID() {
        return id;
    }

    public int getPreced() {
        return preced;
    }

    public boolean isLeftToRight() {
        return LeftToRight;
    }

    public OperatorType getType() {
        return opType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Operator op)) {
            return false;
        }
        return id == op.id;
    }
}
