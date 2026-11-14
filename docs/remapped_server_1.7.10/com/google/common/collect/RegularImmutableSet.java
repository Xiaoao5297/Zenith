package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;

@GwtCompatible(serializable = true, emulated = true)
final class RegularImmutableSet<E> extends ImmutableSet<E> {

    private final Object[] elements;
    @VisibleForTesting
    final transient Object[] table;
    private final transient int mask;
    private final transient int hashCode;

    RegularImmutableSet(Object[] aobject, int i, Object[] aobject1, int j) {
        this.elements = aobject;
        this.table = aobject1;
        this.mask = j;
        this.hashCode = i;
    }

    @Override
    public boolean contains(Object object) {
        if (object == null) {
            return false;
        } else {
            int i = Hashing.smear(object.hashCode());

            while (true) {
                Object object1 = this.table[i & this.mask];

                if (object1 == null) {
                    return false;
                }

                if (object1.equals(object)) {
                    return true;
                }

                ++i;
            }
        }
    }

    public int size() {
        return this.elements.length;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.forArray(this.elements);
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        System.arraycopy(this.elements, 0, aobject, i, this.elements.length);
        return i + this.elements.length;
    }

    @Override
    ImmutableList<E> createAsList() {
        return new RegularImmutableAsList<>(this, this.elements);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }
}
