package Symbols;

import Exceptions.SymbolTableException;
import Utilities.Block;
import Utilities.Global;
import Utilities.Token;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<SymbolInfo, SymbolInfo> symbols = new HashMap<>();
    private static SymbolTable symbolTable;
    private static boolean init = false;

    private SymbolTable() {
    }

    public static SymbolTable getInstance() {
        if (!init) {
            symbolTable = new SymbolTable();
            symbolTable.insert(new KeywordInfo(new Token(Global.ID_DECL, Token.TokenType.ID_DECL)));
            symbolTable.insert(new TypeInfo(new Token(Global.INT_TYPE_ID, Token.TokenType.INT_TYPE)));
            symbolTable.insert(new TypeInfo(new Token(Global.FLOAT_TYPE_ID, Token.TokenType.FLOAT_TYPE)));
            symbolTable.insert(new OperatorInfo(new Token("+", Token.TokenType.BINARY_UNARY), 0, true));
            symbolTable.insert(new OperatorInfo(new Token("-", Token.TokenType.BINARY_UNARY), 0, true));
            symbolTable.insert(new OperatorInfo(new Token("*", Token.TokenType.BINARY), 0, true));
            symbolTable.insert(new OperatorInfo(new Token("/", Token.TokenType.BINARY), 0, true));
            symbolTable.insert(new OperatorInfo(new Token(".", Token.TokenType.DOT), 0, true));
            symbolTable.insert(new OperatorInfo(new Token(":", Token.TokenType.COLON), 0, true));
            symbolTable.insert(new OperatorInfo(new Token("(", Token.TokenType.LPAREN), 0, true));
            symbolTable.insert(new OperatorInfo(new Token(")", Token.TokenType.RPAREN), 0, true));
            symbolTable.insert(new OperatorInfo(new Token(";", Token.TokenType.SEMICOLON), 0, false));
            symbolTable.insert(new OperatorInfo(new Token("=", Token.TokenType.ASSIGNMENT), 0, false));
            init = true;
        }
        return symbolTable;
    }

    /**
     * Finds the symbol in the table associated with the given key.
     *
     * @param key a dummy symbol.
     * @return the symbol associated with the given key.
     */
    private SymbolInfo find(SymbolInfo key) {
        return symbols.get(key);
    }

    /**
     * Inserts a new symbol into the table.
     *
     * @param value the new symbol.
     * @return the old symbol if one exists and null otherwise.
     */
    public SymbolInfo insert(SymbolInfo value) {
        return symbols.put(value, value);
    }

    /**
     * Finds a symbol based on the key string and the symbol type.
     *
     * @param keyStr     the string that identifies the symbol.
     * @param symbolType the type of the symbol.
     * @return a symbol if one exists in the table and null otherwise.
     */
    private SymbolInfo findSymbol(String keyStr, SymbolInfo.SymbolType symbolType) {
        Token dummyToken = new Token(keyStr);
        SymbolInfo dummyInfo = new SymbolInfo(dummyToken, symbolType);
        return find(dummyInfo);
    }

    /**
     * Finds an ID symbol based on the given key string and the scope.
     *
     * @param keyStr the string that identifies the ID.
     * @param scope  the scope of the ID.
     * @return an ID symbol if one exists in the table and null otherwise.
     */
    private SymbolInfo findID(String keyStr, Block scope) {
        Token dummyToken = new Token(keyStr);
        IDInfo dummyInfo = new IDInfo(dummyToken, scope);
        return find(dummyInfo);
    }

    /**
     * Determines if a string is a valid operator in the symbol table.
     *
     * @param opStr the string to be checked.
     * @return true if the given string is an operator and false otherwise.
     */
    public boolean isOperator(String opStr) {
        SymbolInfo info = findSymbol(opStr, SymbolInfo.SymbolType.OPERATOR);
        return info != null;
    }

    /**
     * Determines if a string is a valid ID in the symbol table.
     *
     * @param idStr the string to be checked.
     * @param scope the scope of the supposed ID.
     * @return true if the given string is a valid ID and false otherwise.
     */
    public boolean isID(String idStr, Block scope) {
        SymbolInfo info = findID(idStr, scope);
        return info != null;
    }

    /**
     * Gets a keyword symbol in the symbol table.
     *
     * @param keywordStr the string that identifies the keyword.
     * @return a keyword symbol if one exists and null otherwise.
     */
    public SymbolInfo getKeyword(String keywordStr) {
        return findSymbol(keywordStr, SymbolInfo.SymbolType.KEYWORD);
    }

    /**
     * Gets an operator symbol in the symbol table.
     *
     * @param opStr the string that identifies the operator.
     * @return an operator symbol if one exists and null otherwise.
     */
    public SymbolInfo getOperator(String opStr) {
        return findSymbol(opStr, SymbolInfo.SymbolType.OPERATOR);
    }

    /**
     * Gets a type symbol in the symbol table.
     *
     * @param typeStr the string that identifies the type.
     * @return a type symbol if one exists and null otherwise.
     */
    public SymbolInfo getType(String typeStr) {
        return findSymbol(typeStr, SymbolInfo.SymbolType.TYPE);
    }

    /**
     * Determines if an operator is left-to-right.
     *
     * @param opStr the operator string.
     * @return true if the operator is left-to-right and false otherwise.
     * @throws SymbolTableException if the operator is not found in the symbol table.
     */
    public boolean isOperatorLeftToRight(String opStr) throws SymbolTableException {
        SymbolInfo opSymbol = getOperator(opStr);
        if (opSymbol == null) {
            throw new SymbolTableException("'" + opStr + "' is not a valid operator in the symbol table");
        }
        OperatorInfo opInfo = (OperatorInfo) opSymbol;
        return opInfo.isLeftToRight();
    }

    /**
     * Determines if an operator is binary.
     *
     * @param opStr the operator string.
     * @return true if the operator is binary and false otherwise.
     * @throws SymbolTableException if the operator is not found in the symbol table.
     */
    public boolean isOperatorBinaryOrUnary(String opStr) throws SymbolTableException {
        SymbolInfo opSymbol = getOperator(opStr);
        if (opSymbol == null) {
            throw new SymbolTableException("'" + opStr + "' is not a valid operator in the symbol table");
        }
        OperatorInfo opInfo = (OperatorInfo) opSymbol;
        Token.TokenType opTokenType = opInfo.getToken().getType();
        return opTokenType == Token.TokenType.BINARY || opTokenType == Token.TokenType.BINARY_UNARY;
    }

    /**
     * Compares the operators' precedences.
     *
     * @param opStr1 the first operator string.
     * @param opStr2 the second operator string.
     * @return -1, 0, 1 if the first operator's precedence is less than, equal to, or greater than
     * that of the second operator respectively.
     * @throws SymbolTableException if the one of the operators is not found in the symbol table.
     */
    public int compareOperatorPreced(String opStr1, String opStr2) throws SymbolTableException {
        SymbolInfo opSymbol1 = getOperator(opStr1);
        if (opSymbol1 == null) {
            throw new SymbolTableException("'" + opStr1 + "' is not a valid operator in the symbol table");
        }
        SymbolInfo opSymbol2 = getOperator(opStr2);
        if (opSymbol2 == null) {
            throw new SymbolTableException("'" + opStr2 + "' is not a valid operator in the symbol table");
        }
        OperatorInfo opInfo1 = (OperatorInfo) opSymbol1;
        OperatorInfo opInfo2 = (OperatorInfo) opSymbol2;
        Token.TokenType opTokenType1 = opInfo1.getToken().getType();
        Token.TokenType opTokenType2 = opInfo2.getToken().getType();
        if (opTokenType1 == Token.TokenType.UNARY) {
            return 1;
        } else if (opTokenType2 == Token.TokenType.UNARY) {
            return -1;
        }
        return Integer.compare(opInfo1.getPreced(), opInfo2.getPreced());
    }
}
