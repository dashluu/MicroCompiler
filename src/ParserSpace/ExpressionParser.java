package ParserSpace;

import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Operators.OperatorTable;
import Symbols.IDInfo;
import Symbols.SymbolTable;
import Symbols.TypeInfo;
import Types.TypeConversion;
import Types.TypeTable;
import Utilities.Block;
import Utilities.Global;
import Utilities.Token;
import Utilities.TokenType;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class ExpressionParser {
    private final Lexer lexer;
    private int numParen = 0;

    public ExpressionParser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Lexer getLexer() {
        return lexer;
    }

    /**
     * Consumes an expression in a given scope and produces an AST root node associated with the parsed expression.
     *
     * @param scope scope of the expression.
     * @return an AST node that represents the root of the AST.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public Node parseExpression(Block scope) throws SyntaxError, IOException {
        ArrayList<ExpressionNode> infixNodes = getExpressionInfixNodes(scope);
        ArrayList<ExpressionNode> postfixNodes = getPostfixOrder(infixNodes);
        return buildASTFromPostFix(postfixNodes);
    }

    /**
     * Parses and retrieves a list of expression nodes in infix order.
     *
     * @param scope scope of the expression.
     * @return a list of expression nodes in infix order.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public ArrayList<ExpressionNode> getExpressionInfixNodes(Block scope) throws SyntaxError, IOException {
        ArrayList<ExpressionNode> nodes = new ArrayList<>();
        recurParseExpression(nodes, scope);
        return nodes;
    }

    /**
     * A helper method that parses an expression recursively.
     *
     * @param nodes a list to store expression nodes in infix order.
     * @param scope the scope of the expression.
     * @param str   a string which the expression follows.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    private void recurParseExpressionHelper(ArrayList<ExpressionNode> nodes, Block scope, String str)
            throws SyntaxError, IOException {
        // If the size of the list is unchanged, an expression is clearly missing
        int numNodesBefore = nodes.size();
        // Recursively parse an expression
        recurParseExpression(nodes, scope);
        if (nodes.size() == numNodesBefore) {
            throw new SyntaxError("Missing a valid expression after '" + str + "'", lexer.getCurrentLine());
        }
    }

    /**
     * Recursively consumes an expression in a given scope and stores the token nodes in a given list.
     *
     * @param nodes a list to store the token nodes.
     * @param scope scope of the expression.
     * @throws IOException if the read operation causes an IO error.
     * @throws SyntaxError if there is a syntax error.
     */
    private void recurParseExpression(ArrayList<ExpressionNode> nodes, Block scope) throws SyntaxError, IOException {
        /*
          Expr = '(' Expr ')' [binary operator] Expr
               = '+/-' Expr [binary operator] Expr
               = term [binary operator] Expr
         */

        Token currToken = lexer.getNextToken();
        if (currToken == null) {
            return;
        }

        SymbolTable symbolTable = SymbolTable.getInstance();
        OperatorTable opTable = OperatorTable.getInstance();
        String currTokenStr = currToken.getValue();
        TokenType currTokenType = currToken.getType();
        boolean isOpBinary = opTable.isOperator(currTokenType) && opTable.isOperatorBinary(currTokenType);

        // Check if the expression starts with '(', a binary operator, an ID, or a number
        if (currTokenType != TokenType.LPAREN && !isOpBinary && currTokenType != TokenType.ID &&
                currTokenType != TokenType.INT_LITERAL && currTokenType != TokenType.FLOAT_LITERAL) {
            throw new SyntaxError("Invalid expression syntax at '" + currTokenStr + "'", lexer.getCurrentLine());
        }

        TypeInfo currTokenDataType;

        if (currTokenType == TokenType.ID) {
            // Check if the token is a valid ID
            IDInfo idInfo = (IDInfo) symbolTable.getID(currTokenStr, scope);
            if (idInfo == null) {
                throw new SyntaxError("Invalid variable '" + currTokenStr + "'", lexer.getCurrentLine());
            }
            // Get the ID's data type
            currTokenDataType = idInfo.getDataType();
            nodes.add(new ExpressionNode(currTokenStr, currTokenType, currTokenDataType));
        } else if (currTokenType == TokenType.INT_LITERAL) {
            // Consume an integer
            currTokenDataType = (TypeInfo) symbolTable.getType(Global.INT_TYPE_ID);
            nodes.add(new ExpressionNode(currTokenStr, currTokenType, currTokenDataType));
        } else if (currTokenType == TokenType.FLOAT_LITERAL) {
            // Consume a floating point
            currTokenDataType = (TypeInfo) symbolTable.getType(Global.FLOAT_TYPE_ID);
            nodes.add(new ExpressionNode(currTokenStr, currTokenType, currTokenDataType));
        } else if (isOpBinary) {
            // Try to map the binary operator to a unary operator since a token can be both a binary or a unary operator
            // For example, '+' and '-'
            TokenType unaryOpTokenType = opTable.mapBinaryToUnaryOperator(currTokenType);
            if (unaryOpTokenType == null) {
                throw new SyntaxError("Invalid unary operator '" + currTokenStr + "'", lexer.getCurrentLine());
            }
            currToken.setType(unaryOpTokenType);
            currTokenType = unaryOpTokenType;
            nodes.add(new ExpressionNode(currTokenStr, currTokenType));
            // Recursively parse an expression
            recurParseExpressionHelper(nodes, scope, currTokenStr);
        } else {
            // Consume '(' and increment the number of parentheses
            nodes.add(new ExpressionNode(currTokenStr, currTokenType));
            ++numParen;
            // Recursively parse an expression
            recurParseExpressionHelper(nodes, scope, currTokenStr);
            // Consume ')' and decrement the number of parentheses
            currToken = lexer.getNextToken();
            if (currToken == null || (currTokenType = currToken.getType()) != TokenType.RPAREN) {
                throw new SyntaxError("Missing ')'", lexer.getCurrentLine());
            }
            currTokenStr = currToken.getValue();
            nodes.add(new ExpressionNode(currTokenStr, currTokenType));
            --numParen;
        }

        currToken = lexer.getNextToken();

        // Check if the next token is empty or ')'
        if (currToken == null) {
            return;
        }

        currTokenStr = currToken.getValue();
        currTokenType = currToken.getType();

        if (currTokenType == TokenType.RPAREN) {
            // If the token is ')', check if ')' is redundant
            // If it is, throw an exception
            // Otherwise, put it back to the lexer's token buffer and return
            if (numParen > 0) {
                lexer.putBack(currToken);
                return;
            } else {
                throw new SyntaxError("Redundant ')'", lexer.getCurrentLine());
            }
        } else if (currTokenType == TokenType.SEMICOLON) {
            // If the token is ';', put it back to the lexer's token buffer and return
            lexer.putBack(currToken);
            return;
        }

        // Check if the token is a valid binary operator
        isOpBinary = opTable.isOperator(currTokenType) && opTable.isOperatorBinary(currTokenType);

        if (!isOpBinary) {
            throw new SyntaxError("Invalid binary operator '" + currTokenStr + "'", lexer.getCurrentLine());
        }
        nodes.add(new ExpressionNode(currTokenStr, currTokenType));

        // Recursively parse an expression
        recurParseExpressionHelper(nodes, scope, currTokenStr);
    }

    /**
     * Gets a list of nodes in postfix order.
     *
     * @param nodes list of input nodes.
     * @return a list of nodes in postfix order.
     */
    public ArrayList<ExpressionNode> getPostfixOrder(ArrayList<ExpressionNode> nodes) {
        OperatorTable opTable = OperatorTable.getInstance();
        ArrayList<ExpressionNode> postfixNodes = new ArrayList<>();
        ArrayDeque<ExpressionNode> opStack = new ArrayDeque<>();
        ExpressionNode opNode;
        TokenType currValueType, opValueType;
        boolean stop;
        int precedCmp;

        for (ExpressionNode currNode : nodes) {
            currValueType = currNode.getValueType();
            // The token is an operand so push it directly to the postfix list
            if (currValueType == TokenType.ID || currValueType == TokenType.INT_LITERAL || currValueType == TokenType.FLOAT_LITERAL) {
                postfixNodes.add(currNode);
            } else if (currValueType == TokenType.LPAREN) {
                opStack.add(currNode);
            } else if (currValueType == TokenType.RPAREN) {
                stop = false;
                // Pop the operator stack until '(' is encountered, discard it without adding it to the postfix list
                while (!opStack.isEmpty() && !stop) {
                    opNode = opStack.removeLast();
                    opValueType = opNode.getValueType();
                    stop = opValueType == TokenType.LPAREN;
                    if (!stop) {
                        postfixNodes.add(opNode);
                    }
                }
            } else {
                stop = false;
                // Pop the operator stack until '(' is encountered or
                // the operator at the top of the stack has equal or greater preced than the current operator
                while (!opStack.isEmpty() && !stop) {
                    opNode = opStack.getLast();
                    opValueType = opNode.getValueType();
                    stop = opValueType == TokenType.LPAREN;
                    if (!stop) {
                        precedCmp = opTable.compareOperatorPreced(opValueType, currValueType);
                        stop = precedCmp < 0;
                    }
                    if (!stop) {
                        postfixNodes.add(opNode);
                        opStack.removeLast();
                    }
                }
                opStack.add(currNode);
            }
        }

        // Pop nodes that are left from the stack and add them to the postfix list
        while (!opStack.isEmpty()) {
            postfixNodes.add(opStack.removeLast());
        }

        return postfixNodes;
    }

    /**
     * Determines if an operand is compatible with the given unary operator and helps construct a subtree.
     *
     * @param operatorNode a node containing the operator.
     * @param operandNode  a node containing the operand.
     * @throws SyntaxError if the operand is not compatible with the operator.
     */
    private void checkOperandTypeWithUnaryOperator(ExpressionNode operatorNode, ExpressionNode operandNode)
            throws SyntaxError {
        String operand = operandNode.getValue();
        TypeInfo operandDataType = operandNode.getDataType();
        String operandDataTypeId = operandDataType.getId();
        TypeTable typeTable = TypeTable.getInstance();
        // Type check the operand to determine if it is compatible with the operator
        boolean typeCompatible = typeTable.IsTypeCompatibleUsingUnaryOperator(operatorNode.getValueType(), operandDataType);
        if (!typeCompatible) {
            throw new SyntaxError("'" + operand + "' of type '" + operandDataTypeId +
                    "' is not compatible with the operator '" + operatorNode.getValue() + "'",
                    lexer.getCurrentLine());
        }
        operatorNode.addChild(operandNode);
        operatorNode.setDataType(operandDataType);
    }

    /**
     * Determines if two operands are compatible with the given binary operator and helps construct a subtree.
     *
     * @param operatorNode a node containing the operator.
     * @param operandNode1 the node containing the first operand.
     * @param operandNode2 the node containing the second operand.
     * @throws SyntaxError if the operands are not compatible with the operator.
     */
    private void checkOperandTypesWithBinaryOperators(ExpressionNode operatorNode,
                                                      ExpressionNode operandNode1,
                                                      ExpressionNode operandNode2) throws SyntaxError {
        String operand1 = operandNode1.getValue();
        String operand2 = operandNode2.getValue();
        TypeInfo operandDataType1 = operandNode1.getDataType();
        TypeInfo operandDataType2 = operandNode2.getDataType();
        String operandDataTypeId1 = operandDataType1.getId();
        String operandDataTypeId2 = operandDataType2.getId();
        TypeTable typeTable = TypeTable.getInstance();
        DataTypeNode typeConversionNode;
        TypeConversion typeConversion;
        TypeInfo resultType;
        // Type check the two operands
        boolean typeCompatible = typeTable.AreTypesCompatibleUsingBinaryOperator(
                operatorNode.getValueType(), operandDataType1, operandDataType2);
        if (!typeCompatible) {
            throw new SyntaxError(
                    "'" + operand1 + "' of type '" + operandDataTypeId1 +
                            "' and '" + operand2 + "' of type '" + operandDataTypeId2 +
                            "' are not compatible using the operator '" + operatorNode.getValue() + "'",
                    lexer.getCurrentLine());
        }
        if (operandDataType1 != operandDataType2) {
            // If the two types are not the same but compatible with the given operator,
            // add a conversion node that indicates the type to be converted to
            typeConversion = typeTable.getTypeConversion(operandDataType1, operandDataType2);
            if (!typeConversion.isImplicit()) {
                // If the type conversion is not implicit, there must be type casting
                // If not, throw an exception
                throw new SyntaxError(
                        "Missing type casting from '" + operand1 +
                                "' of type '" + operandDataTypeId1 + "' to '" + operand2 +
                                "' of type '" + operandDataTypeId2 + "'", lexer.getCurrentLine());
            }
            resultType = typeConversion.getResultType();
            typeConversionNode = new DataTypeNode(NodeType.TYPE_CONVERSION, resultType);
            operatorNode.addChild(typeConversionNode);
            operatorNode.setDataType(resultType);
            if (resultType != operandDataType1) {
                typeConversionNode.addChild(operandNode1);
                operatorNode.addChild(operandNode2);
            } else {
                typeConversionNode.addChild(operandNode2);
                operatorNode.addChild(operandNode1);
            }
        } else {
            // If the two types are the same and compatible using the given operator,
            // the final target type must also be the same as the two types
            operatorNode.addChild(operandNode1);
            operatorNode.addChild(operandNode2);
            operatorNode.setDataType(operandDataType1);
        }
    }

    /**
     * Builds an AST using a postfix ordered list of nodes.
     *
     * @param postfixNodes a list of nodes in postfix order.
     * @return the root node of the AST.
     */
    public Node buildASTFromPostFix(ArrayList<ExpressionNode> postfixNodes) throws SyntaxError {
        // If the postfix list is empty, immediately return null
        if (postfixNodes.isEmpty()) {
            return null;
        }

        ArrayDeque<ExpressionNode> tempStack = new ArrayDeque<>();
        TokenType currValueType;
        ExpressionNode operandNode1, operandNode2;

        for (ExpressionNode currNode : postfixNodes) {
            currValueType = currNode.getValueType();
            if (currValueType == TokenType.ID || currValueType == TokenType.INT_LITERAL || currValueType == TokenType.FLOAT_LITERAL) {
                // Push the operand onto the temp stack
                tempStack.add(currNode);
            } else if (OperatorTable.getInstance().isOperatorUnary(currValueType)) {
                // Since this is a unary operator, remove one node from the stack and assign it as the only child
                // of the current operator node
                operandNode1 = tempStack.removeLast();
                checkOperandTypeWithUnaryOperator(currNode, operandNode1);
                // Push the new root node onto the stack
                tempStack.add(currNode);
            } else {
                // The node stores a binary operator
                // Remove two nodes from the stack and assign them as current operator node's children
                operandNode2 = tempStack.removeLast();
                operandNode1 = tempStack.removeLast();
                checkOperandTypesWithBinaryOperators(currNode, operandNode1, operandNode2);
                // Push the new root node onto the stack
                tempStack.add(currNode);
            }
        }

        // If everything works correctly and postfix list is not empty, the temp stack should have one last node
        return tempStack.removeLast();
    }
}
