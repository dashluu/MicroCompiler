import Exceptions.SymbolTableException;
import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import ParserSpace.ExpressionParser;
import Symbols.IDInfo;
import Symbols.SymbolTable;
import Utilities.Global;
import Utilities.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws SyntaxError, IOException, SymbolTableException {
        String str = "    a+a  *(-2.e-1+--(75))\t";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        Lexer lexer = new Lexer(reader);
        ExpressionParser expressionParser = new ExpressionParser(lexer);
        SymbolTable.getInstance().insert(new IDInfo(new Token("a", Token.TokenType.ID), Global.globalScope));
        expressionParser.parseExpression(Global.globalScope);
    }
}