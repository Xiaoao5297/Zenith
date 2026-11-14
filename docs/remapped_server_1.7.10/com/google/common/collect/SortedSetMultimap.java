package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public interface SortedSetMultimap<K, V> extends SetMultimap<K, V> {

    @Override
    SortedSet<V> get(@Nullable K k0);

    @Override
    SortedSet<V> removeAll(@Nullable Object object);

    @Override
    SortedSet<V> replaceValues(K k0, Iterable<? extends V> iterable);

    @Override
    Map<K, Collection<V>> asMap();

    Comparator<? super V> valueComparator();
}
