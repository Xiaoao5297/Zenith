package io.netty.channel.group;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class CombinedIterator<E> implements Iterator<E> {

    private final Iterator<E> i1;
    private final Iterator<E> i2;
    private Iterator<E> currentIterator;

    CombinedIterator(Iterator<E> iterator, Iterator<E> iterator1) {
        if (iterator == null) {
            throw new NullPointerException("i1");
        } else if (iterator1 == null) {
            throw new NullPointerException("i2");
        } else {
            this.i1 = iterator;
            this.i2 = iterator1;
            this.currentIterator = iterator;
        }
    }

    public boolean hasNext() {
        while (!this.currentIterator.hasNext()) {
            if (this.currentIterator != this.i1) {
                return false;
            }

            this.currentIterator = this.i2;
        }

        return true;
    }

    public E next() {
        while (true) {
            try {
                return this.currentIterator.next();
            } catch (NoSuchElementException nosuchelementexception) {
                if (this.currentIterator != this.i1) {
                    throw nosuchelementexception;
                }

                this.currentIterator = this.i2;
            }
        }
    }

    public void remove() {
        this.currentIterator.remove();
    }
}
