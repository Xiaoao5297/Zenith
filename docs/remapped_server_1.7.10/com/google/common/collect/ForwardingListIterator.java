package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@GwtCompatible
public abstract class ForwardingListIterator<E> extends ForwardingIterator<E> implements ListIterator<E> {

    protected ForwardingListIterator() {}

    @Override
    protected abstract ListIterator<E> delegate();

    public void add(E e0) {
        this.delegate().add(e0);
    }

    public boolean hasPrevious() {
        return this.delegate().hasPrevious();
    }

    public int nextIndex() {
        return this.delegate().nextIndex();
    }

    public E previous() {
        return this.delegate().previous();
    }

    public int previousIndex() {
        return this.delegate().previousIndex();
    }

    public void set(E e0) {
        this.delegate().set(e0);
    }
}
