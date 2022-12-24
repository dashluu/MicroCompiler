package Symbols;

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
}
