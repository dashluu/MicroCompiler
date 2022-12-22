package Symbols;

public class SymbolInfo {

    public enum SymbolType {
        UNKNOWN, ID, KEYWORD
    }

    protected final String id;
    protected final SymbolType symbolType;

    public SymbolInfo(String id, SymbolType symbolType) {
        this.id = id;
        this.symbolType = symbolType;
    }

    public String getId() {
        return id;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SymbolInfo info)) {
            return false;
        }
        return id.equals(info.id);
    }
}
