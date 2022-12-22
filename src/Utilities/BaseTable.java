package Utilities;

public interface BaseTable<K, V> {
    boolean contain(K key);
    V find(K key);
    void insert(V value);
}
