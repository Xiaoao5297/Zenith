package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Sets {

    private Sets() {}

    @GwtCompatible(serializable = true)
    public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(E e0, E... ae) {
        return ImmutableEnumSet.asImmutable(EnumSet.of(e0, ae));
    }

    @GwtCompatible(serializable = true)
    public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(Iterable<E> iterable) {
        if (iterable instanceof ImmutableEnumSet) {
            return (ImmutableEnumSet) iterable;
        } else if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;

            return collection.isEmpty() ? ImmutableSet.of() : ImmutableEnumSet.asImmutable(EnumSet.copyOf(collection));
        } else {
            Iterator iterator = iterable.iterator();

            if (iterator.hasNext()) {
                EnumSet enumset = EnumSet.of((Enum) iterator.next());

                Iterators.addAll(enumset, iterator);
                return ImmutableEnumSet.asImmutable(enumset);
            } else {
                return ImmutableSet.of();
            }
        }
    }

    public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> oclass) {
        EnumSet enumset = EnumSet.noneOf(oclass);

        Iterables.addAll(enumset, iterable);
        return enumset;
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet();
    }

    public static <E> HashSet<E> newHashSet(E... ae) {
        HashSet hashset = newHashSetWithExpectedSize(ae.length);

        Collections.addAll(hashset, ae);
        return hashset;
    }

    public static <E> HashSet<E> newHashSetWithExpectedSize(int i) {
        return new HashSet(Maps.capacity(i));
    }

    public static <E> HashSet<E> newHashSet(Iterable<? extends E> iterable) {
        return iterable instanceof Collection ? new HashSet(Collections2.cast(iterable)) : newHashSet(iterable.iterator());
    }

    public static <E> HashSet<E> newHashSet(Iterator<? extends E> iterator) {
        HashSet hashset = newHashSet();

        Iterators.addAll(hashset, iterator);
        return hashset;
    }

    public static <E> Set<E> newConcurrentHashSet() {
        return newSetFromMap(new ConcurrentHashMap());
    }

    public static <E> Set<E> newConcurrentHashSet(Iterable<? extends E> iterable) {
        Set set = newConcurrentHashSet();

        Iterables.addAll(set, iterable);
        return set;
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet();
    }

    public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int i) {
        return new LinkedHashSet(Maps.capacity(i));
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new LinkedHashSet(Collections2.cast(iterable));
        } else {
            LinkedHashSet linkedhashset = newLinkedHashSet();

            Iterables.addAll(linkedhashset, iterable);
            return linkedhashset;
        }
    }

    public static <E extends Comparable> TreeSet<E> newTreeSet() {
        return new TreeSet();
    }

    public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> iterable) {
        TreeSet treeset = newTreeSet();

        Iterables.addAll(treeset, iterable);
        return treeset;
    }

    public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
        return new TreeSet((Comparator) Preconditions.checkNotNull(comparator));
    }

    public static <E> Set<E> newIdentityHashSet() {
        return newSetFromMap(Maps.newIdentityHashMap());
    }

    @GwtIncompatible("CopyOnWriteArraySet")
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet();
    }

    @GwtIncompatible("CopyOnWriteArraySet")
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> iterable) {
        Object object = iterable instanceof Collection ? Collections2.cast(iterable) : Lists.newArrayList(iterable);

        return new CopyOnWriteArraySet((Collection) object);
    }

    public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection) {
        if (collection instanceof EnumSet) {
            return EnumSet.complementOf((EnumSet) collection);
        } else {
            Preconditions.checkArgument(!collection.isEmpty(), "collection is empty; use the other version of this method");
            Class oclass = ((Enum) collection.iterator().next()).getDeclaringClass();

            return makeComplementByHand(collection, oclass);
        }
    }

    public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection, Class<E> oclass) {
        Preconditions.checkNotNull(collection);
        return collection instanceof EnumSet ? EnumSet.complementOf((EnumSet) collection) : makeComplementByHand(collection, oclass);
    }

    private static <E extends Enum<E>> EnumSet<E> makeComplementByHand(Collection<E> collection, Class<E> oclass) {
        EnumSet enumset = EnumSet.allOf(oclass);

        enumset.removeAll(collection);
        return enumset;
    }

    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Platform.newSetFromMap(map);
    }

    public static <E> Sets.SetView<E> union(final Set<? extends E> set, final Set<? extends E> set1) {
        Preconditions.checkNotNull(set, "set1");
        Preconditions.checkNotNull(set1, "set2");
        final Sets.SetView sets_setview = difference(set1, set);

        return new Sets.SetView<E>() {
            public int size() {
                return set.size() + sets_setview.size();
            }

            public boolean isEmpty() {
                return set.isEmpty() && set1.isEmpty();
            }

            public Iterator<E> iterator() {
                return Iterators.unmodifiableIterator(Iterators.concat(set.iterator(), sets_setview.iterator()));
            }

            public boolean contains(Object object) {
                return set.contains(object) || set1.contains(object);
            }

            @Override
            public <S extends Set<E>> S copyInto(S s0) {
                s0.addAll(set);
                s0.addAll(set1);
                return s0;
            }

            @Override
            public ImmutableSet<E> immutableCopy() {
                return (new ImmutableSet.Builder<>()).addAll((Iterable) set).addAll((Iterable) set1).build();
            }
        };
    }

    public static <E> Sets.SetView<E> intersection(final Set<E> set, final Set<?> set1) {
        Preconditions.checkNotNull(set, "set1");
        Preconditions.checkNotNull(set1, "set2");
        final Predicate predicate = Predicates.in(set1);

        return new Sets.SetView<E>() {
            public Iterator<E> iterator() {
                return Iterators.filter(set.iterator(), predicate);
            }

            public int size() {
                return Iterators.size(this.iterator());
            }

            public boolean isEmpty() {
                return !this.iterator().hasNext();
            }

            public boolean contains(Object object) {
                return set.contains(object) && set1.contains(object);
            }

            public boolean containsAll(Collection<?> collection) {
                return set.containsAll(collection) && set1.containsAll(collection);
            }
        };
    }

    public static <E> Sets.SetView<E> difference(final Set<E> set, final Set<?> set1) {
        Preconditions.checkNotNull(set, "set1");
        Preconditions.checkNotNull(set1, "set2");
        final Predicate predicate = Predicates.not(Predicates.in(set1));

        return new Sets.SetView<E>() {
            public Iterator<E> iterator() {
                return Iterators.filter(set.iterator(), predicate);
            }

            public int size() {
                return Iterators.size(this.iterator());
            }

            public boolean isEmpty() {
                return set1.containsAll(set);
            }

            public boolean contains(Object object) {
                return set.contains(object) && !set1.contains(object);
            }
        };
    }

    public static <E> Sets.SetView<E> symmetricDifference(Set<? extends E> set, Set<? extends E> set1) {
        Preconditions.checkNotNull(set, "set1");
        Preconditions.checkNotNull(set1, "set2");
        return difference(union(set, set1), intersection(set, set1));
    }

    public static <E> Set<E> filter(Set<E> set, Predicate<? super E> predicate) {
        if (set instanceof SortedSet) {
            return filter((SortedSet) set, predicate);
        } else if (set instanceof Sets.FilteredSet) {
            Sets.FilteredSet sets_filteredset = (Sets.FilteredSet) set;
            Predicate predicate1 = Predicates.and(sets_filteredset.predicate, predicate);

            return new Sets.FilteredSet<>((Set) sets_filteredset.unfiltered, predicate1);
        } else {
            return new Sets.FilteredSet<>((Set) Preconditions.checkNotNull(set), (Predicate) Preconditions.checkNotNull(predicate));
        }
    }

    public static <E> SortedSet<E> filter(SortedSet<E> sortedset, Predicate<? super E> predicate) {
        return Platform.setsFilterSortedSet(sortedset, predicate);
    }

    static <E> SortedSet<E> filterSortedIgnoreNavigable(SortedSet<E> sortedset, Predicate<? super E> predicate) {
        if (sortedset instanceof Sets.FilteredSet) {
            Sets.FilteredSet sets_filteredset = (Sets.FilteredSet) sortedset;
            Predicate predicate1 = Predicates.and(sets_filteredset.predicate, predicate);

            return new Sets.FilteredSortedSet<>((SortedSet) sets_filteredset.unfiltered, predicate1);
        } else {
            return new Sets.FilteredSortedSet<>((SortedSet) Preconditions.checkNotNull(sortedset), (Predicate) Preconditions.checkNotNull(predicate));
        }
    }

    @GwtIncompatible("NavigableSet")
    public static <E> NavigableSet<E> filter(NavigableSet<E> navigableset, Predicate<? super E> predicate) {
        if (navigableset instanceof Sets.FilteredSet) {
            Sets.FilteredSet sets_filteredset = (Sets.FilteredSet) navigableset;
            Predicate predicate1 = Predicates.and(sets_filteredset.predicate, predicate);

            return new Sets.FilteredNavigableSet<>((NavigableSet) sets_filteredset.unfiltered, predicate1);
        } else {
            return new Sets.FilteredNavigableSet<>((NavigableSet) Preconditions.checkNotNull(navigableset), (Predicate) Preconditions.checkNotNull(predicate));
        }
    }

    public static <B> Set<List<B>> cartesianProduct(List<? extends Set<? extends B>> list) {
        return Sets.CartesianSet.create(list);
    }

    public static <B> Set<List<B>> cartesianProduct(Set<? extends B>... aset) {
        return cartesianProduct(Arrays.asList(aset));
    }

    @GwtCompatible(serializable = false)
    public static <E> Set<Set<E>> powerSet(Set<E> set) {
        return new Sets.PowerSet<>(set);
    }

    static int hashCodeImpl(Set<?> set) {
        int i = 0;

        for (Iterator iterator = set.iterator(); iterator.hasNext(); i = ~(~i)) {
            Object object = iterator.next();

            i += object != null ? object.hashCode() : 0;
        }

        return i;
    }

    static boolean equalsImpl(Set<?> set, @Nullable Object object) {
        if (set == object) {
            return true;
        } else if (object instanceof Set) {
            Set set1 = (Set) object;

            try {
                return set.size() == set1.size() && set.containsAll(set1);
            } catch (NullPointerException nullpointerexception) {
                return false;
            } catch (ClassCastException classcastexception) {
                return false;
            }
        } else {
            return false;
        }
    }

    @GwtIncompatible("NavigableSet")
    public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> navigableset) {
        return (NavigableSet) (!(navigableset instanceof ImmutableSortedSet) && !(navigableset instanceof Sets.UnmodifiableNavigableSet) ? new Sets.UnmodifiableNavigableSet<>(navigableset) : navigableset);
    }

    @GwtIncompatible("NavigableSet")
    public static <E> NavigableSet<E> synchronizedNavigableSet(NavigableSet<E> navigableset) {
        return Synchronized.navigableSet(navigableset);
    }

    static boolean removeAllImpl(Set<?> set, Iterator<?> iterator) {
        boolean flag;

        for (flag = false; iterator.hasNext(); flag |= set.remove(iterator.next())) {
            ;
        }

        return flag;
    }

    static boolean removeAllImpl(Set<?> set, Collection<?> collection) {
        Preconditions.checkNotNull(collection);
        if (collection instanceof Multiset) {
            collection = ((Multiset) collection).elementSet();
        }

        return collection instanceof Set && ((Collection) collection).size() > set.size() ? Iterators.removeAll(set.iterator(), (Collection) collection) : removeAllImpl(set, ((Collection) collection).iterator());
    }

    @GwtIncompatible("NavigableSet")
    static class DescendingSet<E> extends ForwardingNavigableSet<E> {

        private final NavigableSet<E> forward;

        DescendingSet(NavigableSet<E> navigableset) {
            this.forward = navigableset;
        }

        @Override
        protected NavigableSet<E> delegate() {
            return this.forward;
        }

        @Override
        public E lower(E e0) {
            return this.forward.higher(e0);
        }

        @Override
        public E floor(E e0) {
            return this.forward.ceiling(e0);
        }

        @Override
        public E ceiling(E e0) {
            return this.forward.floor(e0);
        }

        @Override
        public E higher(E e0) {
            return this.forward.lower(e0);
        }

        @Override
        public E pollFirst() {
            return this.forward.pollLast();
        }

        @Override
        public E pollLast() {
            return this.forward.pollFirst();
        }

        @Override
        public NavigableSet<E> descendingSet() {
            return this.forward;
        }

        @Override
        public Iterator<E> descendingIterator() {
            return this.forward.iterator();
        }

        @Override
        public NavigableSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
            return this.forward.subSet(e1, flag1, e0, flag).descendingSet();
        }

        @Override
        public NavigableSet<E> headSet(E e0, boolean flag) {
            return this.forward.tailSet(e0, flag).descendingSet();
        }

        @Override
        public NavigableSet<E> tailSet(E e0, boolean flag) {
            return this.forward.headSet(e0, flag).descendingSet();
        }

        @Override
        public Comparator<? super E> comparator() {
            Comparator comparator = this.forward.comparator();

            return comparator == null ? Ordering.natural().reverse() : reverse(comparator);
        }

        private static <T> Ordering<T> reverse(Comparator<T> comparator) {
            return Ordering.from(comparator).reverse();
        }

        @Override
        public E first() {
            return this.forward.last();
        }

        @Override
        public SortedSet<E> headSet(E e0) {
            return this.standardHeadSet(e0);
        }

        @Override
        public E last() {
            return this.forward.first();
        }

        @Override
        public SortedSet<E> subSet(E e0, E e1) {
            return this.standardSubSet(e0, e1);
        }

        @Override
        public SortedSet<E> tailSet(E e0) {
            return this.standardTailSet(e0);
        }

        @Override
        public Iterator<E> iterator() {
            return this.forward.descendingIterator();
        }

        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }

        @Override
        public <T> T[] toArray(T[] at) {
            return this.standardToArray(at);
        }

        @Override
        public String toString() {
            return this.standardToString();
        }
    }

    @GwtIncompatible("NavigableSet")
    static final class UnmodifiableNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E>, Serializable {

        private final NavigableSet<E> delegate;
        private transient Sets.UnmodifiableNavigableSet<E> descendingSet;
        private static final long serialVersionUID = 0L;

        UnmodifiableNavigableSet(NavigableSet<E> navigableset) {
            this.delegate = (NavigableSet) Preconditions.checkNotNull(navigableset);
        }

        @Override
        protected SortedSet<E> delegate() {
            return Collections.unmodifiableSortedSet(this.delegate);
        }

        public E lower(E e0) {
            return this.delegate.lower(e0);
        }

        public E floor(E e0) {
            return this.delegate.floor(e0);
        }

        public E ceiling(E e0) {
            return this.delegate.ceiling(e0);
        }

        public E higher(E e0) {
            return this.delegate.higher(e0);
        }

        public E pollFirst() {
            throw new UnsupportedOperationException();
        }

        public E pollLast() {
            throw new UnsupportedOperationException();
        }

        public NavigableSet<E> descendingSet() {
            Sets.UnmodifiableNavigableSet sets_unmodifiablenavigableset = this.descendingSet;

            if (sets_unmodifiablenavigableset == null) {
                sets_unmodifiablenavigableset = this.descendingSet = new Sets.UnmodifiableNavigableSet<>(this.delegate.descendingSet());
                sets_unmodifiablenavigableset.descendingSet = this;
            }

            return sets_unmodifiablenavigableset;
        }

        public Iterator<E> descendingIterator() {
            return Iterators.unmodifiableIterator(this.delegate.descendingIterator());
        }

        public NavigableSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
            return Sets.unmodifiableNavigableSet(this.delegate.subSet(e0, flag, e1, flag1));
        }

        public NavigableSet<E> headSet(E e0, boolean flag) {
            return Sets.unmodifiableNavigableSet(this.delegate.headSet(e0, flag));
        }

        public NavigableSet<E> tailSet(E e0, boolean flag) {
            return Sets.unmodifiableNavigableSet(this.delegate.tailSet(e0, flag));
        }
    }

    private static final class PowerSet<E> extends AbstractSet<Set<E>> {

        final ImmutableMap<E, Integer> inputSet;

        PowerSet(Set<E> set) {
            ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
            int i = 0;
            Iterator iterator = ((Set) Preconditions.checkNotNull(set)).iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                immutablemap_builder.put(object, i++);
            }

            this.inputSet = immutablemap_builder.build();
            Preconditions.checkArgument(this.inputSet.size() <= 30, "Too many elements to create power set: %s > 30", this.inputSet.size());
        }

        public int size() {
            return 1 << this.inputSet.size();
        }

        public boolean isEmpty() {
            return false;
        }

        public Iterator<Set<E>> iterator() {
            return new AbstractIndexedListIterator<Set<E>>(this.size()) {
                @Override
                protected Set<E> get(int i) {
                    return new Sets.SubSet<>(PowerSet.this.inputSet, i);
                }
            };
        }

        public boolean contains(@Nullable Object object) {
            if (object instanceof Set) {
                Set set = (Set) object;

                return this.inputSet.keySet().containsAll(set);
            } else {
                return false;
            }
        }

        public boolean equals(@Nullable Object object) {
            if (object instanceof Sets.PowerSet) {
                Sets.PowerSet sets_powerset = (Sets.PowerSet) object;

                return this.inputSet.equals(sets_powerset.inputSet);
            } else {
                return super.equals(object);
            }
        }

        public int hashCode() {
            return this.inputSet.keySet().hashCode() << this.inputSet.size() - 1;
        }

        public String toString() {
            return "powerSet(" + this.inputSet + ")";
        }
    }

    private static final class SubSet<E> extends AbstractSet<E> {

        private final ImmutableMap<E, Integer> inputSet;
        private final int mask;

        SubSet(ImmutableMap<E, Integer> immutablemap, int i) {
            this.inputSet = immutablemap;
            this.mask = i;
        }

        public Iterator<E> iterator() {
            return new UnmodifiableIterator<E>() {
                final ImmutableList<E> elements;
                int remainingSetBits;

                {
                    this.elements = SubSet.this.inputSet.keySet().asList();
                    this.remainingSetBits = SubSet.this.mask;
                }

                public boolean hasNext() {
                    return this.remainingSetBits != 0;
                }

                public E next() {
                    int i = Integer.numberOfTrailingZeros(this.remainingSetBits);

                    if (i == 32) {
                        throw new NoSuchElementException();
                    } else {
                        this.remainingSetBits &= ~(1 << i);
                        return this.elements.get(i);
                    }
                }
            };
        }

        public int size() {
            return Integer.bitCount(this.mask);
        }

        public boolean contains(@Nullable Object object) {
            Integer integer = (Integer) this.inputSet.get(object);

            return integer != null && (this.mask & 1 << integer) != 0;
        }
    }

    private static final class CartesianSet<E> extends ForwardingCollection<List<E>> implements Set<List<E>> {

        private final transient ImmutableList<ImmutableSet<E>> axes;
        private final transient CartesianList<E> delegate;

        static <E> Set<List<E>> create(List<? extends Set<? extends E>> list) {
            ImmutableList.Builder immutablelist_builder = new ImmutableList.Builder<>(list.size());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Set set = (Set) iterator.next();
                ImmutableSet immutableset = ImmutableSet.copyOf((Collection) set);

                if (immutableset.isEmpty()) {
                    return ImmutableSet.of();
                }

                immutablelist_builder.add((Object) immutableset);
            }

            final ImmutableList immutablelist = immutablelist_builder.build();
            ImmutableList immutablelist1 = new ImmutableList<List<E>>() {
                public int size() {
                    return immutablelist.size();
                }

                public List<E> get(int i) {
                    return ((ImmutableSet) immutablelist.get(i)).asList();
                }

                @Override
                boolean isPartialView() {
                    return true;
                }
            };

            return new Sets.CartesianSet<>(immutablelist, new CartesianList<>(immutablelist1));
        }

        private CartesianSet(ImmutableList<ImmutableSet<E>> immutablelist, CartesianList<E> cartesianlist) {
            this.axes = immutablelist;
            this.delegate = cartesianlist;
        }

        @Override
        protected Collection<List<E>> delegate() {
            return this.delegate;
        }

        public boolean equals(@Nullable Object object) {
            if (object instanceof Sets.CartesianSet) {
                Sets.CartesianSet sets_cartesianset = (Sets.CartesianSet) object;

                return this.axes.equals(sets_cartesianset.axes);
            } else {
                return super.equals(object);
            }
        }

        public int hashCode() {
            int i = this.size() - 1;

            int j;

            for (j = 0; j < this.axes.size(); ++j) {
                i *= 31;
                i = ~(~i);
            }

            j = 1;

            for (Iterator iterator = this.axes.iterator(); iterator.hasNext(); j = ~(~j)) {
                Set set = (Set) iterator.next();

                j = 31 * j + this.size() / set.size() * set.hashCode();
            }

            j += i;
            return ~(~j);
        }
    }

    @GwtIncompatible("NavigableSet")
    private static class FilteredNavigableSet<E> extends Sets.FilteredSortedSet<E> implements NavigableSet<E> {

        FilteredNavigableSet(NavigableSet<E> navigableset, Predicate<? super E> predicate) {
            super((SortedSet) navigableset, predicate);
        }

        NavigableSet<E> unfiltered() {
            return (NavigableSet) this.unfiltered;
        }

        @Nullable
        public E lower(E e0) {
            return Iterators.getNext(this.headSet(e0, false).descendingIterator(), (Object) null);
        }

        @Nullable
        public E floor(E e0) {
            return Iterators.getNext(this.headSet(e0, true).descendingIterator(), (Object) null);
        }

        public E ceiling(E e0) {
            return Iterables.getFirst(this.tailSet(e0, true), (Object) null);
        }

        public E higher(E e0) {
            return Iterables.getFirst(this.tailSet(e0, false), (Object) null);
        }

        public E pollFirst() {
            return Iterables.removeFirstMatching(this.unfiltered(), this.predicate);
        }

        public E pollLast() {
            return Iterables.removeFirstMatching(this.unfiltered().descendingSet(), this.predicate);
        }

        public NavigableSet<E> descendingSet() {
            return Sets.filter(this.unfiltered().descendingSet(), this.predicate);
        }

        public Iterator<E> descendingIterator() {
            return Iterators.filter(this.unfiltered().descendingIterator(), this.predicate);
        }

        @Override
        public E last() {
            return this.descendingIterator().next();
        }

        public NavigableSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
            return Sets.filter(this.unfiltered().subSet(e0, flag, e1, flag1), this.predicate);
        }

        public NavigableSet<E> headSet(E e0, boolean flag) {
            return Sets.filter(this.unfiltered().headSet(e0, flag), this.predicate);
        }

        public NavigableSet<E> tailSet(E e0, boolean flag) {
            return Sets.filter(this.unfiltered().tailSet(e0, flag), this.predicate);
        }
    }

    private static class FilteredSortedSet<E> extends Sets.FilteredSet<E> implements SortedSet<E> {

        FilteredSortedSet(SortedSet<E> sortedset, Predicate<? super E> predicate) {
            super((Set) sortedset, predicate);
        }

        public Comparator<? super E> comparator() {
            return ((SortedSet) this.unfiltered).comparator();
        }

        public SortedSet<E> subSet(E e0, E e1) {
            return new Sets.FilteredSortedSet<>(((SortedSet) this.unfiltered).subSet(e0, e1), this.predicate);
        }

        public SortedSet<E> headSet(E e0) {
            return new Sets.FilteredSortedSet<>(((SortedSet) this.unfiltered).headSet(e0), this.predicate);
        }

        public SortedSet<E> tailSet(E e0) {
            return new Sets.FilteredSortedSet<>(((SortedSet) this.unfiltered).tailSet(e0), this.predicate);
        }

        public E first() {
            return this.iterator().next();
        }

        public E last() {
            SortedSet sortedset = (SortedSet) this.unfiltered;

            while (true) {
                Object object = sortedset.last();

                if (this.predicate.apply(object)) {
                    return object;
                }

                sortedset = sortedset.headSet(object);
            }
        }
    }

    private static class FilteredSet<E> extends Collections2.FilteredCollection<E> implements Set<E> {

        FilteredSet(Set<E> set, Predicate<? super E> predicate) {
            super(set, predicate);
        }

        public boolean equals(@Nullable Object object) {
            return Sets.equalsImpl(this, object);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    public abstract static class SetView<E> extends AbstractSet<E> {

        private SetView() {}

        public ImmutableSet<E> immutableCopy() {
            return ImmutableSet.copyOf((Collection) this);
        }

        public <S extends Set<E>> S copyInto(S s0) {
            s0.addAll(this);
            return s0;
        }
    }

    abstract static class ImprovedAbstractSet<E> extends AbstractSet<E> {

        ImprovedAbstractSet() {}

        public boolean removeAll(Collection<?> collection) {
            return Sets.removeAllImpl(this, collection);
        }

        public boolean retainAll(Collection<?> collection) {
            return super.retainAll((Collection) Preconditions.checkNotNull(collection));
        }
    }
}
