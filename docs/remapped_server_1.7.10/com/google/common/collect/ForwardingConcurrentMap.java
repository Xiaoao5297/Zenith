package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible
public abstract class ForwardingConcurrentMap<K, V> extends ForwardingMap<K, V> implements ConcurrentMap<K, V> {

    protected ForwardingConcurrentMap() {}

    @Override
    protected abstract ConcurrentMap<K, V> delegate();

    public V putIfAbsent(K k0, V v0) {
        return this.delegate().putIfAbsent(k0, v0);
    }

    public boolean remove(Object object, Object object1) {
        return this.delegate().remove(object, object1);
    }

    public V replace(K k0, V v0) {
        return this.delegate().replace(k0, v0);
    }

    public boolean replace(K k0, V v0, V v1) {
        return this.delegate().replace(k0, v0, v1);
    }
}
