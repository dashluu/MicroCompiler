package Symbols;

import Utilities.TokenType;

public class OperatorInfo extends SymbolInfo {
    public OperatorInfo(String id, TokenType idType) {
        super(id, idType, SymbolType.OPERATOR);
    }
}
