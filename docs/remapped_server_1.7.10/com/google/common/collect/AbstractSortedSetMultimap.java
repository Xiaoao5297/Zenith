package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements SortedSetMultimap<K, V> {

    private static final long serialVersionUID = 430848587173315748L;

    protected AbstractSortedSetMultimap(Map<K, Collection<V>> map) {
        super(map);
    }

    @Override
    abstract SortedSet<V> createCollection();

    @Override
    SortedSet<V> createUnmodifiableEmptyCollection() {
        Comparator comparator = this.valueComparator();

        return (SortedSet) (comparator == null ? Collections.unmodifiableSortedSet(this.createCollection()) : ImmutableSortedSet.emptySet(this.valueComparator()));
    }

    @Override
    public SortedSet<V> get(@Nullable K k0) {
        return (SortedSet) super.get(k0);
    }

    @Override
    public SortedSet<V> removeAll(@Nullable Object object) {
        return (SortedSet) super.removeAll(object);
    }

    @Override
    public SortedSet<V> replaceValues(@Nullable K k0, Iterable<? extends V> iterable) {
        return (SortedSet) super.replaceValues(k0, iterable);
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }
}
