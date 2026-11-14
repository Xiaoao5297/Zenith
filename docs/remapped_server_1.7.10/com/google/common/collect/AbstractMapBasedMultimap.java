package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
abstract class AbstractMapBasedMultimap<K, V> extends AbstractMultimap<K, V> implements Serializable {

    private transient Map<K, Collection<V>> map;
    private transient int totalSize;
    private static final long serialVersionUID = 2447537837011683357L;

    protected AbstractMapBasedMultimap(Map<K, Collection<V>> map) {
        Preconditions.checkArgument(map.isEmpty());
        this.map = map;
    }

    final void setMap(Map<K, Collection<V>> map) {
        this.map = map;
        this.totalSize = 0;

        Collection collection;

        for (Iterator iterator = map.values().iterator(); iterator.hasNext(); this.totalSize += collection.size()) {
            collection = (Collection) iterator.next();
            Preconditions.checkArgument(!collection.isEmpty());
        }

    }

    Collection<V> createUnmodifiableEmptyCollection() {
        return this.unmodifiableCollectionSubclass(this.createCollection());
    }

    abstract Collection<V> createCollection();

    Collection<V> createCollection(@Nullable K k0) {
        return this.createCollection();
    }

    Map<K, Collection<V>> backingMap() {
        return this.map;
    }

    @Override
    public int size() {
        return this.totalSize;
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.map.containsKey(object);
    }

    @Override
    public boolean put(@Nullable K k0, @Nullable V v0) {
        Collection collection = (Collection) this.map.get(k0);

        if (collection == null) {
            collection = this.createCollection(k0);
            if (collection.add(v0)) {
                ++this.totalSize;
                this.map.put(k0, collection);
                return true;
            } else {
                throw new AssertionError("New Collection violated the Collection spec");
            }
        } else if (collection.add(v0)) {
            ++this.totalSize;
            return true;
        } else {
            return false;
        }
    }

    private Collection<V> getOrCreateCollection(@Nullable K k0) {
        Collection collection = (Collection) this.map.get(k0);

        if (collection == null) {
            collection = this.createCollection(k0);
            this.map.put(k0, collection);
        }

        return collection;
    }

    @Override
    public Collection<V> replaceValues(@Nullable K k0, Iterable<? extends V> iterable) {
        Iterator iterator = iterable.iterator();

        if (!iterator.hasNext()) {
            return this.removeAll(k0);
        } else {
            Collection collection = this.getOrCreateCollection(k0);
            Collection collection1 = this.createCollection();

            collection1.addAll(collection);
            this.totalSize -= collection.size();
            collection.clear();

            while (iterator.hasNext()) {
                if (collection.add(iterator.next())) {
                    ++this.totalSize;
                }
            }

            return this.unmodifiableCollectionSubclass(collection1);
        }
    }

    @Override
    public Collection<V> removeAll(@Nullable Object object) {
        Collection collection = (Collection) this.map.remove(object);

        if (collection == null) {
            return this.createUnmodifiableEmptyCollection();
        } else {
            Collection collection1 = this.createCollection();

            collection1.addAll(collection);
            this.totalSize -= collection.size();
            collection.clear();
            return this.unmodifiableCollectionSubclass(collection1);
        }
    }

    Collection<V> unmodifiableCollectionSubclass(Collection<V> collection) {
        return (Collection) (collection instanceof SortedSet ? Collections.unmodifiableSortedSet((SortedSet) collection) : (collection instanceof Set ? Collections.unmodifiableSet((Set) collection) : (collection instanceof List ? Collections.unmodifiableList((List) collection) : Collections.unmodifiableCollection(collection))));
    }

    @Override
    public void clear() {
        Iterator iterator = this.map.values().iterator();

        while (iterator.hasNext()) {
            Collection collection = (Collection) iterator.next();

            collection.clear();
        }

        this.map.clear();
        this.totalSize = 0;
    }

    @Override
    public Collection<V> get(@Nullable K k0) {
        Collection collection = (Collection) this.map.get(k0);

        if (collection == null) {
            collection = this.createCollection(k0);
        }

        return this.wrapCollection(k0, collection);
    }

    Collection<V> wrapCollection(@Nullable K k0, Collection<V> collection) {
        return (Collection) (collection instanceof SortedSet ? new AbstractMapBasedMultimap.WrappedSortedSet(k0, (SortedSet) collection, (AbstractMapBasedMultimap.WrappedCollection) null) : (collection instanceof Set ? new AbstractMapBasedMultimap.WrappedSet(k0, (Set) collection) : (collection instanceof List ? this.wrapList(k0, (List) collection, (AbstractMapBasedMultimap.WrappedCollection) null) : new AbstractMapBasedMultimap.WrappedCollection(k0, collection, (AbstractMapBasedMultimap.WrappedCollection) null))));
    }

