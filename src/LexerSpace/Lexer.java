package LexerSpace;

import Exceptions.SyntaxError;
import Symbols.SymbolInfo;
import Symbols.SymbolTable;
import Utilities.Token;
import Utilities.TokenType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;

public class Lexer {
    private final CharBuffer charBuffer;
    private final ArrayDeque<Token> tokenBuffer = new ArrayDeque<>();
    private final static String SPECIAL_CHARS = "()+-*/&|%<>=,.;:_";
    private final static int EOS = -1;
    private int currLine = 1;

    public Lexer(BufferedReader reader) {
        this.charBuffer = new CharBuffer(reader);
    }

    /**
     * Get the current line in the stream.
     *
     * @return the current line in the stream.
     */
    public int getCurrentLine() {
        return currLine;
    }

    public void putBack(Token token) {
        tokenBuffer.addFirst(token);
        currLine = token.getLineNumber();
    }

    public void putBack(ArrayDeque<Token> aTokenBuffer) {
        Token token;
        while (!aTokenBuffer.isEmpty()) {
            token = aTokenBuffer.removeLast();
            tokenBuffer.addFirst(token);
            currLine = token.getLineNumber();
        }
    }

    /**
     * Checks if lexer has reached the end of the stream.
     *
     * @return true if lexer has reached the end of the stream and false otherwise.
     * @throws IOException if the read operation causes an IO error.
     */
    public boolean isEOS() throws IOException {
        return charBuffer.peek() == EOS;
    }

    /**
     * Skips the spaces until a non-space character is encountered.
     *
     * @throws IOException if the read operation causes an IO error.
     */
    private void skipSpaces() throws IOException {
        short c;
        while ((c = charBuffer.peek()) != EOS && isSpace(c)) {
            if (c == '\n') {
                ++currLine;
            }
            charBuffer.read();
        }
    }

    /**
     * Determines if the character is a space.
     *
     * @param c the character to be checked.
     * @return true if the character is a space and false otherwise.
     */
    private boolean isSpace(short c) {
        return Character.isWhitespace(c);
    }

    /**
     * Determines if the character is an alphanumeric or an underscore.
     *
     * @param c the character to be checked.
     * @return true if the character is an alphanumeric or an underscore and false otherwise.
     */
    private boolean isAlnumUnderscore(short c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
    }

    /**
     * Determines if the character is a valid special character.
     *
     * @param c the character to be checked.
     * @return true if the character is a valid special character and false otherwise.
     */
    private boolean isSpecialChar(short c) {
        return SPECIAL_CHARS.indexOf(c) >= 0;
    }

    /**
     * Determines if the character is a valid separator.
     *
     * @param c the character to be checked.
     * @return true if the character is a valid separator and false otherwise.
     */
    private boolean isSeparator(short c) {
        return c == EOS || isSpace(c) || c == ';';
    }

