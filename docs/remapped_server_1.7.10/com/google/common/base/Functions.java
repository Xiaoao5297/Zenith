package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public final class Functions {

    private Functions() {}

    public static Function<Object, String> toStringFunction() {
        return Functions.ToStringFunction.INSTANCE;
    }

    public static <E> Function<E, E> identity() {
        return Functions.IdentityFunction.INSTANCE;
    }

    public static <K, V> Function<K, V> forMap(Map<K, V> map) {
        return new Functions.FunctionForMapNoDefault<>(map);
    }

    public static <K, V> Function<K, V> forMap(Map<K, ? extends V> map, @Nullable V v0) {
        return new Functions.ForMapWithDefault<>(map, v0);
    }

    public static <A, B, C> Function<A, C> compose(Function<B, C> function, Function<A, ? extends B> function1) {
        return new Functions.FunctionComposition<>(function, function1);
    }

    public static <T> Function<T, Boolean> forPredicate(Predicate<T> predicate) {
        return new Functions.PredicateFunction<>(predicate);
    }

    public static <E> Function<Object, E> constant(@Nullable E e0) {
        return new Functions.ConstantFunction<>(e0);
    }

    @Beta
    public static <T> Function<Object, T> forSupplier(Supplier<T> supplier) {
        return new Functions.SupplierFunction<>(supplier);
    }

    private static class SupplierFunction<T> implements Function<Object, T>, Serializable {

        private final Supplier<T> supplier;
        private static final long serialVersionUID = 0L;

        private SupplierFunction(Supplier<T> supplier) {
            this.supplier = (Supplier) Preconditions.checkNotNull(supplier);
        }

        @Override
        public T apply(@Nullable Object object) {
            return this.supplier.get();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Functions.SupplierFunction) {
                Functions.SupplierFunction functions_supplierfunction = (Functions.SupplierFunction) object;

                return this.supplier.equals(functions_supplierfunction.supplier);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.supplier.hashCode();
        }

        public String toString() {
            return "forSupplier(" + this.supplier + ")";
        }
    }

    private static class ConstantFunction<E> implements Function<Object, E>, Serializable {

        private final E value;
        private static final long serialVersionUID = 0L;

        public ConstantFunction(@Nullable E e0) {
            this.value = e0;
        }

        @Override
        public E apply(@Nullable Object object) {
            return this.value;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Functions.ConstantFunction) {
                Functions.ConstantFunction functions_constantfunction = (Functions.ConstantFunction) object;

                return Objects.equal(this.value, functions_constantfunction.value);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.value == null ? 0 : this.value.hashCode();
        }

        public String toString() {
            return "constant(" + this.value + ")";
        }
    }

    private static class PredicateFunction<T> implements Function<T, Boolean>, Serializable {

        private final Predicate<T> predicate;
        private static final long serialVersionUID = 0L;

        private PredicateFunction(Predicate<T> predicate) {
            this.predicate = (Predicate) Preconditions.checkNotNull(predicate);
        }

        @Override
        public Boolean apply(@Nullable T t0) {
            return this.predicate.apply(t0);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Functions.PredicateFunction) {
                Functions.PredicateFunction functions_predicatefunction = (Functions.PredicateFunction) object;

                return this.predicate.equals(functions_predicatefunction.predicate);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.predicate.hashCode();
        }

        public String toString() {
            return "forPredicate(" + this.predicate + ")";
        }
    }

    private static class FunctionComposition<A, B, C> implements Function<A, C>, Serializable {

        private final Function<B, C> g;
        private final Function<A, ? extends B> f;
        private static final long serialVersionUID = 0L;

        public FunctionComposition(Function<B, C> function, Function<A, ? extends B> function1) {
            this.g = (Function) Preconditions.checkNotNull(function);
            this.f = (Function) Preconditions.checkNotNull(function1);
        }

        @Override
        public C apply(@Nullable A a0) {
            return this.g.apply(this.f.apply(a0));
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Functions.FunctionComposition)) {
                return false;
            } else {
                Functions.FunctionComposition functions_functioncomposition = (Functions.FunctionComposition) object;

                return this.f.equals(functions_functioncomposition.f) && this.g.equals(functions_functioncomposition.g);
            }
        }

        public int hashCode() {
            return this.f.hashCode() ^ this.g.hashCode();
        }

        public String toString() {
            return this.g.toString() + "(" + this.f.toString() + ")";
        }
    }

    private static class ForMapWithDefault<K, V> implements Function<K, V>, Serializable {

        final Map<K, ? extends V> map;
        final V defaultValue;
        private static final long serialVersionUID = 0L;

        ForMapWithDefault(Map<K, ? extends V> map, @Nullable V v0) {
            this.map = (Map) Preconditions.checkNotNull(map);
            this.defaultValue = v0;
        }

        @Override
        public V apply(@Nullable K k0) {
            Object object = this.map.get(k0);

            return object == null && !this.map.containsKey(k0) ? this.defaultValue : object;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Functions.ForMapWithDefault)) {
                return false;
            } else {
                Functions.ForMapWithDefault functions_formapwithdefault = (Functions.ForMapWithDefault) object;

                return this.map.equals(functions_formapwithdefault.map) && Objects.equal(this.defaultValue, functions_formapwithdefault.defaultValue);
            }
        }

        public int hashCode() {
            return Objects.hashCode(this.map, this.defaultValue);
        }

        public String toString() {
            return "forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
        }
    }

    private static class FunctionForMapNoDefault<K, V> implements Function<K, V>, Serializable {

        final Map<K, V> map;
        private static final long serialVersionUID = 0L;

        FunctionForMapNoDefault(Map<K, V> map) {
            this.map = (Map) Preconditions.checkNotNull(map);
        }

        @Override
        public V apply(@Nullable K k0) {
            Object object = this.map.get(k0);

            Preconditions.checkArgument(object != null || this.map.containsKey(k0), "Key '%s' not present in map", k0);
            return object;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Functions.FunctionForMapNoDefault) {
                Functions.FunctionForMapNoDefault functions_functionformapnodefault = (Functions.FunctionForMapNoDefault) object;

                return this.map.equals(functions_functionformapnodefault.map);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.map.hashCode();
        }

        public String toString() {
            return "forMap(" + this.map + ")";
        }
    }

    private static enum IdentityFunction implements Function<Object, Object> {

        INSTANCE;

        private IdentityFunction() {}

        @Nullable
        @Override
        public Object apply(@Nullable Object object) {
            return object;
        }

        public String toString() {
            return "identity";
        }
    }

    private static enum ToStringFunction implements Function<Object, String> {

        INSTANCE;

        private ToStringFunction() {}

        @Override
        public String apply(Object object) {
            Preconditions.checkNotNull(object);
            return object.toString();
        }

        public String toString() {
            return "toString";
        }
    }
}
