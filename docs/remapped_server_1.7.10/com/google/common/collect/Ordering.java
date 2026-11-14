package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Ordering<T> implements Comparator<T> {

    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;

    @GwtCompatible(serializable = true)
    public static <C extends Comparable> Ordering<C> natural() {
        return NaturalOrdering.INSTANCE;
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> from(Comparator<T> comparator) {
        return (Ordering) (comparator instanceof Ordering ? (Ordering) comparator : new ComparatorOrdering<>(comparator));
    }

    /** @deprecated */
    @Deprecated
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> from(Ordering<T> ordering) {
        return (Ordering) Preconditions.checkNotNull(ordering);
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> explicit(List<T> list) {
        return new ExplicitOrdering<>(list);
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> explicit(T t0, T... at) {
        return explicit(Lists.asList(t0, at));
    }

    @GwtCompatible(serializable = true)
    public static Ordering<Object> allEqual() {
        return AllEqualOrdering.INSTANCE;
    }

    @GwtCompatible(serializable = true)
    public static Ordering<Object> usingToString() {
        return UsingToStringOrdering.INSTANCE;
    }

    public static Ordering<Object> arbitrary() {
        return Ordering.ArbitraryOrderingHolder.ARBITRARY_ORDERING;
    }

    protected Ordering() {}

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering<>(this);
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> nullsFirst() {
        return new NullsFirstOrdering<>(this);
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> nullsLast() {
        return new NullsLastOrdering<>(this);
    }

    @GwtCompatible(serializable = true)
    public <F> Ordering<F> onResultOf(Function<F, ? extends T> function) {
        return new ByFunctionOrdering<>(function, this);
    }

    <T2 extends T> Ordering<Entry<T2, ?>> onKeys() {
        return this.onResultOf(Maps.keyFunction());
    }

    @GwtCompatible(serializable = true)
    public <U extends T> Ordering<U> compound(Comparator<? super U> comparator) {
        return new CompoundOrdering<>(this, (Comparator) Preconditions.checkNotNull(comparator));
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> iterable) {
        return new CompoundOrdering<>(iterable);
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<Iterable<S>> lexicographical() {
        return new LexicographicalOrdering<>(this);
    }

    public abstract int compare(@Nullable T t0, @Nullable T t1);

    public <E extends T> E min(Iterator<E> iterator) {
        Object object;

        for (object = iterator.next(); iterator.hasNext(); object = this.min(object, iterator.next())) {
            ;
        }

        return object;
    }

    public <E extends T> E min(Iterable<E> iterable) {
        return this.min(iterable.iterator());
    }

    public <E extends T> E min(@Nullable E e0, @Nullable E e1) {
        return this.compare(e0, e1) <= 0 ? e0 : e1;
    }

    public <E extends T> E min(@Nullable E e0, @Nullable E e1, @Nullable E e2, E... ae) {
        Object object = this.min(this.min(e0, e1), e2);
        Object[] aobject = ae;
        int i = ae.length;

        for (int j = 0; j < i; ++j) {
            Object object1 = aobject[j];

            object = this.min(object, object1);
        }

        return object;
    }

    public <E extends T> E max(Iterator<E> iterator) {
        Object object;

        for (object = iterator.next(); iterator.hasNext(); object = this.max(object, iterator.next())) {
            ;
        }

        return object;
    }

    public <E extends T> E max(Iterable<E> iterable) {
        return this.max(iterable.iterator());
    }

    public <E extends T> E max(@Nullable E e0, @Nullable E e1) {
        return this.compare(e0, e1) >= 0 ? e0 : e1;
    }

    public <E extends T> E max(@Nullable E e0, @Nullable E e1, @Nullable E e2, E... ae) {
        Object object = this.max(this.max(e0, e1), e2);
        Object[] aobject = ae;
        int i = ae.length;

        for (int j = 0; j < i; ++j) {
            Object object1 = aobject[j];

            object = this.max(object, object1);
        }

        return object;
    }

    public <E extends T> List<E> leastOf(Iterable<E> iterable, int i) {
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;

            if ((long) collection.size() <= 2L * (long) i) {
                Object[] aobject = (Object[]) collection.toArray();

                Arrays.sort(aobject, this);
                if (aobject.length > i) {
                    aobject = ObjectArrays.arraysCopyOf(aobject, i);
                }

                return Collections.unmodifiableList(Arrays.asList(aobject));
            }
        }

        return this.leastOf(iterable.iterator(), i);
    }

    public <E extends T> List<E> leastOf(Iterator<E> iterator, int i) {
        Preconditions.checkNotNull(iterator);
        CollectPreconditions.checkNonnegative(i, "k");
        if (i != 0 && iterator.hasNext()) {
            if (i >= 1073741823) {
                ArrayList arraylist = Lists.newArrayList(iterator);

                Collections.sort(arraylist, this);
                if (arraylist.size() > i) {
                    arraylist.subList(i, arraylist.size()).clear();
                }

                arraylist.trimToSize();
                return Collections.unmodifiableList(arraylist);
            } else {
                int j = i * 2;
                Object[] aobject = (Object[]) (new Object[j]);
                Object object = iterator.next();

                aobject[0] = object;

                Object object1;
                int k;

                for (k = 1; k < i && iterator.hasNext(); object = this.max(object, object1)) {
                    object1 = iterator.next();
                    aobject[k++] = object1;
                }

                label51:
                while (iterator.hasNext()) {
                    object1 = iterator.next();
                    if (this.compare(object1, object) < 0) {
                        aobject[k++] = object1;
                        if (k == j) {
                            int l = 0;
                            int i1 = j - 1;
                            int j1 = 0;

                            while (true) {
                                int k1;

                                if (l < i1) {
                                    k1 = l + i1 + 1 >>> 1;
                                    int l1 = this.partition(aobject, l, i1, k1);

                                    if (l1 > i) {
                                        i1 = l1 - 1;
                                        continue;
                                    }

                                    if (l1 < i) {
                                        l = Math.max(l1, l + 1);
                                        j1 = l1;
                                        continue;
                                    }
                                }

                                k = i;
                                object = aobject[j1];
                                k1 = j1 + 1;

                                while (true) {
                                    if (k1 >= k) {
                                        continue label51;
                                    }

                                    object = this.max(object, aobject[k1]);
                                    ++k1;
                                }
                            }
                        }
                    }
                }

                Arrays.sort(aobject, 0, k, this);
                k = Math.min(k, i);
                return Collections.unmodifiableList(Arrays.asList(ObjectArrays.arraysCopyOf(aobject, k)));
            }
        } else {
            return ImmutableList.of();
        }
    }

    private <E extends T> int partition(E[] ae, int i, int j, int k) {
        Object object = ae[k];

        ae[k] = ae[j];
        ae[j] = object;
        int l = i;

        for (int i1 = i; i1 < j; ++i1) {
            if (this.compare(ae[i1], object) < 0) {
                ObjectArrays.swap(ae, l, i1);
                ++l;
            }
        }

        ObjectArrays.swap(ae, j, l);
        return l;
    }

    public <E extends T> List<E> greatestOf(Iterable<E> iterable, int i) {
        return this.reverse().leastOf(iterable, i);
    }

    public <E extends T> List<E> greatestOf(Iterator<E> iterator, int i) {
        return this.reverse().leastOf(iterator, i);
    }

    public <E extends T> List<E> sortedCopy(Iterable<E> iterable) {
        Object[] aobject = (Object[]) Iterables.toArray(iterable);

        Arrays.sort(aobject, this);
        return Lists.newArrayList((Iterable) Arrays.asList(aobject));
    }

    public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> iterable) {
        Object[] aobject = (Object[]) Iterables.toArray(iterable);
        Object[] aobject1 = aobject;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object object = aobject1[j];

            Preconditions.checkNotNull(object);
        }

        Arrays.sort(aobject, this);
        return ImmutableList.asImmutableList(aobject);
    }

    public boolean isOrdered(Iterable<? extends T> iterable) {
        Iterator iterator = iterable.iterator();
        Object object;

        if (iterator.hasNext()) {
            for (Object object1 = iterator.next(); iterator.hasNext(); object1 = object) {
                object = iterator.next();
                if (this.compare(object1, object) > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isStrictlyOrdered(Iterable<? extends T> iterable) {
        Iterator iterator = iterable.iterator();
        Object object;

        if (iterator.hasNext()) {
            for (Object object1 = iterator.next(); iterator.hasNext(); object1 = object) {
                object = iterator.next();
                if (this.compare(object1, object) >= 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public int binarySearch(List<? extends T> list, @Nullable T t0) {
        return Collections.binarySearch(list, t0, this);
    }

    @VisibleForTesting
    static class IncomparableValueException extends ClassCastException {

        final Object value;
        private static final long serialVersionUID = 0L;

        IncomparableValueException(Object object) {
            super("Cannot compare value: " + object);
            this.value = object;
        }
    }

    @VisibleForTesting
    static class ArbitraryOrdering extends Ordering<Object> {

        private Map<Object, Integer> uids = Platform.tryWeakKeys(new MapMaker()).makeComputingMap(new Function<Object, Integer>() {
            final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Integer apply(Object object) {
                return this.counter.getAndIncrement();
            }
        });

        ArbitraryOrdering() {}

        @Override
        public int compare(Object object, Object object1) {
            if (object == object1) {
                return 0;
            } else if (object == null) {
                return -1;
            } else if (object1 == null) {
                return 1;
            } else {
                int i = this.identityHashCode(object);
                int j = this.identityHashCode(object1);

                if (i != j) {
                    return i < j ? -1 : 1;
                } else {
                    int k = ((Integer) this.uids.get(object)).compareTo((Integer) this.uids.get(object1));

                    if (k == 0) {
                        throw new AssertionError();
                    } else {
                        return k;
                    }
                }
            }
        }

        public String toString() {
            return "Ordering.arbitrary()";
        }

        int identityHashCode(Object object) {
            return System.identityHashCode(object);
        }
    }

    private static class ArbitraryOrderingHolder {

        static final Ordering<Object> ARBITRARY_ORDERING = new Ordering.ArbitraryOrdering();

        private ArbitraryOrderingHolder() {}
    }
}
