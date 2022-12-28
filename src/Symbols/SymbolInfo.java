package Symbols;

import Utilities.Token;

public class SymbolInfo {

    protected final Token token;
    protected final SymbolType symbolType;

    public SymbolInfo(Token token, SymbolType symbolType) {
        this.token = token;
        this.symbolType = symbolType;
    }

    public Token getToken() {
        return token;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    @Override
    public int hashCode() {
        return token.getValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SymbolInfo symbol)) {
            return false;
        }
        return token.getValue().equals(symbol.getToken().getValue()) && symbolType == symbol.getSymbolType();
    }
}
