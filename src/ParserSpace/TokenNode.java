package ParserSpace;

import Utilities.Token;

public class TokenNode extends Node {

    private final Token token;

    public TokenNode(Token token) {
        super(NodeType.TOKEN);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + token.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        TokenNode tokenNode = (TokenNode) obj;
        return token.equals(tokenNode.token);
    }

}
