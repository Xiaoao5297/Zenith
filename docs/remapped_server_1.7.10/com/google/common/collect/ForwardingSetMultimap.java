package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSetMultimap<K, V> extends ForwardingMultimap<K, V> implements SetMultimap<K, V> {

    public ForwardingSetMultimap() {}

    @Override
    protected abstract SetMultimap<K, V> delegate();

    @Override
    public Set<Entry<K, V>> entries() {
        return this.delegate().entries();
    }

    @Override
    public Set<V> get(@Nullable K k0) {
        return this.delegate().get(k0);
    }

    @Override
    public Set<V> removeAll(@Nullable Object object) {
        return this.delegate().removeAll(object);
    }

    @Override
    public Set<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k0, iterable);
    }
}
