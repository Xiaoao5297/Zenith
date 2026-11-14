package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
class EmptyImmutableSortedSet<E> extends ImmutableSortedSet<E> {

    EmptyImmutableSortedSet(Comparator<? super E> comparator) {
        super(comparator);
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return false;
    }

    public boolean containsAll(Collection<?> collection) {
        return collection.isEmpty();
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.emptyIterator();
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<E> descendingIterator() {
        return Iterators.emptyIterator();
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public ImmutableList<E> asList() {
        return ImmutableList.of();
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        return i;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object instanceof Set) {
            Set set = (Set) object;

            return set.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "[]";
    }

    @Override
    public E first() {
        throw new NoSuchElementException();
    }

    @Override
    public E last() {
        throw new NoSuchElementException();
    }

    @Override
    ImmutableSortedSet<E> headSetImpl(E e0, boolean flag) {
        return this;
    }

    @Override
    ImmutableSortedSet<E> subSetImpl(E e0, boolean flag, E e1, boolean flag1) {
        return this;
    }

    @Override
    ImmutableSortedSet<E> tailSetImpl(E e0, boolean flag) {
        return this;
    }

    @Override
    int indexOf(@Nullable Object object) {
        return -1;
    }

    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        return new EmptyImmutableSortedSet<>(Ordering.from(this.comparator).reverse());
    }
}
