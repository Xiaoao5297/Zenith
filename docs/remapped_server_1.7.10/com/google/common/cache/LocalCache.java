package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

@GwtCompatible(emulated = true)
class LocalCache<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int CONTAINS_VALUE_RETRIES = 3;
    static final int DRAIN_THRESHOLD = 63;
    static final int DRAIN_MAX = 16;
    static final Logger logger = Logger.getLogger(LocalCache.class.getName());
    static final ListeningExecutorService sameThreadExecutor = MoreExecutors.sameThreadExecutor();
    final int segmentMask;
    final int segmentShift;
    final LocalCache.Segment<K, V>[] segments;
    final int concurrencyLevel;
    final Equivalence<Object> keyEquivalence;
    final Equivalence<Object> valueEquivalence;
    final LocalCache.Strength keyStrength;
    final LocalCache.Strength valueStrength;
    final long maxWeight;
    final Weigher<K, V> weigher;
    final long expireAfterAccessNanos;
    final long expireAfterWriteNanos;
    final long refreshNanos;
    final Queue<RemovalNotification<K, V>> removalNotificationQueue;
    final RemovalListener<K, V> removalListener;
    final Ticker ticker;
    final LocalCache.EntryFactory entryFactory;
    final AbstractCache.StatsCounter globalStatsCounter;
    @Nullable
    final CacheLoader<? super K, V> defaultLoader;
    static final LocalCache.ValueReference<Object, Object> UNSET = new LocalCache.ValueReference<Object, Object>() {
        @Override
        public Object get() {
            return null;
        }

        @Override
        public int getWeight() {
            return 0;
        }

        @Override
        public LocalCache.ReferenceEntry<Object, Object> getEntry() {
            return null;
        }

        @Override
        public LocalCache.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> referencequeue, @Nullable Object object, LocalCache.ReferenceEntry<Object, Object> localcache_referenceentry) {
            return this;
        }

        @Override
        public boolean isLoading() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public Object waitForValue() {
            return null;
        }

        @Override
        public void notifyNewValue(Object object) {}
    };
    static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue<Object>() {
        public boolean offer(Object object) {
            return true;
        }

        public Object peek() {
            return null;
        }

        public Object poll() {
            return null;
        }

        public int size() {
            return 0;
        }

        public Iterator<Object> iterator() {
            return Iterators.emptyIterator();
        }
    };
    Set<K> keySet;
    Collection<V> values;
    Set<Entry<K, V>> entrySet;

    LocalCache(CacheBuilder<? super K, ? super V> cachebuilder, @Nullable CacheLoader<? super K, V> cacheloader) {
        this.concurrencyLevel = Math.min(cachebuilder.getConcurrencyLevel(), 65536);
        this.keyStrength = cachebuilder.getKeyStrength();
        this.valueStrength = cachebuilder.getValueStrength();
        this.keyEquivalence = cachebuilder.getKeyEquivalence();
        this.valueEquivalence = cachebuilder.getValueEquivalence();
        this.maxWeight = cachebuilder.getMaximumWeight();
        this.weigher = cachebuilder.getWeigher();
        this.expireAfterAccessNanos = cachebuilder.getExpireAfterAccessNanos();
        this.expireAfterWriteNanos = cachebuilder.getExpireAfterWriteNanos();
        this.refreshNanos = cachebuilder.getRefreshNanos();
        this.removalListener = cachebuilder.getRemovalListener();
        this.removalNotificationQueue = (Queue) (this.removalListener == CacheBuilder.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
        this.ticker = cachebuilder.getTicker(this.recordsTime());
        this.entryFactory = LocalCache.EntryFactory.getFactory(this.keyStrength, this.usesAccessEntries(), this.usesWriteEntries());
        this.globalStatsCounter = (AbstractCache.StatsCounter) cachebuilder.getStatsCounterSupplier().get();
        this.defaultLoader = cacheloader;
        int i = Math.min(cachebuilder.getInitialCapacity(), 1073741824);

        if (this.evictsBySize() && !this.customWeigher()) {
            i = Math.min(i, (int) this.maxWeight);
        }

        int j = 0;

        int k;

        for (k = 1; k < this.concurrencyLevel && (!this.evictsBySize() || (long) (k * 20) <= this.maxWeight); k <<= 1) {
            ++j;
        }

        this.segmentShift = 32 - j;
        this.segmentMask = k - 1;
        this.segments = this.newSegmentArray(k);
        int l = i / k;

        if (l * k < i) {
            ++l;
        }

        int i1;

        for (i1 = 1; i1 < l; i1 <<= 1) {
            ;
        }

        if (this.evictsBySize()) {
            long j1 = this.maxWeight / (long) k + 1L;
            long k1 = this.maxWeight % (long) k;

            for (int l1 = 0; l1 < this.segments.length; ++l1) {
                if ((long) l1 == k1) {
                    --j1;
                }

                this.segments[l1] = this.createSegment(i1, j1, (AbstractCache.StatsCounter) cachebuilder.getStatsCounterSupplier().get());
            }
        } else {
            for (int i2 = 0; i2 < this.segments.length; ++i2) {
                this.segments[i2] = this.createSegment(i1, -1L, (AbstractCache.StatsCounter) cachebuilder.getStatsCounterSupplier().get());
            }
        }

    }

    boolean evictsBySize() {
        return this.maxWeight >= 0L;
    }

    boolean customWeigher() {
        return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
    }

    boolean expires() {
        return this.expiresAfterWrite() || this.expiresAfterAccess();
    }

    boolean expiresAfterWrite() {
        return this.expireAfterWriteNanos > 0L;
    }

    boolean expiresAfterAccess() {
        return this.expireAfterAccessNanos > 0L;
    }

    boolean refreshes() {
        return this.refreshNanos > 0L;
    }

    boolean usesAccessQueue() {
        return this.expiresAfterAccess() || this.evictsBySize();
    }

    boolean usesWriteQueue() {
        return this.expiresAfterWrite();
    }

    boolean recordsWrite() {
        return this.expiresAfterWrite() || this.refreshes();
    }

    boolean recordsAccess() {
        return this.expiresAfterAccess();
    }

    boolean recordsTime() {
        return this.recordsWrite() || this.recordsAccess();
    }

    boolean usesWriteEntries() {
        return this.usesWriteQueue() || this.recordsWrite();
    }

    boolean usesAccessEntries() {
        return this.usesAccessQueue() || this.recordsAccess();
    }

    boolean usesKeyReferences() {
        return this.keyStrength != LocalCache.Strength.STRONG;
    }

    boolean usesValueReferences() {
        return this.valueStrength != LocalCache.Strength.STRONG;
    }

    static <K, V> LocalCache.ValueReference<K, V> unset() {
        return LocalCache.UNSET;
    }

    static <K, V> LocalCache.ReferenceEntry<K, V> nullEntry() {
        return LocalCache.NullEntry.INSTANCE;
    }

    static <E> Queue<E> discardingQueue() {
        return LocalCache.DISCARDING_QUEUE;
    }

    static int rehash(int i) {
        i += i << 15 ^ -12931;
        i ^= i >>> 10;
        i += i << 3;
        i ^= i >>> 6;
        i += (i << 2) + (i << 14);
        return i ^ i >>> 16;
    }

    @GuardedBy("Segment.this")
    @VisibleForTesting
    LocalCache.ReferenceEntry<K, V> newEntry(K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
        return this.segmentFor(i).newEntry(k0, i, localcache_referenceentry);
    }

    @GuardedBy("Segment.this")
    @VisibleForTesting
    LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
        int i = localcache_referenceentry.getHash();

        return this.segmentFor(i).copyEntry(localcache_referenceentry, localcache_referenceentry1);
    }

    @GuardedBy("Segment.this")
    @VisibleForTesting
    LocalCache.ValueReference<K, V> newValueReference(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, V v0, int i) {
        int j = localcache_referenceentry.getHash();

        return this.valueStrength.referenceValue(this.segmentFor(j), localcache_referenceentry, Preconditions.checkNotNull(v0), i);
    }

    int hash(@Nullable Object object) {
        int i = this.keyEquivalence.hash(object);

        return rehash(i);
    }

    void reclaimValue(LocalCache.ValueReference<K, V> localcache_valuereference) {
        LocalCache.ReferenceEntry localcache_referenceentry = localcache_valuereference.getEntry();
        int i = localcache_referenceentry.getHash();

        this.segmentFor(i).reclaimValue(localcache_referenceentry.getKey(), i, localcache_valuereference);
    }

    void reclaimKey(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
        int i = localcache_referenceentry.getHash();

        this.segmentFor(i).reclaimKey(localcache_referenceentry, i);
    }

    @VisibleForTesting
    boolean isLive(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, long i) {
        return this.segmentFor(localcache_referenceentry.getHash()).getLiveValue(localcache_referenceentry, i) != null;
    }

    LocalCache.Segment<K, V> segmentFor(int i) {
        return this.segments[i >>> this.segmentShift & this.segmentMask];
    }

    LocalCache.Segment<K, V> createSegment(int i, long j, AbstractCache.StatsCounter abstractcache_statscounter) {
        return new LocalCache.Segment<>(this, i, j, abstractcache_statscounter);
    }

    @Nullable
    V getLiveValue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, long i) {
        if (localcache_referenceentry.getKey() == null) {
            return null;
        } else {
            Object object = localcache_referenceentry.getValueReference().get();

            return object == null ? null : (this.isExpired(localcache_referenceentry, i) ? null : object);
        }
    }

    boolean isExpired(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, long i) {
        Preconditions.checkNotNull(localcache_referenceentry);
        return this.expiresAfterAccess() && i - localcache_referenceentry.getAccessTime() >= this.expireAfterAccessNanos ? true : this.expiresAfterWrite() && i - localcache_referenceentry.getWriteTime() >= this.expireAfterWriteNanos;
    }

    @GuardedBy("Segment.this")
    static <K, V> void connectAccessOrder(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
        localcache_referenceentry.setNextInAccessQueue(localcache_referenceentry1);
        localcache_referenceentry1.setPreviousInAccessQueue(localcache_referenceentry);
    }

    @GuardedBy("Segment.this")
    static <K, V> void nullifyAccessOrder(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
        LocalCache.ReferenceEntry localcache_referenceentry1 = nullEntry();

        localcache_referenceentry.setNextInAccessQueue(localcache_referenceentry1);
        localcache_referenceentry.setPreviousInAccessQueue(localcache_referenceentry1);
    }

    @GuardedBy("Segment.this")
    static <K, V> void connectWriteOrder(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
        localcache_referenceentry.setNextInWriteQueue(localcache_referenceentry1);
        localcache_referenceentry1.setPreviousInWriteQueue(localcache_referenceentry);
    }

    @GuardedBy("Segment.this")
    static <K, V> void nullifyWriteOrder(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
        LocalCache.ReferenceEntry localcache_referenceentry1 = nullEntry();

        localcache_referenceentry.setNextInWriteQueue(localcache_referenceentry1);
        localcache_referenceentry.setPreviousInWriteQueue(localcache_referenceentry1);
    }

    void processPendingNotifications() {
        RemovalNotification removalnotification;

        while ((removalnotification = (RemovalNotification) this.removalNotificationQueue.poll()) != null) {
            try {
                this.removalListener.onRemoval(removalnotification);
            } catch (Throwable throwable) {
                LocalCache.logger.log(Level.WARNING, "Exception thrown by removal listener", throwable);
            }
        }

    }

    final LocalCache.Segment<K, V>[] newSegmentArray(int i) {
        return new LocalCache.Segment[i];
    }

    public void cleanUp() {
        LocalCache.Segment[] alocalcache_segment = this.segments;
        int i = alocalcache_segment.length;

        for (int j = 0; j < i; ++j) {
            LocalCache.Segment localcache_segment = alocalcache_segment[j];

            localcache_segment.cleanUp();
        }

    }

    public boolean isEmpty() {
        long i = 0L;
        LocalCache.Segment[] alocalcache_segment = this.segments;

        int j;

        for (j = 0; j < alocalcache_segment.length; ++j) {
            if (alocalcache_segment[j].count != 0) {
                return false;
            }

            i += (long) alocalcache_segment[j].modCount;
        }

        if (i != 0L) {
            for (j = 0; j < alocalcache_segment.length; ++j) {
                if (alocalcache_segment[j].count != 0) {
                    return false;
                }

                i -= (long) alocalcache_segment[j].modCount;
            }

            if (i != 0L) {
                return false;
            }
        }

        return true;
    }

    long longSize() {
        LocalCache.Segment[] alocalcache_segment = this.segments;
        long i = 0L;

        for (int j = 0; j < alocalcache_segment.length; ++j) {
            i += (long) alocalcache_segment[j].count;
        }

        return i;
    }

    public int size() {
        return Ints.saturatedCast(this.longSize());
    }

    @Nullable
    public V get(@Nullable Object object) {
        if (object == null) {
            return null;
        } else {
            int i = this.hash(object);

            return this.segmentFor(i).get(object, i);
        }
    }

    @Nullable
    public V getIfPresent(Object object) {
        int i = this.hash(Preconditions.checkNotNull(object));
        Object object1 = this.segmentFor(i).get(object, i);

        if (object1 == null) {
            this.globalStatsCounter.recordMisses(1);
        } else {
            this.globalStatsCounter.recordHits(1);
        }

        return object1;
    }

    V get(K k0, CacheLoader<? super K, V> cacheloader) throws ExecutionException {
        int i = this.hash(Preconditions.checkNotNull(k0));

        return this.segmentFor(i).get(k0, i, cacheloader);
    }

    V getOrLoad(K k0) throws ExecutionException {
        return this.get(k0, this.defaultLoader);
    }

    ImmutableMap<K, V> getAllPresent(Iterable<?> iterable) {
        int i = 0;
        int j = 0;
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();
            Object object1 = this.get(object);

            if (object1 == null) {
                ++j;
            } else {
                linkedhashmap.put(object, object1);
                ++i;
            }
        }

        this.globalStatsCounter.recordHits(i);
        this.globalStatsCounter.recordMisses(j);
        return ImmutableMap.copyOf(linkedhashmap);
    }

    ImmutableMap<K, V> getAll(Iterable<? extends K> iterable) throws ExecutionException {
        int i = 0;
        int j = 0;
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        LinkedHashSet linkedhashset = Sets.newLinkedHashSet();
        Iterator iterator = iterable.iterator();

        Object object;

        while (iterator.hasNext()) {
            Object object1 = iterator.next();

            object = this.get(object1);
            if (!linkedhashmap.containsKey(object1)) {
                linkedhashmap.put(object1, object);
                if (object == null) {
                    ++j;
                    linkedhashset.add(object1);
                } else {
                    ++i;
                }
            }
        }

        ImmutableMap immutablemap;

        try {
            if (!linkedhashset.isEmpty()) {
                Iterator iterator1;

                try {
                    Map map = this.loadAll(linkedhashset, this.defaultLoader);

                    iterator1 = linkedhashset.iterator();

                    while (iterator1.hasNext()) {
                        object = iterator1.next();
                        Object object2 = map.get(object);

                        if (object2 == null) {
                            throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + object);
                        }

                        linkedhashmap.put(object, object2);
                    }
                } catch (CacheLoader.UnsupportedLoadingOperationException cacheloader_unsupportedloadingoperationexception) {
                    iterator1 = linkedhashset.iterator();

                    while (iterator1.hasNext()) {
                        object = iterator1.next();
                        --j;
                        linkedhashmap.put(object, this.get(object, this.defaultLoader));
                    }
                }
            }

            immutablemap = ImmutableMap.copyOf(linkedhashmap);
        } finally {
            this.globalStatsCounter.recordHits(i);
            this.globalStatsCounter.recordMisses(j);
        }

        return immutablemap;
    }

    @Nullable
    Map<K, V> loadAll(Set<? extends K> set, CacheLoader<? super K, V> cacheloader) throws ExecutionException {
        Preconditions.checkNotNull(cacheloader);
        Preconditions.checkNotNull(set);
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean flag = false;

        Map map;

        try {
            Map map1 = cacheloader.loadAll(set);

            map = map1;
            flag = true;
        } catch (CacheLoader.UnsupportedLoadingOperationException cacheloader_unsupportedloadingoperationexception) {
            flag = true;
            throw cacheloader_unsupportedloadingoperationexception;
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
            throw new ExecutionException(interruptedexception);
        } catch (RuntimeException runtimeexception) {
            throw new UncheckedExecutionException(runtimeexception);
        } catch (Exception exception) {
            throw new ExecutionException(exception);
        } catch (Error error) {
            throw new ExecutionError(error);
        } finally {
            if (!flag) {
                this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
            }

        }

        if (map == null) {
            this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
            throw new CacheLoader.InvalidCacheLoadException(cacheloader + " returned null map from loadAll");
        } else {
            stopwatch.stop();
            boolean flag1 = false;
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                Object object = entry.getKey();
                Object object1 = entry.getValue();

                if (object != null && object1 != null) {
                    this.put(object, object1);
                } else {
                    flag1 = true;
                }
            }

            if (flag1) {
                this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
                throw new CacheLoader.InvalidCacheLoadException(cacheloader + " returned null keys or values from loadAll");
            } else {
                this.globalStatsCounter.recordLoadSuccess(stopwatch.elapsed(TimeUnit.NANOSECONDS));
                return map;
            }
        }
    }

    LocalCache.ReferenceEntry<K, V> getEntry(@Nullable Object object) {
        if (object == null) {
            return null;
        } else {
            int i = this.hash(object);

            return this.segmentFor(i).getEntry(object, i);
        }
    }

    void refresh(K k0) {
        int i = this.hash(Preconditions.checkNotNull(k0));

        this.segmentFor(i).refresh(k0, i, this.defaultLoader, false);
    }

    public boolean containsKey(@Nullable Object object) {
        if (object == null) {
            return false;
        } else {
            int i = this.hash(object);

            return this.segmentFor(i).containsKey(object, i);
        }
    }

    public boolean containsValue(@Nullable Object object) {
        if (object == null) {
            return false;
        } else {
            long i = this.ticker.read();
            LocalCache.Segment[] alocalcache_segment = this.segments;
            long j = -1L;

            for (int k = 0; k < 3; ++k) {
                long l = 0L;
                LocalCache.Segment[] alocalcache_segment1 = alocalcache_segment;
                int i1 = alocalcache_segment.length;

                for (int j1 = 0; j1 < i1; ++j1) {
                    LocalCache.Segment localcache_segment = alocalcache_segment1[j1];
                    int k1 = localcache_segment.count;
                    AtomicReferenceArray atomicreferencearray = localcache_segment.table;

                    for (int l1 = 0; l1 < atomicreferencearray.length(); ++l1) {
                        for (LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(l1); localcache_referenceentry != null; localcache_referenceentry = localcache_referenceentry.getNext()) {
                            Object object1 = localcache_segment.getLiveValue(localcache_referenceentry, i);

                            if (object1 != null && this.valueEquivalence.equivalent(object, object1)) {
                                return true;
                            }
                        }
                    }

                    l += (long) localcache_segment.modCount;
                }

                if (l == j) {
                    break;
                }

                j = l;
            }

            return false;
        }
    }

    public V put(K k0, V v0) {
        Preconditions.checkNotNull(k0);
        Preconditions.checkNotNull(v0);
        int i = this.hash(k0);

        return this.segmentFor(i).put(k0, i, v0, false);
    }

    public V putIfAbsent(K k0, V v0) {
        Preconditions.checkNotNull(k0);
        Preconditions.checkNotNull(v0);
        int i = this.hash(k0);

        return this.segmentFor(i).put(k0, i, v0, true);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), entry.getValue());
        }

    }

    public V remove(@Nullable Object object) {
        if (object == null) {
            return null;
        } else {
            int i = this.hash(object);

            return this.segmentFor(i).remove(object, i);
        }
    }

    public boolean remove(@Nullable Object object, @Nullable Object object1) {
        if (object != null && object1 != null) {
            int i = this.hash(object);

            return this.segmentFor(i).remove(object, i, object1);
        } else {
            return false;
        }
    }

    public boolean replace(K k0, @Nullable V v0, V v1) {
        Preconditions.checkNotNull(k0);
        Preconditions.checkNotNull(v1);
        if (v0 == null) {
            return false;
        } else {
            int i = this.hash(k0);

            return this.segmentFor(i).replace(k0, i, v0, v1);
        }
    }

    public V replace(K k0, V v0) {
        Preconditions.checkNotNull(k0);
        Preconditions.checkNotNull(v0);
        int i = this.hash(k0);

        return this.segmentFor(i).replace(k0, i, v0);
    }

    public void clear() {
        LocalCache.Segment[] alocalcache_segment = this.segments;
        int i = alocalcache_segment.length;

        for (int j = 0; j < i; ++j) {
            LocalCache.Segment localcache_segment = alocalcache_segment[j];

            localcache_segment.clear();
        }

    }

    void invalidateAll(Iterable<?> iterable) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            this.remove(object);
        }

    }

    public Set<K> keySet() {
        Set set = this.keySet;

        return set != null ? set : (this.keySet = new LocalCache.KeySet(this));
    }

    public Collection<V> values() {
        Collection collection = this.values;

        return collection != null ? collection : (this.values = new LocalCache.Values(this));
    }

    @GwtIncompatible("Not supported.")
    public Set<Entry<K, V>> entrySet() {
        Set set = this.entrySet;

        return set != null ? set : (this.entrySet = new LocalCache.EntrySet(this));
    }

    static class LocalLoadingCache<K, V> extends LocalCache.LocalManualCache<K, V> implements LoadingCache<K, V> {

        private static final long serialVersionUID = 1L;

        LocalLoadingCache(CacheBuilder<? super K, ? super V> cachebuilder, CacheLoader<? super K, V> cacheloader) {
            super(new LocalCache<>(cachebuilder, (CacheLoader) Preconditions.checkNotNull(cacheloader)), null);
        }

        @Override
        public V get(K k0) throws ExecutionException {
            return this.localCache.getOrLoad(k0);
        }

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
            return this.localCache.getAll(iterable);
        }

        @Override
        public void refresh(K k0) {
            this.localCache.refresh(k0);
        }

        @Override
        public final V apply(K k0) {
            return this.getUnchecked(k0);
        }

        @Override
        Object writeReplace() {
            return new LocalCache.LoadingSerializationProxy<>(this.localCache);
        }
    }

    static class LocalManualCache<K, V> implements Cache<K, V>, Serializable {

        final LocalCache<K, V> localCache;
        private static final long serialVersionUID = 1L;

        LocalManualCache(CacheBuilder<? super K, ? super V> cachebuilder) {
            this(new LocalCache<>(cachebuilder, (CacheLoader) null));
        }

        private LocalManualCache(LocalCache<K, V> localcache) {
            this.localCache = localcache;
        }

        @Nullable
        @Override
        public V getIfPresent(Object object) {
            return this.localCache.getIfPresent(object);
        }

        @Override
        public V get(K k0, final Callable<? extends V> callable) throws ExecutionException {
            Preconditions.checkNotNull(callable);
            return this.localCache.get(k0, new CacheLoader<Object, V>() {
                @Override
                public V load(Object object) throws Exception {
                    return callable.call();
                }
            });
        }

        @Override
        public ImmutableMap<K, V> getAllPresent(Iterable<?> iterable) {
            return this.localCache.getAllPresent(iterable);
        }

        @Override
        public void put(K k0, V v0) {
            this.localCache.put(k0, v0);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            this.localCache.putAll(map);
        }

        @Override
        public void invalidate(Object object) {
            Preconditions.checkNotNull(object);
            this.localCache.remove(object);
        }

        @Override
        public void invalidateAll(Iterable<?> iterable) {
            this.localCache.invalidateAll(iterable);
        }

        @Override
        public void invalidateAll() {
            this.localCache.clear();
        }

        @Override
        public long size() {
            return this.localCache.longSize();
        }

        @Override
        public ConcurrentMap<K, V> asMap() {
            return this.localCache;
        }

        @Override
        public CacheStats stats() {
            AbstractCache.SimpleStatsCounter abstractcache_simplestatscounter = new AbstractCache.SimpleStatsCounter();

            abstractcache_simplestatscounter.incrementBy(this.localCache.globalStatsCounter);
            LocalCache.Segment[] alocalcache_segment = this.localCache.segments;
            int i = alocalcache_segment.length;

            for (int j = 0; j < i; ++j) {
                LocalCache.Segment localcache_segment = alocalcache_segment[j];

                abstractcache_simplestatscounter.incrementBy(localcache_segment.statsCounter);
            }

            return abstractcache_simplestatscounter.snapshot();
        }

        @Override
        public void cleanUp() {
            this.localCache.cleanUp();
        }

        Object writeReplace() {
            return new LocalCache.ManualSerializationProxy<>(this.localCache);
        }
    }

    static final class LoadingSerializationProxy<K, V> extends LocalCache.ManualSerializationProxy<K, V> implements LoadingCache<K, V>, Serializable {

        private static final long serialVersionUID = 1L;
        transient LoadingCache<K, V> autoDelegate;

        LoadingSerializationProxy(LocalCache<K, V> localcache) {
            super(localcache);
        }

        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            CacheBuilder cachebuilder = this.recreateCacheBuilder();

            this.autoDelegate = cachebuilder.build(this.loader);
        }

        @Override
        public V get(K k0) throws ExecutionException {
            return this.autoDelegate.get(k0);
        }

        @Override
        public V getUnchecked(K k0) {
            return this.autoDelegate.getUnchecked(k0);
        }

        @Override
        public ImmutableMap<K, V> getAll(Iterable<? extends K> iterable) throws ExecutionException {
            return this.autoDelegate.getAll(iterable);
        }

        @Override
        public final V apply(K k0) {
            return this.autoDelegate.apply(k0);
        }

        @Override
        public void refresh(K k0) {
            this.autoDelegate.refresh(k0);
        }

        private Object readResolve() {
            return this.autoDelegate;
        }
    }

    static class ManualSerializationProxy<K, V> extends ForwardingCache<K, V> implements Serializable {

        private static final long serialVersionUID = 1L;
        final LocalCache.Strength keyStrength;
        final LocalCache.Strength valueStrength;
        final Equivalence<Object> keyEquivalence;
        final Equivalence<Object> valueEquivalence;
        final long expireAfterWriteNanos;
        final long expireAfterAccessNanos;
        final long maxWeight;
        final Weigher<K, V> weigher;
        final int concurrencyLevel;
        final RemovalListener<? super K, ? super V> removalListener;
        final Ticker ticker;
        final CacheLoader<? super K, V> loader;
        transient Cache<K, V> delegate;

        ManualSerializationProxy(LocalCache<K, V> localcache) {
            this(localcache.keyStrength, localcache.valueStrength, localcache.keyEquivalence, localcache.valueEquivalence, localcache.expireAfterWriteNanos, localcache.expireAfterAccessNanos, localcache.maxWeight, localcache.weigher, localcache.concurrencyLevel, localcache.removalListener, localcache.ticker, localcache.defaultLoader);
        }

        private ManualSerializationProxy(LocalCache.Strength localcache_strength, LocalCache.Strength localcache_strength1, Equivalence<Object> equivalence, Equivalence<Object> equivalence1, long i, long j, long k, Weigher<K, V> weigher, int l, RemovalListener<? super K, ? super V> removallistener, Ticker ticker, CacheLoader<? super K, V> cacheloader) {
            this.keyStrength = localcache_strength;
            this.valueStrength = localcache_strength1;
            this.keyEquivalence = equivalence;
            this.valueEquivalence = equivalence1;
            this.expireAfterWriteNanos = i;
            this.expireAfterAccessNanos = j;
            this.maxWeight = k;
            this.weigher = weigher;
            this.concurrencyLevel = l;
            this.removalListener = removallistener;
            this.ticker = ticker != Ticker.systemTicker() && ticker != CacheBuilder.NULL_TICKER ? ticker : null;
            this.loader = cacheloader;
        }

        CacheBuilder<K, V> recreateCacheBuilder() {
            CacheBuilder cachebuilder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);

            cachebuilder.strictParsing = false;
            if (this.expireAfterWriteNanos > 0L) {
                cachebuilder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
            }

            if (this.expireAfterAccessNanos > 0L) {
                cachebuilder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
            }

            if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
                cachebuilder.weigher(this.weigher);
                if (this.maxWeight != -1L) {
                    cachebuilder.maximumWeight(this.maxWeight);
                }
            } else if (this.maxWeight != -1L) {
                cachebuilder.maximumSize(this.maxWeight);
            }

            if (this.ticker != null) {
                cachebuilder.ticker(this.ticker);
            }

            return cachebuilder;
        }

        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            CacheBuilder cachebuilder = this.recreateCacheBuilder();

            this.delegate = cachebuilder.build();
        }

        private Object readResolve() {
            return this.delegate;
        }

        @Override
        protected Cache<K, V> delegate() {
            return this.delegate;
        }
    }

    final class EntrySet extends LocalCache<K, V>.AbstractCacheSet<Entry<K, V>> {

        EntrySet(ConcurrentMap<?, ?> concurrentmap) {
            super(concurrentmap);
        }

        public Iterator<Entry<K, V>> iterator() {
            return LocalCache.this.new EntryIterator();
        }

        public boolean contains(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                Entry entry = (Entry) object;
                Object object1 = entry.getKey();

                if (object1 == null) {
                    return false;
                } else {
                    Object object2 = LocalCache.this.get(object1);

                    return object2 != null && LocalCache.this.valueEquivalence.equivalent(entry.getValue(), object2);
                }
            }
        }

        public boolean remove(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                Entry entry = (Entry) object;
                Object object1 = entry.getKey();

                return object1 != null && LocalCache.this.remove(object1, entry.getValue());
            }
        }
    }

    final class Values extends AbstractCollection<V> {

        private final ConcurrentMap<?, ?> map;

        Values(ConcurrentMap<?, ?> concurrentmap) {
            this.map = concurrentmap;
        }

        public int size() {
            return this.map.size();
        }

        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        public void clear() {
            this.map.clear();
        }

        public Iterator<V> iterator() {
            return LocalCache.this.new ValueIterator();
        }

        public boolean contains(Object object) {
            return this.map.containsValue(object);
        }
    }

    final class KeySet extends LocalCache<K, V>.AbstractCacheSet<K> {

        KeySet(ConcurrentMap<?, ?> concurrentmap) {
            super(concurrentmap);
        }

        public Iterator<K> iterator() {
            return LocalCache.this.new KeyIterator();
        }

        public boolean contains(Object object) {
            return this.map.containsKey(object);
        }

        public boolean remove(Object object) {
            return this.map.remove(object) != null;
        }
    }

    abstract class AbstractCacheSet<T> extends AbstractSet<T> {

        final ConcurrentMap<?, ?> map;

        AbstractCacheSet(ConcurrentMap<?, ?> concurrentmap) {
            this.map = concurrentmap;
        }

        public int size() {
            return this.map.size();
        }

        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        public void clear() {
            this.map.clear();
        }
    }

    final class EntryIterator extends LocalCache<K, V>.HashIterator<Entry<K, V>> {

        EntryIterator() {
            super();
        }

        @Override
        public Entry<K, V> next() {
            return this.nextEntry();
        }
    }

    final class WriteThroughEntry implements Entry<K, V> {

        final K key;
        V value;

        WriteThroughEntry(K k0, V v0) {
            this.key = k0;
            this.value = v0;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                Entry entry = (Entry) object;

                return this.key.equals(entry.getKey()) && this.value.equals(entry.getValue());
            }
        }

        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }

        public V setValue(V v0) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }

    final class ValueIterator extends LocalCache<K, V>.HashIterator<V> {

        ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return this.nextEntry().getValue();
        }
    }

    final class KeyIterator extends LocalCache<K, V>.HashIterator<K> {

        KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }

    abstract class HashIterator<T> implements Iterator<T> {

        int nextSegmentIndex;
        int nextTableIndex;
        LocalCache.Segment<K, V> currentSegment;
        AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> currentTable;
        LocalCache.ReferenceEntry<K, V> nextEntry;
        LocalCache<K, V>.WriteThroughEntry nextExternal;
        LocalCache<K, V>.WriteThroughEntry lastReturned;

        HashIterator() {
            this.nextSegmentIndex = LocalCache.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }

        public abstract T next();

        final void advance() {
            this.nextExternal = null;
            if (!this.nextInChain()) {
                if (!this.nextInTable()) {
                    while (this.nextSegmentIndex >= 0) {
                        this.currentSegment = LocalCache.this.segments[this.nextSegmentIndex--];
                        if (this.currentSegment.count != 0) {
                            this.currentTable = this.currentSegment.table;
                            this.nextTableIndex = this.currentTable.length() - 1;
                            if (this.nextInTable()) {
                                return;
                            }
                        }
                    }

                }
            }
        }

        boolean nextInChain() {
            if (this.nextEntry != null) {
                for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
                    if (this.advanceTo(this.nextEntry)) {
                        return true;
                    }
                }
            }

            return false;
        }

        boolean nextInTable() {
            while (true) {
                if (this.nextTableIndex >= 0) {
                    if ((this.nextEntry = (LocalCache.ReferenceEntry) this.currentTable.get(this.nextTableIndex--)) == null || !this.advanceTo(this.nextEntry) && !this.nextInChain()) {
                        continue;
                    }

                    return true;
                }

                return false;
            }
        }

        boolean advanceTo(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            boolean flag;

            try {
                long i = LocalCache.this.ticker.read();
                Object object = localcache_referenceentry.getKey();
                Object object1 = LocalCache.this.getLiveValue(localcache_referenceentry, i);

                if (object1 == null) {
                    flag = false;
                    return flag;
                }

                this.nextExternal = LocalCache.this.new WriteThroughEntry(object, object1);
                flag = true;
            } finally {
                this.currentSegment.postReadCleanup();
            }

            return flag;
        }

        public boolean hasNext() {
            return this.nextExternal != null;
        }

        LocalCache<K, V>.WriteThroughEntry nextEntry() {
            if (this.nextExternal == null) {
                throw new NoSuchElementException();
            } else {
                this.lastReturned = this.nextExternal;
                this.advance();
                return this.lastReturned;
            }
        }

        public void remove() {
            Preconditions.checkState(this.lastReturned != null);
            LocalCache.this.remove(this.lastReturned.getKey());
            this.lastReturned = null;
        }
    }

    static final class AccessQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>> {

        final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>() {
            LocalCache.ReferenceEntry<K, V> nextAccess = this;
            LocalCache.ReferenceEntry<K, V> previousAccess = this;

            @Override
            public long getAccessTime() {
                return Long.MAX_VALUE;
            }

            @Override
            public void setAccessTime(long i) {}

            @Override
            public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
                return this.nextAccess;
            }

            @Override
            public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                this.nextAccess = localcache_referenceentry;
            }

            @Override
            public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
                return this.previousAccess;
            }

            @Override
            public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                this.previousAccess = localcache_referenceentry;
            }
        };

        AccessQueue() {}

        public boolean offer(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            LocalCache.connectAccessOrder(localcache_referenceentry.getPreviousInAccessQueue(), localcache_referenceentry.getNextInAccessQueue());
            LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), localcache_referenceentry);
            LocalCache.connectAccessOrder(localcache_referenceentry, this.head);
            return true;
        }

        public LocalCache.ReferenceEntry<K, V> peek() {
            LocalCache.ReferenceEntry localcache_referenceentry = this.head.getNextInAccessQueue();

            return localcache_referenceentry == this.head ? null : localcache_referenceentry;
        }

        public LocalCache.ReferenceEntry<K, V> poll() {
            LocalCache.ReferenceEntry localcache_referenceentry = this.head.getNextInAccessQueue();

            if (localcache_referenceentry == this.head) {
                return null;
            } else {
                this.remove(localcache_referenceentry);
                return localcache_referenceentry;
            }
        }

        public boolean remove(Object object) {
            LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) object;
            LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry.getPreviousInAccessQueue();
            LocalCache.ReferenceEntry localcache_referenceentry2 = localcache_referenceentry.getNextInAccessQueue();

            LocalCache.connectAccessOrder(localcache_referenceentry1, localcache_referenceentry2);
            LocalCache.nullifyAccessOrder(localcache_referenceentry);
            return localcache_referenceentry2 != LocalCache.NullEntry.INSTANCE;
        }

        public boolean contains(Object object) {
            LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) object;

            return localcache_referenceentry.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE;
        }

        public boolean isEmpty() {
            return this.head.getNextInAccessQueue() == this.head;
        }

        public int size() {
            int i = 0;

            for (LocalCache.ReferenceEntry localcache_referenceentry = this.head.getNextInAccessQueue(); localcache_referenceentry != this.head; localcache_referenceentry = localcache_referenceentry.getNextInAccessQueue()) {
                ++i;
            }

            return i;
        }

        public void clear() {
            LocalCache.ReferenceEntry localcache_referenceentry;

            for (LocalCache.ReferenceEntry localcache_referenceentry1 = this.head.getNextInAccessQueue(); localcache_referenceentry1 != this.head; localcache_referenceentry1 = localcache_referenceentry) {
                localcache_referenceentry = localcache_referenceentry1.getNextInAccessQueue();
                LocalCache.nullifyAccessOrder(localcache_referenceentry1);
            }

            this.head.setNextInAccessQueue(this.head);
            this.head.setPreviousInAccessQueue(this.head);
        }

        public Iterator<LocalCache.ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<LocalCache.ReferenceEntry<K, V>>(this.peek()) {
                protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                    LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry.getNextInAccessQueue();

                    return localcache_referenceentry1 == AccessQueue.this.head ? null : localcache_referenceentry1;
                }
            };
        }
    }

    static final class WriteQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>> {

        final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>() {
            LocalCache.ReferenceEntry<K, V> nextWrite = this;
            LocalCache.ReferenceEntry<K, V> previousWrite = this;

            @Override
            public long getWriteTime() {
                return Long.MAX_VALUE;
            }

            @Override
            public void setWriteTime(long i) {}

            @Override
            public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
                return this.nextWrite;
            }

            @Override
            public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                this.nextWrite = localcache_referenceentry;
            }

            @Override
            public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
                return this.previousWrite;
            }

            @Override
            public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                this.previousWrite = localcache_referenceentry;
            }
        };

        WriteQueue() {}

        public boolean offer(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            LocalCache.connectWriteOrder(localcache_referenceentry.getPreviousInWriteQueue(), localcache_referenceentry.getNextInWriteQueue());
            LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), localcache_referenceentry);
            LocalCache.connectWriteOrder(localcache_referenceentry, this.head);
            return true;
        }

        public LocalCache.ReferenceEntry<K, V> peek() {
            LocalCache.ReferenceEntry localcache_referenceentry = this.head.getNextInWriteQueue();

            return localcache_referenceentry == this.head ? null : localcache_referenceentry;
        }

        public LocalCache.ReferenceEntry<K, V> poll() {
            LocalCache.ReferenceEntry localcache_referenceentry = this.head.getNextInWriteQueue();

            if (localcache_referenceentry == this.head) {
                return null;
            } else {
                this.remove(localcache_referenceentry);
                return localcache_referenceentry;
            }
        }

        public boolean remove(Object object) {
            LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) object;
            LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry.getPreviousInWriteQueue();
            LocalCache.ReferenceEntry localcache_referenceentry2 = localcache_referenceentry.getNextInWriteQueue();

            LocalCache.connectWriteOrder(localcache_referenceentry1, localcache_referenceentry2);
            LocalCache.nullifyWriteOrder(localcache_referenceentry);
            return localcache_referenceentry2 != LocalCache.NullEntry.INSTANCE;
        }

        public boolean contains(Object object) {
            LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) object;

            return localcache_referenceentry.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE;
        }

        public boolean isEmpty() {
            return this.head.getNextInWriteQueue() == this.head;
        }

        public int size() {
            int i = 0;

            for (LocalCache.ReferenceEntry localcache_referenceentry = this.head.getNextInWriteQueue(); localcache_referenceentry != this.head; localcache_referenceentry = localcache_referenceentry.getNextInWriteQueue()) {
                ++i;
            }

            return i;
        }

        public void clear() {
            LocalCache.ReferenceEntry localcache_referenceentry;

            for (LocalCache.ReferenceEntry localcache_referenceentry1 = this.head.getNextInWriteQueue(); localcache_referenceentry1 != this.head; localcache_referenceentry1 = localcache_referenceentry) {
                localcache_referenceentry = localcache_referenceentry1.getNextInWriteQueue();
                LocalCache.nullifyWriteOrder(localcache_referenceentry1);
            }

            this.head.setNextInWriteQueue(this.head);
            this.head.setPreviousInWriteQueue(this.head);
        }

        public Iterator<LocalCache.ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<LocalCache.ReferenceEntry<K, V>>(this.peek()) {
                protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                    LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry.getNextInWriteQueue();

                    return localcache_referenceentry1 == WriteQueue.this.head ? null : localcache_referenceentry1;
                }
            };
        }
    }

    static class LoadingValueReference<K, V> implements LocalCache.ValueReference<K, V> {

        volatile LocalCache.ValueReference<K, V> oldValue;
        final SettableFuture<V> futureValue;
        final Stopwatch stopwatch;

        public LoadingValueReference() {
            this(LocalCache.unset());
        }

        public LoadingValueReference(LocalCache.ValueReference<K, V> localcache_valuereference) {
            this.futureValue = SettableFuture.create();
            this.stopwatch = Stopwatch.createUnstarted();
            this.oldValue = localcache_valuereference;
        }

        @Override
        public boolean isLoading() {
            return true;
        }

        @Override
        public boolean isActive() {
            return this.oldValue.isActive();
        }

        @Override
        public int getWeight() {
            return this.oldValue.getWeight();
        }

        public boolean set(@Nullable V v0) {
            return this.futureValue.set(v0);
        }

        public boolean setException(Throwable throwable) {
            return this.futureValue.setException(throwable);
        }

        private ListenableFuture<V> fullyFailedFuture(Throwable throwable) {
            return Futures.immediateFailedFuture(throwable);
        }

        @Override
        public void notifyNewValue(@Nullable V v0) {
            if (v0 != null) {
                this.set(v0);
            } else {
                this.oldValue = LocalCache.unset();
            }

        }

        public ListenableFuture<V> loadFuture(K k0, CacheLoader<? super K, V> cacheloader) {
            this.stopwatch.start();
            Object object = this.oldValue.get();

            try {
                if (object == null) {
                    Object object1 = cacheloader.load(k0);

                    return (ListenableFuture) (this.set(object1) ? this.futureValue : Futures.immediateFuture(object1));
                } else {
                    ListenableFuture listenablefuture = cacheloader.reload(k0, object);

                    return listenablefuture == null ? Futures.immediateFuture((Object) null) : Futures.transform(listenablefuture, new Function<V, V>() {
                        @Override
                        public V apply(V v0) {
                            LoadingValueReference.this.set(v0);
                            return v0;
                        }
                    });
                }
            } catch (Throwable throwable) {
                if (throwable instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }

                return (ListenableFuture) (this.setException(throwable) ? this.futureValue : this.fullyFailedFuture(throwable));
            }
        }

        public long elapsedNanos() {
            return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
        }

        @Override
        public V waitForValue() throws ExecutionException {
            return Uninterruptibles.getUninterruptibly(this.futureValue);
        }

        @Override
        public V get() {
            return this.oldValue.get();
        }

        public LocalCache.ValueReference<K, V> getOldValue() {
            return this.oldValue;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        @Override
        public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, @Nullable V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            return this;
        }
    }

    static class Segment<K, V> extends ReentrantLock {

        final LocalCache<K, V> map;
        volatile int count;
        @GuardedBy("Segment.this")
        int totalWeight;
        int modCount;
        int threshold;
        volatile AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table;
        final long maxSegmentWeight;
        final ReferenceQueue<K> keyReferenceQueue;
        final ReferenceQueue<V> valueReferenceQueue;
        final Queue<LocalCache.ReferenceEntry<K, V>> recencyQueue;
        final AtomicInteger readCount = new AtomicInteger();
        @GuardedBy("Segment.this")
        final Queue<LocalCache.ReferenceEntry<K, V>> writeQueue;
        @GuardedBy("Segment.this")
        final Queue<LocalCache.ReferenceEntry<K, V>> accessQueue;
        final AbstractCache.StatsCounter statsCounter;

        Segment(LocalCache<K, V> localcache, int i, long j, AbstractCache.StatsCounter abstractcache_statscounter) {
            this.map = localcache;
            this.maxSegmentWeight = j;
            this.statsCounter = (AbstractCache.StatsCounter) Preconditions.checkNotNull(abstractcache_statscounter);
            this.initTable(this.newEntryArray(i));
            this.keyReferenceQueue = localcache.usesKeyReferences() ? new ReferenceQueue() : null;
            this.valueReferenceQueue = localcache.usesValueReferences() ? new ReferenceQueue() : null;
            this.recencyQueue = (Queue) (localcache.usesAccessQueue() ? new ConcurrentLinkedQueue() : LocalCache.discardingQueue());
            this.writeQueue = (Queue) (localcache.usesWriteQueue() ? new LocalCache.WriteQueue<>() : LocalCache.discardingQueue());
            this.accessQueue = (Queue) (localcache.usesAccessQueue() ? new LocalCache.AccessQueue<>() : LocalCache.discardingQueue());
        }

        AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> newEntryArray(int i) {
            return new AtomicReferenceArray(i);
        }

        void initTable(AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> atomicreferencearray) {
            this.threshold = atomicreferencearray.length() * 3 / 4;
            if (!this.map.customWeigher() && (long) this.threshold == this.maxSegmentWeight) {
                ++this.threshold;
            }

            this.table = atomicreferencearray;
        }

        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> newEntry(K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull(k0), i, localcache_referenceentry);
        }

        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
            if (localcache_referenceentry.getKey() == null) {
                return null;
            } else {
                LocalCache.ValueReference localcache_valuereference = localcache_referenceentry.getValueReference();
                Object object = localcache_valuereference.get();

                if (object == null && localcache_valuereference.isActive()) {
                    return null;
                } else {
                    LocalCache.ReferenceEntry localcache_referenceentry2 = this.map.entryFactory.copyEntry(this, localcache_referenceentry, localcache_referenceentry1);

                    localcache_referenceentry2.setValueReference(localcache_valuereference.copyFor(this.valueReferenceQueue, object, localcache_referenceentry2));
                    return localcache_referenceentry2;
                }
            }
        }

        @GuardedBy("Segment.this")
        void setValue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, K k0, V v0, long i) {
            LocalCache.ValueReference localcache_valuereference = localcache_referenceentry.getValueReference();
            int j = this.map.weigher.weigh(k0, v0);

            Preconditions.checkState(j >= 0, "Weights must be non-negative");
            LocalCache.ValueReference localcache_valuereference1 = this.map.valueStrength.referenceValue(this, localcache_referenceentry, v0, j);

            localcache_referenceentry.setValueReference(localcache_valuereference1);
            this.recordWrite(localcache_referenceentry, j, i);
            localcache_valuereference.notifyNewValue(v0);
        }

        V get(K k0, int i, CacheLoader<? super K, V> cacheloader) throws ExecutionException {
            Preconditions.checkNotNull(k0);
            Preconditions.checkNotNull(cacheloader);

            try {
                if (this.count != 0) {
                    LocalCache.ReferenceEntry localcache_referenceentry = this.getEntry(k0, i);

                    if (localcache_referenceentry != null) {
                        long j = this.map.ticker.read();
                        Object object = this.getLiveValue(localcache_referenceentry, j);

                        if (object != null) {
                            this.recordRead(localcache_referenceentry, j);
                            this.statsCounter.recordHits(1);
                            Object object1 = this.scheduleRefresh(localcache_referenceentry, k0, i, object, j, cacheloader);

                            return object1;
                        }

                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry.getValueReference();

                        if (localcache_valuereference.isLoading()) {
                            Object object2 = this.waitForLoadingValue(localcache_referenceentry, k0, localcache_valuereference);

                            return object2;
                        }
                    }
                }

                Object object3 = this.lockedGetOrLoad(k0, i, cacheloader);

                return object3;
            } catch (ExecutionException executionexception) {
                Throwable throwable = executionexception.getCause();

                if (throwable instanceof Error) {
                    throw new ExecutionError((Error) throwable);
                } else if (throwable instanceof RuntimeException) {
                    throw new UncheckedExecutionException(throwable);
                } else {
                    throw executionexception;
                }
            } finally {
                this.postReadCleanup();
            }
        }

        V lockedGetOrLoad(K k0, int i, CacheLoader<? super K, V> cacheloader) throws ExecutionException {
            LocalCache.ValueReference localcache_valuereference = null;
            LocalCache.LoadingValueReference localcache_loadingvaluereference = null;
            boolean flag = true;

            this.lock();

            LocalCache.ReferenceEntry localcache_referenceentry;

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                int k = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int l = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry1 = (LocalCache.ReferenceEntry) atomicreferencearray.get(l);

                localcache_referenceentry = localcache_referenceentry1;

                while (true) {
                    if (localcache_referenceentry != null) {
                        Object object = localcache_referenceentry.getKey();

                        if (localcache_referenceentry.getHash() != i || object == null || !this.map.keyEquivalence.equivalent(k0, object)) {
                            localcache_referenceentry = localcache_referenceentry.getNext();
                            continue;
                        }

                        localcache_valuereference = localcache_referenceentry.getValueReference();
                        if (localcache_valuereference.isLoading()) {
                            flag = false;
                        } else {
                            Object object1 = localcache_valuereference.get();

                            if (object1 == null) {
                                this.enqueueNotification(object, i, localcache_valuereference, RemovalCause.COLLECTED);
                            } else {
                                if (!this.map.isExpired(localcache_referenceentry, j)) {
                                    this.recordLockedRead(localcache_referenceentry, j);
                                    this.statsCounter.recordHits(1);
                                    Object object2 = object1;

                                    return object2;
                                }

                                this.enqueueNotification(object, i, localcache_valuereference, RemovalCause.EXPIRED);
                            }

                            this.writeQueue.remove(localcache_referenceentry);
                            this.accessQueue.remove(localcache_referenceentry);
                            this.count = k;
                        }
                    }

                    if (flag) {
                        localcache_loadingvaluereference = new LocalCache.LoadingValueReference<>();
                        if (localcache_referenceentry == null) {
                            localcache_referenceentry = this.newEntry(k0, i, localcache_referenceentry1);
                            localcache_referenceentry.setValueReference(localcache_loadingvaluereference);
                            atomicreferencearray.set(l, localcache_referenceentry);
                        } else {
                            localcache_referenceentry.setValueReference(localcache_loadingvaluereference);
                        }
                    }
                    break;
                }
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }

            if (flag) {
                Object object3;

                try {
                    synchronized (localcache_referenceentry) {
                        object3 = this.loadSync(k0, i, localcache_loadingvaluereference, cacheloader);
                    }
                } finally {
                    this.statsCounter.recordMisses(1);
                }

                return object3;
            } else {
                return this.waitForLoadingValue(localcache_referenceentry, k0, localcache_valuereference);
            }
        }

        V waitForLoadingValue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, K k0, LocalCache.ValueReference<K, V> localcache_valuereference) throws ExecutionException {
            if (!localcache_valuereference.isLoading()) {
                throw new AssertionError();
            } else {
                Preconditions.checkState(!Thread.holdsLock(localcache_referenceentry), "Recursive load of: %s", k0);

                Object object;

                try {
                    Object object1 = localcache_valuereference.waitForValue();

                    if (object1 == null) {
                        throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + k0 + ".");
                    }

                    long i = this.map.ticker.read();

                    this.recordRead(localcache_referenceentry, i);
                    object = object1;
                } finally {
                    this.statsCounter.recordMisses(1);
                }

                return object;
            }
        }

        V loadSync(K k0, int i, LocalCache.LoadingValueReference<K, V> localcache_loadingvaluereference, CacheLoader<? super K, V> cacheloader) throws ExecutionException {
            ListenableFuture listenablefuture = localcache_loadingvaluereference.loadFuture(k0, cacheloader);

            return this.getAndRecordStats(k0, i, localcache_loadingvaluereference, listenablefuture);
        }

        ListenableFuture<V> loadAsync(final K k0, final int i, final LocalCache.LoadingValueReference<K, V> localcache_loadingvaluereference, CacheLoader<? super K, V> cacheloader) {
            final ListenableFuture listenablefuture = localcache_loadingvaluereference.loadFuture(k0, cacheloader);

            listenablefuture.addListener(new Runnable() {
                public void run() {
                    try {
                        Object object = Segment.this.getAndRecordStats(k0, i, localcache_loadingvaluereference, listenablefuture);
                    } catch (Throwable throwable) {
                        LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", throwable);
                        localcache_loadingvaluereference.setException(throwable);
                    }

                }
            }, LocalCache.sameThreadExecutor);
            return listenablefuture;
        }

        V getAndRecordStats(K k0, int i, LocalCache.LoadingValueReference<K, V> localcache_loadingvaluereference, ListenableFuture<V> listenablefuture) throws ExecutionException {
            Object object = null;

            Object object1;

            try {
                object = Uninterruptibles.getUninterruptibly(listenablefuture);
                if (object == null) {
                    throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + k0 + ".");
                }

                this.statsCounter.recordLoadSuccess(localcache_loadingvaluereference.elapsedNanos());
                this.storeLoadedValue(k0, i, localcache_loadingvaluereference, object);
                object1 = object;
            } finally {
                if (object == null) {
                    this.statsCounter.recordLoadException(localcache_loadingvaluereference.elapsedNanos());
                    this.removeLoadingValue(k0, i, localcache_loadingvaluereference);
                }

            }

            return object1;
        }

        V scheduleRefresh(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, K k0, int i, V v0, long j, CacheLoader<? super K, V> cacheloader) {
            if (this.map.refreshes() && j - localcache_referenceentry.getWriteTime() > this.map.refreshNanos && !localcache_referenceentry.getValueReference().isLoading()) {
                Object object = this.refresh(k0, i, cacheloader, true);

                if (object != null) {
                    return object;
                }
            }

            return v0;
        }

        @Nullable
        V refresh(K k0, int i, CacheLoader<? super K, V> cacheloader, boolean flag) {
            LocalCache.LoadingValueReference localcache_loadingvaluereference = this.insertLoadingValueReference(k0, i, flag);

            if (localcache_loadingvaluereference == null) {
                return null;
            } else {
                ListenableFuture listenablefuture = this.loadAsync(k0, i, localcache_loadingvaluereference, cacheloader);

                if (listenablefuture.isDone()) {
                    try {
                        return Uninterruptibles.getUninterruptibly(listenablefuture);
                    } catch (Throwable throwable) {
                        ;
                    }
                }

                return null;
            }
        }

        @Nullable
        LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K k0, int i, boolean flag) {
            LocalCache.ReferenceEntry localcache_referenceentry = null;

            this.lock();

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry1 = (LocalCache.ReferenceEntry) atomicreferencearray.get(k);

                for (localcache_referenceentry = localcache_referenceentry1; localcache_referenceentry != null; localcache_referenceentry = localcache_referenceentry.getNext()) {
                    Object object = localcache_referenceentry.getKey();

                    if (localcache_referenceentry.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry.getValueReference();
                        LocalCache.LoadingValueReference localcache_loadingvaluereference;

                        if (localcache_valuereference.isLoading() || flag && j - localcache_referenceentry.getWriteTime() < this.map.refreshNanos) {
                            localcache_loadingvaluereference = null;
                            return localcache_loadingvaluereference;
                        }

                        ++this.modCount;
                        localcache_loadingvaluereference = new LocalCache.LoadingValueReference<>(localcache_valuereference);
                        localcache_referenceentry.setValueReference(localcache_loadingvaluereference);
                        LocalCache.LoadingValueReference localcache_loadingvaluereference1 = localcache_loadingvaluereference;

                        return localcache_loadingvaluereference1;
                    }
                }

                ++this.modCount;
                LocalCache.LoadingValueReference localcache_loadingvaluereference2 = new LocalCache.LoadingValueReference<>();

                localcache_referenceentry = this.newEntry(k0, i, localcache_referenceentry1);
                localcache_referenceentry.setValueReference(localcache_loadingvaluereference2);
                atomicreferencearray.set(k, localcache_referenceentry);
                LocalCache.LoadingValueReference localcache_loadingvaluereference3 = localcache_loadingvaluereference2;

                return localcache_loadingvaluereference3;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        void tryDrainReferenceQueues() {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                } finally {
                    this.unlock();
                }
            }

        }

        @GuardedBy("Segment.this")
        void drainReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                this.drainKeyReferenceQueue();
            }

            if (this.map.usesValueReferences()) {
                this.drainValueReferenceQueue();
            }

        }

        @GuardedBy("Segment.this")
        void drainKeyReferenceQueue() {
            int i = 0;

            Reference reference;

            while ((reference = this.keyReferenceQueue.poll()) != null) {
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) reference;

                this.map.reclaimKey(localcache_referenceentry);
                ++i;
                if (i == 16) {
                    break;
                }
            }

        }

        @GuardedBy("Segment.this")
        void drainValueReferenceQueue() {
            int i = 0;

            Reference reference;

            while ((reference = this.valueReferenceQueue.poll()) != null) {
                LocalCache.ValueReference localcache_valuereference = (LocalCache.ValueReference) reference;

                this.map.reclaimValue(localcache_valuereference);
                ++i;
                if (i == 16) {
                    break;
                }
            }

        }

        void clearReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                this.clearKeyReferenceQueue();
            }

            if (this.map.usesValueReferences()) {
                this.clearValueReferenceQueue();
            }

        }

        void clearKeyReferenceQueue() {
            while (this.keyReferenceQueue.poll() != null) {
                ;
            }

        }

        void clearValueReferenceQueue() {
            while (this.valueReferenceQueue.poll() != null) {
                ;
            }

        }

        void recordRead(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, long i) {
            if (this.map.recordsAccess()) {
                localcache_referenceentry.setAccessTime(i);
            }

            this.recencyQueue.add(localcache_referenceentry);
        }

        @GuardedBy("Segment.this")
        void recordLockedRead(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, long i) {
            if (this.map.recordsAccess()) {
                localcache_referenceentry.setAccessTime(i);
            }

            this.accessQueue.add(localcache_referenceentry);
        }

        @GuardedBy("Segment.this")
        void recordWrite(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, int i, long j) {
            this.drainRecencyQueue();
            this.totalWeight += i;
            if (this.map.recordsAccess()) {
                localcache_referenceentry.setAccessTime(j);
            }

            if (this.map.recordsWrite()) {
                localcache_referenceentry.setWriteTime(j);
            }

            this.accessQueue.add(localcache_referenceentry);
            this.writeQueue.add(localcache_referenceentry);
        }

        @GuardedBy("Segment.this")
        void drainRecencyQueue() {
            LocalCache.ReferenceEntry localcache_referenceentry;

            while ((localcache_referenceentry = (LocalCache.ReferenceEntry) this.recencyQueue.poll()) != null) {
                if (this.accessQueue.contains(localcache_referenceentry)) {
                    this.accessQueue.add(localcache_referenceentry);
                }
            }

        }

        void tryExpireEntries(long i) {
            if (this.tryLock()) {
                try {
                    this.expireEntries(i);
                } finally {
                    this.unlock();
                }
            }

        }

        @GuardedBy("Segment.this")
        void expireEntries(long i) {
            this.drainRecencyQueue();

            LocalCache.ReferenceEntry localcache_referenceentry;

            while ((localcache_referenceentry = (LocalCache.ReferenceEntry) this.writeQueue.peek()) != null && this.map.isExpired(localcache_referenceentry, i)) {
                if (!this.removeEntry(localcache_referenceentry, localcache_referenceentry.getHash(), RemovalCause.EXPIRED)) {
                    throw new AssertionError();
                }
            }

            while ((localcache_referenceentry = (LocalCache.ReferenceEntry) this.accessQueue.peek()) != null && this.map.isExpired(localcache_referenceentry, i)) {
                if (!this.removeEntry(localcache_referenceentry, localcache_referenceentry.getHash(), RemovalCause.EXPIRED)) {
                    throw new AssertionError();
                }
            }

        }

        @GuardedBy("Segment.this")
        void enqueueNotification(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, RemovalCause removalcause) {
            this.enqueueNotification(localcache_referenceentry.getKey(), localcache_referenceentry.getHash(), localcache_referenceentry.getValueReference(), removalcause);
        }

        @GuardedBy("Segment.this")
        void enqueueNotification(@Nullable K k0, int i, LocalCache.ValueReference<K, V> localcache_valuereference, RemovalCause removalcause) {
            this.totalWeight -= localcache_valuereference.getWeight();
            if (removalcause.wasEvicted()) {
                this.statsCounter.recordEviction();
            }

            if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
                Object object = localcache_valuereference.get();
                RemovalNotification removalnotification = new RemovalNotification<>(k0, object, removalcause);

                this.map.removalNotificationQueue.offer(removalnotification);
            }

        }

        @GuardedBy("Segment.this")
        void evictEntries() {
            if (this.map.evictsBySize()) {
                this.drainRecencyQueue();

                LocalCache.ReferenceEntry localcache_referenceentry;

                do {
                    if ((long) this.totalWeight <= this.maxSegmentWeight) {
                        return;
                    }

                    localcache_referenceentry = this.getNextEvictable();
                } while (this.removeEntry(localcache_referenceentry, localcache_referenceentry.getHash(), RemovalCause.SIZE));

                throw new AssertionError();
            }
        }

        LocalCache.ReferenceEntry<K, V> getNextEvictable() {
            Iterator iterator = this.accessQueue.iterator();

            LocalCache.ReferenceEntry localcache_referenceentry;
            int i;

            do {
                if (!iterator.hasNext()) {
                    throw new AssertionError();
                }

                localcache_referenceentry = (LocalCache.ReferenceEntry) iterator.next();
                i = localcache_referenceentry.getValueReference().getWeight();
            } while (i <= 0);

            return localcache_referenceentry;
        }

        LocalCache.ReferenceEntry<K, V> getFirst(int i) {
            AtomicReferenceArray atomicreferencearray = this.table;

            return (LocalCache.ReferenceEntry) atomicreferencearray.get(i & atomicreferencearray.length() - 1);
        }

        @Nullable
        LocalCache.ReferenceEntry<K, V> getEntry(Object object, int i) {
            for (LocalCache.ReferenceEntry localcache_referenceentry = this.getFirst(i); localcache_referenceentry != null; localcache_referenceentry = localcache_referenceentry.getNext()) {
                if (localcache_referenceentry.getHash() == i) {
                    Object object1 = localcache_referenceentry.getKey();

                    if (object1 == null) {
                        this.tryDrainReferenceQueues();
                    } else if (this.map.keyEquivalence.equivalent(object, object1)) {
                        return localcache_referenceentry;
                    }
                }
            }

            return null;
        }

        @Nullable
        LocalCache.ReferenceEntry<K, V> getLiveEntry(Object object, int i, long j) {
            LocalCache.ReferenceEntry localcache_referenceentry = this.getEntry(object, i);

            if (localcache_referenceentry == null) {
                return null;
            } else if (this.map.isExpired(localcache_referenceentry, j)) {
                this.tryExpireEntries(j);
                return null;
            } else {
                return localcache_referenceentry;
            }
        }

        V getLiveValue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, long i) {
            if (localcache_referenceentry.getKey() == null) {
                this.tryDrainReferenceQueues();
                return null;
            } else {
                Object object = localcache_referenceentry.getValueReference().get();

                if (object == null) {
                    this.tryDrainReferenceQueues();
                    return null;
                } else if (this.map.isExpired(localcache_referenceentry, i)) {
                    this.tryExpireEntries(i);
                    return null;
                } else {
                    return object;
                }
            }
        }

        @Nullable
        V get(Object object, int i) {
            try {
                if (this.count != 0) {
                    long j = this.map.ticker.read();
                    LocalCache.ReferenceEntry localcache_referenceentry = this.getLiveEntry(object, i, j);
                    Object object1;

                    if (localcache_referenceentry == null) {
                        object1 = null;
                        return object1;
                    }

                    object1 = localcache_referenceentry.getValueReference().get();
                    if (object1 != null) {
                        this.recordRead(localcache_referenceentry, j);
                        Object object2 = this.scheduleRefresh(localcache_referenceentry, localcache_referenceentry.getKey(), i, object1, j, this.map.defaultLoader);

                        return object2;
                    }

                    this.tryDrainReferenceQueues();
                }

                Object object3 = null;

                return object3;
            } finally {
                this.postReadCleanup();
            }
        }

        boolean containsKey(Object object, int i) {
            boolean flag;

            try {
                if (this.count == 0) {
                    boolean flag1 = false;

                    return flag1;
                }

                long j = this.map.ticker.read();
                LocalCache.ReferenceEntry localcache_referenceentry = this.getLiveEntry(object, i, j);

                if (localcache_referenceentry != null) {
                    flag = localcache_referenceentry.getValueReference().get() != null;
                    return flag;
                }

                flag = false;
            } finally {
                this.postReadCleanup();
            }

            return flag;
        }

        @VisibleForTesting
        boolean containsValue(Object object) {
            try {
                if (this.count != 0) {
                    long i = this.map.ticker.read();
                    AtomicReferenceArray atomicreferencearray = this.table;
                    int j = atomicreferencearray.length();

                    for (int k = 0; k < j; ++k) {
                        for (LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(k); localcache_referenceentry != null; localcache_referenceentry = localcache_referenceentry.getNext()) {
                            Object object1 = this.getLiveValue(localcache_referenceentry, i);

                            if (object1 != null && this.map.valueEquivalence.equivalent(object, object1)) {
                                boolean flag = true;

                                return flag;
                            }
                        }
                    }
                }

                boolean flag1 = false;

                return flag1;
            } finally {
                this.postReadCleanup();
            }
        }

        @Nullable
        V put(K k0, int i, V v0, boolean flag) {
            this.lock();

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                int k = this.count + 1;

                if (k > this.threshold) {
                    this.expand();
                    k = this.count + 1;
                }

                AtomicReferenceArray atomicreferencearray = this.table;
                int l = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(l);

                Object object;
                LocalCache.ReferenceEntry localcache_referenceentry1;

                for (localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    object = localcache_referenceentry1.getKey();
                    if (localcache_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry1.getValueReference();
                        Object object1 = localcache_valuereference.get();
                        Object object2;

                        if (object1 == null) {
                            ++this.modCount;
                            if (localcache_valuereference.isActive()) {
                                this.enqueueNotification(k0, i, localcache_valuereference, RemovalCause.COLLECTED);
                                this.setValue(localcache_referenceentry1, k0, v0, j);
                                k = this.count;
                            } else {
                                this.setValue(localcache_referenceentry1, k0, v0, j);
                                k = this.count + 1;
                            }

                            this.count = k;
                            this.evictEntries();
                            object2 = null;
                            return object2;
                        }

                        if (!flag) {
                            ++this.modCount;
                            this.enqueueNotification(k0, i, localcache_valuereference, RemovalCause.REPLACED);
                            this.setValue(localcache_referenceentry1, k0, v0, j);
                            this.evictEntries();
                            object2 = object1;
                            return object2;
                        }

                        this.recordLockedRead(localcache_referenceentry1, j);
                        object2 = object1;
                        return object2;
                    }
                }

                ++this.modCount;
                localcache_referenceentry1 = this.newEntry(k0, i, localcache_referenceentry);
                this.setValue(localcache_referenceentry1, k0, v0, j);
                atomicreferencearray.set(l, localcache_referenceentry1);
                k = this.count + 1;
                this.count = k;
                this.evictEntries();
                object = null;
                return object;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        @GuardedBy("Segment.this")
        void expand() {
            AtomicReferenceArray atomicreferencearray = this.table;
            int i = atomicreferencearray.length();

            if (i < 1073741824) {
                int j = this.count;
                AtomicReferenceArray atomicreferencearray1 = this.newEntryArray(i << 1);

                this.threshold = atomicreferencearray1.length() * 3 / 4;
                int k = atomicreferencearray1.length() - 1;

                for (int l = 0; l < i; ++l) {
                    LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(l);

                    if (localcache_referenceentry != null) {
                        LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry.getNext();
                        int i1 = localcache_referenceentry.getHash() & k;

                        if (localcache_referenceentry1 == null) {
                            atomicreferencearray1.set(i1, localcache_referenceentry);
                        } else {
                            LocalCache.ReferenceEntry localcache_referenceentry2 = localcache_referenceentry;
                            int j1 = i1;

                            int k1;
                            LocalCache.ReferenceEntry localcache_referenceentry3;

                            for (localcache_referenceentry3 = localcache_referenceentry1; localcache_referenceentry3 != null; localcache_referenceentry3 = localcache_referenceentry3.getNext()) {
                                k1 = localcache_referenceentry3.getHash() & k;
                                if (k1 != j1) {
                                    j1 = k1;
                                    localcache_referenceentry2 = localcache_referenceentry3;
                                }
                            }

                            atomicreferencearray1.set(j1, localcache_referenceentry2);

                            for (localcache_referenceentry3 = localcache_referenceentry; localcache_referenceentry3 != localcache_referenceentry2; localcache_referenceentry3 = localcache_referenceentry3.getNext()) {
                                k1 = localcache_referenceentry3.getHash() & k;
                                LocalCache.ReferenceEntry localcache_referenceentry4 = (LocalCache.ReferenceEntry) atomicreferencearray1.get(k1);
                                LocalCache.ReferenceEntry localcache_referenceentry5 = this.copyEntry(localcache_referenceentry3, localcache_referenceentry4);

                                if (localcache_referenceentry5 != null) {
                                    atomicreferencearray1.set(k1, localcache_referenceentry5);
                                } else {
                                    this.removeCollectedEntry(localcache_referenceentry3);
                                    --j;
                                }
                            }
                        }
                    }
                }

                this.table = atomicreferencearray1;
                this.count = j;
            }
        }

        boolean replace(K k0, int i, V v0, V v1) {
            this.lock();

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(k);

                for (LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    Object object = localcache_referenceentry1.getKey();

                    if (localcache_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry1.getValueReference();
                        Object object1 = localcache_valuereference.get();
                        boolean flag;

                        if (object1 == null) {
                            if (localcache_valuereference.isActive()) {
                                int l = this.count - 1;

                                ++this.modCount;
                                LocalCache.ReferenceEntry localcache_referenceentry2 = this.removeValueFromChain(localcache_referenceentry, localcache_referenceentry1, object, i, localcache_valuereference, RemovalCause.COLLECTED);

                                l = this.count - 1;
                                atomicreferencearray.set(k, localcache_referenceentry2);
                                this.count = l;
                            }

                            flag = false;
                            return flag;
                        }

                        if (!this.map.valueEquivalence.equivalent(v0, object1)) {
                            this.recordLockedRead(localcache_referenceentry1, j);
                            flag = false;
                            return flag;
                        }

                        ++this.modCount;
                        this.enqueueNotification(k0, i, localcache_valuereference, RemovalCause.REPLACED);
                        this.setValue(localcache_referenceentry1, k0, v1, j);
                        this.evictEntries();
                        flag = true;
                        return flag;
                    }
                }

                boolean flag1 = false;

                return flag1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        @Nullable
        V replace(K k0, int i, V v0) {
            this.lock();

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(k);

                LocalCache.ReferenceEntry localcache_referenceentry1;

                for (localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    Object object = localcache_referenceentry1.getKey();

                    if (localcache_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry1.getValueReference();
                        Object object1 = localcache_valuereference.get();
                        Object object2;

                        if (object1 != null) {
                            ++this.modCount;
                            this.enqueueNotification(k0, i, localcache_valuereference, RemovalCause.REPLACED);
                            this.setValue(localcache_referenceentry1, k0, v0, j);
                            this.evictEntries();
                            object2 = object1;
                            return object2;
                        }

                        if (localcache_valuereference.isActive()) {
                            int l = this.count - 1;

                            ++this.modCount;
                            LocalCache.ReferenceEntry localcache_referenceentry2 = this.removeValueFromChain(localcache_referenceentry, localcache_referenceentry1, object, i, localcache_valuereference, RemovalCause.COLLECTED);

                            l = this.count - 1;
                            atomicreferencearray.set(k, localcache_referenceentry2);
                            this.count = l;
                        }

                        object2 = null;
                        return object2;
                    }
                }

                localcache_referenceentry1 = null;
                return localcache_referenceentry1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        @Nullable
        V remove(Object object, int i) {
            this.lock();

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                int k = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int l = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(l);

                LocalCache.ReferenceEntry localcache_referenceentry1;

                for (localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    Object object1 = localcache_referenceentry1.getKey();

                    if (localcache_referenceentry1.getHash() == i && object1 != null && this.map.keyEquivalence.equivalent(object, object1)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry1.getValueReference();
                        Object object2 = localcache_valuereference.get();
                        RemovalCause removalcause;
                        LocalCache.ReferenceEntry localcache_referenceentry2;

                        if (object2 != null) {
                            removalcause = RemovalCause.EXPLICIT;
                        } else {
                            if (!localcache_valuereference.isActive()) {
                                localcache_referenceentry2 = null;
                                return localcache_referenceentry2;
                            }

                            removalcause = RemovalCause.COLLECTED;
                        }

                        ++this.modCount;
                        localcache_referenceentry2 = this.removeValueFromChain(localcache_referenceentry, localcache_referenceentry1, object1, i, localcache_valuereference, removalcause);
                        k = this.count - 1;
                        atomicreferencearray.set(l, localcache_referenceentry2);
                        this.count = k;
                        Object object3 = object2;

                        return object3;
                    }
                }

                localcache_referenceentry1 = null;
                return localcache_referenceentry1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        boolean storeLoadedValue(K k0, int i, LocalCache.LoadingValueReference<K, V> localcache_loadingvaluereference, V v0) {
            this.lock();

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                int k = this.count + 1;

                if (k > this.threshold) {
                    this.expand();
                    k = this.count + 1;
                }

                AtomicReferenceArray atomicreferencearray = this.table;
                int l = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(l);

                LocalCache.ReferenceEntry localcache_referenceentry1;

                for (localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    Object object = localcache_referenceentry1.getKey();

                    if (localcache_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry1.getValueReference();
                        Object object1 = localcache_valuereference.get();
                        boolean flag;

                        if (localcache_loadingvaluereference == localcache_valuereference || object1 == null && localcache_valuereference != LocalCache.UNSET) {
                            ++this.modCount;
                            if (localcache_loadingvaluereference.isActive()) {
                                RemovalCause removalcause = object1 == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;

                                this.enqueueNotification(k0, i, localcache_loadingvaluereference, removalcause);
                                --k;
                            }

                            this.setValue(localcache_referenceentry1, k0, v0, j);
                            this.count = k;
                            this.evictEntries();
                            flag = true;
                            return flag;
                        }

                        LocalCache.WeightedStrongValueReference localcache_weightedstrongvaluereference = new LocalCache.WeightedStrongValueReference<>(v0, 0);

                        this.enqueueNotification(k0, i, localcache_weightedstrongvaluereference, RemovalCause.REPLACED);
                        flag = false;
                        return flag;
                    }
                }

                ++this.modCount;
                localcache_referenceentry1 = this.newEntry(k0, i, localcache_referenceentry);
                this.setValue(localcache_referenceentry1, k0, v0, j);
                atomicreferencearray.set(l, localcache_referenceentry1);
                this.count = k;
                this.evictEntries();
                boolean flag1 = true;

                return flag1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        boolean remove(Object object, int i, Object object1) {
            this.lock();

            try {
                long j = this.map.ticker.read();

                this.preWriteCleanup(j);
                int k = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int l = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(l);

                for (LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    Object object2 = localcache_referenceentry1.getKey();

                    if (localcache_referenceentry1.getHash() == i && object2 != null && this.map.keyEquivalence.equivalent(object, object2)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry1.getValueReference();
                        Object object3 = localcache_valuereference.get();
                        RemovalCause removalcause;

                        if (this.map.valueEquivalence.equivalent(object1, object3)) {
                            removalcause = RemovalCause.EXPLICIT;
                        } else {
                            if (object3 != null || !localcache_valuereference.isActive()) {
                                boolean flag = false;

                                return flag;
                            }

                            removalcause = RemovalCause.COLLECTED;
                        }

                        ++this.modCount;
                        LocalCache.ReferenceEntry localcache_referenceentry2 = this.removeValueFromChain(localcache_referenceentry, localcache_referenceentry1, object2, i, localcache_valuereference, removalcause);

                        k = this.count - 1;
                        atomicreferencearray.set(l, localcache_referenceentry2);
                        this.count = k;
                        boolean flag1 = removalcause == RemovalCause.EXPLICIT;

                        return flag1;
                    }
                }

                boolean flag2 = false;

                return flag2;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        void clear() {
            if (this.count != 0) {
                this.lock();

                try {
                    AtomicReferenceArray atomicreferencearray = this.table;

                    int i;

                    for (i = 0; i < atomicreferencearray.length(); ++i) {
                        for (LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(i); localcache_referenceentry != null; localcache_referenceentry = localcache_referenceentry.getNext()) {
                            if (localcache_referenceentry.getValueReference().isActive()) {
                                this.enqueueNotification(localcache_referenceentry, RemovalCause.EXPLICIT);
                            }
                        }
                    }

                    for (i = 0; i < atomicreferencearray.length(); ++i) {
                        atomicreferencearray.set(i, (Object) null);
                    }

                    this.clearReferenceQueues();
                    this.writeQueue.clear();
                    this.accessQueue.clear();
                    this.readCount.set(0);
                    ++this.modCount;
                    this.count = 0;
                } finally {
                    this.unlock();
                    this.postWriteCleanup();
                }
            }

        }

        @Nullable
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> removeValueFromChain(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1, @Nullable K k0, int i, LocalCache.ValueReference<K, V> localcache_valuereference, RemovalCause removalcause) {
            this.enqueueNotification(k0, i, localcache_valuereference, removalcause);
            this.writeQueue.remove(localcache_referenceentry1);
            this.accessQueue.remove(localcache_referenceentry1);
            if (localcache_valuereference.isLoading()) {
                localcache_valuereference.notifyNewValue((Object) null);
                return localcache_referenceentry;
            } else {
                return this.removeEntryFromChain(localcache_referenceentry, localcache_referenceentry1);
            }
        }

        @Nullable
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> removeEntryFromChain(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
            int i = this.count;
            LocalCache.ReferenceEntry localcache_referenceentry2 = localcache_referenceentry1.getNext();

            for (LocalCache.ReferenceEntry localcache_referenceentry3 = localcache_referenceentry; localcache_referenceentry3 != localcache_referenceentry1; localcache_referenceentry3 = localcache_referenceentry3.getNext()) {
                LocalCache.ReferenceEntry localcache_referenceentry4 = this.copyEntry(localcache_referenceentry3, localcache_referenceentry2);

                if (localcache_referenceentry4 != null) {
                    localcache_referenceentry2 = localcache_referenceentry4;
                } else {
                    this.removeCollectedEntry(localcache_referenceentry3);
                    --i;
                }
            }

            this.count = i;
            return localcache_referenceentry2;
        }

        @GuardedBy("Segment.this")
        void removeCollectedEntry(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.enqueueNotification(localcache_referenceentry, RemovalCause.COLLECTED);
            this.writeQueue.remove(localcache_referenceentry);
            this.accessQueue.remove(localcache_referenceentry);
        }

        boolean reclaimKey(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, int i) {
            this.lock();

            try {
                int j = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry1 = (LocalCache.ReferenceEntry) atomicreferencearray.get(k);

                for (LocalCache.ReferenceEntry localcache_referenceentry2 = localcache_referenceentry1; localcache_referenceentry2 != null; localcache_referenceentry2 = localcache_referenceentry2.getNext()) {
                    if (localcache_referenceentry2 == localcache_referenceentry) {
                        ++this.modCount;
                        LocalCache.ReferenceEntry localcache_referenceentry3 = this.removeValueFromChain(localcache_referenceentry1, localcache_referenceentry2, localcache_referenceentry2.getKey(), i, localcache_referenceentry2.getValueReference(), RemovalCause.COLLECTED);

                        j = this.count - 1;
                        atomicreferencearray.set(k, localcache_referenceentry3);
                        this.count = j;
                        boolean flag = true;

                        return flag;
                    }
                }

                boolean flag1 = false;

                return flag1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        boolean reclaimValue(K k0, int i, LocalCache.ValueReference<K, V> localcache_valuereference) {
            this.lock();

            try {
                int j = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(k);

                for (LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    Object object = localcache_referenceentry1.getKey();

                    if (localcache_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        LocalCache.ValueReference localcache_valuereference1 = localcache_referenceentry1.getValueReference();

                        if (localcache_valuereference1 != localcache_valuereference) {
                            boolean flag = false;

                            return flag;
                        }

                        ++this.modCount;
                        LocalCache.ReferenceEntry localcache_referenceentry2 = this.removeValueFromChain(localcache_referenceentry, localcache_referenceentry1, object, i, localcache_valuereference, RemovalCause.COLLECTED);

                        j = this.count - 1;
                        atomicreferencearray.set(k, localcache_referenceentry2);
                        this.count = j;
                        boolean flag1 = true;

                        return flag1;
                    }
                }

                boolean flag2 = false;

                return flag2;
            } finally {
                this.unlock();
                if (!this.isHeldByCurrentThread()) {
                    this.postWriteCleanup();
                }

            }
        }

        boolean removeLoadingValue(K k0, int i, LocalCache.LoadingValueReference<K, V> localcache_loadingvaluereference) {
            this.lock();

            try {
                AtomicReferenceArray atomicreferencearray = this.table;
                int j = i & atomicreferencearray.length() - 1;
                LocalCache.ReferenceEntry localcache_referenceentry = (LocalCache.ReferenceEntry) atomicreferencearray.get(j);

                for (LocalCache.ReferenceEntry localcache_referenceentry1 = localcache_referenceentry; localcache_referenceentry1 != null; localcache_referenceentry1 = localcache_referenceentry1.getNext()) {
                    Object object = localcache_referenceentry1.getKey();

                    if (localcache_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        LocalCache.ValueReference localcache_valuereference = localcache_referenceentry1.getValueReference();
                        boolean flag;

                        if (localcache_valuereference != localcache_loadingvaluereference) {
                            flag = false;
                            return flag;
                        }

                        if (localcache_loadingvaluereference.isActive()) {
                            localcache_referenceentry1.setValueReference(localcache_loadingvaluereference.getOldValue());
                        } else {
                            LocalCache.ReferenceEntry localcache_referenceentry2 = this.removeEntryFromChain(localcache_referenceentry, localcache_referenceentry1);

                            atomicreferencearray.set(j, localcache_referenceentry2);
                        }

                        flag = true;
                        return flag;
                    }
                }

                boolean flag1 = false;

                return flag1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        @GuardedBy("Segment.this")
        boolean removeEntry(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, int i, RemovalCause removalcause) {
            int j = this.count - 1;
            AtomicReferenceArray atomicreferencearray = this.table;
            int k = i & atomicreferencearray.length() - 1;
            LocalCache.ReferenceEntry localcache_referenceentry1 = (LocalCache.ReferenceEntry) atomicreferencearray.get(k);

            for (LocalCache.ReferenceEntry localcache_referenceentry2 = localcache_referenceentry1; localcache_referenceentry2 != null; localcache_referenceentry2 = localcache_referenceentry2.getNext()) {
                if (localcache_referenceentry2 == localcache_referenceentry) {
                    ++this.modCount;
                    LocalCache.ReferenceEntry localcache_referenceentry3 = this.removeValueFromChain(localcache_referenceentry1, localcache_referenceentry2, localcache_referenceentry2.getKey(), i, localcache_referenceentry2.getValueReference(), removalcause);

                    j = this.count - 1;
                    atomicreferencearray.set(k, localcache_referenceentry3);
                    this.count = j;
                    return true;
                }
            }

            return false;
        }

        void postReadCleanup() {
            if ((this.readCount.incrementAndGet() & 63) == 0) {
                this.cleanUp();
            }

        }

        @GuardedBy("Segment.this")
        void preWriteCleanup(long i) {
            this.runLockedCleanup(i);
        }

        void postWriteCleanup() {
            this.runUnlockedCleanup();
        }

        void cleanUp() {
            long i = this.map.ticker.read();

            this.runLockedCleanup(i);
            this.runUnlockedCleanup();
        }

        void runLockedCleanup(long i) {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                    this.expireEntries(i);
                    this.readCount.set(0);
                } finally {
                    this.unlock();
                }
            }

        }

        void runUnlockedCleanup() {
            if (!this.isHeldByCurrentThread()) {
                this.map.processPendingNotifications();
            }

        }
    }

    static final class WeightedStrongValueReference<K, V> extends LocalCache.StrongValueReference<K, V> {

        final int weight;

        WeightedStrongValueReference(V v0, int i) {
            super(v0);
            this.weight = i;
        }

        @Override
        public int getWeight() {
            return this.weight;
        }
    }

    static final class WeightedSoftValueReference<K, V> extends LocalCache.SoftValueReference<K, V> {

        final int weight;

        WeightedSoftValueReference(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, int i) {
            super(referencequeue, v0, localcache_referenceentry);
            this.weight = i;
        }

        @Override
        public int getWeight() {
            return this.weight;
        }

        @Override
        public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            return new LocalCache.WeightedSoftValueReference<>(referencequeue, v0, localcache_referenceentry, this.weight);
        }
    }

    static final class WeightedWeakValueReference<K, V> extends LocalCache.WeakValueReference<K, V> {

        final int weight;

        WeightedWeakValueReference(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, int i) {
            super(referencequeue, v0, localcache_referenceentry);
            this.weight = i;
        }

        @Override
        public int getWeight() {
            return this.weight;
        }

        @Override
        public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            return new LocalCache.WeightedWeakValueReference<>(referencequeue, v0, localcache_referenceentry, this.weight);
        }
    }

    static class StrongValueReference<K, V> implements LocalCache.ValueReference<K, V> {

        final V referent;

        StrongValueReference(V v0) {
            this.referent = v0;
        }

        @Override
        public V get() {
            return this.referent;
        }

        @Override
        public int getWeight() {
            return 1;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        @Override
        public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            return this;
        }

        @Override
        public boolean isLoading() {
            return false;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }

        @Override
        public void notifyNewValue(V v0) {}
    }

    static class SoftValueReference<K, V> extends SoftReference<V> implements LocalCache.ValueReference<K, V> {

        final LocalCache.ReferenceEntry<K, V> entry;

        SoftValueReference(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(v0, referencequeue);
            this.entry = localcache_referenceentry;
        }

        @Override
        public int getWeight() {
            return 1;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }

        @Override
        public void notifyNewValue(V v0) {}

        @Override
        public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            return new LocalCache.SoftValueReference<>(referencequeue, v0, localcache_referenceentry);
        }

        @Override
        public boolean isLoading() {
            return false;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }
    }

    static class WeakValueReference<K, V> extends WeakReference<V> implements LocalCache.ValueReference<K, V> {

        final LocalCache.ReferenceEntry<K, V> entry;

        WeakValueReference(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(v0, referencequeue);
            this.entry = localcache_referenceentry;
        }

        @Override
        public int getWeight() {
            return 1;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }

        @Override
        public void notifyNewValue(V v0) {}

        @Override
        public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            return new LocalCache.WeakValueReference<>(referencequeue, v0, localcache_referenceentry);
        }

        @Override
        public boolean isLoading() {
            return false;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }
    }

    static final class WeakAccessWriteEntry<K, V> extends LocalCache.WeakEntry<K, V> {

        volatile long accessTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
        volatile long writeTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

        WeakAccessWriteEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(referencequeue, k0, i, localcache_referenceentry);
        }

        @Override
        public long getAccessTime() {
            return this.accessTime;
        }

        @Override
        public void setAccessTime(long i) {
            this.accessTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }

        @Override
        public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextAccess = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }

        @Override
        public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousAccess = localcache_referenceentry;
        }

        @Override
        public long getWriteTime() {
            return this.writeTime;
        }

        @Override
        public void setWriteTime(long i) {
            this.writeTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }

        @Override
        public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextWrite = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }

        @Override
        public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousWrite = localcache_referenceentry;
        }
    }

    static final class WeakWriteEntry<K, V> extends LocalCache.WeakEntry<K, V> {

        volatile long writeTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

        WeakWriteEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(referencequeue, k0, i, localcache_referenceentry);
        }

        @Override
        public long getWriteTime() {
            return this.writeTime;
        }

        @Override
        public void setWriteTime(long i) {
            this.writeTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }

        @Override
        public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextWrite = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }

        @Override
        public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousWrite = localcache_referenceentry;
        }
    }

    static final class WeakAccessEntry<K, V> extends LocalCache.WeakEntry<K, V> {

        volatile long accessTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();

        WeakAccessEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(referencequeue, k0, i, localcache_referenceentry);
        }

        @Override
        public long getAccessTime() {
            return this.accessTime;
        }

        @Override
        public void setAccessTime(long i) {
            this.accessTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }

        @Override
        public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextAccess = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }

        @Override
        public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousAccess = localcache_referenceentry;
        }
    }

    static class WeakEntry<K, V> extends WeakReference<K> implements LocalCache.ReferenceEntry<K, V> {

        final int hash;
        final LocalCache.ReferenceEntry<K, V> next;
        volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();

        WeakEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(k0, referencequeue);
            this.hash = i;
            this.next = localcache_referenceentry;
        }

        @Override
        public K getKey() {
            return this.get();
        }

        @Override
        public long getAccessTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAccessTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getWriteTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWriteTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(LocalCache.ValueReference<K, V> localcache_valuereference) {
            this.valueReference = localcache_valuereference;
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    static final class StrongAccessWriteEntry<K, V> extends LocalCache.StrongEntry<K, V> {

        volatile long accessTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
        volatile long writeTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

        StrongAccessWriteEntry(K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(k0, i, localcache_referenceentry);
        }

        @Override
        public long getAccessTime() {
            return this.accessTime;
        }

        @Override
        public void setAccessTime(long i) {
            this.accessTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }

        @Override
        public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextAccess = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }

        @Override
        public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousAccess = localcache_referenceentry;
        }

        @Override
        public long getWriteTime() {
            return this.writeTime;
        }

        @Override
        public void setWriteTime(long i) {
            this.writeTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }

        @Override
        public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextWrite = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }

        @Override
        public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousWrite = localcache_referenceentry;
        }
    }

    static final class StrongWriteEntry<K, V> extends LocalCache.StrongEntry<K, V> {

        volatile long writeTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

        StrongWriteEntry(K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(k0, i, localcache_referenceentry);
        }

        @Override
        public long getWriteTime() {
            return this.writeTime;
        }

        @Override
        public void setWriteTime(long i) {
            this.writeTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }

        @Override
        public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextWrite = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }

        @Override
        public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousWrite = localcache_referenceentry;
        }
    }

    static final class StrongAccessEntry<K, V> extends LocalCache.StrongEntry<K, V> {

        volatile long accessTime = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
        @GuardedBy("Segment.this")
        LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();

        StrongAccessEntry(K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            super(k0, i, localcache_referenceentry);
        }

        @Override
        public long getAccessTime() {
            return this.accessTime;
        }

        @Override
        public void setAccessTime(long i) {
            this.accessTime = i;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }

        @Override
        public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.nextAccess = localcache_referenceentry;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }

        @Override
        public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.previousAccess = localcache_referenceentry;
        }
    }

    static class StrongEntry<K, V> extends LocalCache.AbstractReferenceEntry<K, V> {

        final K key;
        final int hash;
        final LocalCache.ReferenceEntry<K, V> next;
        volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();

        StrongEntry(K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            this.key = k0;
            this.hash = i;
            this.next = localcache_referenceentry;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public LocalCache.ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(LocalCache.ValueReference<K, V> localcache_valuereference) {
            this.valueReference = localcache_valuereference;
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    abstract static class AbstractReferenceEntry<K, V> implements LocalCache.ReferenceEntry<K, V> {

        AbstractReferenceEntry() {}

        @Override
        public LocalCache.ValueReference<K, V> getValueReference() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setValueReference(LocalCache.ValueReference<K, V> localcache_valuereference) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getHash() {
            throw new UnsupportedOperationException();
        }

        @Override
        public K getKey() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getAccessTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAccessTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getWriteTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWriteTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
            throw new UnsupportedOperationException();
        }
    }

    private static enum NullEntry implements LocalCache.ReferenceEntry<Object, Object> {

        INSTANCE;

        private NullEntry() {}

        @Override
        public LocalCache.ValueReference<Object, Object> getValueReference() {
            return null;
        }

        @Override
        public void setValueReference(LocalCache.ValueReference<Object, Object> localcache_valuereference) {}

        @Override
        public LocalCache.ReferenceEntry<Object, Object> getNext() {
            return null;
        }

        @Override
        public int getHash() {
            return 0;
        }

        @Override
        public Object getKey() {
            return null;
        }

        @Override
        public long getAccessTime() {
            return 0L;
        }

        @Override
        public void setAccessTime(long i) {}

        @Override
        public LocalCache.ReferenceEntry<Object, Object> getNextInAccessQueue() {
            return this;
        }

        @Override
        public void setNextInAccessQueue(LocalCache.ReferenceEntry<Object, Object> localcache_referenceentry) {}

        @Override
        public LocalCache.ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
            return this;
        }

        @Override
        public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<Object, Object> localcache_referenceentry) {}

        @Override
        public long getWriteTime() {
            return 0L;
        }

        @Override
        public void setWriteTime(long i) {}

        @Override
        public LocalCache.ReferenceEntry<Object, Object> getNextInWriteQueue() {
            return this;
        }

        @Override
        public void setNextInWriteQueue(LocalCache.ReferenceEntry<Object, Object> localcache_referenceentry) {}

        @Override
        public LocalCache.ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
            return this;
        }

        @Override
        public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<Object, Object> localcache_referenceentry) {}
    }

    interface ReferenceEntry<K, V> {

        LocalCache.ValueReference<K, V> getValueReference();

        void setValueReference(LocalCache.ValueReference<K, V> localcache_valuereference);

        @Nullable
        LocalCache.ReferenceEntry<K, V> getNext();

        int getHash();

        @Nullable
        K getKey();

        long getAccessTime();

        void setAccessTime(long i);

        LocalCache.ReferenceEntry<K, V> getNextInAccessQueue();

        void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry);

        LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue();

        void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry);

        long getWriteTime();

        void setWriteTime(long i);

        LocalCache.ReferenceEntry<K, V> getNextInWriteQueue();

        void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry);

        LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue();

        void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> localcache_referenceentry);
    }

    interface ValueReference<K, V> {

        @Nullable
        V get();

        V waitForValue() throws ExecutionException;

        int getWeight();

        @Nullable
        LocalCache.ReferenceEntry<K, V> getEntry();

        LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, @Nullable V v0, LocalCache.ReferenceEntry<K, V> localcache_referenceentry);

        void notifyNewValue(@Nullable V v0);

        boolean isLoading();

        boolean isActive();
    }

    static enum EntryFactory {

        STRONG {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.StrongEntry<>(k0, i, localcache_referenceentry);
            }
        },
        STRONG_ACCESS {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.StrongAccessEntry<>(k0, i, localcache_referenceentry);
            }

            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
                LocalCache.ReferenceEntry localcache_referenceentry2 = super.copyEntry(localcache_segment, localcache_referenceentry, localcache_referenceentry1);

                this.copyAccessEntry(localcache_referenceentry, localcache_referenceentry2);
                return localcache_referenceentry2;
            }
        },
        STRONG_WRITE {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.StrongWriteEntry<>(k0, i, localcache_referenceentry);
            }

            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
                LocalCache.ReferenceEntry localcache_referenceentry2 = super.copyEntry(localcache_segment, localcache_referenceentry, localcache_referenceentry1);

                this.copyWriteEntry(localcache_referenceentry, localcache_referenceentry2);
                return localcache_referenceentry2;
            }
        },
        STRONG_ACCESS_WRITE {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.StrongAccessWriteEntry<>(k0, i, localcache_referenceentry);
            }

            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
                LocalCache.ReferenceEntry localcache_referenceentry2 = super.copyEntry(localcache_segment, localcache_referenceentry, localcache_referenceentry1);

                this.copyAccessEntry(localcache_referenceentry, localcache_referenceentry2);
                this.copyWriteEntry(localcache_referenceentry, localcache_referenceentry2);
                return localcache_referenceentry2;
            }
        },
        WEAK {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.WeakEntry<>(localcache_segment.keyReferenceQueue, k0, i, localcache_referenceentry);
            }
        },
        WEAK_ACCESS {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.WeakAccessEntry<>(localcache_segment.keyReferenceQueue, k0, i, localcache_referenceentry);
            }

            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
                LocalCache.ReferenceEntry localcache_referenceentry2 = super.copyEntry(localcache_segment, localcache_referenceentry, localcache_referenceentry1);

                this.copyAccessEntry(localcache_referenceentry, localcache_referenceentry2);
                return localcache_referenceentry2;
            }
        },
        WEAK_WRITE {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.WeakWriteEntry<>(localcache_segment.keyReferenceQueue, k0, i, localcache_referenceentry);
            }

            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
                LocalCache.ReferenceEntry localcache_referenceentry2 = super.copyEntry(localcache_segment, localcache_referenceentry, localcache_referenceentry1);

                this.copyWriteEntry(localcache_referenceentry, localcache_referenceentry2);
                return localcache_referenceentry2;
            }
        },
        WEAK_ACCESS_WRITE {
            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry) {
                return new LocalCache.WeakAccessWriteEntry<>(localcache_segment.keyReferenceQueue, k0, i, localcache_referenceentry);
            }

            @Override
            <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
                LocalCache.ReferenceEntry localcache_referenceentry2 = super.copyEntry(localcache_segment, localcache_referenceentry, localcache_referenceentry1);

                this.copyAccessEntry(localcache_referenceentry, localcache_referenceentry2);
                this.copyWriteEntry(localcache_referenceentry, localcache_referenceentry2);
                return localcache_referenceentry2;
            }
        };

        static final int ACCESS_MASK = 1;
        static final int WRITE_MASK = 2;
        static final int WEAK_MASK = 4;
        static final LocalCache.EntryFactory[] factories = new LocalCache.EntryFactory[]{LocalCache.EntryFactory.STRONG, LocalCache.EntryFactory.STRONG_ACCESS, LocalCache.EntryFactory.STRONG_WRITE, LocalCache.EntryFactory.STRONG_ACCESS_WRITE, LocalCache.EntryFactory.WEAK, LocalCache.EntryFactory.WEAK_ACCESS, LocalCache.EntryFactory.WEAK_WRITE, LocalCache.EntryFactory.WEAK_ACCESS_WRITE};

        private EntryFactory() {}

        static LocalCache.EntryFactory getFactory(LocalCache.Strength localcache_strength, boolean flag, boolean flag1) {
            int i = (localcache_strength == LocalCache.Strength.WEAK ? 4 : 0) | (flag ? 1 : 0) | (flag1 ? 2 : 0);

            return LocalCache.EntryFactory.factories[i];
        }

        abstract <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> localcache_segment, K k0, int i, @Nullable LocalCache.ReferenceEntry<K, V> localcache_referenceentry);

        @GuardedBy("Segment.this")
        <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
            return this.newEntry(localcache_segment, localcache_referenceentry.getKey(), localcache_referenceentry.getHash(), localcache_referenceentry1);
        }

        @GuardedBy("Segment.this")
        <K, V> void copyAccessEntry(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
            localcache_referenceentry1.setAccessTime(localcache_referenceentry.getAccessTime());
            LocalCache.connectAccessOrder(localcache_referenceentry.getPreviousInAccessQueue(), localcache_referenceentry1);
            LocalCache.connectAccessOrder(localcache_referenceentry1, localcache_referenceentry.getNextInAccessQueue());
            LocalCache.nullifyAccessOrder(localcache_referenceentry);
        }

        @GuardedBy("Segment.this")
        <K, V> void copyWriteEntry(LocalCache.ReferenceEntry<K, V> localcache_referenceentry, LocalCache.ReferenceEntry<K, V> localcache_referenceentry1) {
            localcache_referenceentry1.setWriteTime(localcache_referenceentry.getWriteTime());
            LocalCache.connectWriteOrder(localcache_referenceentry.getPreviousInWriteQueue(), localcache_referenceentry1);
            LocalCache.connectWriteOrder(localcache_referenceentry1, localcache_referenceentry.getNextInWriteQueue());
            LocalCache.nullifyWriteOrder(localcache_referenceentry);
        }
    }

    static enum Strength {

        STRONG {
            @Override
            <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, V v0, int i) {
                return (LocalCache.ValueReference) (i == 1 ? new LocalCache.StrongValueReference<>(v0) : new LocalCache.WeightedStrongValueReference<>(v0, i));
            }

            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.equals();
            }
        },
        SOFT {
            @Override
            <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, V v0, int i) {
                return (LocalCache.ValueReference) (i == 1 ? new LocalCache.SoftValueReference<>(localcache_segment.valueReferenceQueue, v0, localcache_referenceentry) : new LocalCache.WeightedSoftValueReference<>(localcache_segment.valueReferenceQueue, v0, localcache_referenceentry, i));
            }

            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
        },
        WEAK {
            @Override
            <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, V v0, int i) {
                return (LocalCache.ValueReference) (i == 1 ? new LocalCache.WeakValueReference<>(localcache_segment.valueReferenceQueue, v0, localcache_referenceentry) : new LocalCache.WeightedWeakValueReference<>(localcache_segment.valueReferenceQueue, v0, localcache_referenceentry, i));
            }

            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
        };

        private Strength() {}

        abstract <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> localcache_segment, LocalCache.ReferenceEntry<K, V> localcache_referenceentry, V v0, int i);

        abstract Equivalence<Object> defaultEquivalence();
    }
}
