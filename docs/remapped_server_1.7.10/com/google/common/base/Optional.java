package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
public abstract class Optional<T> implements Serializable {

    private static final long serialVersionUID = 0L;

    public static <T> Optional<T> absent() {
        return Absent.withType();
    }

    public static <T> Optional<T> of(T t0) {
        return new Present<>(Preconditions.checkNotNull(t0));
    }

    public static <T> Optional<T> fromNullable(@Nullable T t0) {
        return (Optional) (t0 == null ? absent() : new Present<>(t0));
    }

    Optional() {}

    public abstract boolean isPresent();

    public abstract T get();

    public abstract T or(T t0);

    public abstract Optional<T> or(Optional<? extends T> optional);

    @Beta
    public abstract T or(Supplier<? extends T> supplier);

    @Nullable
    public abstract T orNull();

    public abstract Set<T> asSet();

    public abstract <V> Optional<V> transform(Function<? super T, V> function);

    public abstract boolean equals(@Nullable Object object);

    public abstract int hashCode();

    public abstract String toString();

    @Beta
    public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> iterable) {
        Preconditions.checkNotNull(iterable);
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() {
                    private final Iterator<? extends Optional<? extends T>> iterator = (Iterator) Preconditions.checkNotNull(iterable.iterator());

                    @Override
                    protected T computeNext() {
                        while (true) {
                            if (this.iterator.hasNext()) {
                                Optional optional = (Optional) this.iterator.next();

                                if (!optional.isPresent()) {
                                    continue;
                                }

                                return optional.get();
                            }

                            return this.endOfData();
                        }
                    }
                };
            }
        };
    }
}