    /**
     * Gets the next token in the stream.
     *
     * @return a token if one exists and null otherwise.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public Token getNextToken() throws SyntaxError, IOException {
        // Reads from the token buffer before extracting characters from the stream
        if (!tokenBuffer.isEmpty()) {
            return tokenBuffer.removeFirst();
        }

        skipSpaces();
        if (charBuffer.peek() == EOS) {
            return null;
        }

        Token token = getAlnumUnderscoreToken();
        SymbolTable symbolTable = SymbolTable.getInstance();

        if (token != null) {
            // Check if the token is a keyword, if it is, change its token type
            String tokenStr = token.getValue();
            SymbolInfo info = symbolTable.getKeyword(tokenStr);
            if (info != null) {
                token.setType(info.getIdType());
                return token;
            }
            // Check if the token is a type id, if it is, change its token type
            info = symbolTable.getType(tokenStr);
            if (info != null) {
                token.setType(info.getIdType());
                return token;
            }
            // Otherwise, keep it as an ID
            return token;
        }
        token = getScientificNumberToken();
        if (token != null) {
            return token;
        }
        token = getOperatorToken();
        if (token != null) {
            // Get the correct operator type using the symbol table
            SymbolInfo info = symbolTable.getOperator(token.getValue());
            if (info != null) {
                token.setType(info.getIdType());
                return token;
            }
            return token;
        }
        throw new SyntaxError("Unable to get next token because of invalid syntax", getCurrentLine());
    }

    /**
     * Reads alphanumeric and underscore characters into a string and stores it in a token.
     *
     * @return a token containing a string of alphanumeric and underscore characters.
     * @throws IOException if the read operation causes an error.
     * @throws SyntaxError if there is an invalid character.
     */
    private Token getAlnumUnderscoreToken() throws IOException, SyntaxError {
        short c;

        // Check if the first character is end of stream or a letter or '_'
        if ((c = charBuffer.peek()) == EOS || (!Character.isAlphabetic(c) && c != '_')) {
            return null;
        }

        StringBuilder tokenStr = new StringBuilder();
        boolean end = false;

        // Consume the character from the stream until it is a separator or a valid special character
        while (!isSeparator(c) && !end) {
            if (isAlnumUnderscore(c)) {
                tokenStr.append((char) c);
                charBuffer.read();
            } else if (isSpecialChar(c)) {
                end = true;
            } else {
                throw new SyntaxError("Invalid character '" + c + "' after '" + tokenStr + "'", getCurrentLine());
            }
            c = charBuffer.peek();
        }

        // The string cannot be empty
        // Set the type to ID and check it later
        return new Token(tokenStr.toString(), TokenType.ID, currLine);
    }

    /**
     * Reads a string and stores it in a new token if it matches the given string.
     *
     * @param strToMatch    the string to match.
     * @param readStrBuffer a buffer that stores the characters that have been read when this method runs.
     * @param tokenType     the type of the token to assign if one is present.
     * @return a token containing the string if the operation succeeds and null otherwise.
     * @throws IOException if there is an error while reading.
     */
    private Token getStrToken(String strToMatch, StringBuilder readStrBuffer, TokenType tokenType)
            throws IOException {
        // Clears the charBuffer
        readStrBuffer.setLength(0);

        short c;
        int i = 0;
        boolean end = false;

        while (i < strToMatch.length() && (c = charBuffer.peek()) != EOS && !end) {
            end = (char) c != strToMatch.charAt(i);
            if (!end) {
                readStrBuffer.append((char) c);
                ++i;
                charBuffer.read();
            }
        }

        if (!strToMatch.equals(readStrBuffer.toString())) {
            return null;
        }

        return new Token(strToMatch, tokenType, currLine);
    }

    /**
     * Reads an operator (can be multiple characters) and creates a token that stores the operator if the operation
     * succeeds.
     *
     * @return a token that stores the operator.
     * @throws IOException if the read operation causes an error.
     */
    private Token getOperatorToken() throws IOException {
        short c;
        StringBuilder tokenStr = new StringBuilder();
        String tempStr;
        boolean end = false;

        while ((c = charBuffer.peek()) != EOS && !end) {
            tempStr = tokenStr.toString() + (char) c;
            // Find the operator corresponding to the string
            end = !SymbolTable.getInstance().isOperator(tempStr);
            if (!end) {
                tokenStr.append((char) c);
                charBuffer.read();
            }
        }

        if (tokenStr.isEmpty()) {
            return null;
        }

        return new Token(tokenStr.toString(), TokenType.UNKNOWN, currLine);
    }

    /**
     * Reads a sequence of digits, adds them to a string and creates a token that contains
     * the string if successful.
     *
     * @return a token containing a sequence of digits.
     * @throws IOException if the read operation causes an error.
     */
    private Token getDigitSeqToken() throws IOException {
        short c;
        StringBuilder tokenStr = new StringBuilder();

        while ((c = charBuffer.peek()) != EOS && Character.isDigit(c)) {
            tokenStr.append((char) c);
            charBuffer.read();
        }

        if (tokenStr.isEmpty()) {
            return null;
        }

        return new Token(tokenStr.toString(), TokenType.INT, currLine);
    }

