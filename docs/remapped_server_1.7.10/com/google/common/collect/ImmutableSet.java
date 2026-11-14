package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {

    static final int MAX_TABLE_SIZE = 1073741824;
    private static final double DESIRED_LOAD_FACTOR = 0.7D;
    private static final int CUTOFF = 751619276;

    public static <E> ImmutableSet<E> of() {
        return EmptyImmutableSet.INSTANCE;
    }

    public static <E> ImmutableSet<E> of(E e0) {
        return new SingletonImmutableSet<>(e0);
    }

    public static <E> ImmutableSet<E> of(E e0, E e1) {
        return construct(2, e0, e1);
    }

    public static <E> ImmutableSet<E> of(E e0, E e1, E e2) {
        return construct(3, e0, e1, e2);
    }

    public static <E> ImmutableSet<E> of(E e0, E e1, E e2, E e3) {
        return construct(4, e0, e1, e2, e3);
    }

    public static <E> ImmutableSet<E> of(E e0, E e1, E e2, E e3, E e4) {
        return construct(5, e0, e1, e2, e3, e4);
    }

    public static <E> ImmutableSet<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E... ae) {
        boolean flag = true;
        Object[] aobject = new Object[6 + ae.length];

        aobject[0] = e0;
        aobject[1] = e1;
        aobject[2] = e2;
        aobject[3] = e3;
        aobject[4] = e4;
        aobject[5] = e5;
        System.arraycopy(ae, 0, aobject, 6, ae.length);
        return construct(aobject.length, aobject);
    }

    private static <E> ImmutableSet<E> construct(int i, Object... aobject) {
        switch (i) {
            case 0:
                return of();
            case 1:
                Object object = aobject[0];

                return of(object);
            default:
                int j = chooseTableSize(i);
                Object[] aobject1 = new Object[j];
                int k = j - 1;
                int l = 0;
                int i1 = 0;
                int j1 = 0;

                while (j1 < i) {
                    Object object1 = ObjectArrays.checkElementNotNull(aobject[j1], j1);
                    int k1 = object1.hashCode();
                    int l1 = Hashing.smear(k1);

                    while (true) {
                        int i2 = l1 & k;
                        Object object2 = aobject1[i2];

                        if (object2 == null) {
                            aobject[i1++] = object1;
                            aobject1[i2] = object1;
                            l += k1;
                        } else if (!object2.equals(object1)) {
                            ++l1;
                            continue;
                        }

                        ++j1;
                        break;
                    }
                }

                Arrays.fill(aobject, i1, i, (Object) null);
                if (i1 == 1) {
                    Object object3 = aobject[0];

                    return new SingletonImmutableSet<>(object3, l);
                } else if (j != chooseTableSize(i1)) {
                    return construct(i1, aobject);
                } else {
                    Object[] aobject2 = i1 < aobject.length ? ObjectArrays.arraysCopyOf(aobject, i1) : aobject;

                    return new RegularImmutableSet<>(aobject2, l, aobject1, k);
                }
        }
    }

    @VisibleForTesting
    static int chooseTableSize(int i) {
        if (i >= 751619276) {
            Preconditions.checkArgument(i < 1073741824, "collection too large");
            return 1073741824;
        } else {
            int j;

            for (j = Integer.highestOneBit(i - 1) << 1; (double) j * 0.7D < (double) i; j <<= 1) {
                ;
            }

            return j;
        }
    }

    public static <E> ImmutableSet<E> copyOf(E[] ae) {
        switch (ae.length) {
            case 0:
                return of();
            case 1:
                return of(ae[0]);
            default:
                return construct(ae.length, (Object[]) ae.clone());
        }
    }

    public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> iterable) {
        return iterable instanceof Collection ? copyOf(Collections2.cast(iterable)) : copyOf(iterable.iterator());
    }

    public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> iterator) {
        if (!iterator.hasNext()) {
            return of();
        } else {
            Object object = iterator.next();

            return !iterator.hasNext() ? of(object) : (new ImmutableSet.Builder<>()).add(object).addAll(iterator).build();
        }
    }

    public static <E> ImmutableSet<E> copyOf(Collection<? extends E> collection) {
        if (collection instanceof ImmutableSet && !(collection instanceof ImmutableSortedSet)) {
            ImmutableSet immutableset = (ImmutableSet) collection;

            if (!immutableset.isPartialView()) {
                return immutableset;
            }
        } else if (collection instanceof EnumSet) {
            return copyOfEnumSet((EnumSet) collection);
        }

        Object[] aobject = collection.toArray();

        return construct(aobject.length, aobject);
    }

    private static <E extends Enum<E>> ImmutableSet<E> copyOfEnumSet(EnumSet<E> enumset) {
        return ImmutableEnumSet.asImmutable(EnumSet.copyOf(enumset));
    }

    ImmutableSet() {}

    boolean isHashCodeFast() {
        return false;
    }

    public boolean equals(@Nullable Object object) {
        return object == this ? true : (object instanceof ImmutableSet && this.isHashCodeFast() && ((ImmutableSet) object).isHashCodeFast() && this.hashCode() != object.hashCode() ? false : Sets.equalsImpl(this, object));
    }

    public int hashCode() {
        return Sets.hashCodeImpl(this);
    }

    @Override
    public abstract UnmodifiableIterator<E> iterator();

    @Override
    Object writeReplace() {
        return new ImmutableSet.SerializedForm(this.toArray());
    }

    public static <E> ImmutableSet.Builder<E> builder() {
        return new ImmutableSet.Builder<>();
    }

    public static class Builder<E> extends ImmutableCollection.ArrayBasedBuilder<E> {

        public Builder() {
            this(4);
        }

        Builder(int i) {
            super(i);
        }

        @Override
        public ImmutableSet.Builder<E> add(E e0) {
            super.add(e0);
            return this;
        }

        @Override
        public ImmutableSet.Builder<E> add(E... ae) {
            super.add(ae);
            return this;
        }

        @Override
        public ImmutableSet.Builder<E> addAll(Iterable<? extends E> iterable) {
            super.addAll(iterable);
            return this;
        }

        @Override
        public ImmutableSet.Builder<E> addAll(Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }

        @Override
        public ImmutableSet<E> build() {
            ImmutableSet immutableset = ImmutableSet.construct(this.size, this.contents);

            this.size = immutableset.size();
            return immutableset;
        }
    }

    private static class SerializedForm implements Serializable {

        final Object[] elements;
        private static final long serialVersionUID = 0L;

        SerializedForm(Object[] aobject) {
            this.elements = aobject;
        }

        Object readResolve() {
            return ImmutableSet.copyOf(this.elements);
        }
    }
}
