package org.apache.commons.lang3.concurrent;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrentUtils {

    private ConcurrentUtils() {}

    public static ConcurrentException extractCause(ExecutionException executionexception) {
        if (executionexception != null && executionexception.getCause() != null) {
            throwCause(executionexception);
            return new ConcurrentException(executionexception.getMessage(), executionexception.getCause());
        } else {
            return null;
        }
    }

    public static ConcurrentRuntimeException extractCauseUnchecked(ExecutionException executionexception) {
        if (executionexception != null && executionexception.getCause() != null) {
            throwCause(executionexception);
            return new ConcurrentRuntimeException(executionexception.getMessage(), executionexception.getCause());
        } else {
            return null;
        }
    }

    public static void handleCause(ExecutionException executionexception) throws ConcurrentException {
        ConcurrentException concurrentexception = extractCause(executionexception);

        if (concurrentexception != null) {
            throw concurrentexception;
        }
    }

    public static void handleCauseUnchecked(ExecutionException executionexception) {
        ConcurrentRuntimeException concurrentruntimeexception = extractCauseUnchecked(executionexception);

        if (concurrentruntimeexception != null) {
            throw concurrentruntimeexception;
        }
    }

    static Throwable checkedException(Throwable throwable) {
        if (throwable != null && !(throwable instanceof RuntimeException) && !(throwable instanceof Error)) {
            return throwable;
        } else {
            throw new IllegalArgumentException("Not a checked exception: " + throwable);
        }
    }

    private static void throwCause(ExecutionException executionexception) {
        if (executionexception.getCause() instanceof RuntimeException) {
            throw (RuntimeException) executionexception.getCause();
        } else if (executionexception.getCause() instanceof Error) {
            throw (Error) executionexception.getCause();
        }
    }

    public static <T> T initialize(ConcurrentInitializer<T> concurrentinitializer) throws ConcurrentException {
        return concurrentinitializer != null ? concurrentinitializer.get() : null;
    }

    public static <T> T initializeUnchecked(ConcurrentInitializer<T> concurrentinitializer) {
        try {
            return initialize(concurrentinitializer);
        } catch (ConcurrentException concurrentexception) {
            throw new ConcurrentRuntimeException(concurrentexception.getCause());
        }
    }

    public static <K, V> V putIfAbsent(ConcurrentMap<K, V> concurrentmap, K k0, V v0) {
        if (concurrentmap == null) {
            return null;
        } else {
            Object object = concurrentmap.putIfAbsent(k0, v0);

            return object != null ? object : v0;
        }
    }

    public static <K, V> V createIfAbsent(ConcurrentMap<K, V> concurrentmap, K k0, ConcurrentInitializer<V> concurrentinitializer) throws ConcurrentException {
        if (concurrentmap != null && concurrentinitializer != null) {
            Object object = concurrentmap.get(k0);

            return object == null ? putIfAbsent(concurrentmap, k0, concurrentinitializer.get()) : object;
        } else {
            return null;
        }
    }

    public static <K, V> V createIfAbsentUnchecked(ConcurrentMap<K, V> concurrentmap, K k0, ConcurrentInitializer<V> concurrentinitializer) {
        try {
            return createIfAbsent(concurrentmap, k0, concurrentinitializer);
        } catch (ConcurrentException concurrentexception) {
            throw new ConcurrentRuntimeException(concurrentexception.getCause());
        }
    }

    public static <T> Future<T> constantFuture(T t0) {
        return new ConcurrentUtils.ConstantFuture<>(t0);
    }

    static final class ConstantFuture<T> implements Future<T> {

        private final T value;

        ConstantFuture(T t0) {
            this.value = t0;
        }

        public boolean isDone() {
            return true;
        }

        public T get() {
            return this.value;
        }

        public T get(long i, TimeUnit timeunit) {
            return this.value;
        }

        public boolean isCancelled() {
            return false;
        }

        public boolean cancel(boolean flag) {
            return false;
        }
    }
}
