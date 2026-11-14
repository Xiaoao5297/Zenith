package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V> {

    private static final long serialVersionUID = 6588350623831699109L;

    protected AbstractListMultimap(Map<K, Collection<V>> map) {
        super(map);
    }

    @Override
    abstract List<V> createCollection();

    @Override
    List<V> createUnmodifiableEmptyCollection() {
        return ImmutableList.of();
    }

    @Override
    public List<V> get(@Nullable K k0) {
        return (List) super.get(k0);
    }

    @Override
    public List<V> removeAll(@Nullable Object object) {
        return (List) super.removeAll(object);
    }

    @Override
    public List<V> replaceValues(@Nullable K k0, Iterable<? extends V> iterable) {
        return (List) super.replaceValues(k0, iterable);
    }

    @Override
    public boolean put(@Nullable K k0, @Nullable V v0) {
        return super.put(k0, v0);
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return super.equals(object);
    }
}
