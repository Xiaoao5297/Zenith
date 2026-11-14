package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E> implements NavigableSet<E>, SortedIterable<E> {

    private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
    private static final ImmutableSortedSet<Comparable> NATURAL_EMPTY_SET = new EmptyImmutableSortedSet<>(ImmutableSortedSet.NATURAL_ORDER);
    final transient Comparator<? super E> comparator;
    @GwtIncompatible("NavigableSet")
    transient ImmutableSortedSet<E> descendingSet;

    private static <E> ImmutableSortedSet<E> emptySet() {
        return ImmutableSortedSet.NATURAL_EMPTY_SET;
    }

    static <E> ImmutableSortedSet<E> emptySet(Comparator<? super E> comparator) {
        return (ImmutableSortedSet) (ImmutableSortedSet.NATURAL_ORDER.equals(comparator) ? emptySet() : new EmptyImmutableSortedSet<>(comparator));
    }

    public static <E> ImmutableSortedSet<E> of() {
        return emptySet();
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e0) {
        return new RegularImmutableSortedSet<>(ImmutableList.of(e0), Ordering.natural());
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e0, E e1) {
        return construct(Ordering.natural(), 2, e0, e1);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e0, E e1, E e2) {
        return construct(Ordering.natural(), 3, e0, e1, e2);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e0, E e1, E e2, E e3) {
        return construct(Ordering.natural(), 4, e0, e1, e2, e3);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e0, E e1, E e2, E e3, E e4) {
        return construct(Ordering.natural(), 5, e0, e1, e2, e3, e4);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E... ae) {
        Comparable[] acomparable = new Comparable[6 + ae.length];

        acomparable[0] = e0;
        acomparable[1] = e1;
        acomparable[2] = e2;
        acomparable[3] = e3;
        acomparable[4] = e4;
        acomparable[5] = e5;
        System.arraycopy(ae, 0, acomparable, 6, ae.length);
        return construct(Ordering.natural(), acomparable.length, (Comparable[]) acomparable);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] ae) {
        return construct(Ordering.natural(), ae.length, (Object[]) ae.clone());
    }

    public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> iterable) {
        Ordering ordering = Ordering.natural();

        return copyOf(ordering, iterable);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> collection) {
        Ordering ordering = Ordering.natural();

        return copyOf(ordering, collection);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> iterator) {
        Ordering ordering = Ordering.natural();

        return copyOf(ordering, iterator);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> iterator) {
        return (new ImmutableSortedSet.Builder<>(comparator)).addAll(iterator).build();
    }

    public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> iterable) {
        Preconditions.checkNotNull(comparator);
        boolean flag = SortedIterables.hasSameComparator(comparator, iterable);

        if (flag && iterable instanceof ImmutableSortedSet) {
            ImmutableSortedSet immutablesortedset = (ImmutableSortedSet) iterable;

            if (!immutablesortedset.isPartialView()) {
                return immutablesortedset;
            }
        }

        Object[] aobject = (Object[]) Iterables.toArray(iterable);

        return construct(comparator, aobject.length, aobject);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Collection<? extends E> collection) {
        return copyOf(comparator, (Iterable) collection);
    }

    public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> sortedset) {
        Comparator comparator = SortedIterables.comparator(sortedset);
        ImmutableList immutablelist = ImmutableList.copyOf((Collection) sortedset);

        return (ImmutableSortedSet) (immutablelist.isEmpty() ? emptySet(comparator) : new RegularImmutableSortedSet<>(immutablelist, comparator));
    }

    static <E> ImmutableSortedSet<E> construct(Comparator<? super E> comparator, int i, E... ae) {
        if (i == 0) {
            return emptySet(comparator);
        } else {
            ObjectArrays.checkElementsNotNull(ae, i);
            Arrays.sort(ae, 0, i, comparator);
            int j = 1;

            for (int k = 1; k < i; ++k) {
                Object object = ae[k];
                Object object1 = ae[j - 1];

                if (comparator.compare(object, object1) != 0) {
                    ae[j++] = object;
                }
            }

            Arrays.fill(ae, j, i, (Object) null);
            return new RegularImmutableSortedSet<>(ImmutableList.asImmutableList(ae, j), comparator);
        }
    }

    public static <E> ImmutableSortedSet.Builder<E> orderedBy(Comparator<E> comparator) {
        return new ImmutableSortedSet.Builder<>(comparator);
    }

    public static <E extends Comparable<?>> ImmutableSortedSet.Builder<E> reverseOrder() {
        return new ImmutableSortedSet.Builder<>(Ordering.natural().reverse());
    }

    public static <E extends Comparable<?>> ImmutableSortedSet.Builder<E> naturalOrder() {
        return new ImmutableSortedSet.Builder<>(Ordering.natural());
    }

    int unsafeCompare(Object object, Object object1) {
        return unsafeCompare(this.comparator, object, object1);
    }

    static int unsafeCompare(Comparator<?> comparator, Object object, Object object1) {
        return comparator.compare(object, object1);
    }

    ImmutableSortedSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    @Override
    public abstract UnmodifiableIterator<E> iterator();

    public ImmutableSortedSet<E> headSet(E e0) {
        return this.headSet(e0, false);
    }

    @GwtIncompatible("NavigableSet")
    public ImmutableSortedSet<E> headSet(E e0, boolean flag) {
        return this.headSetImpl(Preconditions.checkNotNull(e0), flag);
    }

    public ImmutableSortedSet<E> subSet(E e0, E e1) {
        return this.subSet(e0, true, e1, false);
    }

    @GwtIncompatible("NavigableSet")
    public ImmutableSortedSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
        Preconditions.checkNotNull(e0);
        Preconditions.checkNotNull(e1);
        Preconditions.checkArgument(this.comparator.compare(e0, e1) <= 0);
        return this.subSetImpl(e0, flag, e1, flag1);
    }

    public ImmutableSortedSet<E> tailSet(E e0) {
        return this.tailSet(e0, true);
    }

    @GwtIncompatible("NavigableSet")
    public ImmutableSortedSet<E> tailSet(E e0, boolean flag) {
        return this.tailSetImpl(Preconditions.checkNotNull(e0), flag);
    }

    abstract ImmutableSortedSet<E> headSetImpl(E e0, boolean flag);

    abstract ImmutableSortedSet<E> subSetImpl(E e0, boolean flag, E e1, boolean flag1);

    abstract ImmutableSortedSet<E> tailSetImpl(E e0, boolean flag);

    @GwtIncompatible("NavigableSet")
    public E lower(E e0) {
        return Iterators.getNext(this.headSet(e0, false).descendingIterator(), (Object) null);
    }

    @GwtIncompatible("NavigableSet")
    public E floor(E e0) {
        return Iterators.getNext(this.headSet(e0, true).descendingIterator(), (Object) null);
    }

    @GwtIncompatible("NavigableSet")
    public E ceiling(E e0) {
        return Iterables.getFirst(this.tailSet(e0, true), (Object) null);
    }

    @GwtIncompatible("NavigableSet")
    public E higher(E e0) {
        return Iterables.getFirst(this.tailSet(e0, false), (Object) null);
    }

    public E first() {
        return this.iterator().next();
    }

    public E last() {
        return this.descendingIterator().next();
    }

    /** @deprecated */
    @Deprecated
    @GwtIncompatible("NavigableSet")
    public final E pollFirst() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @GwtIncompatible("NavigableSet")
    public final E pollLast() {
        throw new UnsupportedOperationException();
    }

    @GwtIncompatible("NavigableSet")
    public ImmutableSortedSet<E> descendingSet() {
        ImmutableSortedSet immutablesortedset = this.descendingSet;

        if (immutablesortedset == null) {
            immutablesortedset = this.descendingSet = this.createDescendingSet();
            immutablesortedset.descendingSet = this;
        }

        return immutablesortedset;
    }

    @GwtIncompatible("NavigableSet")
    ImmutableSortedSet<E> createDescendingSet() {
        return new DescendingImmutableSortedSet<>(this);
    }

    @GwtIncompatible("NavigableSet")
    public abstract UnmodifiableIterator<E> descendingIterator();

    abstract int indexOf(@Nullable Object object);

    private void readObject(ObjectInputStream objectinputstream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    @Override
    Object writeReplace() {
        return new ImmutableSortedSet.SerializedForm<>(this.comparator, this.toArray());
    }

    private static class SerializedForm<E> implements Serializable {

        final Comparator<? super E> comparator;
        final Object[] elements;
        private static final long serialVersionUID = 0L;

        public SerializedForm(Comparator<? super E> comparator, Object[] aobject) {
            this.comparator = comparator;
            this.elements = aobject;
        }

        Object readResolve() {
            return (new ImmutableSortedSet.Builder<>(this.comparator)).add((Object[]) this.elements).build();
        }
    }

    public static final class Builder<E> extends ImmutableSet.Builder<E> {

        private final Comparator<? super E> comparator;

        public Builder(Comparator<? super E> comparator) {
            this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
        }

        @Override
        public ImmutableSortedSet.Builder<E> add(E e0) {
            super.add(e0);
            return this;
        }

        @Override
        public ImmutableSortedSet.Builder<E> add(E... ae) {
            super.add(ae);
            return this;
        }

        @Override
        public ImmutableSortedSet.Builder<E> addAll(Iterable<? extends E> iterable) {
            super.addAll(iterable);
            return this;
        }

        @Override
        public ImmutableSortedSet.Builder<E> addAll(Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }

        @Override
        public ImmutableSortedSet<E> build() {
            Object[] aobject = (Object[]) this.contents;
            ImmutableSortedSet immutablesortedset = ImmutableSortedSet.construct(this.comparator, this.size, aobject);

            this.size = immutablesortedset.size();
            return immutablesortedset;
        }
    }
}
