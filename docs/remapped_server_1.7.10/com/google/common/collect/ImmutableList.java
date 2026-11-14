package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E>, RandomAccess {

    private static final ImmutableList<Object> EMPTY = new RegularImmutableList<>(ObjectArrays.EMPTY_ARRAY);

    public static <E> ImmutableList<E> of() {
        return ImmutableList.EMPTY;
    }

    public static <E> ImmutableList<E> of(E e0) {
        return new SingletonImmutableList<>(e0);
    }

    public static <E> ImmutableList<E> of(E e0, E e1) {
        return construct(e0, e1);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2) {
        return construct(e0, e1, e2);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3) {
        return construct(e0, e1, e2, e3);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4) {
        return construct(e0, e1, e2, e3, e4);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4, E e5) {
        return construct(e0, e1, e2, e3, e4, e5);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E e6) {
        return construct(e0, e1, e2, e3, e4, e5, e6);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return construct(e0, e1, e2, e3, e4, e5, e6, e7);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return construct(e0, e1, e2, e3, e4, e5, e6, e7, e8);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return construct(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return construct(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
    }

    public static <E> ImmutableList<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E... ae) {
        Object[] aobject = new Object[12 + ae.length];

        aobject[0] = e0;
        aobject[1] = e1;
        aobject[2] = e2;
        aobject[3] = e3;
        aobject[4] = e4;
        aobject[5] = e5;
        aobject[6] = e6;
        aobject[7] = e7;
        aobject[8] = e8;
        aobject[9] = e9;
        aobject[10] = e10;
        aobject[11] = e11;
        System.arraycopy(ae, 0, aobject, 12, ae.length);
        return construct(aobject);
    }

    public static <E> ImmutableList<E> copyOf(Iterable<? extends E> iterable) {
        Preconditions.checkNotNull(iterable);
        return iterable instanceof Collection ? copyOf(Collections2.cast(iterable)) : copyOf(iterable.iterator());
    }

    public static <E> ImmutableList<E> copyOf(Collection<? extends E> collection) {
        if (collection instanceof ImmutableCollection) {
            ImmutableList immutablelist = ((ImmutableCollection) collection).asList();

            return immutablelist.isPartialView() ? asImmutableList(immutablelist.toArray()) : immutablelist;
        } else {
            return construct(collection.toArray());
        }
    }

    public static <E> ImmutableList<E> copyOf(Iterator<? extends E> iterator) {
        if (!iterator.hasNext()) {
            return of();
        } else {
            Object object = iterator.next();

            return !iterator.hasNext() ? of(object) : (new ImmutableList.Builder<>()).add(object).addAll(iterator).build();
        }
    }

    public static <E> ImmutableList<E> copyOf(E[] ae) {
        switch (ae.length) {
            case 0:
                return of();
            case 1:
                return new SingletonImmutableList<>(ae[0]);
            default:
                return new RegularImmutableList<>(ObjectArrays.checkElementsNotNull((Object[]) ae.clone()));
        }
    }

    private static <E> ImmutableList<E> construct(Object... aobject) {
        return asImmutableList(ObjectArrays.checkElementsNotNull(aobject));
    }

    static <E> ImmutableList<E> asImmutableList(Object[] aobject) {
        return asImmutableList(aobject, aobject.length);
    }

    static <E> ImmutableList<E> asImmutableList(Object[] aobject, int i) {
        switch (i) {
            case 0:
                return of();
            case 1:
                SingletonImmutableList singletonimmutablelist = new SingletonImmutableList<>(aobject[0]);

                return singletonimmutablelist;
            default:
                if (i < aobject.length) {
                    aobject = ObjectArrays.arraysCopyOf(aobject, i);
                }

                return new RegularImmutableList<>(aobject);
        }
    }

    ImmutableList() {}

    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.listIterator();
    }

    public UnmodifiableListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    public UnmodifiableListIterator<E> listIterator(int i) {
        return new AbstractIndexedListIterator<E>(this.size(), i) {
            @Override
            protected E get(int j) {
                return ImmutableList.this.get(j);
            }
        };
    }

    public int indexOf(@Nullable Object object) {
        return object == null ? -1 : Lists.indexOfImpl(this, object);
    }

    public int lastIndexOf(@Nullable Object object) {
        return object == null ? -1 : Lists.lastIndexOfImpl(this, object);
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.indexOf(object) >= 0;
    }

    public ImmutableList<E> subList(int i, int j) {
        Preconditions.checkPositionIndexes(i, j, this.size());
        int k = j - i;

        switch (k) {
            case 0:
                return of();
            case 1:
                return of(this.get(i));
            default:
                return this.subListUnchecked(i, j);
        }
    }

    ImmutableList<E> subListUnchecked(int i, int j) {
        return new ImmutableList.SubList(i, j - i);
    }

    /** @deprecated */
    @Deprecated
    public final boolean addAll(int i, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final E set(int i, E e0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final void add(int i, E e0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final E remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final ImmutableList<E> asList() {
        return this;
    }

    @Override
    int copyIntoArray(Object[] aobject, int i) {
        int j = this.size();

        for (int k = 0; k < j; ++k) {
            aobject[i + k] = this.get(k);
        }

        return i + j;
    }

    public ImmutableList<E> reverse() {
        return new ImmutableList.ReverseImmutableList<>(this);
    }

    public boolean equals(@Nullable Object object) {
        return Lists.equalsImpl(this, object);
    }

    public int hashCode() {
        int i = 1;
        int j = this.size();

        for (int k = 0; k < j; ++k) {
            i = 31 * i + this.get(k).hashCode();
            i = ~(~i);
        }

        return i;
    }

    private void readObject(ObjectInputStream objectinputstream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    @Override
    Object writeReplace() {
        return new ImmutableList.SerializedForm(this.toArray());
    }

    public static <E> ImmutableList.Builder<E> builder() {
        return new ImmutableList.Builder<>();
    }

    public static final class Builder<E> extends ImmutableCollection.ArrayBasedBuilder<E> {

        public Builder() {
            this(4);
        }

        Builder(int i) {
            super(i);
        }

        @Override
        public ImmutableList.Builder<E> add(E e0) {
            super.add(e0);
            return this;
        }

        @Override
        public ImmutableList.Builder<E> addAll(Iterable<? extends E> iterable) {
            super.addAll(iterable);
            return this;
        }

        @Override
        public ImmutableList.Builder<E> add(E... ae) {
            super.add(ae);
            return this;
        }

        @Override
        public ImmutableList.Builder<E> addAll(Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }

        @Override
        public ImmutableList<E> build() {
            return ImmutableList.asImmutableList(this.contents, this.size);
        }
    }

    static class SerializedForm implements Serializable {

        final Object[] elements;
        private static final long serialVersionUID = 0L;

        SerializedForm(Object[] aobject) {
            this.elements = aobject;
        }

        Object readResolve() {
            return ImmutableList.copyOf(this.elements);
        }
    }

    private static class ReverseImmutableList<E> extends ImmutableList<E> {

        private final transient ImmutableList<E> forwardList;

        ReverseImmutableList(ImmutableList<E> immutablelist) {
            this.forwardList = immutablelist;
        }

        private int reverseIndex(int i) {
            return this.size() - 1 - i;
        }

        private int reversePosition(int i) {
            return this.size() - i;
        }

        @Override
        public ImmutableList<E> reverse() {
            return this.forwardList;
        }

        @Override
        public boolean contains(@Nullable Object object) {
            return this.forwardList.contains(object);
        }

        @Override
        public int indexOf(@Nullable Object object) {
            int i = this.forwardList.lastIndexOf(object);

            return i >= 0 ? this.reverseIndex(i) : -1;
        }

        @Override
        public int lastIndexOf(@Nullable Object object) {
            int i = this.forwardList.indexOf(object);

            return i >= 0 ? this.reverseIndex(i) : -1;
        }

        @Override
        public ImmutableList<E> subList(int i, int j) {
            Preconditions.checkPositionIndexes(i, j, this.size());
            return this.forwardList.subList(this.reversePosition(j), this.reversePosition(i)).reverse();
        }

        public E get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.forwardList.get(this.reverseIndex(i));
        }

        public int size() {
            return this.forwardList.size();
        }

        @Override
        boolean isPartialView() {
            return this.forwardList.isPartialView();
        }
    }

    class SubList extends ImmutableList<E> {

        final transient int offset;
        final transient int length;

        SubList(int i, int j) {
            this.offset = i;
            this.length = j;
        }

        public int size() {
            return this.length;
        }

        public E get(int i) {
            Preconditions.checkElementIndex(i, this.length);
            return ImmutableList.this.get(i + this.offset);
        }

        @Override
        public ImmutableList<E> subList(int i, int j) {
            Preconditions.checkPositionIndexes(i, j, this.length);
            return ImmutableList.this.subList(i + this.offset, j + this.offset);
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }
}
