package Symbols;

import Utilities.BaseTable;
import Utilities.Block;
import Utilities.Global;

import java.util.HashMap;

public class SymbolTable implements BaseTable<SymbolInfo, SymbolInfo> {
    private final HashMap<SymbolInfo, SymbolInfo> symbols = new HashMap<>();
    private static SymbolTable symbolTable;
    private static boolean init = false;

    private SymbolTable() {
    }

    public static SymbolTable getInstance() {
        if (!init) {
            symbolTable = new SymbolTable();
            symbolTable.insert(new KeywordInfo(Global.ID_DECL, KeywordInfo.KeywordType.ID_DECL));
            symbolTable.insert(new KeywordInfo(Global.INT_TYPE_ID, KeywordInfo.KeywordType.TYPE));
            init = true;
        }
        return symbolTable;
    }


    @Override
    public boolean contain(SymbolInfo key) {
        return symbols.containsKey(key);
    }

    @Override
    public SymbolInfo find(SymbolInfo key) {
        return symbols.get(key);
    }

    @Override
    public void insert(SymbolInfo value) {
        symbols.put(value, value);
    }

    public boolean isKeyword(String keywordStr) {
        SymbolInfo inputSymbol = new SymbolInfo(keywordStr, SymbolInfo.SymbolType.KEYWORD);
        SymbolInfo outputSymbol = find(inputSymbol);
        return outputSymbol != null && outputSymbol.getSymbolType() == SymbolInfo.SymbolType.KEYWORD;
    }

    /**
     * Determines if a string is a valid keyword in the table.
     *
     * @param keywordStr  the input string to check.
     * @param keywordType type of the expected keyword.
     * @return true if the string is a valid keyword and false otherwise.
     */
    public boolean isValidKeyword(String keywordStr, KeywordInfo.KeywordType keywordType) {
        SymbolInfo inputSymbol = new KeywordInfo(keywordStr, keywordType);
        SymbolInfo outputSymbol = find(inputSymbol);
        if (outputSymbol == null || outputSymbol.getSymbolType() != SymbolInfo.SymbolType.KEYWORD) {
            return false;
        }
        KeywordInfo outputKeyword = (KeywordInfo) outputSymbol;
        return outputKeyword.getKeywordType() == keywordType;
    }

    /**
     * Determines if a string is a valid id in a given scope.
     *
     * @param idStr the string to check.
     * @param scope scope of the expected id.
     * @return true if the string is a valid id and false otherwise.
     */
    public boolean isValidID(String idStr, Block scope) {
        IDInfo inputSymbol = new IDInfo(idStr, scope);
        SymbolInfo outputSymbol = find(inputSymbol);
        if (outputSymbol == null || outputSymbol.getSymbolType() != SymbolInfo.SymbolType.ID) {
            return false;
        }
        IDInfo outputID = (IDInfo) outputSymbol;
        return outputID.equals(inputSymbol);
    }
}
