package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
final class SingletonImmutableSet<E> extends ImmutableSet<E> {

    final transient E element;
    private transient int cachedHashCode;

    SingletonImmutableSet(E e0) {
        this.element = Preconditions.checkNotNull(e0);
    }

    SingletonImmutableSet(E e0, int i) {
        this.element = e0;
        this.cachedHashCode = i;
    }

    public int size() {
        return 1;
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object object) {
        return this.element.equals(object);
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        aobject[i] = this.element;
        return i + 1;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof Set)) {
            return false;
        } else {
            Set set = (Set) object;

            return set.size() == 1 && this.element.equals(set.iterator().next());
        }
    }

    @Override
    public final int hashCode() {
        int i = this.cachedHashCode;

        if (i == 0) {
            this.cachedHashCode = i = this.element.hashCode();
        }

        return i;
    }

    @Override
    boolean isHashCodeFast() {
        return this.cachedHashCode != 0;
    }

    public String toString() {
        String s = this.element.toString();

        return (new StringBuilder(s.length() + 2)).append('[').append(s).append(']').toString();
    }
}
