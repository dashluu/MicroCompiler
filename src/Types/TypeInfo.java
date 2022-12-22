package Types;

public class TypeInfo {
    public enum Type {
        UNKNOWN, PRIMITIVE
    }

    private final String id;
    private final int size;
    private final Type type;

    public TypeInfo(String id, int size, Type type) {
        this.id = id;
        this.size = size;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public Type getType() {
        return type;
    }
}
