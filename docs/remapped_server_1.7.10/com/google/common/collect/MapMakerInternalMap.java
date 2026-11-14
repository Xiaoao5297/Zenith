package com.google.common.collect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
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

class MapMakerInternalMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {

    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int CONTAINS_VALUE_RETRIES = 3;
    static final int DRAIN_THRESHOLD = 63;
    static final int DRAIN_MAX = 16;
    static final long CLEANUP_EXECUTOR_DELAY_SECS = 60L;
    private static final Logger logger = Logger.getLogger(MapMakerInternalMap.class.getName());
    final transient int segmentMask;
    final transient int segmentShift;
    final transient MapMakerInternalMap.Segment<K, V>[] segments;
    final int concurrencyLevel;
    final Equivalence<Object> keyEquivalence;
    final Equivalence<Object> valueEquivalence;
    final MapMakerInternalMap.Strength keyStrength;
    final MapMakerInternalMap.Strength valueStrength;
    final int maximumSize;
    final long expireAfterAccessNanos;
    final long expireAfterWriteNanos;
    final Queue<MapMaker.RemovalNotification<K, V>> removalNotificationQueue;
    final MapMaker.RemovalListener<K, V> removalListener;
    final transient MapMakerInternalMap.EntryFactory entryFactory;
    final Ticker ticker;
    static final MapMakerInternalMap.ValueReference<Object, Object> UNSET = new MapMakerInternalMap.ValueReference<Object, Object>() {
        @Override
        public Object get() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<Object, Object> getEntry() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> referencequeue, @Nullable Object object, MapMakerInternalMap.ReferenceEntry<Object, Object> mapmakerinternalmap_referenceentry) {
            return this;
        }

        @Override
        public boolean isComputingReference() {
            return false;
        }

        @Override
        public Object waitForValue() {
            return null;
        }

        @Override
        public void clear(MapMakerInternalMap.ValueReference<Object, Object> mapmakerinternalmap_valuereference) {}
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
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Entry<K, V>> entrySet;
    private static final long serialVersionUID = 5L;

    MapMakerInternalMap(MapMaker mapmaker) {
        this.concurrencyLevel = Math.min(mapmaker.getConcurrencyLevel(), 65536);
        this.keyStrength = mapmaker.getKeyStrength();
        this.valueStrength = mapmaker.getValueStrength();
        this.keyEquivalence = mapmaker.getKeyEquivalence();
        this.valueEquivalence = this.valueStrength.defaultEquivalence();
        this.maximumSize = mapmaker.maximumSize;
        this.expireAfterAccessNanos = mapmaker.getExpireAfterAccessNanos();
        this.expireAfterWriteNanos = mapmaker.getExpireAfterWriteNanos();
        this.entryFactory = MapMakerInternalMap.EntryFactory.getFactory(this.keyStrength, this.expires(), this.evictsBySize());
        this.ticker = mapmaker.getTicker();
        this.removalListener = mapmaker.getRemovalListener();
        this.removalNotificationQueue = (Queue) (this.removalListener == GenericMapMaker.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
        int i = Math.min(mapmaker.getInitialCapacity(), 1073741824);

        if (this.evictsBySize()) {
            i = Math.min(i, this.maximumSize);
        }

        int j = 0;

        int k;

        for (k = 1; k < this.concurrencyLevel && (!this.evictsBySize() || k * 2 <= this.maximumSize); k <<= 1) {
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

        int j1;

        if (this.evictsBySize()) {
            j1 = this.maximumSize / k + 1;
            int k1 = this.maximumSize % k;

            for (int l1 = 0; l1 < this.segments.length; ++l1) {
                if (l1 == k1) {
                    --j1;
                }

                this.segments[l1] = this.createSegment(i1, j1);
            }
        } else {
            for (j1 = 0; j1 < this.segments.length; ++j1) {
                this.segments[j1] = this.createSegment(i1, -1);
            }
        }

    }

    boolean evictsBySize() {
        return this.maximumSize != -1;
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

    boolean usesKeyReferences() {
        return this.keyStrength != MapMakerInternalMap.Strength.STRONG;
    }

    boolean usesValueReferences() {
        return this.valueStrength != MapMakerInternalMap.Strength.STRONG;
    }

    static <K, V> MapMakerInternalMap.ValueReference<K, V> unset() {
        return MapMakerInternalMap.UNSET;
    }

    static <K, V> MapMakerInternalMap.ReferenceEntry<K, V> nullEntry() {
        return MapMakerInternalMap.NullEntry.INSTANCE;
    }

    static <E> Queue<E> discardingQueue() {
        return MapMakerInternalMap.DISCARDING_QUEUE;
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
    MapMakerInternalMap.ReferenceEntry<K, V> newEntry(K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
        return this.segmentFor(i).newEntry(k0, i, mapmakerinternalmap_referenceentry);
    }

    @GuardedBy("Segment.this")
    @VisibleForTesting
    MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
        int i = mapmakerinternalmap_referenceentry.getHash();

        return this.segmentFor(i).copyEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);
    }

    @GuardedBy("Segment.this")
    @VisibleForTesting
    MapMakerInternalMap.ValueReference<K, V> newValueReference(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, V v0) {
        int i = mapmakerinternalmap_referenceentry.getHash();

        return this.valueStrength.referenceValue(this.segmentFor(i), mapmakerinternalmap_referenceentry, v0);
    }

    int hash(Object object) {
        int i = this.keyEquivalence.hash(object);

        return rehash(i);
    }

    void reclaimValue(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = mapmakerinternalmap_valuereference.getEntry();
        int i = mapmakerinternalmap_referenceentry.getHash();

        this.segmentFor(i).reclaimValue(mapmakerinternalmap_referenceentry.getKey(), i, mapmakerinternalmap_valuereference);
    }

    void reclaimKey(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
        int i = mapmakerinternalmap_referenceentry.getHash();

        this.segmentFor(i).reclaimKey(mapmakerinternalmap_referenceentry, i);
    }

    @VisibleForTesting
    boolean isLive(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
        return this.segmentFor(mapmakerinternalmap_referenceentry.getHash()).getLiveValue(mapmakerinternalmap_referenceentry) != null;
    }

    MapMakerInternalMap.Segment<K, V> segmentFor(int i) {
        return this.segments[i >>> this.segmentShift & this.segmentMask];
    }

    MapMakerInternalMap.Segment<K, V> createSegment(int i, int j) {
        return new MapMakerInternalMap.Segment<>(this, i, j);
    }

    V getLiveValue(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
        if (mapmakerinternalmap_referenceentry.getKey() == null) {
            return null;
        } else {
            Object object = mapmakerinternalmap_referenceentry.getValueReference().get();

            return object == null ? null : (this.expires() && this.isExpired(mapmakerinternalmap_referenceentry) ? null : object);
        }
    }

    boolean isExpired(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
        return this.isExpired(mapmakerinternalmap_referenceentry, this.ticker.read());
    }

    boolean isExpired(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, long i) {
        return i - mapmakerinternalmap_referenceentry.getExpirationTime() > 0L;
    }

    @GuardedBy("Segment.this")
    static <K, V> void connectExpirables(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
        mapmakerinternalmap_referenceentry.setNextExpirable(mapmakerinternalmap_referenceentry1);
        mapmakerinternalmap_referenceentry1.setPreviousExpirable(mapmakerinternalmap_referenceentry);
    }

    @GuardedBy("Segment.this")
    static <K, V> void nullifyExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = nullEntry();

        mapmakerinternalmap_referenceentry.setNextExpirable(mapmakerinternalmap_referenceentry1);
        mapmakerinternalmap_referenceentry.setPreviousExpirable(mapmakerinternalmap_referenceentry1);
    }

    void processPendingNotifications() {
        MapMaker.RemovalNotification mapmaker_removalnotification;

        while ((mapmaker_removalnotification = (MapMaker.RemovalNotification) this.removalNotificationQueue.poll()) != null) {
            try {
                this.removalListener.onRemoval(mapmaker_removalnotification);
            } catch (Exception exception) {
                MapMakerInternalMap.logger.log(Level.WARNING, "Exception thrown by removal listener", exception);
            }
        }

    }

    @GuardedBy("Segment.this")
    static <K, V> void connectEvictables(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
        mapmakerinternalmap_referenceentry.setNextEvictable(mapmakerinternalmap_referenceentry1);
        mapmakerinternalmap_referenceentry1.setPreviousEvictable(mapmakerinternalmap_referenceentry);
    }

    @GuardedBy("Segment.this")
    static <K, V> void nullifyEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = nullEntry();

        mapmakerinternalmap_referenceentry.setNextEvictable(mapmakerinternalmap_referenceentry1);
        mapmakerinternalmap_referenceentry.setPreviousEvictable(mapmakerinternalmap_referenceentry1);
    }

    final MapMakerInternalMap.Segment<K, V>[] newSegmentArray(int i) {
        return new MapMakerInternalMap.Segment[i];
    }

