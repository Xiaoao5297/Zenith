package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

class DescendingImmutableSortedSet<E> extends ImmutableSortedSet<E> {

    private final ImmutableSortedSet<E> forward;

    DescendingImmutableSortedSet(ImmutableSortedSet<E> immutablesortedset) {
        super(Ordering.from(immutablesortedset.comparator()).reverse());
        this.forward = immutablesortedset;
    }

    public int size() {
        return this.forward.size();
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.forward.descendingIterator();
    }

    @Override
    ImmutableSortedSet<E> headSetImpl(E e0, boolean flag) {
        return this.forward.tailSet(e0, flag).descendingSet();
    }

    @Override
    ImmutableSortedSet<E> subSetImpl(E e0, boolean flag, E e1, boolean flag1) {
        return this.forward.subSet(e1, flag1, e0, flag).descendingSet();
    }

    @Override
    ImmutableSortedSet<E> tailSetImpl(E e0, boolean flag) {
        return this.forward.headSet(e0, flag).descendingSet();
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public ImmutableSortedSet<E> descendingSet() {
        return this.forward;
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<E> descendingIterator() {
        return this.forward.iterator();
    }

    @GwtIncompatible("NavigableSet")
    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        throw new AssertionError("should never be called");
    }

    @Override
    public E lower(E e0) {
        return this.forward.higher(e0);
    }

    @Override
    public E floor(E e0) {
        return this.forward.ceiling(e0);
    }

    @Override
    public E ceiling(E e0) {
        return this.forward.floor(e0);
    }

    @Override
    public E higher(E e0) {
        return this.forward.lower(e0);
    }

    @Override
    int indexOf(@Nullable Object object) {
        int i = this.forward.indexOf(object);

        return i == -1 ? i : this.size() - 1 - i;
    }

    @Override
    boolean isPartialView() {
        return this.forward.isPartialView();
    }
}
