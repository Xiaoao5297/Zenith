package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class NaturalOrdering extends Ordering<Comparable> implements Serializable {

    static final NaturalOrdering INSTANCE = new NaturalOrdering();
    private static final long serialVersionUID = 0L;

    public int compare(Comparable comparable, Comparable comparable1) {
        Preconditions.checkNotNull(comparable);
        Preconditions.checkNotNull(comparable1);
        return comparable.compareTo(comparable1);
    }

    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return ReverseNaturalOrdering.INSTANCE;
    }

    private Object readResolve() {
        return NaturalOrdering.INSTANCE;
    }

    public String toString() {
        return "Ordering.natural()";
    }

    private NaturalOrdering() {}
}
