package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T> {

    private T nextOrNull;

    protected AbstractSequentialIterator(@Nullable T t0) {
        this.nextOrNull = t0;
    }

    protected abstract T computeNext(T t0);

    public final boolean hasNext() {
        return this.nextOrNull != null;
    }

    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            Object object;

            try {
                object = this.nextOrNull;
            } finally {
                this.nextOrNull = this.computeNext(this.nextOrNull);
            }

            return object;
        }
    }
}
