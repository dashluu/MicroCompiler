package ParserSpace;

import Exceptions.SymbolTableException;
import Exceptions.SyntaxError;
import LexerSpace.Lexer;
import Symbols.SymbolTable;
import Utilities.Block;
import Utilities.Node;
import Utilities.Token;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class ExpressionParser {
    private final Lexer lexer;
    private int numParen = 0;

    public ExpressionParser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Consumes an expression in a given scope and produces an AST root node associated with the parsed expression.
     *
     * @param scope scope of the expression.
     * @return an AST node that represents the root of the AST.
     * @throws SyntaxError if there is a syntax error.
     * @throws IOException if the read operation causes an IO error.
     */
    public Node parseExpression(Block scope) throws SyntaxError, IOException, SymbolTableException {
        ArrayList<Node> infixNodes = getExpressionInfixNodes(scope);
        ArrayList<Node> postfixNodes = getPostfixOrder(infixNodes);
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
    public ArrayList<Node> getExpressionInfixNodes(Block scope) throws SyntaxError, IOException, SymbolTableException {
        ArrayList<Node> nodes = new ArrayList<>();
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
    private void recurParseExpressionHelper(ArrayList<Node> nodes, Block scope, String str)
            throws SyntaxError, IOException, SymbolTableException {
        // If the size of the list is unchanged, an expression is clearly missing
        int numNodesBefore = nodes.size();
        // Recursively parse an expression
        recurParseExpression(nodes, scope);
        if (nodes.size() == numNodesBefore) {
            throw new SyntaxError("Missing a valid expression after '" + str + "'", lexer.getCurrLine());
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
    private void recurParseExpression(ArrayList<Node> nodes, Block scope) throws SyntaxError, IOException, SymbolTableException {
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
        Token.TokenType tokenType = currToken.getType();

        if (tokenType != Token.TokenType.LPAREN && tokenType != Token.TokenType.BINARY_UNARY &&
                tokenType != Token.TokenType.ID && tokenType != Token.TokenType.NUM) {
            throw new SyntaxError("Invalid expression syntax", lexer.getCurrLine());
        }

        String currTokenStr = currToken.getValue();

        if (tokenType == Token.TokenType.ID) {
            // Check if the token is a valid ID
            if (!symbolTable.isID(currTokenStr, scope)) {
                throw new SyntaxError("Invalid variable '" + currTokenStr + "'", lexer.getCurrLine());
            }
            nodes.add(new Node(currToken));
        } else if (tokenType == Token.TokenType.NUM) {
            // Consume a number
            nodes.add(new Node(currToken));
        } else if (tokenType == Token.TokenType.BINARY_UNARY) {
            // Consume unary operator
            currToken.setType(Token.TokenType.UNARY);
            nodes.add(new Node(currToken));
            // Recursively parse an expression
            recurParseExpressionHelper(nodes, scope, currTokenStr);
        } else {
            // Consume '(' and increment the number of parentheses
            nodes.add(new Node(currToken));
            ++numParen;
            // Recursively parse an expression
            recurParseExpressionHelper(nodes, scope, currTokenStr);
            // Consume ')' and decrement the number of parentheses
            currToken = lexer.getNextToken();
            if (currToken == null || currToken.getType() != Token.TokenType.RPAREN) {
                throw new SyntaxError("Missing ')'", lexer.getCurrLine());
            }
            nodes.add(new Node(currToken));
            --numParen;
        }

        currToken = lexer.getNextToken();
        // Check if the next token is empty or ')'
        if (currToken == null) {
            return;
        } else if (currToken.getType() == Token.TokenType.RPAREN) {
            // Check if ')' is redundant
            if (numParen > 0) {
                lexer.putBack(currToken.getValue());
                return;
            } else {
                throw new SyntaxError("Redundant ')'", lexer.getCurrLine());
            }
        }

        // Check if the token is a valid binary operator
        currTokenStr = currToken.getValue();
        if (!symbolTable.isOperatorBinaryOrUnary(currTokenStr)) {
            throw new SyntaxError("Invalid binary operator '" + currTokenStr + "'", lexer.getCurrLine());
        }
        currToken.setType(Token.TokenType.BINARY);
        nodes.add(new Node(currToken));

        // Recursively parse an expression
        recurParseExpressionHelper(nodes, scope, currTokenStr);
    }

    /**
     * Gets a list of nodes in postfix order.
     *
     * @param nodes list of input nodes.
     * @return a list of nodes in postfix order.
     */
    private ArrayList<Node> getPostfixOrder(ArrayList<Node> nodes) throws SymbolTableException {
        SymbolTable symbolTable = SymbolTable.getInstance();
        ArrayList<Node> postfixNodes = new ArrayList<>();
        ArrayDeque<Node> opStack = new ArrayDeque<>();
        Node opNode;
        Token currToken;
        Token.TokenType currTokenType;
        Token.TokenType opTokenType;
        String opStr;
        boolean stop;
        int precedCmp;

        for (Node currNode : nodes) {
            currToken = currNode.getToken();
            currTokenType = currToken.getType();
            // The token is an operand so push it directly to the postfix list
            if (currTokenType == Token.TokenType.ID || currTokenType == Token.TokenType.NUM) {
                postfixNodes.add(currNode);
            } else if (currTokenType == Token.TokenType.LPAREN) {
                opStack.add(currNode);
            } else if (currTokenType == Token.TokenType.RPAREN) {
                stop = false;
                // Pop the operator stack until '(' is encountered, discard it without adding it to the postfix list
                while (!opStack.isEmpty() && !stop) {
                    opNode = opStack.removeLast();
                    opTokenType = opNode.getToken().getType();
                    stop = opTokenType == Token.TokenType.LPAREN;
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
                    opTokenType = opNode.getToken().getType();
                    stop = opTokenType == Token.TokenType.LPAREN;
                    if (!stop) {
                        opStr = opNode.getToken().getValue();
                        precedCmp = symbolTable.compareOperatorPreced(opStr, currToken.getValue());
                        stop = precedCmp < 0 || (precedCmp == 0 && !symbolTable.isOperatorLeftToRight(opStr));
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
     * Builds an AST using a postfix ordered list of nodes.
     *
     * @param postfixNodes a list of nodes in postfix order.
     * @return the root node of the AST.
     */
    private Node buildASTFromPostFix(ArrayList<Node> postfixNodes) {
        // If the postfix list is empty, immediately return null
        if (postfixNodes.isEmpty()) {
            return null;
        }

        ArrayDeque<Node> tempStack = new ArrayDeque<>();
        Token currToken;
        Token.TokenType currTokenType;
        Node operandNode1;
        Node operandNode2;

        for (Node currNode : postfixNodes) {
            currToken = currNode.getToken();
            currTokenType = currToken.getType();
            if (currTokenType == Token.TokenType.ID || currTokenType == Token.TokenType.NUM) {
                // Push the operand onto the temp stack
                tempStack.add(currNode);
            } else if (currTokenType == Token.TokenType.UNARY) {
                // Since this is a unary operator, remove one node from the stack and assign it as the only child
                // of the current operator node
                operandNode1 = tempStack.removeLast();
                currNode.addChild(operandNode1);
                // Push the new root node onto the stack
                tempStack.add(currNode);
            } else {
                // The node stores a binary operator
                // Remove two nodes from the stack and assign them as current operator node's children
                operandNode1 = tempStack.removeLast();
                operandNode2 = tempStack.removeLast();
                currNode.addChild(operandNode1);
                currNode.addChild(operandNode2);
                // Push the new root node onto the stack
                tempStack.add(currNode);
            }
        }

        // If everything works correctly and postfix list is not empty, the temp stack should have one last node
        // that is the root of the AST
        return tempStack.removeLast();
    }
}
