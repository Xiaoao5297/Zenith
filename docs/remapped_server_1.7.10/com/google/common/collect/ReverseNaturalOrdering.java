package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;

@GwtCompatible(serializable = true)
final class ReverseNaturalOrdering extends Ordering<Comparable> implements Serializable {

    static final ReverseNaturalOrdering INSTANCE = new ReverseNaturalOrdering();
    private static final long serialVersionUID = 0L;

    public int compare(Comparable comparable, Comparable comparable1) {
        Preconditions.checkNotNull(comparable);
        return comparable == comparable1 ? 0 : comparable1.compareTo(comparable);
    }

    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return Ordering.natural();
    }

    public <E extends Comparable> E min(E e0, E e1) {
        return (Comparable) NaturalOrdering.INSTANCE.max(e0, e1);
    }

    public <E extends Comparable> E min(E e0, E e1, E e2, E... ae) {
        return (Comparable) NaturalOrdering.INSTANCE.max(e0, e1, e2, ae);
    }

    @Override
    public <E extends Comparable> E min(Iterator<E> iterator) {
        return (Comparable) NaturalOrdering.INSTANCE.max(iterator);
    }

    @Override
    public <E extends Comparable> E min(Iterable<E> iterable) {
        return (Comparable) NaturalOrdering.INSTANCE.max(iterable);
    }

    public <E extends Comparable> E max(E e0, E e1) {
        return (Comparable) NaturalOrdering.INSTANCE.min(e0, e1);
    }

    public <E extends Comparable> E max(E e0, E e1, E e2, E... ae) {
        return (Comparable) NaturalOrdering.INSTANCE.min(e0, e1, e2, ae);
    }

    @Override
    public <E extends Comparable> E max(Iterator<E> iterator) {
        return (Comparable) NaturalOrdering.INSTANCE.min(iterator);
    }

    @Override
    public <E extends Comparable> E max(Iterable<E> iterable) {
        return (Comparable) NaturalOrdering.INSTANCE.min(iterable);
    }

    private Object readResolve() {
        return ReverseNaturalOrdering.INSTANCE;
    }

    public String toString() {
        return "Ordering.natural().reverse()";
    }

    private ReverseNaturalOrdering() {}
}
