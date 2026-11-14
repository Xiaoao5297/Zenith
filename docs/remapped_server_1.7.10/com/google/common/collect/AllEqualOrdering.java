package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class AllEqualOrdering extends Ordering<Object> implements Serializable {

    static final AllEqualOrdering INSTANCE = new AllEqualOrdering();
    private static final long serialVersionUID = 0L;

    AllEqualOrdering() {}

    @Override
    public int compare(@Nullable Object object, @Nullable Object object1) {
        return 0;
    }

    @Override
    public <E> List<E> sortedCopy(Iterable<E> iterable) {
        return Lists.newArrayList(iterable);
    }

    @Override
    public <E> ImmutableList<E> immutableSortedCopy(Iterable<E> iterable) {
        return ImmutableList.copyOf(iterable);
    }

    @Override
    public <S> Ordering<S> reverse() {
        return this;
    }

    private Object readResolve() {
        return AllEqualOrdering.INSTANCE;
    }

    public String toString() {
        return "Ordering.allEqual()";
    }
}
