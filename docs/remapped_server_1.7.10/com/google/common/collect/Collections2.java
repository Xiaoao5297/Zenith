package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
public final class Collections2 {

    static final Joiner STANDARD_JOINER = Joiner.on(", ").useForNull("null");

    private Collections2() {}

    public static <E> Collection<E> filter(Collection<E> collection, Predicate<? super E> predicate) {
        return collection instanceof Collections2.FilteredCollection ? ((Collections2.FilteredCollection) collection).createCombined(predicate) : new Collections2.FilteredCollection<>((Collection) Preconditions.checkNotNull(collection), (Predicate) Preconditions.checkNotNull(predicate));
    }

    static boolean safeContains(Collection<?> collection, @Nullable Object object) {
        Preconditions.checkNotNull(collection);

        try {
            return collection.contains(object);
        } catch (ClassCastException classcastexception) {
            return false;
        } catch (NullPointerException nullpointerexception) {
            return false;
        }
    }

    static boolean safeRemove(Collection<?> collection, @Nullable Object object) {
        Preconditions.checkNotNull(collection);

        try {
            return collection.remove(object);
        } catch (ClassCastException classcastexception) {
            return false;
        } catch (NullPointerException nullpointerexception) {
            return false;
        }
    }

    public static <F, T> Collection<T> transform(Collection<F> collection, Function<? super F, T> function) {
        return new Collections2.TransformedCollection<>(collection, function);
    }

    static boolean containsAllImpl(Collection<?> collection, Collection<?> collection1) {
        return Iterables.all(collection1, Predicates.in(collection));
    }

    static String toStringImpl(final Collection<?> collection) {
        StringBuilder stringbuilder = newStringBuilderForCollection(collection.size()).append('[');

        Collections2.STANDARD_JOINER.appendTo(stringbuilder, Iterables.transform(collection, new Function<Object, Object>() {
            @Override
            public Object apply(Object object) {
                return object == collection ? "(this Collection)" : object;
            }
        }));
        return stringbuilder.append(']').toString();
    }

    static StringBuilder newStringBuilderForCollection(int i) {
        CollectPreconditions.checkNonnegative(i, "size");
        return new StringBuilder((int) Math.min((long) i * 8L, 1073741824L));
    }

