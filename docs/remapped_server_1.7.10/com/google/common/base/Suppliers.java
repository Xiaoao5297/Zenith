package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@GwtCompatible
public final class Suppliers {

    private Suppliers() {}

    public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
        Preconditions.checkNotNull(function);
        Preconditions.checkNotNull(supplier);
        return new Suppliers.SupplierComposition<>(function, supplier);
    }

    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return (Supplier) (supplier instanceof Suppliers.MemoizingSupplier ? supplier : new Suppliers.MemoizingSupplier<>((Supplier) Preconditions.checkNotNull(supplier)));
    }

    public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> supplier, long i, TimeUnit timeunit) {
        return new Suppliers.ExpiringMemoizingSupplier<>(supplier, i, timeunit);
    }

    public static <T> Supplier<T> ofInstance(@Nullable T t0) {
        return new Suppliers.SupplierOfInstance<>(t0);
    }

    public static <T> Supplier<T> synchronizedSupplier(Supplier<T> supplier) {
        return new Suppliers.ThreadSafeSupplier<>((Supplier) Preconditions.checkNotNull(supplier));
    }

    @Beta
    public static <T> Function<Supplier<T>, T> supplierFunction() {
        Suppliers.SupplierFunctionImpl suppliers_supplierfunctionimpl = Suppliers.SupplierFunctionImpl.INSTANCE;

        return suppliers_supplierfunctionimpl;
    }

    private static enum SupplierFunctionImpl implements Suppliers.SupplierFunction<Object> {

        INSTANCE;

        private SupplierFunctionImpl() {}

        public Object apply(Supplier<Object> supplier) {
            return supplier.get();
        }

        public String toString() {
            return "Suppliers.supplierFunction()";
        }
    }

    private interface SupplierFunction<T> extends Function<Supplier<T>, T> {}

    private static class ThreadSafeSupplier<T> implements Supplier<T>, Serializable {

        final Supplier<T> delegate;
        private static final long serialVersionUID = 0L;

        ThreadSafeSupplier(Supplier<T> supplier) {
            this.delegate = supplier;
        }

        @Override
        public T get() {
            Supplier supplier = this.delegate;

            synchronized (this.delegate) {
                return this.delegate.get();
            }
        }

        public String toString() {
            return "Suppliers.synchronizedSupplier(" + this.delegate + ")";
        }
    }

    private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {

        final T instance;
        private static final long serialVersionUID = 0L;

        SupplierOfInstance(@Nullable T t0) {
            this.instance = t0;
        }

        @Override
        public T get() {
            return this.instance;
        }

        public boolean equals(@Nullable Object object) {
            if (object instanceof Suppliers.SupplierOfInstance) {
                Suppliers.SupplierOfInstance suppliers_supplierofinstance = (Suppliers.SupplierOfInstance) object;

                return Objects.equal(this.instance, suppliers_supplierofinstance.instance);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hashCode(this.instance);
        }

        public String toString() {
            return "Suppliers.ofInstance(" + this.instance + ")";
        }
    }

    @VisibleForTesting
    static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable {

        final Supplier<T> delegate;
        final long durationNanos;
        transient volatile T value;
        transient volatile long expirationNanos;
        private static final long serialVersionUID = 0L;

        ExpiringMemoizingSupplier(Supplier<T> supplier, long i, TimeUnit timeunit) {
            this.delegate = (Supplier) Preconditions.checkNotNull(supplier);
            this.durationNanos = timeunit.toNanos(i);
            Preconditions.checkArgument(i > 0L);
        }

        @Override
        public T get() {
            long i = this.expirationNanos;
            long j = Platform.systemNanoTime();

            if (i == 0L || j - i >= 0L) {
                synchronized (this) {
                    if (i == this.expirationNanos) {
                        Object object = this.delegate.get();

                        this.value = object;
                        i = j + this.durationNanos;
                        this.expirationNanos = i == 0L ? 1L : i;
                        return object;
                    }
                }
            }

            return this.value;
        }

        public String toString() {
            return "Suppliers.memoizeWithExpiration(" + this.delegate + ", " + this.durationNanos + ", NANOS)";
        }
    }

    @VisibleForTesting
    static class MemoizingSupplier<T> implements Supplier<T>, Serializable {

        final Supplier<T> delegate;
        transient volatile boolean initialized;
        transient T value;
        private static final long serialVersionUID = 0L;

        MemoizingSupplier(Supplier<T> supplier) {
            this.delegate = supplier;
        }

        @Override
        public T get() {
            if (!this.initialized) {
                synchronized (this) {
                    if (!this.initialized) {
                        Object object = this.delegate.get();

                        this.value = object;
                        this.initialized = true;
                        return object;
                    }
                }
            }

            return this.value;
        }

        public String toString() {
            return "Suppliers.memoize(" + this.delegate + ")";
        }
    }

    private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {

        final Function<? super F, T> function;
        final Supplier<F> supplier;
        private static final long serialVersionUID = 0L;

        SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
            this.function = function;
            this.supplier = supplier;
        }

        @Override
        public T get() {
            return this.function.apply(this.supplier.get());
        }

        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Suppliers.SupplierComposition)) {
                return false;
            } else {
                Suppliers.SupplierComposition suppliers_suppliercomposition = (Suppliers.SupplierComposition) object;

                return this.function.equals(suppliers_suppliercomposition.function) && this.supplier.equals(suppliers_suppliercomposition.supplier);
            }
        }

        public int hashCode() {
            return Objects.hashCode(this.function, this.supplier);
        }

        public String toString() {
            return "Suppliers.compose(" + this.function + ", " + this.supplier + ")";
        }
    }
}
