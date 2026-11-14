package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
abstract class AbstractSortedKeySortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V> {

    AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> sortedmap) {
        super(sortedmap);
    }

    @Override
    public SortedMap<K, Collection<V>> asMap() {
        return (SortedMap) super.asMap();
    }

    @Override
    SortedMap<K, Collection<V>> backingMap() {
        return (SortedMap) super.backingMap();
    }

    @Override
    public SortedSet<K> keySet() {
        return (SortedSet) super.keySet();
    }
}
