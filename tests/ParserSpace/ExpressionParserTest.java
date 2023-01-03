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

    private final SymbolTable symbolTable = SymbolTable.getInstance();
    private final TypeInfo intType = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
    private final TypeInfo floatType = (TypeInfo) symbolTable.getType(Global.FLOAT_TYPE_ID);

    private ExpressionParser initExprParser(String inputStr) {
        BufferedReader reader = new BufferedReader(new StringReader(inputStr));
        Lexer lexer = new Lexer(reader);
        return new ExpressionParser(lexer);
    }

    private ArrayList<ExpressionNode> getExprInfixNodesHelper(String inputStr, Block scope)
            throws SyntaxError, IOException {
        ExpressionParser exprParser = initExprParser(inputStr);
        return exprParser.getExpressionInfixNodes(scope);
    }

    private ArrayList<ExpressionNode> getExprPostfixNodesHelper(String inputStr, Block scope)
            throws SyntaxError, IOException {
        ExpressionParser exprParser = initExprParser(inputStr);
        ArrayList<ExpressionNode> infixNodes = exprParser.getExpressionInfixNodes(scope);
        return exprParser.getPostfixOrder(infixNodes);
    }

    @Test
    void testGetEmptyExprInfixNodes() {
        String inputStr = "";
        ArrayList<ExpressionNode> expectedInfixNodes = new ArrayList<>();

        try {
            ArrayList<ExpressionNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetBlankExprInfixNodes() {
        String inputStr = "\t \n\n";
        ArrayList<ExpressionNode> expectedInfixNodes = new ArrayList<>();

        try {
            ArrayList<ExpressionNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetExprInfixNodesValid2() {
        String inputStr = "\na \n*\nb+ .e-. /34.*-41--+35/c+\t((777-4)+12 \n*95)";
        ArrayList<ExpressionNode> expectedInfixNodes = new ArrayList<>();
        expectedInfixNodes.add(new ExpressionNode("a", TokenType.ID, intType));
        expectedInfixNodes.add(new ExpressionNode("*", TokenType.MULT));
        expectedInfixNodes.add(new ExpressionNode("b", TokenType.ID, intType));
        expectedInfixNodes.add(new ExpressionNode("+", TokenType.ADD));
        expectedInfixNodes.add(new ExpressionNode("0.0e-0.0", TokenType.FLOAT_LITERAL, floatType));
        expectedInfixNodes.add(new ExpressionNode("/", TokenType.DIV));
        expectedInfixNodes.add(new ExpressionNode("34.0", TokenType.FLOAT_LITERAL, floatType));
        expectedInfixNodes.add(new ExpressionNode("*", TokenType.MULT));
        expectedInfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedInfixNodes.add(new ExpressionNode("41", TokenType.INT_LITERAL, intType));
        expectedInfixNodes.add(new ExpressionNode("-", TokenType.SUB));
        expectedInfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedInfixNodes.add(new ExpressionNode("+", TokenType.PLUS));
        expectedInfixNodes.add(new ExpressionNode("35", TokenType.INT_LITERAL, intType));
        expectedInfixNodes.add(new ExpressionNode("/", TokenType.DIV));
        expectedInfixNodes.add(new ExpressionNode("c", TokenType.ID, floatType));
        expectedInfixNodes.add(new ExpressionNode("+", TokenType.ADD));
        expectedInfixNodes.add(new ExpressionNode("(", TokenType.LPAREN));
        expectedInfixNodes.add(new ExpressionNode("(", TokenType.LPAREN));
        expectedInfixNodes.add(new ExpressionNode("777", TokenType.INT_LITERAL, intType));
        expectedInfixNodes.add(new ExpressionNode("-", TokenType.SUB));
        expectedInfixNodes.add(new ExpressionNode("4", TokenType.INT_LITERAL, intType));
        expectedInfixNodes.add(new ExpressionNode(")", TokenType.RPAREN));
        expectedInfixNodes.add(new ExpressionNode("+", TokenType.ADD));
        expectedInfixNodes.add(new ExpressionNode("12", TokenType.INT_LITERAL, intType));
        expectedInfixNodes.add(new ExpressionNode("*", TokenType.MULT));
        expectedInfixNodes.add(new ExpressionNode("95", TokenType.INT_LITERAL, intType));
        expectedInfixNodes.add(new ExpressionNode(")", TokenType.RPAREN));

        // Set up the symbol table
        symbolTable.set(new IDInfo("a", Global.globalScope, intType, true));
        symbolTable.set(new IDInfo("b", Global.globalScope, intType, true));
        symbolTable.set(new IDInfo("c", Global.globalScope, floatType, true));

        try {
            ArrayList<ExpressionNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetExprInfixNodesValid3() {
        String inputStr = "    a+a  *(-2.e-1+--(75))\t";
        ArrayList<ExpressionNode> expectedInfixNodes = new ArrayList<>();
        expectedInfixNodes.add(new ExpressionNode("a", TokenType.ID, intType));
        expectedInfixNodes.add(new ExpressionNode("+", TokenType.ADD));
        expectedInfixNodes.add(new ExpressionNode("a", TokenType.ID, intType));
        expectedInfixNodes.add(new ExpressionNode("*", TokenType.MULT));
        expectedInfixNodes.add(new ExpressionNode("(", TokenType.LPAREN));
        expectedInfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedInfixNodes.add(new ExpressionNode("2.0e-1", TokenType.FLOAT_LITERAL, floatType));
        expectedInfixNodes.add(new ExpressionNode("+", TokenType.ADD));
        expectedInfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedInfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedInfixNodes.add(new ExpressionNode("(", TokenType.LPAREN));
        expectedInfixNodes.add(new ExpressionNode("75", TokenType.INT_LITERAL, intType));
        expectedInfixNodes.add(new ExpressionNode(")", TokenType.RPAREN));
        expectedInfixNodes.add(new ExpressionNode(")", TokenType.RPAREN));

        // Set up the symbol table
        symbolTable.set(new IDInfo("a", Global.globalScope, intType, true));

        try {
            ArrayList<ExpressionNode> actualInfixNodes = getExprInfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedInfixNodes, actualInfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetExprPostfixNodesValid1() {
        String inputStr = "a+\ta \n*(-2.e-1\n+-\n-(75))";
        ArrayList<ExpressionNode> expectedPostfixNodes = new ArrayList<>();
        expectedPostfixNodes.add(new ExpressionNode("a", TokenType.ID, intType));
        expectedPostfixNodes.add(new ExpressionNode("a", TokenType.ID, intType));
        expectedPostfixNodes.add(new ExpressionNode("2.0e-1", TokenType.FLOAT_LITERAL, floatType));
        expectedPostfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedPostfixNodes.add(new ExpressionNode("75", TokenType.INT_LITERAL, intType));
        expectedPostfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedPostfixNodes.add(new ExpressionNode("-", TokenType.MINUS));
        expectedPostfixNodes.add(new ExpressionNode("+", TokenType.ADD));
        expectedPostfixNodes.add(new ExpressionNode("*", TokenType.MULT));
        expectedPostfixNodes.add(new ExpressionNode("+", TokenType.ADD));

        // Set up the symbol table
        symbolTable.set(new IDInfo("a", Global.globalScope, intType, true));

        try {
            ArrayList<ExpressionNode> actualPostfixNodes = getExprPostfixNodesHelper(inputStr, Global.globalScope);
            assertEquals(expectedPostfixNodes, actualPostfixNodes);
        } catch (SyntaxError | IOException e) {
            e.printStackTrace();
        }
    }
}