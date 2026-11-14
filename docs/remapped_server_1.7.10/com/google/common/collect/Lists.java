package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Lists {

    private Lists() {}

    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList();
    }

    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList(E... ae) {
        Preconditions.checkNotNull(ae);
        int i = computeArrayListCapacity(ae.length);
        ArrayList arraylist = new ArrayList(i);

        Collections.addAll(arraylist, ae);
        return arraylist;
    }

    @VisibleForTesting
    static int computeArrayListCapacity(int i) {
        CollectPreconditions.checkNonnegative(i, "arraySize");
        return Ints.saturatedCast(5L + (long) i + (long) (i / 10));
    }

    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> iterable) {
        Preconditions.checkNotNull(iterable);
        return iterable instanceof Collection ? new ArrayList(Collections2.cast(iterable)) : newArrayList(iterable.iterator());
    }

    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> iterator) {
        ArrayList arraylist = newArrayList();

        Iterators.addAll(arraylist, iterator);
        return arraylist;
    }

    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayListWithCapacity(int i) {
        CollectPreconditions.checkNonnegative(i, "initialArraySize");
        return new ArrayList(i);
    }

    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayListWithExpectedSize(int i) {
        return new ArrayList(computeArrayListCapacity(i));
    }

    @GwtCompatible(serializable = true)
    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList();
    }

    @GwtCompatible(serializable = true)
    public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> iterable) {
        LinkedList linkedlist = newLinkedList();

        Iterables.addAll(linkedlist, iterable);
        return linkedlist;
    }

    @GwtIncompatible("CopyOnWriteArrayList")
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList();
    }

    @GwtIncompatible("CopyOnWriteArrayList")
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> iterable) {
        Object object = iterable instanceof Collection ? Collections2.cast(iterable) : newArrayList(iterable);

        return new CopyOnWriteArrayList((Collection) object);
    }

    public static <E> List<E> asList(@Nullable E e0, E[] ae) {
        return new Lists.OnePlusArrayList<>(e0, ae);
    }

    public static <E> List<E> asList(@Nullable E e0, @Nullable E e1, E[] ae) {
        return new Lists.TwoPlusArrayList<>(e0, e1, ae);
    }

    static <B> List<List<B>> cartesianProduct(List<? extends List<? extends B>> list) {
        return CartesianList.create(list);
    }

    static <B> List<List<B>> cartesianProduct(List<? extends B>... alist) {
        return cartesianProduct(Arrays.asList(alist));
    }

    public static <F, T> List<T> transform(List<F> list, Function<? super F, ? extends T> function) {
        return (List) (list instanceof RandomAccess ? new Lists.TransformingRandomAccessList<>(list, function) : new Lists.TransformingSequentialList<>(list, function));
    }

    public static <T> List<List<T>> partition(List<T> list, int i) {
        Preconditions.checkNotNull(list);
        Preconditions.checkArgument(i > 0);
        return (List) (list instanceof RandomAccess ? new Lists.RandomAccessPartition<>(list, i) : new Lists.Partition<>(list, i));
    }

    @Beta
    public static ImmutableList<Character> charactersOf(String s) {
        return new Lists.StringAsImmutableList((String) Preconditions.checkNotNull(s));
    }

    @Beta
    public static List<Character> charactersOf(CharSequence charsequence) {
        return new Lists.CharSequenceAsList((CharSequence) Preconditions.checkNotNull(charsequence));
    }

    public static <T> List<T> reverse(List<T> list) {
        return (List) (list instanceof ImmutableList ? ((ImmutableList) list).reverse() : (list instanceof Lists.ReverseList ? ((Lists.ReverseList) list).getForwardList() : (list instanceof RandomAccess ? new Lists.RandomAccessReverseList<>(list) : new Lists.ReverseList<>(list))));
    }

    static int hashCodeImpl(List<?> list) {
        int i = 1;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i = ~(~i)) {
            Object object = iterator.next();

            i = 31 * i + (object == null ? 0 : object.hashCode());
        }

        return i;
    }

    static boolean equalsImpl(List<?> list, @Nullable Object object) {
        if (object == Preconditions.checkNotNull(list)) {
            return true;
        } else if (!(object instanceof List)) {
            return false;
        } else {
            List list1 = (List) object;

            return list.size() == list1.size() && Iterators.elementsEqual(list.iterator(), list1.iterator());
        }
    }

    static <E> boolean addAllImpl(List<E> list, int i, Iterable<? extends E> iterable) {
        boolean flag = false;
        ListIterator listiterator = list.listIterator(i);

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); flag = true) {
            Object object = iterator.next();

            listiterator.add(object);
        }

        return flag;
    }

    static int indexOfImpl(List<?> list, @Nullable Object object) {
        ListIterator listiterator = list.listIterator();

        do {
            if (!listiterator.hasNext()) {
                return -1;
            }
        } while (!Objects.equal(object, listiterator.next()));

        return listiterator.previousIndex();
    }

    static int lastIndexOfImpl(List<?> list, @Nullable Object object) {
        ListIterator listiterator = list.listIterator(list.size());

        do {
            if (!listiterator.hasPrevious()) {
                return -1;
            }
        } while (!Objects.equal(object, listiterator.previous()));

        return listiterator.nextIndex();
    }

    static <E> ListIterator<E> listIteratorImpl(List<E> list, int i) {
        return (new Lists.AbstractListWrapper<>(list)).listIterator(i);
    }

    static <E> List<E> subListImpl(List<E> list, int i, int j) {
        Object object;

        if (list instanceof RandomAccess) {
            object = new Lists.RandomAccessListWrapper<E>(list) {
                private static final long serialVersionUID = 0L;

                public ListIterator<E> listIterator(int k) {
                    return this.backingList.listIterator(k);
                }
            };
        } else {
            object = new Lists.AbstractListWrapper<E>(list) {
                private static final long serialVersionUID = 0L;

                public ListIterator<E> listIterator(int k) {
                    return this.backingList.listIterator(k);
                }
            };
        }

        return ((List) object).subList(i, j);
    }

    static <T> List<T> cast(Iterable<T> iterable) {
        return (List) iterable;
    }

    private static class RandomAccessListWrapper<E> extends Lists.AbstractListWrapper<E> implements RandomAccess {

        RandomAccessListWrapper(List<E> list) {
            super(list);
        }
    }

    private static class AbstractListWrapper<E> extends AbstractList<E> {

        final List<E> backingList;

        AbstractListWrapper(List<E> list) {
            this.backingList = (List) Preconditions.checkNotNull(list);
        }

        public void add(int i, E e0) {
            this.backingList.add(i, e0);
        }

        public boolean addAll(int i, Collection<? extends E> collection) {
            return this.backingList.addAll(i, collection);
        }

        public E get(int i) {
            return this.backingList.get(i);
        }

        public E remove(int i) {
            return this.backingList.remove(i);
        }

        public E set(int i, E e0) {
            return this.backingList.set(i, e0);
        }

        public boolean contains(Object object) {
            return this.backingList.contains(object);
        }

        public int size() {
            return this.backingList.size();
        }
    }

    private static class RandomAccessReverseList<T> extends Lists.ReverseList<T> implements RandomAccess {

        RandomAccessReverseList(List<T> list) {
            super(list);
        }
    }

    private static class ReverseList<T> extends AbstractList<T> {

        private final List<T> forwardList;

        ReverseList(List<T> list) {
            this.forwardList = (List) Preconditions.checkNotNull(list);
        }

        List<T> getForwardList() {
            return this.forwardList;
        }

        private int reverseIndex(int i) {
            int j = this.size();

            Preconditions.checkElementIndex(i, j);
            return j - 1 - i;
        }

        private int reversePosition(int i) {
            int j = this.size();

            Preconditions.checkPositionIndex(i, j);
            return j - i;
        }

        public void add(int i, @Nullable T t0) {
            this.forwardList.add(this.reversePosition(i), t0);
        }

        public void clear() {
            this.forwardList.clear();
        }

        public T remove(int i) {
            return this.forwardList.remove(this.reverseIndex(i));
        }

        protected void removeRange(int i, int j) {
            this.subList(i, j).clear();
        }

        public T set(int i, @Nullable T t0) {
            return this.forwardList.set(this.reverseIndex(i), t0);
        }

        public T get(int i) {
            return this.forwardList.get(this.reverseIndex(i));
        }

        public int size() {
            return this.forwardList.size();
        }

        public List<T> subList(int i, int j) {
            Preconditions.checkPositionIndexes(i, j, this.size());
            return Lists.reverse(this.forwardList.subList(this.reversePosition(j), this.reversePosition(i)));
        }

        public Iterator<T> iterator() {
            return this.listIterator();
        }

        public ListIterator<T> listIterator(int i) {
            int j = this.reversePosition(i);
            final ListIterator listiterator = this.forwardList.listIterator(j);

            return new ListIterator<T>() {
                boolean canRemoveOrSet;

                public void add(T t0) {
                    listiterator.add(t0);
                    listiterator.previous();
                    this.canRemoveOrSet = false;
                }

                public boolean hasNext() {
                    return listiterator.hasPrevious();
                }

                public boolean hasPrevious() {
                    return listiterator.hasNext();
                }

                public T next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    } else {
                        this.canRemoveOrSet = true;
                        return listiterator.previous();
                    }
                }

                public int nextIndex() {
                    return ReverseList.this.reversePosition(listiterator.nextIndex());
                }

                public T previous() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    } else {
                        this.canRemoveOrSet = true;
                        return listiterator.next();
                    }
                }

                public int previousIndex() {
                    return this.nextIndex() - 1;
                }

                public void remove() {
                    CollectPreconditions.checkRemove(this.canRemoveOrSet);
                    listiterator.remove();
                    this.canRemoveOrSet = false;
                }

                public void set(T t0) {
                    Preconditions.checkState(this.canRemoveOrSet);
                    listiterator.set(t0);
                }
            };
        }
    }

    private static final class CharSequenceAsList extends AbstractList<Character> {

        private final CharSequence sequence;

        CharSequenceAsList(CharSequence charsequence) {
            this.sequence = charsequence;
        }

        public Character get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.sequence.charAt(i);
        }

        public int size() {
            return this.sequence.length();
        }
    }

    private static final class StringAsImmutableList extends ImmutableList<Character> {

        private final String string;

        StringAsImmutableList(String s) {
            this.string = s;
        }

        @Override
        public int indexOf(@Nullable Object object) {
            return object instanceof Character ? this.string.indexOf((Character) object) : -1;
        }

        @Override
        public int lastIndexOf(@Nullable Object object) {
            return object instanceof Character ? this.string.lastIndexOf((Character) object) : -1;
        }

        @Override
        public ImmutableList<Character> subList(int i, int j) {
            Preconditions.checkPositionIndexes(i, j, this.size());
            return Lists.charactersOf(this.string.substring(i, j));
        }

        @Override
        boolean isPartialView() {
            return false;
        }

        public Character get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.string.charAt(i);
        }

        public int size() {
            return this.string.length();
        }
    }

    private static class RandomAccessPartition<T> extends Lists.Partition<T> implements RandomAccess {

        RandomAccessPartition(List<T> list, int i) {
            super(list, i);
        }
    }

    private static class Partition<T> extends AbstractList<List<T>> {

        final List<T> list;
        final int size;

        Partition(List<T> list, int i) {
            this.list = list;
            this.size = i;
        }

        public List<T> get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            int j = i * this.size;
            int k = Math.min(j + this.size, this.list.size());

            return this.list.subList(j, k);
        }

        public int size() {
            return IntMath.divide(this.list.size(), this.size, RoundingMode.CEILING);
        }

        public boolean isEmpty() {
            return this.list.isEmpty();
        }
    }

    private static class TransformingRandomAccessList<F, T> extends AbstractList<T> implements RandomAccess, Serializable {

        final List<F> fromList;
        final Function<? super F, ? extends T> function;
        private static final long serialVersionUID = 0L;

        TransformingRandomAccessList(List<F> list, Function<? super F, ? extends T> function) {
            this.fromList = (List) Preconditions.checkNotNull(list);
            this.function = (Function) Preconditions.checkNotNull(function);
        }

        public void clear() {
            this.fromList.clear();
        }

        public T get(int i) {
            return this.function.apply(this.fromList.get(i));
        }

        public Iterator<T> iterator() {
            return this.listIterator();
        }

        public ListIterator<T> listIterator(int i) {
            return new TransformedListIterator<F, T>(this.fromList.listIterator(i)) {
                @Override
                T transform(F f0) {
                    return TransformingRandomAccessList.this.function.apply(f0);
                }
            };
        }

        public boolean isEmpty() {
            return this.fromList.isEmpty();
        }

        public T remove(int i) {
            return this.function.apply(this.fromList.remove(i));
        }

        public int size() {
            return this.fromList.size();
        }
    }

    private static class TransformingSequentialList<F, T> extends AbstractSequentialList<T> implements Serializable {

        final List<F> fromList;
        final Function<? super F, ? extends T> function;
        private static final long serialVersionUID = 0L;

        TransformingSequentialList(List<F> list, Function<? super F, ? extends T> function) {
            this.fromList = (List) Preconditions.checkNotNull(list);
            this.function = (Function) Preconditions.checkNotNull(function);
        }

        public void clear() {
            this.fromList.clear();
        }

        public int size() {
            return this.fromList.size();
        }

        public ListIterator<T> listIterator(int i) {
            return new TransformedListIterator<F, T>(this.fromList.listIterator(i)) {
                @Override
                T transform(F f0) {
                    return TransformingSequentialList.this.function.apply(f0);
                }
            };
        }
    }

    private static class TwoPlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {

        final E first;
        final E second;
        final E[] rest;
        private static final long serialVersionUID = 0L;

        TwoPlusArrayList(@Nullable E e0, @Nullable E e1, E[] ae) {
            this.first = e0;
            this.second = e1;
            this.rest = (Object[]) Preconditions.checkNotNull(ae);
        }

        public int size() {
            return this.rest.length + 2;
        }

        public E get(int i) {
            switch (i) {
                case 0:
                    return this.first;
                case 1:
                    return this.second;
                default:
                    Preconditions.checkElementIndex(i, this.size());
                    return this.rest[i - 2];
            }
        }
    }

    private static class OnePlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {

        final E first;
        final E[] rest;
        private static final long serialVersionUID = 0L;

        OnePlusArrayList(@Nullable E e0, E[] ae) {
            this.first = e0;
            this.rest = (Object[]) Preconditions.checkNotNull(ae);
        }

        public int size() {
            return this.rest.length + 1;
        }

        public E get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return i == 0 ? this.first : this.rest[i - 1];
        }
    }
}
