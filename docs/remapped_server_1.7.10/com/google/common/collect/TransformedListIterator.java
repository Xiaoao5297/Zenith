package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@GwtCompatible
abstract class TransformedListIterator<F, T> extends TransformedIterator<F, T> implements ListIterator<T> {

    TransformedListIterator(ListIterator<? extends F> listiterator) {
        super(listiterator);
    }

    private ListIterator<? extends F> backingIterator() {
        return Iterators.cast(this.backingIterator);
    }

    public final boolean hasPrevious() {
        return this.backingIterator().hasPrevious();
    }

    public final T previous() {
        return this.transform(this.backingIterator().previous());
    }

    public final int nextIndex() {
        return this.backingIterator().nextIndex();
    }

    public final int previousIndex() {
        return this.backingIterator().previousIndex();
    }

    public void set(T t0) {
        throw new UnsupportedOperationException();
    }

    public void add(T t0) {
        throw new UnsupportedOperationException();
    }
}
