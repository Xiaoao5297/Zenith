package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
final class SingletonImmutableList<E> extends ImmutableList<E> {

    final transient E element;

    SingletonImmutableList(E e0) {
        this.element = Preconditions.checkNotNull(e0);
    }

    public E get(int i) {
        Preconditions.checkElementIndex(i, 1);
        return this.element;
    }

    @Override
    public int indexOf(@Nullable Object object) {
        return this.element.equals(object) ? 0 : -1;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }

    @Override
    public int lastIndexOf(@Nullable Object object) {
        return this.indexOf(object);
    }

    public int size() {
        return 1;
    }

    @Override
    public ImmutableList<E> subList(int i, int j) {
        Preconditions.checkPositionIndexes(i, j, 1);
        return (ImmutableList) (i == j ? ImmutableList.of() : this);
    }

    @Override
    public ImmutableList<E> reverse() {
        return this;
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.element.equals(object);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof List)) {
            return false;
        } else {
            List list = (List) object;

            return list.size() == 1 && this.element.equals(list.get(0));
        }
    }

    @Override
    public int hashCode() {
        return 31 + this.element.hashCode();
    }

    public String toString() {
        String s = this.element.toString();

        return (new StringBuilder(s.length() + 2)).append('[').append(s).append(']').toString();
    }

    public boolean isEmpty() {
        return false;
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
}
