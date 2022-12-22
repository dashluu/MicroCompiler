import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Utilities.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws SyntaxError, IOException {
        String str = "var a: int =(-125 + .1    /    .e+1   )   ;";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        Lexer lexer = new Lexer(reader);
        Token token;

        while ((token = lexer.getNextToken()) != null) {
            System.out.println(token);
        }
    }
}