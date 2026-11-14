package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public abstract class ForwardingNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V> {

    protected ForwardingNavigableMap() {}

    @Override
    protected abstract NavigableMap<K, V> delegate();

    public Entry<K, V> lowerEntry(K k0) {
        return this.delegate().lowerEntry(k0);
    }

    protected Entry<K, V> standardLowerEntry(K k0) {
        return this.headMap(k0, false).lastEntry();
    }

    public K lowerKey(K k0) {
        return this.delegate().lowerKey(k0);
    }

    protected K standardLowerKey(K k0) {
        return Maps.keyOrNull(this.lowerEntry(k0));
    }

    public Entry<K, V> floorEntry(K k0) {
        return this.delegate().floorEntry(k0);
    }

    protected Entry<K, V> standardFloorEntry(K k0) {
        return this.headMap(k0, true).lastEntry();
    }

    public K floorKey(K k0) {
        return this.delegate().floorKey(k0);
    }

    protected K standardFloorKey(K k0) {
        return Maps.keyOrNull(this.floorEntry(k0));
    }

    public Entry<K, V> ceilingEntry(K k0) {
        return this.delegate().ceilingEntry(k0);
    }

    protected Entry<K, V> standardCeilingEntry(K k0) {
        return this.tailMap(k0, true).firstEntry();
    }

    public K ceilingKey(K k0) {
        return this.delegate().ceilingKey(k0);
    }

    protected K standardCeilingKey(K k0) {
        return Maps.keyOrNull(this.ceilingEntry(k0));
    }

    public Entry<K, V> higherEntry(K k0) {
        return this.delegate().higherEntry(k0);
    }

    protected Entry<K, V> standardHigherEntry(K k0) {
        return this.tailMap(k0, false).firstEntry();
    }

    public K higherKey(K k0) {
        return this.delegate().higherKey(k0);
    }

    protected K standardHigherKey(K k0) {
        return Maps.keyOrNull(this.higherEntry(k0));
    }

    public Entry<K, V> firstEntry() {
        return this.delegate().firstEntry();
    }

    protected Entry<K, V> standardFirstEntry() {
        return (Entry) Iterables.getFirst(this.entrySet(), (Object) null);
    }

    protected K standardFirstKey() {
        Entry entry = this.firstEntry();

        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    public Entry<K, V> lastEntry() {
        return this.delegate().lastEntry();
    }

    protected Entry<K, V> standardLastEntry() {
        return (Entry) Iterables.getFirst(this.descendingMap().entrySet(), (Object) null);
    }

    protected K standardLastKey() {
        Entry entry = this.lastEntry();

        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    public Entry<K, V> pollFirstEntry() {
        return this.delegate().pollFirstEntry();
    }

    protected Entry<K, V> standardPollFirstEntry() {
        return (Entry) Iterators.pollNext(this.entrySet().iterator());
    }

    public Entry<K, V> pollLastEntry() {
        return this.delegate().pollLastEntry();
    }

    protected Entry<K, V> standardPollLastEntry() {
        return (Entry) Iterators.pollNext(this.descendingMap().entrySet().iterator());
    }

    public NavigableMap<K, V> descendingMap() {
        return this.delegate().descendingMap();
    }

    public NavigableSet<K> navigableKeySet() {
        return this.delegate().navigableKeySet();
    }

    public NavigableSet<K> descendingKeySet() {
        return this.delegate().descendingKeySet();
    }

    @Beta
    protected NavigableSet<K> standardDescendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }

    @Override
    protected SortedMap<K, V> standardSubMap(K k0, K k1) {
        return this.subMap(k0, true, k1, false);
    }

    public NavigableMap<K, V> subMap(K k0, boolean flag, K k1, boolean flag1) {
        return this.delegate().subMap(k0, flag, k1, flag1);
    }

    public NavigableMap<K, V> headMap(K k0, boolean flag) {
        return this.delegate().headMap(k0, flag);
    }

    public NavigableMap<K, V> tailMap(K k0, boolean flag) {
        return this.delegate().tailMap(k0, flag);
    }

    protected SortedMap<K, V> standardHeadMap(K k0) {
        return this.headMap(k0, false);
    }

    protected SortedMap<K, V> standardTailMap(K k0) {
        return this.tailMap(k0, true);
    }

    @Beta
    protected class StandardNavigableKeySet extends Maps.NavigableKeySet<K, V> {

        public StandardNavigableKeySet() {
            super((NavigableMap) ForwardingNavigableMap.this);
        }
    }

    @Beta
    protected class StandardDescendingMap extends Maps.DescendingMap<K, V> {

        public StandardDescendingMap() {}

        @Override
        NavigableMap<K, V> forward() {
            return ForwardingNavigableMap.this;
        }

        @Override
        protected Iterator<Entry<K, V>> entryIterator() {
            return new Iterator<Entry<K, V>>() {
                private Entry<K, V> toRemove = null;
                private Entry<K, V> nextOrNull = StandardDescendingMap.this.forward().lastEntry();

                public boolean hasNext() {
                    return this.nextOrNull != null;
                }

                public Entry<K, V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    } else {
                        Entry entry;

                        try {
                            entry = this.nextOrNull;
                        } finally {
                            this.toRemove = this.nextOrNull;
                            this.nextOrNull = StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
                        }

                        return entry;
                    }
                }

                public void remove() {
                    CollectPreconditions.checkRemove(this.toRemove != null);
                    StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
                    this.toRemove = null;
                }
            };
        }
    }
}