    public boolean isEmpty() {
        long i = 0L;
        MapMakerInternalMap.Segment[] amapmakerinternalmap_segment = this.segments;

        int j;

        for (j = 0; j < amapmakerinternalmap_segment.length; ++j) {
            if (amapmakerinternalmap_segment[j].count != 0) {
                return false;
            }

            i += (long) amapmakerinternalmap_segment[j].modCount;
        }

        if (i != 0L) {
            for (j = 0; j < amapmakerinternalmap_segment.length; ++j) {
                if (amapmakerinternalmap_segment[j].count != 0) {
                    return false;
                }

                i -= (long) amapmakerinternalmap_segment[j].modCount;
            }

            if (i != 0L) {
                return false;
            }
        }

        return true;
    }

    public int size() {
        MapMakerInternalMap.Segment[] amapmakerinternalmap_segment = this.segments;
        long i = 0L;

        for (int j = 0; j < amapmakerinternalmap_segment.length; ++j) {
            i += (long) amapmakerinternalmap_segment[j].count;
        }

        return Ints.saturatedCast(i);
    }

    public V get(@Nullable Object object) {
        if (object == null) {
            return null;
        } else {
            int i = this.hash(object);

            return this.segmentFor(i).get(object, i);
        }
    }

    MapMakerInternalMap.ReferenceEntry<K, V> getEntry(@Nullable Object object) {
        if (object == null) {
            return null;
        } else {
            int i = this.hash(object);

            return this.segmentFor(i).getEntry(object, i);
        }
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
            MapMakerInternalMap.Segment[] amapmakerinternalmap_segment = this.segments;
            long i = -1L;

            for (int j = 0; j < 3; ++j) {
                long k = 0L;
                MapMakerInternalMap.Segment[] amapmakerinternalmap_segment1 = amapmakerinternalmap_segment;
                int l = amapmakerinternalmap_segment.length;

                for (int i1 = 0; i1 < l; ++i1) {
                    MapMakerInternalMap.Segment mapmakerinternalmap_segment = amapmakerinternalmap_segment1[i1];
                    int j1 = mapmakerinternalmap_segment.count;
                    AtomicReferenceArray atomicreferencearray = mapmakerinternalmap_segment.table;

                    for (int k1 = 0; k1 < atomicreferencearray.length(); ++k1) {
                        for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k1); mapmakerinternalmap_referenceentry != null; mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry.getNext()) {
                            Object object1 = mapmakerinternalmap_segment.getLiveValue(mapmakerinternalmap_referenceentry);

                            if (object1 != null && this.valueEquivalence.equivalent(object, object1)) {
                                return true;
                            }
                        }
                    }

                    k += (long) mapmakerinternalmap_segment.modCount;
                }

                if (k == i) {
                    break;
                }

                i = k;
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
        MapMakerInternalMap.Segment[] amapmakerinternalmap_segment = this.segments;
        int i = amapmakerinternalmap_segment.length;

