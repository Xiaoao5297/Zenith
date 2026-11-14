package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public abstract class ImmutableCollection<E> extends AbstractCollection<E> implements Serializable {

    private transient ImmutableList<E> asList;

    ImmutableCollection() {}

    public abstract UnmodifiableIterator<E> iterator();

    public final Object[] toArray() {
        int i = this.size();

        if (i == 0) {
            return ObjectArrays.EMPTY_ARRAY;
        } else {
            Object[] aobject = new Object[this.size()];

            this.copyIntoArray(aobject, 0);
            return aobject;
        }
    }

    public final <T> T[] toArray(T[] at) {
        Preconditions.checkNotNull(at);
        int i = this.size();

        if (at.length < i) {
            at = ObjectArrays.newArray(at, i);
        } else if (at.length > i) {
            at[i] = null;
        }

        this.copyIntoArray(at, 0);
        return at;
    }

    public boolean contains(@Nullable Object object) {
        return object != null && super.contains(object);
    }

    /** @deprecated */
    @Deprecated
    public final boolean add(E e0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final boolean addAll(Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    public ImmutableList<E> asList() {
        ImmutableList immutablelist = this.asList;

        return immutablelist == null ? (this.asList = this.createAsList()) : immutablelist;
    }

    ImmutableList<E> createAsList() {
        switch (this.size()) {
            case 0:
                return ImmutableList.of();
            case 1:
                return ImmutableList.of(this.iterator().next());
            default:
                return new RegularImmutableAsList<>(this, this.toArray());
        }
    }

    abstract boolean isPartialView();

    int copyIntoArray(Object[] aobject, int i) {
        Object object;

        for (Iterator iterator = this.iterator(); iterator.hasNext(); aobject[i++] = object) {
            object = iterator.next();
        }

        return i;
    }

    Object writeReplace() {
        return new ImmutableList.SerializedForm(this.toArray());
    }

    abstract static class ArrayBasedBuilder<E> extends ImmutableCollection.Builder<E> {

        Object[] contents;
        int size;

        ArrayBasedBuilder(int i) {
            CollectPreconditions.checkNonnegative(i, "initialCapacity");
            this.contents = new Object[i];
            this.size = 0;
        }

        private void ensureCapacity(int i) {
            if (this.contents.length < i) {
                this.contents = ObjectArrays.arraysCopyOf(this.contents, expandedCapacity(this.contents.length, i));
            }

        }

        @Override
        public ImmutableCollection.ArrayBasedBuilder<E> add(E e0) {
            Preconditions.checkNotNull(e0);
            this.ensureCapacity(this.size + 1);
            this.contents[this.size++] = e0;
            return this;
        }

        @Override
        public ImmutableCollection.Builder<E> add(E... ae) {
            ObjectArrays.checkElementsNotNull(ae);
            this.ensureCapacity(this.size + ae.length);
            System.arraycopy(ae, 0, this.contents, this.size, ae.length);
            this.size += ae.length;
            return this;
        }

        @Override
        public ImmutableCollection.Builder<E> addAll(Iterable<? extends E> iterable) {
            if (iterable instanceof Collection) {
                Collection collection = (Collection) iterable;

                this.ensureCapacity(this.size + collection.size());
            }

            super.addAll(iterable);
            return this;
        }
    }

    public abstract static class Builder<E> {

        static final int DEFAULT_INITIAL_CAPACITY = 4;

        static int expandedCapacity(int i, int j) {
            if (j < 0) {
                throw new AssertionError("cannot store more than MAX_VALUE elements");
            } else {
                int k = i + (i >> 1) + 1;

                if (k < j) {
                    k = Integer.highestOneBit(j - 1) << 1;
                }

                if (k < 0) {
                    k = Integer.MAX_VALUE;
                }

                return k;
            }
        }

        Builder() {}

        public abstract ImmutableCollection.Builder<E> add(E e0);

        public ImmutableCollection.Builder<E> add(E... ae) {
            Object[] aobject = ae;
            int i = ae.length;

            for (int j = 0; j < i; ++j) {
                Object object = aobject[j];

                this.add(object);
            }

            return this;
        }

        public ImmutableCollection.Builder<E> addAll(Iterable<? extends E> iterable) {
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                this.add(object);
            }

            return this;
        }

        public ImmutableCollection.Builder<E> addAll(Iterator<? extends E> iterator) {
            while (iterator.hasNext()) {
                this.add(iterator.next());
            }

            return this;
        }

        public abstract ImmutableCollection<E> build();
    }
}
