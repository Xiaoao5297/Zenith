package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

@Beta
@GwtIncompatible("hasn't been tested yet")
public abstract class ImmutableSortedMultiset<E> extends ImmutableSortedMultisetFauxverideShim<E> implements SortedMultiset<E> {

    private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
    private static final ImmutableSortedMultiset<Comparable> NATURAL_EMPTY_MULTISET = new EmptyImmutableSortedMultiset<>(ImmutableSortedMultiset.NATURAL_ORDER);
    transient ImmutableSortedMultiset<E> descendingMultiset;

    public static <E> ImmutableSortedMultiset<E> of() {
        return ImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e0) {
        RegularImmutableSortedSet regularimmutablesortedset = (RegularImmutableSortedSet) ImmutableSortedSet.of(e0);
        int[] aint = new int[]{1};
        long[] along = new long[]{0L, 1L};

        return new RegularImmutableSortedMultiset<>(regularimmutablesortedset, aint, along, 0, 1);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e0, E e1) {
        return copyOf(Ordering.natural(), (Iterable) Arrays.asList(e0, e1));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e0, E e1, E e2) {
        return copyOf(Ordering.natural(), (Iterable) Arrays.asList(e0, e1, e2));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e0, E e1, E e2, E e3) {
        return copyOf(Ordering.natural(), (Iterable) Arrays.asList(e0, e1, e2, e3));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e0, E e1, E e2, E e3, E e4) {
        return copyOf(Ordering.natural(), (Iterable) Arrays.asList(e0, e1, e2, e3, e4));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E... ae) {
        int i = ae.length + 6;
        ArrayList arraylist = Lists.newArrayListWithCapacity(i);

        Collections.addAll(arraylist, new Comparable[]{e0, e1, e2, e3, e4, e5});
        Collections.addAll(arraylist, ae);
        return copyOf(Ordering.natural(), (Iterable) arraylist);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] ae) {
        return copyOf(Ordering.natural(), (Iterable) Arrays.asList(ae));
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> iterable) {
        Ordering ordering = Ordering.natural();

        return copyOf(ordering, iterable);
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> iterator) {
        Ordering ordering = Ordering.natural();

        return copyOf(ordering, iterator);
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> iterator) {
        Preconditions.checkNotNull(comparator);
        return (new ImmutableSortedMultiset.Builder<>(comparator)).addAll(iterator).build();
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> iterable) {
        if (iterable instanceof ImmutableSortedMultiset) {
            ImmutableSortedMultiset immutablesortedmultiset = (ImmutableSortedMultiset) iterable;

            if (comparator.equals(immutablesortedmultiset.comparator())) {
                if (immutablesortedmultiset.isPartialView()) {
                    return copyOfSortedEntries(comparator, immutablesortedmultiset.entrySet().asList());
                }

                return immutablesortedmultiset;
            }
        }

        ArrayList arraylist = Lists.newArrayList(iterable);
        TreeMultiset treemultiset = TreeMultiset.create((Comparator) Preconditions.checkNotNull(comparator));

        Iterables.addAll(treemultiset, arraylist);
        return copyOfSortedEntries(comparator, treemultiset.entrySet());
    }

    public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> sortedmultiset) {
        return copyOfSortedEntries(sortedmultiset.comparator(), Lists.newArrayList((Iterable) sortedmultiset.entrySet()));
    }

    private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(Comparator<? super E> comparator, Collection<Multiset.Entry<E>> collection) {
        if (collection.isEmpty()) {
            return emptyMultiset(comparator);
        } else {
            ImmutableList.Builder immutablelist_builder = new ImmutableList.Builder<>(collection.size());
            int[] aint = new int[collection.size()];
            long[] along = new long[collection.size() + 1];
            int i = 0;

            for (Iterator iterator = collection.iterator(); iterator.hasNext(); ++i) {
                Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

                immutablelist_builder.add(multiset_entry.getElement());
                aint[i] = multiset_entry.getCount();
                along[i + 1] = along[i] + (long) aint[i];
            }

            return new RegularImmutableSortedMultiset<>(new RegularImmutableSortedSet<>(immutablelist_builder.build(), comparator), aint, along, 0, collection.size());
        }
    }

    static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> comparator) {
        return (ImmutableSortedMultiset) (ImmutableSortedMultiset.NATURAL_ORDER.equals(comparator) ? ImmutableSortedMultiset.NATURAL_EMPTY_MULTISET : new EmptyImmutableSortedMultiset<>(comparator));
    }

    ImmutableSortedMultiset() {}

    @Override
    public final Comparator<? super E> comparator() {
        return this.elementSet().comparator();
    }

    @Override
    public abstract ImmutableSortedSet<E> elementSet();

    @Override
    public ImmutableSortedMultiset<E> descendingMultiset() {
        ImmutableSortedMultiset immutablesortedmultiset = this.descendingMultiset;

        return immutablesortedmultiset == null ? (this.descendingMultiset = new DescendingImmutableSortedMultiset<>(this)) : immutablesortedmultiset;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final Multiset.Entry<E> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final Multiset.Entry<E> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract ImmutableSortedMultiset<E> headMultiset(E e0, BoundType boundtype);

    @Override
    public ImmutableSortedMultiset<E> subMultiset(E e0, BoundType boundtype, E e1, BoundType boundtype1) {
        Preconditions.checkArgument(this.comparator().compare(e0, e1) <= 0, "Expected lowerBound <= upperBound but %s > %s", e0, e1);
        return this.tailMultiset(e0, boundtype).headMultiset(e1, boundtype1);
    }

    @Override
    public abstract ImmutableSortedMultiset<E> tailMultiset(E e0, BoundType boundtype);

    public static <E> ImmutableSortedMultiset.Builder<E> orderedBy(Comparator<E> comparator) {
        return new ImmutableSortedMultiset.Builder<>(comparator);
    }

    public static <E extends Comparable<E>> ImmutableSortedMultiset.Builder<E> reverseOrder() {
        return new ImmutableSortedMultiset.Builder<>(Ordering.natural().reverse());
    }

    public static <E extends Comparable<E>> ImmutableSortedMultiset.Builder<E> naturalOrder() {
        return new ImmutableSortedMultiset.Builder<>(Ordering.natural());
    }

    @Override
    Object writeReplace() {
        return new ImmutableSortedMultiset.SerializedForm<>(this);
    }

    private static final class SerializedForm<E> implements Serializable {

        Comparator<? super E> comparator;
        E[] elements;
        int[] counts;

        SerializedForm(SortedMultiset<E> sortedmultiset) {
            this.comparator = sortedmultiset.comparator();
            int i = sortedmultiset.entrySet().size();

            this.elements = (Object[]) (new Object[i]);
            this.counts = new int[i];
            int j = 0;

            for (Iterator iterator = sortedmultiset.entrySet().iterator(); iterator.hasNext(); ++j) {
                Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

                this.elements[j] = multiset_entry.getElement();
                this.counts[j] = multiset_entry.getCount();
            }

        }

        Object readResolve() {
            int i = this.elements.length;
            ImmutableSortedMultiset.Builder immutablesortedmultiset_builder = new ImmutableSortedMultiset.Builder<>(this.comparator);

            for (int j = 0; j < i; ++j) {
                immutablesortedmultiset_builder.addCopies(this.elements[j], this.counts[j]);
            }

            return immutablesortedmultiset_builder.build();
        }
    }

    public static class Builder<E> extends ImmutableMultiset.Builder<E> {

        private final Comparator<? super E> comparator;

        public Builder(Comparator<? super E> comparator) {
            super(TreeMultiset.create(comparator));
            this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
        }

        @Override
        public ImmutableSortedMultiset.Builder<E> add(E e0) {
            super.add(e0);
            return this;
        }

        @Override
        public ImmutableSortedMultiset.Builder<E> addCopies(E e0, int i) {
            super.addCopies(e0, i);
            return this;
        }

        @Override
        public ImmutableSortedMultiset.Builder<E> setCount(E e0, int i) {
            super.setCount(e0, i);
            return this;
        }

        @Override
        public ImmutableSortedMultiset.Builder<E> add(E... ae) {
            super.add(ae);
            return this;
        }

        @Override
        public ImmutableSortedMultiset.Builder<E> addAll(Iterable<? extends E> iterable) {
            super.addAll(iterable);
            return this;
        }

        @Override
        public ImmutableSortedMultiset.Builder<E> addAll(Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }

        @Override
        public ImmutableSortedMultiset<E> build() {
            return ImmutableSortedMultiset.copyOfSorted((SortedMultiset) this.contents);
        }
    }
}
