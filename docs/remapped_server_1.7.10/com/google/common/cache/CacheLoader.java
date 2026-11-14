package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.Serializable;
import java.util.Map;

@GwtCompatible(emulated = true)
public abstract class CacheLoader<K, V> {

    protected CacheLoader() {}

    public abstract V load(K k0) throws Exception;

    @GwtIncompatible("Futures")
    public ListenableFuture<V> reload(K k0, V v0) throws Exception {
        Preconditions.checkNotNull(k0);
        Preconditions.checkNotNull(v0);
        return Futures.immediateFuture(this.load(k0));
    }

    public Map<K, V> loadAll(Iterable<? extends K> iterable) throws Exception {
        throw new CacheLoader.UnsupportedLoadingOperationException();
    }

    @Beta
    public static <K, V> CacheLoader<K, V> from(Function<K, V> function) {
        return new CacheLoader.FunctionToCacheLoader<>(function);
    }

    @Beta
    public static <V> CacheLoader<Object, V> from(Supplier<V> supplier) {
        return new CacheLoader.SupplierToCacheLoader<>(supplier);
    }

    public static final class InvalidCacheLoadException extends RuntimeException {

        public InvalidCacheLoadException(String s) {
            super(s);
        }
    }

    static final class UnsupportedLoadingOperationException extends UnsupportedOperationException {

        UnsupportedLoadingOperationException() {}
    }

    private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V> implements Serializable {

        private final Supplier<V> computingSupplier;
        private static final long serialVersionUID = 0L;

        public SupplierToCacheLoader(Supplier<V> supplier) {
            this.computingSupplier = (Supplier) Preconditions.checkNotNull(supplier);
        }

        @Override
        public V load(Object object) {
            Preconditions.checkNotNull(object);
            return this.computingSupplier.get();
        }
    }

    private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V> implements Serializable {

        private final Function<K, V> computingFunction;
        private static final long serialVersionUID = 0L;

        public FunctionToCacheLoader(Function<K, V> function) {
            this.computingFunction = (Function) Preconditions.checkNotNull(function);
        }

        @Override
        public V load(K k0) {
            return this.computingFunction.apply(Preconditions.checkNotNull(k0));
        }
    }
}
