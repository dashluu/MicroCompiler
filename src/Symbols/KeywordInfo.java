package Symbols;

public class KeywordInfo extends SymbolInfo {
    public enum KeywordType {
        ID_DECL, TYPE
    }

    private final KeywordType keywordType;

    public KeywordInfo(String id, KeywordType keywordType) {
        super(id, SymbolType.KEYWORD);
        this.keywordType = keywordType;
    }

    public KeywordType getKeywordType() {
        return keywordType;
    }
}
