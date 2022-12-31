package ParserSpace;

import Symbols.TypeInfo;
import Utilities.Token;

public class TokenNode extends Node {

    private final Token token;
    private TypeInfo type;

    public TokenNode(Token token) {
        this(token, null);
    }

    public TokenNode(Token token, TypeInfo type) {
        super(NodeType.TOKEN);
        this.token = token;
        this.type = type;
    }

    public Token getToken() {
        return token;
    }

    public TypeInfo getType() {
        return type;
    }

    public void setType(TypeInfo type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + token.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !(obj instanceof TokenNode tokenNode)) {
            return false;
        }
        return token.equals(tokenNode.token);
    }

}
