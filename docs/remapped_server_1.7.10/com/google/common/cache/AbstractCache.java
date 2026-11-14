package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    protected AbstractCache() {}

    @Override
    public V get(K k0, Callable<? extends V> callable) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(Iterable<?> iterable) {
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            if (!linkedhashmap.containsKey(object)) {
                linkedhashmap.put(object, this.getIfPresent(object));
            }
        }

        return ImmutableMap.copyOf(linkedhashmap);
    }

    @Override
    public void put(K k0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void cleanUp() {}

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateAll(Iterable<?> iterable) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            this.invalidate(object);
        }

    }

    @Override
    public void invalidateAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheStats stats() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        throw new UnsupportedOperationException();
    }

    @Beta
    public static final class SimpleStatsCounter implements AbstractCache.StatsCounter {

        private final LongAddable hitCount = LongAddables.create();
        private final LongAddable missCount = LongAddables.create();
        private final LongAddable loadSuccessCount = LongAddables.create();
        private final LongAddable loadExceptionCount = LongAddables.create();
        private final LongAddable totalLoadTime = LongAddables.create();
        private final LongAddable evictionCount = LongAddables.create();

        public SimpleStatsCounter() {}

        @Override
        public void recordHits(int i) {
            this.hitCount.add((long) i);
        }

        @Override
        public void recordMisses(int i) {
            this.missCount.add((long) i);
        }

        @Override
        public void recordLoadSuccess(long i) {
            this.loadSuccessCount.increment();
            this.totalLoadTime.add(i);
        }

        @Override
        public void recordLoadException(long i) {
            this.loadExceptionCount.increment();
            this.totalLoadTime.add(i);
        }

        @Override
        public void recordEviction() {
            this.evictionCount.increment();
        }

        @Override
        public CacheStats snapshot() {
            return new CacheStats(this.hitCount.sum(), this.missCount.sum(), this.loadSuccessCount.sum(), this.loadExceptionCount.sum(), this.totalLoadTime.sum(), this.evictionCount.sum());
        }

        public void incrementBy(AbstractCache.StatsCounter abstractcache_statscounter) {
            CacheStats cachestats = abstractcache_statscounter.snapshot();

            this.hitCount.add(cachestats.hitCount());
            this.missCount.add(cachestats.missCount());
            this.loadSuccessCount.add(cachestats.loadSuccessCount());
            this.loadExceptionCount.add(cachestats.loadExceptionCount());
            this.totalLoadTime.add(cachestats.totalLoadTime());
            this.evictionCount.add(cachestats.evictionCount());
        }
    }

    @Beta
    public interface StatsCounter {

        void recordHits(int i);

        void recordMisses(int i);

        void recordLoadSuccess(long i);

        void recordLoadException(long i);

        void recordEviction();

        CacheStats snapshot();
    }
}
