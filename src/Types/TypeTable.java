package Types;

import Utilities.BaseTable;
import Utilities.Global;

import java.util.HashMap;

public class TypeTable implements BaseTable<String, TypeInfo> {
    private final HashMap<String, TypeInfo> types = new HashMap<>();
    private static TypeTable typeTable;
    private static boolean init = false;

    private TypeTable() {
    }

    public static TypeTable getInstance() {
        if (!init) {
            typeTable = new TypeTable();
            typeTable.insert(new TypeInfo(Global.INT_TYPE_ID, 4, TypeInfo.Type.PRIMITIVE));
            typeTable.insert(new TypeInfo(Global.FLOAT_TYPE_ID, 4, TypeInfo.Type.PRIMITIVE));
            init = true;
        }
        return typeTable;
    }


    @Override
    public boolean contain(String key) {
        return types.containsKey(key);
    }

    @Override
    public TypeInfo find(String key) {
        return types.get(key);
    }

    @Override
    public void insert(TypeInfo value) {
        types.put(value.getId(), value);
    }
}