    static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection) iterable;
    }

    @Beta
    public static <E extends Comparable<? super E>> Collection<List<E>> orderedPermutations(Iterable<E> iterable) {
        return orderedPermutations(iterable, Ordering.natural());
    }

    @Beta
    public static <E> Collection<List<E>> orderedPermutations(Iterable<E> iterable, Comparator<? super E> comparator) {
        return new Collections2.OrderedPermutationCollection<>(iterable, comparator);
    }

    @Beta
    public static <E> Collection<List<E>> permutations(Collection<E> collection) {
        return new Collections2.PermutationCollection<>(ImmutableList.copyOf(collection));
    }

    private static boolean isPermutation(List<?> list, List<?> list1) {
        if (list.size() != list1.size()) {
            return false;
        } else {
            HashMultiset hashmultiset = HashMultiset.create(list);
            HashMultiset hashmultiset1 = HashMultiset.create(list1);

            return hashmultiset.equals(hashmultiset1);
        }
    }

    private static boolean isPositiveInt(long i) {
        return i >= 0L && i <= 2147483647L;
    }

    private static class PermutationIterator<E> extends AbstractIterator<List<E>> {

        final List<E> list;
        final int[] c;
        final int[] o;
        int j;

        PermutationIterator(List<E> list) {
            this.list = new ArrayList(list);
            int i = list.size();

            this.c = new int[i];
            this.o = new int[i];
            Arrays.fill(this.c, 0);
            Arrays.fill(this.o, 1);
            this.j = Integer.MAX_VALUE;
        }

        @Override
        protected List<E> computeNext() {
            if (this.j <= 0) {
                return (List) this.endOfData();
            } else {
                ImmutableList immutablelist = ImmutableList.copyOf((Collection) this.list);

                this.calculateNextPermutation();
                return immutablelist;
            }
        }

        void calculateNextPermutation() {
            this.j = this.list.size() - 1;
            int i = 0;

            if (this.j != -1) {
                while (true) {
                    while (true) {
                        int j = this.c[this.j] + this.o[this.j];

                        if (j >= 0) {
                            if (j != this.j + 1) {
                                Collections.swap(this.list, this.j - this.c[this.j] + i, this.j - j + i);
                                this.c[this.j] = j;
                                return;
                            }

                            if (this.j == 0) {
                                return;
                            }

                            ++i;
                            this.switchDirection();
                        } else {
                            this.switchDirection();
                        }
                    }
                }
            }
        }

        void switchDirection() {
            this.o[this.j] = -this.o[this.j];
            --this.j;
        }
    }

    private static final class PermutationCollection<E> extends AbstractCollection<List<E>> {

        final ImmutableList<E> inputList;

        PermutationCollection(ImmutableList<E> immutablelist) {
            this.inputList = immutablelist;
        }

        public int size() {
            return IntMath.factorial(this.inputList.size());
        }

        public boolean isEmpty() {
            return false;
        }

        public Iterator<List<E>> iterator() {
            return new Collections2.PermutationIterator<>(this.inputList);
        }

        public boolean contains(@Nullable Object object) {
            if (object instanceof List) {
                List list = (List) object;

                return Collections2.isPermutation(this.inputList, list);
            } else {
                return false;
            }
        }

        public String toString() {
            return "permutations(" + this.inputList + ")";
        }
    }

    private static final class OrderedPermutationIterator<E> extends AbstractIterator<List<E>> {

        List<E> nextPermutation;
        final Comparator<? super E> comparator;

        OrderedPermutationIterator(List<E> list, Comparator<? super E> comparator) {
            this.nextPermutation = Lists.newArrayList((Iterable) list);
            this.comparator = comparator;
        }

        @Override
        protected List<E> computeNext() {
            if (this.nextPermutation == null) {
                return (List) this.endOfData();
            } else {
                ImmutableList immutablelist = ImmutableList.copyOf((Collection) this.nextPermutation);

                this.calculateNextPermutation();
                return immutablelist;
            }
        }

        void calculateNextPermutation() {
            int i = this.findNextJ();

            if (i == -1) {
                this.nextPermutation = null;
            } else {
                int j = this.findNextL(i);

                Collections.swap(this.nextPermutation, i, j);
                int k = this.nextPermutation.size();

                Collections.reverse(this.nextPermutation.subList(i + 1, k));
            }
        }

        int findNextJ() {
            for (int i = this.nextPermutation.size() - 2; i >= 0; --i) {
                if (this.comparator.compare(this.nextPermutation.get(i), this.nextPermutation.get(i + 1)) < 0) {
                    return i;
                }
            }

            return -1;
        }

        int findNextL(int i) {
            Object object = this.nextPermutation.get(i);

            for (int j = this.nextPermutation.size() - 1; j > i; --j) {
                if (this.comparator.compare(object, this.nextPermutation.get(j)) < 0) {
                    return j;
                }
            }

            throw new AssertionError("this statement should be unreachable");
        }
    }

    private static final class OrderedPermutationCollection<E> extends AbstractCollection<List<E>> {

        final ImmutableList<E> inputList;
        final Comparator<? super E> comparator;
        final int size;

        OrderedPermutationCollection(Iterable<E> iterable, Comparator<? super E> comparator) {
            this.inputList = Ordering.from(comparator).immutableSortedCopy(iterable);
            this.comparator = comparator;
            this.size = calculateSize(this.inputList, comparator);
        }

        private static <E> int calculateSize(List<E> list, Comparator<? super E> comparator) {
            long i = 1L;
            int j = 1;

            int k;

            for (k = 1; j < list.size(); ++k) {
                int l = comparator.compare(list.get(j - 1), list.get(j));

                if (l < 0) {
                    i *= LongMath.binomial(j, k);
                    k = 0;
                    if (!Collections2.isPositiveInt(i)) {
                        return Integer.MAX_VALUE;
                    }
                }

                ++j;
            }

            i *= LongMath.binomial(j, k);
            if (!Collections2.isPositiveInt(i)) {
                return Integer.MAX_VALUE;
            } else {
                return (int) i;
            }
        }

        public int size() {
            return this.size;
        }

        public boolean isEmpty() {
            return false;
        }

        public Iterator<List<E>> iterator() {
            return new Collections2.OrderedPermutationIterator<>(this.inputList, this.comparator);
        }

        public boolean contains(@Nullable Object object) {
            if (object instanceof List) {
                List list = (List) object;

                return Collections2.isPermutation(this.inputList, list);
            } else {
                return false;
            }
        }

        public String toString() {
            return "orderedPermutationCollection(" + this.inputList + ")";
        }
    }

    static class TransformedCollection<F, T> extends AbstractCollection<T> {

        final Collection<F> fromCollection;
        final Function<? super F, ? extends T> function;

        TransformedCollection(Collection<F> collection, Function<? super F, ? extends T> function) {
            this.fromCollection = (Collection) Preconditions.checkNotNull(collection);
            this.function = (Function) Preconditions.checkNotNull(function);
        }

        public void clear() {
            this.fromCollection.clear();
        }

        public boolean isEmpty() {
            return this.fromCollection.isEmpty();
        }

        public Iterator<T> iterator() {
            return Iterators.transform(this.fromCollection.iterator(), this.function);
        }

        public int size() {
            return this.fromCollection.size();
        }
    }

    static class FilteredCollection<E> extends AbstractCollection<E> {

        final Collection<E> unfiltered;
        final Predicate<? super E> predicate;

        FilteredCollection(Collection<E> collection, Predicate<? super E> predicate) {
            this.unfiltered = collection;
            this.predicate = predicate;
        }

        Collections2.FilteredCollection<E> createCombined(Predicate<? super E> predicate) {
            return new Collections2.FilteredCollection<>(this.unfiltered, Predicates.and(this.predicate, predicate));
        }

        public boolean add(E e0) {
            Preconditions.checkArgument(this.predicate.apply(e0));
            return this.unfiltered.add(e0);
        }

        public boolean addAll(Collection<? extends E> collection) {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                Preconditions.checkArgument(this.predicate.apply(object));
            }

            return this.unfiltered.addAll(collection);
        }

        public void clear() {
            Iterables.removeIf(this.unfiltered, this.predicate);
        }

        public boolean contains(@Nullable Object object) {
            return Collections2.safeContains(this.unfiltered, object) ? this.predicate.apply(object) : false;
        }

        public boolean containsAll(Collection<?> collection) {
            return Collections2.containsAllImpl(this, collection);
        }

        public boolean isEmpty() {
            return !Iterables.any(this.unfiltered, this.predicate);
        }

        public Iterator<E> iterator() {
            return Iterators.filter(this.unfiltered.iterator(), this.predicate);
        }

        public boolean remove(Object object) {
            return this.contains(object) && this.unfiltered.remove(object);
        }

        public boolean removeAll(Collection<?> collection) {
            return Iterables.removeIf(this.unfiltered, Predicates.and(this.predicate, Predicates.in(collection)));
        }

        public boolean retainAll(Collection<?> collection) {
            return Iterables.removeIf(this.unfiltered, Predicates.and(this.predicate, Predicates.not(Predicates.in(collection))));
        }

        public int size() {
            return Iterators.size(this.iterator());
        }

        public Object[] toArray() {
            return Lists.newArrayList(this.iterator()).toArray();
        }

        public <T> T[] toArray(T[] at) {
            return Lists.newArrayList(this.iterator()).toArray(at);
        }
    }
}
