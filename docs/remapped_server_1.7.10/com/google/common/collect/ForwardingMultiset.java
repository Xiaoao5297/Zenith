package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMultiset<E> extends ForwardingCollection<E> implements Multiset<E> {

    protected ForwardingMultiset() {}

    @Override
    protected abstract Multiset<E> delegate();

    @Override
    public int count(Object object) {
        return this.delegate().count(object);
    }

    @Override
    public int add(E e0, int i) {
        return this.delegate().add(e0, i);
    }

    @Override
    public int remove(Object object, int i) {
        return this.delegate().remove(object, i);
    }

    @Override
    public Set<E> elementSet() {
        return this.delegate().elementSet();
    }

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        return this.delegate().entrySet();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return object == this || this.delegate().equals(object);
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }

    @Override
    public int setCount(E e0, int i) {
        return this.delegate().setCount(e0, i);
    }

    @Override
    public boolean setCount(E e0, int i, int j) {
        return this.delegate().setCount(e0, i, j);
    }

    @Override
    protected boolean standardContains(@Nullable Object object) {
        return this.count(object) > 0;
    }

    @Override
    protected void standardClear() {
        Iterators.clear(this.entrySet().iterator());
    }

    @Beta
    protected int standardCount(@Nullable Object object) {
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

    protected boolean standardAdd(E e0) {
        this.add(e0, 1);
        return true;
    }

    @Beta
    @Override
    protected boolean standardAddAll(Collection<? extends E> collection) {
        return Multisets.addAllImpl(this, collection);
    }

    @Override
    protected boolean standardRemove(Object object) {
        return this.remove(object, 1) > 0;
    }

    @Override
    protected boolean standardRemoveAll(Collection<?> collection) {
        return Multisets.removeAllImpl(this, collection);
    }

    @Override
    protected boolean standardRetainAll(Collection<?> collection) {
        return Multisets.retainAllImpl(this, collection);
    }

    protected int standardSetCount(E e0, int i) {
        return Multisets.setCountImpl(this, e0, i);
    }

    protected boolean standardSetCount(E e0, int i, int j) {
        return Multisets.setCountImpl(this, e0, i, j);
    }

    protected Iterator<E> standardIterator() {
        return Multisets.iteratorImpl(this);
    }

    protected int standardSize() {
        return Multisets.sizeImpl(this);
    }

    protected boolean standardEquals(@Nullable Object object) {
        return Multisets.equalsImpl(this, object);
    }

    protected int standardHashCode() {
        return this.entrySet().hashCode();
    }

    @Override
    protected String standardToString() {
        return this.entrySet().toString();
    }

    @Beta
    protected class StandardElementSet extends Multisets.ElementSet<E> {

        public StandardElementSet() {}

        @Override
        Multiset<E> multiset() {
            return ForwardingMultiset.this;
        }
    }
}
