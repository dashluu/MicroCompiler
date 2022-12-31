package Symbols;

import Utilities.TokenType;

public class KeywordInfo extends SymbolInfo {
    public KeywordInfo(String id, TokenType idType) {
        super(id, idType, SymbolType.KEYWORD);
    }
}
