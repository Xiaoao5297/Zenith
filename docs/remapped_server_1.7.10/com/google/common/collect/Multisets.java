package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public final class Multisets {

    private static final Ordering<Multiset.Entry<?>> DECREASING_COUNT_ORDERING = new Ordering<Multiset.Entry<?>>() {
        public int compare(Multiset.Entry<?> multiset_entry, Multiset.Entry<?> multiset_entry1) {
            return Ints.compare(multiset_entry1.getCount(), multiset_entry.getCount());
        }
    };

    private Multisets() {}

    public static <E> Multiset<E> unmodifiableMultiset(Multiset<? extends E> multiset) {
        return (Multiset) (!(multiset instanceof Multisets.UnmodifiableMultiset) && !(multiset instanceof ImmutableMultiset) ? new Multisets.UnmodifiableMultiset<>((Multiset) Preconditions.checkNotNull(multiset)) : multiset);
    }

    /** @deprecated */
    @Deprecated
    public static <E> Multiset<E> unmodifiableMultiset(ImmutableMultiset<E> immutablemultiset) {
        return (Multiset) Preconditions.checkNotNull(immutablemultiset);
    }

    @Beta
    public static <E> SortedMultiset<E> unmodifiableSortedMultiset(SortedMultiset<E> sortedmultiset) {
        return new UnmodifiableSortedMultiset<>((SortedMultiset) Preconditions.checkNotNull(sortedmultiset));
    }

    public static <E> Multiset.Entry<E> immutableEntry(@Nullable E e0, int i) {
        return new Multisets.ImmutableEntry<>(e0, i);
    }

    @Beta
    public static <E> Multiset<E> filter(Multiset<E> multiset, Predicate<? super E> predicate) {
        if (multiset instanceof Multisets.FilteredMultiset) {
            Multisets.FilteredMultiset multisets_filteredmultiset = (Multisets.FilteredMultiset) multiset;
            Predicate predicate1 = Predicates.and(multisets_filteredmultiset.predicate, predicate);

            return new Multisets.FilteredMultiset<>(multisets_filteredmultiset.unfiltered, predicate1);
        } else {
            return new Multisets.FilteredMultiset<>(multiset, predicate);
        }
    }

    static int inferDistinctElements(Iterable<?> iterable) {
        return iterable instanceof Multiset ? ((Multiset) iterable).elementSet().size() : 11;
    }

    @Beta
    public static <E> Multiset<E> union(final Multiset<? extends E> multiset, final Multiset<? extends E> multiset1) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset1);
        return new AbstractMultiset<E>() {
            @Override
            public boolean contains(@Nullable Object object) {
                return multiset.contains(object) || multiset1.contains(object);
            }

            @Override
            public boolean isEmpty() {
                return multiset.isEmpty() && multiset1.isEmpty();
            }

            @Override
            public int count(Object object) {
                return Math.max(multiset.count(object), multiset1.count(object));
            }

            @Override
            Set<E> createElementSet() {
                return Sets.union(multiset.elementSet(), multiset1.elementSet());
            }

            @Override
            Iterator<Multiset.Entry<E>> entryIterator() {
                final Iterator iterator = multiset.entrySet().iterator();
                final Iterator iterator1 = multiset1.entrySet().iterator();

                return new AbstractIterator<Multiset.Entry<E>>() {
                    @Override
                    protected Multiset.Entry<E> computeNext() {
                        Multiset.Entry multiset_entry;
                        Object object;

                        if (iterator.hasNext()) {
                            multiset_entry = (Multiset.Entry) iterator.next();
                            object = multiset_entry.getElement();
                            int i = Math.max(multiset_entry.getCount(), multiset1.count(object));

                            return Multisets.immutableEntry(object, i);
                        } else {
                            do {
                                if (!iterator1.hasNext()) {
                                    return (Multiset.Entry) this.endOfData();
                                }

                                multiset_entry = (Multiset.Entry) iterator1.next();
                                object = multiset_entry.getElement();
                            } while (multiset.contains(object));

                            return Multisets.immutableEntry(object, multiset_entry.getCount());
                        }
                    }
                };
            }

            @Override
            int distinctElements() {
                return this.elementSet().size();
            }
        };
    }

    public static <E> Multiset<E> intersection(final Multiset<E> multiset, final Multiset<?> multiset1) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset1);
        return new AbstractMultiset<E>() {
            @Override
            public int count(Object object) {
                int i = multiset.count(object);

                return i == 0 ? 0 : Math.min(i, multiset1.count(object));
            }

            @Override
            Set<E> createElementSet() {
                return Sets.intersection(multiset.elementSet(), multiset1.elementSet());
            }

            @Override
            Iterator<Multiset.Entry<E>> entryIterator() {
                final Iterator iterator = multiset.entrySet().iterator();

                return new AbstractIterator<Multiset.Entry<E>>() {
                    @Override
                    protected Multiset.Entry<E> computeNext() {
                        while (true) {
                            if (iterator.hasNext()) {
                                Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();
                                Object object = multiset_entry.getElement();
                                int i = Math.min(multiset_entry.getCount(), multiset1.count(object));

                                if (i <= 0) {
                                    continue;
                                }

                                return Multisets.immutableEntry(object, i);
                            }

                            return (Multiset.Entry) this.endOfData();
                        }
                    }
                };
            }

            @Override
            int distinctElements() {
                return this.elementSet().size();
            }
        };
    }

    @Beta
    public static <E> Multiset<E> sum(final Multiset<? extends E> multiset, final Multiset<? extends E> multiset1) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset1);
        return new AbstractMultiset<E>() {
            @Override
            public boolean contains(@Nullable Object object) {
                return multiset.contains(object) || multiset1.contains(object);
            }

            @Override
            public boolean isEmpty() {
                return multiset.isEmpty() && multiset1.isEmpty();
            }

            @Override
            public int size() {
                return multiset.size() + multiset1.size();
            }

            @Override
            public int count(Object object) {
                return multiset.count(object) + multiset1.count(object);
            }

            @Override
            Set<E> createElementSet() {
                return Sets.union(multiset.elementSet(), multiset1.elementSet());
            }

            @Override
            Iterator<Multiset.Entry<E>> entryIterator() {
                final Iterator iterator = multiset.entrySet().iterator();
                final Iterator iterator1 = multiset1.entrySet().iterator();

                return new AbstractIterator<Multiset.Entry<E>>() {
                    @Override
                    protected Multiset.Entry<E> computeNext() {
                        Multiset.Entry multiset_entry;
                        Object object;

                        if (iterator.hasNext()) {
                            multiset_entry = (Multiset.Entry) iterator.next();
                            object = multiset_entry.getElement();
                            int i = multiset_entry.getCount() + multiset1.count(object);

                            return Multisets.immutableEntry(object, i);
                        } else {
                            do {
                                if (!iterator1.hasNext()) {
                                    return (Multiset.Entry) this.endOfData();
                                }

                                multiset_entry = (Multiset.Entry) iterator1.next();
                                object = multiset_entry.getElement();
                            } while (multiset.contains(object));

                            return Multisets.immutableEntry(object, multiset_entry.getCount());
                        }
                    }
                };
            }

            @Override
            int distinctElements() {
                return this.elementSet().size();
            }
        };
    }

    @Beta
    public static <E> Multiset<E> difference(final Multiset<E> multiset, final Multiset<?> multiset1) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset1);
        return new AbstractMultiset<E>() {
            @Override
            public int count(@Nullable Object object) {
                int i = multiset.count(object);

                return i == 0 ? 0 : Math.max(0, i - multiset1.count(object));
            }

            @Override
            Iterator<Multiset.Entry<E>> entryIterator() {
                final Iterator iterator = multiset.entrySet().iterator();

                return new AbstractIterator<Multiset.Entry<E>>() {
                    @Override
                    protected Multiset.Entry<E> computeNext() {
                        while (true) {
                            if (iterator.hasNext()) {
                                Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();
                                Object object = multiset_entry.getElement();
                                int i = multiset_entry.getCount() - multiset1.count(object);

                                if (i <= 0) {
                                    continue;
                                }

                                return Multisets.immutableEntry(object, i);
                            }

                            return (Multiset.Entry) this.endOfData();
                        }
                    }
                };
            }

            @Override
            int distinctElements() {
                return Iterators.size(this.entryIterator());
            }
        };
    }

    public static boolean containsOccurrences(Multiset<?> multiset, Multiset<?> multiset1) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset1);
        Iterator iterator = multiset1.entrySet().iterator();

        Multiset.Entry multiset_entry;
        int i;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            multiset_entry = (Multiset.Entry) iterator.next();
            i = multiset.count(multiset_entry.getElement());
        } while (i >= multiset_entry.getCount());

        return false;
    }

    public static boolean retainOccurrences(Multiset<?> multiset, Multiset<?> multiset1) {
        return retainOccurrencesImpl(multiset, multiset1);
    }

    private static <E> boolean retainOccurrencesImpl(Multiset<E> multiset, Multiset<?> multiset1) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset1);
        Iterator iterator = multiset.entrySet().iterator();
        boolean flag = false;

        while (iterator.hasNext()) {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();
            int i = multiset1.count(multiset_entry.getElement());

            if (i == 0) {
                iterator.remove();
                flag = true;
            } else if (i < multiset_entry.getCount()) {
                multiset.setCount(multiset_entry.getElement(), i);
                flag = true;
            }
        }

        return flag;
    }

    public static boolean removeOccurrences(Multiset<?> multiset, Multiset<?> multiset1) {
        return removeOccurrencesImpl(multiset, multiset1);
    }

    private static <E> boolean removeOccurrencesImpl(Multiset<E> multiset, Multiset<?> multiset1) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset1);
        boolean flag = false;
        Iterator iterator = multiset.entrySet().iterator();

        while (iterator.hasNext()) {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();
            int i = multiset1.count(multiset_entry.getElement());

            if (i >= multiset_entry.getCount()) {
                iterator.remove();
                flag = true;
            } else if (i > 0) {
                multiset.remove(multiset_entry.getElement(), i);
                flag = true;
            }
        }

        return flag;
    }

    static boolean equalsImpl(Multiset<?> multiset, @Nullable Object object) {
        if (object == multiset) {
            return true;
        } else if (object instanceof Multiset) {
            Multiset multiset1 = (Multiset) object;

            if (multiset.size() == multiset1.size() && multiset.entrySet().size() == multiset1.entrySet().size()) {
                Iterator iterator = multiset1.entrySet().iterator();

                Multiset.Entry multiset_entry;

                do {
                    if (!iterator.hasNext()) {
                        return true;
                    }

                    multiset_entry = (Multiset.Entry) iterator.next();
                } while (multiset.count(multiset_entry.getElement()) == multiset_entry.getCount());

                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    static <E> boolean addAllImpl(Multiset<E> multiset, Collection<? extends E> collection) {
        if (collection.isEmpty()) {
            return false;
        } else {
            if (collection instanceof Multiset) {
                Multiset multiset1 = cast(collection);
                Iterator iterator = multiset1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

                    multiset.add(multiset_entry.getElement(), multiset_entry.getCount());
                }
            } else {
                Iterators.addAll(multiset, collection.iterator());
            }

            return true;
        }
    }

    static boolean removeAllImpl(Multiset<?> multiset, Collection<?> collection) {
        Object object = collection instanceof Multiset ? ((Multiset) collection).elementSet() : collection;

        return multiset.elementSet().removeAll((Collection) object);
    }

    static boolean retainAllImpl(Multiset<?> multiset, Collection<?> collection) {
        Preconditions.checkNotNull(collection);
        Object object = collection instanceof Multiset ? ((Multiset) collection).elementSet() : collection;

        return multiset.elementSet().retainAll((Collection) object);
    }

    static <E> int setCountImpl(Multiset<E> multiset, E e0, int i) {
        CollectPreconditions.checkNonnegative(i, "count");
        int j = multiset.count(e0);
        int k = i - j;

        if (k > 0) {
            multiset.add(e0, k);
        } else if (k < 0) {
            multiset.remove(e0, -k);
        }

        return j;
    }

    static <E> boolean setCountImpl(Multiset<E> multiset, E e0, int i, int j) {
        CollectPreconditions.checkNonnegative(i, "oldCount");
        CollectPreconditions.checkNonnegative(j, "newCount");
        if (multiset.count(e0) == i) {
            multiset.setCount(e0, j);
            return true;
        } else {
            return false;
        }
    }

    static <E> Iterator<E> iteratorImpl(Multiset<E> multiset) {
        return new Multisets.MultisetIteratorImpl<>(multiset, multiset.entrySet().iterator());
    }

    static int sizeImpl(Multiset<?> multiset) {
        long i = 0L;

        Multiset.Entry multiset_entry;

        for (Iterator iterator = multiset.entrySet().iterator(); iterator.hasNext(); i += (long) multiset_entry.getCount()) {
            multiset_entry = (Multiset.Entry) iterator.next();
        }

        return Ints.saturatedCast(i);
    }

    static <T> Multiset<T> cast(Iterable<T> iterable) {
        return (Multiset) iterable;
    }

    @Beta
    public static <E> ImmutableMultiset<E> copyHighestCountFirst(Multiset<E> multiset) {
        ImmutableList immutablelist = Multisets.DECREASING_COUNT_ORDERING.immutableSortedCopy(multiset.entrySet());

        return ImmutableMultiset.copyFromEntries(immutablelist);
    }

    static final class MultisetIteratorImpl<E> implements Iterator<E> {

        private final Multiset<E> multiset;
        private final Iterator<Multiset.Entry<E>> entryIterator;
        private Multiset.Entry<E> currentEntry;
        private int laterCount;
        private int totalCount;
        private boolean canRemove;

        MultisetIteratorImpl(Multiset<E> multiset, Iterator<Multiset.Entry<E>> iterator) {
            this.multiset = multiset;
            this.entryIterator = iterator;
        }

        public boolean hasNext() {
            return this.laterCount > 0 || this.entryIterator.hasNext();
        }

        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                if (this.laterCount == 0) {
                    this.currentEntry = (Multiset.Entry) this.entryIterator.next();
                    this.totalCount = this.laterCount = this.currentEntry.getCount();
                }

                --this.laterCount;
                this.canRemove = true;
                return this.currentEntry.getElement();
            }
        }

        public void remove() {
            CollectPreconditions.checkRemove(this.canRemove);
            if (this.totalCount == 1) {
                this.entryIterator.remove();
            } else {
                this.multiset.remove(this.currentEntry.getElement());
            }

            --this.totalCount;
            this.canRemove = false;
        }
    }

    abstract static class EntrySet<E> extends Sets.ImprovedAbstractSet<Multiset.Entry<E>> {

        EntrySet() {}

        abstract Multiset<E> multiset();

        public boolean contains(@Nullable Object object) {
            if (object instanceof Multiset.Entry) {
                Multiset.Entry multiset_entry = (Multiset.Entry) object;

                if (multiset_entry.getCount() <= 0) {
                    return false;
                } else {
                    int i = this.multiset().count(multiset_entry.getElement());

                    return i == multiset_entry.getCount();
                }
            } else {
                return false;
            }
        }

        public boolean remove(Object object) {
            if (object instanceof Multiset.Entry) {
                Multiset.Entry multiset_entry = (Multiset.Entry) object;
                Object object1 = multiset_entry.getElement();
                int i = multiset_entry.getCount();

                if (i != 0) {
                    Multiset multiset = this.multiset();

                    return multiset.setCount(object1, i, 0);
                }
            }

            return false;
        }

        public void clear() {
            this.multiset().clear();
        }
    }

    abstract static class ElementSet<E> extends Sets.ImprovedAbstractSet<E> {

        ElementSet() {}

        abstract Multiset<E> multiset();

        public void clear() {
            this.multiset().clear();
        }

        public boolean contains(Object object) {
            return this.multiset().contains(object);
        }

        public boolean containsAll(Collection<?> collection) {
            return this.multiset().containsAll(collection);
        }

        public boolean isEmpty() {
            return this.multiset().isEmpty();
        }

        public Iterator<E> iterator() {
            return new TransformedIterator<Multiset.Entry<E>, E>(this.multiset().entrySet().iterator()) {
                E transform(Multiset.Entry<E> multiset_entry) {
                    return multiset_entry.getElement();
                }
            };
        }

        public boolean remove(Object object) {
            int i = this.multiset().count(object);

            if (i > 0) {
                this.multiset().remove(object, i);
                return true;
            } else {
                return false;
            }
        }

        public int size() {
            return this.multiset().entrySet().size();
        }
    }

    abstract static class AbstractEntry<E> implements Multiset.Entry<E> {

        AbstractEntry() {}

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Multiset.Entry)) {
                return false;
            } else {
                Multiset.Entry multiset_entry = (Multiset.Entry) object;

                return this.getCount() == multiset_entry.getCount() && Objects.equal(this.getElement(), multiset_entry.getElement());
            }
        }

        @Override
        public int hashCode() {
            Object object = this.getElement();

            return (object == null ? 0 : object.hashCode()) ^ this.getCount();
        }

        @Override
        public String toString() {
            String s = String.valueOf(this.getElement());
            int i = this.getCount();

            return i == 1 ? s : s + " x " + i;
        }
    }

    private static final class FilteredMultiset<E> extends AbstractMultiset<E> {

        final Multiset<E> unfiltered;
        final Predicate<? super E> predicate;

        FilteredMultiset(Multiset<E> multiset, Predicate<? super E> predicate) {
            this.unfiltered = (Multiset) Preconditions.checkNotNull(multiset);
            this.predicate = (Predicate) Preconditions.checkNotNull(predicate);
        }

        @Override
        public UnmodifiableIterator<E> iterator() {
            return Iterators.filter(this.unfiltered.iterator(), this.predicate);
        }

        @Override
        Set<E> createElementSet() {
            return Sets.filter(this.unfiltered.elementSet(), this.predicate);
        }

        @Override
        Set<Multiset.Entry<E>> createEntrySet() {
            return Sets.filter(this.unfiltered.entrySet(), new Predicate<Multiset.Entry<E>>() {
                public boolean apply(Multiset.Entry<E> multiset_entry) {
                    return FilteredMultiset.this.predicate.apply(multiset_entry.getElement());
                }
            });
        }

        @Override
        Iterator<Multiset.Entry<E>> entryIterator() {
            throw new AssertionError("should never be called");
        }

        @Override
        int distinctElements() {
            return this.elementSet().size();
        }

        @Override
        public int count(@Nullable Object object) {
            int i = this.unfiltered.count(object);

            return i > 0 ? (this.predicate.apply(object) ? i : 0) : 0;
        }

        @Override
        public int add(@Nullable E e0, int i) {
            Preconditions.checkArgument(this.predicate.apply(e0), "Element %s does not match predicate %s", e0, this.predicate);
            return this.unfiltered.add(e0, i);
        }

        @Override
        public int remove(@Nullable Object object, int i) {
            CollectPreconditions.checkNonnegative(i, "occurrences");
            return i == 0 ? this.count(object) : (this.contains(object) ? this.unfiltered.remove(object, i) : 0);
        }

        @Override
        public void clear() {
            this.elementSet().clear();
        }
    }

    static final class ImmutableEntry<E> extends Multisets.AbstractEntry<E> implements Serializable {

        @Nullable
        final E element;
        final int count;
        private static final long serialVersionUID = 0L;

        ImmutableEntry(@Nullable E e0, int i) {
            this.element = e0;
            this.count = i;
            CollectPreconditions.checkNonnegative(i, "count");
        }

        @Nullable
        @Override
        public E getElement() {
            return this.element;
        }

        @Override
        public int getCount() {
            return this.count;
        }
    }

    static class UnmodifiableMultiset<E> extends ForwardingMultiset<E> implements Serializable {

        final Multiset<? extends E> delegate;
        transient Set<E> elementSet;
        transient Set<Multiset.Entry<E>> entrySet;
        private static final long serialVersionUID = 0L;

        UnmodifiableMultiset(Multiset<? extends E> multiset) {
            this.delegate = multiset;
        }

        @Override
        protected Multiset<E> delegate() {
            return this.delegate;
        }

        Set<E> createElementSet() {
            return Collections.unmodifiableSet(this.delegate.elementSet());
        }

        @Override
        public Set<E> elementSet() {
            Set set = this.elementSet;

            return set == null ? (this.elementSet = this.createElementSet()) : set;
        }

        @Override
        public Set<Multiset.Entry<E>> entrySet() {
            Set set = this.entrySet;

            return set == null ? (this.entrySet = Collections.unmodifiableSet(this.delegate.entrySet())) : set;
        }

        @Override
        public Iterator<E> iterator() {
            return Iterators.unmodifiableIterator(this.delegate.iterator());
        }

        @Override
        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int add(E e0, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remove(Object object, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setCount(E e0, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setCount(E e0, int i, int j) {
            throw new UnsupportedOperationException();
        }
    }
}
