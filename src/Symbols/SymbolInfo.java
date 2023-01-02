package Symbols;

import Utilities.TokenType;

public class SymbolInfo {

    protected final String id;
    protected final TokenType idType;
    protected final SymbolType symbolType;

    public SymbolInfo(String id, TokenType idType, SymbolType symbolType) {
        this.id = id;
        this.idType = idType;
        this.symbolType = symbolType;
    }

    public String getId() {
        return id;
    }

    public TokenType getIdType() {
        return idType;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "symbol '" + id + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SymbolInfo symbol)) {
            return false;
        }
        return id.equals(symbol.id) && symbolType == symbol.symbolType;
    }
}
