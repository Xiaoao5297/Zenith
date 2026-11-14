package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V> {

    @Override
    Set<V> get(@Nullable K k0);

    @Override
    Set<V> removeAll(@Nullable Object object);

    @Override
    Set<V> replaceValues(K k0, Iterable<? extends V> iterable);

    @Override
    Set<Entry<K, V>> entries();

    @Override
    Map<K, Collection<V>> asMap();

    @Override
    boolean equals(@Nullable Object object);
}
