package Symbols;

import Utilities.Token;

public class OperatorInfo extends SymbolInfo {
    public OperatorInfo(Token token) {
        super(token, SymbolType.OPERATOR);
    }
}
