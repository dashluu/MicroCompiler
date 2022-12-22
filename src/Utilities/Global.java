package Utilities;

public class Global {
    public static String ID_DECL = "var";
    public static String INT_TYPE_ID = "int";
    public static String FLOAT_TYPE_ID = "float";

    public static String GLOBAL_SCOPE_ID = "global";

    public static Block globalScope = new Block(GLOBAL_SCOPE_ID, null);
}
