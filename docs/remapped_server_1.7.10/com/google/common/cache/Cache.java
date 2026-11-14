package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public interface Cache<K, V> {

    @Nullable
    V getIfPresent(Object object);

    V get(K k0, Callable<? extends V> callable) throws ExecutionException;

    ImmutableMap<K, V> getAllPresent(Iterable<?> iterable);

    void put(K k0, V v0);

    void putAll(Map<? extends K, ? extends V> map);

    void invalidate(Object object);

    void invalidateAll(Iterable<?> iterable);

    void invalidateAll();

    long size();

    CacheStats stats();

    ConcurrentMap<K, V> asMap();

    void cleanUp();
}
