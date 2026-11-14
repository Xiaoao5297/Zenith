package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;

@Beta
public abstract class ForwardingLoadingCache<K, V> extends ForwardingCache<K, V> implements LoadingCache<K, V> {

    protected ForwardingLoadingCache() {}

    @Override
    protected abstract LoadingCache<K, V> delegate();

    @Override
    public V get(K k0) throws ExecutionException {
        return this.delegate().get(k0);
    }

    @Override
    public V getUnchecked(K k0) {
        return this.delegate().getUnchecked(k0);
    }

    @Override
    public ImmutableMap<K, V> getAll(Iterable<? extends K> iterable) throws ExecutionException {
        return this.delegate().getAll(iterable);
    }

    @Override
    public V apply(K k0) {
        return this.delegate().apply(k0);
    }

    @Override
    public void refresh(K k0) {
        this.delegate().refresh(k0);
    }

    @Beta
    public abstract static class SimpleForwardingLoadingCache<K, V> extends ForwardingLoadingCache<K, V> {

        private final LoadingCache<K, V> delegate;

        protected SimpleForwardingLoadingCache(LoadingCache<K, V> loadingcache) {
            this.delegate = (LoadingCache) Preconditions.checkNotNull(loadingcache);
        }

        @Override
        protected final LoadingCache<K, V> delegate() {
            return this.delegate;
        }
    }
}
