package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;

@GwtCompatible(emulated = true)
abstract class DescendingMultiset<E> extends ForwardingMultiset<E> implements SortedMultiset<E> {

    private transient Comparator<? super E> comparator;
    private transient NavigableSet<E> elementSet;
    private transient Set<Multiset.Entry<E>> entrySet;

    DescendingMultiset() {}

    abstract SortedMultiset<E> forwardMultiset();

    @Override
    public Comparator<? super E> comparator() {
        Comparator comparator = this.comparator;

        return comparator == null ? (this.comparator = Ordering.from(this.forwardMultiset().comparator()).reverse()) : comparator;
    }

    @Override
    public NavigableSet<E> elementSet() {
        NavigableSet navigableset = this.elementSet;

        return navigableset == null ? (this.elementSet = new SortedMultisets.NavigableElementSet<>(this)) : navigableset;
    }

    @Override
    public Multiset.Entry<E> pollFirstEntry() {
        return this.forwardMultiset().pollLastEntry();
    }

    @Override
    public Multiset.Entry<E> pollLastEntry() {
        return this.forwardMultiset().pollFirstEntry();
    }

    @Override
    public SortedMultiset<E> headMultiset(E e0, BoundType boundtype) {
        return this.forwardMultiset().tailMultiset(e0, boundtype).descendingMultiset();
    }

    @Override
    public SortedMultiset<E> subMultiset(E e0, BoundType boundtype, E e1, BoundType boundtype1) {
        return this.forwardMultiset().subMultiset(e1, boundtype1, e0, boundtype).descendingMultiset();
    }

    @Override
    public SortedMultiset<E> tailMultiset(E e0, BoundType boundtype) {
        return this.forwardMultiset().headMultiset(e0, boundtype).descendingMultiset();
    }

    @Override
    protected Multiset<E> delegate() {
        return this.forwardMultiset();
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        return this.forwardMultiset();
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return this.forwardMultiset().lastEntry();
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return this.forwardMultiset().firstEntry();
    }

    abstract Iterator<Multiset.Entry<E>> entryIterator();

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        Set set = this.entrySet;

        return set == null ? (this.entrySet = this.createEntrySet()) : set;
    }

    Set<Multiset.Entry<E>> createEntrySet() {
        return new Multisets.EntrySet<E>() {
            @Override
            Multiset<E> multiset() {
                return DescendingMultiset.this;
            }

            public Iterator<Multiset.Entry<E>> iterator() {
                return DescendingMultiset.this.entryIterator();
            }

            public int size() {
                return DescendingMultiset.this.forwardMultiset().entrySet().size();
            }
        };
    }

    @Override
    public Iterator<E> iterator() {
        return Multisets.iteratorImpl(this);
    }

    @Override
    public Object[] toArray() {
        return this.standardToArray();
    }

    @Override
    public <T> T[] toArray(T[] at) {
        return this.standardToArray(at);
    }

    @Override
    public String toString() {
        return this.entrySet().toString();
    }
}
