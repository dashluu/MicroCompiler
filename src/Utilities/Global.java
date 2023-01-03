package Utilities;

public class Global {
    public static String MUTABLE_ID_DECL = "var";
    public static String IMMUTABLE_ID_DECL = "const";
    public static String INT_TYPE_ID = "Int";
    public static String FLOAT_TYPE_ID = "Float";

    public static String GLOBAL_SCOPE_ID = "global";

    public static Block globalScope = new Block(GLOBAL_SCOPE_ID, null);
}