    /**
     * Reads a floating-point number and stores it in a new token if one exists.
     *
     * @return a token containing the floating-point number.
     * @throws SyntaxError if the numeric expression is invalid.
     * @throws IOException if the read operation causes an error.
     */
    private Token getNumberToken() throws SyntaxError, IOException {
        StringBuilder tokenStr = new StringBuilder();
        StringBuilder tempStr = new StringBuilder();

        // Reads the integer part
        Token intToken = getDigitSeqToken();
        boolean missingInt = intToken == null;
        if (missingInt) {
            tokenStr.append("0");
        } else {
            tokenStr.append(intToken.getValue());
        }

        // Reads '.'
        Token decPointToken = getStrToken(".", tempStr, TokenType.UNKNOWN);
        boolean missingDecPoint = decPointToken == null;
        if (!missingDecPoint) {
            tokenStr.append(".");
        } else {
            // Put back what has been read
            charBuffer.putBack(tempStr.toString());
        }

        if (missingInt && missingDecPoint) {
            return null;
        }

        // Reads the fraction part if there is a decimal point
        if (!missingDecPoint) {
            Token fractionToken = getDigitSeqToken();
            if (fractionToken == null) {
                tokenStr.append("0");
            } else {
                tokenStr.append(fractionToken.getValue());
            }
        }

        TokenType tokenType = missingDecPoint ? TokenType.INT : TokenType.FLOAT;
        return new Token(tokenStr.toString(), tokenType, currLine);
    }

    /**
     * Reads a scientific floating-point number and stores it in a new token if one exists.
     *
     * @return a token containing the scientific floating-point number.
     * @throws SyntaxError if the numeric expression is invalid.
     * @throws IOException if the read operation causes an error.
     */
    private Token getScientificNumberToken() throws IOException, SyntaxError {
        skipSpaces();

        // Get a floating-point number
        Token tempToken = getNumberToken();
        if (tempToken == null) {
            return null;
        }

        short c;
        StringBuilder tokenStr = new StringBuilder();
        StringBuilder tempStr = new StringBuilder();
        TokenType tokenType = tempToken.getType();

        tokenStr.append(tempToken.getValue());
        // Get 'e'
        tempToken = getStrToken("e", tempStr, TokenType.UNKNOWN);
        if (tempToken == null) {
            // Put back what has been read
            charBuffer.putBack(tempStr.toString());
            if ((c = charBuffer.peek()) == EOS || isSpace(c) || isSpecialChar(c) && c != '.') {
                return new Token(tokenStr.toString(), tokenType, currLine);
            } else {
                throw new SyntaxError("Invalid numeric expression after '" + tokenStr + "'", getCurrentLine());
            }
        }
        tokenStr.append("e");

        // Get +/-
        tempToken = getStrToken("+", tempStr, TokenType.UNKNOWN);
        if (tempToken == null) {
            // Put back what has been read
            charBuffer.putBack(tempStr.toString());
            // Reads '-' if '+' is not present
            tempToken = getStrToken("-", tempStr, TokenType.UNKNOWN);
            if (tempToken == null) {
                // Put back what has been read
                charBuffer.putBack(tempStr.toString());
            } else {
                tokenStr.append(tempToken.getValue());
            }
        } else {
            tokenStr.append(tempToken.getValue());
        }

        // Get the exponent
        tempToken = getNumberToken();
        if (tempToken == null) {
            throw new SyntaxError("Invalid numeric expression after '" + tokenStr + "'", getCurrentLine());
        }
        tokenStr.append(tempToken.getValue());
        return new Token(tokenStr.toString(), TokenType.FLOAT, currLine);
    }
}
