package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Symbols.SymbolTable;
import Symbols.TypeInfo;
import Utilities.Block;
import Utilities.Token;
import Utilities.TokenType;

import java.io.IOException;
import java.util.ArrayDeque;

public class StatementParser {

    private final Lexer lexer;
    private final ExpressionParser exprParser;

    public StatementParser(ExpressionParser exprParser) {
        this.exprParser = exprParser;
        this.lexer = exprParser.getLexer();
    }

    private boolean isIDValid(String idStr) {
        char c = idStr.charAt(0);
        // Check if the first character is a letter or '_'
        if (!Character.isAlphabetic(c) && c != '_') {
            return false;
        }
        // Next, check if the other characters are alphanumeric or '_'
        for (int i = 1; i < idStr.length(); ++i) {
            c = idStr.charAt(i);
            if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    private Node parseIDDeclaration(Block scope) throws SyntaxError, IOException {
        /*
        var ID: type = ...;
         */
        Token currToken = lexer.getNextToken();
        if (currToken == null) {
            return null;
        }

        TokenType currTokenType = currToken.getType();
        // Check if the first token is an ID declaration keyword
        if (currTokenType != TokenType.MUTABLE_ID_DECL) {
            lexer.putBack(currToken);
            return null;
        }

        currToken = lexer.getNextToken();
        // Check if there is an ID name
        if (currToken == null) {
            throw new SyntaxError("Missing a variable name after the declaration keyword", lexer.getCurrLine());
        }

        SymbolTable symbolTable = SymbolTable.getInstance();
        String currTokenStr = currToken.getValue();
        // Check if the ID is valid
        if (symbolTable.isID(currTokenStr, scope)) {
            throw new SyntaxError("Cannot redeclare an existing variable", lexer.getCurrLine());
        } else if (symbolTable.isKeyword(currTokenStr) || symbolTable.isType(currTokenStr)) {
            throw new SyntaxError("Cannot use a reserved keyword for a variable name", lexer.getCurrLine());
        } else if (symbolTable.isOperator(currTokenStr)) {
            throw new SyntaxError("Cannot use an operator as a variable name", lexer.getCurrLine());
        } else if (!isIDValid(currTokenStr)) {
            throw new SyntaxError("A variable name can only consist of alphanumeric characters and underscores",
                    lexer.getCurrLine());
        }

        Token idToken = currToken;
        String id = currTokenStr;
        currToken = lexer.getNextToken();
        // Check if ';' is missing
        if (currToken == null) {
            throw new SyntaxError("Missing ':' after '" + id + "'", lexer.getCurrLine());
        }

        currTokenType = currToken.getType();
        // Check if the token is ';'
        if (currTokenType != TokenType.SEMICOLON) {
            throw new SyntaxError("Expected ':' after '" + id + "'", lexer.getCurrLine());
        }

        currToken = lexer.getNextToken();
        // Check if a type token is missing
        if (currToken == null) {
            throw new SyntaxError("Missing a variable type for '" + id + "'", lexer.getCurrLine());
        }

        currTokenStr = currToken.getValue();
        TypeInfo idDataType = (TypeInfo) symbolTable.getType(currTokenStr);
        // Check if the token is a valid type
        if (idDataType == null) {
            throw new SyntaxError("Invalid type for '" + id + "'", lexer.getCurrLine());
        }

        currToken = lexer.getNextToken();
        // Check if '=' is present
        if (currToken == null) {
            throw new SyntaxError("Missing '='", lexer.getCurrLine());
        }

        currTokenStr = currToken.getValue();
        currTokenType = currToken.getType();
        if (currTokenType != TokenType.ASSIGNMENT) {
            throw new SyntaxError("Expected '=' but instead got '" + currTokenStr + "'", lexer.getCurrLine());
        }

        Node assignmentRoot = new Node(NodeType.ASSIGNMENT);
        Node idDeclRoot = new Node(NodeType.MUTABLE_ID_DECL);
        TokenNode idTokenNode = new TokenNode(idToken, idDataType);
        idDeclRoot.addChild(idTokenNode);
        assignmentRoot.addChild(idDeclRoot);
        return assignmentRoot;
    }

    private Node parseLHS(Block scope) throws SyntaxError, IOException {
        /*
        var ID: type = ...;
        ID = ...;
        Expression;
         */

        ArrayDeque<Token> tempTokenBuffer = new ArrayDeque<>();

        // Try parsing ID declaration
        // var ID: type = ...;
        Node assignmentRoot = parseIDDeclaration(scope);
        if (assignmentRoot != null) {
            return assignmentRoot;
        }

        // ID = ...;
        Token currToken = lexer.getNextToken();
        // Check if there is a token
        if (currToken == null) {
            return null;
        }

        String currTokenStr = currToken.getValue();
        SymbolTable symbolTable = SymbolTable.getInstance();
        tempTokenBuffer.addLast(currToken);
        // Check if token is an existing ID
        if (!symbolTable.isID(currTokenStr, scope)) {
            lexer.putBack(tempTokenBuffer);
            return null;
        }

        Token idToken = currToken;
        currToken = lexer.getNextToken();
        // Check if '=' is present
        if (currToken == null) {
            throw new SyntaxError("Expected an assignment or a valid expression", lexer.getCurrLine());
        }

        TokenType currTokenType = currToken.getType();
        tempTokenBuffer.addLast(currToken);
        // If the token is not '=', put back everything that has been read and return
        if (currTokenType != TokenType.ASSIGNMENT) {
            lexer.putBack(tempTokenBuffer);
            return null;
        }

        assignmentRoot = new Node(NodeType.ASSIGNMENT);
        Node idReassignmentRoot = new Node(NodeType.ID_REASSIGNMENT);
        TokenNode idTokenNode = new TokenNode(idToken);
        idReassignmentRoot.addChild(idTokenNode);
        assignmentRoot.addChild(idReassignmentRoot);
        return assignmentRoot;
    }

    public Node parseStatement(Block scope) throws SyntaxError, IOException {
        // Assignment or pure expression
        Node assignmentRoot = parseIDDeclaration(scope);
        if (assignmentRoot == null) {
            assignmentRoot = parseLHS(scope);
        }
        Node exprRoot = exprParser.parseExpression(scope);
        if (assignmentRoot == null) {
            return exprRoot;
        }
        assignmentRoot.addChild(exprRoot);
        return assignmentRoot;
    }

}
