package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V> implements ListMultimap<K, V> {

    protected ForwardingListMultimap() {}

    @Override
    protected abstract ListMultimap<K, V> delegate();

    @Override
    public List<V> get(@Nullable K k0) {
        return this.delegate().get(k0);
    }

    @Override
    public List<V> removeAll(@Nullable Object object) {
        return this.delegate().removeAll(object);
    }

    @Override
    public List<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k0, iterable);
    }
}
