import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import ParserSpace.ExpressionParser;
import Symbols.IDInfo;
import Symbols.SymbolTable;
import Symbols.TypeInfo;
import Utilities.Global;
import Utilities.Token;
import Utilities.TokenType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws SyntaxError, IOException {
        String str = "    a+a  *(-2.e-1+--(75))\t";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        Lexer lexer = new Lexer(reader);
        ExpressionParser expressionParser = new ExpressionParser(lexer);
        SymbolTable symbolTable = SymbolTable.getInstance();
        TypeInfo type = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
        symbolTable.set(new IDInfo(new Token("a", TokenType.ID), Global.globalScope, type, true));
        expressionParser.parseExpression(Global.globalScope);
    }
}