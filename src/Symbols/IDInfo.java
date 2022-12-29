package Symbols;

import Utilities.Block;
import Utilities.Token;

public class IDInfo extends SymbolInfo {

    private final Block scope;
    private final boolean mutable;

    public IDInfo(Token token, Block scope, boolean mutable) {
        super(token, SymbolType.ID);
        this.scope = scope;
        this.mutable = mutable;
    }

    public Block getScope() {
        return scope;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public int hashCode() {
        String hashStr = scope.id() + ":" + token.getValue();
        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        IDInfo info = (IDInfo) obj;
        return scope.equals(info.getScope());
    }
}
