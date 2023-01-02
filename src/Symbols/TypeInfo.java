package Symbols;

import Utilities.TokenType;

public class TypeInfo extends SymbolInfo {
    public TypeInfo(String id, TokenType idType) {
        super(id, idType, SymbolType.TYPE);
    }

    @Override
    public String toString() {
        return "type '" + id + "'";
    }
}
