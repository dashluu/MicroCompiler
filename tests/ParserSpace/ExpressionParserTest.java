package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Symbols.IDInfo;
import Symbols.SymbolTable;
import Utilities.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionParserTest {

    private ArrayList<TokenNode> getExpressionInfixNodesHelper(String inputStr, Block scope)
            throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        ExpressionParser expressionParser = new ExpressionParser(lexer);
        return expressionParser.getExpressionInfixNodes(scope);
    }

    @Test
    void testGetExpressionInfixNodesValid1() {
        String inputStr = "    a+a  *(-2.e-1+--(75))\t";
        ArrayList<TokenNode> expectedInfixNodes = new ArrayList<>();
        expectedInfixNodes.add(new TokenNode(new Token("a", TokenType.ID)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedInfixNodes.add(new TokenNode(new Token("a", TokenType.ID)));
        expectedInfixNodes.add(new TokenNode(new Token("*", TokenType.MULT)));
        expectedInfixNodes.add(new TokenNode(new Token("(", TokenType.LPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("2.0e-1", TokenType.NUM)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("(", TokenType.LPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token("75", TokenType.NUM)));
        expectedInfixNodes.add(new TokenNode(new Token(")", TokenType.RPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token(")", TokenType.RPAREN)));

        // Set up the symbol table
        SymbolTable.getInstance().set(new IDInfo(new Token("a", TokenType.ID), Global.globalScope, true));

        try {
            ArrayList<TokenNode> actualInfixNodes = getExpressionInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }
}