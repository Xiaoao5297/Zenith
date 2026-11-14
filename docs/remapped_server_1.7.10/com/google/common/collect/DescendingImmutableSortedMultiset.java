package com.google.common.collect;

import javax.annotation.Nullable;

final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {

    private final transient ImmutableSortedMultiset<E> forward;

    DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> immutablesortedmultiset) {
        this.forward = immutablesortedmultiset;
    }

    @Override
    public int count(@Nullable Object object) {
        return this.forward.count(object);
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return this.forward.lastEntry();
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return this.forward.firstEntry();
    }

    public int size() {
        return this.forward.size();
    }

    @Override
    public ImmutableSortedSet<E> elementSet() {
        return this.forward.elementSet().descendingSet();
    }

    @Override
    Multiset.Entry<E> getEntry(int i) {
        return (Multiset.Entry) this.forward.entrySet().asList().reverse().get(i);
    }

    @Override
    public ImmutableSortedMultiset<E> descendingMultiset() {
        return this.forward;
    }

    @Override
    public ImmutableSortedMultiset<E> headMultiset(E e0, BoundType boundtype) {
        return this.forward.tailMultiset(e0, boundtype).descendingMultiset();
    }

    @Override
    public ImmutableSortedMultiset<E> tailMultiset(E e0, BoundType boundtype) {
        return this.forward.headMultiset(e0, boundtype).descendingMultiset();
    }

    @Override
    boolean isPartialView() {
        return this.forward.isPartialView();
    }
}