    private List<V> wrapList(@Nullable K k0, List<V> list, @Nullable AbstractMapBasedMultimap<K, V>.WrappedCollection abstractmapbasedmultimap_wrappedcollection) {
        return (List) (list instanceof RandomAccess ? new AbstractMapBasedMultimap.RandomAccessWrappedList(k0, list, abstractmapbasedmultimap_wrappedcollection) : new AbstractMapBasedMultimap.WrappedList(k0, list, abstractmapbasedmultimap_wrappedcollection));
    }

    private Iterator<V> iteratorOrListIterator(Collection<V> collection) {
        return (Iterator) (collection instanceof List ? ((List) collection).listIterator() : collection.iterator());
    }

    @Override
    Set<K> createKeySet() {
        return (Set) (this.map instanceof SortedMap ? new AbstractMapBasedMultimap.SortedKeySet((SortedMap) this.map) : new AbstractMapBasedMultimap.KeySet(this.map));
    }

    private int removeValuesForKey(Object object) {
        Collection collection = (Collection) Maps.safeRemove(this.map, object);
        int i = 0;

        if (collection != null) {
            i = collection.size();
            collection.clear();
            this.totalSize -= i;
        }

        return i;
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    Iterator<V> valueIterator() {
        return new AbstractMapBasedMultimap<K, V>.Itr<V>() {
            @Override
            V output(K k0, V v0) {
                return v0;
            }
        };
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return super.entries();
    }

    @Override
    Iterator<Entry<K, V>> entryIterator() {
        return new AbstractMapBasedMultimap<K, V>.Itr<Entry<K, V>>() {
            @Override
            Entry<K, V> output(K k0, V v0) {
                return Maps.immutableEntry(k0, v0);
            }
        };
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return (Map) (this.map instanceof SortedMap ? new AbstractMapBasedMultimap.SortedAsMap((SortedMap) this.map) : new AbstractMapBasedMultimap.AsMap(this.map));
    }

    @GwtIncompatible("NavigableAsMap")
    class NavigableAsMap extends AbstractMapBasedMultimap<K, V>.SortedAsMap implements NavigableMap<K, Collection<V>> {

        NavigableAsMap(NavigableMap<K, Collection<V>> navigablemap) {
            super((SortedMap) navigablemap);
        }

        @Override
        NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap) super.sortedMap();
        }

        public Entry<K, Collection<V>> lowerEntry(K k0) {
            Entry entry = this.sortedMap().lowerEntry(k0);

            return entry == null ? null : this.wrapEntry(entry);
        }

        public K lowerKey(K k0) {
            return this.sortedMap().lowerKey(k0);
        }

        public Entry<K, Collection<V>> floorEntry(K k0) {
            Entry entry = this.sortedMap().floorEntry(k0);

            return entry == null ? null : this.wrapEntry(entry);
        }

        public K floorKey(K k0) {
            return this.sortedMap().floorKey(k0);
        }

        public Entry<K, Collection<V>> ceilingEntry(K k0) {
            Entry entry = this.sortedMap().ceilingEntry(k0);

            return entry == null ? null : this.wrapEntry(entry);
        }

        public K ceilingKey(K k0) {
            return this.sortedMap().ceilingKey(k0);
        }

        public Entry<K, Collection<V>> higherEntry(K k0) {
            Entry entry = this.sortedMap().higherEntry(k0);

            return entry == null ? null : this.wrapEntry(entry);
        }

        public K higherKey(K k0) {
            return this.sortedMap().higherKey(k0);
        }

        public Entry<K, Collection<V>> firstEntry() {
            Entry entry = this.sortedMap().firstEntry();

            return entry == null ? null : this.wrapEntry(entry);
        }

        public Entry<K, Collection<V>> lastEntry() {
            Entry entry = this.sortedMap().lastEntry();

            return entry == null ? null : this.wrapEntry(entry);
        }

        public Entry<K, Collection<V>> pollFirstEntry() {
            return this.pollAsMapEntry(this.entrySet().iterator());
        }

        public Entry<K, Collection<V>> pollLastEntry() {
            return this.pollAsMapEntry(this.descendingMap().entrySet().iterator());
        }

