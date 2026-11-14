package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
final class SingletonImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {

    final transient K singleKey;
    final transient V singleValue;
    transient ImmutableBiMap<V, K> inverse;

    SingletonImmutableBiMap(K k0, V v0) {
        CollectPreconditions.checkEntryNotNull(k0, v0);
        this.singleKey = k0;
        this.singleValue = v0;
    }

    private SingletonImmutableBiMap(K k0, V v0, ImmutableBiMap<V, K> immutablebimap) {
        this.singleKey = k0;
        this.singleValue = v0;
        this.inverse = immutablebimap;
    }

    SingletonImmutableBiMap(Entry<? extends K, ? extends V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    @Override
    public V get(@Nullable Object object) {
        return this.singleKey.equals(object) ? this.singleValue : null;
    }

    public int size() {
        return 1;
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.singleKey.equals(object);
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return this.singleValue.equals(object);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
    }

    @Override
    ImmutableSet<K> createKeySet() {
        return ImmutableSet.of(this.singleKey);
    }

    @Override
    public ImmutableBiMap<V, K> inverse() {
        ImmutableBiMap immutablebimap = this.inverse;

        return immutablebimap == null ? (this.inverse = new SingletonImmutableBiMap<>(this.singleValue, this.singleKey, this)) : immutablebimap;
    }
}
