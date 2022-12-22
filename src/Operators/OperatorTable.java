package Operators;

import Utilities.BaseTable;

import java.util.HashMap;

public class OperatorTable implements BaseTable<String, OperatorInfo> {
    private final HashMap<String, OperatorInfo> operators = new HashMap<>();
    private static OperatorTable operatorTable;
    private static boolean init = false;

    private OperatorTable() {
    }

    public static OperatorTable getInstance() {
        if (!init) {
            operatorTable = new OperatorTable();
            operatorTable.insert(new OperatorInfo(";", OperatorInfo.OperatorType.SEMICOLON, 1, false));
            operatorTable.insert(new OperatorInfo("=", OperatorInfo.OperatorType.ASSIGNMENT, 2, false));
            operatorTable.insert(new OperatorInfo("+", OperatorInfo.OperatorType.BINARY, 12, true));
            operatorTable.insert(new OperatorInfo("-", OperatorInfo.OperatorType.BINARY, 12, true));
            operatorTable.insert(new OperatorInfo("*", OperatorInfo.OperatorType.BINARY, 13, true));
            operatorTable.insert(new OperatorInfo("/", OperatorInfo.OperatorType.BINARY, 13, true));
            operatorTable.insert(new OperatorInfo(".", OperatorInfo.OperatorType.DOT, 16, true));
            operatorTable.insert(new OperatorInfo(":", OperatorInfo.OperatorType.COLON, 17, true));
            operatorTable.insert(new OperatorInfo("(", OperatorInfo.OperatorType.LPAREN, 18, true));
            operatorTable.insert(new OperatorInfo(")", OperatorInfo.OperatorType.RPAREN, 18, true));
            init = true;
        }
        return operatorTable;
    }


    @Override
    public boolean contain(String key) {
        return operators.containsKey(key);
    }

    @Override
    public OperatorInfo find(String key) {
        return operators.get(key);
    }

    @Override
    public void insert(OperatorInfo value) {
        operators.put(value.getId(), value);
    }

    public boolean isValidOperator(String opStr, OperatorInfo.OperatorType opType) {
        OperatorInfo info = find(opStr);
        return info != null && info.getType() == opType;
    }
}
