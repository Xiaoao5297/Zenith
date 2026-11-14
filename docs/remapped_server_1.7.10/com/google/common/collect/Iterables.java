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
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Iterables {

    private Iterables() {}

    public static <T> Iterable<T> unmodifiableIterable(Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        return (Iterable) (!(iterable instanceof Iterables.UnmodifiableIterable) && !(iterable instanceof ImmutableCollection) ? new Iterables.UnmodifiableIterable<>(iterable) : iterable);
    }

    /** @deprecated */
    @Deprecated
    public static <E> Iterable<E> unmodifiableIterable(ImmutableCollection<E> immutablecollection) {
        return (Iterable) Preconditions.checkNotNull(immutablecollection);
    }

    public static int size(Iterable<?> iterable) {
        return iterable instanceof Collection ? ((Collection) iterable).size() : Iterators.size(iterable.iterator());
    }

    public static boolean contains(Iterable<?> iterable, @Nullable Object object) {
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;

            return Collections2.safeContains(collection, object);
        } else {
            return Iterators.contains(iterable.iterator(), object);
        }
    }

    public static boolean removeAll(Iterable<?> iterable, Collection<?> collection) {
        return iterable instanceof Collection ? ((Collection) iterable).removeAll((Collection) Preconditions.checkNotNull(collection)) : Iterators.removeAll(iterable.iterator(), collection);
    }

    public static boolean retainAll(Iterable<?> iterable, Collection<?> collection) {
        return iterable instanceof Collection ? ((Collection) iterable).retainAll((Collection) Preconditions.checkNotNull(collection)) : Iterators.retainAll(iterable.iterator(), collection);
    }

    public static <T> boolean removeIf(Iterable<T> iterable, Predicate<? super T> predicate) {
        return iterable instanceof RandomAccess && iterable instanceof List ? removeIfFromRandomAccessList((List) iterable, (Predicate) Preconditions.checkNotNull(predicate)) : Iterators.removeIf(iterable.iterator(), predicate);
    }

    private static <T> boolean removeIfFromRandomAccessList(List<T> list, Predicate<? super T> predicate) {
        int i = 0;

        int j;

        for (j = 0; i < list.size(); ++i) {
            Object object = list.get(i);

            if (!predicate.apply(object)) {
                if (i > j) {
                    try {
                        list.set(j, object);
                    } catch (UnsupportedOperationException unsupportedoperationexception) {
                        slowRemoveIfForRemainingElements(list, predicate, j, i);
                        return true;
                    }
                }

                ++j;
            }
        }

        list.subList(j, list.size()).clear();
        return i != j;
    }

    private static <T> void slowRemoveIfForRemainingElements(List<T> list, Predicate<? super T> predicate, int i, int j) {
        int k;

        for (k = list.size() - 1; k > j; --k) {
            if (predicate.apply(list.get(k))) {
                list.remove(k);
            }
        }

        for (k = j - 1; k >= i; --k) {
            list.remove(k);
        }

    }

    @Nullable
    static <T> T removeFirstMatching(Iterable<T> iterable, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        Iterator iterator = iterable.iterator();

        Object object;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            object = iterator.next();
        } while (!predicate.apply(object));

        iterator.remove();
        return object;
    }

    public static boolean elementsEqual(Iterable<?> iterable, Iterable<?> iterable1) {
        if (iterable instanceof Collection && iterable1 instanceof Collection) {
            Collection collection = (Collection) iterable;
            Collection collection1 = (Collection) iterable1;

            if (collection.size() != collection1.size()) {
                return false;
            }
        }

        return Iterators.elementsEqual(iterable.iterator(), iterable1.iterator());
    }

    public static String toString(Iterable<?> iterable) {
        return Iterators.toString(iterable.iterator());
    }

    public static <T> T getOnlyElement(Iterable<T> iterable) {
        return Iterators.getOnlyElement(iterable.iterator());
    }

    @Nullable
    public static <T> T getOnlyElement(Iterable<? extends T> iterable, @Nullable T t0) {
        return Iterators.getOnlyElement(iterable.iterator(), t0);
    }

    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> oclass) {
        Collection collection = toCollection(iterable);
        Object[] aobject = ObjectArrays.newArray(oclass, collection.size());

        return collection.toArray(aobject);
    }

    static Object[] toArray(Iterable<?> iterable) {
        return toCollection(iterable).toArray();
    }

    private static <E> Collection<E> toCollection(Iterable<E> iterable) {
        return (Collection) (iterable instanceof Collection ? (Collection) iterable : Lists.newArrayList(iterable.iterator()));
    }

    public static <T> boolean addAll(Collection<T> collection, Iterable<? extends T> iterable) {
        if (iterable instanceof Collection) {
            Collection collection1 = Collections2.cast(iterable);

            return collection.addAll(collection1);
        } else {
            return Iterators.addAll(collection, ((Iterable) Preconditions.checkNotNull(iterable)).iterator());
        }
    }

    public static int frequency(Iterable<?> iterable, @Nullable Object object) {
        return iterable instanceof Multiset ? ((Multiset) iterable).count(object) : (iterable instanceof Set ? (((Set) iterable).contains(object) ? 1 : 0) : Iterators.frequency(iterable.iterator(), object));
    }

    public static <T> Iterable<T> cycle(final Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        return new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return Iterators.cycle(iterable);
            }

            @Override
            public String toString() {
                return iterable.toString() + " (cycled)";
            }
        };
    }

    public static <T> Iterable<T> cycle(T... at) {
        return cycle((Iterable) Lists.newArrayList(at));
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> iterable, Iterable<? extends T> iterable1) {
        return concat((Iterable) ImmutableList.of(iterable, iterable1));
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> iterable, Iterable<? extends T> iterable1, Iterable<? extends T> iterable2) {
        return concat((Iterable) ImmutableList.of(iterable, iterable1, iterable2));
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> iterable, Iterable<? extends T> iterable1, Iterable<? extends T> iterable2, Iterable<? extends T> iterable3) {
        return concat((Iterable) ImmutableList.of(iterable, iterable1, iterable2, iterable3));
    }

    public static <T> Iterable<T> concat(Iterable<? extends T>... aiterable) {
        return concat((Iterable) ImmutableList.copyOf((Object[]) aiterable));
    }

    public static <T> Iterable<T> concat(final Iterable<? extends Iterable<? extends T>> iterable) {
        Preconditions.checkNotNull(iterable);
        return new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return Iterators.concat(Iterables.iterators(iterable));
            }
        };
    }

    private static <T> Iterator<Iterator<? extends T>> iterators(Iterable<? extends Iterable<? extends T>> iterable) {
        return new TransformedIterator<Iterable<? extends T>, Iterator<? extends T>>(iterable.iterator()) {
            Iterator<? extends T> transform(Iterable<? extends T> iterable1) {
                return iterable1.iterator();
            }
        };
    }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final int i) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument(i > 0);
        return new FluentIterable<List<T>>() {
            public Iterator<List<T>> iterator() {
                return Iterators.partition(iterable.iterator(), i);
            }
        };
    }

    public static <T> Iterable<List<T>> paddedPartition(final Iterable<T> iterable, final int i) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument(i > 0);
        return new FluentIterable<List<T>>() {
            public Iterator<List<T>> iterator() {
                return Iterators.paddedPartition(iterable.iterator(), i);
            }
        };
    }

    public static <T> Iterable<T> filter(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(predicate);
        return new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return Iterators.filter(iterable.iterator(), predicate);
            }
        };
    }

    @GwtIncompatible("Class.isInstance")
    public static <T> Iterable<T> filter(final Iterable<?> iterable, final Class<T> oclass) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(oclass);
        return new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return Iterators.filter(iterable.iterator(), oclass);
            }
        };
    }

    public static <T> boolean any(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.any(iterable.iterator(), predicate);
    }

    public static <T> boolean all(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.all(iterable.iterator(), predicate);
    }

    public static <T> T find(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.find(iterable.iterator(), predicate);
    }

    @Nullable
    public static <T> T find(Iterable<? extends T> iterable, Predicate<? super T> predicate, @Nullable T t0) {
        return Iterators.find(iterable.iterator(), predicate, t0);
    }

    public static <T> Optional<T> tryFind(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.tryFind(iterable.iterator(), predicate);
    }

    public static <T> int indexOf(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.indexOf(iterable.iterator(), predicate);
    }

    public static <F, T> Iterable<T> transform(final Iterable<F> iterable, final Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(function);
        return new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return Iterators.transform(iterable.iterator(), function);
            }
        };
    }

    public static <T> T get(Iterable<T> iterable, int i) {
        Preconditions.checkNotNull(iterable);
        return iterable instanceof List ? ((List) iterable).get(i) : Iterators.get(iterable.iterator(), i);
    }

    @Nullable
    public static <T> T get(Iterable<? extends T> iterable, int i, @Nullable T t0) {
        Preconditions.checkNotNull(iterable);
        Iterators.checkNonnegative(i);
        if (iterable instanceof List) {
            List list = Lists.cast(iterable);

            return i < list.size() ? list.get(i) : t0;
        } else {
            Iterator iterator = iterable.iterator();

            Iterators.advance(iterator, i);
            return Iterators.getNext(iterator, t0);
        }
    }

    @Nullable
    public static <T> T getFirst(Iterable<? extends T> iterable, @Nullable T t0) {
        return Iterators.getNext(iterable.iterator(), t0);
    }

    public static <T> T getLast(Iterable<T> iterable) {
        if (iterable instanceof List) {
            List list = (List) iterable;

            if (list.isEmpty()) {
                throw new NoSuchElementException();
            } else {
                return getLastInNonemptyList(list);
            }
        } else {
            return Iterators.getLast(iterable.iterator());
        }
    }

    @Nullable
    public static <T> T getLast(Iterable<? extends T> iterable, @Nullable T t0) {
        if (iterable instanceof Collection) {
            Collection collection = Collections2.cast(iterable);

            if (collection.isEmpty()) {
                return t0;
            }

            if (iterable instanceof List) {
                return getLastInNonemptyList(Lists.cast(iterable));
            }
        }

        return Iterators.getLast(iterable.iterator(), t0);
    }

    private static <T> T getLastInNonemptyList(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> Iterable<T> skip(final Iterable<T> iterable, final int i) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument(i >= 0, "number to skip cannot be negative");
        if (iterable instanceof List) {
            final List list = (List) iterable;

            return new FluentIterable<T>() {
                public Iterator<T> iterator() {
                    int j = Math.min(list.size(), i);

                    return list.subList(j, list.size()).iterator();
                }
            };
        } else {
            return new FluentIterable<T>() {
                public Iterator<T> iterator() {
                    final Iterator iterator = iterable.iterator();

                    Iterators.advance(iterator, i);
                    return new Iterator<T>() {
                        boolean atStart = true;

                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        public T next() {
                            Object object = iterator.next();

                            this.atStart = false;
                            return object;
                        }

                        public void remove() {
                            CollectPreconditions.checkRemove(!this.atStart);
                            iterator.remove();
                        }
                    };
                }
            };
        }
    }

    public static <T> Iterable<T> limit(final Iterable<T> iterable, final int i) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument(i >= 0, "limit is negative");
        return new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return Iterators.limit(iterable.iterator(), i);
            }
        };
    }

    public static <T> Iterable<T> consumingIterable(final Iterable<T> iterable) {
        if (iterable instanceof Queue) {
            return new FluentIterable<T>() {
                public Iterator<T> iterator() {
                    return new Iterables.ConsumingQueueIterator<>((Queue) iterable);
                }
            };
        } else {
            Preconditions.checkNotNull(iterable);
            return new FluentIterable<T>() {
                public Iterator<T> iterator() {
                    return Iterators.consumingIterator(iterable.iterator());
                }
            };
        }
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        return iterable instanceof Collection ? ((Collection) iterable).isEmpty() : !iterable.iterator().hasNext();
    }

    @Beta
    public static <T> Iterable<T> mergeSorted(final Iterable<? extends Iterable<? extends T>> iterable, final Comparator<? super T> comparator) {
        Preconditions.checkNotNull(iterable, "iterables");
        Preconditions.checkNotNull(comparator, "comparator");
        FluentIterable fluentiterable = new FluentIterable<T>() {
            public Iterator<T> iterator() {
                return Iterators.mergeSorted(Iterables.transform(iterable, Iterables.toIterator()), comparator);
            }
        };

        return new Iterables.UnmodifiableIterable<>(fluentiterable);
    }

    private static <T> Function<Iterable<? extends T>, Iterator<? extends T>> toIterator() {
        return new Function<Iterable<? extends T>, Iterator<? extends T>>() {
            public Iterator<? extends T> apply(Iterable<? extends T> iterable) {
                return iterable.iterator();
            }
        };
    }

    private static class ConsumingQueueIterator<T> extends AbstractIterator<T> {

        private final Queue<T> queue;

        private ConsumingQueueIterator(Queue<T> queue) {
            this.queue = queue;
        }

        @Override
        public T computeNext() {
            try {
                return this.queue.remove();
            } catch (NoSuchElementException nosuchelementexception) {
                return this.endOfData();
            }
        }
    }

    private static final class UnmodifiableIterable<T> extends FluentIterable<T> {

        private final Iterable<T> iterable;

        private UnmodifiableIterable(Iterable<T> iterable) {
            this.iterable = iterable;
        }

        public Iterator<T> iterator() {
            return Iterators.unmodifiableIterator(this.iterable.iterator());
        }

        @Override
        public String toString() {
            return this.iterable.toString();
        }
    }
}
