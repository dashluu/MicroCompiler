package Symbols;

import Utilities.Block;
import Utilities.Token;

public class IDInfo extends SymbolInfo {

    private final Block scope;

    public IDInfo(Token token, Block scope) {
        super(token, SymbolType.ID);
        this.scope = scope;
    }

    public Block getScope() {
        return scope;
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
