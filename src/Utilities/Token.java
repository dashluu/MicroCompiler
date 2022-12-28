package Utilities;

public class Token {

    private final String value;
    private TokenType tokenType;

    public Token(String value, TokenType tokenType) {
        this.value = value;
        this.tokenType = tokenType;
    }

    public Token(String value) {
        this(value, TokenType.UNKNOWN);
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return tokenType;
    }

    public void setType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return value + ": " + tokenType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Token token)) {
            return false;
        }
        return value.equals(token.value) && tokenType == token.tokenType;
    }
}
