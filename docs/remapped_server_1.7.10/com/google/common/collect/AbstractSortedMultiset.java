package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
abstract class AbstractSortedMultiset<E> extends AbstractMultiset<E> implements SortedMultiset<E> {

    @GwtTransient
    final Comparator<? super E> comparator;
    private transient SortedMultiset<E> descendingMultiset;

    AbstractSortedMultiset() {
        this(Ordering.natural());
    }

    AbstractSortedMultiset(Comparator<? super E> comparator) {
        this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
    }

    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet) super.elementSet();
    }

    @Override
    NavigableSet<E> createElementSet() {
        return new SortedMultisets.NavigableElementSet<>(this);
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        Iterator iterator = this.entryIterator();

        return iterator.hasNext() ? (Multiset.Entry) iterator.next() : null;
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        Iterator iterator = this.descendingEntryIterator();

        return iterator.hasNext() ? (Multiset.Entry) iterator.next() : null;
    }

    @Override
    public Multiset.Entry<E> pollFirstEntry() {
        Iterator iterator = this.entryIterator();

        if (iterator.hasNext()) {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

            multiset_entry = Multisets.immutableEntry(multiset_entry.getElement(), multiset_entry.getCount());
            iterator.remove();
            return multiset_entry;
        } else {
            return null;
        }
    }

    @Override
    public Multiset.Entry<E> pollLastEntry() {
        Iterator iterator = this.descendingEntryIterator();

        if (iterator.hasNext()) {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

            multiset_entry = Multisets.immutableEntry(multiset_entry.getElement(), multiset_entry.getCount());
            iterator.remove();
            return multiset_entry;
        } else {
            return null;
        }
    }

    @Override
    public SortedMultiset<E> subMultiset(@Nullable E e0, BoundType boundtype, @Nullable E e1, BoundType boundtype1) {
        Preconditions.checkNotNull(boundtype);
        Preconditions.checkNotNull(boundtype1);
        return this.tailMultiset(e0, boundtype).headMultiset(e1, boundtype1);
    }

    abstract Iterator<Multiset.Entry<E>> descendingEntryIterator();

    Iterator<E> descendingIterator() {
        return Multisets.iteratorImpl(this.descendingMultiset());
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        SortedMultiset sortedmultiset = this.descendingMultiset;

        return sortedmultiset == null ? (this.descendingMultiset = this.createDescendingMultiset()) : sortedmultiset;
    }

    SortedMultiset<E> createDescendingMultiset() {
        return new DescendingMultiset<E>() {
            @Override
            SortedMultiset<E> forwardMultiset() {
                return AbstractSortedMultiset.this;
            }

            @Override
            Iterator<Multiset.Entry<E>> entryIterator() {
                return AbstractSortedMultiset.this.descendingEntryIterator();
            }

            @Override
            public Iterator<E> iterator() {
                return AbstractSortedMultiset.this.descendingIterator();
            }
        };
    }
}
