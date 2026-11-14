package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import javax.annotation.Nullable;

final class EmptyImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {

    private final ImmutableSortedSet<E> elementSet;

    EmptyImmutableSortedMultiset(Comparator<? super E> comparator) {
        this.elementSet = ImmutableSortedSet.emptySet(comparator);
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return null;
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return null;
    }

    @Override
    public int count(@Nullable Object object) {
        return 0;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return collection.isEmpty();
    }

    public int size() {
        return 0;
    }

    @Override
    public ImmutableSortedSet<E> elementSet() {
        return this.elementSet;
    }

    @Override
    Multiset.Entry<E> getEntry(int i) {
        throw new AssertionError("should never be called");
    }

    @Override
    public ImmutableSortedMultiset<E> headMultiset(E e0, BoundType boundtype) {
        Preconditions.checkNotNull(e0);
        Preconditions.checkNotNull(boundtype);
        return this;
    }

    @Override
    public ImmutableSortedMultiset<E> tailMultiset(E e0, BoundType boundtype) {
        Preconditions.checkNotNull(e0);
        Preconditions.checkNotNull(boundtype);
        return this;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.emptyIterator();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object instanceof Multiset) {
            Multiset multiset = (Multiset) object;

            return multiset.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        return i;
    }

    @Override
    public ImmutableList<E> asList() {
        return ImmutableList.of();
    }
}
