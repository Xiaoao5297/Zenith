package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public abstract class FluentIterable<E> implements Iterable<E> {

    private final Iterable<E> iterable;

    protected FluentIterable() {
        this.iterable = this;
    }

    FluentIterable(Iterable<E> iterable) {
        this.iterable = (Iterable) Preconditions.checkNotNull(iterable);
    }

    public static <E> FluentIterable<E> from(final Iterable<E> iterable) {
        return iterable instanceof FluentIterable ? (FluentIterable) iterable : new FluentIterable<E>(iterable) {
            public Iterator<E> iterator() {
                return iterable.iterator();
            }
        };
    }

    /** @deprecated */
    @Deprecated
    public static <E> FluentIterable<E> from(FluentIterable<E> fluentiterable) {
        return (FluentIterable) Preconditions.checkNotNull(fluentiterable);
    }

    public String toString() {
        return Iterables.toString(this.iterable);
    }

    public final int size() {
        return Iterables.size(this.iterable);
    }

    public final boolean contains(@Nullable Object object) {
        return Iterables.contains(this.iterable, object);
    }

    @CheckReturnValue
    public final FluentIterable<E> cycle() {
        return from(Iterables.cycle(this.iterable));
    }

    @CheckReturnValue
    public final FluentIterable<E> filter(Predicate<? super E> predicate) {
        return from(Iterables.filter(this.iterable, predicate));
    }

    @CheckReturnValue
    @GwtIncompatible("Class.isInstance")
    public final <T> FluentIterable<T> filter(Class<T> oclass) {
        return from(Iterables.filter(this.iterable, oclass));
    }

    public final boolean anyMatch(Predicate<? super E> predicate) {
        return Iterables.any(this.iterable, predicate);
    }

    public final boolean allMatch(Predicate<? super E> predicate) {
        return Iterables.all(this.iterable, predicate);
    }

    public final Optional<E> firstMatch(Predicate<? super E> predicate) {
        return Iterables.tryFind(this.iterable, predicate);
    }

    public final <T> FluentIterable<T> transform(Function<? super E, T> function) {
        return from(Iterables.transform(this.iterable, function));
    }

    public <T> FluentIterable<T> transformAndConcat(Function<? super E, ? extends Iterable<? extends T>> function) {
        return from(Iterables.concat((Iterable) this.transform(function)));
    }

    public final Optional<E> first() {
        Iterator iterator = this.iterable.iterator();

        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.absent();
    }

    public final Optional<E> last() {
        if (this.iterable instanceof List) {
            List list = (List) this.iterable;

            return list.isEmpty() ? Optional.absent() : Optional.of(list.get(list.size() - 1));
        } else {
            Iterator iterator = this.iterable.iterator();

            if (!iterator.hasNext()) {
                return Optional.absent();
            } else if (this.iterable instanceof SortedSet) {
                SortedSet sortedset = (SortedSet) this.iterable;

                return Optional.of(sortedset.last());
            } else {
                Object object;

                do {
                    object = iterator.next();
                } while (iterator.hasNext());

                return Optional.of(object);
            }
        }
    }

    @CheckReturnValue
    public final FluentIterable<E> skip(int i) {
        return from(Iterables.skip(this.iterable, i));
    }

    @CheckReturnValue
    public final FluentIterable<E> limit(int i) {
        return from(Iterables.limit(this.iterable, i));
    }

    public final boolean isEmpty() {
        return !this.iterable.iterator().hasNext();
    }

    public final ImmutableList<E> toList() {
        return ImmutableList.copyOf(this.iterable);
    }

    @Beta
    public final ImmutableList<E> toSortedList(Comparator<? super E> comparator) {
        return Ordering.from(comparator).immutableSortedCopy(this.iterable);
    }

    public final ImmutableSet<E> toSet() {
        return ImmutableSet.copyOf(this.iterable);
    }

    public final ImmutableSortedSet<E> toSortedSet(Comparator<? super E> comparator) {
        return ImmutableSortedSet.copyOf(comparator, this.iterable);
    }

    public final <V> ImmutableMap<E, V> toMap(Function<? super E, V> function) {
        return Maps.toMap(this.iterable, function);
    }

    public final <K> ImmutableListMultimap<K, E> index(Function<? super E, K> function) {
        return Multimaps.index(this.iterable, function);
    }

    public final <K> ImmutableMap<K, E> uniqueIndex(Function<? super E, K> function) {
        return Maps.uniqueIndex(this.iterable, function);
    }

    @GwtIncompatible("Array.newArray(Class, int)")
    public final E[] toArray(Class<E> oclass) {
        return Iterables.toArray(this.iterable, oclass);
    }

    public final <C extends Collection<? super E>> C copyInto(C c0) {
        Preconditions.checkNotNull(c0);
        if (this.iterable instanceof Collection) {
            c0.addAll(Collections2.cast(this.iterable));
        } else {
            Iterator iterator = this.iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                c0.add(object);
            }
        }

        return c0;
    }

    public final E get(int i) {
        return Iterables.get(this.iterable, i);
    }

    private static class FromIterableFunction<E> implements Function<Iterable<E>, FluentIterable<E>> {

        private FromIterableFunction() {}

        public FluentIterable<E> apply(Iterable<E> iterable) {
            return FluentIterable.from(iterable);
        }
    }
}
