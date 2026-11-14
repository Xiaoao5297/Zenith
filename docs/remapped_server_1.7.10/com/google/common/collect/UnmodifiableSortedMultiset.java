package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.NavigableSet;

@GwtCompatible(emulated = true)
final class UnmodifiableSortedMultiset<E> extends Multisets.UnmodifiableMultiset<E> implements SortedMultiset<E> {

    private transient UnmodifiableSortedMultiset<E> descendingMultiset;
    private static final long serialVersionUID = 0L;

    UnmodifiableSortedMultiset(SortedMultiset<E> sortedmultiset) {
        super(sortedmultiset);
    }

    @Override
    protected SortedMultiset<E> delegate() {
        return (SortedMultiset) super.delegate();
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }

    @Override
    NavigableSet<E> createElementSet() {
        return Sets.unmodifiableNavigableSet(this.delegate().elementSet());
    }

    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet) super.elementSet();
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        UnmodifiableSortedMultiset unmodifiablesortedmultiset = this.descendingMultiset;

        if (unmodifiablesortedmultiset == null) {
            unmodifiablesortedmultiset = new UnmodifiableSortedMultiset<>(this.delegate().descendingMultiset());
            unmodifiablesortedmultiset.descendingMultiset = this;
            return this.descendingMultiset = unmodifiablesortedmultiset;
        } else {
            return unmodifiablesortedmultiset;
        }
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return this.delegate().firstEntry();
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return this.delegate().lastEntry();
    }

    @Override
    public Multiset.Entry<E> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Multiset.Entry<E> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMultiset<E> headMultiset(E e0, BoundType boundtype) {
        return Multisets.unmodifiableSortedMultiset(this.delegate().headMultiset(e0, boundtype));
    }

    @Override
    public SortedMultiset<E> subMultiset(E e0, BoundType boundtype, E e1, BoundType boundtype1) {
        return Multisets.unmodifiableSortedMultiset(this.delegate().subMultiset(e0, boundtype, e1, boundtype1));
    }

    @Override
    public SortedMultiset<E> tailMultiset(E e0, BoundType boundtype) {
        return Multisets.unmodifiableSortedMultiset(this.delegate().tailMultiset(e0, boundtype));
    }
}
