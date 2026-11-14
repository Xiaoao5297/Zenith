package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;
import java.util.NoSuchElementException;

@GwtCompatible
abstract class AbstractIterator<T> implements Iterator<T> {

    private AbstractIterator.State state;
    private T next;

    protected AbstractIterator() {
        this.state = AbstractIterator.State.NOT_READY;
    }

    protected abstract T computeNext();

    protected final T endOfData() {
        this.state = AbstractIterator.State.DONE;
        return null;
    }

    public final boolean hasNext() {
        Preconditions.checkState(this.state != AbstractIterator.State.FAILED);
        switch (this.state) {
            case DONE:
                return false;
            case READY:
                return true;
            default:
                return this.tryToComputeNext();
        }
    }

    private boolean tryToComputeNext() {
        this.state = AbstractIterator.State.FAILED;
        this.next = this.computeNext();
        if (this.state != AbstractIterator.State.DONE) {
            this.state = AbstractIterator.State.READY;
            return true;
        } else {
            return false;
        }
    }

    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            this.state = AbstractIterator.State.NOT_READY;
            Object object = this.next;

            this.next = null;
            return object;
        }
    }

    public final void remove() {
        throw new UnsupportedOperationException();
    }

    private static enum State {

        READY, NOT_READY, DONE, FAILED;

        private State() {}
    }
}
