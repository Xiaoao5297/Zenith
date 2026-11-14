package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class SortedMultisets {

    private SortedMultisets() {}

    private static <E> E getElementOrThrow(Multiset.Entry<E> multiset_entry) {
        if (multiset_entry == null) {
            throw new NoSuchElementException();
        } else {
            return multiset_entry.getElement();
        }
    }

    private static <E> E getElementOrNull(@Nullable Multiset.Entry<E> multiset_entry) {
        return multiset_entry == null ? null : multiset_entry.getElement();
    }

    @GwtIncompatible("Navigable")
    static class NavigableElementSet<E> extends SortedMultisets.ElementSet<E> implements NavigableSet<E> {

        NavigableElementSet(SortedMultiset<E> sortedmultiset) {
            super(sortedmultiset);
        }

        public E lower(E e0) {
            return SortedMultisets.getElementOrNull(this.multiset().headMultiset(e0, BoundType.OPEN).lastEntry());
        }

        public E floor(E e0) {
            return SortedMultisets.getElementOrNull(this.multiset().headMultiset(e0, BoundType.CLOSED).lastEntry());
        }

        public E ceiling(E e0) {
            return SortedMultisets.getElementOrNull(this.multiset().tailMultiset(e0, BoundType.CLOSED).firstEntry());
        }

        public E higher(E e0) {
            return SortedMultisets.getElementOrNull(this.multiset().tailMultiset(e0, BoundType.OPEN).firstEntry());
        }

        public NavigableSet<E> descendingSet() {
            return new SortedMultisets.NavigableElementSet<>(this.multiset().descendingMultiset());
        }

        public Iterator<E> descendingIterator() {
            return this.descendingSet().iterator();
        }

        public E pollFirst() {
            return SortedMultisets.getElementOrNull(this.multiset().pollFirstEntry());
        }

        public E pollLast() {
            return SortedMultisets.getElementOrNull(this.multiset().pollLastEntry());
        }

        public NavigableSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
            return new SortedMultisets.NavigableElementSet<>(this.multiset().subMultiset(e0, BoundType.forBoolean(flag), e1, BoundType.forBoolean(flag1)));
        }

        public NavigableSet<E> headSet(E e0, boolean flag) {
            return new SortedMultisets.NavigableElementSet<>(this.multiset().headMultiset(e0, BoundType.forBoolean(flag)));
        }

        public NavigableSet<E> tailSet(E e0, boolean flag) {
            return new SortedMultisets.NavigableElementSet<>(this.multiset().tailMultiset(e0, BoundType.forBoolean(flag)));
        }
    }

    static class ElementSet<E> extends Multisets.ElementSet<E> implements SortedSet<E> {

        private final SortedMultiset<E> multiset;

        ElementSet(SortedMultiset<E> sortedmultiset) {
            this.multiset = sortedmultiset;
        }

        @Override
        final SortedMultiset<E> multiset() {
            return this.multiset;
        }

        public Comparator<? super E> comparator() {
            return this.multiset().comparator();
        }

        public SortedSet<E> subSet(E e0, E e1) {
            return this.multiset().subMultiset(e0, BoundType.CLOSED, e1, BoundType.OPEN).elementSet();
        }

        public SortedSet<E> headSet(E e0) {
            return this.multiset().headMultiset(e0, BoundType.OPEN).elementSet();
        }

        public SortedSet<E> tailSet(E e0) {
            return this.multiset().tailMultiset(e0, BoundType.CLOSED).elementSet();
        }

        public E first() {
            return SortedMultisets.getElementOrThrow(this.multiset().firstEntry());
        }

        public E last() {
            return SortedMultisets.getElementOrThrow(this.multiset().lastEntry());
        }
    }
}
