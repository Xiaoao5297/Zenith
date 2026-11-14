package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultiset<E> extends AbstractCollection<E> implements Multiset<E> {

    private transient Set<E> elementSet;
    private transient Set<Multiset.Entry<E>> entrySet;

    AbstractMultiset() {}

    public int size() {
        return Multisets.sizeImpl(this);
    }

    public boolean isEmpty() {
        return this.entrySet().isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.count(object) > 0;
    }

    @Override
    public Iterator<E> iterator() {
        return Multisets.iteratorImpl(this);
    }

    @Override
    public int count(@Nullable Object object) {
        Iterator iterator = this.entrySet().iterator();

        Multiset.Entry multiset_entry;

        do {
            if (!iterator.hasNext()) {
                return 0;
            }

            multiset_entry = (Multiset.Entry) iterator.next();
        } while (!Objects.equal(multiset_entry.getElement(), object));

        return multiset_entry.getCount();
    }

    @Override
    public boolean add(@Nullable E e0) {
        this.add(e0, 1);
        return true;
    }

    @Override
    public int add(@Nullable E e0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(@Nullable Object object) {
        return this.remove(object, 1) > 0;
    }

    @Override
    public int remove(@Nullable Object object, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int setCount(@Nullable E e0, int i) {
        return Multisets.setCountImpl(this, e0, i);
    }

    @Override
    public boolean setCount(@Nullable E e0, int i, int j) {
        return Multisets.setCountImpl(this, e0, i, j);
    }

    public boolean addAll(Collection<? extends E> collection) {
        return Multisets.addAllImpl(this, collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return Multisets.removeAllImpl(this, collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return Multisets.retainAllImpl(this, collection);
    }

    public void clear() {
        Iterators.clear(this.entryIterator());
    }

    @Override
    public Set<E> elementSet() {
        Set set = this.elementSet;

        if (set == null) {
            this.elementSet = set = this.createElementSet();
        }

        return set;
    }

    Set<E> createElementSet() {
        return new AbstractMultiset.ElementSet();
    }

    abstract Iterator<Multiset.Entry<E>> entryIterator();

    abstract int distinctElements();

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        Set set = this.entrySet;

        return set == null ? (this.entrySet = this.createEntrySet()) : set;
    }

    Set<Multiset.Entry<E>> createEntrySet() {
        return new AbstractMultiset.EntrySet();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return Multisets.equalsImpl(this, object);
    }

    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }

    @Override
    public String toString() {
        return this.entrySet().toString();
    }

    class EntrySet extends Multisets.EntrySet<E> {

        EntrySet() {}

        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }

        public Iterator<Multiset.Entry<E>> iterator() {
            return AbstractMultiset.this.entryIterator();
        }

        public int size() {
            return AbstractMultiset.this.distinctElements();
        }
    }

    class ElementSet extends Multisets.ElementSet<E> {

        ElementSet() {}

        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }
    }
}
