package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Iterators {

    static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR = new UnmodifiableListIterator<Object>() {
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        public boolean hasPrevious() {
            return false;
        }

        public Object previous() {
            throw new NoSuchElementException();
        }

        public int nextIndex() {
            return 0;
        }

        public int previousIndex() {
            return -1;
        }
    };
    private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        public void remove() {
            CollectPreconditions.checkRemove(false);
        }
    };

    private Iterators() {}

    public static <T> UnmodifiableIterator<T> emptyIterator() {
        return emptyListIterator();
    }

    static <T> UnmodifiableListIterator<T> emptyListIterator() {
        return Iterators.EMPTY_LIST_ITERATOR;
    }

    static <T> Iterator<T> emptyModifiableIterator() {
        return Iterators.EMPTY_MODIFIABLE_ITERATOR;
    }

    public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        return iterator instanceof UnmodifiableIterator ? (UnmodifiableIterator) iterator : new UnmodifiableIterator<T>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public T next() {
                return iterator.next();
            }
        };
    }

    /** @deprecated */
    @Deprecated
    public static <T> UnmodifiableIterator<T> unmodifiableIterator(UnmodifiableIterator<T> unmodifiableiterator) {
        return (UnmodifiableIterator) Preconditions.checkNotNull(unmodifiableiterator);
    }

    public static int size(Iterator<?> iterator) {
        int i;

        for (i = 0; iterator.hasNext(); ++i) {
            iterator.next();
        }

        return i;
    }

    public static boolean contains(Iterator<?> iterator, @Nullable Object object) {
        return any(iterator, Predicates.equalTo(object));
    }

    public static boolean removeAll(Iterator<?> iterator, Collection<?> collection) {
        return removeIf(iterator, Predicates.in(collection));
    }

    public static <T> boolean removeIf(Iterator<T> iterator, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        boolean flag = false;

        while (iterator.hasNext()) {
            if (predicate.apply(iterator.next())) {
                iterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    public static boolean retainAll(Iterator<?> iterator, Collection<?> collection) {
        return removeIf(iterator, Predicates.not(Predicates.in(collection)));
    }

    public static boolean elementsEqual(Iterator<?> iterator, Iterator<?> iterator1) {
        while (true) {
            if (iterator.hasNext()) {
                if (!iterator1.hasNext()) {
                    return false;
                }

                Object object = iterator.next();
                Object object1 = iterator1.next();

                if (Objects.equal(object, object1)) {
                    continue;
                }

                return false;
            }

            return !iterator1.hasNext();
        }
    }

    public static String toString(Iterator<?> iterator) {
        return Collections2.STANDARD_JOINER.appendTo((new StringBuilder()).append('['), iterator).append(']').toString();
    }

    public static <T> T getOnlyElement(Iterator<T> iterator) {
        Object object = iterator.next();

        if (!iterator.hasNext()) {
            return object;
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            stringbuilder.append("expected one element but was: <" + object);

            for (int i = 0; i < 4 && iterator.hasNext(); ++i) {
                stringbuilder.append(", " + iterator.next());
            }

            if (iterator.hasNext()) {
                stringbuilder.append(", ...");
            }

            stringbuilder.append('>');
            throw new IllegalArgumentException(stringbuilder.toString());
        }
    }

    @Nullable
    public static <T> T getOnlyElement(Iterator<? extends T> iterator, @Nullable T t0) {
        return iterator.hasNext() ? getOnlyElement(iterator) : t0;
    }

    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] toArray(Iterator<? extends T> iterator, Class<T> oclass) {
        ArrayList arraylist = Lists.newArrayList(iterator);

        return Iterables.toArray(arraylist, oclass);
    }

    public static <T> boolean addAll(Collection<T> collection, Iterator<? extends T> iterator) {
        Preconditions.checkNotNull(collection);
        Preconditions.checkNotNull(iterator);

        boolean flag;

        for (flag = false; iterator.hasNext(); flag |= collection.add(iterator.next())) {
            ;
        }

        return flag;
    }

    public static int frequency(Iterator<?> iterator, @Nullable Object object) {
        return size(filter(iterator, Predicates.equalTo(object)));
    }

    public static <T> Iterator<T> cycle(final Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        return new Iterator<T>() {
            Iterator<T> iterator = Iterators.emptyIterator();
            Iterator<T> removeFrom;

            public boolean hasNext() {
                if (!this.iterator.hasNext()) {
                    this.iterator = iterable.iterator();
                }

                return this.iterator.hasNext();
            }

            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    this.removeFrom = this.iterator;
                    return this.iterator.next();
                }
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.removeFrom != null);
                this.removeFrom.remove();
                this.removeFrom = null;
            }
        };
    }

    public static <T> Iterator<T> cycle(T... at) {
        return cycle((Iterable) Lists.newArrayList(at));
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> iterator, Iterator<? extends T> iterator1) {
        return concat((Iterator) ImmutableList.of(iterator, iterator1).iterator());
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> iterator, Iterator<? extends T> iterator1, Iterator<? extends T> iterator2) {
        return concat((Iterator) ImmutableList.of(iterator, iterator1, iterator2).iterator());
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> iterator, Iterator<? extends T> iterator1, Iterator<? extends T> iterator2, Iterator<? extends T> iterator3) {
        return concat((Iterator) ImmutableList.of(iterator, iterator1, iterator2, iterator3).iterator());
    }

    public static <T> Iterator<T> concat(Iterator<? extends T>... aiterator) {
        return concat((Iterator) ImmutableList.copyOf((Object[]) aiterator).iterator());
    }

    public static <T> Iterator<T> concat(final Iterator<? extends Iterator<? extends T>> iterator) {
        Preconditions.checkNotNull(iterator);
        return new Iterator<T>() {
            Iterator<? extends T> current = Iterators.emptyIterator();
            Iterator<? extends T> removeFrom;

            public boolean hasNext() {
                boolean flag;

                while (!(flag = ((Iterator) Preconditions.checkNotNull(this.current)).hasNext()) && iterator.hasNext()) {
                    this.current = (Iterator) iterator.next();
                }

                return flag;
            }

            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    this.removeFrom = this.current;
                    return this.current.next();
                }
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.removeFrom != null);
                this.removeFrom.remove();
                this.removeFrom = null;
            }
        };
    }

    public static <T> UnmodifiableIterator<List<T>> partition(Iterator<T> iterator, int i) {
        return partitionImpl(iterator, i, false);
    }

    public static <T> UnmodifiableIterator<List<T>> paddedPartition(Iterator<T> iterator, int i) {
        return partitionImpl(iterator, i, true);
    }

    private static <T> UnmodifiableIterator<List<T>> partitionImpl(final Iterator<T> iterator, final int i, final boolean flag) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkArgument(i > 0);
        return new UnmodifiableIterator<List<T>>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public List<T> next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    Object[] aobject = new Object[i];

                    int j;

                    for (j = 0; j < i && iterator.hasNext(); ++j) {
                        aobject[j] = iterator.next();
                    }

                    for (int k = j; k < i; ++k) {
                        aobject[k] = null;
                    }

                    List list = Collections.unmodifiableList(Arrays.asList(aobject));

                    return !flag && j != i ? list.subList(0, j) : list;
                }
            }
        };
    }

    public static <T> UnmodifiableIterator<T> filter(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkNotNull(predicate);
        return new AbstractIterator<T>() {
            @Override
            protected T computeNext() {
                while (true) {
                    if (iterator.hasNext()) {
                        Object object = iterator.next();

                        if (!predicate.apply(object)) {
                            continue;
                        }

                        return object;
                    }

                    return this.endOfData();
                }
            }
        };
    }

    @GwtIncompatible("Class.isInstance")
    public static <T> UnmodifiableIterator<T> filter(Iterator<?> iterator, Class<T> oclass) {
        return filter(iterator, Predicates.instanceOf(oclass));
    }

    public static <T> boolean any(Iterator<T> iterator, Predicate<? super T> predicate) {
        return indexOf(iterator, predicate) != -1;
    }

    public static <T> boolean all(Iterator<T> iterator, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);

        Object object;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            object = iterator.next();
        } while (predicate.apply(object));

        return false;
    }

    public static <T> T find(Iterator<T> iterator, Predicate<? super T> predicate) {
        return filter(iterator, predicate).next();
    }

    @Nullable
    public static <T> T find(Iterator<? extends T> iterator, Predicate<? super T> predicate, @Nullable T t0) {
        return getNext(filter(iterator, predicate), t0);
    }

    public static <T> Optional<T> tryFind(Iterator<T> iterator, Predicate<? super T> predicate) {
        UnmodifiableIterator unmodifiableiterator = filter(iterator, predicate);

        return unmodifiableiterator.hasNext() ? Optional.of(unmodifiableiterator.next()) : Optional.absent();
    }

    public static <T> int indexOf(Iterator<T> iterator, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate, "predicate");

        for (int i = 0; iterator.hasNext(); ++i) {
            Object object = iterator.next();

            if (predicate.apply(object)) {
                return i;
            }
        }

        return -1;
    }

    public static <F, T> Iterator<T> transform(Iterator<F> iterator, final Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(function);
        return new TransformedIterator<F, T>(iterator) {
            @Override
            T transform(F f0) {
                return function.apply(f0);
            }
        };
    }

    public static <T> T get(Iterator<T> iterator, int i) {
        checkNonnegative(i);
        int j = advance(iterator, i);

        if (!iterator.hasNext()) {
            throw new IndexOutOfBoundsException("position (" + i + ") must be less than the number of elements that remained (" + j + ")");
        } else {
            return iterator.next();
        }
    }

    static void checkNonnegative(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("position (" + i + ") must not be negative");
        }
    }

    @Nullable
    public static <T> T get(Iterator<? extends T> iterator, int i, @Nullable T t0) {
        checkNonnegative(i);
        advance(iterator, i);
        return getNext(iterator, t0);
    }

    @Nullable
    public static <T> T getNext(Iterator<? extends T> iterator, @Nullable T t0) {
        return iterator.hasNext() ? iterator.next() : t0;
    }

    public static <T> T getLast(Iterator<T> iterator) {
        Object object;

        do {
            object = iterator.next();
        } while (iterator.hasNext());

        return object;
    }

    @Nullable
    public static <T> T getLast(Iterator<? extends T> iterator, @Nullable T t0) {
        return iterator.hasNext() ? getLast(iterator) : t0;
    }

    public static int advance(Iterator<?> iterator, int i) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkArgument(i >= 0, "numberToAdvance must be nonnegative");

        int j;

        for (j = 0; j < i && iterator.hasNext(); ++j) {
            iterator.next();
        }

        return j;
    }

    public static <T> Iterator<T> limit(final Iterator<T> iterator, final int i) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkArgument(i >= 0, "limit is negative");
        return new Iterator<T>() {
            private int count;

            public boolean hasNext() {
                return this.count < i && iterator.hasNext();
            }

            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    ++this.count;
                    return iterator.next();
                }
            }

            public void remove() {
                iterator.remove();
            }
        };
    }

    public static <T> Iterator<T> consumingIterator(final Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        return new UnmodifiableIterator<T>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public T next() {
                Object object = iterator.next();

                iterator.remove();
                return object;
            }
        };
    }

    @Nullable
    static <T> T pollNext(Iterator<T> iterator) {
        if (iterator.hasNext()) {
            Object object = iterator.next();

            iterator.remove();
            return object;
        } else {
            return null;
        }
    }

    static void clear(Iterator<?> iterator) {
        Preconditions.checkNotNull(iterator);

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

    }

    public static <T> UnmodifiableIterator<T> forArray(T... at) {
        return forArray(at, 0, at.length, 0);
    }

    static <T> UnmodifiableListIterator<T> forArray(final T[] at, final int i, int j, int k) {
        Preconditions.checkArgument(j >= 0);
        int l = i + j;

        Preconditions.checkPositionIndexes(i, l, at.length);
        Preconditions.checkPositionIndex(k, j);
        return (UnmodifiableListIterator) (j == 0 ? emptyListIterator() : new AbstractIndexedListIterator<T>(j, k) {
            @Override
            protected T get(int i1) {
                return at[i + i1];
            }
        });
    }

    public static <T> UnmodifiableIterator<T> singletonIterator(@Nullable final T t0) {
        return new UnmodifiableIterator<T>() {
            boolean done;

            public boolean hasNext() {
                return !this.done;
            }

            public T next() {
                if (this.done) {
                    throw new NoSuchElementException();
                } else {
                    this.done = true;
                    return t0;
                }
            }
        };
    }

    public static <T> UnmodifiableIterator<T> forEnumeration(final Enumeration<T> enumeration) {
        Preconditions.checkNotNull(enumeration);
        return new UnmodifiableIterator<T>() {
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            public T next() {
                return enumeration.nextElement();
            }
        };
    }

    public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        return new Enumeration<T>() {
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            public T nextElement() {
                return iterator.next();
            }
        };
    }

    public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> iterator) {
        if (iterator instanceof Iterators.PeekingImpl) {
            Iterators.PeekingImpl iterators_peekingimpl = (Iterators.PeekingImpl) iterator;

            return iterators_peekingimpl;
        } else {
            return new Iterators.PeekingImpl<>(iterator);
        }
    }

    /** @deprecated */
    @Deprecated
    public static <T> PeekingIterator<T> peekingIterator(PeekingIterator<T> peekingiterator) {
        return (PeekingIterator) Preconditions.checkNotNull(peekingiterator);
    }

    @Beta
    public static <T> UnmodifiableIterator<T> mergeSorted(Iterable<? extends Iterator<? extends T>> iterable, Comparator<? super T> comparator) {
        Preconditions.checkNotNull(iterable, "iterators");
        Preconditions.checkNotNull(comparator, "comparator");
        return new Iterators.MergingIterator<>(iterable, comparator);
    }

    static <T> ListIterator<T> cast(Iterator<T> iterator) {
        return (ListIterator) iterator;
    }

    private static class MergingIterator<T> extends UnmodifiableIterator<T> {

        final Queue<PeekingIterator<T>> queue;

        public MergingIterator(Iterable<? extends Iterator<? extends T>> iterable, final Comparator<? super T> comparator) {
            Comparator comparator1 = new Comparator<PeekingIterator<T>>() {
                public int compare(PeekingIterator<T> peekingiterator, PeekingIterator<T> peekingiterator1) {
                    return comparator.compare(peekingiterator.peek(), peekingiterator1.peek());
                }
            };

            this.queue = new PriorityQueue(2, comparator1);
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Iterator iterator1 = (Iterator) iterator.next();

                if (iterator1.hasNext()) {
                    this.queue.add(Iterators.peekingIterator(iterator1));
                }
            }

        }

        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        public T next() {
            PeekingIterator peekingiterator = (PeekingIterator) this.queue.remove();
            Object object = peekingiterator.next();

            if (peekingiterator.hasNext()) {
                this.queue.add(peekingiterator);
            }

            return object;
        }
    }

    private static class PeekingImpl<E> implements PeekingIterator<E> {

        private final Iterator<? extends E> iterator;
        private boolean hasPeeked;
        private E peekedElement;

        public PeekingImpl(Iterator<? extends E> iterator) {
            this.iterator = (Iterator) Preconditions.checkNotNull(iterator);
        }

        public boolean hasNext() {
            return this.hasPeeked || this.iterator.hasNext();
        }

        @Override
        public E next() {
            if (!this.hasPeeked) {
                return this.iterator.next();
            } else {
                Object object = this.peekedElement;

                this.hasPeeked = false;
                this.peekedElement = null;
                return object;
            }
        }

        @Override
        public void remove() {
            Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
            this.iterator.remove();
        }

        @Override
        public E peek() {
            if (!this.hasPeeked) {
                this.peekedElement = this.iterator.next();
                this.hasPeeked = true;
            }

            return this.peekedElement;
        }
    }
}
