package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
final class EmptyImmutableSet extends ImmutableSet<Object> {

    static final EmptyImmutableSet INSTANCE = new EmptyImmutableSet();
    private static final long serialVersionUID = 0L;

    private EmptyImmutableSet() {}

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return false;
    }

    public boolean containsAll(Collection<?> collection) {
        return collection.isEmpty();
    }

    @Override
    public UnmodifiableIterator<Object> iterator() {
        return Iterators.emptyIterator();
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        return i;
    }

    @Override
    public ImmutableList<Object> asList() {
        return ImmutableList.of();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object instanceof Set) {
            Set set = (Set) object;

            return set.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return 0;
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }

    public String toString() {
        return "[]";
    }

    Object readResolve() {
        return EmptyImmutableSet.INSTANCE;
    }
}
