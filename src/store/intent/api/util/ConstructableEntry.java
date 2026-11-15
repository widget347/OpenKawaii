/*
 * Decompiled with CFR 0.152.
 */
package store.intent.api.util;

import java.util.Map;

public class ConstructableEntry<K, V>
implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public ConstructableEntry(K entryKey, V entryValue) {
        this.key = entryKey;
        this.value = entryValue;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V newValue) {
        V oldValue = this.value;
        this.value = newValue;
        return oldValue;
    }
}

