package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V> {

    V get(K k0) throws ExecutionException;

    V getUnchecked(K k0);

    ImmutableMap<K, V> getAll(Iterable<? extends K> iterable) throws ExecutionException;

    /** @deprecated */
    @Deprecated
    @Override
    V apply(K k0);

    void refresh(K k0);

    @Override
    ConcurrentMap<K, V> asMap();
}
