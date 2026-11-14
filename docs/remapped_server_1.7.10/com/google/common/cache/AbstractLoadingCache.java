package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

@Beta
public abstract class AbstractLoadingCache<K, V> extends AbstractCache<K, V> implements LoadingCache<K, V> {

    protected AbstractLoadingCache() {}

    @Override
    public V getUnchecked(K k0) {
        try {
            return this.get(k0);
        } catch (ExecutionException executionexception) {
            throw new UncheckedExecutionException(executionexception.getCause());
        }
    }

    @Override
    public ImmutableMap<K, V> getAll(Iterable<? extends K> iterable) throws ExecutionException {
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            if (!linkedhashmap.containsKey(object)) {
                linkedhashmap.put(object, this.get(object));
            }
        }

        return ImmutableMap.copyOf(linkedhashmap);
    }

    @Override
    public final V apply(K k0) {
        return this.getUnchecked(k0);
    }

    @Override
    public void refresh(K k0) {
        throw new UnsupportedOperationException();
    }
}
