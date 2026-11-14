package com.google.common.collect;

import java.util.Deque;
import java.util.Iterator;

public abstract class ForwardingDeque<E> extends ForwardingQueue<E> implements Deque<E> {

    protected ForwardingDeque() {}

    @Override
    protected abstract Deque<E> delegate();

    public void addFirst(E e0) {
        this.delegate().addFirst(e0);
    }

    public void addLast(E e0) {
        this.delegate().addLast(e0);
    }

    public Iterator<E> descendingIterator() {
        return this.delegate().descendingIterator();
    }

    public E getFirst() {
        return this.delegate().getFirst();
    }

    public E getLast() {
        return this.delegate().getLast();
    }

    public boolean offerFirst(E e0) {
        return this.delegate().offerFirst(e0);
    }

    public boolean offerLast(E e0) {
        return this.delegate().offerLast(e0);
    }

    public E peekFirst() {
        return this.delegate().peekFirst();
    }

    public E peekLast() {
        return this.delegate().peekLast();
    }

    public E pollFirst() {
        return this.delegate().pollFirst();
    }

    public E pollLast() {
        return this.delegate().pollLast();
    }

    public E pop() {
        return this.delegate().pop();
    }

    public void push(E e0) {
        this.delegate().push(e0);
    }

    public E removeFirst() {
        return this.delegate().removeFirst();
    }

    public E removeLast() {
        return this.delegate().removeLast();
    }

    public boolean removeFirstOccurrence(Object object) {
        return this.delegate().removeFirstOccurrence(object);
    }

    public boolean removeLastOccurrence(Object object) {
        return this.delegate().removeLastOccurrence(object);
    }
}
