package Symbols;

import Utilities.Block;

public class IDInfo extends SymbolInfo {

    private final Block scope;

    public IDInfo(String id, Block scope) {
        super(id, SymbolType.ID);
        this.scope = scope;
    }

    public Block getScope() {
        return scope;
    }

    @Override
    public int hashCode() {
        String hashStr = id + ":" + scope.id();
        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IDInfo info)) {
            return false;
        }
        return id.equals(info.id) && scope.equals(info.scope);
    }
}
