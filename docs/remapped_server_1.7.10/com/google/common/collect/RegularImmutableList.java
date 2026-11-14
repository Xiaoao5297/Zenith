package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
class RegularImmutableList<E> extends ImmutableList<E> {

    private final transient int offset;
    private final transient int size;
    private final transient Object[] array;

    RegularImmutableList(Object[] aobject, int i, int j) {
        this.offset = i;
        this.size = j;
        this.array = aobject;
    }

    RegularImmutableList(Object[] aobject) {
        this(aobject, 0, aobject.length);
    }

    public int size() {
        return this.size;
    }

    @Override
    boolean isPartialView() {
        return this.size != this.array.length;
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        System.arraycopy(this.array, this.offset, aobject, i, this.size);
        return i + this.size;
    }

    public E get(int i) {
        Preconditions.checkElementIndex(i, this.size);
        return this.array[i + this.offset];
    }

    @Override
    public int indexOf(@Nullable Object object) {
        if (object == null) {
            return -1;
        } else {
            for (int i = 0; i < this.size; ++i) {
                if (this.array[this.offset + i].equals(object)) {
                    return i;
                }
            }

            return -1;
        }
    }

    @Override
    public int lastIndexOf(@Nullable Object object) {
        if (object == null) {
            return -1;
        } else {
            for (int i = this.size - 1; i >= 0; --i) {
                if (this.array[this.offset + i].equals(object)) {
                    return i;
                }
            }

            return -1;
        }
    }

    @Override
    ImmutableList<E> subListUnchecked(int i, int j) {
        return new RegularImmutableList<>(this.array, this.offset + i, j - i);
    }

    @Override
    public UnmodifiableListIterator<E> listIterator(int i) {
        return Iterators.forArray(this.array, this.offset, this.size, i);
    }
}
