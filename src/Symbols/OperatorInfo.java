package Symbols;

import Utilities.Token;

public class OperatorInfo extends SymbolInfo {

    private final int preced;
    private final boolean LeftToRight;

    public OperatorInfo(Token token, int preced, boolean LeftToRight) {
        super(token, SymbolType.OPERATOR);
        this.preced = preced;
        this.LeftToRight = LeftToRight;
    }

    public int getPreced() {
        return preced;
    }

    public boolean isLeftToRight() {
        return LeftToRight;
    }
}
