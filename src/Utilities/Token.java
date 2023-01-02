package Utilities;

public class Token {

    private final String value;
    private TokenType tokenType;
    private final int lineNumber;

    public Token(String value, TokenType tokenType, int lineNumber) {
        this.value = value;
        this.tokenType = tokenType;
        this.lineNumber = lineNumber;
    }

    public Token(String value, TokenType tokenType) {
        this(value, tokenType, 1);
    }

    public Token(String value) {
        this(value, TokenType.UNKNOWN, 1);
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return tokenType;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return "token '" + value + "' of type '" + tokenType + "'";
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
