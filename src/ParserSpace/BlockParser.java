package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Utilities.Block;

import java.io.IOException;

public class BlockParser {
    private final Lexer lexer;
    private final StatementParser statementParser;

    public BlockParser(StatementParser statementParser) {
        this.lexer = statementParser.getLexer();
        this.statementParser = statementParser;
    }

    public void parseBlock(Block scope) throws IOException, SyntaxError {
        Node blockNode;
        while (!lexer.isEOS()) {
            blockNode = statementParser.parseStatement(scope);
        }
    }
}
