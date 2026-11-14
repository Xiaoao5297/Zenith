package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements Serializable {

    final K key;
    final V value;
    private static final long serialVersionUID = 0L;

    ImmutableEntry(@Nullable K k0, @Nullable V v0) {
        this.key = k0;
        this.value = v0;
    }

    @Nullable
    @Override
    public final K getKey() {
        return this.key;
    }

    @Nullable
    @Override
    public final V getValue() {
        return this.value;
    }

    @Override
    public final V setValue(V v0) {
        throw new UnsupportedOperationException();
    }
}
