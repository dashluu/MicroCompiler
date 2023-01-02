package Utilities;

public record Block(String id, Block parent) {
    @Override
    public String toString() {
        String str = "block '" + id + "'";
        return parent == null ? str : str + " in " + parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Block block)) {
            return false;
        }
        return id.equals(block.id);
    }
}
