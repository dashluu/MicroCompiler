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
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws SyntaxError, IOException {
        String fileName = "code.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        Lexer lexer = new Lexer(reader);
        ExpressionParser expressionParser = new ExpressionParser(lexer);
        SymbolTable symbolTable = SymbolTable.getInstance();
        TypeInfo type = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
        symbolTable.set(new IDInfo("a", Global.globalScope, type, true));
        expressionParser.parseExpression(Global.globalScope);
    }
}