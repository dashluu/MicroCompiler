package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Operators.OperatorInfo;
import Operators.OperatorTable;
import Symbols.SymbolTable;
import Utilities.Block;
import Utilities.Node;
import Utilities.Token;

import java.io.IOException;
import java.util.ArrayList;

public class ExpressionParser {
    private Lexer lexer;

    private Node parseOperand(Block scope) throws SyntaxError, IOException {
        Token token = lexer.getNextToken();
        if (token == null) {
            return null;
        }
        // Check if the token is a valid number
        if (token.getType() == Token.TokenType.NUM) {
            return new Node(token);
        }
        // Check if the token is a valid ID
        if (SymbolTable.getInstance().isValidID(token.getValue(), scope)) {
            return new Node(token);
        }
        lexer.putBack(token.getValue());
        return null;
    }

    private void recurParseExpression(ArrayList<Node> nodes, Block scope) throws SyntaxError, IOException {
        Token token = lexer.getNextToken();
        if (token == null) {
            return;
        }
        OperatorTable opTable = OperatorTable.getInstance();
        // Check if '(' is present
        if (opTable.isValidOperator(token.getValue(), OperatorInfo.OperatorType.LPAREN)) {
            nodes.add(new Node(token));
            recurParseExpression(nodes, scope);
            token = lexer.getNextToken();
            if (token == null || !opTable.isValidOperator(token.getValue(), OperatorInfo.OperatorType.RPAREN)) {
                throw new SyntaxError("Missing ')'", lexer.getCurrLine());
            }
            nodes.add(new Node(token));
            return;
        }


    }
}
