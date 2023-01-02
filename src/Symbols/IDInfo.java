package Symbols;

import Utilities.Block;
import Utilities.TokenType;

public class IDInfo extends SymbolInfo {

    private final Block scope;
    private final TypeInfo dataType;
    private final boolean mutable;

    public IDInfo(String id, Block scope, TypeInfo dataType, boolean mutable) {
        super(id, TokenType.ID, SymbolType.ID);
        this.scope = scope;
        this.dataType = dataType;
        this.mutable = mutable;
    }

    public Block getScope() {
        return scope;
    }

    public TypeInfo getDataType() {
        return dataType;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public int hashCode() {
        String hashStr = scope.id() + ":" + id;
        return hashStr.hashCode();
    }

    @Override
    public String toString() {
        return "ID '" + id + "' in scope " + scope;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof IDInfo info)) {
            return false;
        }
        return scope.equals(info.getScope());
    }
}
