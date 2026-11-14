package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E> {

    private final transient ImmutableList<E> elements;

    RegularImmutableSortedSet(ImmutableList<E> immutablelist, Comparator<? super E> comparator) {
        super(comparator);
        this.elements = immutablelist;
        Preconditions.checkArgument(!immutablelist.isEmpty());
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.elements.iterator();
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<E> descendingIterator() {
        return this.elements.reverse().iterator();
    }

    public boolean isEmpty() {
        return false;
    }

    public int size() {
        return this.elements.size();
    }

    @Override
    public boolean contains(Object object) {
        try {
            return object != null && this.unsafeBinarySearch(object) >= 0;
        } catch (ClassCastException classcastexception) {
            return false;
        }
    }

    public boolean containsAll(Collection<?> collection) {
        if (collection instanceof Multiset) {
            collection = ((Multiset) collection).elementSet();
        }

        if (SortedIterables.hasSameComparator(this.comparator(), (Iterable) collection) && ((Collection) collection).size() > 1) {
            PeekingIterator peekingiterator = Iterators.peekingIterator((Iterator) this.iterator());
            Iterator iterator = ((Collection) collection).iterator();
            Object object = iterator.next();

            try {
                while (peekingiterator.hasNext()) {
                    int i = this.unsafeCompare(peekingiterator.peek(), object);

                    if (i < 0) {
                        peekingiterator.next();
                    } else if (i == 0) {
                        if (!iterator.hasNext()) {
                            return true;
                        }

                        object = iterator.next();
                    } else if (i > 0) {
                        return false;
                    }
                }

                return false;
            } catch (NullPointerException nullpointerexception) {
                return false;
            } catch (ClassCastException classcastexception) {
                return false;
            }
        } else {
            return super.containsAll((Collection) collection);
        }
    }

    private int unsafeBinarySearch(Object object) throws ClassCastException {
        return Collections.binarySearch(this.elements, object, this.unsafeComparator());
    }

    @Override
    boolean isPartialView() {
        return this.elements.isPartialView();
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        return this.elements.copyIntoArray(aobject, i);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof Set)) {
            return false;
        } else {
            Set set = (Set) object;

            if (this.size() != set.size()) {
                return false;
            } else if (SortedIterables.hasSameComparator(this.comparator, set)) {
                Iterator iterator = set.iterator();

                try {
                    UnmodifiableIterator unmodifiableiterator = this.iterator();

                    Object object1;
                    Object object2;

                    do {
                        if (!unmodifiableiterator.hasNext()) {
                            return true;
                        }

                        object1 = unmodifiableiterator.next();
                        object2 = iterator.next();
                    } while (object2 != null && this.unsafeCompare(object1, object2) == 0);

                    return false;
                } catch (ClassCastException classcastexception) {
                    return false;
                } catch (NoSuchElementException nosuchelementexception) {
                    return false;
                }
            } else {
                return this.containsAll(set);
            }
        }
    }

    @Override
    public E first() {
        return this.elements.get(0);
    }

    @Override
    public E last() {
        return this.elements.get(this.size() - 1);
    }

    @Override
    public E lower(E e0) {
        int i = this.headIndex(e0, false) - 1;

        return i == -1 ? null : this.elements.get(i);
    }

    @Override
    public E floor(E e0) {
        int i = this.headIndex(e0, true) - 1;

        return i == -1 ? null : this.elements.get(i);
    }

    @Override
    public E ceiling(E e0) {
        int i = this.tailIndex(e0, true);

        return i == this.size() ? null : this.elements.get(i);
    }

    @Override
    public E higher(E e0) {
        int i = this.tailIndex(e0, false);

        return i == this.size() ? null : this.elements.get(i);
    }

    @Override
    ImmutableSortedSet<E> headSetImpl(E e0, boolean flag) {
        return this.getSubSet(0, this.headIndex(e0, flag));
    }

    int headIndex(E e0, boolean flag) {
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(e0), this.comparator(), flag ? SortedLists.KeyPresentBehavior.FIRST_AFTER : SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    @Override
    ImmutableSortedSet<E> subSetImpl(E e0, boolean flag, E e1, boolean flag1) {
        return this.tailSetImpl(e0, flag).headSetImpl(e1, flag1);
    }

    @Override
    ImmutableSortedSet<E> tailSetImpl(E e0, boolean flag) {
        return this.getSubSet(this.tailIndex(e0, flag), this.size());
    }

    int tailIndex(E e0, boolean flag) {
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(e0), this.comparator(), flag ? SortedLists.KeyPresentBehavior.FIRST_PRESENT : SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    Comparator<Object> unsafeComparator() {
        return this.comparator;
    }

    ImmutableSortedSet<E> getSubSet(int i, int j) {
        return (ImmutableSortedSet) (i == 0 && j == this.size() ? this : (i < j ? new RegularImmutableSortedSet<>(this.elements.subList(i, j), this.comparator) : emptySet(this.comparator)));
    }

    @Override
    int indexOf(@Nullable Object object) {
        if (object == null) {
            return -1;
        } else {
            int i;

            try {
                i = SortedLists.binarySearch(this.elements, object, this.unsafeComparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
            } catch (ClassCastException classcastexception) {
                return -1;
            }

            return i >= 0 ? i : -1;
        }
    }

    @Override
    ImmutableList<E> createAsList() {
        return new ImmutableSortedAsList<>(this, this.elements);
    }

    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        return new RegularImmutableSortedSet<>(this.elements.reverse(), Ordering.from(this.comparator).reverse());
    }
}