        Entry<K, Collection<V>> pollAsMapEntry(Iterator<Entry<K, Collection<V>>> iterator) {
            if (!iterator.hasNext()) {
                return null;
            } else {
                Entry entry = (Entry) iterator.next();
                Collection collection = AbstractMapBasedMultimap.this.createCollection();

                collection.addAll((Collection) entry.getValue());
                iterator.remove();
                return Maps.immutableEntry(entry.getKey(), AbstractMapBasedMultimap.this.unmodifiableCollectionSubclass(collection));
            }
        }

        public NavigableMap<K, Collection<V>> descendingMap() {
            return AbstractMapBasedMultimap.this.new NavigableAsMap(this.sortedMap().descendingMap());
        }

        @Override
        public NavigableSet<K> keySet() {
            return (NavigableSet) super.keySet();
        }

        @Override
        NavigableSet<K> createKeySet() {
            return AbstractMapBasedMultimap.this.new NavigableKeySet(this.sortedMap());
        }

        public NavigableSet<K> navigableKeySet() {
            return this.keySet();
        }

        public NavigableSet<K> descendingKeySet() {
            return this.descendingMap().navigableKeySet();
        }

        @Override
        public NavigableMap<K, Collection<V>> subMap(K k0, K k1) {
            return this.subMap(k0, true, k1, false);
        }

        public NavigableMap<K, Collection<V>> subMap(K k0, boolean flag, K k1, boolean flag1) {
            return AbstractMapBasedMultimap.this.new NavigableAsMap(this.sortedMap().subMap(k0, flag, k1, flag1));
        }

        @Override
        public NavigableMap<K, Collection<V>> headMap(K k0) {
            return this.headMap(k0, false);
        }

        public NavigableMap<K, Collection<V>> headMap(K k0, boolean flag) {
            return AbstractMapBasedMultimap.this.new NavigableAsMap(this.sortedMap().headMap(k0, flag));
        }

        @Override
        public NavigableMap<K, Collection<V>> tailMap(K k0) {
            return this.tailMap(k0, true);
        }

