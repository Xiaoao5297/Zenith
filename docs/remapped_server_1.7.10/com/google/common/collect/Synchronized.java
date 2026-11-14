package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class Synchronized {

    private Synchronized() {}

    private static <E> Collection<E> collection(Collection<E> collection, @Nullable Object object) {
        return new Synchronized.SynchronizedCollection<>(collection, object);
    }

    @VisibleForTesting
    static <E> Set<E> set(Set<E> set, @Nullable Object object) {
        return new Synchronized.SynchronizedSet<>(set, object);
    }

    private static <E> SortedSet<E> sortedSet(SortedSet<E> sortedset, @Nullable Object object) {
        return new Synchronized.SynchronizedSortedSet<>(sortedset, object);
    }

    private static <E> List<E> list(List<E> list, @Nullable Object object) {
        return (List) (list instanceof RandomAccess ? new Synchronized.SynchronizedRandomAccessList<>(list, object) : new Synchronized.SynchronizedList<>(list, object));
    }

    static <E> Multiset<E> multiset(Multiset<E> multiset, @Nullable Object object) {
        return (Multiset) (!(multiset instanceof Synchronized.SynchronizedMultiset) && !(multiset instanceof ImmutableMultiset) ? new Synchronized.SynchronizedMultiset<>(multiset, object) : multiset);
    }

    static <K, V> Multimap<K, V> multimap(Multimap<K, V> multimap, @Nullable Object object) {
        return (Multimap) (!(multimap instanceof Synchronized.SynchronizedMultimap) && !(multimap instanceof ImmutableMultimap) ? new Synchronized.SynchronizedMultimap<>(multimap, object) : multimap);
    }

    static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> listmultimap, @Nullable Object object) {
        return (ListMultimap) (!(listmultimap instanceof Synchronized.SynchronizedListMultimap) && !(listmultimap instanceof ImmutableListMultimap) ? new Synchronized.SynchronizedListMultimap<>(listmultimap, object) : listmultimap);
    }

    static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> setmultimap, @Nullable Object object) {
        return (SetMultimap) (!(setmultimap instanceof Synchronized.SynchronizedSetMultimap) && !(setmultimap instanceof ImmutableSetMultimap) ? new Synchronized.SynchronizedSetMultimap<>(setmultimap, object) : setmultimap);
    }

    static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> sortedsetmultimap, @Nullable Object object) {
        return (SortedSetMultimap) (sortedsetmultimap instanceof Synchronized.SynchronizedSortedSetMultimap ? sortedsetmultimap : new Synchronized.SynchronizedSortedSetMultimap<>(sortedsetmultimap, object));
    }

    private static <E> Collection<E> typePreservingCollection(Collection<E> collection, @Nullable Object object) {
        return (Collection) (collection instanceof SortedSet ? sortedSet((SortedSet) collection, object) : (collection instanceof Set ? set((Set) collection, object) : (collection instanceof List ? list((List) collection, object) : collection(collection, object))));
    }

    private static <E> Set<E> typePreservingSet(Set<E> set, @Nullable Object object) {
        return (Set) (set instanceof SortedSet ? sortedSet((SortedSet) set, object) : set(set, object));
    }

    @VisibleForTesting
    static <K, V> Map<K, V> map(Map<K, V> map, @Nullable Object object) {
        return new Synchronized.SynchronizedMap<>(map, object);
    }

    static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> sortedmap, @Nullable Object object) {
        return new Synchronized.SynchronizedSortedMap<>(sortedmap, object);
    }

    static <K, V> BiMap<K, V> biMap(BiMap<K, V> bimap, @Nullable Object object) {
        return (BiMap) (!(bimap instanceof Synchronized.SynchronizedBiMap) && !(bimap instanceof ImmutableBiMap) ? new Synchronized.SynchronizedBiMap<>(bimap, object, (BiMap) null) : bimap);
    }

    @GwtIncompatible("NavigableSet")
    static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableset, @Nullable Object object) {
        return new Synchronized.SynchronizedNavigableSet<>(navigableset, object);
    }

    @GwtIncompatible("NavigableSet")
    static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableset) {
        return navigableSet(navigableset, (Object) null);
    }

    @GwtIncompatible("NavigableMap")
    static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigablemap) {
        return navigableMap(navigablemap, (Object) null);
    }

    @GwtIncompatible("NavigableMap")
    static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigablemap, @Nullable Object object) {
        return new Synchronized.SynchronizedNavigableMap<>(navigablemap, object);
    }

    @GwtIncompatible("works but is needed only for NavigableMap")
    private static <K, V> Entry<K, V> nullableSynchronizedEntry(@Nullable Entry<K, V> entry, @Nullable Object object) {
        return entry == null ? null : new Synchronized.SynchronizedEntry<>(entry, object);
    }

    static <E> Queue<E> queue(Queue<E> queue, @Nullable Object object) {
        return (Queue) (queue instanceof Synchronized.SynchronizedQueue ? queue : new Synchronized.SynchronizedQueue<>(queue, object));
    }

    @GwtIncompatible("Deque")
    static <E> Deque<E> deque(Deque<E> deque, @Nullable Object object) {
        return new Synchronized.SynchronizedDeque<>(deque, object);
    }

    @GwtIncompatible("Deque")
    private static final class SynchronizedDeque<E> extends Synchronized.SynchronizedQueue<E> implements Deque<E> {

        private static final long serialVersionUID = 0L;

        SynchronizedDeque(Deque<E> deque, @Nullable Object object) {
            super((Queue) deque, object);
        }

        @Override
        Deque<E> delegate() {
            return (Deque) super.delegate();
        }

        public void addFirst(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().addFirst(e0);
            }
        }

        public void addLast(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().addLast(e0);
            }
        }

        public boolean offerFirst(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().offerFirst(e0);
            }
        }

        public boolean offerLast(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().offerLast(e0);
            }
        }

        public E removeFirst() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeFirst();
            }
        }

        public E removeLast() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeLast();
            }
        }

        public E pollFirst() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().pollFirst();
            }
        }

        public E pollLast() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().pollLast();
            }
        }

        public E getFirst() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().getFirst();
            }
        }

        public E getLast() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().getLast();
            }
        }

        public E peekFirst() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().peekFirst();
            }
        }

        public E peekLast() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().peekLast();
            }
        }

        public boolean removeFirstOccurrence(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeFirstOccurrence(object);
            }
        }

        public boolean removeLastOccurrence(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeLastOccurrence(object);
            }
        }

        public void push(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().push(e0);
            }
        }

        public E pop() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().pop();
            }
        }

        public Iterator<E> descendingIterator() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().descendingIterator();
            }
        }
    }

    private static class SynchronizedQueue<E> extends Synchronized.SynchronizedCollection<E> implements Queue<E> {

        private static final long serialVersionUID = 0L;

        SynchronizedQueue(Queue<E> queue, @Nullable Object object) {
            super(queue, object, null);
        }

        @Override
        Queue<E> delegate() {
            return (Queue) super.delegate();
        }

        public E element() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().element();
            }
        }

        public boolean offer(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().offer(e0);
            }
        }

        public E peek() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().peek();
            }
        }

        public E poll() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().poll();
            }
        }

        public E remove() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().remove();
            }
        }
    }

    @GwtIncompatible("works but is needed only for NavigableMap")
    private static class SynchronizedEntry<K, V> extends Synchronized.SynchronizedObject implements Entry<K, V> {

        private static final long serialVersionUID = 0L;

        SynchronizedEntry(Entry<K, V> entry, @Nullable Object object) {
            super(entry, object);
        }

        @Override
        Entry<K, V> delegate() {
            return (Entry) super.delegate();
        }

        public boolean equals(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().equals(object);
            }
        }

        public int hashCode() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }

        public K getKey() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().getKey();
            }
        }

        public V getValue() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().getValue();
            }
        }

        public V setValue(V v0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().setValue(v0);
            }
        }
    }

    @GwtIncompatible("NavigableMap")
    @VisibleForTesting
    static class SynchronizedNavigableMap<K, V> extends Synchronized.SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {

        transient NavigableSet<K> descendingKeySet;
        transient NavigableMap<K, V> descendingMap;
        transient NavigableSet<K> navigableKeySet;
        private static final long serialVersionUID = 0L;

        SynchronizedNavigableMap(NavigableMap<K, V> navigablemap, @Nullable Object object) {
            super((SortedMap) navigablemap, object);
        }

        @Override
        NavigableMap<K, V> delegate() {
            return (NavigableMap) super.delegate();
        }

        public Entry<K, V> ceilingEntry(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().ceilingEntry(k0), this.mutex);
            }
        }

        public K ceilingKey(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().ceilingKey(k0);
            }
        }

        public NavigableSet<K> descendingKeySet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.descendingKeySet == null ? (this.descendingKeySet = Synchronized.navigableSet(this.delegate().descendingKeySet(), this.mutex)) : this.descendingKeySet;
            }
        }

        public NavigableMap<K, V> descendingMap() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.descendingMap == null ? (this.descendingMap = Synchronized.navigableMap(this.delegate().descendingMap(), this.mutex)) : this.descendingMap;
            }
        }

        public Entry<K, V> firstEntry() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().firstEntry(), this.mutex);
            }
        }

        public Entry<K, V> floorEntry(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().floorEntry(k0), this.mutex);
            }
        }

        public K floorKey(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().floorKey(k0);
            }
        }

        public NavigableMap<K, V> headMap(K k0, boolean flag) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.navigableMap(this.delegate().headMap(k0, flag), this.mutex);
            }
        }

        public Entry<K, V> higherEntry(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().higherEntry(k0), this.mutex);
            }
        }

        public K higherKey(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().higherKey(k0);
            }
        }

        public Entry<K, V> lastEntry() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().lastEntry(), this.mutex);
            }
        }

        public Entry<K, V> lowerEntry(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().lowerEntry(k0), this.mutex);
            }
        }

        public K lowerKey(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().lowerKey(k0);
            }
        }

        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }

        public NavigableSet<K> navigableKeySet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.navigableKeySet == null ? (this.navigableKeySet = Synchronized.navigableSet(this.delegate().navigableKeySet(), this.mutex)) : this.navigableKeySet;
            }
        }

        public Entry<K, V> pollFirstEntry() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().pollFirstEntry(), this.mutex);
            }
        }

        public Entry<K, V> pollLastEntry() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.nullableSynchronizedEntry(this.delegate().pollLastEntry(), this.mutex);
            }
        }

        public NavigableMap<K, V> subMap(K k0, boolean flag, K k1, boolean flag1) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.navigableMap(this.delegate().subMap(k0, flag, k1, flag1), this.mutex);
            }
        }

        public NavigableMap<K, V> tailMap(K k0, boolean flag) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.navigableMap(this.delegate().tailMap(k0, flag), this.mutex);
            }
        }

        @Override
        public SortedMap<K, V> headMap(K k0) {
            return this.headMap(k0, false);
        }

        @Override
        public SortedMap<K, V> subMap(K k0, K k1) {
            return this.subMap(k0, true, k1, false);
        }

        @Override
        public SortedMap<K, V> tailMap(K k0) {
            return this.tailMap(k0, true);
        }
    }

    @GwtIncompatible("NavigableSet")
    @VisibleForTesting
    static class SynchronizedNavigableSet<E> extends Synchronized.SynchronizedSortedSet<E> implements NavigableSet<E> {

        transient NavigableSet<E> descendingSet;
        private static final long serialVersionUID = 0L;

        SynchronizedNavigableSet(NavigableSet<E> navigableset, @Nullable Object object) {
            super((SortedSet) navigableset, object);
        }

        @Override
        NavigableSet<E> delegate() {
            return (NavigableSet) super.delegate();
        }

        public E ceiling(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().ceiling(e0);
            }
        }

        public Iterator<E> descendingIterator() {
            return this.delegate().descendingIterator();
        }

        public NavigableSet<E> descendingSet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.descendingSet == null) {
                    NavigableSet navigableset = Synchronized.navigableSet(this.delegate().descendingSet(), this.mutex);

                    this.descendingSet = navigableset;
                    return navigableset;
                } else {
                    return this.descendingSet;
                }
            }
        }

        public E floor(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().floor(e0);
            }
        }

        public NavigableSet<E> headSet(E e0, boolean flag) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.navigableSet(this.delegate().headSet(e0, flag), this.mutex);
            }
        }

        public E higher(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().higher(e0);
            }
        }

        public E lower(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().lower(e0);
            }
        }

        public E pollFirst() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().pollFirst();
            }
        }

        public E pollLast() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().pollLast();
            }
        }

        public NavigableSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.navigableSet(this.delegate().subSet(e0, flag, e1, flag1), this.mutex);
            }
        }

        public NavigableSet<E> tailSet(E e0, boolean flag) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.navigableSet(this.delegate().tailSet(e0, flag), this.mutex);
            }
        }

        @Override
        public SortedSet<E> headSet(E e0) {
            return this.headSet(e0, false);
        }

        @Override
        public SortedSet<E> subSet(E e0, E e1) {
            return this.subSet(e0, true, e1, false);
        }

        @Override
        public SortedSet<E> tailSet(E e0) {
            return this.tailSet(e0, true);
        }
    }

    private static class SynchronizedAsMapValues<V> extends Synchronized.SynchronizedCollection<Collection<V>> {

        private static final long serialVersionUID = 0L;

        SynchronizedAsMapValues(Collection<Collection<V>> collection, @Nullable Object object) {
            super(collection, object, null);
        }

        @Override
        public Iterator<Collection<V>> iterator() {
            final Iterator iterator = super.iterator();

            return new ForwardingIterator<Collection<V>>() {
                @Override
                protected Iterator<Collection<V>> delegate() {
                    return iterator;
                }

                @Override
                public Collection<V> next() {
                    return Synchronized.typePreservingCollection((Collection) super.next(), SynchronizedAsMapValues.this.mutex);
                }
            };
        }
    }

    private static class SynchronizedAsMap<K, V> extends Synchronized.SynchronizedMap<K, Collection<V>> {

        transient Set<Entry<K, Collection<V>>> asMapEntrySet;
        transient Collection<Collection<V>> asMapValues;
        private static final long serialVersionUID = 0L;

        SynchronizedAsMap(Map<K, Collection<V>> map, @Nullable Object object) {
            super(map, object);
        }

        @Override
        public Collection<V> get(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                Collection collection = (Collection) super.get(object);

                return collection == null ? null : Synchronized.typePreservingCollection(collection, this.mutex);
            }
        }

        @Override
        public Set<Entry<K, Collection<V>>> entrySet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.asMapEntrySet == null) {
                    this.asMapEntrySet = new Synchronized.SynchronizedAsMapEntries<>(this.delegate().entrySet(), this.mutex);
                }

                return this.asMapEntrySet;
            }
        }

        @Override
        public Collection<Collection<V>> values() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.asMapValues == null) {
                    this.asMapValues = new Synchronized.SynchronizedAsMapValues<>(this.delegate().values(), this.mutex);
                }

                return this.asMapValues;
            }
        }

        @Override
        public boolean containsValue(Object object) {
            return this.values().contains(object);
        }
    }

    @VisibleForTesting
    static class SynchronizedBiMap<K, V> extends Synchronized.SynchronizedMap<K, V> implements BiMap<K, V>, Serializable {

        private transient Set<V> valueSet;
        private transient BiMap<V, K> inverse;
        private static final long serialVersionUID = 0L;

        private SynchronizedBiMap(BiMap<K, V> bimap, @Nullable Object object, @Nullable BiMap<V, K> bimap1) {
            super((Map) bimap, object);
            this.inverse = bimap1;
        }

        @Override
        BiMap<K, V> delegate() {
            return (BiMap) super.delegate();
        }

        @Override
        public Set<V> values() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.valueSet == null) {
                    this.valueSet = Synchronized.set(this.delegate().values(), this.mutex);
                }

                return this.valueSet;
            }
        }

        @Override
        public V forcePut(K k0, V v0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().forcePut(k0, v0);
            }
        }

        @Override
        public BiMap<V, K> inverse() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.inverse == null) {
                    this.inverse = new Synchronized.SynchronizedBiMap<>(this.delegate().inverse(), this.mutex, this);
                }

                return this.inverse;
            }
        }
    }

    static class SynchronizedSortedMap<K, V> extends Synchronized.SynchronizedMap<K, V> implements SortedMap<K, V> {

        private static final long serialVersionUID = 0L;

        SynchronizedSortedMap(SortedMap<K, V> sortedmap, @Nullable Object object) {
            super((Map) sortedmap, object);
        }

        @Override
        SortedMap<K, V> delegate() {
            return (SortedMap) super.delegate();
        }

        public Comparator<? super K> comparator() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().comparator();
            }
        }

        public K firstKey() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().firstKey();
            }
        }

        public SortedMap<K, V> headMap(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.sortedMap(this.delegate().headMap(k0), this.mutex);
            }
        }

        public K lastKey() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().lastKey();
            }
        }

        public SortedMap<K, V> subMap(K k0, K k1) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.sortedMap(this.delegate().subMap(k0, k1), this.mutex);
            }
        }

        public SortedMap<K, V> tailMap(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.sortedMap(this.delegate().tailMap(k0), this.mutex);
            }
        }
    }

    private static class SynchronizedMap<K, V> extends Synchronized.SynchronizedObject implements Map<K, V> {

        transient Set<K> keySet;
        transient Collection<V> values;
        transient Set<Entry<K, V>> entrySet;
        private static final long serialVersionUID = 0L;

        SynchronizedMap(Map<K, V> map, @Nullable Object object) {
            super(map, object);
        }

        @Override
        Map<K, V> delegate() {
            return (Map) super.delegate();
        }

        public void clear() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().clear();
            }
        }

        public boolean containsKey(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().containsKey(object);
            }
        }

        public boolean containsValue(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().containsValue(object);
            }
        }

        public Set<Entry<K, V>> entrySet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.set(this.delegate().entrySet(), this.mutex);
                }

                return this.entrySet;
            }
        }

        public V get(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().get(object);
            }
        }

        public boolean isEmpty() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().isEmpty();
            }
        }

        public Set<K> keySet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = Synchronized.set(this.delegate().keySet(), this.mutex);
                }

                return this.keySet;
            }
        }

        public V put(K k0, V v0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().put(k0, v0);
            }
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().putAll(map);
            }
        }

        public V remove(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().remove(object);
            }
        }

        public int size() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().size();
            }
        }

        public Collection<V> values() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.values == null) {
                    this.values = Synchronized.collection(this.delegate().values(), this.mutex);
                }

                return this.values;
            }
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else {
                Object object1 = this.mutex;

                synchronized (this.mutex) {
                    return this.delegate().equals(object);
                }
            }
        }

        public int hashCode() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
    }

    private static class SynchronizedAsMapEntries<K, V> extends Synchronized.SynchronizedSet<Entry<K, Collection<V>>> {

        private static final long serialVersionUID = 0L;

        SynchronizedAsMapEntries(Set<Entry<K, Collection<V>>> set, @Nullable Object object) {
            super(set, object);
        }

        @Override
        public Iterator<Entry<K, Collection<V>>> iterator() {
            final Iterator iterator = super.iterator();

            return new ForwardingIterator<Entry<K, Collection<V>>>() {
                @Override
                protected Iterator<Entry<K, Collection<V>>> delegate() {
                    return iterator;
                }

                @Override
                public Entry<K, Collection<V>> next() {
                    final Entry entry = (Entry) super.next();

                    return new ForwardingMapEntry<K, Collection<V>>() {
                        @Override
                        protected Entry<K, Collection<V>> delegate() {
                            return entry;
                        }

                        @Override
                        public Collection<V> getValue() {
                            return Synchronized.typePreservingCollection((Collection) entry.getValue(), SynchronizedAsMapEntries.this.mutex);
                        }
                    };
                }
            };
        }

        @Override
        public Object[] toArray() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return ObjectArrays.toArrayImpl(this.delegate());
            }
        }

        @Override
        public <T> T[] toArray(T[] at) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return ObjectArrays.toArrayImpl(this.delegate(), at);
            }
        }

        @Override
        public boolean contains(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return Maps.containsEntryImpl(this.delegate(), object);
            }
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Collections2.containsAllImpl(this.delegate(), collection);
            }
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else {
                Object object1 = this.mutex;

                synchronized (this.mutex) {
                    return Sets.equalsImpl(this.delegate(), object);
                }
            }
        }

        @Override
        public boolean remove(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return Maps.removeEntryImpl(this.delegate(), object);
            }
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Iterators.removeAll(this.delegate().iterator(), collection);
            }
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Iterators.retainAll(this.delegate().iterator(), collection);
            }
        }
    }

    private static class SynchronizedSortedSetMultimap<K, V> extends Synchronized.SynchronizedSetMultimap<K, V> implements SortedSetMultimap<K, V> {

        private static final long serialVersionUID = 0L;

        SynchronizedSortedSetMultimap(SortedSetMultimap<K, V> sortedsetmultimap, @Nullable Object object) {
            super((SetMultimap) sortedsetmultimap, object);
        }

        @Override
        SortedSetMultimap<K, V> delegate() {
            return (SortedSetMultimap) super.delegate();
        }

        @Override
        public SortedSet<V> get(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.sortedSet(this.delegate().get(k0), this.mutex);
            }
        }

        @Override
        public SortedSet<V> removeAll(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeAll(object);
            }
        }

        @Override
        public SortedSet<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().replaceValues(k0, iterable);
            }
        }

        @Override
        public Comparator<? super V> valueComparator() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().valueComparator();
            }
        }
    }

    private static class SynchronizedSetMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V> implements SetMultimap<K, V> {

        transient Set<Entry<K, V>> entrySet;
        private static final long serialVersionUID = 0L;

        SynchronizedSetMultimap(SetMultimap<K, V> setmultimap, @Nullable Object object) {
            super((Multimap) setmultimap, object);
        }

        @Override
        SetMultimap<K, V> delegate() {
            return (SetMultimap) super.delegate();
        }

        @Override
        public Set<V> get(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.set(this.delegate().get(k0), this.mutex);
            }
        }

        @Override
        public Set<V> removeAll(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeAll(object);
            }
        }

        @Override
        public Set<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().replaceValues(k0, iterable);
            }
        }

        @Override
        public Set<Entry<K, V>> entries() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.set(this.delegate().entries(), this.mutex);
                }

                return this.entrySet;
            }
        }
    }

    private static class SynchronizedListMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V> implements ListMultimap<K, V> {

        private static final long serialVersionUID = 0L;

        SynchronizedListMultimap(ListMultimap<K, V> listmultimap, @Nullable Object object) {
            super((Multimap) listmultimap, object);
        }

        @Override
        ListMultimap<K, V> delegate() {
            return (ListMultimap) super.delegate();
        }

        @Override
        public List<V> get(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.list(this.delegate().get(k0), this.mutex);
            }
        }

        @Override
        public List<V> removeAll(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeAll(object);
            }
        }

        @Override
        public List<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().replaceValues(k0, iterable);
            }
        }
    }

    private static class SynchronizedMultimap<K, V> extends Synchronized.SynchronizedObject implements Multimap<K, V> {

        transient Set<K> keySet;
        transient Collection<V> valuesCollection;
        transient Collection<Entry<K, V>> entries;
        transient Map<K, Collection<V>> asMap;
        transient Multiset<K> keys;
        private static final long serialVersionUID = 0L;

        @Override
        Multimap<K, V> delegate() {
            return (Multimap) super.delegate();
        }

        SynchronizedMultimap(Multimap<K, V> multimap, @Nullable Object object) {
            super(multimap, object);
        }

        @Override
        public int size() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().size();
            }
        }

        @Override
        public boolean isEmpty() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().isEmpty();
            }
        }

        @Override
        public boolean containsKey(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().containsKey(object);
            }
        }

        @Override
        public boolean containsValue(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().containsValue(object);
            }
        }

        @Override
        public boolean containsEntry(Object object, Object object1) {
            Object object2 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().containsEntry(object, object1);
            }
        }

        @Override
        public Collection<V> get(K k0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.typePreservingCollection(this.delegate().get(k0), this.mutex);
            }
        }

        @Override
        public boolean put(K k0, V v0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().put(k0, v0);
            }
        }

        @Override
        public boolean putAll(K k0, Iterable<? extends V> iterable) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().putAll(k0, iterable);
            }
        }

        @Override
        public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().putAll(multimap);
            }
        }

        @Override
        public Collection<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().replaceValues(k0, iterable);
            }
        }

        @Override
        public boolean remove(Object object, Object object1) {
            Object object2 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().remove(object, object1);
            }
        }

        @Override
        public Collection<V> removeAll(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeAll(object);
            }
        }

        @Override
        public void clear() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().clear();
            }
        }

        @Override
        public Set<K> keySet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = Synchronized.typePreservingSet(this.delegate().keySet(), this.mutex);
                }

                return this.keySet;
            }
        }

        @Override
        public Collection<V> values() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.valuesCollection == null) {
                    this.valuesCollection = Synchronized.collection(this.delegate().values(), this.mutex);
                }

                return this.valuesCollection;
            }
        }

        @Override
        public Collection<Entry<K, V>> entries() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.entries == null) {
                    this.entries = Synchronized.typePreservingCollection(this.delegate().entries(), this.mutex);
                }

                return this.entries;
            }
        }

        @Override
        public Map<K, Collection<V>> asMap() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.asMap == null) {
                    this.asMap = new Synchronized.SynchronizedAsMap<>(this.delegate().asMap(), this.mutex);
                }

                return this.asMap;
            }
        }

        @Override
        public Multiset<K> keys() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.keys == null) {
                    this.keys = Synchronized.multiset(this.delegate().keys(), this.mutex);
                }

                return this.keys;
            }
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else {
                Object object1 = this.mutex;

                synchronized (this.mutex) {
                    return this.delegate().equals(object);
                }
            }
        }

        @Override
        public int hashCode() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
    }

    private static class SynchronizedMultiset<E> extends Synchronized.SynchronizedCollection<E> implements Multiset<E> {

        transient Set<E> elementSet;
        transient Set<Multiset.Entry<E>> entrySet;
        private static final long serialVersionUID = 0L;

        SynchronizedMultiset(Multiset<E> multiset, @Nullable Object object) {
            super(multiset, object, null);
        }

        @Override
        Multiset<E> delegate() {
            return (Multiset) super.delegate();
        }

        @Override
        public int count(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().count(object);
            }
        }

        @Override
        public int add(E e0, int i) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().add(e0, i);
            }
        }

        @Override
        public int remove(Object object, int i) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().remove(object, i);
            }
        }

        @Override
        public int setCount(E e0, int i) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().setCount(e0, i);
            }
        }

        @Override
        public boolean setCount(E e0, int i, int j) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().setCount(e0, i, j);
            }
        }

        @Override
        public Set<E> elementSet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.elementSet == null) {
                    this.elementSet = Synchronized.typePreservingSet(this.delegate().elementSet(), this.mutex);
                }

                return this.elementSet;
            }
        }

        @Override
        public Set<Multiset.Entry<E>> entrySet() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.typePreservingSet(this.delegate().entrySet(), this.mutex);
                }

                return this.entrySet;
            }
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else {
                Object object1 = this.mutex;

                synchronized (this.mutex) {
                    return this.delegate().equals(object);
                }
            }
        }

        @Override
        public int hashCode() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
    }

    private static class SynchronizedRandomAccessList<E> extends Synchronized.SynchronizedList<E> implements RandomAccess {

        private static final long serialVersionUID = 0L;

        SynchronizedRandomAccessList(List<E> list, @Nullable Object object) {
            super(list, object);
        }
    }

    private static class SynchronizedList<E> extends Synchronized.SynchronizedCollection<E> implements List<E> {

        private static final long serialVersionUID = 0L;

        SynchronizedList(List<E> list, @Nullable Object object) {
            super(list, object, null);
        }

        @Override
        List<E> delegate() {
            return (List) super.delegate();
        }

        public void add(int i, E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().add(i, e0);
            }
        }

        public boolean addAll(int i, Collection<? extends E> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().addAll(i, collection);
            }
        }

        public E get(int i) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().get(i);
            }
        }

        public int indexOf(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().indexOf(object);
            }
        }

        public int lastIndexOf(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().lastIndexOf(object);
            }
        }

        public ListIterator<E> listIterator() {
            return this.delegate().listIterator();
        }

        public ListIterator<E> listIterator(int i) {
            return this.delegate().listIterator(i);
        }

        public E remove(int i) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().remove(i);
            }
        }

        public E set(int i, E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().set(i, e0);
            }
        }

        public List<E> subList(int i, int j) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.list(this.delegate().subList(i, j), this.mutex);
            }
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else {
                Object object1 = this.mutex;

                synchronized (this.mutex) {
                    return this.delegate().equals(object);
                }
            }
        }

        public int hashCode() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
    }

    static class SynchronizedSortedSet<E> extends Synchronized.SynchronizedSet<E> implements SortedSet<E> {

        private static final long serialVersionUID = 0L;

        SynchronizedSortedSet(SortedSet<E> sortedset, @Nullable Object object) {
            super((Set) sortedset, object);
        }

        @Override
        SortedSet<E> delegate() {
            return (SortedSet) super.delegate();
        }

        public Comparator<? super E> comparator() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().comparator();
            }
        }

        public SortedSet<E> subSet(E e0, E e1) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.sortedSet(this.delegate().subSet(e0, e1), this.mutex);
            }
        }

        public SortedSet<E> headSet(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.sortedSet(this.delegate().headSet(e0), this.mutex);
            }
        }

        public SortedSet<E> tailSet(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return Synchronized.sortedSet(this.delegate().tailSet(e0), this.mutex);
            }
        }

        public E first() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().first();
            }
        }

        public E last() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().last();
            }
        }
    }

    static class SynchronizedSet<E> extends Synchronized.SynchronizedCollection<E> implements Set<E> {

        private static final long serialVersionUID = 0L;

        SynchronizedSet(Set<E> set, @Nullable Object object) {
            super(set, object, null);
        }

        @Override
        Set<E> delegate() {
            return (Set) super.delegate();
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else {
                Object object1 = this.mutex;

                synchronized (this.mutex) {
                    return this.delegate().equals(object);
                }
            }
        }

        public int hashCode() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
    }

    @VisibleForTesting
    static class SynchronizedCollection<E> extends Synchronized.SynchronizedObject implements Collection<E> {

        private static final long serialVersionUID = 0L;

        private SynchronizedCollection(Collection<E> collection, @Nullable Object object) {
            super(collection, object);
        }

        @Override
        Collection<E> delegate() {
            return (Collection) super.delegate();
        }

        public boolean add(E e0) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().add(e0);
            }
        }

        public boolean addAll(Collection<? extends E> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().addAll(collection);
            }
        }

        public void clear() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                this.delegate().clear();
            }
        }

        public boolean contains(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().contains(object);
            }
        }

        public boolean containsAll(Collection<?> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().containsAll(collection);
            }
        }

        public boolean isEmpty() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().isEmpty();
            }
        }

        public Iterator<E> iterator() {
            return this.delegate().iterator();
        }

        public boolean remove(Object object) {
            Object object1 = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().remove(object);
            }
        }

        public boolean removeAll(Collection<?> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().removeAll(collection);
            }
        }

        public boolean retainAll(Collection<?> collection) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().retainAll(collection);
            }
        }

        public int size() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().size();
            }
        }

        public Object[] toArray() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().toArray();
            }
        }

        public <T> T[] toArray(T[] at) {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate().toArray(at);
            }
        }
    }

    static class SynchronizedObject implements Serializable {

        final Object delegate;
        final Object mutex;
        @GwtIncompatible("not needed in emulated source")
        private static final long serialVersionUID = 0L;

        SynchronizedObject(Object object, @Nullable Object object1) {
            this.delegate = Preconditions.checkNotNull(object);
            this.mutex = object1 == null ? this : object1;
        }

        Object delegate() {
            return this.delegate;
        }

        public String toString() {
            Object object = this.mutex;

            synchronized (this.mutex) {
                return this.delegate.toString();
            }
        }

        @GwtIncompatible("java.io.ObjectOutputStream")
        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            Object object = this.mutex;

            synchronized (this.mutex) {
                objectoutputstream.defaultWriteObject();
            }
        }
    }
}
