package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingList<E> extends ForwardingCollection<E> implements List<E> {

    protected ForwardingList() {}

    @Override
    protected abstract List<E> delegate();

    public void add(int i, E e0) {
        this.delegate().add(i, e0);
    }

    public boolean addAll(int i, Collection<? extends E> collection) {
        return this.delegate().addAll(i, collection);
    }

    public E get(int i) {
        return this.delegate().get(i);
    }

    public int indexOf(Object object) {
        return this.delegate().indexOf(object);
    }

    public int lastIndexOf(Object object) {
        return this.delegate().lastIndexOf(object);
    }

    public ListIterator<E> listIterator() {
        return this.delegate().listIterator();
    }

    public ListIterator<E> listIterator(int i) {
        return this.delegate().listIterator(i);
    }

    public E remove(int i) {
        return this.delegate().remove(i);
    }

    public E set(int i, E e0) {
        return this.delegate().set(i, e0);
    }

    public List<E> subList(int i, int j) {
        return this.delegate().subList(i, j);
    }

    public boolean equals(@Nullable Object object) {
        return object == this || this.delegate().equals(object);
    }

    public int hashCode() {
        return this.delegate().hashCode();
    }

    protected boolean standardAdd(E e0) {
        this.add(this.size(), e0);
        return true;
    }

    protected boolean standardAddAll(int i, Iterable<? extends E> iterable) {
        return Lists.addAllImpl(this, i, iterable);
    }

    protected int standardIndexOf(@Nullable Object object) {
        return Lists.indexOfImpl(this, object);
    }

    protected int standardLastIndexOf(@Nullable Object object) {
        return Lists.lastIndexOfImpl(this, object);
    }

    protected Iterator<E> standardIterator() {
        return this.listIterator();
    }

    protected ListIterator<E> standardListIterator() {
        return this.listIterator(0);
    }

    @Beta
    protected ListIterator<E> standardListIterator(int i) {
        return Lists.listIteratorImpl(this, i);
    }

    @Beta
    protected List<E> standardSubList(int i, int j) {
        return Lists.subListImpl(this, i, j);
    }

    @Beta
    protected boolean standardEquals(@Nullable Object object) {
        return Lists.equalsImpl(this, object);
    }

    @Beta
    protected int standardHashCode() {
        return Lists.hashCodeImpl(this);
    }
}
