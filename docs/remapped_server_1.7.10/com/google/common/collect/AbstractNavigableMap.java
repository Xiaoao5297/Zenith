package com.google.common.collect;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import javax.annotation.Nullable;

abstract class AbstractNavigableMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V> {

    AbstractNavigableMap() {}

    @Nullable
    public abstract V get(@Nullable Object object);

    @Nullable
    public Entry<K, V> firstEntry() {
        return (Entry) Iterators.getNext(this.entryIterator(), (Object) null);
    }

    @Nullable
    public Entry<K, V> lastEntry() {
        return (Entry) Iterators.getNext(this.descendingEntryIterator(), (Object) null);
    }

    @Nullable
    public Entry<K, V> pollFirstEntry() {
        return (Entry) Iterators.pollNext(this.entryIterator());
    }

    @Nullable
    public Entry<K, V> pollLastEntry() {
        return (Entry) Iterators.pollNext(this.descendingEntryIterator());
    }

    public K firstKey() {
        Entry entry = this.firstEntry();

        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    public K lastKey() {
        Entry entry = this.lastEntry();

        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    @Nullable
    public Entry<K, V> lowerEntry(K k0) {
        return this.headMap(k0, false).lastEntry();
    }

    @Nullable
    public Entry<K, V> floorEntry(K k0) {
        return this.headMap(k0, true).lastEntry();
    }

    @Nullable
    public Entry<K, V> ceilingEntry(K k0) {
        return this.tailMap(k0, true).firstEntry();
    }

    @Nullable
    public Entry<K, V> higherEntry(K k0) {
        return this.tailMap(k0, false).firstEntry();
    }

    public K lowerKey(K k0) {
        return Maps.keyOrNull(this.lowerEntry(k0));
    }

    public K floorKey(K k0) {
        return Maps.keyOrNull(this.floorEntry(k0));
    }

    public K ceilingKey(K k0) {
        return Maps.keyOrNull(this.ceilingEntry(k0));
    }

    public K higherKey(K k0) {
        return Maps.keyOrNull(this.higherEntry(k0));
    }

    abstract Iterator<Entry<K, V>> entryIterator();

    abstract Iterator<Entry<K, V>> descendingEntryIterator();

    public SortedMap<K, V> subMap(K k0, K k1) {
        return this.subMap(k0, true, k1, false);
    }

    public SortedMap<K, V> headMap(K k0) {
        return this.headMap(k0, false);
    }

    public SortedMap<K, V> tailMap(K k0) {
        return this.tailMap(k0, true);
    }

    public NavigableSet<K> navigableKeySet() {
        return new Maps.NavigableKeySet<>(this);
    }

    public Set<K> keySet() {
        return this.navigableKeySet();
    }

    public abstract int size();

    public Set<Entry<K, V>> entrySet() {
        return new Maps.EntrySet<K, V>() {
            @Override
            Map<K, V> map() {
                return AbstractNavigableMap.this;
            }

            public Iterator<Entry<K, V>> iterator() {
                return AbstractNavigableMap.this.entryIterator();
            }
        };
    }

    public NavigableSet<K> descendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }

    public NavigableMap<K, V> descendingMap() {
        return new AbstractNavigableMap.DescendingMap();
    }

    private final class DescendingMap extends Maps.DescendingMap<K, V> {

        private DescendingMap() {}

        @Override
        NavigableMap<K, V> forward() {
            return AbstractNavigableMap.this;
        }

        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return AbstractNavigableMap.this.descendingEntryIterator();
        }
    }
}