        public NavigableMap<K, Collection<V>> tailMap(K k0, boolean flag) {
            return AbstractMapBasedMultimap.this.new NavigableAsMap(this.sortedMap().tailMap(k0, flag));
        }
    }

    private class SortedAsMap extends AbstractMapBasedMultimap<K, V>.AsMap implements SortedMap<K, Collection<V>> {

        SortedSet<K> sortedKeySet;

        SortedAsMap(SortedMap<K, Collection<V>> sortedmap) {
            super(sortedmap);
        }

        SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap) this.submap;
        }

        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }

        public K firstKey() {
            return this.sortedMap().firstKey();
        }

        public K lastKey() {
            return this.sortedMap().lastKey();
        }

        public SortedMap<K, Collection<V>> headMap(K k0) {
            return AbstractMapBasedMultimap.this.new SortedAsMap(this.sortedMap().headMap(k0));
        }

        public SortedMap<K, Collection<V>> subMap(K k0, K k1) {
            return AbstractMapBasedMultimap.this.new SortedAsMap(this.sortedMap().subMap(k0, k1));
        }

        public SortedMap<K, Collection<V>> tailMap(K k0) {
            return AbstractMapBasedMultimap.this.new SortedAsMap(this.sortedMap().tailMap(k0));
        }

        @Override
        public SortedSet<K> keySet() {
            SortedSet sortedset = this.sortedKeySet;

            return sortedset == null ? (this.sortedKeySet = this.createKeySet()) : sortedset;
        }

        @Override
        SortedSet<K> createKeySet() {
            return AbstractMapBasedMultimap.this.new SortedKeySet(this.sortedMap());
        }
    }

    private class AsMap extends Maps.ImprovedAbstractMap<K, Collection<V>> {

        final transient Map<K, Collection<V>> submap;

        AsMap(Map<K, Collection<V>> map) {
            this.submap = map;
        }

        @Override
        protected Set<Entry<K, Collection<V>>> createEntrySet() {
            return new AbstractMapBasedMultimap.AsMap.AsMapEntries();
        }

        public boolean containsKey(Object object) {
            return Maps.safeContainsKey(this.submap, object);
        }

        public Collection<V> get(Object object) {
            Collection collection = (Collection) Maps.safeGet(this.submap, object);

            return collection == null ? null : AbstractMapBasedMultimap.this.wrapCollection(object, collection);
        }

        @Override
        public Set<K> keySet() {
            return AbstractMapBasedMultimap.this.keySet();
        }

        public int size() {
            return this.submap.size();
        }

        public Collection<V> remove(Object object) {
            Collection collection = (Collection) this.submap.remove(object);

            if (collection == null) {
                return null;
            } else {
                Collection collection1 = AbstractMapBasedMultimap.this.createCollection();

                collection1.addAll(collection);
                AbstractMapBasedMultimap.this.totalSize = collection.size();
                collection.clear();
                return collection1;
            }
        }

        public boolean equals(@Nullable Object object) {
            return this == object || this.submap.equals(object);
        }

        public int hashCode() {
            return this.submap.hashCode();
        }

        public String toString() {
            return this.submap.toString();
        }

        public void clear() {
            if (this.submap == AbstractMapBasedMultimap.this.map) {
                AbstractMapBasedMultimap.this.clear();
            } else {
                Iterators.clear(new AbstractMapBasedMultimap.AsMap.AsMapIterator());
            }

        }

        Entry<K, Collection<V>> wrapEntry(Entry<K, Collection<V>> entry) {
            Object object = entry.getKey();

            return Maps.immutableEntry(object, AbstractMapBasedMultimap.this.wrapCollection(object, (Collection) entry.getValue()));
        }

        class AsMapIterator implements Iterator<Entry<K, Collection<V>>> {

            final Iterator<Entry<K, Collection<V>>> delegateIterator;
            Collection<V> collection;

            AsMapIterator() {
                this.delegateIterator = AsMap.this.submap.entrySet().iterator();
            }

            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }

            public Entry<K, Collection<V>> next() {
                Entry entry = (Entry) this.delegateIterator.next();

                this.collection = (Collection) entry.getValue();
                return AsMap.this.wrapEntry(entry);
            }

            public void remove() {
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.this.totalSize = this.collection.size();
                this.collection.clear();
            }
        }

        class AsMapEntries extends Maps.EntrySet<K, Collection<V>> {

            AsMapEntries() {}

            @Override
            Map<K, Collection<V>> map() {
                return AsMap.this;
            }

            public Iterator<Entry<K, Collection<V>>> iterator() {
                return AsMap.this.new AsMapIterator();
            }

            @Override
            public boolean contains(Object object) {
                return Collections2.safeContains(AsMap.this.submap.entrySet(), object);
            }

            @Override
            public boolean remove(Object object) {
                if (!this.contains(object)) {
                    return false;
                } else {
                    Entry entry = (Entry) object;

                    AbstractMapBasedMultimap.this.removeValuesForKey(entry.getKey());
                    return true;
                }
            }
        }
    }

    private abstract class Itr<T> implements Iterator<T> {

        final Iterator<Entry<K, Collection<V>>> keyIterator;
        K key;
        Collection<V> collection;
        Iterator<V> valueIterator;

        Itr() {
            this.keyIterator = AbstractMapBasedMultimap.this.map.entrySet().iterator();
            this.key = null;
            this.collection = null;
            this.valueIterator = Iterators.emptyModifiableIterator();
        }

        abstract T output(K k0, V v0);

        public boolean hasNext() {
            return this.keyIterator.hasNext() || this.valueIterator.hasNext();
        }

        public T next() {
            if (!this.valueIterator.hasNext()) {
                Entry entry = (Entry) this.keyIterator.next();

                this.key = entry.getKey();
                this.collection = (Collection) entry.getValue();
                this.valueIterator = this.collection.iterator();
            }

            return this.output(this.key, this.valueIterator.next());
        }

        public void remove() {
            this.valueIterator.remove();
            if (this.collection.isEmpty()) {
                this.keyIterator.remove();
            }

            AbstractMapBasedMultimap.this.totalSize--;
        }
    }

    @GwtIncompatible("NavigableSet")
    class NavigableKeySet extends AbstractMapBasedMultimap<K, V>.SortedKeySet implements NavigableSet<K> {

        NavigableKeySet(NavigableMap<K, Collection<V>> navigablemap) {
            super((SortedMap) navigablemap);
        }

        @Override
        NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap) super.sortedMap();
        }

        public K lower(K k0) {
            return this.sortedMap().lowerKey(k0);
        }

        public K floor(K k0) {
            return this.sortedMap().floorKey(k0);
        }

        public K ceiling(K k0) {
            return this.sortedMap().ceilingKey(k0);
        }

        public K higher(K k0) {
            return this.sortedMap().higherKey(k0);
        }

        public K pollFirst() {
            return Iterators.pollNext(this.iterator());
        }

        public K pollLast() {
            return Iterators.pollNext(this.descendingIterator());
        }

        public NavigableSet<K> descendingSet() {
            return AbstractMapBasedMultimap.this.new NavigableKeySet(this.sortedMap().descendingMap());
        }

        public Iterator<K> descendingIterator() {
            return this.descendingSet().iterator();
        }

        @Override
        public NavigableSet<K> headSet(K k0) {
            return this.headSet(k0, false);
        }

        public NavigableSet<K> headSet(K k0, boolean flag) {
            return AbstractMapBasedMultimap.this.new NavigableKeySet(this.sortedMap().headMap(k0, flag));
        }

        @Override
        public NavigableSet<K> subSet(K k0, K k1) {
            return this.subSet(k0, true, k1, false);
        }

        public NavigableSet<K> subSet(K k0, boolean flag, K k1, boolean flag1) {
            return AbstractMapBasedMultimap.this.new NavigableKeySet(this.sortedMap().subMap(k0, flag, k1, flag1));
        }

        @Override
        public NavigableSet<K> tailSet(K k0) {
            return this.tailSet(k0, true);
        }

        public NavigableSet<K> tailSet(K k0, boolean flag) {
            return AbstractMapBasedMultimap.this.new NavigableKeySet(this.sortedMap().tailMap(k0, flag));
        }
    }

    private class SortedKeySet extends AbstractMapBasedMultimap<K, V>.KeySet implements SortedSet<K> {

        SortedKeySet(SortedMap<K, Collection<V>> sortedmap) {
            super(sortedmap);
        }

        SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap) super.map();
        }

        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }

        public K first() {
            return this.sortedMap().firstKey();
        }

        public SortedSet<K> headSet(K k0) {
            return AbstractMapBasedMultimap.this.new SortedKeySet(this.sortedMap().headMap(k0));
        }

        public K last() {
            return this.sortedMap().lastKey();
        }

        public SortedSet<K> subSet(K k0, K k1) {
            return AbstractMapBasedMultimap.this.new SortedKeySet(this.sortedMap().subMap(k0, k1));
        }

        public SortedSet<K> tailSet(K k0) {
            return AbstractMapBasedMultimap.this.new SortedKeySet(this.sortedMap().tailMap(k0));
        }
    }

    private class KeySet extends Maps.KeySet<K, Collection<V>> {

        KeySet(Map<K, Collection<V>> map) {
            super(map);
        }

        @Override
        public Iterator<K> iterator() {
            final Iterator iterator = this.map().entrySet().iterator();

            return new Iterator<K>() {
                Entry<K, Collection<V>> entry;

                public boolean hasNext() {
                    return iterator.hasNext();
                }

                public K next() {
                    this.entry = (Entry) iterator.next();
                    return this.entry.getKey();
                }

                public void remove() {
                    CollectPreconditions.checkRemove(this.entry != null);
                    Collection collection = (Collection) this.entry.getValue();

                    iterator.remove();
                    AbstractMapBasedMultimap.this.totalSize = collection.size();
                    collection.clear();
                }
            };
        }

        @Override
        public boolean remove(Object object) {
            int i = 0;
            Collection collection = (Collection) this.map().remove(object);

            if (collection != null) {
                i = collection.size();
                collection.clear();
                AbstractMapBasedMultimap.this.totalSize = i;
            }

            return i > 0;
        }

        @Override
        public void clear() {
            Iterators.clear(this.iterator());
        }

        public boolean containsAll(Collection<?> collection) {
            return this.map().keySet().containsAll(collection);
        }

        public boolean equals(@Nullable Object object) {
            return this == object || this.map().keySet().equals(object);
        }

        public int hashCode() {
            return this.map().keySet().hashCode();
        }
    }

    private class RandomAccessWrappedList extends AbstractMapBasedMultimap<K, V>.WrappedList implements RandomAccess {

        RandomAccessWrappedList(@Nullable K k0, List<V> list, @Nullable AbstractMapBasedMultimap<K, V>.WrappedCollection abstractmapbasedmultimap_wrappedcollection) {
            super(k0, list, abstractmapbasedmultimap_wrappedcollection);
        }
    }

    private class WrappedList extends AbstractMapBasedMultimap<K, V>.WrappedCollection implements List<V> {

        WrappedList(@Nullable K k0, List<V> list, @Nullable AbstractMapBasedMultimap<K, V>.WrappedCollection abstractmapbasedmultimap_wrappedcollection) {
            super(k0, list, abstractmapbasedmultimap_wrappedcollection);
        }

        List<V> getListDelegate() {
            return (List) this.getDelegate();
        }

        public boolean addAll(int i, Collection<? extends V> collection) {
            if (collection.isEmpty()) {
                return false;
            } else {
                int j = this.size();
                boolean flag = this.getListDelegate().addAll(i, collection);

                if (flag) {
                    int k = this.getDelegate().size();

                    AbstractMapBasedMultimap.this.totalSize = k - j;
                    if (j == 0) {
                        this.addToMap();
                    }
                }

                return flag;
            }
        }

        public V get(int i) {
            this.refreshIfEmpty();
            return this.getListDelegate().get(i);
        }

        public V set(int i, V v0) {
            this.refreshIfEmpty();
            return this.getListDelegate().set(i, v0);
        }

        public void add(int i, V v0) {
            this.refreshIfEmpty();
            boolean flag = this.getDelegate().isEmpty();

            this.getListDelegate().add(i, v0);
            AbstractMapBasedMultimap.this.totalSize++;
            if (flag) {
                this.addToMap();
            }

        }

        public V remove(int i) {
            this.refreshIfEmpty();
            Object object = this.getListDelegate().remove(i);

            AbstractMapBasedMultimap.this.totalSize--;
            this.removeIfEmpty();
            return object;
        }

        public int indexOf(Object object) {
            this.refreshIfEmpty();
            return this.getListDelegate().indexOf(object);
        }

        public int lastIndexOf(Object object) {
            this.refreshIfEmpty();
            return this.getListDelegate().lastIndexOf(object);
        }

        public ListIterator<V> listIterator() {
            this.refreshIfEmpty();
            return new AbstractMapBasedMultimap.WrappedList.WrappedListIterator();
        }

        public ListIterator<V> listIterator(int i) {
            this.refreshIfEmpty();
            return new AbstractMapBasedMultimap.WrappedList.WrappedListIterator(i);
        }

        public List<V> subList(int i, int j) {
            this.refreshIfEmpty();
            return AbstractMapBasedMultimap.this.wrapList(this.getKey(), this.getListDelegate().subList(i, j), (AbstractMapBasedMultimap.WrappedCollection) (this.getAncestor() == null ? this : this.getAncestor()));
        }

        private class WrappedListIterator extends AbstractMapBasedMultimap<K, V>.WrappedCollection.WrappedIterator implements ListIterator<V> {

            WrappedListIterator() {
                super();
            }

            public WrappedListIterator(int i) {
                super(WrappedList.this.getListDelegate().listIterator(i));
            }

            private ListIterator<V> getDelegateListIterator() {
                return (ListIterator) this.getDelegateIterator();
            }

            public boolean hasPrevious() {
                return this.getDelegateListIterator().hasPrevious();
            }

            public V previous() {
                return this.getDelegateListIterator().previous();
            }

            public int nextIndex() {
                return this.getDelegateListIterator().nextIndex();
            }

            public int previousIndex() {
                return this.getDelegateListIterator().previousIndex();
            }

            public void set(V v0) {
                this.getDelegateListIterator().set(v0);
            }

            public void add(V v0) {
                boolean flag = WrappedList.this.isEmpty();

                this.getDelegateListIterator().add(v0);
                AbstractMapBasedMultimap.this.totalSize++;
                if (flag) {
                    WrappedList.this.addToMap();
                }

            }
        }
    }

    @GwtIncompatible("NavigableSet")
    class WrappedNavigableSet extends AbstractMapBasedMultimap<K, V>.WrappedSortedSet implements NavigableSet<V> {

        WrappedNavigableSet(@Nullable K k0, NavigableSet<V> navigableset, @Nullable AbstractMapBasedMultimap<K, V>.WrappedCollection abstractmapbasedmultimap_wrappedcollection) {
            super(k0, (SortedSet) navigableset, abstractmapbasedmultimap_wrappedcollection);
        }

        @Override
        NavigableSet<V> getSortedSetDelegate() {
            return (NavigableSet) super.getSortedSetDelegate();
        }

        public V lower(V v0) {
            return this.getSortedSetDelegate().lower(v0);
        }

        public V floor(V v0) {
            return this.getSortedSetDelegate().floor(v0);
        }

        public V ceiling(V v0) {
            return this.getSortedSetDelegate().ceiling(v0);
        }

        public V higher(V v0) {
            return this.getSortedSetDelegate().higher(v0);
        }

        public V pollFirst() {
            return Iterators.pollNext(this.iterator());
        }

        public V pollLast() {
            return Iterators.pollNext(this.descendingIterator());
        }

        private NavigableSet<V> wrap(NavigableSet<V> navigableset) {
            return AbstractMapBasedMultimap.this.new WrappedNavigableSet(this.key, navigableset, (AbstractMapBasedMultimap.WrappedCollection) (this.getAncestor() == null ? this : this.getAncestor()));
        }

        public NavigableSet<V> descendingSet() {
            return this.wrap(this.getSortedSetDelegate().descendingSet());
        }

        public Iterator<V> descendingIterator() {
            return new AbstractMapBasedMultimap.WrappedCollection.WrappedIterator(this.getSortedSetDelegate().descendingIterator());
        }

        public NavigableSet<V> subSet(V v0, boolean flag, V v1, boolean flag1) {
            return this.wrap(this.getSortedSetDelegate().subSet(v0, flag, v1, flag1));
        }

        public NavigableSet<V> headSet(V v0, boolean flag) {
            return this.wrap(this.getSortedSetDelegate().headSet(v0, flag));
        }

        public NavigableSet<V> tailSet(V v0, boolean flag) {
            return this.wrap(this.getSortedSetDelegate().tailSet(v0, flag));
        }
    }

    private class WrappedSortedSet extends AbstractMapBasedMultimap<K, V>.WrappedCollection implements SortedSet<V> {

        WrappedSortedSet(@Nullable K k0, SortedSet<V> sortedset, @Nullable AbstractMapBasedMultimap<K, V>.WrappedCollection abstractmapbasedmultimap_wrappedcollection) {
            super(k0, sortedset, abstractmapbasedmultimap_wrappedcollection);
        }

        SortedSet<V> getSortedSetDelegate() {
            return (SortedSet) this.getDelegate();
        }

        public Comparator<? super V> comparator() {
            return this.getSortedSetDelegate().comparator();
        }

        public V first() {
            this.refreshIfEmpty();
            return this.getSortedSetDelegate().first();
        }

        public V last() {
            this.refreshIfEmpty();
            return this.getSortedSetDelegate().last();
        }

        public SortedSet<V> headSet(V v0) {
            this.refreshIfEmpty();
            return AbstractMapBasedMultimap.this.new WrappedSortedSet(this.getKey(), this.getSortedSetDelegate().headSet(v0), (AbstractMapBasedMultimap.WrappedCollection) (this.getAncestor() == null ? this : this.getAncestor()));
        }

        public SortedSet<V> subSet(V v0, V v1) {
            this.refreshIfEmpty();
            return AbstractMapBasedMultimap.this.new WrappedSortedSet(this.getKey(), this.getSortedSetDelegate().subSet(v0, v1), (AbstractMapBasedMultimap.WrappedCollection) (this.getAncestor() == null ? this : this.getAncestor()));
        }

        public SortedSet<V> tailSet(V v0) {
            this.refreshIfEmpty();
            return AbstractMapBasedMultimap.this.new WrappedSortedSet(this.getKey(), this.getSortedSetDelegate().tailSet(v0), (AbstractMapBasedMultimap.WrappedCollection) (this.getAncestor() == null ? this : this.getAncestor()));
        }
    }

    private class WrappedSet extends AbstractMapBasedMultimap<K, V>.WrappedCollection implements Set<V> {

        WrappedSet(@Nullable K k0, Set<V> set) {
            super(k0, set, (AbstractMapBasedMultimap.WrappedCollection) null);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            if (collection.isEmpty()) {
                return false;
            } else {
                int i = this.size();
                boolean flag = Sets.removeAllImpl((Set) this.delegate, collection);

                if (flag) {
                    int j = this.delegate.size();

                    AbstractMapBasedMultimap.this.totalSize = j - i;
                    this.removeIfEmpty();
                }

                return flag;
            }
        }
    }

    private class WrappedCollection extends AbstractCollection<V> {

        final K key;
        Collection<V> delegate;
        final AbstractMapBasedMultimap<K, V>.WrappedCollection ancestor;
        final Collection<V> ancestorDelegate;

        WrappedCollection(@Nullable K k0, Collection<V> collection, @Nullable AbstractMapBasedMultimap<K, V>.WrappedCollection abstractmapbasedmultimap_wrappedcollection) {
            this.key = k0;
            this.delegate = collection;
            this.ancestor = abstractmapbasedmultimap_wrappedcollection;
            this.ancestorDelegate = abstractmapbasedmultimap_wrappedcollection == null ? null : abstractmapbasedmultimap_wrappedcollection.getDelegate();
        }

        void refreshIfEmpty() {
            if (this.ancestor != null) {
                this.ancestor.refreshIfEmpty();
                if (this.ancestor.getDelegate() != this.ancestorDelegate) {
                    throw new ConcurrentModificationException();
                }
            } else if (this.delegate.isEmpty()) {
                Collection collection = (Collection) AbstractMapBasedMultimap.this.map.get(this.key);

                if (collection != null) {
                    this.delegate = collection;
                }
            }

        }

        void removeIfEmpty() {
            if (this.ancestor != null) {
                this.ancestor.removeIfEmpty();
            } else if (this.delegate.isEmpty()) {
                AbstractMapBasedMultimap.this.map.remove(this.key);
            }

        }

        K getKey() {
            return this.key;
        }

        void addToMap() {
            if (this.ancestor != null) {
                this.ancestor.addToMap();
            } else {
                AbstractMapBasedMultimap.this.map.put(this.key, this.delegate);
            }

        }

        public int size() {
            this.refreshIfEmpty();
            return this.delegate.size();
        }

        public boolean equals(@Nullable Object object) {
            if (object == this) {
                return true;
            } else {
                this.refreshIfEmpty();
                return this.delegate.equals(object);
            }
        }

        public int hashCode() {
            this.refreshIfEmpty();
            return this.delegate.hashCode();
        }

        public String toString() {
            this.refreshIfEmpty();
            return this.delegate.toString();
        }

        Collection<V> getDelegate() {
            return this.delegate;
        }

        public Iterator<V> iterator() {
            this.refreshIfEmpty();
            return new AbstractMapBasedMultimap.WrappedCollection.WrappedIterator();
        }

        public boolean add(V v0) {
            this.refreshIfEmpty();
            boolean flag = this.delegate.isEmpty();
            boolean flag1 = this.delegate.add(v0);

            if (flag1) {
                AbstractMapBasedMultimap.this.totalSize++;
                if (flag) {
                    this.addToMap();
                }
            }

            return flag1;
        }

        AbstractMapBasedMultimap<K, V>.WrappedCollection getAncestor() {
            return this.ancestor;
        }

        public boolean addAll(Collection<? extends V> collection) {
            if (collection.isEmpty()) {
                return false;
            } else {
                int i = this.size();
                boolean flag = this.delegate.addAll(collection);

                if (flag) {
                    int j = this.delegate.size();

                    AbstractMapBasedMultimap.this.totalSize = j - i;
                    if (i == 0) {
                        this.addToMap();
                    }
                }

                return flag;
            }
        }

        public boolean contains(Object object) {
            this.refreshIfEmpty();
            return this.delegate.contains(object);
        }

        public boolean containsAll(Collection<?> collection) {
            this.refreshIfEmpty();
            return this.delegate.containsAll(collection);
        }

        public void clear() {
            int i = this.size();

            if (i != 0) {
                this.delegate.clear();
                AbstractMapBasedMultimap.this.totalSize = i;
                this.removeIfEmpty();
            }
        }

        public boolean remove(Object object) {
            this.refreshIfEmpty();
            boolean flag = this.delegate.remove(object);

            if (flag) {
                AbstractMapBasedMultimap.this.totalSize--;
                this.removeIfEmpty();
            }

            return flag;
        }

        public boolean removeAll(Collection<?> collection) {
            if (collection.isEmpty()) {
                return false;
            } else {
                int i = this.size();
                boolean flag = this.delegate.removeAll(collection);

                if (flag) {
                    int j = this.delegate.size();

                    AbstractMapBasedMultimap.this.totalSize = j - i;
                    this.removeIfEmpty();
                }

                return flag;
            }
        }

        public boolean retainAll(Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            int i = this.size();
            boolean flag = this.delegate.retainAll(collection);

            if (flag) {
                int j = this.delegate.size();

                AbstractMapBasedMultimap.this.totalSize = j - i;
                this.removeIfEmpty();
            }

            return flag;
        }

        class WrappedIterator implements Iterator<V> {

            final Iterator<V> delegateIterator;
            final Collection<V> originalDelegate;

            WrappedIterator() {
                this.originalDelegate = WrappedCollection.this.delegate;
                this.delegateIterator = AbstractMapBasedMultimap.this.iteratorOrListIterator(WrappedCollection.this.delegate);
            }

            WrappedIterator(Iterator<V> iterator) {
                this.originalDelegate = WrappedCollection.this.delegate;
                this.delegateIterator = iterator;
            }

            void validateIterator() {
                WrappedCollection.this.refreshIfEmpty();
                if (WrappedCollection.this.delegate != this.originalDelegate) {
                    throw new ConcurrentModificationException();
                }
            }

            public boolean hasNext() {
                this.validateIterator();
                return this.delegateIterator.hasNext();
            }

            public V next() {
                this.validateIterator();
                return this.delegateIterator.next();
            }

            public void remove() {
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.this.totalSize--;
                WrappedCollection.this.removeIfEmpty();
            }

            Iterator<V> getDelegateIterator() {
                this.validateIterator();
                return this.delegateIterator;
            }
        }
    }
}
