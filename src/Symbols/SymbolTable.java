package Symbols;

import Utilities.Block;
import Utilities.Global;
import Utilities.TokenType;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<SymbolInfo, SymbolInfo> symbols = new HashMap<>();
    private static final SymbolTable symbolTable = new SymbolTable();
    private static boolean init = false;

    private SymbolTable() {
    }

    /**
     * Initializes the only instance of the symbol table if it has not been initialized and then returns it.
     *
     * @return a SymbolTable object.
     */
    public static SymbolTable getInstance() {
        if (!init) {
            // Initialize the symbol table
            symbolTable.set(new KeywordInfo(Global.MUTABLE_ID_DECL, TokenType.MUTABLE_ID_DECL));
            symbolTable.set(new KeywordInfo(Global.IMMUTABLE_ID_DECL, TokenType.IMMUTABLE_ID_DECL));
            symbolTable.set(new TypeInfo(Global.INT_TYPE_ID, TokenType.INT_TYPE));
            symbolTable.set(new TypeInfo(Global.FLOAT_TYPE_ID, TokenType.FLOAT_TYPE));
            symbolTable.set(new OperatorInfo("+", TokenType.ADD));
            symbolTable.set(new OperatorInfo("-", TokenType.SUB));
            symbolTable.set(new OperatorInfo("*", TokenType.MULT));
            symbolTable.set(new OperatorInfo("/", TokenType.DIV));
            symbolTable.set(new OperatorInfo(".", TokenType.DOT));
            symbolTable.set(new OperatorInfo(":", TokenType.COLON));
            symbolTable.set(new OperatorInfo("(", TokenType.LPAREN));
            symbolTable.set(new OperatorInfo(")", TokenType.RPAREN));
            symbolTable.set(new OperatorInfo(";", TokenType.SEMICOLON));
            symbolTable.set(new OperatorInfo("=", TokenType.ASSIGNMENT));

            init = true;
        }
        return symbolTable;
    }

    /**
     * Gets the symbol in the table associated with the given key.
     *
     * @param key a dummy symbol.
     * @return the symbol associated with the given key.
     */
    private SymbolInfo get(SymbolInfo key) {
        return symbols.get(key);
    }

    /**
     * Inserts a new symbol into the table if it does not exist, otherwise, replace the old symbol with the new one.
     *
     * @param symbol the symbol to be set.
     * @return the old symbol if one exists and null otherwise.
     */
    public SymbolInfo set(SymbolInfo symbol) {
        return symbols.put(symbol, symbol);
    }

    /**
     * Gets a symbol based on the key string and the symbol type.
     *
     * @param keyStr     the string that identifies the symbol.
     * @param symbolType the type of the symbol.
     * @return a symbol if one exists in the table and null otherwise.
     */
    private SymbolInfo getSymbol(String keyStr, SymbolType symbolType) {
        SymbolInfo dummy = new SymbolInfo(keyStr, TokenType.UNKNOWN, symbolType);
        return get(dummy);
    }

    /**
     * Gets an ID symbol based on the given key string and the scope.
     *
     * @param keyStr the string that identifies the ID.
     * @param scope  the scope of the ID.
     * @return an ID symbol if one exists in the table and null otherwise.
     */
    public SymbolInfo getID(String keyStr, Block scope) {
        IDInfo dummy = new IDInfo(keyStr, scope, null, true);
        return get(dummy);
    }

    /**
     * Gets a keyword symbol in the symbol table.
     *
     * @param keywordStr the string that identifies the keyword.
     * @return a keyword symbol if one exists and null otherwise.
     */
    public SymbolInfo getKeyword(String keywordStr) {
        return getSymbol(keywordStr, SymbolType.KEYWORD);
    }

    /**
     * Gets an operator symbol in the symbol table.
     *
     * @param opStr the string that identifies the operator.
     * @return an operator symbol if one exists and null otherwise.
     */
    public SymbolInfo getOperator(String opStr) {
        return getSymbol(opStr, SymbolType.OPERATOR);
    }

    /**
     * Gets a type symbol in the symbol table.
     *
     * @param typeStr the string that identifies the type.
     * @return a type symbol if one exists and null otherwise.
     */
    public SymbolInfo getType(String typeStr) {
        return getSymbol(typeStr, SymbolType.TYPE);
    }

    /**
     * Determines if a string is a valid operator in the symbol table.
     *
     * @param opStr the string to be checked.
     * @return true if the given string is an operator and false otherwise.
     */
    public boolean isOperator(String opStr) {
        SymbolInfo symbol = getSymbol(opStr, SymbolType.OPERATOR);
        return symbol != null;
    }

    /**
     * Determines if a string is a valid ID in the symbol table.
     *
     * @param idStr the string to be checked.
     * @param scope the scope of the supposed ID.
     * @return true if the given string is a valid ID and false otherwise.
     */
    public boolean isID(String idStr, Block scope) {
        SymbolInfo symbol = getID(idStr, scope);
        return symbol != null;
    }

    /**
     * Determines if a string is a valid keyword in the symbol table.
     *
     * @param keywordStr the string to be checked.
     * @return true if the given string is a valid keyword and false otherwise.
     */
    public boolean isKeyword(String keywordStr) {
        SymbolInfo symbol = getSymbol(keywordStr, SymbolType.KEYWORD);
        return symbol != null;
    }

    /**
     * Determines if a string is a valid type in the symbol table.
     *
     * @param typeStr the string to be checked.
     * @return true if the given string is a valid type and false otherwise.
     */
    public boolean isType(String typeStr) {
        SymbolInfo symbol = getSymbol(typeStr, SymbolType.TYPE);
        return symbol != null;
    }
}
