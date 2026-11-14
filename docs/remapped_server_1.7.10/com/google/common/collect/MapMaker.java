package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.base.Ticker;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class MapMaker extends GenericMapMaker<Object, Object> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
    private static final int DEFAULT_EXPIRATION_NANOS = 0;
    static final int UNSET_INT = -1;
    boolean useCustomMap;
    int initialCapacity = -1;
    int concurrencyLevel = -1;
    int maximumSize = -1;
    MapMakerInternalMap.Strength keyStrength;
    MapMakerInternalMap.Strength valueStrength;
    long expireAfterWriteNanos = -1L;
    long expireAfterAccessNanos = -1L;
    MapMaker.RemovalCause nullRemovalCause;
    Equivalence<Object> keyEquivalence;
    Ticker ticker;

    public MapMaker() {}

    @GwtIncompatible("To be supported")
    @Override
    MapMaker keyEquivalence(Equivalence<Object> equivalence) {
        Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", this.keyEquivalence);
        this.keyEquivalence = (Equivalence) Preconditions.checkNotNull(equivalence);
        this.useCustomMap = true;
        return this;
    }

    Equivalence<Object> getKeyEquivalence() {
        return (Equivalence) Objects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
    }

    @Override
    public MapMaker initialCapacity(int i) {
        Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", this.initialCapacity);
        Preconditions.checkArgument(i >= 0);
        this.initialCapacity = i;
        return this;
    }

    int getInitialCapacity() {
        return this.initialCapacity == -1 ? 16 : this.initialCapacity;
    }

    /** @deprecated */
    @Deprecated
    @Override
    MapMaker maximumSize(int i) {
        Preconditions.checkState(this.maximumSize == -1, "maximum size was already set to %s", this.maximumSize);
        Preconditions.checkArgument(i >= 0, "maximum size must not be negative");
        this.maximumSize = i;
        this.useCustomMap = true;
        if (this.maximumSize == 0) {
            this.nullRemovalCause = MapMaker.RemovalCause.SIZE;
        }

        return this;
    }

    @Override
    public MapMaker concurrencyLevel(int i) {
        Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", this.concurrencyLevel);
        Preconditions.checkArgument(i > 0);
        this.concurrencyLevel = i;
        return this;
    }

    int getConcurrencyLevel() {
        return this.concurrencyLevel == -1 ? 4 : this.concurrencyLevel;
    }

    @GwtIncompatible("java.lang.ref.WeakReference")
    @Override
    public MapMaker weakKeys() {
        return this.setKeyStrength(MapMakerInternalMap.Strength.WEAK);
    }

    MapMaker setKeyStrength(MapMakerInternalMap.Strength mapmakerinternalmap_strength) {
        Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", this.keyStrength);
        this.keyStrength = (MapMakerInternalMap.Strength) Preconditions.checkNotNull(mapmakerinternalmap_strength);
        Preconditions.checkArgument(this.keyStrength != MapMakerInternalMap.Strength.SOFT, "Soft keys are not supported");
        if (mapmakerinternalmap_strength != MapMakerInternalMap.Strength.STRONG) {
            this.useCustomMap = true;
        }

        return this;
    }

    MapMakerInternalMap.Strength getKeyStrength() {
        return (MapMakerInternalMap.Strength) Objects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
    }

    @GwtIncompatible("java.lang.ref.WeakReference")
    @Override
    public MapMaker weakValues() {
        return this.setValueStrength(MapMakerInternalMap.Strength.WEAK);
    }

    /** @deprecated */
    @Deprecated
    @GwtIncompatible("java.lang.ref.SoftReference")
    @Override
    public MapMaker softValues() {
        return this.setValueStrength(MapMakerInternalMap.Strength.SOFT);
    }

    MapMaker setValueStrength(MapMakerInternalMap.Strength mapmakerinternalmap_strength) {
        Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", this.valueStrength);
        this.valueStrength = (MapMakerInternalMap.Strength) Preconditions.checkNotNull(mapmakerinternalmap_strength);
        if (mapmakerinternalmap_strength != MapMakerInternalMap.Strength.STRONG) {
            this.useCustomMap = true;
        }

        return this;
    }

    MapMakerInternalMap.Strength getValueStrength() {
        return (MapMakerInternalMap.Strength) Objects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
    }

    /** @deprecated */
    @Deprecated
    @Override
    MapMaker expireAfterWrite(long i, TimeUnit timeunit) {
        this.checkExpiration(i, timeunit);
        this.expireAfterWriteNanos = timeunit.toNanos(i);
        if (i == 0L && this.nullRemovalCause == null) {
            this.nullRemovalCause = MapMaker.RemovalCause.EXPIRED;
        }

        this.useCustomMap = true;
        return this;
    }

    private void checkExpiration(long i, TimeUnit timeunit) {
        Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", this.expireAfterWriteNanos);
        Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", this.expireAfterAccessNanos);
        Preconditions.checkArgument(i >= 0L, "duration cannot be negative: %s %s", i, timeunit);
    }

    long getExpireAfterWriteNanos() {
        return this.expireAfterWriteNanos == -1L ? 0L : this.expireAfterWriteNanos;
    }

    /** @deprecated */
    @Deprecated
    @GwtIncompatible("To be supported")
    @Override
    MapMaker expireAfterAccess(long i, TimeUnit timeunit) {
        this.checkExpiration(i, timeunit);
        this.expireAfterAccessNanos = timeunit.toNanos(i);
        if (i == 0L && this.nullRemovalCause == null) {
            this.nullRemovalCause = MapMaker.RemovalCause.EXPIRED;
        }

        this.useCustomMap = true;
        return this;
    }

    long getExpireAfterAccessNanos() {
        return this.expireAfterAccessNanos == -1L ? 0L : this.expireAfterAccessNanos;
    }

    Ticker getTicker() {
        return (Ticker) Objects.firstNonNull(this.ticker, Ticker.systemTicker());
    }

    /** @deprecated */
    @Deprecated
    @GwtIncompatible("To be supported")
    <K, V> GenericMapMaker<K, V> removalListener(MapMaker.RemovalListener<K, V> mapmaker_removallistener) {
        Preconditions.checkState(this.removalListener == null);
        super.removalListener = (MapMaker.RemovalListener) Preconditions.checkNotNull(mapmaker_removallistener);
        this.useCustomMap = true;
        return this;
    }

    @Override
    public <K, V> ConcurrentMap<K, V> makeMap() {
        return (ConcurrentMap) (!this.useCustomMap ? new ConcurrentHashMap(this.getInitialCapacity(), 0.75F, this.getConcurrencyLevel()) : (ConcurrentMap) (this.nullRemovalCause == null ? new MapMakerInternalMap<>(this) : new MapMaker.NullConcurrentMap<>(this)));
    }

    @GwtIncompatible("MapMakerInternalMap")
    @Override
    <K, V> MapMakerInternalMap<K, V> makeCustomMap() {
        return new MapMakerInternalMap<>(this);
    }

    /** @deprecated */
    @Deprecated
    @Override
    <K, V> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> function) {
        return (ConcurrentMap) (this.nullRemovalCause == null ? new MapMaker.ComputingMapAdapter<>(this, function) : new MapMaker.NullComputingConcurrentMap<>(this, function));
    }

    public String toString() {
        Objects.ToStringHelper objects_tostringhelper = Objects.toStringHelper((Object) this);

        if (this.initialCapacity != -1) {
            objects_tostringhelper.add("initialCapacity", this.initialCapacity);
        }

        if (this.concurrencyLevel != -1) {
            objects_tostringhelper.add("concurrencyLevel", this.concurrencyLevel);
        }

        if (this.maximumSize != -1) {
            objects_tostringhelper.add("maximumSize", this.maximumSize);
        }

        if (this.expireAfterWriteNanos != -1L) {
            objects_tostringhelper.add("expireAfterWrite", this.expireAfterWriteNanos + "ns");
        }

        if (this.expireAfterAccessNanos != -1L) {
            objects_tostringhelper.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
        }

        if (this.keyStrength != null) {
            objects_tostringhelper.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
        }

        if (this.valueStrength != null) {
            objects_tostringhelper.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
        }

        if (this.keyEquivalence != null) {
            objects_tostringhelper.addValue("keyEquivalence");
        }

        if (this.removalListener != null) {
            objects_tostringhelper.addValue("removalListener");
        }

        return objects_tostringhelper.toString();
    }

    static final class ComputingMapAdapter<K, V> extends ComputingConcurrentHashMap<K, V> implements Serializable {

        private static final long serialVersionUID = 0L;

        ComputingMapAdapter(MapMaker mapmaker, Function<? super K, ? extends V> function) {
            super(mapmaker, function);
        }

        @Override
        public V get(Object object) {
            Object object1;

            try {
                object1 = this.getOrCompute(object);
            } catch (ExecutionException executionexception) {
                Throwable throwable = executionexception.getCause();

                Throwables.propagateIfInstanceOf(throwable, ComputationException.class);
                throw new ComputationException(throwable);
            }

            if (object1 == null) {
                throw new NullPointerException(this.computingFunction + " returned null for key " + object + ".");
            } else {
                return object1;
            }
        }
    }

    static final class NullComputingConcurrentMap<K, V> extends MapMaker.NullConcurrentMap<K, V> {

        private static final long serialVersionUID = 0L;
        final Function<? super K, ? extends V> computingFunction;

        NullComputingConcurrentMap(MapMaker mapmaker, Function<? super K, ? extends V> function) {
            super(mapmaker);
            this.computingFunction = (Function) Preconditions.checkNotNull(function);
        }

        @Override
        public V get(Object object) {
            Object object1 = this.compute(object);

            Preconditions.checkNotNull(object1, this.computingFunction + " returned null for key " + object + ".");
            this.notifyRemoval(object, object1);
            return object1;
        }

        private V compute(K k0) {
            Preconditions.checkNotNull(k0);

            try {
                return this.computingFunction.apply(k0);
            } catch (ComputationException computationexception) {
                throw computationexception;
            } catch (Throwable throwable) {
                throw new ComputationException(throwable);
            }
        }
    }

    static class NullConcurrentMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {

        private static final long serialVersionUID = 0L;
        private final MapMaker.RemovalListener<K, V> removalListener;
        private final MapMaker.RemovalCause removalCause;

        NullConcurrentMap(MapMaker mapmaker) {
            this.removalListener = mapmaker.getRemovalListener();
            this.removalCause = mapmaker.nullRemovalCause;
        }

        public boolean containsKey(@Nullable Object object) {
            return false;
        }

        public boolean containsValue(@Nullable Object object) {
            return false;
        }

        public V get(@Nullable Object object) {
            return null;
        }

        void notifyRemoval(K k0, V v0) {
            MapMaker.RemovalNotification mapmaker_removalnotification = new MapMaker.RemovalNotification<>(k0, v0, this.removalCause);

            this.removalListener.onRemoval(mapmaker_removalnotification);
        }

        public V put(K k0, V v0) {
            Preconditions.checkNotNull(k0);
            Preconditions.checkNotNull(v0);
            this.notifyRemoval(k0, v0);
            return null;
        }

        public V putIfAbsent(K k0, V v0) {
            return this.put(k0, v0);
        }

        public V remove(@Nullable Object object) {
            return null;
        }

        public boolean remove(@Nullable Object object, @Nullable Object object1) {
            return false;
        }

        public V replace(K k0, V v0) {
            Preconditions.checkNotNull(k0);
            Preconditions.checkNotNull(v0);
            return null;
        }

        public boolean replace(K k0, @Nullable V v0, V v1) {
            Preconditions.checkNotNull(k0);
            Preconditions.checkNotNull(v1);
            return false;
        }

        public Set<Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }
    }

    static enum RemovalCause {

        EXPLICIT {
            @Override
            boolean wasEvicted() {
                return false;
            }
        },
        REPLACED {
            @Override
            boolean wasEvicted() {
                return false;
            }
        },
        COLLECTED {
            @Override
            boolean wasEvicted() {
                return true;
            }
        },
        EXPIRED {
            @Override
            boolean wasEvicted() {
                return true;
            }
        },
        SIZE {
            @Override
            boolean wasEvicted() {
                return true;
            }
        };

        private RemovalCause() {}

        abstract boolean wasEvicted();
    }

    static final class RemovalNotification<K, V> extends ImmutableEntry<K, V> {

        private static final long serialVersionUID = 0L;
        private final MapMaker.RemovalCause cause;

        RemovalNotification(@Nullable K k0, @Nullable V v0, MapMaker.RemovalCause mapmaker_removalcause) {
            super(k0, v0);
            this.cause = mapmaker_removalcause;
        }

        public MapMaker.RemovalCause getCause() {
            return this.cause;
        }

        public boolean wasEvicted() {
            return this.cause.wasEvicted();
        }
    }

    interface RemovalListener<K, V> {

        void onRemoval(MapMaker.RemovalNotification<K, V> mapmaker_removalnotification);
    }
}
