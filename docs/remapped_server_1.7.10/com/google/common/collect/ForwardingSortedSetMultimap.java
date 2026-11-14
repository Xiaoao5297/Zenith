package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSetMultimap<K, V> extends ForwardingSetMultimap<K, V> implements SortedSetMultimap<K, V> {

    protected ForwardingSortedSetMultimap() {}

    @Override
    protected abstract SortedSetMultimap<K, V> delegate();

    @Override
    public SortedSet<V> get(@Nullable K k0) {
        return this.delegate().get(k0);
    }

    @Override
    public SortedSet<V> removeAll(@Nullable Object object) {
        return this.delegate().removeAll(object);
    }

    @Override
    public SortedSet<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k0, iterable);
    }

    @Override
    public Comparator<? super V> valueComparator() {
        return this.delegate().valueComparator();
    }
}
