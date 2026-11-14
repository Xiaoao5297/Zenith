package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableMultiset<E> extends ImmutableCollection<E> implements Multiset<E> {

    private static final ImmutableMultiset<Object> EMPTY = new RegularImmutableMultiset<>(ImmutableMap.of(), 0);
    private transient ImmutableSet<Multiset.Entry<E>> entrySet;

    public static <E> ImmutableMultiset<E> of() {
        return ImmutableMultiset.EMPTY;
    }

    public static <E> ImmutableMultiset<E> of(E e0) {
        return copyOfInternal(e0);
    }

    public static <E> ImmutableMultiset<E> of(E e0, E e1) {
        return copyOfInternal(e0, e1);
    }

    public static <E> ImmutableMultiset<E> of(E e0, E e1, E e2) {
        return copyOfInternal(e0, e1, e2);
    }

    public static <E> ImmutableMultiset<E> of(E e0, E e1, E e2, E e3) {
        return copyOfInternal(e0, e1, e2, e3);
    }

    public static <E> ImmutableMultiset<E> of(E e0, E e1, E e2, E e3, E e4) {
        return copyOfInternal(e0, e1, e2, e3, e4);
    }

    public static <E> ImmutableMultiset<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E... ae) {
        return (new ImmutableMultiset.Builder<>()).add(e0).add(e1).add(e2).add(e3).add(e4).add(e5).add(ae).build();
    }

    public static <E> ImmutableMultiset<E> copyOf(E[] ae) {
        return copyOf((Iterable) Arrays.asList(ae));
    }

    public static <E> ImmutableMultiset<E> copyOf(Iterable<? extends E> iterable) {
        if (iterable instanceof ImmutableMultiset) {
            ImmutableMultiset immutablemultiset = (ImmutableMultiset) iterable;

            if (!immutablemultiset.isPartialView()) {
                return immutablemultiset;
            }
        }

        Object object = iterable instanceof Multiset ? Multisets.cast(iterable) : LinkedHashMultiset.create(iterable);

        return copyOfInternal((Multiset) object);
    }

    private static <E> ImmutableMultiset<E> copyOfInternal(E... ae) {
        return copyOf((Iterable) Arrays.asList(ae));
    }

    private static <E> ImmutableMultiset<E> copyOfInternal(Multiset<? extends E> multiset) {
        return copyFromEntries(multiset.entrySet());
    }

    static <E> ImmutableMultiset<E> copyFromEntries(Collection<? extends Multiset.Entry<? extends E>> collection) {
        long i = 0L;
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();
            int j = multiset_entry.getCount();

            if (j > 0) {
                immutablemap_builder.put(multiset_entry.getElement(), j);
                i += (long) j;
            }
        }

        if (i == 0L) {
            return of();
        } else {
            return new RegularImmutableMultiset<>(immutablemap_builder.build(), Ints.saturatedCast(i));
        }
    }

    public static <E> ImmutableMultiset<E> copyOf(Iterator<? extends E> iterator) {
        LinkedHashMultiset linkedhashmultiset = LinkedHashMultiset.create();

        Iterators.addAll(linkedhashmultiset, iterator);
        return copyOfInternal((Multiset) linkedhashmultiset);
    }

    ImmutableMultiset() {}

    @Override
    public UnmodifiableIterator<E> iterator() {
        final UnmodifiableIterator unmodifiableiterator = this.entrySet().iterator();

        return new UnmodifiableIterator<E>() {
            int remaining;
            E element;

            public boolean hasNext() {
                return this.remaining > 0 || unmodifiableiterator.hasNext();
            }

            public E next() {
                if (this.remaining <= 0) {
                    Multiset.Entry multiset_entry = (Multiset.Entry) unmodifiableiterator.next();

                    this.element = multiset_entry.getElement();
                    this.remaining = multiset_entry.getCount();
                }

                --this.remaining;
                return this.element;
            }
        };
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.count(object) > 0;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.elementSet().containsAll(collection);
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final int add(E e0, int i) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final int remove(Object object, int i) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final int setCount(E e0, int i) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final boolean setCount(E e0, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @GwtIncompatible("not present in emulated superclass")
    @Override
    int copyIntoArray(Object[] aobject, int i) {
        Multiset.Entry multiset_entry;

        for (Iterator iterator = this.entrySet().iterator(); iterator.hasNext(); i += multiset_entry.getCount()) {
            multiset_entry = (Multiset.Entry) iterator.next();
            Arrays.fill(aobject, i, i + multiset_entry.getCount(), multiset_entry.getElement());
        }

        return i;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return Multisets.equalsImpl(this, object);
    }

    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }

    @Override
    public String toString() {
        return this.entrySet().toString();
    }

    @Override
    public ImmutableSet<Multiset.Entry<E>> entrySet() {
        ImmutableSet immutableset = this.entrySet;

        return immutableset == null ? (this.entrySet = this.createEntrySet()) : immutableset;
    }

    private final ImmutableSet<Multiset.Entry<E>> createEntrySet() {
        return (ImmutableSet) (this.isEmpty() ? ImmutableSet.of() : new ImmutableMultiset.EntrySet());
    }

    abstract Multiset.Entry<E> getEntry(int i);

    @Override
    Object writeReplace() {
        return new ImmutableMultiset.SerializedForm(this);
    }

    public static <E> ImmutableMultiset.Builder<E> builder() {
        return new ImmutableMultiset.Builder<>();
    }

    public static class Builder<E> extends ImmutableCollection.Builder<E> {

        final Multiset<E> contents;

        public Builder() {
            this(LinkedHashMultiset.create());
        }

        Builder(Multiset<E> multiset) {
            this.contents = multiset;
        }

        @Override
        public ImmutableMultiset.Builder<E> add(E e0) {
            this.contents.add(Preconditions.checkNotNull(e0));
            return this;
        }

        public ImmutableMultiset.Builder<E> addCopies(E e0, int i) {
            this.contents.add(Preconditions.checkNotNull(e0), i);
            return this;
        }

        public ImmutableMultiset.Builder<E> setCount(E e0, int i) {
            this.contents.setCount(Preconditions.checkNotNull(e0), i);
            return this;
        }

        @Override
        public ImmutableMultiset.Builder<E> add(E... ae) {
            super.add(ae);
            return this;
        }

        @Override
        public ImmutableMultiset.Builder<E> addAll(Iterable<? extends E> iterable) {
            if (iterable instanceof Multiset) {
                Multiset multiset = Multisets.cast(iterable);
                Iterator iterator = multiset.entrySet().iterator();

                while (iterator.hasNext()) {
                    Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

                    this.addCopies(multiset_entry.getElement(), multiset_entry.getCount());
                }
            } else {
                super.addAll(iterable);
            }

            return this;
        }

        @Override
        public ImmutableMultiset.Builder<E> addAll(Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }

        @Override
        public ImmutableMultiset<E> build() {
            return ImmutableMultiset.copyOf((Iterable) this.contents);
        }
    }

    private static class SerializedForm implements Serializable {

        final Object[] elements;
        final int[] counts;
        private static final long serialVersionUID = 0L;

        SerializedForm(Multiset<?> multiset) {
            int i = multiset.entrySet().size();

            this.elements = new Object[i];
            this.counts = new int[i];
            int j = 0;

            for (Iterator iterator = multiset.entrySet().iterator(); iterator.hasNext(); ++j) {
                Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

                this.elements[j] = multiset_entry.getElement();
                this.counts[j] = multiset_entry.getCount();
            }

        }

        Object readResolve() {
            LinkedHashMultiset linkedhashmultiset = LinkedHashMultiset.create(this.elements.length);

            for (int i = 0; i < this.elements.length; ++i) {
                linkedhashmultiset.add(this.elements[i], this.counts[i]);
            }

            return ImmutableMultiset.copyOf((Iterable) linkedhashmultiset);
        }
    }

    static class EntrySetSerializedForm<E> implements Serializable {

        final ImmutableMultiset<E> multiset;

        EntrySetSerializedForm(ImmutableMultiset<E> immutablemultiset) {
            this.multiset = immutablemultiset;
        }

        Object readResolve() {
            return this.multiset.entrySet();
        }
    }

    private final class EntrySet extends ImmutableSet<Multiset.Entry<E>> {

        private static final long serialVersionUID = 0L;

        private EntrySet() {}

        @Override
        boolean isPartialView() {
            return ImmutableMultiset.this.isPartialView();
        }

        @Override
        public UnmodifiableIterator<Multiset.Entry<E>> iterator() {
            return this.asList().iterator();
        }

        @Override
        ImmutableList<Multiset.Entry<E>> createAsList() {
            return new ImmutableAsList<Multiset.Entry<E>>() {
                public Multiset.Entry<E> get(int i) {
                    return ImmutableMultiset.this.getEntry(i);
                }

                @Override
                ImmutableCollection<Multiset.Entry<E>> delegateCollection() {
                    return EntrySet.this;
                }
            };
        }

        public int size() {
            return ImmutableMultiset.this.elementSet().size();
        }

        @Override
        public boolean contains(Object object) {
            if (object instanceof Multiset.Entry) {
                Multiset.Entry multiset_entry = (Multiset.Entry) object;

                if (multiset_entry.getCount() <= 0) {
                    return false;
                } else {
                    int i = ImmutableMultiset.this.count(multiset_entry.getElement());

                    return i == multiset_entry.getCount();
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return ImmutableMultiset.this.hashCode();
        }

        @Override
        Object writeReplace() {
            return new ImmutableMultiset.EntrySetSerializedForm<>(ImmutableMultiset.this);
        }
    }
}
