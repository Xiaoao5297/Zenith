package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.NoSuchElementException;
import java.util.Queue;

@GwtCompatible
public abstract class ForwardingQueue<E> extends ForwardingCollection<E> implements Queue<E> {

    protected ForwardingQueue() {}

    @Override
    protected abstract Queue<E> delegate();

    public boolean offer(E e0) {
        return this.delegate().offer(e0);
    }

    public E poll() {
        return this.delegate().poll();
    }

    public E remove() {
        return this.delegate().remove();
    }

    public E peek() {
        return this.delegate().peek();
    }

    public E element() {
        return this.delegate().element();
    }

    protected boolean standardOffer(E e0) {
        try {
            return this.add(e0);
        } catch (IllegalStateException illegalstateexception) {
            return false;
        }
    }

    protected E standardPeek() {
        try {
            return this.element();
        } catch (NoSuchElementException nosuchelementexception) {
            return null;
        }
    }

    protected E standardPoll() {
        try {
            return this.remove();
        } catch (NoSuchElementException nosuchelementexception) {
            return null;
        }
    }
}