        for (int j = 0; j < i; ++j) {
            MapMakerInternalMap.Segment mapmakerinternalmap_segment = amapmakerinternalmap_segment[j];

            mapmakerinternalmap_segment.clear();
        }

    }

    public Set<K> keySet() {
        Set set = this.keySet;

        return set != null ? set : (this.keySet = new MapMakerInternalMap.KeySet());
    }

    public Collection<V> values() {
        Collection collection = this.values;

        return collection != null ? collection : (this.values = new MapMakerInternalMap.Values());
    }

    public Set<Entry<K, V>> entrySet() {
        Set set = this.entrySet;

        return set != null ? set : (this.entrySet = new MapMakerInternalMap.EntrySet());
    }

    Object writeReplace() {
        return new MapMakerInternalMap.SerializationProxy<>(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this);
    }

    private static final class SerializationProxy<K, V> extends MapMakerInternalMap.AbstractSerializationProxy<K, V> {

        private static final long serialVersionUID = 3L;

        SerializationProxy(MapMakerInternalMap.Strength mapmakerinternalmap_strength, MapMakerInternalMap.Strength mapmakerinternalmap_strength1, Equivalence<Object> equivalence, Equivalence<Object> equivalence1, long i, long j, int k, int l, MapMaker.RemovalListener<? super K, ? super V> mapmaker_removallistener, ConcurrentMap<K, V> concurrentmap) {
            super(mapmakerinternalmap_strength, mapmakerinternalmap_strength1, equivalence, equivalence1, i, j, k, l, mapmaker_removallistener, concurrentmap);
        }

        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.defaultWriteObject();
            this.writeMapTo(objectoutputstream);
        }

        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            MapMaker mapmaker = this.readMapMaker(objectinputstream);

            this.delegate = mapmaker.makeMap();
            this.readEntries(objectinputstream);
        }

        private Object readResolve() {
            return this.delegate;
        }
    }

    abstract static class AbstractSerializationProxy<K, V> extends ForwardingConcurrentMap<K, V> implements Serializable {

        private static final long serialVersionUID = 3L;
        final MapMakerInternalMap.Strength keyStrength;
        final MapMakerInternalMap.Strength valueStrength;
        final Equivalence<Object> keyEquivalence;
        final Equivalence<Object> valueEquivalence;
        final long expireAfterWriteNanos;
        final long expireAfterAccessNanos;
        final int maximumSize;
        final int concurrencyLevel;
        final MapMaker.RemovalListener<? super K, ? super V> removalListener;
        transient ConcurrentMap<K, V> delegate;

        AbstractSerializationProxy(MapMakerInternalMap.Strength mapmakerinternalmap_strength, MapMakerInternalMap.Strength mapmakerinternalmap_strength1, Equivalence<Object> equivalence, Equivalence<Object> equivalence1, long i, long j, int k, int l, MapMaker.RemovalListener<? super K, ? super V> mapmaker_removallistener, ConcurrentMap<K, V> concurrentmap) {
            this.keyStrength = mapmakerinternalmap_strength;
            this.valueStrength = mapmakerinternalmap_strength1;
            this.keyEquivalence = equivalence;
            this.valueEquivalence = equivalence1;
            this.expireAfterWriteNanos = i;
            this.expireAfterAccessNanos = j;
            this.maximumSize = k;
            this.concurrencyLevel = l;
            this.removalListener = mapmaker_removallistener;
            this.delegate = concurrentmap;
        }

        @Override
        protected ConcurrentMap<K, V> delegate() {
            return this.delegate;
        }

        void writeMapTo(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.writeInt(this.delegate.size());
            Iterator iterator = this.delegate.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                objectoutputstream.writeObject(entry.getKey());
                objectoutputstream.writeObject(entry.getValue());
            }

            objectoutputstream.writeObject((Object) null);
        }

        MapMaker readMapMaker(ObjectInputStream objectinputstream) throws IOException {
            int i = objectinputstream.readInt();
            MapMaker mapmaker = (new MapMaker()).initialCapacity(i).setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).concurrencyLevel(this.concurrencyLevel);

            mapmaker.removalListener(this.removalListener);
            if (this.expireAfterWriteNanos > 0L) {
                mapmaker.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
            }

            if (this.expireAfterAccessNanos > 0L) {
                mapmaker.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
            }

            if (this.maximumSize != -1) {
                mapmaker.maximumSize(this.maximumSize);
            }

            return mapmaker;
        }

        void readEntries(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            while (true) {
                Object object = objectinputstream.readObject();

                if (object == null) {
                    return;
                }

                Object object1 = objectinputstream.readObject();

                this.delegate.put(object, object1);
            }
        }
    }

    final class EntrySet extends AbstractSet<Entry<K, V>> {

        EntrySet() {}

        public Iterator<Entry<K, V>> iterator() {
            return MapMakerInternalMap.this.new EntryIterator();
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
                    Object object2 = MapMakerInternalMap.this.get(object1);

                    return object2 != null && MapMakerInternalMap.this.valueEquivalence.equivalent(entry.getValue(), object2);
                }
            }
        }

        public boolean remove(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                Entry entry = (Entry) object;
                Object object1 = entry.getKey();

                return object1 != null && MapMakerInternalMap.this.remove(object1, entry.getValue());
            }
        }

        public int size() {
            return MapMakerInternalMap.this.size();
        }

        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }

        public void clear() {
            MapMakerInternalMap.this.clear();
        }
    }

    final class Values extends AbstractCollection<V> {

        Values() {}

        public Iterator<V> iterator() {
            return MapMakerInternalMap.this.new ValueIterator();
        }

        public int size() {
            return MapMakerInternalMap.this.size();
        }

        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }

        public boolean contains(Object object) {
            return MapMakerInternalMap.this.containsValue(object);
        }

        public void clear() {
            MapMakerInternalMap.this.clear();
        }
    }

    final class KeySet extends AbstractSet<K> {

        KeySet() {}

        public Iterator<K> iterator() {
            return MapMakerInternalMap.this.new KeyIterator();
        }

        public int size() {
            return MapMakerInternalMap.this.size();
        }

        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }

        public boolean contains(Object object) {
            return MapMakerInternalMap.this.containsKey(object);
        }

        public boolean remove(Object object) {
            return MapMakerInternalMap.this.remove(object) != null;
        }

        public void clear() {
            MapMakerInternalMap.this.clear();
        }
    }

    final class EntryIterator extends MapMakerInternalMap<K, V>.HashIterator<Entry<K, V>> {

        EntryIterator() {
            super();
        }

        @Override
        public Entry<K, V> next() {
            return this.nextEntry();
        }
    }

    final class WriteThroughEntry extends AbstractMapEntry<K, V> {

        final K key;
        V value;

        WriteThroughEntry(K k0, V v0) {
            this.key = k0;
            this.value = v0;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                Entry entry = (Entry) object;

                return this.key.equals(entry.getKey()) && this.value.equals(entry.getValue());
            }
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }

        @Override
        public V setValue(V v0) {
            Object object = MapMakerInternalMap.this.put(this.key, v0);

            this.value = v0;
            return object;
        }
    }

    final class ValueIterator extends MapMakerInternalMap<K, V>.HashIterator<V> {

        ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return this.nextEntry().getValue();
        }
    }

    final class KeyIterator extends MapMakerInternalMap<K, V>.HashIterator<K> {

        KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }

    abstract class HashIterator<E> implements Iterator<E> {

        int nextSegmentIndex;
        int nextTableIndex;
        MapMakerInternalMap.Segment<K, V> currentSegment;
        AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> currentTable;
        MapMakerInternalMap.ReferenceEntry<K, V> nextEntry;
        MapMakerInternalMap<K, V>.WriteThroughEntry nextExternal;
        MapMakerInternalMap<K, V>.WriteThroughEntry lastReturned;

        HashIterator() {
            this.nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }

        public abstract E next();

        final void advance() {
            this.nextExternal = null;
            if (!this.nextInChain()) {
                if (!this.nextInTable()) {
                    while (this.nextSegmentIndex >= 0) {
                        this.currentSegment = MapMakerInternalMap.this.segments[this.nextSegmentIndex--];
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
                    if ((this.nextEntry = (MapMakerInternalMap.ReferenceEntry) this.currentTable.get(this.nextTableIndex--)) == null || !this.advanceTo(this.nextEntry) && !this.nextInChain()) {
                        continue;
                    }

                    return true;
                }

                return false;
            }
        }

        boolean advanceTo(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            boolean flag;

            try {
                Object object = mapmakerinternalmap_referenceentry.getKey();
                Object object1 = MapMakerInternalMap.this.getLiveValue(mapmakerinternalmap_referenceentry);

                if (object1 == null) {
                    flag = false;
                    return flag;
                }

                this.nextExternal = MapMakerInternalMap.this.new WriteThroughEntry(object, object1);
                flag = true;
            } finally {
                this.currentSegment.postReadCleanup();
            }

            return flag;
        }

        public boolean hasNext() {
            return this.nextExternal != null;
        }

        MapMakerInternalMap<K, V>.WriteThroughEntry nextEntry() {
            if (this.nextExternal == null) {
                throw new NoSuchElementException();
            } else {
                this.lastReturned = this.nextExternal;
                this.advance();
                return this.lastReturned;
            }
        }

        public void remove() {
            CollectPreconditions.checkRemove(this.lastReturned != null);
            MapMakerInternalMap.this.remove(this.lastReturned.getKey());
            this.lastReturned = null;
        }
    }

    static final class CleanupMapTask implements Runnable {

        final WeakReference<MapMakerInternalMap<?, ?>> mapReference;

        public CleanupMapTask(MapMakerInternalMap<?, ?> mapmakerinternalmap) {
            this.mapReference = new WeakReference(mapmakerinternalmap);
        }

        public void run() {
            MapMakerInternalMap mapmakerinternalmap = (MapMakerInternalMap) this.mapReference.get();

            if (mapmakerinternalmap == null) {
                throw new CancellationException();
            } else {
                MapMakerInternalMap.Segment[] amapmakerinternalmap_segment = mapmakerinternalmap.segments;
                int i = amapmakerinternalmap_segment.length;

                for (int j = 0; j < i; ++j) {
                    MapMakerInternalMap.Segment mapmakerinternalmap_segment = amapmakerinternalmap_segment[j];

                    mapmakerinternalmap_segment.runCleanup();
                }

            }
        }
    }

    static final class ExpirationQueue<K, V> extends AbstractQueue<MapMakerInternalMap.ReferenceEntry<K, V>> {

        final MapMakerInternalMap.ReferenceEntry<K, V> head = new MapMakerInternalMap.AbstractReferenceEntry<K, V>() {
            MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = this;
            MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = this;

            @Override
            public long getExpirationTime() {
                return Long.MAX_VALUE;
            }

            @Override
            public void setExpirationTime(long i) {}

            @Override
            public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
                return this.nextExpirable;
            }

            @Override
            public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                this.nextExpirable = mapmakerinternalmap_referenceentry;
            }

            @Override
            public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
                return this.previousExpirable;
            }

            @Override
            public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                this.previousExpirable = mapmakerinternalmap_referenceentry;
            }
        };

        ExpirationQueue() {}

        public boolean offer(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            MapMakerInternalMap.connectExpirables(mapmakerinternalmap_referenceentry.getPreviousExpirable(), mapmakerinternalmap_referenceentry.getNextExpirable());
            MapMakerInternalMap.connectExpirables(this.head.getPreviousExpirable(), mapmakerinternalmap_referenceentry);
            MapMakerInternalMap.connectExpirables(mapmakerinternalmap_referenceentry, this.head);
            return true;
        }

        public MapMakerInternalMap.ReferenceEntry<K, V> peek() {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.head.getNextExpirable();

            return mapmakerinternalmap_referenceentry == this.head ? null : mapmakerinternalmap_referenceentry;
        }

        public MapMakerInternalMap.ReferenceEntry<K, V> poll() {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.head.getNextExpirable();

            if (mapmakerinternalmap_referenceentry == this.head) {
                return null;
            } else {
                this.remove(mapmakerinternalmap_referenceentry);
                return mapmakerinternalmap_referenceentry;
            }
        }

        public boolean remove(Object object) {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) object;
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry.getPreviousExpirable();
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry.getNextExpirable();

            MapMakerInternalMap.connectExpirables(mapmakerinternalmap_referenceentry1, mapmakerinternalmap_referenceentry2);
            MapMakerInternalMap.nullifyExpirable(mapmakerinternalmap_referenceentry);
            return mapmakerinternalmap_referenceentry2 != MapMakerInternalMap.NullEntry.INSTANCE;
        }

        public boolean contains(Object object) {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) object;

            return mapmakerinternalmap_referenceentry.getNextExpirable() != MapMakerInternalMap.NullEntry.INSTANCE;
        }

        public boolean isEmpty() {
            return this.head.getNextExpirable() == this.head;
        }

        public int size() {
            int i = 0;

            for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.head.getNextExpirable(); mapmakerinternalmap_referenceentry != this.head; mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry.getNextExpirable()) {
                ++i;
            }

            return i;
        }

        public void clear() {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry;

            for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = this.head.getNextExpirable(); mapmakerinternalmap_referenceentry1 != this.head; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry) {
                mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry1.getNextExpirable();
                MapMakerInternalMap.nullifyExpirable(mapmakerinternalmap_referenceentry1);
            }

            this.head.setNextExpirable(this.head);
            this.head.setPreviousExpirable(this.head);
        }

        public Iterator<MapMakerInternalMap.ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<MapMakerInternalMap.ReferenceEntry<K, V>>(this.peek()) {
                protected MapMakerInternalMap.ReferenceEntry<K, V> computeNext(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                    MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry.getNextExpirable();

                    return mapmakerinternalmap_referenceentry1 == ExpirationQueue.this.head ? null : mapmakerinternalmap_referenceentry1;
                }
            };
        }
    }

    static final class EvictionQueue<K, V> extends AbstractQueue<MapMakerInternalMap.ReferenceEntry<K, V>> {

        final MapMakerInternalMap.ReferenceEntry<K, V> head = new MapMakerInternalMap.AbstractReferenceEntry<K, V>() {
            MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = this;
            MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = this;

            @Override
            public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
                return this.nextEvictable;
            }

            @Override
            public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                this.nextEvictable = mapmakerinternalmap_referenceentry;
            }

            @Override
            public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
                return this.previousEvictable;
            }

            @Override
            public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                this.previousEvictable = mapmakerinternalmap_referenceentry;
            }
        };

        EvictionQueue() {}

        public boolean offer(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            MapMakerInternalMap.connectEvictables(mapmakerinternalmap_referenceentry.getPreviousEvictable(), mapmakerinternalmap_referenceentry.getNextEvictable());
            MapMakerInternalMap.connectEvictables(this.head.getPreviousEvictable(), mapmakerinternalmap_referenceentry);
            MapMakerInternalMap.connectEvictables(mapmakerinternalmap_referenceentry, this.head);
            return true;
        }

        public MapMakerInternalMap.ReferenceEntry<K, V> peek() {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.head.getNextEvictable();

            return mapmakerinternalmap_referenceentry == this.head ? null : mapmakerinternalmap_referenceentry;
        }

        public MapMakerInternalMap.ReferenceEntry<K, V> poll() {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.head.getNextEvictable();

            if (mapmakerinternalmap_referenceentry == this.head) {
                return null;
            } else {
                this.remove(mapmakerinternalmap_referenceentry);
                return mapmakerinternalmap_referenceentry;
            }
        }

        public boolean remove(Object object) {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) object;
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry.getPreviousEvictable();
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry.getNextEvictable();

            MapMakerInternalMap.connectEvictables(mapmakerinternalmap_referenceentry1, mapmakerinternalmap_referenceentry2);
            MapMakerInternalMap.nullifyEvictable(mapmakerinternalmap_referenceentry);
            return mapmakerinternalmap_referenceentry2 != MapMakerInternalMap.NullEntry.INSTANCE;
        }

        public boolean contains(Object object) {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) object;

            return mapmakerinternalmap_referenceentry.getNextEvictable() != MapMakerInternalMap.NullEntry.INSTANCE;
        }

        public boolean isEmpty() {
            return this.head.getNextEvictable() == this.head;
        }

        public int size() {
            int i = 0;

            for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.head.getNextEvictable(); mapmakerinternalmap_referenceentry != this.head; mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry.getNextEvictable()) {
                ++i;
            }

            return i;
        }

        public void clear() {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry;

            for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = this.head.getNextEvictable(); mapmakerinternalmap_referenceentry1 != this.head; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry) {
                mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry1.getNextEvictable();
                MapMakerInternalMap.nullifyEvictable(mapmakerinternalmap_referenceentry1);
            }

            this.head.setNextEvictable(this.head);
            this.head.setPreviousEvictable(this.head);
        }

        public Iterator<MapMakerInternalMap.ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<MapMakerInternalMap.ReferenceEntry<K, V>>(this.peek()) {
                protected MapMakerInternalMap.ReferenceEntry<K, V> computeNext(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                    MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry.getNextEvictable();

                    return mapmakerinternalmap_referenceentry1 == EvictionQueue.this.head ? null : mapmakerinternalmap_referenceentry1;
                }
            };
        }
    }

    static class Segment<K, V> extends ReentrantLock {

        final MapMakerInternalMap<K, V> map;
        volatile int count;
        int modCount;
        int threshold;
        volatile AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table;
        final int maxSegmentSize;
        final ReferenceQueue<K> keyReferenceQueue;
        final ReferenceQueue<V> valueReferenceQueue;
        final Queue<MapMakerInternalMap.ReferenceEntry<K, V>> recencyQueue;
        final AtomicInteger readCount = new AtomicInteger();
        @GuardedBy("Segment.this")
        final Queue<MapMakerInternalMap.ReferenceEntry<K, V>> evictionQueue;
        @GuardedBy("Segment.this")
        final Queue<MapMakerInternalMap.ReferenceEntry<K, V>> expirationQueue;

        Segment(MapMakerInternalMap<K, V> mapmakerinternalmap, int i, int j) {
            this.map = mapmakerinternalmap;
            this.maxSegmentSize = j;
            this.initTable(this.newEntryArray(i));
            this.keyReferenceQueue = mapmakerinternalmap.usesKeyReferences() ? new ReferenceQueue() : null;
            this.valueReferenceQueue = mapmakerinternalmap.usesValueReferences() ? new ReferenceQueue() : null;
            this.recencyQueue = (Queue) (!mapmakerinternalmap.evictsBySize() && !mapmakerinternalmap.expiresAfterAccess() ? MapMakerInternalMap.discardingQueue() : new ConcurrentLinkedQueue());
            this.evictionQueue = (Queue) (mapmakerinternalmap.evictsBySize() ? new MapMakerInternalMap.EvictionQueue<>() : MapMakerInternalMap.discardingQueue());
            this.expirationQueue = (Queue) (mapmakerinternalmap.expires() ? new MapMakerInternalMap.ExpirationQueue<>() : MapMakerInternalMap.discardingQueue());
        }

        AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> newEntryArray(int i) {
            return new AtomicReferenceArray(i);
        }

        void initTable(AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> atomicreferencearray) {
            this.threshold = atomicreferencearray.length() * 3 / 4;
            if (this.threshold == this.maxSegmentSize) {
                ++this.threshold;
            }

            this.table = atomicreferencearray;
        }

        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> newEntry(K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            return this.map.entryFactory.newEntry(this, k0, i, mapmakerinternalmap_referenceentry);
        }

        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
            if (mapmakerinternalmap_referenceentry.getKey() == null) {
                return null;
            } else {
                MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = mapmakerinternalmap_referenceentry.getValueReference();
                Object object = mapmakerinternalmap_valuereference.get();

                if (object == null && !mapmakerinternalmap_valuereference.isComputingReference()) {
                    return null;
                } else {
                    MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = this.map.entryFactory.copyEntry(this, mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                    mapmakerinternalmap_referenceentry2.setValueReference(mapmakerinternalmap_valuereference.copyFor(this.valueReferenceQueue, object, mapmakerinternalmap_referenceentry2));
                    return mapmakerinternalmap_referenceentry2;
                }
            }
        }

        @GuardedBy("Segment.this")
        void setValue(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, V v0) {
            MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = this.map.valueStrength.referenceValue(this, mapmakerinternalmap_referenceentry, v0);

            mapmakerinternalmap_referenceentry.setValueReference(mapmakerinternalmap_valuereference);
            this.recordWrite(mapmakerinternalmap_referenceentry);
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
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) reference;

                this.map.reclaimKey(mapmakerinternalmap_referenceentry);
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
                MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = (MapMakerInternalMap.ValueReference) reference;

                this.map.reclaimValue(mapmakerinternalmap_valuereference);
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

        void recordRead(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            if (this.map.expiresAfterAccess()) {
                this.recordExpirationTime(mapmakerinternalmap_referenceentry, this.map.expireAfterAccessNanos);
            }

            this.recencyQueue.add(mapmakerinternalmap_referenceentry);
        }

        @GuardedBy("Segment.this")
        void recordLockedRead(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.evictionQueue.add(mapmakerinternalmap_referenceentry);
            if (this.map.expiresAfterAccess()) {
                this.recordExpirationTime(mapmakerinternalmap_referenceentry, this.map.expireAfterAccessNanos);
                this.expirationQueue.add(mapmakerinternalmap_referenceentry);
            }

        }

        @GuardedBy("Segment.this")
        void recordWrite(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.drainRecencyQueue();
            this.evictionQueue.add(mapmakerinternalmap_referenceentry);
            if (this.map.expires()) {
                long i = this.map.expiresAfterAccess() ? this.map.expireAfterAccessNanos : this.map.expireAfterWriteNanos;

                this.recordExpirationTime(mapmakerinternalmap_referenceentry, i);
                this.expirationQueue.add(mapmakerinternalmap_referenceentry);
            }

        }

        @GuardedBy("Segment.this")
        void drainRecencyQueue() {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry;

            while ((mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) this.recencyQueue.poll()) != null) {
                if (this.evictionQueue.contains(mapmakerinternalmap_referenceentry)) {
                    this.evictionQueue.add(mapmakerinternalmap_referenceentry);
                }

                if (this.map.expiresAfterAccess() && this.expirationQueue.contains(mapmakerinternalmap_referenceentry)) {
                    this.expirationQueue.add(mapmakerinternalmap_referenceentry);
                }
            }

        }

        void recordExpirationTime(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, long i) {
            mapmakerinternalmap_referenceentry.setExpirationTime(this.map.ticker.read() + i);
        }

        void tryExpireEntries() {
            if (this.tryLock()) {
                try {
                    this.expireEntries();
                } finally {
                    this.unlock();
                }
            }

        }

        @GuardedBy("Segment.this")
        void expireEntries() {
            this.drainRecencyQueue();
            if (!this.expirationQueue.isEmpty()) {
                long i = this.map.ticker.read();

                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry;

                while ((mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) this.expirationQueue.peek()) != null && this.map.isExpired(mapmakerinternalmap_referenceentry, i)) {
                    if (!this.removeEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry.getHash(), MapMaker.RemovalCause.EXPIRED)) {
                        throw new AssertionError();
                    }
                }

            }
        }

        void enqueueNotification(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMaker.RemovalCause mapmaker_removalcause) {
            this.enqueueNotification(mapmakerinternalmap_referenceentry.getKey(), mapmakerinternalmap_referenceentry.getHash(), mapmakerinternalmap_referenceentry.getValueReference().get(), mapmaker_removalcause);
        }

        void enqueueNotification(@Nullable K k0, int i, @Nullable V v0, MapMaker.RemovalCause mapmaker_removalcause) {
            if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
                MapMaker.RemovalNotification mapmaker_removalnotification = new MapMaker.RemovalNotification<>(k0, v0, mapmaker_removalcause);

                this.map.removalNotificationQueue.offer(mapmaker_removalnotification);
            }

        }

        @GuardedBy("Segment.this")
        boolean evictEntries() {
            if (this.map.evictsBySize() && this.count >= this.maxSegmentSize) {
                this.drainRecencyQueue();
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) this.evictionQueue.remove();

                if (!this.removeEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry.getHash(), MapMaker.RemovalCause.SIZE)) {
                    throw new AssertionError();
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        MapMakerInternalMap.ReferenceEntry<K, V> getFirst(int i) {
            AtomicReferenceArray atomicreferencearray = this.table;

            return (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(i & atomicreferencearray.length() - 1);
        }

        MapMakerInternalMap.ReferenceEntry<K, V> getEntry(Object object, int i) {
            if (this.count != 0) {
                for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.getFirst(i); mapmakerinternalmap_referenceentry != null; mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry.getNext()) {
                    if (mapmakerinternalmap_referenceentry.getHash() == i) {
                        Object object1 = mapmakerinternalmap_referenceentry.getKey();

                        if (object1 == null) {
                            this.tryDrainReferenceQueues();
                        } else if (this.map.keyEquivalence.equivalent(object, object1)) {
                            return mapmakerinternalmap_referenceentry;
                        }
                    }
                }
            }

            return null;
        }

        MapMakerInternalMap.ReferenceEntry<K, V> getLiveEntry(Object object, int i) {
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.getEntry(object, i);

            if (mapmakerinternalmap_referenceentry == null) {
                return null;
            } else if (this.map.expires() && this.map.isExpired(mapmakerinternalmap_referenceentry)) {
                this.tryExpireEntries();
                return null;
            } else {
                return mapmakerinternalmap_referenceentry;
            }
        }

        V get(Object object, int i) {
            Object object1;

            try {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.getLiveEntry(object, i);

                if (mapmakerinternalmap_referenceentry != null) {
                    object1 = mapmakerinternalmap_referenceentry.getValueReference().get();
                    if (object1 != null) {
                        this.recordRead(mapmakerinternalmap_referenceentry);
                    } else {
                        this.tryDrainReferenceQueues();
                    }

                    Object object2 = object1;

                    return object2;
                }

                object1 = null;
            } finally {
                this.postReadCleanup();
            }

            return object1;
        }

        boolean containsKey(Object object, int i) {
            boolean flag;

            try {
                if (this.count == 0) {
                    boolean flag1 = false;

                    return flag1;
                }

                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = this.getLiveEntry(object, i);

                if (mapmakerinternalmap_referenceentry != null) {
                    flag = mapmakerinternalmap_referenceentry.getValueReference().get() != null;
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
                    AtomicReferenceArray atomicreferencearray = this.table;
                    int i = atomicreferencearray.length();

                    for (int j = 0; j < i; ++j) {
                        for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(j); mapmakerinternalmap_referenceentry != null; mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry.getNext()) {
                            Object object1 = this.getLiveValue(mapmakerinternalmap_referenceentry);

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

        V put(K k0, int i, V v0, boolean flag) {
            this.lock();

            try {
                this.preWriteCleanup();
                int j = this.count + 1;

                if (j > this.threshold) {
                    this.expand();
                    j = this.count + 1;
                }

                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k);

                Object object;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1;

                for (mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry1 != null; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry1.getNext()) {
                    object = mapmakerinternalmap_referenceentry1.getKey();
                    if (mapmakerinternalmap_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = mapmakerinternalmap_referenceentry1.getValueReference();
                        Object object1 = mapmakerinternalmap_valuereference.get();
                        Object object2;

                        if (object1 == null) {
                            ++this.modCount;
                            this.setValue(mapmakerinternalmap_referenceentry1, v0);
                            if (!mapmakerinternalmap_valuereference.isComputingReference()) {
                                this.enqueueNotification(k0, i, object1, MapMaker.RemovalCause.COLLECTED);
                                j = this.count;
                            } else if (this.evictEntries()) {
                                j = this.count + 1;
                            }

                            this.count = j;
                            object2 = null;
                            return object2;
                        }

                        if (!flag) {
                            ++this.modCount;
                            this.enqueueNotification(k0, i, object1, MapMaker.RemovalCause.REPLACED);
                            this.setValue(mapmakerinternalmap_referenceentry1, v0);
                            object2 = object1;
                            return object2;
                        }

                        this.recordLockedRead(mapmakerinternalmap_referenceentry1);
                        object2 = object1;
                        return object2;
                    }
                }

                ++this.modCount;
                mapmakerinternalmap_referenceentry1 = this.newEntry(k0, i, mapmakerinternalmap_referenceentry);
                this.setValue(mapmakerinternalmap_referenceentry1, v0);
                atomicreferencearray.set(k, mapmakerinternalmap_referenceentry1);
                if (this.evictEntries()) {
                    j = this.count + 1;
                }

                this.count = j;
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
                    MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(l);

                    if (mapmakerinternalmap_referenceentry != null) {
                        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry.getNext();
                        int i1 = mapmakerinternalmap_referenceentry.getHash() & k;

                        if (mapmakerinternalmap_referenceentry1 == null) {
                            atomicreferencearray1.set(i1, mapmakerinternalmap_referenceentry);
                        } else {
                            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry;
                            int j1 = i1;

                            int k1;
                            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry3;

                            for (mapmakerinternalmap_referenceentry3 = mapmakerinternalmap_referenceentry1; mapmakerinternalmap_referenceentry3 != null; mapmakerinternalmap_referenceentry3 = mapmakerinternalmap_referenceentry3.getNext()) {
                                k1 = mapmakerinternalmap_referenceentry3.getHash() & k;
                                if (k1 != j1) {
                                    j1 = k1;
                                    mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry3;
                                }
                            }

                            atomicreferencearray1.set(j1, mapmakerinternalmap_referenceentry2);

                            for (mapmakerinternalmap_referenceentry3 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry3 != mapmakerinternalmap_referenceentry2; mapmakerinternalmap_referenceentry3 = mapmakerinternalmap_referenceentry3.getNext()) {
                                k1 = mapmakerinternalmap_referenceentry3.getHash() & k;
                                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry4 = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray1.get(k1);
                                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry5 = this.copyEntry(mapmakerinternalmap_referenceentry3, mapmakerinternalmap_referenceentry4);

                                if (mapmakerinternalmap_referenceentry5 != null) {
                                    atomicreferencearray1.set(k1, mapmakerinternalmap_referenceentry5);
                                } else {
                                    this.removeCollectedEntry(mapmakerinternalmap_referenceentry3);
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
                this.preWriteCleanup();
                AtomicReferenceArray atomicreferencearray = this.table;
                int j = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(j);

                for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry1 != null; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry1.getNext()) {
                    Object object = mapmakerinternalmap_referenceentry1.getKey();

                    if (mapmakerinternalmap_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = mapmakerinternalmap_referenceentry1.getValueReference();
                        Object object1 = mapmakerinternalmap_valuereference.get();
                        boolean flag;

                        if (object1 == null) {
                            if (this.isCollected(mapmakerinternalmap_valuereference)) {
                                int k = this.count - 1;

                                ++this.modCount;
                                this.enqueueNotification(object, i, object1, MapMaker.RemovalCause.COLLECTED);
                                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = this.removeFromChain(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                                k = this.count - 1;
                                atomicreferencearray.set(j, mapmakerinternalmap_referenceentry2);
                                this.count = k;
                            }

                            flag = false;
                            return flag;
                        }

                        if (!this.map.valueEquivalence.equivalent(v0, object1)) {
                            this.recordLockedRead(mapmakerinternalmap_referenceentry1);
                            flag = false;
                            return flag;
                        }

                        ++this.modCount;
                        this.enqueueNotification(k0, i, object1, MapMaker.RemovalCause.REPLACED);
                        this.setValue(mapmakerinternalmap_referenceentry1, v1);
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

        V replace(K k0, int i, V v0) {
            this.lock();

            try {
                this.preWriteCleanup();
                AtomicReferenceArray atomicreferencearray = this.table;
                int j = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(j);

                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1;

                for (mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry1 != null; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry1.getNext()) {
                    Object object = mapmakerinternalmap_referenceentry1.getKey();

                    if (mapmakerinternalmap_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = mapmakerinternalmap_referenceentry1.getValueReference();
                        Object object1 = mapmakerinternalmap_valuereference.get();
                        Object object2;

                        if (object1 != null) {
                            ++this.modCount;
                            this.enqueueNotification(k0, i, object1, MapMaker.RemovalCause.REPLACED);
                            this.setValue(mapmakerinternalmap_referenceentry1, v0);
                            object2 = object1;
                            return object2;
                        }

                        if (this.isCollected(mapmakerinternalmap_valuereference)) {
                            int k = this.count - 1;

                            ++this.modCount;
                            this.enqueueNotification(object, i, object1, MapMaker.RemovalCause.COLLECTED);
                            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = this.removeFromChain(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                            k = this.count - 1;
                            atomicreferencearray.set(j, mapmakerinternalmap_referenceentry2);
                            this.count = k;
                        }

                        object2 = null;
                        return object2;
                    }
                }

                mapmakerinternalmap_referenceentry1 = null;
                return mapmakerinternalmap_referenceentry1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        V remove(Object object, int i) {
            this.lock();

            try {
                this.preWriteCleanup();
                int j = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k);

                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1;

                for (mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry1 != null; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry1.getNext()) {
                    Object object1 = mapmakerinternalmap_referenceentry1.getKey();

                    if (mapmakerinternalmap_referenceentry1.getHash() == i && object1 != null && this.map.keyEquivalence.equivalent(object, object1)) {
                        MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = mapmakerinternalmap_referenceentry1.getValueReference();
                        Object object2 = mapmakerinternalmap_valuereference.get();
                        MapMaker.RemovalCause mapmaker_removalcause;
                        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2;

                        if (object2 != null) {
                            mapmaker_removalcause = MapMaker.RemovalCause.EXPLICIT;
                        } else {
                            if (!this.isCollected(mapmakerinternalmap_valuereference)) {
                                mapmakerinternalmap_referenceentry2 = null;
                                return mapmakerinternalmap_referenceentry2;
                            }

                            mapmaker_removalcause = MapMaker.RemovalCause.COLLECTED;
                        }

                        ++this.modCount;
                        this.enqueueNotification(object1, i, object2, mapmaker_removalcause);
                        mapmakerinternalmap_referenceentry2 = this.removeFromChain(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);
                        j = this.count - 1;
                        atomicreferencearray.set(k, mapmakerinternalmap_referenceentry2);
                        this.count = j;
                        Object object3 = object2;

                        return object3;
                    }
                }

                mapmakerinternalmap_referenceentry1 = null;
                return mapmakerinternalmap_referenceentry1;
            } finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }

        boolean remove(Object object, int i, Object object1) {
            this.lock();

            try {
                this.preWriteCleanup();
                int j = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k);

                for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry1 != null; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry1.getNext()) {
                    Object object2 = mapmakerinternalmap_referenceentry1.getKey();

                    if (mapmakerinternalmap_referenceentry1.getHash() == i && object2 != null && this.map.keyEquivalence.equivalent(object, object2)) {
                        MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference = mapmakerinternalmap_referenceentry1.getValueReference();
                        Object object3 = mapmakerinternalmap_valuereference.get();
                        MapMaker.RemovalCause mapmaker_removalcause;

                        if (this.map.valueEquivalence.equivalent(object1, object3)) {
                            mapmaker_removalcause = MapMaker.RemovalCause.EXPLICIT;
                        } else {
                            if (!this.isCollected(mapmakerinternalmap_valuereference)) {
                                boolean flag = false;

                                return flag;
                            }

                            mapmaker_removalcause = MapMaker.RemovalCause.COLLECTED;
                        }

                        ++this.modCount;
                        this.enqueueNotification(object2, i, object3, mapmaker_removalcause);
                        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = this.removeFromChain(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                        j = this.count - 1;
                        atomicreferencearray.set(k, mapmakerinternalmap_referenceentry2);
                        this.count = j;
                        boolean flag1 = mapmaker_removalcause == MapMaker.RemovalCause.EXPLICIT;

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

                    if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
                        for (i = 0; i < atomicreferencearray.length(); ++i) {
                            for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(i); mapmakerinternalmap_referenceentry != null; mapmakerinternalmap_referenceentry = mapmakerinternalmap_referenceentry.getNext()) {
                                if (!mapmakerinternalmap_referenceentry.getValueReference().isComputingReference()) {
                                    this.enqueueNotification(mapmakerinternalmap_referenceentry, MapMaker.RemovalCause.EXPLICIT);
                                }
                            }
                        }
                    }

                    for (i = 0; i < atomicreferencearray.length(); ++i) {
                        atomicreferencearray.set(i, (Object) null);
                    }

                    this.clearReferenceQueues();
                    this.evictionQueue.clear();
                    this.expirationQueue.clear();
                    this.readCount.set(0);
                    ++this.modCount;
                    this.count = 0;
                } finally {
                    this.unlock();
                    this.postWriteCleanup();
                }
            }

        }

        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> removeFromChain(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
            this.evictionQueue.remove(mapmakerinternalmap_referenceentry1);
            this.expirationQueue.remove(mapmakerinternalmap_referenceentry1);
            int i = this.count;
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry1.getNext();

            for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry3 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry3 != mapmakerinternalmap_referenceentry1; mapmakerinternalmap_referenceentry3 = mapmakerinternalmap_referenceentry3.getNext()) {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry4 = this.copyEntry(mapmakerinternalmap_referenceentry3, mapmakerinternalmap_referenceentry2);

                if (mapmakerinternalmap_referenceentry4 != null) {
                    mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry4;
                } else {
                    this.removeCollectedEntry(mapmakerinternalmap_referenceentry3);
                    --i;
                }
            }

            this.count = i;
            return mapmakerinternalmap_referenceentry2;
        }

        void removeCollectedEntry(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.enqueueNotification(mapmakerinternalmap_referenceentry, MapMaker.RemovalCause.COLLECTED);
            this.evictionQueue.remove(mapmakerinternalmap_referenceentry);
            this.expirationQueue.remove(mapmakerinternalmap_referenceentry);
        }

        boolean reclaimKey(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, int i) {
            this.lock();

            try {
                int j = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k);

                for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry1; mapmakerinternalmap_referenceentry2 != null; mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry2.getNext()) {
                    if (mapmakerinternalmap_referenceentry2 == mapmakerinternalmap_referenceentry) {
                        ++this.modCount;
                        this.enqueueNotification(mapmakerinternalmap_referenceentry2.getKey(), i, mapmakerinternalmap_referenceentry2.getValueReference().get(), MapMaker.RemovalCause.COLLECTED);
                        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry3 = this.removeFromChain(mapmakerinternalmap_referenceentry1, mapmakerinternalmap_referenceentry2);

                        j = this.count - 1;
                        atomicreferencearray.set(k, mapmakerinternalmap_referenceentry3);
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

        boolean reclaimValue(K k0, int i, MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            this.lock();

            try {
                int j = this.count - 1;
                AtomicReferenceArray atomicreferencearray = this.table;
                int k = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k);

                for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry1 != null; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry1.getNext()) {
                    Object object = mapmakerinternalmap_referenceentry1.getKey();

                    if (mapmakerinternalmap_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference1 = mapmakerinternalmap_referenceentry1.getValueReference();

                        if (mapmakerinternalmap_valuereference1 != mapmakerinternalmap_valuereference) {
                            boolean flag = false;

                            return flag;
                        }

                        ++this.modCount;
                        this.enqueueNotification(k0, i, mapmakerinternalmap_valuereference.get(), MapMaker.RemovalCause.COLLECTED);
                        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = this.removeFromChain(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                        j = this.count - 1;
                        atomicreferencearray.set(k, mapmakerinternalmap_referenceentry2);
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

        boolean clearValue(K k0, int i, MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            this.lock();

            try {
                AtomicReferenceArray atomicreferencearray = this.table;
                int j = i & atomicreferencearray.length() - 1;
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(j);

                for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry; mapmakerinternalmap_referenceentry1 != null; mapmakerinternalmap_referenceentry1 = mapmakerinternalmap_referenceentry1.getNext()) {
                    Object object = mapmakerinternalmap_referenceentry1.getKey();

                    if (mapmakerinternalmap_referenceentry1.getHash() == i && object != null && this.map.keyEquivalence.equivalent(k0, object)) {
                        MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference1 = mapmakerinternalmap_referenceentry1.getValueReference();

                        if (mapmakerinternalmap_valuereference1 != mapmakerinternalmap_valuereference) {
                            boolean flag = false;

                            return flag;
                        }

                        MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = this.removeFromChain(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                        atomicreferencearray.set(j, mapmakerinternalmap_referenceentry2);
                        boolean flag1 = true;

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

        @GuardedBy("Segment.this")
        boolean removeEntry(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, int i, MapMaker.RemovalCause mapmaker_removalcause) {
            int j = this.count - 1;
            AtomicReferenceArray atomicreferencearray = this.table;
            int k = i & atomicreferencearray.length() - 1;
            MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry1 = (MapMakerInternalMap.ReferenceEntry) atomicreferencearray.get(k);

            for (MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry1; mapmakerinternalmap_referenceentry2 != null; mapmakerinternalmap_referenceentry2 = mapmakerinternalmap_referenceentry2.getNext()) {
                if (mapmakerinternalmap_referenceentry2 == mapmakerinternalmap_referenceentry) {
                    ++this.modCount;
                    this.enqueueNotification(mapmakerinternalmap_referenceentry2.getKey(), i, mapmakerinternalmap_referenceentry2.getValueReference().get(), mapmaker_removalcause);
                    MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry3 = this.removeFromChain(mapmakerinternalmap_referenceentry1, mapmakerinternalmap_referenceentry2);

                    j = this.count - 1;
                    atomicreferencearray.set(k, mapmakerinternalmap_referenceentry3);
                    this.count = j;
                    return true;
                }
            }

            return false;
        }

        boolean isCollected(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            return mapmakerinternalmap_valuereference.isComputingReference() ? false : mapmakerinternalmap_valuereference.get() == null;
        }

        V getLiveValue(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            if (mapmakerinternalmap_referenceentry.getKey() == null) {
                this.tryDrainReferenceQueues();
                return null;
            } else {
                Object object = mapmakerinternalmap_referenceentry.getValueReference().get();

                if (object == null) {
                    this.tryDrainReferenceQueues();
                    return null;
                } else if (this.map.expires() && this.map.isExpired(mapmakerinternalmap_referenceentry)) {
                    this.tryExpireEntries();
                    return null;
                } else {
                    return object;
                }
            }
        }

        void postReadCleanup() {
            if ((this.readCount.incrementAndGet() & 63) == 0) {
                this.runCleanup();
            }

        }

        @GuardedBy("Segment.this")
        void preWriteCleanup() {
            this.runLockedCleanup();
        }

        void postWriteCleanup() {
            this.runUnlockedCleanup();
        }

        void runCleanup() {
            this.runLockedCleanup();
            this.runUnlockedCleanup();
        }

        void runLockedCleanup() {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                    this.expireEntries();
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

    static final class StrongValueReference<K, V> implements MapMakerInternalMap.ValueReference<K, V> {

        final V referent;

        StrongValueReference(V v0) {
            this.referent = v0;
        }

        @Override
        public V get() {
            return this.referent;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            return this;
        }

        @Override
        public boolean isComputingReference() {
            return false;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }

        @Override
        public void clear(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {}
    }

    static final class SoftValueReference<K, V> extends SoftReference<V> implements MapMakerInternalMap.ValueReference<K, V> {

        final MapMakerInternalMap.ReferenceEntry<K, V> entry;

        SoftValueReference(ReferenceQueue<V> referencequeue, V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(v0, referencequeue);
            this.entry = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }

        @Override
        public void clear(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            this.clear();
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            return new MapMakerInternalMap.SoftValueReference<>(referencequeue, v0, mapmakerinternalmap_referenceentry);
        }

        @Override
        public boolean isComputingReference() {
            return false;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }
    }

    static final class WeakValueReference<K, V> extends WeakReference<V> implements MapMakerInternalMap.ValueReference<K, V> {

        final MapMakerInternalMap.ReferenceEntry<K, V> entry;

        WeakValueReference(ReferenceQueue<V> referencequeue, V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(v0, referencequeue);
            this.entry = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }

        @Override
        public void clear(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            this.clear();
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            return new MapMakerInternalMap.WeakValueReference<>(referencequeue, v0, mapmakerinternalmap_referenceentry);
        }

        @Override
        public boolean isComputingReference() {
            return false;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }
    }

    static final class WeakExpirableEvictableEntry<K, V> extends MapMakerInternalMap.WeakEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        volatile long time = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();

        WeakExpirableEvictableEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(referencequeue, k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public long getExpirationTime() {
            return this.time;
        }

        @Override
        public void setExpirationTime(long i) {
            this.time = i;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextEvictable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousEvictable = mapmakerinternalmap_referenceentry;
        }
    }

    static final class WeakEvictableEntry<K, V> extends MapMakerInternalMap.WeakEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();

        WeakEvictableEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(referencequeue, k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextEvictable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousEvictable = mapmakerinternalmap_referenceentry;
        }
    }

    static final class WeakExpirableEntry<K, V> extends MapMakerInternalMap.WeakEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        volatile long time = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();

        WeakExpirableEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(referencequeue, k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public long getExpirationTime() {
            return this.time;
        }

        @Override
        public void setExpirationTime(long i) {
            this.time = i;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousExpirable = mapmakerinternalmap_referenceentry;
        }
    }

    static class WeakEntry<K, V> extends WeakReference<K> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        final int hash;
        final MapMakerInternalMap.ReferenceEntry<K, V> next;
        volatile MapMakerInternalMap.ValueReference<K, V> valueReference = MapMakerInternalMap.unset();

        WeakEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(k0, referencequeue);
            this.hash = i;
            this.next = mapmakerinternalmap_referenceentry;
        }

        @Override
        public K getKey() {
            return this.get();
        }

        @Override
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setExpirationTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference1 = this.valueReference;

            this.valueReference = mapmakerinternalmap_valuereference;
            mapmakerinternalmap_valuereference1.clear(mapmakerinternalmap_valuereference);
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    static final class SoftExpirableEvictableEntry<K, V> extends MapMakerInternalMap.SoftEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        volatile long time = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();

        SoftExpirableEvictableEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(referencequeue, k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public long getExpirationTime() {
            return this.time;
        }

        @Override
        public void setExpirationTime(long i) {
            this.time = i;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextEvictable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousEvictable = mapmakerinternalmap_referenceentry;
        }
    }

    static final class SoftEvictableEntry<K, V> extends MapMakerInternalMap.SoftEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();

        SoftEvictableEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(referencequeue, k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextEvictable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousEvictable = mapmakerinternalmap_referenceentry;
        }
    }

    static final class SoftExpirableEntry<K, V> extends MapMakerInternalMap.SoftEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        volatile long time = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();

        SoftExpirableEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(referencequeue, k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public long getExpirationTime() {
            return this.time;
        }

        @Override
        public void setExpirationTime(long i) {
            this.time = i;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousExpirable = mapmakerinternalmap_referenceentry;
        }
    }

    static class SoftEntry<K, V> extends SoftReference<K> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        final int hash;
        final MapMakerInternalMap.ReferenceEntry<K, V> next;
        volatile MapMakerInternalMap.ValueReference<K, V> valueReference = MapMakerInternalMap.unset();

        SoftEntry(ReferenceQueue<K> referencequeue, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(k0, referencequeue);
            this.hash = i;
            this.next = mapmakerinternalmap_referenceentry;
        }

        @Override
        public K getKey() {
            return this.get();
        }

        @Override
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setExpirationTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference1 = this.valueReference;

            this.valueReference = mapmakerinternalmap_valuereference;
            mapmakerinternalmap_valuereference1.clear(mapmakerinternalmap_valuereference);
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    static final class StrongExpirableEvictableEntry<K, V> extends MapMakerInternalMap.StrongEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        volatile long time = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();

        StrongExpirableEvictableEntry(K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public long getExpirationTime() {
            return this.time;
        }

        @Override
        public void setExpirationTime(long i) {
            this.time = i;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextEvictable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousEvictable = mapmakerinternalmap_referenceentry;
        }
    }

    static final class StrongEvictableEntry<K, V> extends MapMakerInternalMap.StrongEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();

        StrongEvictableEntry(K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextEvictable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousEvictable = mapmakerinternalmap_referenceentry;
        }
    }

    static final class StrongExpirableEntry<K, V> extends MapMakerInternalMap.StrongEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        volatile long time = Long.MAX_VALUE;
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
        @GuardedBy("Segment.this")
        MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();

        StrongExpirableEntry(K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            super(k0, i, mapmakerinternalmap_referenceentry);
        }

        @Override
        public long getExpirationTime() {
            return this.time;
        }

        @Override
        public void setExpirationTime(long i) {
            this.time = i;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.nextExpirable = mapmakerinternalmap_referenceentry;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.previousExpirable = mapmakerinternalmap_referenceentry;
        }
    }

    static class StrongEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        final K key;
        final int hash;
        final MapMakerInternalMap.ReferenceEntry<K, V> next;
        volatile MapMakerInternalMap.ValueReference<K, V> valueReference = MapMakerInternalMap.unset();

        StrongEntry(K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            this.key = k0;
            this.hash = i;
            this.next = mapmakerinternalmap_referenceentry;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setExpirationTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            MapMakerInternalMap.ValueReference mapmakerinternalmap_valuereference1 = this.valueReference;

            this.valueReference = mapmakerinternalmap_valuereference;
            mapmakerinternalmap_valuereference1.clear(mapmakerinternalmap_valuereference);
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    abstract static class AbstractReferenceEntry<K, V> implements MapMakerInternalMap.ReferenceEntry<K, V> {

        AbstractReferenceEntry() {}

        @Override
        public MapMakerInternalMap.ValueReference<K, V> getValueReference() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setValueReference(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNext() {
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
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setExpirationTime(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
            throw new UnsupportedOperationException();
        }
    }

    private static enum NullEntry implements MapMakerInternalMap.ReferenceEntry<Object, Object> {

        INSTANCE;

        private NullEntry() {}

        @Override
        public MapMakerInternalMap.ValueReference<Object, Object> getValueReference() {
            return null;
        }

        @Override
        public void setValueReference(MapMakerInternalMap.ValueReference<Object, Object> mapmakerinternalmap_valuereference) {}

        @Override
        public MapMakerInternalMap.ReferenceEntry<Object, Object> getNext() {
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
        public long getExpirationTime() {
            return 0L;
        }

        @Override
        public void setExpirationTime(long i) {}

        @Override
        public MapMakerInternalMap.ReferenceEntry<Object, Object> getNextExpirable() {
            return this;
        }

        @Override
        public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<Object, Object> mapmakerinternalmap_referenceentry) {}

        @Override
        public MapMakerInternalMap.ReferenceEntry<Object, Object> getPreviousExpirable() {
            return this;
        }

        @Override
        public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<Object, Object> mapmakerinternalmap_referenceentry) {}

        @Override
        public MapMakerInternalMap.ReferenceEntry<Object, Object> getNextEvictable() {
            return this;
        }

        @Override
        public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<Object, Object> mapmakerinternalmap_referenceentry) {}

        @Override
        public MapMakerInternalMap.ReferenceEntry<Object, Object> getPreviousEvictable() {
            return this;
        }

        @Override
        public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<Object, Object> mapmakerinternalmap_referenceentry) {}
    }

    interface ReferenceEntry<K, V> {

        MapMakerInternalMap.ValueReference<K, V> getValueReference();

        void setValueReference(MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference);

        MapMakerInternalMap.ReferenceEntry<K, V> getNext();

        int getHash();

        K getKey();

        long getExpirationTime();

        void setExpirationTime(long i);

        MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable();

        void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry);

        MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable();

        void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry);

        MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable();

        void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry);

        MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable();

        void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry);
    }

    interface ValueReference<K, V> {

        V get();

        V waitForValue() throws ExecutionException;

        MapMakerInternalMap.ReferenceEntry<K, V> getEntry();

        MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referencequeue, @Nullable V v0, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry);

        void clear(@Nullable MapMakerInternalMap.ValueReference<K, V> mapmakerinternalmap_valuereference);

        boolean isComputingReference();
    }

    static enum EntryFactory {

        STRONG {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.StrongEntry<>(k0, i, mapmakerinternalmap_referenceentry);
            }
        },
        STRONG_EXPIRABLE {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.StrongExpirableEntry<>(k0, i, mapmakerinternalmap_referenceentry);
            }

            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = super.copyEntry(mapmakerinternalmap_segment, mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                this.copyExpirableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                return mapmakerinternalmap_referenceentry2;
            }
        },
        STRONG_EVICTABLE {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.StrongEvictableEntry<>(k0, i, mapmakerinternalmap_referenceentry);
            }

            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = super.copyEntry(mapmakerinternalmap_segment, mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                this.copyEvictableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                return mapmakerinternalmap_referenceentry2;
            }
        },
        STRONG_EXPIRABLE_EVICTABLE {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.StrongExpirableEvictableEntry<>(k0, i, mapmakerinternalmap_referenceentry);
            }

            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = super.copyEntry(mapmakerinternalmap_segment, mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                this.copyExpirableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                this.copyEvictableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                return mapmakerinternalmap_referenceentry2;
            }
        },
        WEAK {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.WeakEntry<>(mapmakerinternalmap_segment.keyReferenceQueue, k0, i, mapmakerinternalmap_referenceentry);
            }
        },
        WEAK_EXPIRABLE {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.WeakExpirableEntry<>(mapmakerinternalmap_segment.keyReferenceQueue, k0, i, mapmakerinternalmap_referenceentry);
            }

            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = super.copyEntry(mapmakerinternalmap_segment, mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                this.copyExpirableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                return mapmakerinternalmap_referenceentry2;
            }
        },
        WEAK_EVICTABLE {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.WeakEvictableEntry<>(mapmakerinternalmap_segment.keyReferenceQueue, k0, i, mapmakerinternalmap_referenceentry);
            }

            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = super.copyEntry(mapmakerinternalmap_segment, mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                this.copyEvictableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                return mapmakerinternalmap_referenceentry2;
            }
        },
        WEAK_EXPIRABLE_EVICTABLE {
            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry) {
                return new MapMakerInternalMap.WeakExpirableEvictableEntry<>(mapmakerinternalmap_segment.keyReferenceQueue, k0, i, mapmakerinternalmap_referenceentry);
            }

            @Override
            <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
                MapMakerInternalMap.ReferenceEntry mapmakerinternalmap_referenceentry2 = super.copyEntry(mapmakerinternalmap_segment, mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry1);

                this.copyExpirableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                this.copyEvictableEntry(mapmakerinternalmap_referenceentry, mapmakerinternalmap_referenceentry2);
                return mapmakerinternalmap_referenceentry2;
            }
        };

        static final int EXPIRABLE_MASK = 1;
        static final int EVICTABLE_MASK = 2;
        static final MapMakerInternalMap.EntryFactory[][] factories = new MapMakerInternalMap.EntryFactory[][]{{MapMakerInternalMap.EntryFactory.STRONG, MapMakerInternalMap.EntryFactory.STRONG_EXPIRABLE, MapMakerInternalMap.EntryFactory.STRONG_EVICTABLE, MapMakerInternalMap.EntryFactory.STRONG_EXPIRABLE_EVICTABLE}, new MapMakerInternalMap.EntryFactory[0], {MapMakerInternalMap.EntryFactory.WEAK, MapMakerInternalMap.EntryFactory.WEAK_EXPIRABLE, MapMakerInternalMap.EntryFactory.WEAK_EVICTABLE, MapMakerInternalMap.EntryFactory.WEAK_EXPIRABLE_EVICTABLE}};

        private EntryFactory() {}

        static MapMakerInternalMap.EntryFactory getFactory(MapMakerInternalMap.Strength mapmakerinternalmap_strength, boolean flag, boolean flag1) {
            int i = (flag ? 1 : 0) | (flag1 ? 2 : 0);

            return MapMakerInternalMap.EntryFactory.factories[mapmakerinternalmap_strength.ordinal()][i];
        }

        abstract <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, K k0, int i, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry);

        @GuardedBy("Segment.this")
        <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
            return this.newEntry(mapmakerinternalmap_segment, mapmakerinternalmap_referenceentry.getKey(), mapmakerinternalmap_referenceentry.getHash(), mapmakerinternalmap_referenceentry1);
        }

        @GuardedBy("Segment.this")
        <K, V> void copyExpirableEntry(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
            mapmakerinternalmap_referenceentry1.setExpirationTime(mapmakerinternalmap_referenceentry.getExpirationTime());
            MapMakerInternalMap.connectExpirables(mapmakerinternalmap_referenceentry.getPreviousExpirable(), mapmakerinternalmap_referenceentry1);
            MapMakerInternalMap.connectExpirables(mapmakerinternalmap_referenceentry1, mapmakerinternalmap_referenceentry.getNextExpirable());
            MapMakerInternalMap.nullifyExpirable(mapmakerinternalmap_referenceentry);
        }

        @GuardedBy("Segment.this")
        <K, V> void copyEvictableEntry(MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry1) {
            MapMakerInternalMap.connectEvictables(mapmakerinternalmap_referenceentry.getPreviousEvictable(), mapmakerinternalmap_referenceentry1);
            MapMakerInternalMap.connectEvictables(mapmakerinternalmap_referenceentry1, mapmakerinternalmap_referenceentry.getNextEvictable());
            MapMakerInternalMap.nullifyEvictable(mapmakerinternalmap_referenceentry);
        }
    }

    static enum Strength {

        STRONG {
            @Override
            <K, V> MapMakerInternalMap.ValueReference<K, V> referenceValue(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, V v0) {
                return new MapMakerInternalMap.StrongValueReference<>(v0);
            }

            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.equals();
            }
        },
        SOFT {
            @Override
            <K, V> MapMakerInternalMap.ValueReference<K, V> referenceValue(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, V v0) {
                return new MapMakerInternalMap.SoftValueReference<>(mapmakerinternalmap_segment.valueReferenceQueue, v0, mapmakerinternalmap_referenceentry);
            }

            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
        },
        WEAK {
            @Override
            <K, V> MapMakerInternalMap.ValueReference<K, V> referenceValue(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, V v0) {
                return new MapMakerInternalMap.WeakValueReference<>(mapmakerinternalmap_segment.valueReferenceQueue, v0, mapmakerinternalmap_referenceentry);
            }

            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
        };

        private Strength() {}

        abstract <K, V> MapMakerInternalMap.ValueReference<K, V> referenceValue(MapMakerInternalMap.Segment<K, V> mapmakerinternalmap_segment, MapMakerInternalMap.ReferenceEntry<K, V> mapmakerinternalmap_referenceentry, V v0);

        abstract Equivalence<Object> defaultEquivalence();
    }
}
