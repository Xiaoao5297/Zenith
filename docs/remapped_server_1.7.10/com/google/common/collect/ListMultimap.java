package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public interface ListMultimap<K, V> extends Multimap<K, V> {

    @Override
    List<V> get(@Nullable K k0);

    @Override
    List<V> removeAll(@Nullable Object object);

    @Override
    List<V> replaceValues(K k0, Iterable<? extends V> iterable);

    @Override
    Map<K, Collection<V>> asMap();

    @Override
    boolean equals(@Nullable Object object);
}
