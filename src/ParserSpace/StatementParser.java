package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Symbols.IDInfo;
import Symbols.SymbolTable;
import Symbols.TypeInfo;
import Types.TypeConversion;
import Types.TypeTable;
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

    public Lexer getLexer() {
        return lexer;
    }

    /**
     * Determines if the ID name has valid characters, namely alphanumeric and underscores.
     *
     * @param idName name of the ID.
     * @return true if the ID name is valid and false otherwise.
     */
    private boolean isIDNameValid(String idName) {
        char c = idName.charAt(0);
        // Check if the first character is a letter or '_'
        if (!Character.isAlphabetic(c) && c != '_') {
            return false;
        }
        // Next, check if the other characters are alphanumeric or '_'
        for (int i = 1; i < idName.length(); ++i) {
            c = idName.charAt(i);
            if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to parse an ID declaration in the form of "var ID: type =".
     *
     * @param scope scope in which the new ID is declared.
     * @return an AST node that is the root of the assignment statement
     * or null if the statement is not an definition.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseIDDeclaration(Block scope) throws SyntaxError, IOException {
        /*
        var ID: type = ...;
         */
        Token currToken = lexer.getNextToken();
        if (currToken == null) {
            return null;
        }

        String currTokenStr = currToken.getValue();
        TokenType currTokenType = currToken.getType();
        // Check if the first token is an ID declaration keyword
        if (currTokenType != TokenType.MUTABLE_ID_DECL && currTokenType != TokenType.IMMUTABLE_ID_DECL) {
            lexer.putBack(currToken);
            return null;
        }

        boolean isIDMutable = currTokenType == TokenType.MUTABLE_ID_DECL;
        currToken = lexer.getNextToken();
        // Check if there is an ID name
        if (currToken == null) {
            throw new SyntaxError("Missing a variable name after the declaration keyword '" + currTokenStr + "'",
                    lexer.getCurrentLine());
        }

        SymbolTable symbolTable = SymbolTable.getInstance();
        currTokenStr = currToken.getValue();
        // Check if the ID is valid
        if (symbolTable.isID(currTokenStr, scope)) {
            throw new SyntaxError("Cannot redeclare the existing variable '" + currTokenStr + "'",
                    lexer.getCurrentLine());
        } else if (symbolTable.isKeyword(currTokenStr) || symbolTable.isType(currTokenStr)) {
            throw new SyntaxError("Cannot use the reserved keyword '" + currTokenStr +
                    "' for a variable name", lexer.getCurrentLine());
        } else if (symbolTable.isOperator(currTokenStr)) {
            throw new SyntaxError("Cannot use the operator '" + currTokenStr +
                    "' as a variable name", lexer.getCurrentLine());
        } else if (!isIDNameValid(currTokenStr)) {
            throw new SyntaxError("The variable name '" + currTokenStr +
                    "' can only consist of alphanumeric characters and underscores", lexer.getCurrentLine());
        }

        String id = currTokenStr;
        currToken = lexer.getNextToken();
        // Check if ':' is missing
        if (currToken == null) {
            throw new SyntaxError("Missing ':' after '" + id + "'", lexer.getCurrentLine());
        }

        currTokenType = currToken.getType();
        // Check if the token is ':'
        if (currTokenType != TokenType.COLON) {
            throw new SyntaxError("Expected ':' after '" + id + "'", lexer.getCurrentLine());
        }

        currToken = lexer.getNextToken();
        // Check if a type token is missing
        if (currToken == null) {
            throw new SyntaxError("Missing a variable type for '" + id + "'", lexer.getCurrentLine());
        }

        currTokenStr = currToken.getValue();
        TypeInfo idDataType = (TypeInfo) symbolTable.getType(currTokenStr);
        // Check if the token is a valid type
        if (idDataType == null) {
            throw new SyntaxError("Invalid type '" + currTokenStr + "' for variable '" + id + "'",
                    lexer.getCurrentLine());
        }

        currToken = lexer.getNextToken();
        // Check if '=' is present
        if (currToken == null) {
            throw new SyntaxError("Missing '=' after '" + currTokenStr + "'", lexer.getCurrentLine());
        }

        currTokenStr = currToken.getValue();
        currTokenType = currToken.getType();
        if (currTokenType != TokenType.ASSIGNMENT) {
            throw new SyntaxError("Expected '=' but instead got '" + currTokenStr + "'", lexer.getCurrentLine());
        }

        Node assignmentNode = new Node(NodeType.ASSIGNMENT);
        IDNode idNode = new IDNode(NodeType.ID_DECL, id, idDataType, isIDMutable);
        assignmentNode.addChild(idNode);
        return assignmentNode;
    }

    /**
     * Attempts to parse the left-hand-side of an assignment statement.
     *
     * @param scope scope in which the ID exists.
     * @return an AST node that is the root of the assignment statement
     * or null if the statement is not a proper assignment statement.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    private Node parseLHS(Block scope) throws SyntaxError, IOException {
        /*
        var ID: type = ...;
        ID = ...;
        Expression;
         */

        ArrayDeque<Token> tempTokenBuffer = new ArrayDeque<>();

        // Try parsing ID declaration
        // var ID: type = ...;
        Node assignmentNode = parseIDDeclaration(scope);

        if (assignmentNode != null) {
            return assignmentNode;
        }

        // ID = ...;
        Token currToken = lexer.getNextToken();

        // Check if there is a token
        if (currToken == null) {
            return null;
        }

        String currTokenStr = currToken.getValue();
        String id = currTokenStr;
        SymbolTable symbolTable = SymbolTable.getInstance();
        tempTokenBuffer.addLast(currToken);
        IDInfo idInfo = (IDInfo) symbolTable.getID(id, scope);

        // Check if token is an existing ID
        if (idInfo == null) {
            lexer.putBack(tempTokenBuffer);
            return null;
        }

        currToken = lexer.getNextToken();

        // Check if '=' is present
        if (currToken == null) {
            throw new SyntaxError("Missing ';' after '" + currTokenStr + "'", lexer.getCurrentLine());
        }

        TokenType currTokenType = currToken.getType();
        tempTokenBuffer.addLast(currToken);

        if (currTokenType != TokenType.ASSIGNMENT) {
            // If the token is not '=', put back everything that has been read and return
            lexer.putBack(tempTokenBuffer);
            return null;
        } else if (!idInfo.isMutable()) {
            // If the ID is immutable but '=' is present, throw an exception
            throw new SyntaxError("Unable to assign new value to the immutable variable '" + id + "'",
                    lexer.getCurrentLine());
        }

        assignmentNode = new Node(NodeType.ASSIGNMENT);
        IDNode idNode = new IDNode(NodeType.ID_ASSIGNMENT, id, idInfo.getDataType(), true);
        assignmentNode.addChild(idNode);
        return assignmentNode;
    }

    /**
     * Determines if the left-hand side ID and the right-hand side expression are compatible with the assignment operator.
     *
     * @param lhsNode  the left-hand side node containing the ID declaration or reassignment information.
     * @param exprNode the right-hand side node containing the expression information.
     * @return either the current right-hand side node if there is no type conversion needed and a new one
     * if a type conversion is required.
     * @throws SyntaxError if there is a syntax error.
     */
    private Node checkLHSAndExprTypes(IDNode lhsNode, ExpressionNode exprNode) throws SyntaxError {
        String lhsID = lhsNode.getId();
        TypeInfo lhsDataType = lhsNode.getDataType();
        TypeInfo exprDataType = exprNode.getDataType();
        String lhsDataTypeId = lhsDataType.getId();
        String exprDataTypeId = exprDataType.getId();
        TypeTable typeTable = TypeTable.getInstance();

        // Type check the left-hand side and the expression to see if they are compatible
        boolean typeCompatible = typeTable.AreTypesCompatibleUsingBinaryOperator(
                TokenType.ASSIGNMENT, lhsDataType, exprDataType);
        if (!typeCompatible) {
            throw new SyntaxError(
                    "Variable '" + lhsID + "' of type '" + lhsDataTypeId +
                            "' and the expression on the right of type '" + exprDataTypeId +
                            "' are not compatible using the operator '='",
                    lexer.getCurrentLine());
        }

        if (lhsDataType == exprDataType) {
            return exprNode;
        }

        // If the two types are not the same but compatible with the given operator,
        // add a conversion node that indicates the type to be converted to
        TypeConversion typeConversion = typeTable.getTypeConversion(lhsDataType, exprDataType);
        if (!typeConversion.isImplicit()) {
            // If the type conversion is not implicit, there must be type casting
            // If not, throw an exception
            throw new SyntaxError(
                    "Missing type casting from an expression of type '" + exprDataTypeId +
                            "' to variable '" + lhsID + "' of type '" + lhsDataTypeId + "'",
                    lexer.getCurrentLine());
        }
        TypeInfo resultType = typeConversion.getResultType();
        DataTypeNode typeConversionNode = new DataTypeNode(NodeType.TYPE_CONVERSION, resultType);
        typeConversionNode.addChild(exprNode);
        return typeConversionNode;
    }

    /**
     * Attempts to parse an assignment statement or an expression and does any type conversion if necessary.
     *
     * @param scope scope of the assignment or the expression.
     * @return an AST node that is the root of the assignment statement or an expression
     * or null if it is empty or neither a statement nor an expression.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if there is an IO exception.
     */
    public Node parseStatement(Block scope) throws SyntaxError, IOException {
        // Assignment or pure expression
        Node assignmentNode = parseIDDeclaration(scope);
        if (assignmentNode == null) {
            assignmentNode = parseLHS(scope);
        }
        ExpressionNode exprNode = (ExpressionNode) exprParser.parseExpression(scope);

        // Check if ';' is present
        Token currToken = lexer.getNextToken();
        if (currToken == null || currToken.getType() != TokenType.SEMICOLON) {
            throw new SyntaxError("Missing ';' at the end of the statement", lexer.getCurrentLine());
        }

        if (assignmentNode == null) {
            return exprNode;
        }

        // Type check the data returned by the expression and the left-hand side
        IDNode lhsNode = (IDNode) assignmentNode.getChild(0);
        Node rhsNode = checkLHSAndExprTypes(lhsNode, exprNode);

        String id = lhsNode.getId();
        TypeInfo idDataType = lhsNode.getDataType();
        boolean isIDMutable = lhsNode.isMutable();
        // Add a new ID to the symbol table if the left-hand side is an ID declaration
        if (lhsNode.getNodeType() == NodeType.ID_DECL) {
            SymbolTable.getInstance().set(new IDInfo(id, scope, idDataType, isIDMutable));
        }

        assignmentNode.addChild(rhsNode);
        return assignmentNode;
    }

}
