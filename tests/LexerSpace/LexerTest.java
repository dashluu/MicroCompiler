package LexerSpace;

import Exceptions.SyntaxError;
import Utilities.Token;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {
    private ArrayList<Token> testLexerHelper(String inputStr) throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        Token token;
        ArrayList<Token> actualTokens = new ArrayList<>();
        while ((token = lexer.getNextToken()) != null) {
            actualTokens.add(token);
        }
        return actualTokens;
    }

    @Test
    public void testLexerValid1() {
        String inputStr = "52+-(-25.)-(32.4-+.e.)/.9*.";
        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new Token("52", Token.TokenType.NUM));
        expectedTokens.add(new Token("+", Token.TokenType.ADD));
        expectedTokens.add(new Token("-", Token.TokenType.SUB));
        expectedTokens.add(new Token("(", Token.TokenType.LPAREN));
        expectedTokens.add(new Token("-", Token.TokenType.SUB));
        expectedTokens.add(new Token("25.0", Token.TokenType.NUM));
        expectedTokens.add(new Token(")", Token.TokenType.RPAREN));
        expectedTokens.add(new Token("-", Token.TokenType.SUB));
        expectedTokens.add(new Token("(", Token.TokenType.LPAREN));
        expectedTokens.add(new Token("32.4", Token.TokenType.NUM));
        expectedTokens.add(new Token("-", Token.TokenType.SUB));
        expectedTokens.add(new Token("+", Token.TokenType.ADD));
        expectedTokens.add(new Token("0.0e0.0", Token.TokenType.NUM));
        expectedTokens.add(new Token(")", Token.TokenType.RPAREN));
        expectedTokens.add(new Token("/", Token.TokenType.DIV));
        expectedTokens.add(new Token("0.9", Token.TokenType.NUM));
        expectedTokens.add(new Token("*", Token.TokenType.MULT));
        expectedTokens.add(new Token("0.0", Token.TokenType.NUM));

        try {
            ArrayList<Token> actualTokens = testLexerHelper(inputStr);
            assertEquals(expectedTokens, actualTokens);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLexerValid2() {
        String inputStr = "  var b=b +\t-.e+.5 *  a/a  *((2.e-1-67.+71e3*21)))\t";
        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new Token("var", Token.TokenType.ID_DECL));
        expectedTokens.add(new Token("b", Token.TokenType.ID));
        expectedTokens.add(new Token("=", Token.TokenType.ASSIGNMENT));
        expectedTokens.add(new Token("b", Token.TokenType.ID));
        expectedTokens.add(new Token("+", Token.TokenType.ADD));
        expectedTokens.add(new Token("-", Token.TokenType.SUB));
        expectedTokens.add(new Token("0.0e+0.5", Token.TokenType.NUM));
        expectedTokens.add(new Token("*", Token.TokenType.MULT));
        expectedTokens.add(new Token("a", Token.TokenType.ID));
        expectedTokens.add(new Token("/", Token.TokenType.DIV));
        expectedTokens.add(new Token("a", Token.TokenType.ID));
        expectedTokens.add(new Token("*", Token.TokenType.MULT));
        expectedTokens.add(new Token("(", Token.TokenType.LPAREN));
        expectedTokens.add(new Token("(", Token.TokenType.LPAREN));
        expectedTokens.add(new Token("2.0e-1", Token.TokenType.NUM));
        expectedTokens.add(new Token("-", Token.TokenType.SUB));
        expectedTokens.add(new Token("67.0", Token.TokenType.NUM));
        expectedTokens.add(new Token("+", Token.TokenType.ADD));
        expectedTokens.add(new Token("71e3", Token.TokenType.NUM));
        expectedTokens.add(new Token("*", Token.TokenType.MULT));
        expectedTokens.add(new Token("21", Token.TokenType.NUM));
        expectedTokens.add(new Token(")", Token.TokenType.RPAREN));
        expectedTokens.add(new Token(")", Token.TokenType.RPAREN));
        expectedTokens.add(new Token(")", Token.TokenType.RPAREN));

        try {
            ArrayList<Token> actualTokens = testLexerHelper(inputStr);
            assertEquals(expectedTokens, actualTokens);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }
}