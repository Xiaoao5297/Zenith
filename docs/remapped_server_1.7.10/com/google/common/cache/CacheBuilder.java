package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckReturnValue;

@GwtCompatible(emulated = true)
public final class CacheBuilder<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
    private static final int DEFAULT_EXPIRATION_NANOS = 0;
    private static final int DEFAULT_REFRESH_NANOS = 0;
    static final Supplier<? extends AbstractCache.StatsCounter> NULL_STATS_COUNTER = Suppliers.ofInstance(new AbstractCache.StatsCounter() {
        @Override
        public void recordHits(int i) {}

        @Override
        public void recordMisses(int i) {}

        @Override
        public void recordLoadSuccess(long i) {}

        @Override
        public void recordLoadException(long i) {}

        @Override
        public void recordEviction() {}

        @Override
        public CacheStats snapshot() {
            return CacheBuilder.EMPTY_STATS;
        }
    });
    static final CacheStats EMPTY_STATS = new CacheStats(0L, 0L, 0L, 0L, 0L, 0L);
    static final Supplier<AbstractCache.StatsCounter> CACHE_STATS_COUNTER = new Supplier<AbstractCache.StatsCounter>() {
        @Override
        public AbstractCache.StatsCounter get() {
            return new AbstractCache.SimpleStatsCounter();
        }
    };
    static final Ticker NULL_TICKER = new Ticker() {
        @Override
        public long read() {
            return 0L;
        }
    };
    private static final Logger logger = Logger.getLogger(CacheBuilder.class.getName());
    static final int UNSET_INT = -1;
    boolean strictParsing = true;
    int initialCapacity = -1;
    int concurrencyLevel = -1;
    long maximumSize = -1L;
    long maximumWeight = -1L;
    Weigher<? super K, ? super V> weigher;
    LocalCache.Strength keyStrength;
    LocalCache.Strength valueStrength;
    long expireAfterWriteNanos = -1L;
    long expireAfterAccessNanos = -1L;
    long refreshNanos = -1L;
    Equivalence<Object> keyEquivalence;
    Equivalence<Object> valueEquivalence;
    RemovalListener<? super K, ? super V> removalListener;
    Ticker ticker;
    Supplier<? extends AbstractCache.StatsCounter> statsCounterSupplier;

    CacheBuilder() {
        this.statsCounterSupplier = CacheBuilder.NULL_STATS_COUNTER;
    }

    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder<>();
    }

    @Beta
    @GwtIncompatible("To be supported")
    public static CacheBuilder<Object, Object> from(CacheBuilderSpec cachebuilderspec) {
        return cachebuilderspec.toCacheBuilder().lenientParsing();
    }

    @Beta
    @GwtIncompatible("To be supported")
    public static CacheBuilder<Object, Object> from(String s) {
        return from(CacheBuilderSpec.parse(s));
    }

    @GwtIncompatible("To be supported")
    CacheBuilder<K, V> lenientParsing() {
        this.strictParsing = false;
        return this;
    }

    @GwtIncompatible("To be supported")
    CacheBuilder<K, V> keyEquivalence(Equivalence<Object> equivalence) {
        Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", this.keyEquivalence);
        this.keyEquivalence = (Equivalence) Preconditions.checkNotNull(equivalence);
        return this;
    }

    Equivalence<Object> getKeyEquivalence() {
        return (Equivalence) Objects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
    }

    @GwtIncompatible("To be supported")
    CacheBuilder<K, V> valueEquivalence(Equivalence<Object> equivalence) {
        Preconditions.checkState(this.valueEquivalence == null, "value equivalence was already set to %s", this.valueEquivalence);
        this.valueEquivalence = (Equivalence) Preconditions.checkNotNull(equivalence);
        return this;
    }

    Equivalence<Object> getValueEquivalence() {
        return (Equivalence) Objects.firstNonNull(this.valueEquivalence, this.getValueStrength().defaultEquivalence());
    }

    public CacheBuilder<K, V> initialCapacity(int i) {
        Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", this.initialCapacity);
        Preconditions.checkArgument(i >= 0);
        this.initialCapacity = i;
        return this;
    }

    int getInitialCapacity() {
        return this.initialCapacity == -1 ? 16 : this.initialCapacity;
    }

    public CacheBuilder<K, V> concurrencyLevel(int i) {
        Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", this.concurrencyLevel);
        Preconditions.checkArgument(i > 0);
        this.concurrencyLevel = i;
        return this;
    }

    int getConcurrencyLevel() {
        return this.concurrencyLevel == -1 ? 4 : this.concurrencyLevel;
    }

    public CacheBuilder<K, V> maximumSize(long i) {
        Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", this.maximumSize);
        Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", this.maximumWeight);
        Preconditions.checkState(this.weigher == null, "maximum size can not be combined with weigher");
        Preconditions.checkArgument(i >= 0L, "maximum size must not be negative");
        this.maximumSize = i;
        return this;
    }

    @GwtIncompatible("To be supported")
    public CacheBuilder<K, V> maximumWeight(long i) {
        Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", this.maximumWeight);
        Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", this.maximumSize);
        this.maximumWeight = i;
        Preconditions.checkArgument(i >= 0L, "maximum weight must not be negative");
        return this;
    }

    @GwtIncompatible("To be supported")
    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> weigher(Weigher<? super K1, ? super V1> weigher) {
        Preconditions.checkState(this.weigher == null);
        if (this.strictParsing) {
            Preconditions.checkState(this.maximumSize == -1L, "weigher can not be combined with maximum size", this.maximumSize);
        }

        this.weigher = (Weigher) Preconditions.checkNotNull(weigher);
        return this;
    }

    long getMaximumWeight() {
        return this.expireAfterWriteNanos != 0L && this.expireAfterAccessNanos != 0L ? (this.weigher == null ? this.maximumSize : this.maximumWeight) : 0L;
    }

    <K1 extends K, V1 extends V> Weigher<K1, V1> getWeigher() {
        return (Weigher) Objects.firstNonNull(this.weigher, CacheBuilder.OneWeigher.INSTANCE);
    }

    @GwtIncompatible("java.lang.ref.WeakReference")
    public CacheBuilder<K, V> weakKeys() {
        return this.setKeyStrength(LocalCache.Strength.WEAK);
    }

    CacheBuilder<K, V> setKeyStrength(LocalCache.Strength localcache_strength) {
        Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", this.keyStrength);
        this.keyStrength = (LocalCache.Strength) Preconditions.checkNotNull(localcache_strength);
        return this;
    }

    LocalCache.Strength getKeyStrength() {
        return (LocalCache.Strength) Objects.firstNonNull(this.keyStrength, LocalCache.Strength.STRONG);
    }

    @GwtIncompatible("java.lang.ref.WeakReference")
    public CacheBuilder<K, V> weakValues() {
        return this.setValueStrength(LocalCache.Strength.WEAK);
    }

    @GwtIncompatible("java.lang.ref.SoftReference")
    public CacheBuilder<K, V> softValues() {
        return this.setValueStrength(LocalCache.Strength.SOFT);
    }

    CacheBuilder<K, V> setValueStrength(LocalCache.Strength localcache_strength) {
        Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", this.valueStrength);
        this.valueStrength = (LocalCache.Strength) Preconditions.checkNotNull(localcache_strength);
        return this;
    }

    LocalCache.Strength getValueStrength() {
        return (LocalCache.Strength) Objects.firstNonNull(this.valueStrength, LocalCache.Strength.STRONG);
    }

    public CacheBuilder<K, V> expireAfterWrite(long i, TimeUnit timeunit) {
        Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", this.expireAfterWriteNanos);
        Preconditions.checkArgument(i >= 0L, "duration cannot be negative: %s %s", i, timeunit);
        this.expireAfterWriteNanos = timeunit.toNanos(i);
        return this;
    }

    long getExpireAfterWriteNanos() {
        return this.expireAfterWriteNanos == -1L ? 0L : this.expireAfterWriteNanos;
    }

    public CacheBuilder<K, V> expireAfterAccess(long i, TimeUnit timeunit) {
        Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", this.expireAfterAccessNanos);
        Preconditions.checkArgument(i >= 0L, "duration cannot be negative: %s %s", i, timeunit);
        this.expireAfterAccessNanos = timeunit.toNanos(i);
        return this;
    }

    long getExpireAfterAccessNanos() {
        return this.expireAfterAccessNanos == -1L ? 0L : this.expireAfterAccessNanos;
    }

    @Beta
    @GwtIncompatible("To be supported (synchronously).")
    public CacheBuilder<K, V> refreshAfterWrite(long i, TimeUnit timeunit) {
        Preconditions.checkNotNull(timeunit);
        Preconditions.checkState(this.refreshNanos == -1L, "refresh was already set to %s ns", this.refreshNanos);
        Preconditions.checkArgument(i > 0L, "duration must be positive: %s %s", i, timeunit);
        this.refreshNanos = timeunit.toNanos(i);
        return this;
    }

    long getRefreshNanos() {
        return this.refreshNanos == -1L ? 0L : this.refreshNanos;
    }

    public CacheBuilder<K, V> ticker(Ticker ticker) {
        Preconditions.checkState(this.ticker == null);
        this.ticker = (Ticker) Preconditions.checkNotNull(ticker);
        return this;
    }

    Ticker getTicker(boolean flag) {
        return this.ticker != null ? this.ticker : (flag ? Ticker.systemTicker() : CacheBuilder.NULL_TICKER);
    }

    @CheckReturnValue
    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> removalListener(RemovalListener<? super K1, ? super V1> removallistener) {
        Preconditions.checkState(this.removalListener == null);
        this.removalListener = (RemovalListener) Preconditions.checkNotNull(removallistener);
        return this;
    }

    <K1 extends K, V1 extends V> RemovalListener<K1, V1> getRemovalListener() {
        return (RemovalListener) Objects.firstNonNull(this.removalListener, CacheBuilder.NullListener.INSTANCE);
    }

    public CacheBuilder<K, V> recordStats() {
        this.statsCounterSupplier = CacheBuilder.CACHE_STATS_COUNTER;
        return this;
    }

    boolean isRecordingStats() {
        return this.statsCounterSupplier == CacheBuilder.CACHE_STATS_COUNTER;
    }

    Supplier<? extends AbstractCache.StatsCounter> getStatsCounterSupplier() {
        return this.statsCounterSupplier;
    }

    public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(CacheLoader<? super K1, V1> cacheloader) {
        this.checkWeightWithWeigher();
        return new LocalCache.LocalLoadingCache<>(this, cacheloader);
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        this.checkWeightWithWeigher();
        this.checkNonLoadingCache();
        return new LocalCache.LocalManualCache<>(this);
    }

    private void checkNonLoadingCache() {
        Preconditions.checkState(this.refreshNanos == -1L, "refreshAfterWrite requires a LoadingCache");
    }

    private void checkWeightWithWeigher() {
        if (this.weigher == null) {
            Preconditions.checkState(this.maximumWeight == -1L, "maximumWeight requires weigher");
        } else if (this.strictParsing) {
            Preconditions.checkState(this.maximumWeight != -1L, "weigher requires maximumWeight");
        } else if (this.maximumWeight == -1L) {
            CacheBuilder.logger.log(Level.WARNING, "ignoring weigher specified without maximumWeight");
        }

    }

    public String toString() {
        Objects.ToStringHelper objects_tostringhelper = Objects.toStringHelper((Object) this);

        if (this.initialCapacity != -1) {
            objects_tostringhelper.add("initialCapacity", this.initialCapacity);
        }

        if (this.concurrencyLevel != -1) {
            objects_tostringhelper.add("concurrencyLevel", this.concurrencyLevel);
        }

        if (this.maximumSize != -1L) {
            objects_tostringhelper.add("maximumSize", this.maximumSize);
        }

        if (this.maximumWeight != -1L) {
            objects_tostringhelper.add("maximumWeight", this.maximumWeight);
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

        if (this.valueEquivalence != null) {
            objects_tostringhelper.addValue("valueEquivalence");
        }

        if (this.removalListener != null) {
            objects_tostringhelper.addValue("removalListener");
        }

        return objects_tostringhelper.toString();
    }

    static enum OneWeigher implements Weigher<Object, Object> {

        INSTANCE;

        private OneWeigher() {}

        @Override
        public int weigh(Object object, Object object1) {
            return 1;
        }
    }

    static enum NullListener implements RemovalListener<Object, Object> {

        INSTANCE;

        private NullListener() {}

        @Override
        public void onRemoval(RemovalNotification<Object, Object> removalnotification) {}
    }
}
