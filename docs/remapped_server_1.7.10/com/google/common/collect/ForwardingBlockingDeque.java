package com.google.common.collect;

import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public abstract class ForwardingBlockingDeque<E> extends ForwardingDeque<E> implements BlockingDeque<E> {

    protected ForwardingBlockingDeque() {}

    @Override
    protected abstract BlockingDeque<E> delegate();

    public int remainingCapacity() {
        return this.delegate().remainingCapacity();
    }

    public void putFirst(E e0) throws InterruptedException {
        this.delegate().putFirst(e0);
    }

    public void putLast(E e0) throws InterruptedException {
        this.delegate().putLast(e0);
    }

    public boolean offerFirst(E e0, long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().offerFirst(e0, i, timeunit);
    }

    public boolean offerLast(E e0, long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().offerLast(e0, i, timeunit);
    }

    public E takeFirst() throws InterruptedException {
        return this.delegate().takeFirst();
    }

    public E takeLast() throws InterruptedException {
        return this.delegate().takeLast();
    }

    public E pollFirst(long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().pollFirst(i, timeunit);
    }

    public E pollLast(long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().pollLast(i, timeunit);
    }

    public void put(E e0) throws InterruptedException {
        this.delegate().put(e0);
    }

    public boolean offer(E e0, long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().offer(e0, i, timeunit);
    }

    public E take() throws InterruptedException {
        return this.delegate().take();
    }

    public E poll(long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().poll(i, timeunit);
    }

    public int drainTo(Collection<? super E> collection) {
        return this.delegate().drainTo(collection);
    }

    public int drainTo(Collection<? super E> collection, int i) {
        return this.delegate().drainTo(collection, i);
    }
}
