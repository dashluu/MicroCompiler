package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Symbols.IDInfo;
import Symbols.SymbolTable;
import Symbols.TypeInfo;
import Utilities.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionParserTest {

    private ExpressionParser initExprParser(String inputStr) {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        return new ExpressionParser(lexer);
    }

    private ArrayList<TokenNode> getExprInfixNodesHelper(String inputStr, Block scope)
            throws SyntaxError, IOException {
        ExpressionParser exprParser = initExprParser(inputStr);
        return exprParser.getExpressionInfixNodes(scope);
    }

    private ArrayList<TokenNode> getExprPostfixNodesHelper(String inputStr, Block scope)
            throws SyntaxError, IOException {
        ExpressionParser exprParser = initExprParser(inputStr);
        ArrayList<TokenNode> infixNodes = exprParser.getExpressionInfixNodes(scope);
        return exprParser.getPostfixOrder(infixNodes);
    }

    @Test
    void testGetEmptyExprInfixNodes() {
        String inputStr = "";
        ArrayList<TokenNode> expectedInfixNodes = new ArrayList<>();

        try {
            ArrayList<TokenNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetBlankExprInfixNodes() {
        String inputStr = "\t \n\n";
        ArrayList<TokenNode> expectedInfixNodes = new ArrayList<>();

        try {
            ArrayList<TokenNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetExprInfixNodesValid2() {
        String inputStr = "\na \n*\nb+ .e-. /34.*-41--+35/c+\t((777-4)+12 \n*95)";
        ArrayList<TokenNode> expectedInfixNodes = new ArrayList<>();
        expectedInfixNodes.add(new TokenNode(new Token("a", TokenType.ID)));
        expectedInfixNodes.add(new TokenNode(new Token("*", TokenType.MULT)));
        expectedInfixNodes.add(new TokenNode(new Token("b", TokenType.ID)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedInfixNodes.add(new TokenNode(new Token("0.0e-0.0", TokenType.FLOAT)));
        expectedInfixNodes.add(new TokenNode(new Token("/", TokenType.DIV)));
        expectedInfixNodes.add(new TokenNode(new Token("34.0", TokenType.FLOAT)));
        expectedInfixNodes.add(new TokenNode(new Token("*", TokenType.MULT)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("41", TokenType.INT)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.SUB)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.PLUS)));
        expectedInfixNodes.add(new TokenNode(new Token("35", TokenType.INT)));
        expectedInfixNodes.add(new TokenNode(new Token("/", TokenType.DIV)));
        expectedInfixNodes.add(new TokenNode(new Token("c", TokenType.ID)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedInfixNodes.add(new TokenNode(new Token("(", TokenType.LPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token("(", TokenType.LPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token("777", TokenType.INT)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.SUB)));
        expectedInfixNodes.add(new TokenNode(new Token("4", TokenType.INT)));
        expectedInfixNodes.add(new TokenNode(new Token(")", TokenType.RPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedInfixNodes.add(new TokenNode(new Token("12", TokenType.INT)));
        expectedInfixNodes.add(new TokenNode(new Token("*", TokenType.MULT)));
        expectedInfixNodes.add(new TokenNode(new Token("95", TokenType.INT)));
        expectedInfixNodes.add(new TokenNode(new Token(")", TokenType.RPAREN)));

        // Set up the symbol table
        SymbolTable symbolTable = SymbolTable.getInstance();
        TypeInfo intType = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
        TypeInfo floatType = (TypeInfo) symbolTable.getType(Global.FLOAT_TYPE_ID);
        symbolTable.set(new IDInfo("a", Global.globalScope, intType, true));
        symbolTable.set(new IDInfo("b", Global.globalScope, intType, true));
        symbolTable.set(new IDInfo("c", Global.globalScope, floatType, true));

        try {
            ArrayList<TokenNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetExprInfixNodesValid3() {
        String inputStr = "    a+a  *(-2.e-1+--(75))\t";
        ArrayList<TokenNode> expectedInfixNodes = new ArrayList<>();
        expectedInfixNodes.add(new TokenNode(new Token("a", TokenType.ID)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedInfixNodes.add(new TokenNode(new Token("a", TokenType.ID)));
        expectedInfixNodes.add(new TokenNode(new Token("*", TokenType.MULT)));
        expectedInfixNodes.add(new TokenNode(new Token("(", TokenType.LPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("2.0e-1", TokenType.FLOAT)));
        expectedInfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedInfixNodes.add(new TokenNode(new Token("(", TokenType.LPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token("75", TokenType.INT)));
        expectedInfixNodes.add(new TokenNode(new Token(")", TokenType.RPAREN)));
        expectedInfixNodes.add(new TokenNode(new Token(")", TokenType.RPAREN)));

        // Set up the symbol table
        SymbolTable symbolTable = SymbolTable.getInstance();
        TypeInfo type = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
        symbolTable.set(new IDInfo("a", Global.globalScope, type, true));

        try {
            ArrayList<TokenNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetExprPostfixNodesValid1() {
        String inputStr = "a+\ta \n*(-2.e-1\n+-\n-(75))";
        ArrayList<TokenNode> expectedPostfixNodes = new ArrayList<>();
        expectedPostfixNodes.add(new TokenNode(new Token("a", TokenType.ID)));
        expectedPostfixNodes.add(new TokenNode(new Token("a", TokenType.ID)));
        expectedPostfixNodes.add(new TokenNode(new Token("2.0e-1", TokenType.FLOAT)));
        expectedPostfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedPostfixNodes.add(new TokenNode(new Token("75", TokenType.INT)));
        expectedPostfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedPostfixNodes.add(new TokenNode(new Token("-", TokenType.MINUS)));
        expectedPostfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));
        expectedPostfixNodes.add(new TokenNode(new Token("*", TokenType.MULT)));
        expectedPostfixNodes.add(new TokenNode(new Token("+", TokenType.ADD)));

        // Set up the symbol table
        SymbolTable symbolTable = SymbolTable.getInstance();
        TypeInfo type = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
        symbolTable.set(new IDInfo("a", Global.globalScope, type, true));

        try {
            ArrayList<TokenNode> actualPostfixNodes = getExprPostfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedPostfixNodes, actualPostfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }
}