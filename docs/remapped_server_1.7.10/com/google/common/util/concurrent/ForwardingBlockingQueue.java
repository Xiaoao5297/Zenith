package com.google.common.util.concurrent;

import com.google.common.collect.ForwardingQueue;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class ForwardingBlockingQueue<E> extends ForwardingQueue<E> implements BlockingQueue<E> {

    protected ForwardingBlockingQueue() {}

    @Override
    protected abstract BlockingQueue<E> delegate();

    public int drainTo(Collection<? super E> collection, int i) {
        return this.delegate().drainTo(collection, i);
    }

    public int drainTo(Collection<? super E> collection) {
        return this.delegate().drainTo(collection);
    }

    public boolean offer(E e0, long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().offer(e0, i, timeunit);
    }

    public E poll(long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().poll(i, timeunit);
    }

    public void put(E e0) throws InterruptedException {
        this.delegate().put(e0);
    }

    public int remainingCapacity() {
        return this.delegate().remainingCapacity();
    }

    public E take() throws InterruptedException {
        return this.delegate().take();
    }
}
