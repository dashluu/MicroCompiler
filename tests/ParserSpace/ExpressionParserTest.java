package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Symbols.IDInfo;
import Symbols.SymbolInfo;
import Symbols.SymbolTable;
import Utilities.Block;
import Utilities.Global;
import Utilities.Node;
import Utilities.Token;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionParserTest {

    private ArrayList<Node> getExpressionInfixNodesHelper(String inputStr, Block scope) throws SyntaxError, IOException {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        ExpressionParser expressionParser = new ExpressionParser(lexer);
        return expressionParser.getExpressionInfixNodes(scope);
    }

    @Test
    void parseExpression() {
    }

    @Test
    void testGetExpressionInfixNodesValid1() {
        String inputStr = "    a+a  *(-2.e-1+--(75))\t";
        ArrayList<Node> expectedInfixNodes = new ArrayList<>();
        expectedInfixNodes.add(new Node(new Token("a", Token.TokenType.ID)));
        expectedInfixNodes.add(new Node(new Token("+", Token.TokenType.BINARY)));
        expectedInfixNodes.add(new Node(new Token("a", Token.TokenType.ID)));
        expectedInfixNodes.add(new Node(new Token("*", Token.TokenType.BINARY)));
        expectedInfixNodes.add(new Node(new Token("(", Token.TokenType.LPAREN)));
        expectedInfixNodes.add(new Node(new Token("-", Token.TokenType.UNARY)));
        expectedInfixNodes.add(new Node(new Token("2.0e-1", Token.TokenType.NUM)));
        expectedInfixNodes.add(new Node(new Token("+", Token.TokenType.BINARY)));
        expectedInfixNodes.add(new Node(new Token("-", Token.TokenType.UNARY)));
        expectedInfixNodes.add(new Node(new Token("-", Token.TokenType.UNARY)));
        expectedInfixNodes.add(new Node(new Token("(", Token.TokenType.LPAREN)));
        expectedInfixNodes.add(new Node(new Token("75", Token.TokenType.NUM)));
        expectedInfixNodes.add(new Node(new Token(")", Token.TokenType.RPAREN)));
        expectedInfixNodes.add(new Node(new Token(")", Token.TokenType.RPAREN)));

        // Set up the symbol table
        SymbolTable.getInstance().insert(new IDInfo(new Token("a", Token.TokenType.ID), Global.globalScope));

        try {
            ArrayList<Node> actualInfixNodes = getExpressionInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }
}