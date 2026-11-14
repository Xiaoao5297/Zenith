package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/** @deprecated */
@Deprecated
@Beta
@GwtCompatible(emulated = true)
abstract class GenericMapMaker<K0, V0> {

    @GwtIncompatible("To be supported")
    MapMaker.RemovalListener<K0, V0> removalListener;

    GenericMapMaker() {}

    @GwtIncompatible("To be supported")
    abstract GenericMapMaker<K0, V0> keyEquivalence(Equivalence<Object> equivalence);

    public abstract GenericMapMaker<K0, V0> initialCapacity(int i);

    abstract GenericMapMaker<K0, V0> maximumSize(int i);

    public abstract GenericMapMaker<K0, V0> concurrencyLevel(int i);

    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakKeys();

    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakValues();

    /** @deprecated */
    @Deprecated
    @GwtIncompatible("java.lang.ref.SoftReference")
    public abstract GenericMapMaker<K0, V0> softValues();

    abstract GenericMapMaker<K0, V0> expireAfterWrite(long i, TimeUnit timeunit);

    @GwtIncompatible("To be supported")
    abstract GenericMapMaker<K0, V0> expireAfterAccess(long i, TimeUnit timeunit);

    @GwtIncompatible("To be supported")
    <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener() {
        return (MapMaker.RemovalListener) Objects.firstNonNull(this.removalListener, GenericMapMaker.NullListener.INSTANCE);
    }

    public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();

    @GwtIncompatible("MapMakerInternalMap")
    abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();

    /** @deprecated */
    @Deprecated
    abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> function);

    @GwtIncompatible("To be supported")
    static enum NullListener implements MapMaker.RemovalListener<Object, Object> {

        INSTANCE;

        private NullListener() {}

        @Override
        public void onRemoval(MapMaker.RemovalNotification<Object, Object> mapmaker_removalnotification) {}
    }
}
