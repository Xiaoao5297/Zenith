package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

@Beta
@GwtCompatible(emulated = true)
public abstract class ForwardingSortedMultiset<E> extends ForwardingMultiset<E> implements SortedMultiset<E> {

    protected ForwardingSortedMultiset() {}

    @Override
    protected abstract SortedMultiset<E> delegate();

    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet) super.elementSet();
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        return this.delegate().descendingMultiset();
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return this.delegate().firstEntry();
    }

    protected Multiset.Entry<E> standardFirstEntry() {
        Iterator iterator = this.entrySet().iterator();

        if (!iterator.hasNext()) {
            return null;
        } else {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

            return Multisets.immutableEntry(multiset_entry.getElement(), multiset_entry.getCount());
        }
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return this.delegate().lastEntry();
    }

    protected Multiset.Entry<E> standardLastEntry() {
        Iterator iterator = this.descendingMultiset().entrySet().iterator();

        if (!iterator.hasNext()) {
            return null;
        } else {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

            return Multisets.immutableEntry(multiset_entry.getElement(), multiset_entry.getCount());
        }
    }

    @Override
    public Multiset.Entry<E> pollFirstEntry() {
        return this.delegate().pollFirstEntry();
    }

    protected Multiset.Entry<E> standardPollFirstEntry() {
        Iterator iterator = this.entrySet().iterator();

        if (!iterator.hasNext()) {
            return null;
        } else {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

            multiset_entry = Multisets.immutableEntry(multiset_entry.getElement(), multiset_entry.getCount());
            iterator.remove();
            return multiset_entry;
        }
    }

    @Override
    public Multiset.Entry<E> pollLastEntry() {
        return this.delegate().pollLastEntry();
    }

    protected Multiset.Entry<E> standardPollLastEntry() {
        Iterator iterator = this.descendingMultiset().entrySet().iterator();

        if (!iterator.hasNext()) {
            return null;
        } else {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

            multiset_entry = Multisets.immutableEntry(multiset_entry.getElement(), multiset_entry.getCount());
            iterator.remove();
            return multiset_entry;
        }
    }

    @Override
    public SortedMultiset<E> headMultiset(E e0, BoundType boundtype) {
        return this.delegate().headMultiset(e0, boundtype);
    }

    @Override
    public SortedMultiset<E> subMultiset(E e0, BoundType boundtype, E e1, BoundType boundtype1) {
        return this.delegate().subMultiset(e0, boundtype, e1, boundtype1);
    }

    protected SortedMultiset<E> standardSubMultiset(E e0, BoundType boundtype, E e1, BoundType boundtype1) {
        return this.tailMultiset(e0, boundtype).headMultiset(e1, boundtype1);
    }

    @Override
    public SortedMultiset<E> tailMultiset(E e0, BoundType boundtype) {
        return this.delegate().tailMultiset(e0, boundtype);
    }

    protected abstract class StandardDescendingMultiset extends DescendingMultiset<E> {

        public StandardDescendingMultiset() {}

        @Override
        SortedMultiset<E> forwardMultiset() {
            return ForwardingSortedMultiset.this;
        }
    }

    protected class StandardElementSet extends SortedMultisets.NavigableElementSet<E> {

        public StandardElementSet() {
            super(ForwardingSortedMultiset.this);
        }
    }
}
