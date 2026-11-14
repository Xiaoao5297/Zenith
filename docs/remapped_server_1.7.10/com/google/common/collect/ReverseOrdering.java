package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class ReverseOrdering<T> extends Ordering<T> implements Serializable {

    final Ordering<? super T> forwardOrder;
    private static final long serialVersionUID = 0L;

    ReverseOrdering(Ordering<? super T> ordering) {
        this.forwardOrder = (Ordering) Preconditions.checkNotNull(ordering);
    }

    @Override
    public int compare(T t0, T t1) {
        return this.forwardOrder.compare(t1, t0);
    }

    @Override
    public <S extends T> Ordering<S> reverse() {
        return this.forwardOrder;
    }

    @Override
    public <E extends T> E min(E e0, E e1) {
        return this.forwardOrder.max(e0, e1);
    }

    @Override
    public <E extends T> E min(E e0, E e1, E e2, E... ae) {
        return this.forwardOrder.max(e0, e1, e2, ae);
    }

    @Override
    public <E extends T> E min(Iterator<E> iterator) {
        return this.forwardOrder.max(iterator);
    }

    @Override
    public <E extends T> E min(Iterable<E> iterable) {
        return this.forwardOrder.max(iterable);
    }

    @Override
    public <E extends T> E max(E e0, E e1) {
        return this.forwardOrder.min(e0, e1);
    }

    @Override
    public <E extends T> E max(E e0, E e1, E e2, E... ae) {
        return this.forwardOrder.min(e0, e1, e2, ae);
    }

    @Override
    public <E extends T> E max(Iterator<E> iterator) {
        return this.forwardOrder.min(iterator);
    }

    @Override
    public <E extends T> E max(Iterable<E> iterable) {
        return this.forwardOrder.min(iterable);
    }

    public int hashCode() {
        return -this.forwardOrder.hashCode();
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof ReverseOrdering) {
            ReverseOrdering reverseordering = (ReverseOrdering) object;

            return this.forwardOrder.equals(reverseordering.forwardOrder);
        } else {
            return false;
        }
    }

    public String toString() {
        return this.forwardOrder + ".reverse()";
    }
}
