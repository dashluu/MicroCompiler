package Symbols;

import Utilities.Token;

public class KeywordInfo extends SymbolInfo {
    public KeywordInfo(Token token) {
        super(token, SymbolType.KEYWORD);
    }
}
